package com.btgpactual.fondos.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.btgpactual.fondos.dto.CancelacionRequest;
import com.btgpactual.fondos.dto.SuscripcionRequest;
import com.btgpactual.fondos.dto.TransaccionResponse;
import com.btgpactual.fondos.exception.ClienteNoEncontradoException;
import com.btgpactual.fondos.exception.FondoNoEncontradoException;
import com.btgpactual.fondos.exception.SaldoInsuficienteException;
import com.btgpactual.fondos.exception.SuscripcionDuplicadaException;
import com.btgpactual.fondos.exception.CancelacionInvalidaException;
import com.btgpactual.fondos.model.Cliente;
import com.btgpactual.fondos.model.Fondo;
import com.btgpactual.fondos.model.Transaccion;
import com.btgpactual.fondos.repository.ClienteRepository;
import com.btgpactual.fondos.repository.FondoRepository;
import com.btgpactual.fondos.repository.TransaccionRepository;
import com.btgpactual.fondos.service.FondoService;
import com.btgpactual.fondos.utils.GeneradorIdUnico;
import com.btgpactual.fondos.utils.MensajesConstantes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FondoServiceImpl implements FondoService {

	private final ClienteRepository clienteRepository;
	private final FondoRepository fondoRepository;
	private final TransaccionRepository transaccionRepository;
	private final NotificacionServiceImpl notificacionService;

	@Transactional
	public TransaccionResponse suscribirFondo(SuscripcionRequest request) {
		log.info(MensajesConstantes.LOG_INICIANDO_SUSCRIPCION, request.getIdCliente(), request.getIdFondo());

		Fondo fondo = fondoRepository.findById(request.getIdFondo()).orElseThrow(() -> new FondoNoEncontradoException(
				MensajesConstantes.ERROR_FONDO_NO_ENCONTRADO + request.getIdFondo()));

		Cliente cliente = clienteRepository.findById(request.getIdCliente())
				.orElseThrow(() -> new ClienteNoEncontradoException(request.getIdCliente()));

		long aperturasExitosas = transaccionRepository.countByIdClienteAndIdFondoAndTipoAndEstado(
				request.getIdCliente(), request.getIdFondo(), MensajesConstantes.TIPO_APERTURA,
				MensajesConstantes.ESTADO_EXITOSA);

		long cancelacionesExitosas = transaccionRepository.countByIdClienteAndIdFondoAndTipoAndEstado(
				request.getIdCliente(), request.getIdFondo(), MensajesConstantes.TIPO_CANCELACION,
				MensajesConstantes.ESTADO_EXITOSA);

		boolean fondoActivo = aperturasExitosas > cancelacionesExitosas;

		if (fondoActivo) {
			throw new SuscripcionDuplicadaException(MensajesConstantes.ERROR_SUSCRIPCION_DUPLICADA + fondo.getNombre());
		}

		List<Transaccion> cancelacionesRecientes = transaccionRepository
				.findByIdClienteAndIdFondoAndTipoAndEstadoOrderByFechaDesc(request.getIdCliente(), request.getIdFondo(),
						MensajesConstantes.TIPO_CANCELACION, MensajesConstantes.ESTADO_EXITOSA);

		if (!cancelacionesRecientes.isEmpty()) {
			Transaccion ultimaCancelacion = cancelacionesRecientes.get(0);
			if (ultimaCancelacion.getFecha().plusMinutes(1).isAfter(LocalDateTime.now())) {
				throw new SuscripcionDuplicadaException(MensajesConstantes.ERROR_ESPERA_CANCELACION);
			}
		}

		if (!cliente.tieneSaldoSuficiente(fondo.getMontoMinimo())) {
			log.warn(MensajesConstantes.LOG_SALDO_INSUFICIENTE, cliente.getId(), cliente.getSaldo(),
					fondo.getMontoMinimo());
			throw new SaldoInsuficienteException(String.format(MensajesConstantes.ERROR_SALDO_INSUFICIENTE,
					fondo.getNombre(), cliente.getSaldo().toString(), fondo.getMontoMinimo().toString()));
		}

		Transaccion transaccion = Transaccion.builder().id(GeneradorIdUnico.generarId()).idCliente(cliente.getId())
				.idFondo(fondo.getId()).nombreFondo(fondo.getNombre()).tipo(MensajesConstantes.TIPO_APERTURA)
				.monto(fondo.getMontoMinimo()).fecha(LocalDateTime.now()).estado(MensajesConstantes.ESTADO_PENDIENTE)
				.mensaje(MensajesConstantes.MENSAJE_PROCESANDO_SUSCRIPCION).build();

		transaccion = transaccionRepository.save(transaccion);

		try {
			BigDecimal saldoAnterior = cliente.getSaldo();
			cliente.restarSaldo(fondo.getMontoMinimo());

			if (cliente.getFondosSuscritos() == null) {
				cliente.setFondosSuscritos(new java.util.ArrayList<>());
			}
			cliente.getFondosSuscritos().add(fondo.getId());

			clienteRepository.save(cliente);

			transaccion.setEstado(MensajesConstantes.ESTADO_EXITOSA);
			transaccion.setMensaje(MensajesConstantes.MENSAJE_SUSCRIPCION_EXITOSA + fondo.getNombre());
			transaccion = transaccionRepository.save(transaccion);

			log.info(MensajesConstantes.LOG_SUSCRIPCION_EXITOSA, transaccion.getId(), saldoAnterior,
					cliente.getSaldo());

			notificacionService.enviarNotificacion(cliente, fondo, MensajesConstantes.TIPO_APERTURA);

		} catch (Exception e) {
			transaccion.setEstado(MensajesConstantes.ESTADO_FALLIDA);
			transaccion.setMensaje(MensajesConstantes.MENSAJE_ERROR_PREFIX + e.getMessage());
			transaccionRepository.save(transaccion);
			throw e;
		}

		return mapToTransaccionResponse(transaccion);
	}

	@Transactional
	public TransaccionResponse cancelarSuscripcion(CancelacionRequest request) {
		log.info(MensajesConstantes.LOG_INICIANDO_CANCELACION, request.getIdCliente(), request.getIdFondo());

		Fondo fondo = fondoRepository.findById(request.getIdFondo()).orElseThrow(() -> new FondoNoEncontradoException(
				MensajesConstantes.ERROR_FONDO_NO_ENCONTRADO + request.getIdFondo()));

		Cliente cliente = clienteRepository.findById(request.getIdCliente())
				.orElseThrow(() -> new ClienteNoEncontradoException(request.getIdCliente()));

		long aperturasExitosas = transaccionRepository.countByIdClienteAndIdFondoAndTipoAndEstado(
				request.getIdCliente(), request.getIdFondo(), MensajesConstantes.TIPO_APERTURA,
				MensajesConstantes.ESTADO_EXITOSA);

		long cancelacionesExitosas = transaccionRepository.countByIdClienteAndIdFondoAndTipoAndEstado(
				request.getIdCliente(), request.getIdFondo(), MensajesConstantes.TIPO_CANCELACION,
				MensajesConstantes.ESTADO_EXITOSA);

		boolean fondoActivo = aperturasExitosas > cancelacionesExitosas;

		if (!fondoActivo) {
			throw new CancelacionInvalidaException(MensajesConstantes.ERROR_NO_SUSCRIPCION_ACTIVA + fondo.getNombre());
		}

		if (cliente.getFondosSuscritos() == null || !cliente.getFondosSuscritos().contains(fondo.getId())) {
			throw new CancelacionInvalidaException(MensajesConstantes.ERROR_INCONSISTENCIA_LISTA);
		}

		Transaccion transaccion = Transaccion.builder().id(GeneradorIdUnico.generarId()).idCliente(cliente.getId())
				.idFondo(fondo.getId()).nombreFondo(fondo.getNombre()).tipo(MensajesConstantes.TIPO_CANCELACION)
				.monto(fondo.getMontoMinimo()).fecha(LocalDateTime.now()).estado(MensajesConstantes.ESTADO_PENDIENTE)
				.mensaje(MensajesConstantes.MENSAJE_PROCESANDO_CANCELACION).build();

		transaccion = transaccionRepository.save(transaccion);

		try {
			BigDecimal saldoAnterior = cliente.getSaldo();
			cliente.sumarSaldo(fondo.getMontoMinimo());

			cliente.getFondosSuscritos().remove(fondo.getId());

			clienteRepository.save(cliente);

			transaccion.setEstado(MensajesConstantes.ESTADO_EXITOSA);
			transaccion.setMensaje(MensajesConstantes.MENSAJE_CANCELACION_EXITOSA + fondo.getNombre());
			transaccion = transaccionRepository.save(transaccion);

			log.info(MensajesConstantes.LOG_CANCELACION_EXITOSA, transaccion.getId(), saldoAnterior,
					cliente.getSaldo());

			notificacionService.enviarNotificacion(cliente, fondo, MensajesConstantes.TIPO_CANCELACION);

		} catch (Exception e) {
			transaccion.setEstado(MensajesConstantes.ESTADO_FALLIDA);
			transaccion.setMensaje(MensajesConstantes.MENSAJE_ERROR_PREFIX + e.getMessage());
			transaccionRepository.save(transaccion);
			throw e;
		}

		return mapToTransaccionResponse(transaccion);
	}

	public List<TransaccionResponse> obtenerHistorial(String idCliente) {
		log.info(MensajesConstantes.LOG_OBTENIENDO_HISTORIAL, idCliente);

		List<Transaccion> transacciones = transaccionRepository.findByIdClienteOrderByFechaDesc(idCliente);

		log.info(MensajesConstantes.LOG_HISTORIAL_ENCONTRADO, transacciones.size(), idCliente);

		return transacciones.stream().map(this::mapToTransaccionResponse).collect(Collectors.toList());
	}

	private TransaccionResponse mapToTransaccionResponse(Transaccion transaccion) {
		return TransaccionResponse.builder().idTransaccion(transaccion.getId()).tipo(transaccion.getTipo())
				.fondo(transaccion.getNombreFondo()).monto(transaccion.getMonto()).fecha(transaccion.getFecha())
				.estado(transaccion.getEstado()).mensaje(transaccion.getMensaje()).build();
	}
}
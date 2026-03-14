package com.btgpactual.fondos.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.btgpactual.fondos.dto.CancelacionRequest;
import com.btgpactual.fondos.dto.SuscripcionRequest;
import com.btgpactual.fondos.dto.TransaccionResponse;
import com.btgpactual.fondos.exception.FondoNoEncontradoException;
import com.btgpactual.fondos.exception.SaldoInsuficienteException;
import com.btgpactual.fondos.model.Cliente;
import com.btgpactual.fondos.model.Fondo;
import com.btgpactual.fondos.model.Transaccion;
import com.btgpactual.fondos.repository.ClienteRepository;
import com.btgpactual.fondos.repository.FondoRepository;
import com.btgpactual.fondos.repository.TransaccionRepository;
import com.btgpactual.fondos.service.FondoService;
import com.btgpactual.fondos.utils.GeneradorIdUnico;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FondoServiceImpl implements FondoService{
    
    private final ClienteRepository clienteRepository;
    private final FondoRepository fondoRepository;
    private final TransaccionRepository transaccionRepository;
    private final NotificacionServiceImpl notificacionService;
    
    @Transactional
    public TransaccionResponse suscribirFondo(SuscripcionRequest request) {
        log.info("Iniciando suscripción para cliente: {} al fondo: {}", request.getIdCliente(), request.getIdFondo());
        
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            
        Fondo fondo = fondoRepository.findById(request.getIdFondo())
            .orElseThrow(() -> new FondoNoEncontradoException("Fondo no encontrado"));
        
        if (!cliente.tieneSaldoSuficiente(fondo.getMontoMinimo())) {
            throw new SaldoInsuficienteException(fondo.getNombre());
        }
        
        Transaccion transaccion = Transaccion.builder()
            .id(GeneradorIdUnico.generarId())
            .idCliente(cliente.getId())
            .idFondo(fondo.getId())
            .nombreFondo(fondo.getNombre())
            .tipo("APERTURA")
            .monto(fondo.getMontoMinimo())
            .fecha(LocalDateTime.now())
            .estado("EXITOSA")
            .build();
        
        cliente.restarSaldo(fondo.getMontoMinimo());
        
        if (cliente.getFondosSuscritos() == null) {
            cliente.setFondosSuscritos(new java.util.ArrayList<>());
        }
        cliente.getFondosSuscritos().add(fondo.getId());
        
        clienteRepository.save(cliente);
        transaccion = transaccionRepository.save(transaccion);
        
        notificacionService.enviarNotificacion(cliente, fondo, "APERTURA");
        
        log.info("Suscripción exitosa. ID Transacción: {}", transaccion.getId());
        
        return mapToTransaccionResponse(transaccion);
    }
    
    @Transactional
    public TransaccionResponse cancelarSuscripcion(CancelacionRequest request) {
        log.info("Cancelando suscripción para cliente: {} del fondo: {}", request.getIdCliente(), request.getIdFondo());
        
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            
        Fondo fondo = fondoRepository.findById(request.getIdFondo())
            .orElseThrow(() -> new FondoNoEncontradoException("Fondo no encontrado"));
        
        if (cliente.getFondosSuscritos() == null || 
            !cliente.getFondosSuscritos().contains(fondo.getId())) {
            throw new RuntimeException("El cliente no está suscrito a este fondo");
        }
        
        Transaccion transaccion = Transaccion.builder()
            .id(GeneradorIdUnico.generarId())
            .idCliente(cliente.getId())
            .idFondo(fondo.getId())
            .nombreFondo(fondo.getNombre())
            .tipo("CANCELACION")
            .monto(fondo.getMontoMinimo())
            .fecha(LocalDateTime.now())
            .estado("EXITOSA")
            .build();
        
        cliente.sumarSaldo(fondo.getMontoMinimo());
        
        cliente.getFondosSuscritos().remove(fondo.getId());
        
        clienteRepository.save(cliente);
        transaccion = transaccionRepository.save(transaccion);
        

        notificacionService.enviarNotificacion(cliente, fondo, "CANCELACION");
        
        log.info("Cancelación exitosa. ID Transacción: {}", transaccion.getId());
        
        return mapToTransaccionResponse(transaccion);
    }
    
    public List<TransaccionResponse> obtenerHistorial(String idCliente) {
        log.info("Obteniendo historial para cliente: {}", idCliente);
        
        return transaccionRepository.findByIdClienteOrderByFechaDesc(idCliente)
            .stream()
            .map(this::mapToTransaccionResponse)
            .collect(Collectors.toList());
    }
    
    private TransaccionResponse mapToTransaccionResponse(Transaccion transaccion) {
        return TransaccionResponse.builder()
            .idTransaccion(transaccion.getId())
            .tipo(transaccion.getTipo())
            .fondo(transaccion.getNombreFondo())
            .monto(transaccion.getMonto().doubleValue())
            .fecha(transaccion.getFecha())
            .estado(transaccion.getEstado())
            .mensaje(transaccion.getMensaje())
            .build();
    }
}
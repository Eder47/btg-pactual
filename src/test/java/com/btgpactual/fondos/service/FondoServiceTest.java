package com.btgpactual.fondos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.btgpactual.fondos.dto.CancelacionRequest;
import com.btgpactual.fondos.dto.ClienteRequest;
import com.btgpactual.fondos.dto.ClienteResponse;
import com.btgpactual.fondos.dto.SuscripcionRequest;
import com.btgpactual.fondos.dto.TransaccionResponse;
import com.btgpactual.fondos.exception.CancelacionInvalidaException;
import com.btgpactual.fondos.exception.ClienteNoEncontradoException;
import com.btgpactual.fondos.exception.FondoNoEncontradoException;
import com.btgpactual.fondos.exception.SaldoInsuficienteException;
import com.btgpactual.fondos.exception.SuscripcionDuplicadaException;
import com.btgpactual.fondos.model.Cliente;
import com.btgpactual.fondos.model.Fondo;
import com.btgpactual.fondos.model.Transaccion;
import com.btgpactual.fondos.repository.ClienteRepository;
import com.btgpactual.fondos.repository.FondoRepository;
import com.btgpactual.fondos.repository.TransaccionRepository;
import com.btgpactual.fondos.service.impl.FondoServiceImpl;
import com.btgpactual.fondos.service.impl.NotificacionServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para FondoServiceImpl")
class FondoServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private FondoRepository fondoRepository;

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private NotificacionServiceImpl notificacionService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private FondoServiceImpl fondoService;

    private Cliente cliente;
    private Fondo fondo;
    private SuscripcionRequest suscripcionRequest;
    private CancelacionRequest cancelacionRequest;

	@BeforeEach
	void setUp() {
		cliente = Cliente.builder().id("1").nombre("Cliente Test").email("test@test.com").telefono("+573001234567")
				.tipoNotificacion("EMAIL").saldo(new BigDecimal("500000")).fondosSuscritos(new ArrayList<>()).build();

		fondo = Fondo.builder().id("1").nombre("FPV_BTG_PACTUAL_RECAUDADORA").montoMinimo(new BigDecimal("75000"))
				.categoria("FPV").build();

		suscripcionRequest = new SuscripcionRequest();
		suscripcionRequest.setIdCliente("1");
		suscripcionRequest.setIdFondo("1");
		suscripcionRequest.setMonto(new BigDecimal("75000"));

		cancelacionRequest = new CancelacionRequest();
		cancelacionRequest.setIdCliente("1");
		cancelacionRequest.setIdFondo("1");
	}


    @Test
    @DisplayName("Debería suscribir exitosamente a un fondo cuando el cliente tiene saldo suficiente")
    void deberiaSuscribirExitosamente() {

        when(fondoRepository.findById("1")).thenReturn(Optional.of(fondo));
        when(clienteRepository.findById("1")).thenReturn(Optional.of(cliente));
        when(transaccionRepository.countByIdClienteAndIdFondoAndTipoAndEstado(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(0L);
        when(transaccionRepository.findByIdClienteAndIdFondoAndTipoAndEstadoOrderByFechaDesc(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> {
            Transaccion t = invocation.getArgument(0);
            t.setId("TRX-123");
            return t;
        });
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        TransaccionResponse response = fondoService.suscribirFondo(suscripcionRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTipo()).isEqualTo("APERTURA");
        assertThat(response.getEstado()).isEqualTo("EXITOSA");
        assertThat(response.getMonto()).isEqualTo(new BigDecimal("75000"));

        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(transaccionRepository, times(2)).save(any(Transaccion.class));
        verify(notificacionService, times(1)).enviarNotificacion(any(Cliente.class), any(Fondo.class), eq("APERTURA"));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el fondo no existe")
    void deberiaLanzarExcepcionCuandoFondoNoExiste() {

        when(fondoRepository.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fondoService.suscribirFondo(suscripcionRequest))
                .isInstanceOf(FondoNoEncontradoException.class)
                .hasMessageContaining("Fondo no encontrado con ID: 1");
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el cliente no existe")
    void deberiaLanzarExcepcionCuandoClienteNoExiste() {

        when(fondoRepository.findById("1")).thenReturn(Optional.of(fondo));
        when(clienteRepository.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fondoService.suscribirFondo(suscripcionRequest))
                .isInstanceOf(ClienteNoEncontradoException.class)
                .hasMessageContaining("Cliente no encontrado con ID: 1");
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el cliente ya está suscrito al fondo")
    void deberiaLanzarExcepcionCuandoYaSuscrito() {

        when(fondoRepository.findById("1")).thenReturn(Optional.of(fondo));
        when(clienteRepository.findById("1")).thenReturn(Optional.of(cliente));
        when(transaccionRepository.countByIdClienteAndIdFondoAndTipoAndEstado(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(1L); 

        assertThatThrownBy(() -> fondoService.suscribirFondo(suscripcionRequest))
                .isInstanceOf(SuscripcionDuplicadaException.class)
                .hasMessageContaining("El cliente ya está suscrito al fondo");
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el saldo es insuficiente")
    void deberiaLanzarExcepcionCuandoSaldoInsuficiente() {
        // Given
        cliente.setSaldo(new BigDecimal("50000")); 
        when(fondoRepository.findById("1")).thenReturn(Optional.of(fondo));
        when(clienteRepository.findById("1")).thenReturn(Optional.of(cliente));
        when(transaccionRepository.countByIdClienteAndIdFondoAndTipoAndEstado(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(0L);
        when(transaccionRepository.findByIdClienteAndIdFondoAndTipoAndEstadoOrderByFechaDesc(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> fondoService.suscribirFondo(suscripcionRequest))
                .isInstanceOf(SaldoInsuficienteException.class)
                .hasMessageContaining("Saldo insuficiente");
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando hay una cancelación reciente (menos de 1 minuto)")
    void deberiaLanzarExcepcionCuandoCancelacionReciente() {

        Transaccion cancelacionReciente = Transaccion.builder()
                .fecha(LocalDateTime.now().minusSeconds(30)) 
                .build();

        when(fondoRepository.findById("1")).thenReturn(Optional.of(fondo));
        when(clienteRepository.findById("1")).thenReturn(Optional.of(cliente));
        when(transaccionRepository.countByIdClienteAndIdFondoAndTipoAndEstado(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(0L);
        when(transaccionRepository.findByIdClienteAndIdFondoAndTipoAndEstadoOrderByFechaDesc(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(List.of(cancelacionReciente));


        assertThatThrownBy(() -> fondoService.suscribirFondo(suscripcionRequest))
                .isInstanceOf(SuscripcionDuplicadaException.class)
                .hasMessageContaining("Debe esperar 1 minuto después de cancelar");
    }


    @Test
    @DisplayName("Debería cancelar exitosamente una suscripción activa")
    void deberiaCancelarExitosamente() {

        cliente.getFondosSuscritos().add("1");
        
        when(fondoRepository.findById("1")).thenReturn(Optional.of(fondo));
        when(clienteRepository.findById("1")).thenReturn(Optional.of(cliente));
        when(transaccionRepository.countByIdClienteAndIdFondoAndTipoAndEstado(anyString(), anyString(), eq("APERTURA"), anyString()))
                .thenReturn(1L);
        when(transaccionRepository.countByIdClienteAndIdFondoAndTipoAndEstado(anyString(), anyString(), eq("CANCELACION"), anyString()))
                .thenReturn(0L); 
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> {
            Transaccion t = invocation.getArgument(0);
            t.setId("TRX-456");
            return t;
        });
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        TransaccionResponse response = fondoService.cancelarSuscripcion(cancelacionRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTipo()).isEqualTo("CANCELACION");
        assertThat(response.getEstado()).isEqualTo("EXITOSA");

        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(transaccionRepository, times(2)).save(any(Transaccion.class));
        verify(notificacionService, times(1)).enviarNotificacion(any(Cliente.class), any(Fondo.class), eq("CANCELACION"));
    }

    @Test
    @DisplayName("Debería lanzar excepción al cancelar un fondo al que no está suscrito")
    void deberiaLanzarExcepcionAlCancelarFondoNoSuscrito() {

        when(fondoRepository.findById("1")).thenReturn(Optional.of(fondo));
        when(clienteRepository.findById("1")).thenReturn(Optional.of(cliente));
        when(transaccionRepository.countByIdClienteAndIdFondoAndTipoAndEstado(anyString(), anyString(), eq("APERTURA"), anyString()))
                .thenReturn(0L); 
        when(transaccionRepository.countByIdClienteAndIdFondoAndTipoAndEstado(anyString(), anyString(), eq("CANCELACION"), anyString()))
                .thenReturn(0L);

        assertThatThrownBy(() -> fondoService.cancelarSuscripcion(cancelacionRequest))
                .isInstanceOf(CancelacionInvalidaException.class)
                .hasMessageContaining("El cliente no tiene una suscripción activa");
    }

    @Test
    @DisplayName("Debería lanzar excepción al cancelar un fondo ya cancelado previamente")
    void deberiaLanzarExcepcionAlCancelarFondoYaCancelado() {

        when(fondoRepository.findById("1")).thenReturn(Optional.of(fondo));
        when(clienteRepository.findById("1")).thenReturn(Optional.of(cliente));
        when(transaccionRepository.countByIdClienteAndIdFondoAndTipoAndEstado(anyString(), anyString(), eq("APERTURA"), anyString()))
                .thenReturn(1L);
        when(transaccionRepository.countByIdClienteAndIdFondoAndTipoAndEstado(anyString(), anyString(), eq("CANCELACION"), anyString()))
                .thenReturn(1L); 

        assertThatThrownBy(() -> fondoService.cancelarSuscripcion(cancelacionRequest))
                .isInstanceOf(CancelacionInvalidaException.class)
                .hasMessageContaining("Este fondo ya fue cancelado previamente");
    }


    @Test
    @DisplayName("Debería obtener historial de transacciones del cliente")
    void deberiaObtenerHistorial() {

        List<Transaccion> transacciones = List.of(
                Transaccion.builder()
                        .id("TRX-1")
                        .tipo("APERTURA")
                        .nombreFondo("Fondo 1")
                        .monto(new BigDecimal("75000"))
                        .fecha(LocalDateTime.now())
                        .estado("EXITOSA")
                        .mensaje("Suscripción exitosa")
                        .build(),
                Transaccion.builder()
                        .id("TRX-2")
                        .tipo("CANCELACION")
                        .nombreFondo("Fondo 1")
                        .monto(new BigDecimal("75000"))
                        .fecha(LocalDateTime.now())
                        .estado("EXITOSA")
                        .mensaje("Cancelación exitosa")
                        .build()
        );

        when(transaccionRepository.findByIdClienteOrderByFechaDesc("1")).thenReturn(transacciones);


        List<TransaccionResponse> historial = fondoService.obtenerHistorial("1");

        // Then
        assertThat(historial).isNotNull();
        assertThat(historial).hasSize(2);
        assertThat(historial.get(0).getTipo()).isEqualTo("APERTURA");
        assertThat(historial.get(1).getTipo()).isEqualTo("CANCELACION");

        verify(transaccionRepository, times(1)).findByIdClienteOrderByFechaDesc("1");
    }

    @Test
    @DisplayName("Debería retornar lista vacía cuando el cliente no tiene transacciones")
    void deberiaRetornarListaVaciaCuandoNoHayTransacciones() {

        when(transaccionRepository.findByIdClienteOrderByFechaDesc("1")).thenReturn(Collections.emptyList());


        List<TransaccionResponse> historial = fondoService.obtenerHistorial("1");


        assertThat(historial).isNotNull();
        assertThat(historial).isEmpty();
    }



    @Test
    @DisplayName("Debería crear un cliente exitosamente")
    void deberiaCrearClienteExitosamente() {
        // Given
        ClienteRequest request = ClienteRequest.builder()
                .id("2")
                .nombre("Nuevo Cliente")
                .email("nuevo@test.com")
                .telefono("+573001234567")
                .tipoNotificacion("EMAIL")
                .saldo(new BigDecimal("1000000"))
                .build();

        Cliente nuevoCliente = Cliente.builder()
                .id("2")
                .nombre("Nuevo Cliente")
                .email("nuevo@test.com")
                .telefono("+573001234567")
                .tipoNotificacion("EMAIL")
                .saldo(new BigDecimal("1000000"))
                .fondosSuscritos(new ArrayList<>())
                .build();

        ClienteResponse responseEsperado = ClienteResponse.builder()
                .id("2")
                .nombre("Nuevo Cliente")
                .email("nuevo@test.com")
                .telefono("+573001234567")
                .tipoNotificacion("EMAIL")
                .saldo(new BigDecimal("1000000"))
                .fondosSuscritos(new ArrayList<>())
                .build();

        when(clienteRepository.existsById("2")).thenReturn(false);
        when(modelMapper.map(request, Cliente.class)).thenReturn(nuevoCliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(nuevoCliente);
        when(modelMapper.map(nuevoCliente, ClienteResponse.class)).thenReturn(responseEsperado);


        ClienteResponse response = fondoService.crearCliente(request);


        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("2");
        assertThat(response.getNombre()).isEqualTo("Nuevo Cliente");
        assertThat(response.getSaldo()).isEqualTo(new BigDecimal("1000000"));

        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

	@Test
	@DisplayName("Debería lanzar excepción al crear cliente con ID duplicado")
	void deberiaLanzarExcepcionAlCrearClienteDuplicado() {

		ClienteRequest request = ClienteRequest.builder().id("1").nombre("Cliente Duplicado")
				.email("duplicado@test.com").telefono("+573001234567").tipoNotificacion("EMAIL")
				.saldo(new BigDecimal("1000000")).build();

		when(clienteRepository.existsById("1")).thenReturn(true);

		assertThatThrownBy(() -> fondoService.crearCliente(request)).isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Ya existe un cliente con ID: 1");
	}
}
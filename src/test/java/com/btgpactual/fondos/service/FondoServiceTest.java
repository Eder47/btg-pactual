package com.btgpactual.fondos.service;

import com.btgpactual.fondos.dto.SuscripcionRequest;
import com.btgpactual.fondos.dto.TransaccionResponse;
import com.btgpactual.fondos.exception.SaldoInsuficienteException;
import com.btgpactual.fondos.model.Cliente;
import com.btgpactual.fondos.model.Fondo;
import com.btgpactual.fondos.repository.ClienteRepository;
import com.btgpactual.fondos.repository.FondoRepository;
import com.btgpactual.fondos.repository.TransaccionRepository;
import com.btgpactual.fondos.service.impl.FondoServiceImpl;
import com.btgpactual.fondos.service.impl.NotificacionServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FondoServiceTest {
    
    @Mock
    private ClienteRepository clienteRepository;
    
    @Mock
    private FondoRepository fondoRepository;
    
    @Mock
    private TransaccionRepository transaccionRepository;
    
    @Mock
    private NotificacionServiceImpl notificacionService;
    
    @InjectMocks
    private FondoServiceImpl fondoService;
    
    private Cliente cliente;
    private Fondo fondo;
    private SuscripcionRequest request;
    
    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
            .id("1")
            .nombre("Cliente Test")
            .saldo(new BigDecimal("500000"))
            .tipoNotificacion("EMAIL")
            .build();
            
        fondo = Fondo.builder()
            .id("1")
            .nombre("FPV_TEST")
            .montoMinimo(new BigDecimal("75000"))
            .categoria("FPV")
            .build();
            
        request = new SuscripcionRequest();
        request.setIdCliente("1");
        request.setIdFondo("1");
        request.setMonto(75000.0);
    }
    
    @Test
    void testSuscribirFondo_Exitoso() {
        when(clienteRepository.findById("1")).thenReturn(Optional.of(cliente));
        when(fondoRepository.findById("1")).thenReturn(Optional.of(fondo));
        when(transaccionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        
        TransaccionResponse response = fondoService.suscribirFondo(request);
        
        assertNotNull(response);
        assertEquals("APERTURA", response.getTipo());
        assertEquals("FPV_TEST", response.getFondo());
        assertEquals(75000.0, response.getMonto());
        
        verify(clienteRepository).save(any());
        verify(transaccionRepository).save(any());
        verify(notificacionService).enviarNotificacion(any(), any(), any());
    }
    
    @Test
    void testSuscribirFondo_SaldoInsuficiente() {
        cliente.setSaldo(new BigDecimal("50000"));
        
        when(clienteRepository.findById("1")).thenReturn(Optional.of(cliente));
        when(fondoRepository.findById("1")).thenReturn(Optional.of(fondo));
        
        assertThrows(SaldoInsuficienteException.class, () -> {
            fondoService.suscribirFondo(request);
        });
        
        verify(clienteRepository, never()).save(any());
        verify(transaccionRepository, never()).save(any());
    }
}
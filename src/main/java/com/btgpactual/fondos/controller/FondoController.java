package com.btgpactual.fondos.controller;

import com.btgpactual.fondos.dto.SuscripcionRequest;
import com.btgpactual.fondos.dto.CancelacionRequest;
import com.btgpactual.fondos.dto.TransaccionResponse;
import com.btgpactual.fondos.service.FondoService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/fondos")
@RequiredArgsConstructor
public class FondoController {
    
    private final FondoService fondoService;
    
    @PostMapping("/suscripcion")
    public ResponseEntity<TransaccionResponse> suscribir(@Valid @RequestBody SuscripcionRequest request) {
        TransaccionResponse response = fondoService.suscribirFondo(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/cancelacion")
    public ResponseEntity<TransaccionResponse> cancelar(@Valid @RequestBody CancelacionRequest request) {
        TransaccionResponse response = fondoService.cancelarSuscripcion(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/historial/{idCliente}")
    public ResponseEntity<List<TransaccionResponse>> obtenerHistorial(@PathVariable String idCliente) {
        List<TransaccionResponse> historial = fondoService.obtenerHistorial(idCliente);
        return ResponseEntity.ok(historial);
    }
}
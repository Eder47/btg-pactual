package com.btgpactual.fondos.service;

import java.util.List;

import com.btgpactual.fondos.dto.CancelacionRequest;
import com.btgpactual.fondos.dto.ClienteRequest;
import com.btgpactual.fondos.dto.ClienteResponse;
import com.btgpactual.fondos.dto.SuscripcionRequest;
import com.btgpactual.fondos.dto.TransaccionResponse;

public interface FondoService {

    TransaccionResponse suscribirFondo(SuscripcionRequest request);

    TransaccionResponse cancelarSuscripcion(CancelacionRequest request);

    List<TransaccionResponse> obtenerHistorial(String idCliente);
    
    ClienteResponse crearCliente(ClienteRequest request);
}
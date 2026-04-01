package com.btgpactual.fondos.service;

import com.btgpactual.fondos.dto.ClienteRequest;
import com.btgpactual.fondos.dto.ClienteResponse;

public interface ClienteService {
    
    ClienteResponse crearCliente(ClienteRequest request);
    
}
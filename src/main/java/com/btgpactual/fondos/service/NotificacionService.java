package com.btgpactual.fondos.service;

import com.btgpactual.fondos.model.Cliente;
import com.btgpactual.fondos.model.Fondo;

public interface NotificacionService {

    void enviarNotificacion(Cliente cliente, Fondo fondo, String tipo);

}
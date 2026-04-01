package com.btgpactual.fondos.exception;

public class ClienteNoEncontradoException extends RuntimeException {
    
    public ClienteNoEncontradoException(String id) {
        super(String.format("Cliente no encontrado con ID: %s", id));
    }
    
    public ClienteNoEncontradoException(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }
}
package com.btgpactual.fondos.exception;

public class FondoNoEncontradoException extends RuntimeException {
    
    public FondoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    
    public FondoNoEncontradoException(String id, String tipo) {
        super(String.format("No se encontró el fondo con %s: %s", tipo, id));
    }
    
    public FondoNoEncontradoException(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }
}
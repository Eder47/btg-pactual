package com.btgpactual.fondos.utils;

import java.util.UUID;

public class GeneradorIdUnico {
    
    public static String generarId() {
        return UUID.randomUUID().toString();
    }
    
    public static String generarIdTransaccion() {
        return "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
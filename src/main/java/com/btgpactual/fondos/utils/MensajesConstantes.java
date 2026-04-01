package com.btgpactual.fondos.utils;

public final class MensajesConstantes {
    
    private MensajesConstantes() {
    }
    

    public static final String TIPO_APERTURA = "APERTURA";
    public static final String TIPO_CANCELACION = "CANCELACION";
    

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_EXITOSA = "EXITOSA";
    public static final String ESTADO_FALLIDA = "FALLIDA";
    

    public static final String MENSAJE_PROCESANDO_SUSCRIPCION = "Procesando suscripción...";
    public static final String MENSAJE_PROCESANDO_CANCELACION = "Procesando cancelación...";
    

    public static final String MENSAJE_SUSCRIPCION_EXITOSA = "Suscripción exitosa al fondo ";
    public static final String MENSAJE_CANCELACION_EXITOSA = "Cancelación exitosa del fondo ";
    

    public static final String MENSAJE_ERROR_PREFIX = "Error: ";
    public static final String ERROR_FONDO_NO_ENCONTRADO = "Fondo no encontrado con ID: ";
    public static final String ERROR_CLIENTE_NO_ENCONTRADO = "Cliente no encontrado con ID: ";
    public static final String ERROR_SUSCRIPCION_DUPLICADA = "El cliente ya está suscrito al fondo: ";
    public static final String ERROR_ESPERA_CANCELACION = "Debe esperar 1 minuto después de cancelar para volver a suscribirse";
    public static final String ERROR_SALDO_INSUFICIENTE = "Saldo insuficiente para suscribirse al fondo %s. Saldo actual: $%s, Monto mínimo: $%s";
    public static final String ERROR_NO_SUSCRIPCION_ACTIVA = "El cliente no tiene una suscripción activa al fondo: ";
    public static final String ERROR_INCONSISTENCIA_LISTA = "Inconsistencia: El cliente no está suscrito a este fondo en su lista personal";
    

    public static final String LOG_INICIANDO_SUSCRIPCION = "Iniciando suscripción para cliente: {} al fondo: {}";
    public static final String LOG_INICIANDO_CANCELACION = "Cancelando suscripción para cliente: {} del fondo: {}";
    public static final String LOG_SALDO_INSUFICIENTE = "Saldo insuficiente. Cliente: {}, Saldo: {}, Monto requerido: {}";
    public static final String LOG_SUSCRIPCION_EXITOSA = "Suscripción exitosa. ID Transacción: {}, Saldo anterior: {}, Nuevo saldo: {}";
    public static final String LOG_CANCELACION_EXITOSA = "Cancelación exitosa. ID Transacción: {}, Saldo anterior: {}, Nuevo saldo: {}";
    public static final String LOG_OBTENIENDO_HISTORIAL = "Obteniendo historial para cliente: {}";
    public static final String LOG_HISTORIAL_ENCONTRADO = "Se encontraron {} transacciones para el cliente {}";
}
package com.btgpactual.fondos.utils;

public final class MensajesConstantes {

	private MensajesConstantes() {
	}

	// ========== TIPOS DE TRANSACCION ==========
	public static final String TIPO_APERTURA = "APERTURA";
	public static final String TIPO_CANCELACION = "CANCELACION";

	// ========== ESTADOS DE TRANSACCION ==========
	public static final String ESTADO_PENDIENTE = "PENDIENTE";
	public static final String ESTADO_EXITOSA = "EXITOSA";
	public static final String ESTADO_FALLIDA = "FALLIDA";

	// ========== MENSAJES DE PROCESAMIENTO ==========
	public static final String MENSAJE_PROCESANDO_SUSCRIPCION = "Procesando suscripción...";
	public static final String MENSAJE_PROCESANDO_CANCELACION = "Procesando cancelación...";

	// ========== MENSAJES DE ÉXITO ==========
	public static final String MENSAJE_SUSCRIPCION_EXITOSA = "Suscripción exitosa al fondo ";
	public static final String MENSAJE_CANCELACION_EXITOSA = "Cancelación exitosa del fondo ";

	// ========== MENSAJES DE ERROR ==========
	public static final String MENSAJE_ERROR_PREFIX = "Error: ";
	public static final String ERROR_FONDO_NO_ENCONTRADO = "Fondo no encontrado con ID: ";
	public static final String ERROR_CLIENTE_NO_ENCONTRADO = "Cliente no encontrado con ID: ";
	public static final String ERROR_SUSCRIPCION_DUPLICADA = "El cliente ya está suscrito al fondo: ";
	public static final String ERROR_ESPERA_CANCELACION = "Debe esperar 1 minuto después de cancelar para volver a suscribirse";
	public static final String ERROR_SALDO_INSUFICIENTE = "Saldo insuficiente para suscribirse al fondo %s. Saldo actual: $%s, Monto mínimo: $%s";
	public static final String ERROR_NO_SUSCRIPCION_ACTIVA = "El cliente no tiene una suscripción activa al fondo: ";
	public static final String ERROR_INCONSISTENCIA_LISTA = "Inconsistencia: El cliente no está suscrito a este fondo en su lista personal";

	// ========== LOGS ==========
	public static final String LOG_INICIANDO_SUSCRIPCION = "Iniciando suscripción para cliente: {} al fondo: {}";
	public static final String LOG_INICIANDO_CANCELACION = "Cancelando suscripción para cliente: {} del fondo: {}";
	public static final String LOG_SALDO_INSUFICIENTE = "Saldo insuficiente. Cliente: {}, Saldo: {}, Monto requerido: {}";
	public static final String LOG_SUSCRIPCION_EXITOSA = "Suscripción exitosa. ID Transacción: {}, Saldo anterior: {}, Nuevo saldo: {}";
	public static final String LOG_CANCELACION_EXITOSA = "Cancelación exitosa. ID Transacción: {}, Saldo anterior: {}, Nuevo saldo: {}";
	public static final String LOG_OBTENIENDO_HISTORIAL = "Obteniendo historial para cliente: {}";
	public static final String LOG_HISTORIAL_ENCONTRADO = "Se encontraron {} transacciones para el cliente {}";

	// ========== MENSAJES DE NOTIFICACIÓN ==========
	public static final String NOTIFICACION_SUSCRIPCION_EXITOSA = "Hola %s, te has suscrito exitosamente al fondo %s con un monto de $%s";
	public static final String NOTIFICACION_CANCELACION_EXITOSA = "Hola %s, has cancelado tu suscripción al fondo %s. Se ha retornado $%s a tu saldo.";
	public static final String NOTIFICACION_ASUNTO = "Notificación de Fondo";

	// ========== LOGS DE NOTIFICACIÓN ==========
	public static final String LOG_EMAIL_ENVIADO = "Email enviado a: {}";
	public static final String LOG_SMS_ENVIADO = "SMS enviado a: {}";
	public static final String LOG_ERROR_EMAIL = "Error enviando email: {}";
	public static final String LOG_ERROR_SMS = "Error enviando SMS: {}";

	// ========== MENSAJES DE CLIENTES ==========
	public static final String LOG_CREANDO_CLIENTE = "Creando cliente con ID: {}";
	public static final String LOG_CLIENTE_CREADO_EXITOSAMENTE = "Cliente creado exitosamente: {}";
	public static final String LOG_OBTENIENDO_CLIENTE = "Obteniendo cliente con ID: {}";
	public static final String LOG_ACTUALIZANDO_CLIENTE = "Actualizando cliente con ID: {}";
	public static final String LOG_CLIENTE_ACTUALIZADO_EXITOSAMENTE = "Cliente actualizado exitosamente: {}";
	public static final String LOG_ELIMINANDO_CLIENTE = "Eliminando cliente con ID: {}";
	public static final String LOG_CLIENTE_ELIMINADO_EXITOSAMENTE = "Cliente eliminado exitosamente: {}";
	public static final String LOG_OBTENIENDO_TODOS_CLIENTES = "Obteniendo todos los clientes";
	public static final String LOG_CLIENTES_ENCONTRADOS = "Se encontraron {} clientes";

	// ========== ERRORES DE CLIENTES ==========
	public static final String ERROR_CLIENTE_YA_EXISTE = "Ya existe un cliente con ID: {}";
}
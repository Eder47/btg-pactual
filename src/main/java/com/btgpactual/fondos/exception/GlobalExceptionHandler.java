package com.btgpactual.fondos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final String TIMESTAMP = "timestamp";
    private static final String MENSAJE = "mensaje";
    private static final String ERROR = "error";
    private static final String STATUS = "status";
    private static final String PATH = "path";
    
    // ========== MANEJO DE EXCEPCIONES DE NEGOCIO ==========
    
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleSaldoInsuficiente(
            SaldoInsuficienteException ex, WebRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(MENSAJE, ex.getMessage());
        response.put(ERROR, "SALDO_INSUFICIENTE");
        response.put(STATUS, HttpStatus.BAD_REQUEST.value());
        response.put(PATH, getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(FondoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleFondoNoEncontrado(
            FondoNoEncontradoException ex, WebRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(MENSAJE, ex.getMessage());
        response.put(ERROR, "FONDO_NO_ENCONTRADO");
        response.put(STATUS, HttpStatus.NOT_FOUND.value());
        response.put(PATH, getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(SuscripcionDuplicadaException.class)
    public ResponseEntity<Map<String, Object>> handleSuscripcionDuplicada(
            SuscripcionDuplicadaException ex, WebRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(MENSAJE, ex.getMessage());
        response.put(ERROR, "SUSCRIPCION_DUPLICADA");
        response.put(STATUS, HttpStatus.CONFLICT.value());
        response.put(PATH, getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(CancelacionInvalidaException.class)
    public ResponseEntity<Map<String, Object>> handleCancelacionInvalida(
            CancelacionInvalidaException ex, WebRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(MENSAJE, ex.getMessage());
        response.put(ERROR, "CANCELACION_INVALIDA");
        response.put(STATUS, HttpStatus.BAD_REQUEST.value());
        response.put(PATH, getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
	@ExceptionHandler(ClienteNoEncontradoException.class)
	public ResponseEntity<Map<String, Object>> handleClienteNoEncontrado(ClienteNoEncontradoException ex,
			WebRequest request) {

		Map<String, Object> response = new HashMap<>();
		response.put(TIMESTAMP, LocalDateTime.now());
		response.put(MENSAJE, ex.getMessage());
		response.put(ERROR, "CLIENTE_NO_ENCONTRADO");
		response.put(STATUS, HttpStatus.NOT_FOUND.value());
		response.put(PATH, getPath(request));

		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
    
    // ========== MANEJO DE EXCEPCIONES DE VALIDACIÓN ==========
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(ERROR, "VALIDACION_FALLIDA");
        response.put(STATUS, HttpStatus.BAD_REQUEST.value());
        response.put(PATH, getPath(request));
        
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errores.put(error.getField(), error.getDefaultMessage())
        );
        response.put(MENSAJE, errores);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindException(
            BindException ex, WebRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(ERROR, "ERROR_EN_PARAMETROS");
        response.put(STATUS, HttpStatus.BAD_REQUEST.value());
        response.put(PATH, getPath(request));
        
        Map<String, String> errores = new HashMap<>();
        ex.getFieldErrors().forEach(error -> 
            errores.put(error.getField(), error.getDefaultMessage())
        );
        response.put(MENSAJE, errores);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    // ========== MANEJO DE EXCEPCIONES GENÉRICAS ==========
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(MENSAJE, "Ocurrió un error interno en el servidor");
        response.put(ERROR, "ERROR_INTERNO");
        response.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put(PATH, getPath(request));
        
        ex.printStackTrace();
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(MENSAJE, "Error no controlado en el sistema");
        response.put(ERROR, "ERROR_NO_CONTROLADO");
        response.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put(PATH, getPath(request));
        
        ex.printStackTrace();
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    
    // ========== MÉTODO AUXILIAR ==========
    
    private String getPath(WebRequest request) {
        try {
            return request.getDescription(false).replace("uri=", "");
        } catch (Exception e) {
            return "desconocido";
        }
    }
}
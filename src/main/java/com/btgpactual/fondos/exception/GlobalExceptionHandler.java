package com.btgpactual.fondos.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
 
 @ExceptionHandler(SaldoInsuficienteException.class)
 public ResponseEntity<Map<String, Object>> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
     Map<String, Object> response = new HashMap<>();
     response.put("timestamp", LocalDateTime.now());
     response.put("mensaje", ex.getMessage());
     response.put("error", "Saldo Insuficiente");
     return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
 }
 
 @ExceptionHandler(FondoNoEncontradoException.class)
 public ResponseEntity<Map<String, Object>> handleFondoNoEncontrado(FondoNoEncontradoException ex) {
     Map<String, Object> response = new HashMap<>();
     response.put("timestamp", LocalDateTime.now());
     response.put("mensaje", ex.getMessage());
     response.put("error", "Fondo No Encontrado");
     return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
 }
}

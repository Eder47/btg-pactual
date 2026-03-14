package com.btgpactual.fondos.dto;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TransaccionResponse {
 private String idTransaccion;
 private String tipo;
 private String fondo;
 private Double monto;
 private LocalDateTime fecha;
 private String estado;
 private String mensaje;
}

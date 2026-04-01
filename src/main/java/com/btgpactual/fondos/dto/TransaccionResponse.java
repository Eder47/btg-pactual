package com.btgpactual.fondos.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
public class TransaccionResponse {
 private String idTransaccion;
 private String tipo;
 private String fondo;
 private BigDecimal monto;
 
 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
 private LocalDateTime fecha;
 private String estado;
 private String mensaje;
}

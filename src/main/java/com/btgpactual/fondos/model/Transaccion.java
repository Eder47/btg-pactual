package com.btgpactual.fondos.model;


import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transacciones")
public class Transaccion {
 @Id
 private String id;
 private String idCliente;
 private String idFondo;
 private String nombreFondo;
 private String tipo;
 private BigDecimal monto;
 private LocalDateTime fecha;
 private String estado;
 private String mensaje;
}
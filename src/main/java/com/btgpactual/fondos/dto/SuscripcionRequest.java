package com.btgpactual.fondos.dto;
	
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class SuscripcionRequest {
 @NotBlank(message = "El ID del cliente es obligatorio")
 private String idCliente;
 
 @NotBlank(message = "El ID del fondo es obligatorio")
 private String idFondo;
 
 @NotNull(message = "El monto es obligatorio")
 private Double monto;
}
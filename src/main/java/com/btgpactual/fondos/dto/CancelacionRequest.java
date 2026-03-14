package com.btgpactual.fondos.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CancelacionRequest {
 @NotBlank(message = "El ID del cliente es obligatorio")
 private String idCliente;
 
 @NotBlank(message = "El ID del fondo es obligatorio")
 private String idFondo;
}

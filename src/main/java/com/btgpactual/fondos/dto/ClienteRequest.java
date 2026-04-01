package com.btgpactual.fondos.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequest {
    
    @NotBlank(message = "El ID del cliente es obligatorio")
    private String id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "El teléfono debe tener entre 10 y 15 dígitos")
    private String telefono;
    
    @NotBlank(message = "El tipo de notificación es obligatorio")
    @Pattern(regexp = "EMAIL|SMS", message = "El tipo de notificación debe ser EMAIL o SMS")
    private String tipoNotificacion;
    
    @NotNull(message = "El saldo es obligatorio")
    @PositiveOrZero(message = "El saldo debe ser mayor o igual a cero")
    private BigDecimal saldo;
}
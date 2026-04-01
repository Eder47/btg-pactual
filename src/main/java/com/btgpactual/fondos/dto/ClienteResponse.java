package com.btgpactual.fondos.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {
    private String id;
    private String nombre;
    private String email;
    private String telefono;
    private String tipoNotificacion;
    private BigDecimal saldo;
    private List<String> fondosSuscritos;
}
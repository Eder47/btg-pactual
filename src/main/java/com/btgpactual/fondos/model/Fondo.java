package com.btgpactual.fondos.model;


import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fondos")
public class Fondo {
 @Id
 private String id;
 private String nombre;
 private BigDecimal montoMinimo;
 private String categoria;
 
 public static Fondo[] getFondosIniciales() {
     return new Fondo[] {
         Fondo.builder().id("1").nombre("FPV_BTG_PACTUAL_RECAUDADORA")
             .montoMinimo(new BigDecimal("75000")).categoria("FPV").build(),
         Fondo.builder().id("2").nombre("FPV_BTG_PACTUAL_ECOPETROL")
             .montoMinimo(new BigDecimal("125000")).categoria("FPV").build(),
         Fondo.builder().id("3").nombre("DEUDAPRIVADA")
             .montoMinimo(new BigDecimal("50000")).categoria("FIC").build(),
         Fondo.builder().id("4").nombre("FDO-ACCIONES")
             .montoMinimo(new BigDecimal("250000")).categoria("FIC").build(),
         Fondo.builder().id("5").nombre("FPV_BTG_PACTUAL_DINAMICA")
             .montoMinimo(new BigDecimal("100000")).categoria("FPV").build()
     };
 }
}
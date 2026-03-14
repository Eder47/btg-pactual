	
	package com.btgpactual.fondos.model;
	
	import lombok.Data;
	import lombok.Builder;
	import lombok.NoArgsConstructor;
	import lombok.AllArgsConstructor;
	import org.springframework.data.annotation.Id;
	import org.springframework.data.mongodb.core.mapping.Document;
	import java.math.BigDecimal;
	import java.util.List;
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Document(collection = "clientes")
	public class Cliente {
	 @Id
	 private String id;
	 private String nombre;
	 private String email;
	 private String telefono;
	 private String tipoNotificacion;
	 private BigDecimal saldo;
	 private List<String> fondosSuscritos;
	 
	 public void restarSaldo(BigDecimal monto) {
	     this.saldo = this.saldo.subtract(monto);
	 }
	 
	 public void sumarSaldo(BigDecimal monto) {
	     this.saldo = this.saldo.add(monto);
	 }
	 
	 public boolean tieneSaldoSuficiente(BigDecimal montoMinimo) {
	     return this.saldo.compareTo(montoMinimo) >= 0;
	 }
	}

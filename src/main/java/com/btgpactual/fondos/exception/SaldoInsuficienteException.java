package com.btgpactual.fondos.exception;

public class SaldoInsuficienteException extends RuntimeException {
	public SaldoInsuficienteException(String nombreFondo) {
		super(String.format("No tiene saldo disponible para vincularse al fondo %s", nombreFondo));
	}
}

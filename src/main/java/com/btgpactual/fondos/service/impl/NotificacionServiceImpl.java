package com.btgpactual.fondos.service.impl;

import com.btgpactual.fondos.model.Cliente;
import com.btgpactual.fondos.model.Fondo;
import com.btgpactual.fondos.service.NotificacionService;
import com.btgpactual.fondos.utils.MensajesConstantes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

	private final JavaMailSender mailSender;

	@Value("${twilio.account-sid}")
	private String twilioAccountSid;

	@Value("${twilio.auth-token}")
	private String twilioAuthToken;

	@Value("${twilio.phone-number}")
	private String twilioPhoneNumber;

	public void enviarNotificacion(Cliente cliente, Fondo fondo, String tipo) {
		String mensaje = construirMensaje(cliente, fondo, tipo);

		if ("EMAIL".equalsIgnoreCase(cliente.getTipoNotificacion())) {
			enviarEmail(cliente.getEmail(), MensajesConstantes.NOTIFICACION_ASUNTO, mensaje);
		} else if ("SMS".equalsIgnoreCase(cliente.getTipoNotificacion())) {
			enviarSMS(cliente.getTelefono(), mensaje);
		}
	}

	private String construirMensaje(Cliente cliente, Fondo fondo, String tipo) {
		if (MensajesConstantes.TIPO_APERTURA.equals(tipo)) {
			return String.format(MensajesConstantes.NOTIFICACION_SUSCRIPCION_EXITOSA, cliente.getNombre(),
					fondo.getNombre(), fondo.getMontoMinimo().toString());
		} else {
			return String.format(MensajesConstantes.NOTIFICACION_CANCELACION_EXITOSA, cliente.getNombre(),
					fondo.getNombre(), fondo.getMontoMinimo().toString());
		}
	}

	private void enviarEmail(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			mailSender.send(message);
			log.info(MensajesConstantes.LOG_EMAIL_ENVIADO, to);
		} catch (Exception e) {
			log.error(MensajesConstantes.LOG_ERROR_EMAIL, e.getMessage());
		}
	}

	private void enviarSMS(String to, String mensaje) {
		try {
			log.info(MensajesConstantes.LOG_SMS_ENVIADO, to);
		} catch (Exception e) {
			log.error(MensajesConstantes.LOG_ERROR_SMS, e.getMessage());
		}
	}
}
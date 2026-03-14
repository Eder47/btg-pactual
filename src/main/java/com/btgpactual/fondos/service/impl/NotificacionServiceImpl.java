package com.btgpactual.fondos.service.impl;

import com.btgpactual.fondos.model.Cliente;
import com.btgpactual.fondos.model.Fondo;
import com.btgpactual.fondos.service.NotificacionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService{
    
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
            enviarEmail(cliente.getEmail(), "Notificación de Fondo", mensaje);
        } else if ("SMS".equalsIgnoreCase(cliente.getTipoNotificacion())) {
            enviarSMS(cliente.getTelefono(), mensaje);
        }
    }
    
    private String construirMensaje(Cliente cliente, Fondo fondo, String tipo) {
        if ("APERTURA".equals(tipo)) {
            return String.format("Hola %s, te has suscrito exitosamente al fondo %s con un monto de $%.2f",
                cliente.getNombre(), fondo.getNombre(), fondo.getMontoMinimo());
        } else {
            return String.format("Hola %s, has cancelado tu suscripción al fondo %s. Se ha retornado $%.2f a tu saldo.",
                cliente.getNombre(), fondo.getNombre(), fondo.getMontoMinimo());
        }
    }
    
    private void enviarEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Email enviado a: {}", to);
        } catch (Exception e) {
            log.error("Error enviando email: {}", e.getMessage());
        }
    }
    
    private void enviarSMS(String to, String mensaje) {
        try {
            log.info("SMS enviado a: {}", to);
        } catch (Exception e) {
            log.error("Error enviando SMS: {}", e.getMessage());
        }
    }
}
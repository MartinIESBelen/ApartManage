package com.habitalis.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void enviarEmailRecuperacion(String destinatario, String token) {
        Resend resend = new Resend(resendApiKey);

        String enlace = frontendUrl + "/reset-password?token=" + token;

        String cuerpoHtml = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2>Recuperación de contraseña</h2>
                    <p>Hemos recibido una solicitud para restablecer tu contraseña.</p>
                    <p>Haz clic en el siguiente enlace. Expira en <strong>15 minutos</strong>:</p>
                    <a href="%s"
                       style="background-color: #2563eb; color: white; padding: 12px 24px;
                              text-decoration: none; border-radius: 6px; display: inline-block;">
                        Restablecer contraseña
                    </a>
                    <p style="color: #6b7280; margin-top: 24px; font-size: 14px;">
                        Si no solicitaste este cambio, ignora este email.
                    </p>
                </div>
                """.formatted(enlace);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("ApartManage <onboarding@resend.dev>")
                .to(destinatario)
                .subject("Recupera tu contraseña - ApartManage")
                .html(cuerpoHtml)
                .build();

        try {
            resend.emails().send(params);
        } catch (ResendException e) {
            throw new RuntimeException("Error al enviar el email: " + e.getMessage());
        }
    }
}
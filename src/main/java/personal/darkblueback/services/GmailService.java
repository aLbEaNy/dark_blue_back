package personal.darkblueback.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class GmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${storage.media.images-dir}")
    private String imagesDir;

    /**
     * Envía un correo HTML profesional
     *
     * @param to correo del destinatario
     * @param subject asunto del correo
     * @param htmlBody cuerpo en HTML
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // true = multipart
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Remitente con nombre mostrado
            helper.setFrom(fromEmail, "darkBlue");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML

            // Adjuntar el logo desde el sistema de archivos externo
            File logoFile = new File(imagesDir + "\\logoDarkBlueLetras2.png");
            if (logoFile.exists()) {
                FileSystemResource logo = new FileSystemResource(logoFile);
                helper.addInline("logo", logo);
            } else {
                System.err.println("No se encontró el logo en: " + logoFile.getAbsolutePath());
            }

            mailSender.send(message);

        } catch (java.io.UnsupportedEncodingException e) {
            throw new MessagingException("Error al codificar el nombre del remitente", e);
        }
    }

    /**
     * Envía un código de verificación con diseño profesional
     *
     * @param to correo del destinatario
     * @param code código de verificación
     */
    public void sendVerificationCode(String to, String code) throws MessagingException {

        String html = """
                <html>
                                <body style="font-family: Arial, sans-serif; color: #fff; background-color: #0a0f1a; margin: 0; padding: 0;">
                                  <div style="max-width: 600px; margin: auto; padding: 20px; background-color: #0a0f1a; border-radius: 10px;">
                                    <!-- Logo -->
                                    <div style="text-align: center; margin-bottom: 8px;">
                                      <img src='cid:logo' alt='DarkBlue Logo' style="width:220px;"/>
                                    </div>
                                    <!-- Título -->
                                    <h2 style="color:#00bfff; text-align:center; font-size: 28px; margin-bottom: 20px;">Bienvenido a DarkBlue Mar de Acero</h2>
                                    <!-- Mensaje -->
                                    <p style="font-size:16px; color:#ccc;">Hola,</p>
                                    <p style="font-size:16px; color:#ccc;">Tu código de verificación para activar tu cuenta es:</p>
                                    <!-- Código de verificación -->
                                    <div style="text-align:center; margin: 20px 0;">
                                      <span style="font-size:24px; font-weight:bold; background-color:#1e2a48; color:#00bfff; padding:15px 30px; border-radius:8px; display:inline-block;">
                                        %s
                                      </span>
                                    </div>
                                    <hr style="border-color: #333; margin: 30px 0;">
                                    <!-- Pie de página -->
                                    <p style="font-size:12px; color:#777; text-align:center;">Si no has solicitado este código, ignora este mensaje.<br>DarkBlueBack &copy; 2025</p>
                                  </div>
                                </body>
                              </html>
            """.formatted(code);

        sendHtmlEmail(to, "Código de verificación DarkBlue", html);
    }

    /**
     * Envía un correo con la nueva contraseña generada
     *
     * @param to correo del destinatario
     * @param newPassword la contraseña temporal generada
     */
    public void sendResetPassword(String to, String newPassword) throws MessagingException {

        String html = """
            <html>
              <body style="font-family: Arial, sans-serif; color: #fff; background-color: #0a0f1a; margin: 0; padding: 0;">
                <div style="max-width: 600px; margin: auto; padding: 20px; background-color: #0a0f1a; border-radius: 10px;">
                  
                  <!-- Logo -->
                  <div style="text-align: center; margin-bottom: 8px;">
                    <img src='cid:logo' alt='DarkBlue Logo' style="width:220px;"/>
                  </div>
                  
                  <!-- Título -->
                  <h2 style="color:#00bfff; text-align:center; font-size: 26px; margin-bottom: 20px;">
                    Tu contraseña ha sido restablecida
                  </h2>
                  
                  <!-- Mensaje -->
                  <p style="font-size:16px; color:#ccc;">Hola,</p>
                  <p style="font-size:16px; color:#ccc;">
                    Hemos generado una nueva contraseña temporal para tu cuenta DarkBlue. 
                    Te recomendamos cambiarla desde los ajustes de tu perfil después de iniciar sesión.
                  </p>
                  
                  <!-- Contraseña temporal -->
                  <div style="text-align:center; margin: 30px 0;">
                    <span style="font-size:24px; font-weight:bold; background-color:#1e2a48; color:#00bfff; 
                                 padding:15px 30px; border-radius:8px; display:inline-block;">
                        %s
                    </span>
                  </div>
                  
                  <p style="font-size:14px; color:#999;">
                    Si no solicitaste el restablecimiento de la contraseña puedes ponerte en contacto con el administrador del sitio a traves del email registroatv@gmail.com.
                  </p>
                  
                  <hr style="border-color:#333; margin:30px 0;">
                  
                  <!-- Pie -->
                  <p style="font-size:12px; color:#777; text-align:center;">
                    DarkBlueBack © 2025 - Todos los derechos reservados
                  </p>
                </div>
              </body>
            </html>
            """.formatted(newPassword);

        sendHtmlEmail(to, "Nueva contraseña - DarkBlue", html);
    }

}

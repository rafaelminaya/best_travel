package com.debuggeando_ideas.best_travel.infraestructure.helpers;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class EmailHelper {
    // IOnyectamos mi objeto dependencia para enviar email
    private final JavaMailSender mailSender;

    // método para enviar el email hacia el destinatario
    public void sendMail(String to, String name, String product) {
        // Usaremos la clase "SimpleMailMessage" si queremos enviar texto plano
        // Pero usaremos "MimeMessage" para enviar un "html" como email.
        MimeMessage message = this.mailSender.createMimeMessage();
        String htmlContent = this.readHtmlTemplate(name, product);

        try {
            // Indicamos el remitente
            message.setFrom(new InternetAddress("rafael.dm.20@gmail.com"));
            // Indicamos el asunto del mensaje
            message.setSubject("Best travel - ".concat(product).concat(" successful"));
            // Indicamos el destinatario y las posibles copias (CC y BCC)
            message.setRecipients(MimeMessage.RecipientType.TO, to);
            // Indicamos el cuerpo del mensaje y especificamos el formato de este.
            message.setContent(htmlContent, MediaType.TEXT_HTML_VALUE);
            // envimos el mensaej
            mailSender.send(message);

        } catch (MessagingException ex) {
            log.error("Error to send email", ex);
        }
    }

    // Leemos el archivo "resources/email/email_tempate.html" como si fuese un "String"
    // los 2 argumentos reemplazarán estos valores en el html.
    // Recibirá de argumentos el "name" y el "product", como el método "sendMail()" ya que se hará el reemplazo en esas variables
    private String readHtmlTemplate(String name, String product) {
        // lines representa las líneas del html
        try (var lines = Files.lines(TEMPLATE_PATH)) {
            // convertimos a un "String" toda el contenido del archivo "email_template.html"
            // joining() : Concatena línea por línea convirtiéndolo en tipo "String"
            var html = lines.collect(Collectors.joining());
            // reemplazamos los parámetros por los valores "{name}" y "{product}" establecidos en el archivo template del email
            return html.replace("{name}", name).replace("{product}", product);

        } catch (IOException ex) {
            log.error("Cant red html template", ex);
            throw new RuntimeException();
        }
    }

    // Ruta relativa hacia el archivo "template del email" a enviar
    private final Path TEMPLATE_PATH = Paths.get("src/main/resources/email/email_template.html");

}

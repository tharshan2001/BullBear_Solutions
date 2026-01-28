package bullbear.app.utils;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class EmailSender {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @PostConstruct
    public void verifySMTP() {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            System.out.println("SMTP server is ready to send emails");
        } catch (Exception e) {
            System.err.println("SMTP configuration error: " + e.getMessage());
        }
    }

    @Async
    public void sendEmail(String to, String subject, String html, List<String> attachments) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(env.getProperty("spring.mail.username"));
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        if (attachments != null) {
            for (String path : attachments) {
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    FileSystemResource resource = new FileSystemResource(file);
                    helper.addAttachment(file.getName(), resource);
                } else {
                    System.err.println("Attachment not found: " + path);
                }
            }
        }

        mailSender.send(message);
        System.out.println("Email sent to " + to);
    }
}

package africa.semicolon.goodreads.events;

import africa.semicolon.goodreads.dto.request.VerificationMessageRequest;
import africa.semicolon.goodreads.services.email.EmailService;
import africa.semicolon.goodreads.dto.response.MailResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

@Component//("mailgun_sender")
@Slf4j
@AllArgsConstructor
public class SendMessageEventListener {

        @Qualifier("mailgun_sender")
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final Environment environment;

    @EventListener
    public void handleSendMessageEvent(SendMessageEvent event) throws UnirestException, ExecutionException, InterruptedException {
        VerificationMessageRequest messageRequest = (VerificationMessageRequest) event.getSource();

        String verificationLink = messageRequest.getDomainUrl() + "api/v1/auth/verify/" + messageRequest.getVerificationToken();

        log.info("Message request --> {}", messageRequest);

        Context context = new Context();
        context.setVariable("user_name", messageRequest.getUsersFullName().toUpperCase());
        context.setVariable("verification_token", verificationLink);
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            log.info("Message Event -> {}", event.getSource());
            messageRequest.setBody(templateEngine.process("registration_verification_mail.html", context));
            MailResponse mailResponse = emailService.sendHtmlMail(messageRequest).get();
            log.info("Mail Response --> {}", mailResponse);

        } else {
            messageRequest.setBody(verificationLink);
            MailResponse mailResponse = emailService.sendSimpleMail((VerificationMessageRequest) event.getSource()).get();
            log.info("Mail Response --> {}", mailResponse);

        }
    }

//    VerificationMessageRequest messageRequest = (VerificationMessageRequest) event.getSource();
//    Context context = new Context();
//        context.setVariable("user_name", messageRequest.getUsersFullName().toUpperCase());
//        context.setVariable("verification_token", "https://www.google.com");
////        messageRequest.setBody("hello there");
//        messageRequest.setBody(templateEngine.process("registration_verification_mail.html", context));
//    MailResponse mailResponse = emailService.sendSimpleMail((MessageRequest) event.getSource()).get();
}

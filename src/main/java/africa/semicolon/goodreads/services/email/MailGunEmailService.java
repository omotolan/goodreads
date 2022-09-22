package africa.semicolon.goodreads.services.email;

import africa.semicolon.goodreads.dto.request.VerificationMessageRequest;
import africa.semicolon.goodreads.dto.response.MailResponse;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.concurrent.CompletableFuture;

@Service("mailgun_sender")
@Slf4j
@RequiredArgsConstructor
public class MailGunEmailService implements EmailService {
    private final String DOMAIN = System.getenv("DOMAIN");

    private final String PRIVATE_KEY = System.getenv("PRIVATE_KEY");
    @NonNull
    private final TemplateEngine templateEngine;


    @Override
    public CompletableFuture<MailResponse> sendSimpleMail(VerificationMessageRequest messageRequest) throws UnirestException {
        log.info("DOMAIN -> {}", DOMAIN);
        log.info("API KEY -> {}", PRIVATE_KEY);
        HttpResponse<String> request = Unirest.post("https://api.mailgun.net/v3/" + DOMAIN + "/messages")
                .basicAuth("api", PRIVATE_KEY)
                .queryString("from", messageRequest.getSender())
                .queryString("to", messageRequest.getReceiver())
                .queryString("subject", messageRequest.getSubject())
                .queryString("text", messageRequest.getBody())
                .asString();
        MailResponse mailResponse = request.getStatus() == 200 ? new MailResponse(true) : new MailResponse(false);
        return CompletableFuture.completedFuture(mailResponse);


    }

    @Override
    public CompletableFuture<MailResponse> sendHtmlMail(VerificationMessageRequest messageRequest) throws UnirestException {
        log.info("DOMAIN -> {}", DOMAIN);
        log.info("API KEY -> {}", PRIVATE_KEY);
        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + DOMAIN + "/messages")
                .basicAuth("api", PRIVATE_KEY)
                .queryString("from", messageRequest.getSender())
                .queryString("to", messageRequest.getReceiver())
                .queryString("subject", messageRequest.getSubject())
                .queryString("html", messageRequest.getBody())
                .asJson();
        MailResponse mailResponse = request.getStatus() == 200 ? new MailResponse(true) : new MailResponse(false);
        return CompletableFuture.completedFuture(mailResponse);
    }
//@Override
//public CompletableFuture<MailResponse> sendHtmlMail(VerificationMessageRequest messageRequest) throws UnirestException {
//    log.info("DOMAIN -> {}", DOMAIN);
//    log.info("API KEY -> {}", PRIVATE_KEY);
//    Context context = new Context();
//    context.setVariable("user_name", messageRequest.getUsersFullName());
//    messageRequest.setBody(templateEngine.process("registration_verification_mail.html",context));
//
//    HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + DOMAIN + "/messages")
//            .basicAuth("api", PRIVATE_KEY)
//            .queryString("from", messageRequest.getSender())
//            .queryString("to", messageRequest.getReceiver())
//            .queryString("subject", messageRequest.getSubject())
//            .queryString("html", messageRequest.getBody())
//            .asJson();
//    MailResponse mailResponse = request.getStatus() == 200 ? new MailResponse(true) : new MailResponse(false);
//    return CompletableFuture.completedFuture(mailResponse);
//}
}

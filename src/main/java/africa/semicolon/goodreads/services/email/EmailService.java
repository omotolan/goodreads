package africa.semicolon.goodreads.services.email;

import africa.semicolon.goodreads.dto.request.VerificationMessageRequest;
import africa.semicolon.goodreads.dto.response.MailResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<MailResponse> sendSimpleMail(VerificationMessageRequest messageRequest) throws UnirestException, UnirestException;
    CompletableFuture<MailResponse> sendHtmlMail(VerificationMessageRequest messageRequest) throws UnirestException;
}

package africa.semicolon.goodreads.services;

import africa.semicolon.goodreads.dto.VerificationMessageRequest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<MailResponse> sendSimpleMail(VerificationMessageRequest messageRequest) throws UnirestException, UnirestException;
    CompletableFuture<MailResponse> sendHtmlMail(VerificationMessageRequest messageRequest) throws UnirestException;
}

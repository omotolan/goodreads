package africa.semicolon.goodreads.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class VerificationMessageRequest {
    @Email
    private String sender;
    @Email
    private String receiver;
    private String body;
    private String subject;
    private String usersFullName;
    private String verificationToken;
    private String domainUrl;
}

package africa.semicolon.goodreads.services;

import lombok.*;

import javax.validation.constraints.Email;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
public class MessageRequest {

        @Email
        private String sender;
        @Email
        private String receiver;
        private String body;
        private String subject;
        private String usersFullName;

}

package africa.semicolon.goodreads.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AccountCreationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}

package africa.semicolon.goodreads.util;

import africa.semicolon.goodreads.data.models.User;
import africa.semicolon.goodreads.data.repository.UserRepository;
import africa.semicolon.goodreads.dto.AccountCreationRequest;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountValidation {

    public static void validate(AccountCreationRequest accountCreationRequest, UserRepository userRepository) throws GoodReadsException {
        User user = userRepository.findUserByEmail(accountCreationRequest.getEmail()).orElse(null);
        if (user != null) {
            throw new GoodReadsException("user email already exists", 400);
        }
    }
}

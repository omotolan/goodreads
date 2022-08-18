package africa.semicolon.goodreads.security;

import africa.semicolon.goodreads.dto.AccountCreationRequest;
import africa.semicolon.goodreads.dto.UserDto;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.concurrent.ExecutionException;

public interface UserAuthService {
    UserDto createUserAccount(String host, AccountCreationRequest accountCreationRequest) throws GoodReadsException, UnirestException, ExecutionException, InterruptedException;
    void verifyUser(String token) throws GoodReadsException;

}

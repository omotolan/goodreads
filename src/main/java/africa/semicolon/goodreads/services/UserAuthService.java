package africa.semicolon.goodreads.services;

import africa.semicolon.goodreads.data.models.User;
import africa.semicolon.goodreads.dto.request.AccountCreationRequest;
import africa.semicolon.goodreads.dto.UserDto;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import africa.semicolon.goodreads.exceptions.UserAlreadyExist;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.concurrent.ExecutionException;

public interface UserAuthService {
    UserDto createUserAccount(String host, AccountCreationRequest accountCreationRequest) throws GoodReadsException, UnirestException, ExecutionException, InterruptedException;
    void verifyUser(String token) throws GoodReadsException;
    User findUserByEmail(String email) throws UserAlreadyExist;


}

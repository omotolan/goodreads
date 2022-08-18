package africa.semicolon.goodreads.services;

import africa.semicolon.goodreads.dto.AccountCreationRequest;
import africa.semicolon.goodreads.dto.UpdateRequest;
import africa.semicolon.goodreads.dto.UserDto;
import africa.semicolon.goodreads.exceptions.GoodReadsException;

import java.util.List;

public interface UserService {
    UserDto createUserAccount(AccountCreationRequest accountCreationRequest) throws GoodReadsException;

    UserDto findUserById(String userId) throws GoodReadsException;

    List<UserDto> findAll();

    UserDto updateUserProfile(String id, UpdateRequest updateRequest) throws GoodReadsException;


}

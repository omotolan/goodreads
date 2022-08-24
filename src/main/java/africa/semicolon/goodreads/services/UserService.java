package africa.semicolon.goodreads.services;

import africa.semicolon.goodreads.dto.request.UpdateRequest;
import africa.semicolon.goodreads.dto.UserDto;
import africa.semicolon.goodreads.exceptions.GoodReadsException;

import java.util.List;

public interface UserService {

    UserDto findUserById(String userId) throws GoodReadsException;

    List<UserDto> findAll();

    UserDto updateUserProfile(String id, UpdateRequest updateRequest) throws GoodReadsException;


}

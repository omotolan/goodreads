package africa.semicolon.goodreads.services;

import africa.semicolon.goodreads.data.models.User;
import africa.semicolon.goodreads.data.repository.UserRepository;
import africa.semicolon.goodreads.dto.request.AccountCreationRequest;
import africa.semicolon.goodreads.dto.request.UpdateRequest;
import africa.semicolon.goodreads.dto.UserDto;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper mapper) {
        this.userRepository = userRepository;
        this.modelMapper = mapper;

    }



    @Override
    public UserDto  findUserById(String userId) throws GoodReadsException {
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(
                () -> new GoodReadsException(String.format("User with id %s not found", userId), 404)
        );
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class)).toList();
    }

    @Override
    public UserDto updateUserProfile(String id, UpdateRequest updateRequest) throws GoodReadsException {
        User user = userRepository.findById(Long.valueOf(id)).orElseThrow(
                () -> new GoodReadsException("user id not found", 404)
        );
        User userToSave = modelMapper.map(updateRequest,User.class);
        userToSave.setId(user.getId());
        userToSave.setDateJoined(user.getDateJoined());
        userRepository.save(userToSave);
        return modelMapper.map(userToSave, UserDto.class);
    }

    private static void validate(AccountCreationRequest accountCreationRequest, UserRepository userRepository) throws GoodReadsException {

        User user = userRepository.findUserByEmailIgnoreCase(accountCreationRequest.getEmail());
        if (user != null){
            throw new GoodReadsException("user email already exists", 400);
        }
    }
}


package africa.semicolon.goodreads.services;

import africa.semicolon.goodreads.data.models.User;
import africa.semicolon.goodreads.data.repository.UserRepository;
import africa.semicolon.goodreads.dto.AccountCreationRequest;
import africa.semicolon.goodreads.dto.UpdateRequest;
import africa.semicolon.goodreads.dto.UserDto;
import africa.semicolon.goodreads.events.SendMessageEvent;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import africa.semicolon.goodreads.util.AccountValidation;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

//@Service
//@AllArgsConstructor
//public class UserServiceImpl implements UserService {
//    private final UserRepository userRepository;
//    private final ModelMapper modelMapper;
//    private ApplicationEventPublisher applicationEventPublisher;
//
//    private final EmailService emailService;
//
////    public UserServiceImpl(UserRepository userRepository) {
////        this.userRepository = userRepository;
////        modelMapper = new ModelMapper();
////    }
//
//    @Override
//    public UserDto createUserAccount(AccountCreationRequest accountCreationRequest) throws GoodReadsException {
//        validate(accountCreationRequest);
//        User user = User.builder()
//                .firstName(accountCreationRequest.getFirstName())
//                .lastName(accountCreationRequest.getLastName())
//                .email(accountCreationRequest.getEmail())
//                .password(accountCreationRequest.getPassword())
//                .dateJoined(LocalDate.now())
//                .build();
//        MessageRequest message = MessageRequest.builder()
//                .subject("VERIFY EMAIL")
//                .sender("akinsolatolani@yahoo.com")
//                .receiver(user.getEmail())
//                .usersFullName(String.format("%s %s", user.getFirstName(), user.getLastName()))
//                .build();
//        SendMessageEvent event = new SendMessageEvent(message);
//        applicationEventPublisher.publishEvent(event);
//        User savedUser = userRepository.save(user);
//        return modelMapper.map(savedUser, UserDto.class);
////        validate(accountCreationRequest);
////        User user = User.builder()
////                .firstName(accountCreationRequest.getFirstName())
////                .lastName(accountCreationRequest.getLastName())
////                .email(accountCreationRequest.getEmail())
////                .password(accountCreationRequest.getPassword())
////                .build();
////        MessageRequest message = MessageRequest.builder()
////                .subject("VERIFY EMAIL")
////                .sender("akinsolatolani@yahoo.com")
////                .receiver(user.getEmail())
////                .usersFullName(String.format("%s %s", user.getFirstName(), user.getLastName()))
////                .build();
////        SendMessageEvent event = new SendMessageEvent(message);
////        applicationEventPublisher.publishEvent(event);
////        User savedUser = userRepository.save(user);
////        return modelMapper.map(savedUser, UserDto.class);
//    }
//
//    @Override
//    public UserDto findUserById(String userId) throws GoodReadsException {
//        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(
//                () -> new GoodReadsException(String.format("User with id %s not found", userId), 404)
//        );
//        return modelMapper.map(user, UserDto.class);
//    }
//
//    @Override
//    public List<UserDto> findAll() {
//        return userRepository.findAll().stream()
//                .map(user -> modelMapper.map(user, UserDto.class)).toList();
//    }
//
//    @Override
//    public UserDto updateUserProfile(String id, UpdateRequest updateRequest) throws GoodReadsException {
//        User user = userRepository.findById(Long.valueOf(id)).orElseThrow(
//                () -> new GoodReadsException("user id not found", 404)
//        );
//        User userToSave = modelMapper.map(updateRequest, User.class);
//        userToSave.setId(user.getId());
//        userRepository.save(userToSave);
//        return modelMapper.map(userToSave, UserDto.class);
//    }
//    private void validate(AccountCreationRequest accountCreationRequest) throws GoodReadsException {
//        User user = userRepository.findUserByEmail(accountCreationRequest.getEmail()).orElse(null);
//        if (user != null) {
//            throw new GoodReadsException("user email already exists", 400);
//        }
//    }
//}

@Service
@Slf4j

public class UserServiceImpl implements UserService{
    private UserRepository userRepository;

    private ApplicationEventPublisher applicationEventPublisher;
    private ModelMapper modelMapper;
    private EmailService emailService;

    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper mapper,
                           EmailService emailService,
                           ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.modelMapper = mapper;
        this.emailService = emailService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public UserDto createUserAccount(AccountCreationRequest accountCreationRequest) throws GoodReadsException {
        validate(accountCreationRequest, userRepository);
        User user = User.builder()
                .firstName(accountCreationRequest.getFirstName())
                .lastName(accountCreationRequest.getLastName())
                .email(accountCreationRequest.getEmail())
                .password(accountCreationRequest.getPassword())
                .dateJoined(LocalDate.now())
                .build();
        MessageRequest message = MessageRequest.builder()
                .subject("VERIFY EMAIL")
                .sender("ehizman.tutoredafrica@gmail.com")
                .receiver(accountCreationRequest.getEmail())
                .usersFullName(String.format("%s %s", user.getFirstName(), user.getLastName()))
                .build();
        log.info(user.getEmail());
        SendMessageEvent event = new SendMessageEvent(message);
        applicationEventPublisher.publishEvent(event);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
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

        User user = userRepository.findUserByEmail(accountCreationRequest.getEmail()).orElse(null);
        if (user != null){
            throw new GoodReadsException("user email already exists", 400);
        }
    }
}


package africa.semicolon.goodreads.services;

import africa.semicolon.goodreads.data.models.User;
import africa.semicolon.goodreads.data.repository.UserRepository;
import africa.semicolon.goodreads.dto.request.AccountCreationRequest;
import africa.semicolon.goodreads.dto.UserDto;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import africa.semicolon.goodreads.services.email.EmailService;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceAuthImplTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
@Autowired
    private UserAuthService userService;

//    @BeforeEach
//    void setUp() {
//        userService = new UserServiceImpl(userRepository, mapper, applicationEventPublisher);
//    }

    @Test
    void testThatUserCanCreateAccount() throws GoodReadsException, UnirestException, ExecutionException, InterruptedException {
        HttpServletRequest request = null;
        AccountCreationRequest accountCreationRequest =
                new AccountCreationRequest("Firstname", "Lastname", "testemail@gmail.com","password" );
        UserDto userDto = userService.createUserAccount(String.valueOf(request), accountCreationRequest);

        Optional<User> optionalUser = userRepository.findById(userDto.getId());
        assertThat(optionalUser.isPresent()).isEqualTo(true);
        assertThat(optionalUser.get().getFirstName()).isEqualTo("Firstname");
        assertThat(optionalUser.get().getLastName()).isEqualTo("Lastname");
        assertThat(optionalUser.get().getEmail()).isEqualTo("testemail@gmail.com");
        assertThat(optionalUser.get().getPassword()).isEqualTo("password");
    }

    @Test
    void testThatUserEmailIsUnique() throws GoodReadsException, UnirestException, ExecutionException, InterruptedException {
        HttpServletRequest request = null;

        AccountCreationRequest firstAccountCreationRequest =
                new AccountCreationRequest("Firstname", "Lastname", "testemail@gmail.com","password" );
        UserDto userDto = userService.createUserAccount(String.valueOf(request),firstAccountCreationRequest);

        AccountCreationRequest secondAccountCreationRequest =
                new AccountCreationRequest("Amaka", "Chopper", "testemail@gmail.com","password1234" );
        assertThrows(GoodReadsException.class, ()-> userService.createUserAccount(String.valueOf(request),secondAccountCreationRequest));
    }

}
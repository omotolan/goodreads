package africa.semicolon.goodreads.mocktest;

import africa.semicolon.goodreads.data.models.User;
import africa.semicolon.goodreads.data.repository.UserRepository;
import africa.semicolon.goodreads.dto.AccountCreationRequest;
import africa.semicolon.goodreads.dto.UserDto;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import africa.semicolon.goodreads.services.UserService;
import africa.semicolon.goodreads.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTest {
    @Mock
    private UserRepository userRepository;
    private UserService userService;

//    @Captor
//    private ArgumentCaptor<User> userArgumentCaptor;

//    @BeforeEach
//    void setUp() {
//        userService = new UserServiceImpl(userRepository);
//    }

    @Test
    void userCanCreateAccountTest() throws GoodReadsException {
        AccountCreationRequest accountCreationRequest =
                new AccountCreationRequest("Ernest", "Ehigiator", "ernest@example.com", "password");

        User userToReturn = User.builder()
                .firstName(accountCreationRequest.getFirstName())
                .lastName(accountCreationRequest.getLastName())
                .email(accountCreationRequest.getEmail())
                .password(accountCreationRequest.getPassword())
                .build();

        when(userRepository.findUserByEmail("ernest@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(userToReturn);
        UserDto userDto = userService.createUserAccount(accountCreationRequest);

        // to verify that void methods are called
//        verify(userRepository, times(1)).findUserByEmail("ernest@example.com");
//        verify(userRepository, times(1)).save(userArgumentCaptor.capture());

        // for argument captor
//        User capturedUser = userArgumentCaptor.getValue();
//        assertThat(capturedUser.getId()).isEqualTo(1L);


        // assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getFirstName()).isEqualTo("Ernest");
        assertThat(userDto.getLastName()).isEqualTo("Ehigiator");
        assertThat(userDto.getEmail()).isEqualTo("ernest@example.com");

    }

    @Test
    void testThatUserEmailIsUnique() throws GoodReadsException {
        AccountCreationRequest firstAccountCreationRequest =
                new AccountCreationRequest("Firstname", "Lastname", "testemail@gmail.com", "password");
        UserDto userDto = userService.createUserAccount(firstAccountCreationRequest);

        AccountCreationRequest secondAccountCreationRequest =
                new AccountCreationRequest("Amaka", "Chopper", "testemail@gmail.com", "password1234");
        assertThrows(GoodReadsException.class, () -> userService.createUserAccount(secondAccountCreationRequest));
    }

}

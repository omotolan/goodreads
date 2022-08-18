package africa.semicolon.goodreads.security;

import africa.semicolon.goodreads.data.models.User;
import africa.semicolon.goodreads.data.models.UserRole;
import africa.semicolon.goodreads.data.repository.UserRepository;
import africa.semicolon.goodreads.dto.AccountCreationRequest;
import africa.semicolon.goodreads.dto.UserDto;
import africa.semicolon.goodreads.dto.VerificationMessageRequest;
import africa.semicolon.goodreads.events.SendMessageEvent;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import africa.semicolon.goodreads.exceptions.UserAlreadyExist;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserAuthServiceImpl implements UserAuthService, UserDetailsService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ModelMapper modelMapper;
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public UserDto createUserAccount(String host, AccountCreationRequest accountCreationRequest) throws GoodReadsException {
        validateEmail(accountCreationRequest.getEmail());
        User user = modelMapper.map(accountCreationRequest, User.class);
        user.setPassword(bCryptPasswordEncoder.encode(accountCreationRequest.getPassword()));
        user.setDateJoined(LocalDate.now());
        User savedUser = userRepository.save(user);

        VerificationMessageRequest message = VerificationMessageRequest.builder()
                .subject("VERIFY EMAIL")
                .sender("ehizman.tutoredafrica@gmail.com")
                .receiver(savedUser.getEmail())
                .domainUrl(host)
                .verificationToken("sd")
                .usersFullName(String.format("%s %s", savedUser.getFirstName(), savedUser.getLastName()))
                .build();
        SendMessageEvent event = new SendMessageEvent(message);
        applicationEventPublisher.publishEvent(event);
        return modelMapper.map(savedUser, UserDto.class);
    }

    private void validateEmail(String email) throws UserAlreadyExist {
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isPresent()) {
            throw new UserAlreadyExist("Email already exist", 404);
        }

    }

    @Override
    public void verifyUser(String token) throws GoodReadsException {

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email).orElse(null);
        if (user != null){
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getAuthorities(user.getUserRoles()));
        }

        return null;
    }
    private Collection<? extends GrantedAuthority> getAuthorities(Set<UserRole> roles) {
        return roles.stream().map(
                role -> new SimpleGrantedAuthority(role.getRole().name())
        ).collect(Collectors.toSet());
    }
}

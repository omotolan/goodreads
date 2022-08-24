package africa.semicolon.goodreads.services;

import africa.semicolon.goodreads.data.models.User;
import africa.semicolon.goodreads.data.models.UserRole;
import africa.semicolon.goodreads.data.repository.UserRepository;
import africa.semicolon.goodreads.dto.request.AccountCreationRequest;
import africa.semicolon.goodreads.dto.UserDto;
import africa.semicolon.goodreads.dto.request.VerificationMessageRequest;
import africa.semicolon.goodreads.events.SendMessageEvent;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import africa.semicolon.goodreads.exceptions.UserAlreadyExist;
import africa.semicolon.goodreads.security.jwt.TokenProvider;
import io.jsonwebtoken.Claims;
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
import java.util.Date;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserAuthServiceImpl implements UserAuthService, UserDetailsService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ModelMapper modelMapper;
    private ApplicationEventPublisher applicationEventPublisher;
    private TokenProvider tokenProvider;

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
        User user = userRepository.findUserByEmailIgnoreCase(email);
        if (user != null) {
            throw new UserAlreadyExist("Email already exist", 404);
        }

    }

    @Override
    public void verifyUser(String token) throws GoodReadsException {
        Claims claims = tokenProvider.getAllClaimsFromJWTToken(token);
        Function<Claims, String> getSubjectFromClaim = Claims::getSubject;  // Claims-> Claims
        Function<Claims, Date> getExpirationDateFromClaim = Claims::getExpiration;
        Function<Claims, Date> getIssuedAtDateFromClaim = Claims::getIssuedAt;

        String userId = getSubjectFromClaim.apply(claims);
        if (userId == null) {
            throw new GoodReadsException("User id not present in verification token", 404);
        }
        Date expiryDate = getExpirationDateFromClaim.apply(claims);
        if (expiryDate == null) {
            throw new GoodReadsException("Expiry Date not present in verification token", 404);
        }
        Date issuedAtDate = getIssuedAtDateFromClaim.apply(claims);

        if (issuedAtDate == null) {
            throw new GoodReadsException("Issued At date not present in verification token", 404);
        }

        if (expiryDate.compareTo(issuedAtDate) > 14.4) {
            throw new GoodReadsException("Verification Token has already expired", 404);
        }

        User user = findUserById(userId);
        if (user == null) {
            throw new GoodReadsException("User id does not exist", 404);
        }
        user.setIsVerified(true);
        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) throws UserAlreadyExist {
        User user = userRepository.findUserByEmailIgnoreCase(email);
        if (user == null){
            throw new UserAlreadyExist("user email does not exist", 400);
        }
        return user;
    }

    private User findUserById(String id) {
        return userRepository.findById(Long.valueOf(id)).orElse(null);

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmailIgnoreCase(email);
        if (user != null) {
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

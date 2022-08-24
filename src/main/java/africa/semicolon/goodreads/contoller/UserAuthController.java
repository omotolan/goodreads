package africa.semicolon.goodreads.contoller;

import africa.semicolon.goodreads.data.models.User;
import africa.semicolon.goodreads.dto.request.AccountCreationRequest;
import africa.semicolon.goodreads.dto.response.ApiResponse;
import africa.semicolon.goodreads.dto.request.LoginRequest;
import africa.semicolon.goodreads.dto.UserDto;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import africa.semicolon.goodreads.exceptions.UserAlreadyExist;
import africa.semicolon.goodreads.security.jwt.TokenProvider;
import africa.semicolon.goodreads.services.UserAuthService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final AuthenticationManager authenticationManager;

    private final TokenProvider tokenProvider;

    public UserAuthController(UserAuthService userAuthService, AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.userAuthService = userAuthService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(HttpServletRequest request, @RequestBody @Valid @NotNull AccountCreationRequest accountCreationRequest) throws UnirestException, GoodReadsException, ExecutionException, InterruptedException {
        String host = request.getRequestURL().toString();
        int index = host.indexOf("/", host.indexOf("/", host.indexOf("/")) + 2);
        host = host.substring(0, index + 1);
        log.info("Host --> {}", host);
        UserDto userDto = userAuthService.createUserAccount(host, accountCreationRequest);
        ApiResponse apiResponse = ApiResponse.builder()
                .status("success")
                .message("user created successfully")
                .data(userDto)
                .build();
        log.info("Returning response");
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping("/verify/{token}")
    public ModelAndView verify(@PathVariable String token) throws GoodReadsException {
        userAuthService.verifyUser(token);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("verification_success");
        return modelAndView;

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws UserAlreadyExist {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = tokenProvider.generateJWTToken(authentication);
        User user = userAuthService.findUserByEmail(loginRequest.getEmail());
        return new ResponseEntity<>(new AuthToken(token, user.getId()), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}

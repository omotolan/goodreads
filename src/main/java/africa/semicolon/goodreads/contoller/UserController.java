package africa.semicolon.goodreads.contoller;

import africa.semicolon.goodreads.dto.AccountCreationRequest;
import africa.semicolon.goodreads.dto.ApiResponse;
import africa.semicolon.goodreads.dto.UpdateRequest;
import africa.semicolon.goodreads.dto.UserDto;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import africa.semicolon.goodreads.services.UserService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/reg")
    public ResponseEntity<?> createUser(@RequestBody @Valid @NotNull AccountCreationRequest accountCreationRequest) throws GoodReadsException {
        try {
            log.info("Account Creation Request ==> {}",accountCreationRequest);
            UserDto userDto = userService.createUserAccount(accountCreationRequest);
            ApiResponse apiResponse = ApiResponse.builder()
                    .status("success")
                    .message("user created successfully")
                    .data(userDto)
                    .build();
            log.info("Returning response");
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        }
        catch (GoodReadsException e) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .status("fail")
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(e.getStatusCode()));
        }
//        catch (UnirestException | ExecutionException | InterruptedException e){
//            ApiResponse apiResponse = ApiResponse.builder()
//                    .status("fail")
//                    .message(e.getMessage())
//                    .build();
//            return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(400));
//        }
    }

//    @PostMapping("/")
//    public ResponseEntity<?> createUser(@RequestBody @Valid @NotNull AccountCreationRequest accountCreationRequest) {
//        try {
//            log.info("Account Creation Request ==> {}", accountCreationRequest);
//            UserDto userDto = userService.createUserAccount(accountCreationRequest);
//            ApiResponse apiResponse = ApiResponse.builder()
//                    .status("success")
//                    .message("user created successfully")
//                    .data(userDto)
//                    .build();
//            log.info("Returning response");
//            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
//        } catch (GoodReadsException e) {
//            ApiResponse apiResponse = ApiResponse.builder()
//                    .status("fail")
//                    .message(e.getMessage())
//                    .build();
//            return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(e.getStatusCode()));
//        }
//    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") @NotNull @NotBlank String userId) {
        try {
            if (("null").equals(userId) || ("").equals(userId.trim())) {
                throw new GoodReadsException("String id cannot be null", 400);
            }
            UserDto userDto = userService.findUserById(userId);
            ApiResponse apiResponse = ApiResponse.builder()
                    .status("success")
                    .message("user found")
                    .data(userDto)
                    .result(1)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (GoodReadsException e) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .status("fail")
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllUsers() {
        List<UserDto> users = userService.findAll();
        ApiResponse apiResponse = ApiResponse.builder()
                .status("success")
                .message(users.size() != 0 ? "users found" : "no user exists in database")
                .data(users)
                .result(users.size())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PatchMapping("/")
    public ResponseEntity<?> updateUserProfile(@Valid @NotBlank @NotNull @RequestParam String id,
                                               @RequestBody @NotNull UpdateRequest updateRequest) {

        try {
            UserDto userDto = userService.updateUserProfile(id, updateRequest);
            ApiResponse apiResponse = ApiResponse.builder()
                    .status("success")
                    .message("user found")
                    .data(userDto)
                    .result(1)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (GoodReadsException e) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .status("fail")
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(e.getStatusCode()));
        }
    }
}

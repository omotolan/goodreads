package africa.semicolon.goodreads.contoller;

import africa.semicolon.goodreads.dto.*;
import africa.semicolon.goodreads.dto.request.UpdateRequest;
import africa.semicolon.goodreads.dto.response.ApiResponse;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import africa.semicolon.goodreads.services.BookService;
import africa.semicolon.goodreads.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final BookService bookService;

//    public UserController(UserService userService) {
//        this.userService = userService;
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

    @GetMapping(value = "/", produces = {"application/hal+json"})
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<UserDto> users = userService.findAll();
        for(final UserDto user : users){
            Long userId = user.getId();
            Link selfLink = linkTo(UserController.class).slash(userId).withSelfRel();
            user.add(selfLink);

            List<BookDto> booksUploadedByUser = bookService.getAllBooksForUser(user.getEmail());

            if (booksUploadedByUser.size() > 0) {
                Link booksLink = linkTo(methodOn(UserController.class).getAllBooksForUser(user.getEmail())).withRel("books uploaded");
                user.add(booksLink);
            }

        }
        Link link = linkTo(UserController.class).withSelfRel();
        CollectionModel<UserDto> result = CollectionModel.of(users, link);
        ApiResponse apiResponse = ApiResponse.builder()
                .status("success")
                .message(users.size() != 0 ? "users found" : "no user exists in database")
                .data(result)
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

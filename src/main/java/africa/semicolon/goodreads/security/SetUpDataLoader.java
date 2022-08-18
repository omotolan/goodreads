package africa.semicolon.goodreads.security;

import africa.semicolon.goodreads.data.models.Role;
import africa.semicolon.goodreads.data.models.User;
import africa.semicolon.goodreads.data.models.UserRole;
import africa.semicolon.goodreads.data.repository.UserRepository;
import africa.semicolon.goodreads.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;

@Component
@AllArgsConstructor
@Slf4j
public class SetUpDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String password = System.getenv("ADMIN_PASSWORD");
        HashSet<UserRole> userRole = new HashSet<>();
        userRole.add(new UserRole(Role.ADMIN));
        if (userRepository.findUserByEmail("admin@yahoo.com").isEmpty()) {
            User user = User.builder()
                    .firstName("admin")
                    .lastName("user")
                    .email("admin@yahoo.com")
                    .accountStatus(AccountStatus.PRO)
                    .userRoles(userRole)
                    .dateJoined(LocalDate.now())
                    .password(bCryptPasswordEncoder.encode(password))
                    .isVerified(Boolean.TRUE)
                    .build();
            userRepository.save(user);
        }
    }
}

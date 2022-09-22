package africa.semicolon.goodreads.security;

import africa.semicolon.goodreads.data.enums.Role;
import africa.semicolon.goodreads.data.models.User;
import africa.semicolon.goodreads.data.models.UserRole;
import africa.semicolon.goodreads.data.repository.UserRepository;
import africa.semicolon.goodreads.data.enums.AccountStatus;
import africa.semicolon.goodreads.services.RoleService;
import africa.semicolon.goodreads.services.RoleServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class SetUpDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleService roleService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String password = System.getenv("ADMIN_PASSWORD");

        if (roleService.getRoles() <= 0) {
            roleService.createRole(Role.ADMIN);
            System.out.println("role created here");
        }
        Set<UserRole> roles = new HashSet<>();
        roles.add(roleService.getRoleById(1L));
        if (userRepository.findUserByEmail("admin@yahoo.com").isEmpty()) {
            User user = User.builder()
                    .firstName("admin")
                    .lastName("user")
                    .email("admin@yahoo.com")
                    .accountStatus(AccountStatus.PRO)
                    .userRoles(roles)
                    .dateJoined(LocalDate.now())
                    .password(bCryptPasswordEncoder.encode(password))
                    .isVerified(Boolean.TRUE)
                    .build();
            userRepository.save(user);
        }
    }
}

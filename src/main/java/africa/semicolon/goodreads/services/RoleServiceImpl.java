package africa.semicolon.goodreads.services;

import africa.semicolon.goodreads.data.enums.Role;
import africa.semicolon.goodreads.data.models.UserRole;
import africa.semicolon.goodreads.data.repository.UserRoleRepository;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final UserRoleRepository roleRepository;

    @Override
    public String createRole(Role role) {
        UserRole userRole = new UserRole();
        userRole.setRole(role);
        roleRepository.save(userRole);
        return "Role created";
    }

    @Override
    public UserRole getRoleById(Long id) {
        Optional<UserRole> userRole = roleRepository.findById(id);

        return userRole.get();
    }

    @Override
    public int getRoles() {
        return roleRepository.findAll().size();
    }

    @Override
    public List<UserRole> getAllRole() {
        return roleRepository.findAll();
    }
}

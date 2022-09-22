package africa.semicolon.goodreads.services;

import africa.semicolon.goodreads.data.enums.Role;
import africa.semicolon.goodreads.data.models.UserRole;

import java.util.List;

public interface RoleService {
    String createRole(Role role);

    UserRole getRoleById(Long id);

    int getRoles();

    List<UserRole> getAllRole();
}

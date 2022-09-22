package africa.semicolon.goodreads.data.repository;

import africa.semicolon.goodreads.data.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}

package africa.semicolon.goodreads.data.repository;

import africa.semicolon.goodreads.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByEmailIgnoreCase(String email);
   Optional< User> findUserByEmail(String email);
}

package africa.semicolon.goodreads.data.models;

import africa.semicolon.goodreads.data.enums.Role;
import lombok.*;

import javax.persistence.*;


@NoArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@Entity
public class UserRole {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NonNull
    @Enumerated(EnumType.STRING)
    private Role role;

}

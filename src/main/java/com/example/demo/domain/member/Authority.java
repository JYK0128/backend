package com.example.demo.domain.member;

import com.example.demo.event.member.AuthorityEventHandler;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuthorityEventHandler.class)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"target", "permission"}))
@EqualsAndHashCode(exclude = {"roles"})
public class Authority implements GrantedAuthority {
    public enum PERMISSION_TYPE {READ, WRITE};

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id Long id;
    private @Column String target;
    private @Column @Enumerated(EnumType.STRING) PERMISSION_TYPE permission;

    @Singular
    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles;

    @Override
    public String getAuthority() {
        return target.toUpperCase() + ":" + permission;
    }
}

package com.example.demo.domain.member;

import com.example.demo.event.member.RoleEventHandler;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(RoleEventHandler.class)
@EqualsAndHashCode(exclude = {"members", "authorities"})
public class Role implements GrantedAuthority {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id Long id;
    private @Column(unique = true) String name;

    @Singular
    @OneToMany(mappedBy = "role")
    private @Column Set<Member> members;

    @Singular
    @ManyToMany
    private @Column Set<Authority> authorities;

    @Override
    public String getAuthority() {
        return "ROLE_" + name.toUpperCase();
    }
}
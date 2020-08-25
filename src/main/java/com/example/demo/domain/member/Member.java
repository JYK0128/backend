package com.example.demo.domain.member;

import com.example.demo.domain.board.Post;
import com.example.demo.domain.board.Message;
import com.example.demo.event.member.MemberEventHandler;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(MemberEventHandler.class)
@EqualsAndHashCode(exclude = {"role", "articles", "messages"})
public class Member implements UserDetails {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id Long id;
    private @Column(unique = true) @NonNull String username;
    private @Column @NonNull String password;
    private @Column LocalDate expiredDate;
    private @Column LocalDate lockedDate;
    private @Column LocalDate credentialsDate;
    private @Column boolean isUnable;

    @ManyToOne
    private Role role;
    @OneToMany(mappedBy = "member")
    private Set<Post> posts;
    @OneToMany(mappedBy = "member")
    private Set<Message> messages;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return role.getAuthorities(); }
    @Override
    public boolean isAccountNonExpired() { return (expiredDate == null) ? true : expiredDate.isBefore(LocalDate.now()); }
    @Override
    public boolean isAccountNonLocked() { return (lockedDate == null) ? true : lockedDate.isBefore(LocalDate.now()); }
    @Override
    public boolean isCredentialsNonExpired() { return (credentialsDate == null) ? true : credentialsDate.isBefore(LocalDate.now()); }
    @Override
    public boolean isEnabled() { return !isUnable; }
}

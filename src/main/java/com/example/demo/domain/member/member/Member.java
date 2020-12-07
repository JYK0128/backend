package com.example.demo.domain.member.member;

import com.example.demo.config.security.OAuthServerProvider;
import com.example.demo.domain.board.message.Message;
import com.example.demo.domain.board.post.Post;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@EntityListeners({AuditingEntityListener.class, MemberEventHandler.class})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "provider"}),
        @UniqueConstraint(columnNames = {"nickname"}),
})
public class Member extends MemberUserDetails {
    @Id @OrderBy
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Email
    private String email;
    @Enumerated(EnumType.STRING)
    private OAuthServerProvider provider;
    @Builder.Default
    @Pattern(regexp = "^[\\w가-힣0-9]{2,20}$")
    private String nickname = UUID.randomUUID().toString();

    @Builder.Default
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Message> messages = new ArrayList<>();
    @Builder.Default
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Post> posts = new ArrayList<>();

    @Override
    @Transient
    public String getPassword() {
        return String.format("{\"provider\" : %s, \"email\" : %s}", provider, email);
    }

    @Override
    @Transient
    public String getUsername() {
        return nickname;
    }
}
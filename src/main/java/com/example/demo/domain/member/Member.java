package com.example.demo.domain.member;

import com.example.demo.config.security.OAuthServerProvider;
import com.example.demo.domain.board.Message;
import com.example.demo.domain.board.Post;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
        }
)
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    private String email;
    @Enumerated(EnumType.STRING)
    private OAuthServerProvider provider;

    @Builder.Default
    @OneToMany(mappedBy = "writer")
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Message> messages = new ArrayList<>();
    @Builder.Default
    @OneToMany(mappedBy = "writer")
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Post> posts = new ArrayList<>();
}
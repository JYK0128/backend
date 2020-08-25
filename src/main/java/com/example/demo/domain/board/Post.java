package com.example.demo.domain.board;

import com.example.demo.domain.member.Member;
import com.example.demo.event.board.ArticleEventHandler;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(ArticleEventHandler.class)
@EqualsAndHashCode(exclude = {"member", "uploads", "messages"})
public class Post {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id Long id;
    private @Column String tag;
    private @Column String title;
    private @Column String content;

    @ManyToOne
    private Member member;
    @OneToMany(mappedBy = "post")
    private Set<Upload> uploads;
    @OneToMany(mappedBy = "post")
    private Set<Message> messages;
}
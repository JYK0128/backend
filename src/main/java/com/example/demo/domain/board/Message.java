package com.example.demo.domain.board;

import com.example.demo.domain.member.Member;
import com.example.demo.event.board.CommentEventHandler;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(CommentEventHandler.class)
@EqualsAndHashCode(exclude = {"post", "article"})
public class Message {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id Long id;
    private @Column String comment;

    @ManyToOne
    private Member member;
    @ManyToOne
    private Post post;
}

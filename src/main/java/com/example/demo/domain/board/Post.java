package com.example.demo.domain.board;

import com.example.demo.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tag;
    private String title;
    private String content;
    private LocalDateTime date;
    private Long view;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member writer;
    @Builder.Default
    @OneToMany(mappedBy = "post")
    private List<Message> messages = new ArrayList<>();
    @Builder.Default
    @OneToMany(mappedBy = "post")
    private List<Upload> uploads = new ArrayList<>();
}

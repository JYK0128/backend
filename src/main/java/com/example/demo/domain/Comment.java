package com.example.demo.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(exclude = {"member", "article"})
public class Comment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id Long id;
    private @Column String comment;

    @ManyToOne
    private Member member;
    @ManyToOne
    private Article article;
}

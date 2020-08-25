package com.example.demo.domain.board;

import com.example.demo.event.board.FileEventHandler;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(FileEventHandler.class)
@EqualsAndHashCode(exclude = {"post"})
public class Upload {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id Long id;
    private @Column String name;

    @ManyToOne
    private Post post;
}
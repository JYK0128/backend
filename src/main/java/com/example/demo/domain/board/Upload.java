package com.example.demo.domain.board;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "uuid")
        }
)
public class Upload {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Pattern(regexp = "^[ㄱ-ㅎ,ㅏ-ㅣ,가-힣,\\w,\\s-]+\\.[A-Za-z]{1,}$")
    private String oriName;
    @Pattern(regexp = "^[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}$")
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Post post;
}

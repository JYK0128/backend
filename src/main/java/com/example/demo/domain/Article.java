package com.example.demo.domain;

import com.example.demo.event.AuthorityEventHandler;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(exclude = {"member", "uploadFiles"})
public class Article {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id Long id;
    private @Column String tag;
    private @Column String title;
    private @Column String content;

    @ManyToOne
    private Member member;
    @OneToMany(mappedBy = "article")
    private Set<UploadFile> uploadFiles;
}
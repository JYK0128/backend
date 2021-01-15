package com.example.demo.domain.board.upload;

import com.example.demo.domain.board.post.Post;
import com.example.demo.domain.member.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.security.Principal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@EntityListeners({AuditingEntityListener.class, UploadEventHandler.class})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "uuid")})
public class Upload {
    @Id @OrderBy
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Pattern(regexp = "^[ㄱ-ㅎ,ㅏ-ㅣ가-힣\\w\\s-]+\\.[A-Za-z]{1,}$")
    private String filename;
    @Pattern(regexp = "^[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}$")
    @Builder.Default
    private String uuid = UUID.randomUUID().toString();
    @ManyToOne
    private Post post;
    @CreatedBy
    @ManyToOne
    private Member uploader;

    @JsonIgnore
    public boolean isDeletable(Principal principal) {
        Member member = (Member) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Member uploader = this.uploader;
        Assert.isTrue(member.getId() == uploader.getId(), "upload must be deleted by writer");
        return true;
    }
}

package com.example.demo.domain.board.post;

import com.example.demo.domain.board.reply.Reply;
import com.example.demo.domain.board.upload.Upload;
import com.example.demo.domain.member.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@EntityListeners({AuditingEntityListener.class, PostEventHandler.class})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Cacheable(value = false)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tag;
    private String title;
    private String content;
    @CreatedDate
    @Column(name = "create_date", updatable = false)
    private LocalDateTime createDate;
    @LastModifiedDate
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
    @Builder.Default
    private Long views = 0L;

    @CreatedBy
    @ManyToOne(optional = false)
    private Member writer;
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Upload> uploads = new ArrayList<>();

    public void addUpload(Upload upload) {
        uploads.add(upload);
        upload.setPost(this);
    }

    public void addReply(Reply reply) {
        replies.add(reply);
        reply.setPost(this);
    }

    public void deleteUpload(int index) {
        Upload upload = uploads.get(index);
        uploads.remove(index);
    }

    public void deleteMessage(int index) {
        Reply message = replies.get(index);
        if (message.getReplies().isEmpty()) {
            replies.remove(index);
        } else {
            message.setWriter(null);
            message.setMessage("deleted topic");
        }
    }

    public void deleteMessage(Reply message) {
        replies.remove(message);
    }

    @JsonIgnore
    public boolean isCreatable() {
        Assert.isNull(this.id, "id must be null");
        Assert.hasText(this.title, "title must not be empty");
        Assert.hasText(this.content, "contents must not be empty");
        return true;
    }

    @JsonIgnore
    public boolean isUpdatable(Post origin, Principal principal) {
        Member writer = origin.writer;
        Member member = (Member) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Assert.isTrue(member.getId() == writer.getId(), "message must be updated by writer");
        Assert.isNull(this.id, "id must be null");
        return true;
    }

    @JsonIgnore
    public boolean isDeletable(Principal principal){
        Member writer = this.writer;
        Member member = (Member) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Assert.isTrue(member.getId() == writer.getId(), "message must be updated by writer");
        return true;
    }
}
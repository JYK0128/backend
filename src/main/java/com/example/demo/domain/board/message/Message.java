package com.example.demo.domain.board.message;

import com.example.demo.domain.board.post.Post;
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
@EntityListeners({AuditingEntityListener.class, MessageEventHandler.class})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Message {
    @Id @OrderBy
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String message;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDate; //TODO: Auditing when only null?
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @CreatedBy
    @ManyToOne
    private Member writer;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    private Message topic;
    @Builder.Default
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> replies = new ArrayList<>();  //TODO: Implement from List to TreeMap

    public void addReply(Message message) {
        replies.add(message);
        message.setTopic(this);
        message.setPost(this.post);
    }

    public void deleteReply(int index) {
        Message reply = replies.get(index);
        if(reply.getReplies().isEmpty()){
            replies.remove(index);
            reply.getPost().deleteMessage(reply);
        }else{
            reply.setWriter(null);
            reply.setMessage("deleted topic");
        }
    }

    @JsonIgnore
    public boolean isCreatable() {
        Assert.isNull(this.id, "id must be null");
        Assert.notNull(this.post, "post must not be null");
        Assert.notNull(this.message, "message must not be null");
        return true;
    }

    @JsonIgnore
    public boolean isUpdatable(Message origin, Principal principal) {
        Member writer = origin.writer;
        Member member = (Member) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Assert.isTrue(member.getId() == writer.getId(), "message must be updated by writer");

        Assert.isNull(this.id, "id must be null");
        Assert.isNull(this.post, "post must be null");
        Assert.notNull(this.message, "message must not be null");
        return true;
    }

    @JsonIgnore
    public boolean isDeletable(Principal principal) {
        Member writer = this.writer;
        Member member = (Member) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Assert.isTrue(member.getId() == writer.getId(), "message must be updated by writer");
        return true;
    }
}
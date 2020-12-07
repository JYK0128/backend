package com.example.demo.domain.board.message;

import com.example.demo.domain.board.post.Post;
import com.example.demo.domain.member.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
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
    @Column(updatable = false, insertable = false)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createDate;
    @LastModifiedDate
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
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
    private List<Message> replies = new ArrayList<>();

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
}
package com.example.demo.domain.board.message;

import com.example.demo.domain.board.post.Post;
import com.example.demo.domain.member.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.*;
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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    @Builder.Default
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updated = LocalDateTime.now();

    @ManyToOne
    private Member writer;
    @ManyToOne(fetch = FetchType.LAZY)
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
package com.example.demo.domain.board;

import com.example.demo.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;

    @ManyToOne
    private Member writer;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Post post;
    @ManyToOne
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
        }else{
            reply.setWriter(null);
            reply.setMessage("deleted topic");
        }
    }
}
package com.example.demo.domain.board;

import com.example.demo.domain.member.Member;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tag;
    private String title;
    private String contents;
    @Builder.Default
    private LocalDateTime updated = LocalDateTime.now();
    @Builder.Default
    private Long views = 0L;

    @ManyToOne(optional = false)
    private Member writer;
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Message> messages = new ArrayList<>();
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Upload> uploads = new ArrayList<>();

    public void addUpload(Upload upload) {
        uploads.add(upload);
        upload.setPost_id(this.id);
    }

    public void addMessage(Message message) {
        messages.add(message);
        message.setPost(this);
    }

    public void deleteUpload(int index) {
        Upload upload = uploads.get(index);
        uploads.remove(index);
    }

    public void deleteMessage(int index) {
        Message message = messages.get(index);
        if (message.getReplies().isEmpty()) {
            messages.remove(index);
        } else {
            message.setWriter(null);
            message.setMessage("deleted topic");
        }
    }

    @PreRemove
    public void preRemove(){
        messages.forEach(message -> message.setPost(null));
    }
}

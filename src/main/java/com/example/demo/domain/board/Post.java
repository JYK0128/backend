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
@Getter @Setter @Builder
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tag;
    private String title;
    private String content;
    @Builder.Default
    private LocalDateTime update = LocalDateTime.now();
    @Builder.Default
    private Long views = 0L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member writer;
    @Builder.Default
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
    @Builder.Default
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Upload> uploads = new ArrayList<>();

    public void addUpload(Upload upload) {
        uploads.add(upload);
        upload.setPost(this);
    }

    public void addMessage(Message message) {
        messages.add(message);
        message.setPost(this);
    }

    public void deleteUpload(int index){
        uploads.remove(index);
    }

    public void deleteMessage(int index) {
        Message message = messages.get(index);
        if(message.getReplies().isEmpty()){
            messages.remove(index);
        }else{
            message.setWriter(null);
            message.setMessage("deleted topic");
        }

    }
}

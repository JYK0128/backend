package com.example.demo.domain.board.post;

import com.example.demo.domain.board.message.Message;
import com.example.demo.domain.board.upload.Upload;
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
@EntityListeners({AuditingEntityListener.class, PostEventHandler.class})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Cacheable(value = false)
public class Post {
    @Id @OrderBy
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tag;
    private String title;
    private String contents;
    @CreatedDate
    @Column(updatable = false, insertable = false)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createDate;
    @LastModifiedDate
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime modifiedDate;
    @Builder.Default
    private Long views = 0L;

    @CreatedBy
    @ManyToOne(optional = false)
    private Member writer;
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
    @Builder.Default
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

    public void deleteMessage(Message message) {
        messages.remove(message);
    }

    public void read() {
        views += 1;
    }
}
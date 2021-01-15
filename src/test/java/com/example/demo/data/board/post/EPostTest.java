package com.example.demo.data.board.post;

import com.example.demo.domain.board.reply.Reply;
import com.example.demo.domain.board.post.Post;
import com.example.demo.domain.board.upload.Upload;
import com.example.demo.domain.member.member.Member;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EPostTest {
    private final Post post;


    public EPostTest() {
        Upload upload = mock(Upload.class);
        Reply message = mock(Reply.class);
        Member member = mock(Member.class);

        this.post = Post.builder()
                .id((long) 1)
                .tag("tag")
                .title("title")
                .createDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .views((long) 10)
                .content("test")

                .writer(member)
                .replies(Lists.newArrayList())
                .uploads(Lists.newArrayList())
                .build();
    }

    @Nested
    @Tag("entity")
    class Example_when {
        @Test
        void Post_has_No_Null_Fields() {
            assertThat(post).hasNoNullFieldsOrProperties();
        }
    }
}

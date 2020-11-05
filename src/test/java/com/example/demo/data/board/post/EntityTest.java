package com.example.demo.data.board.post;

import com.example.demo.domain.board.Message;
import com.example.demo.domain.board.Post;
import com.example.demo.domain.board.Upload;
import com.example.demo.domain.member.Member;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DPostTest {
    final Post post;


    public DPostTest() {
        Upload upload = mock(Upload.class);
        Message message = mock(Message.class);
        Member member = mock(Member.class);
        this.post = Post.builder()
                .id((long) 1)
                .tag("tag")
                .title("title")
                .date(LocalDateTime.now())
                .view((long) 10)
                .content("test")

                .member(member)
                .messages(Lists.newArrayList(message))
                .uploads(Lists.newArrayList(upload))
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

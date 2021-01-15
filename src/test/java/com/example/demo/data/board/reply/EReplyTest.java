package com.example.demo.data.board.reply;

import com.example.demo.domain.board.reply.Reply;
import com.example.demo.domain.board.post.Post;
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
public class EReplyTest {
    private final Reply message;

    public EReplyTest() {
        Reply reply = mock(Reply.class);
        Reply topic = mock(Reply.class);
        Member member = mock(Member.class);
        Post post = mock(Post.class);

        this.message = Reply.builder()
                .id((long) 1)
                .message("test")

                .topic(topic)
                .replies(Lists.newArrayList())
                .post(post)
                .writer(member)
                .createDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
    }

    @Nested
    @Tag("entity")
    class Example_when {
        @Test
        void Message_has_No_Null_Fields() {
            assertThat(message).hasNoNullFieldsOrProperties();
        }
    }
}

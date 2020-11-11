package com.example.demo.data.member.member;

import com.example.demo.domain.board.Message;
import com.example.demo.domain.board.Post;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ProviderType;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EMemberTest {
    final Member member;

    EMemberTest() {
        Message message = mock(Message.class);
        Post post = mock(Post.class);
        this.member = Member.builder()
                .id((long) 1)
                .email("test@test.com")
                .nickname("test")
                .provider(ProviderType.KAKAO)

                .posts(Lists.newArrayList(post))
                .messages(Lists.newArrayList(message))
                .build();
    }

    @Nested
    @Tag("entity")
    class Example_when {
        @Test
        void Member_has_No_Null_Fields() {
            assertThat(member).hasNoNullFieldsOrProperties();
        }
    }
}
package com.example.demo.data.board.upload;

import com.example.demo.domain.board.post.Post;
import com.example.demo.domain.board.upload.Upload;
import com.example.demo.domain.member.member.Member;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EUploadTest {
    private final Upload upload;


    public EUploadTest() {
        Post post = mock(Post.class);
        Member member = mock(Member.class);

        this.upload = Upload.builder()
                .id((long) 1)
                .filename("test.txt")
                .uuid(UUID.randomUUID().toString())
                .post(post)
                .uploader(member)
                .build();
    }

    @Nested
    @Tag("entity")
    class Example_when {
        @Test
        void Upload_has_No_Null_Fields() {
            assertThat(upload).hasNoNullFieldsOrProperties();
        }
    }
}
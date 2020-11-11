package com.example.demo.business;

import com.example.demo.domain.board.Message;
import com.example.demo.domain.board.Post;
import com.example.demo.domain.board.Upload;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ProviderType;
import com.example.demo.repository.board.MessageRepository;
import com.example.demo.repository.board.PostRepository;
import com.example.demo.repository.board.UploadRepository;
import com.example.demo.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BoardServiceTests {
    private EntityManager entityManager;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final UploadRepository uploadRepository;
    private final MessageRepository messageRepository;
    private Member.MemberBuilder preDefinedWriterBuilder;
    private Member.MemberBuilder preDefinedReplierBuilder;
    private Post.PostBuilder preDefinedPostBuilder;

    @Autowired
    BoardServiceTests(EntityManager entityManager, MemberRepository memberRepository,
                      PostRepository postRepository, MessageRepository messageRepository, UploadRepository uploadRepository) {
        this.entityManager = entityManager;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.uploadRepository = uploadRepository;
        this.messageRepository = messageRepository;
    }

    @BeforeAll
    void setUp() {
        preDefinedWriterBuilder = Member.builder()
                .email("test1@test.com")
                .nickname("test1")
                .provider(ProviderType.KAKAO);

        preDefinedReplierBuilder = Member.builder()
                .email("test2@test.com")
                .nickname("test2")
                .provider(ProviderType.GOOGLE);

        preDefinedPostBuilder = Post.builder()
                .tag("test")
                .title("hello")
                .content("write post")
                .date(LocalDateTime.now())
                .view((long) 0);
    }

    @Test
    void create_post() {
        //given
        Member writer = this.preDefinedWriterBuilder.build();
        writer = memberRepository.save(writer);

        //when
        Post post = preDefinedPostBuilder
                .writer(writer)
                .build();
        post = postRepository.save(post);

        Upload upload = Upload.builder()
                .oriName("test.txt")
                .uuid(UUID.randomUUID().toString())
                .post(post)
                .build();
        upload = uploadRepository.save(upload);

        entityManager.refresh(post);
        entityManager.refresh(writer);

        //then
        assertThat(writer.getPosts()).contains(post);
        assertThat(post.getUploads()).contains(upload);

        assertThat(upload.getPost()).isEqualTo(post);
        assertThat(post.getWriter()).isEqualTo(writer);
    }

    @Test
    void update_post() {
        //given
        Member writer = this.preDefinedWriterBuilder.build();
        writer = memberRepository.save(writer);

        Post post = preDefinedPostBuilder
                .writer(writer)
                .build();
        post = postRepository.save(post);

        //when
        post.setTitle("updated post");
        post = postRepository.save(post);

        //then
        assertThat(post.getTitle()).isEqualTo("updated post");
    }

    @Test
    void delete_post() {
        //given
        Member writer = this.preDefinedWriterBuilder.build();
        writer = memberRepository.save(writer);

        Post post = preDefinedPostBuilder
                .writer(writer)
                .build();
        post = postRepository.save(post);

        //when
        postRepository.delete(post);

        //then
        assertFalse(entityManager.contains(post));
    }

    @Test
    void visit_post() {
        //given
        Member writer = this.preDefinedWriterBuilder.build();
        writer = memberRepository.save(writer);

        Post post = preDefinedPostBuilder
                .writer(writer)
                .build();
        post = postRepository.save(post);

        //when
        long view = post.getView() + 1;
        post.setView(view);
        postRepository.save(post);

        //then
        assertThat(post.getView()).isEqualTo(view);
    }

    @Test
    void add_upload(){
        //given
        Member writer = this.preDefinedWriterBuilder.build();
        writer = memberRepository.save(writer);

        Post post = preDefinedPostBuilder
                .writer(writer)
                .build();
        post = postRepository.save(post);

        Upload upload = Upload.builder()
                .oriName("test.txt")
                .uuid(UUID.randomUUID().toString())
                .post(post)
                .build();
        uploadRepository.save(upload);

        //when
        Upload newUpload = Upload.builder()
                .oriName("newUpload")
                .uuid(UUID.randomUUID().toString())
                .post(post)
                .build();
        newUpload = uploadRepository.save(newUpload);

        entityManager.refresh(post);

        //then
        assertThat(post.getUploads())
                .hasSameElementsAs(Arrays.asList(upload, newUpload));
    }

    @Test
    void delete_upload(){
        //given
        Member writer = this.preDefinedWriterBuilder.build();
        writer = memberRepository.save(writer);

        Post post = preDefinedPostBuilder
                .writer(writer)
                .build();
        post = postRepository.save(post);

        Upload upload = Upload.builder()
                .oriName("test.txt")
                .uuid(UUID.randomUUID().toString())
                .post(post)
                .build();
        upload = uploadRepository.save(upload);

        Upload newUpload = Upload.builder()
                .oriName("newUpload")
                .uuid(UUID.randomUUID().toString())
                .post(post)
                .build();
        newUpload = uploadRepository.save(newUpload);

        //when
        uploadRepository.delete(newUpload);
        uploadRepository.flush();
        entityManager.refresh(post);

        //then
        assertThat(post.getUploads())
                .hasSameElementsAs(Collections.singletonList(upload));
    }

    @Test
    void write_message() {
        //given
        Member writer = this.preDefinedWriterBuilder.build();
        Member replier = this.preDefinedReplierBuilder.build();
        memberRepository.saveAll(Arrays.asList(writer, replier));

        Post post = preDefinedPostBuilder.writer(writer).build();
        post = postRepository.save(post);

        //when
        Message message = Message.builder().writer(replier).post(post).message("message").build();
        message = messageRepository.save(message);

        entityManager.refresh(post);

        //then
        assertThat(post.getMessages()).hasSameElementsAs(Collections.singletonList(message));
    }

    @Test
    void write_reply() {
        //given
        Member writer = this.preDefinedWriterBuilder.build();
        writer = memberRepository.save(writer);

        Member replier = this.preDefinedReplierBuilder.build();
        replier = memberRepository.save(replier);

        Post post = preDefinedPostBuilder.writer(writer).build();
        post = postRepository.save(post);

        Message message = Message.builder().writer(replier).post(post).message("message").build();
        message = messageRepository.save(message);

        //when
        Message reply = Message.builder().writer(writer).post(post).message("reply").topic(message).build();
        reply = messageRepository.save(reply);

        entityManager.refresh(message);

        //then
        assertThat(message.getReply()).contains(reply);
        assertThat(reply.getTopic()).isEqualTo(message);
    }

    @Test
    void update_message() {
        //given
        Member writer = this.preDefinedWriterBuilder.build();
        Member replier = this.preDefinedReplierBuilder.build();
        memberRepository.saveAll(Arrays.asList(writer, replier));

        Post post = preDefinedPostBuilder.writer(writer).build();
        post = postRepository.save(post);

        Message message = Message.builder().writer(replier).post(post).message("message").build();
        message = messageRepository.save(message);

        entityManager.refresh(post);

        //when
        message.setMessage("updated message");
        message = messageRepository.save(message);

        //then
        assertThat(message.getMessage()).isEqualTo("updated message");
    }

    @Test
    void delete_message(){
        //given
        Member writer = this.preDefinedWriterBuilder.build();
        Member replier = this.preDefinedReplierBuilder.build();
        memberRepository.saveAll(Arrays.asList(writer, replier));

        Post post = preDefinedPostBuilder.writer(writer).build();
        post = postRepository.save(post);

        Message message = Message.builder().writer(replier).post(post).message("message").build();
        message = messageRepository.save(message);

        Message reply = Message.builder().writer(writer).post(post).message("reply").topic(message).build();
        reply = messageRepository.save(reply);

        entityManager.refresh(message);

        //when
        message.setMessage("deleted message");
        message.setWriter(null);
        message = messageRepository.save(message);

        //then
        assertThat(reply.getTopic()).isEqualTo(message);
        assertThat(reply.getTopic().getMessage()).isEqualTo("deleted message");
        assertThat(reply.getTopic().getWriter()).isNull();
    }

    @Test
    void delete_reply(){
        //given
        Member writer = this.preDefinedWriterBuilder.build();
        Member replier = this.preDefinedReplierBuilder.build();
        memberRepository.saveAll(Arrays.asList(writer, replier));

        Post post = preDefinedPostBuilder.writer(writer).build();
        post = postRepository.save(post);

        Message message = Message.builder().writer(replier).post(post).message("message").build();
        message = messageRepository.save(message);

        Message reply = Message.builder().writer(writer).post(post).message("reply").topic(message).build();
        reply = messageRepository.save(reply);

        //when
        messageRepository.delete(reply);
        messageRepository.flush();
        entityManager.refresh(message);

        //then
        assertThat(message.getReply()).isEmpty();
    }
}
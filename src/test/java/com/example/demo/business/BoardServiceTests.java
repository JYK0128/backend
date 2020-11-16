package com.example.demo.business;

import com.example.demo.config.security.OAuthServerProvider;
import com.example.demo.domain.board.Message;
import com.example.demo.domain.board.Post;
import com.example.demo.domain.board.Upload;
import com.example.demo.domain.member.Member;
import com.example.demo.repository.board.MessageRepository;
import com.example.demo.repository.board.PostRepository;
import com.example.demo.repository.board.UploadRepository;
import com.example.demo.repository.member.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BoardServiceTests {
    private EntityManager entityManager;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final UploadRepository uploadRepository;
    private final MessageRepository messageRepository;
    private Post.PostBuilder preDefinedPostBuilder;

    Member writer, replier;

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
        writer = Member.builder()
                .email("test1@test.com")
                .provider(OAuthServerProvider.KAKAO)
                .build();

        replier = Member.builder()
                .email("test2@test.com")
                .provider(OAuthServerProvider.GOOGLE)
                .build();

        memberRepository.saveAll(Arrays.asList(writer, replier));

        preDefinedPostBuilder = Post.builder()
                .tag("tag")
                .title("title")
                .content("content");
    }

    @Test
    @Order(1)
    @Rollback(value = false)
    void writePost() {
        Upload upload = Upload.builder().oriName("one.txt").build();
        Post post = preDefinedPostBuilder.writer(writer).build();
        post.addUpload(upload);
        postRepository.save(post);

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(upload.getPost()).isEqualTo(post);
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void addUpload() {
        Post post = postRepository.findById((long) 1).get();
        Upload upload = Upload.builder().oriName("two.txt").build();
        post.addUpload(upload);

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(upload.getPost()).isEqualTo(post);
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    void deleteUpload() {
        Post post = postRepository.findById((long) 1).get();
        post.deleteUpload(1);

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(uploadRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @Order(4)
    @Rollback(value = false)
    void visitPost(){
        Post post = postRepository.findById((long) 1).get();
        post.setViews(post.getViews() + 1);

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(post.getViews()).isEqualTo(1);
    }

    @Test
    @Order(5)
    @Rollback(value = false)
    void addMessage() {
        Post post = postRepository.findById((long) 1).get();
        Message topic1 = Message.builder().message("topic1").writer(replier).build();
        Message topic2 = Message.builder().message("topic2").writer(replier).build();
        Message topic3 = Message.builder().message("topic3").writer(replier).build();

        post.addMessage(topic1);
        post.addMessage(topic2);
        post.addMessage(topic3);

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(topic1.getPost()).isEqualTo(post);
        assertThat(topic2.getPost()).isEqualTo(post);
        assertThat(topic3.getPost()).isEqualTo(post);
    }

    @Test
    @Order(6)
    @Rollback(value = false)
    void deleteMessage() {
        Post post = postRepository.findById((long) 1).get();
        post.deleteMessage(1);

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(messageRepository.findAll().size()).isEqualTo(2);
    }


    @Test
    @Order(7)
    @Rollback(value = false)
    void addReply() {
        Post post = postRepository.findById((long) 1).get();
        Message topic = post.getMessages().get(0);
        Message reply1 = Message.builder().message("reply1").writer(writer).build();
        Message reply2 = Message.builder().message("reply2").writer(writer).build();
        topic.addReply(reply1);
        topic.addReply(reply2);

        Message reply1_1 = Message.builder().message("reply1_1").writer(writer).build();
        reply1.addReply(reply1_1);

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(reply1.getTopic()).isEqualTo(topic);
        assertThat(reply2.getTopic()).isEqualTo(topic);
        assertThat(reply1_1.getTopic()).isEqualTo(reply1);
    }

    @Test
    @Order(8)
    @Rollback(value = false)
    void deleteReply() {
        Post post = postRepository.findById((long) 1).get();
        Message message = post.getMessages().get(0);
        message.deleteReply(1);

        assertThat(entityManager.contains(post)).isTrue();
    }

    @Test
    @Order(9)
    @Rollback(value = false)
    void deleteTopicWhenExistReply() {
        Post post = postRepository.findById((long) 1).get();
        Message deletedMessage = post.getMessages().get(0);
        post.deleteMessage(0);

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(deletedMessage.getMessage()).isEqualTo("deleted topic");
        assertThat(deletedMessage.getWriter()).isNull();
    }

    @Test
    @Order(10)
    @Rollback(value = false)
    void deleteReplyWhenExistReply() {
        Post post = postRepository.findById((long) 1).get();
        Message topic = post.getMessages().get(0);
        Message deletedReply = topic.getReplies().get(0);
        topic.deleteReply(0);

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(deletedReply.getMessage()).isEqualTo("deleted topic");
        assertThat(deletedReply.getWriter()).isNull();
    }

    @Test
    @Order(999)
    @Rollback(value = false)
    void deletePost() {
        assertThat(uploadRepository.findAll().size()).isEqualTo(1);
        assertThat(messageRepository.findAll().size()).isEqualTo(4);

        Post post = postRepository.findById((long) 1).get();
        postRepository.delete(post);

        assertThat(postRepository.findAll()).isEmpty();
        assertThat(messageRepository.findAll()).isEmpty();
        assertThat(uploadRepository.findAll()).isEmpty();
    }
}
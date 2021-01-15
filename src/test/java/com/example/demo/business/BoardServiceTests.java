package com.example.demo.business;

import com.example.demo.config.security.OAuthProvider;
import com.example.demo.domain.board.reply.Reply;
import com.example.demo.domain.board.reply.ReplyRepository;
import com.example.demo.domain.board.post.Post;
import com.example.demo.domain.board.post.PostRepository;
import com.example.demo.domain.board.upload.Upload;
import com.example.demo.domain.board.upload.UploadRepository;
import com.example.demo.domain.member.member.Member;
import com.example.demo.domain.member.member.MemberRepository;
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
    private final EntityManager entityManager;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final UploadRepository uploadRepository;
    private final ReplyRepository replyRepository;
    private Post.PostBuilder preDefinedPostBuilder;

    Member writer, replier;

    @Autowired
    BoardServiceTests(EntityManager entityManager, MemberRepository memberRepository,
                      PostRepository postRepository, ReplyRepository replyRepository, UploadRepository uploadRepository) {
        this.entityManager = entityManager;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.uploadRepository = uploadRepository;
        this.replyRepository = replyRepository;
    }

    @BeforeAll
    void setUp() {
        writer = Member.builder()
                .email("test1@test.com")
                .provider(OAuthProvider.KAKAO)
                .build();
        replier = Member.builder()
                .email("test2@test.com")
                .provider(OAuthProvider.GOOGLE)
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
        Upload upload = Upload.builder().filename("one.txt").build();
        uploadRepository.save(upload);

        Post post = preDefinedPostBuilder.writer(writer).build();
        post.addUpload(upload);
        postRepository.save(post);

        entityManager.flush();

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(entityManager.contains(upload)).isTrue();
        assertThat(post.getUploads()).containsExactly(upload);
        assertThat(upload.getPost()).isEqualTo(post);
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void addUpload() {
        Post post = postRepository.findById((long) 1).get();
        Upload upload = Upload.builder().filename("two.txt").build();
        post.addUpload(upload);

        entityManager.flush();

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(entityManager.contains(upload)).isTrue();
        assertThat(post.getUploads()).contains(upload);
        assertThat(post.getUploads().size()).isEqualTo(2);
        assertThat(upload.getPost()).isEqualTo(post);
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    void deleteUpload() {
        Post post = postRepository.findById((long) 1).get();
        post.deleteUpload(1);

        entityManager.flush();

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(uploadRepository.findAll()).containsExactlyElementsOf(post.getUploads());
        assertThat(uploadRepository.findAll().size()).isEqualTo(1);

    }

    @Test
    @Order(4)
    @Rollback(value = false)
    void visitPost(){
        Post post = postRepository.findById((long) 1).get();
        post.setViews(post.getViews() + 1);

        entityManager.flush();

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(post.getViews()).isEqualTo(1);
    }

    @Test
    @Order(5)
    @Rollback(value = false)
    void addMessage() {
        Post post = postRepository.findById((long) 1).get();
        Reply topic1 = Reply.builder().message("topic1").writer(replier).build();
        Reply topic2 = Reply.builder().message("topic2").writer(replier).build();
        Reply topic3 = Reply.builder().message("topic3").writer(replier).build();

        post.addReply(topic1);
        post.addReply(topic2);
        post.addReply(topic3);

        entityManager.flush();

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(entityManager.contains(topic1)).isTrue();
        assertThat(entityManager.contains(topic2)).isTrue();
        assertThat(entityManager.contains(topic3)).isTrue();
        assertThat(post.getReplies()).containsExactly(topic1, topic2, topic3);
        assertThat(topic1.getPost()).isEqualTo(post);
        assertThat(topic2.getPost()).isEqualTo(post);
        assertThat(topic3.getPost()).isEqualTo(post);
    }

    @Test
    @Order(6)
    @Rollback(value = false)
    void deleteMessage() {
        Post post = postRepository.findById((long) 1).get();
        post.deleteMessage(0);

        entityManager.flush();

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(replyRepository.findAll()).containsAnyElementsOf(post.getReplies());
        assertThat(replyRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @Order(7)
    @Rollback(value = false)
    void addReply() {
        Post post = postRepository.findById((long) 1).get();
        Reply topic = post.getReplies().get(0);
        Reply reply1 = Reply.builder().message("reply1").writer(writer).build();
        Reply reply2 = Reply.builder().message("reply2").writer(writer).build();
        topic.addReply(reply1);
        topic.addReply(reply2);

        Reply reply1_1 = Reply.builder().message("reply1_1").writer(writer).build();
        reply1.addReply(reply1_1);

        entityManager.flush();

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(reply1.getTopic()).isEqualTo(topic);
        assertThat(reply2.getTopic()).isEqualTo(topic);
        assertThat(reply1_1.getTopic()).isEqualTo(reply1);

        assertThat(reply1.getPost()).isEqualTo(post);
        assertThat(reply2.getPost()).isEqualTo(post);
        assertThat(reply1_1.getPost()).isEqualTo(post);
        assertThat(replyRepository.findAll().size()).isEqualTo(5);
    }

    @Test
    @Order(8)
    @Rollback(value = false)
    void deleteReplyWhenNotExistReply() {
        Post post = postRepository.findById((long) 1).get();
        Reply message = post.getReplies().get(0);
        message.deleteReply(1);

        assertThat(post.getReplies().size()).isEqualTo(4);
        assertThat(message.getReplies().size()).isEqualTo(1);

        entityManager.flush();
        entityManager.refresh(message);

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(entityManager.contains(message)).isTrue();
        assertThat(post.getReplies().size()).isEqualTo(4);
        assertThat(replyRepository.findAll().size()).isEqualTo(4);
    }

    @Test
    @Order(9)
    @Rollback(value = false)
    void deleteTopicWhenExistReply() {
        Post post = postRepository.findById((long) 1).get();
        Reply deletedMessage = post.getReplies().get(0);
        post.deleteMessage(0);

        entityManager.flush();

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(entityManager.contains(deletedMessage)).isTrue();
        assertThat(deletedMessage.getMessage()).isEqualTo("deleted topic");
        assertThat(deletedMessage.getWriter()).isNull();
        assertThat(deletedMessage.getPost()).isEqualTo(post);
        assertThat(post.getReplies()).contains(deletedMessage);
        assertThat(post.getReplies().size()).isEqualTo(4);
    }

    @Test
    @Order(10)
    @Rollback(value = false)
    void deleteReplyWhenExistReply() {
        Post post = postRepository.findById((long) 1).get();
        Reply topic = post.getReplies().get(0);
        Reply deletedReply = topic.getReplies().get(0);
        topic.deleteReply(0);

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(entityManager.contains(deletedReply)).isTrue();
        assertThat(deletedReply.getMessage()).isEqualTo("deleted topic");
        assertThat(deletedReply.getWriter()).isNull();
        assertThat(deletedReply.getReplies()).isNotEmpty();
        assertThat(deletedReply.getPost()).isEqualTo(post);
        assertThat(post.getReplies().size()).isEqualTo(4);
        assertThat(memberRepository.findById(1L).get().getMessages().size()).isEqualTo(2);
    }

    @Test
    @Order(999)
    @Rollback(value = false)
    void deletePost() {
        assertThat(uploadRepository.findAll().size()).isEqualTo(1);
        assertThat(replyRepository.findAll().size()).isEqualTo(4);

        Post post = postRepository.findById((long) 1).get();
        postRepository.deleteById(post.getId());

        assertThat(postRepository.findAll()).isEmpty();
        assertThat(uploadRepository.findAll()).isEmpty();
        assertThat(memberRepository.findById(1L).get().getMessages().size()).isEqualTo(0);
        assertThat(replyRepository.findAll().size()).isEqualTo(0);
    }
}
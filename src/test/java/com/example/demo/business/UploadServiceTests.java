package com.example.demo.business;

import com.example.demo.config.security.OAuthServerProvider;
import com.example.demo.domain.board.post.Post;
import com.example.demo.domain.board.post.PostRepository;
import com.example.demo.domain.board.upload.Upload;
import com.example.demo.domain.board.upload.UploadRepository;
import com.example.demo.domain.member.member.Member;
import com.example.demo.domain.member.member.MemberRepository;
import com.example.demo.util.FileUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UploadServiceTests {
    private final EntityManager entityManager;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final UploadRepository uploadRepository;

    private final String projDir = System.getProperty("user.dir");
    private final String fileDir = projDir + "/src/main/webapp/FILE-SYS/";

    @Autowired
    UploadServiceTests(EntityManager entityManager,
            MemberRepository memberRepository, PostRepository postRepository, UploadRepository uploadRepository) {
        this.entityManager = entityManager;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.uploadRepository = uploadRepository;
    }

    @BeforeAll
    void setUp() {
        Member member = Member.builder()
                .email("test@test.com")
                .provider(OAuthServerProvider.GOOGLE)
                .build();
        memberRepository.save(member);
    }

    @Test
    @Order(1)
    @Rollback(value = false)
    void addFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile("test.txt", "test".getBytes());

        Upload upload = FileUtils.upload(file);
        uploadRepository.save(upload);

        assertThat(entityManager.contains(upload)).isTrue();
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void after_addFile() {
        Member member = memberRepository.findById(1L).get();
        Upload upload = uploadRepository.findById(1L).get();

        Post post = Post.builder()
                .writer(member)
                .tag("test")
                .title("test")
                .contents("test").build();
        post.addUpload(upload);
        postRepository.save(post);

        entityManager.flush();
        entityManager.refresh(upload);

        assertEquals(upload.getPost(), post);
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    void updateFile() throws IOException {
        deleteFile();
        addFile();

        Post post = postRepository.findById(1L).get();
        Upload upload = uploadRepository.findById(2L).get();

        post.addUpload(upload);
        postRepository.save(post);

        assertEquals(upload.getPost(), post);
    }

    @Test
    @Order(4)
    @Rollback(value = false)
    void deleteFile() {
        Post post = postRepository.findById(1L).get();
        post.deleteUpload(0);
        entityManager.flush();

        assertThat(uploadRepository.findById(2L)).isEmpty();
    }
}
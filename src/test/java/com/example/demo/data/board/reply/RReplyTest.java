package com.example.demo.data.board.reply;

import com.example.demo.domain.board.reply.Reply;
import com.example.demo.domain.board.reply.ReplyRepository;
import com.example.demo.domain.board.post.Post;
import com.example.demo.domain.board.post.PostRepository;
import com.example.demo.domain.member.member.Member;
import com.example.demo.domain.member.member.MemberRepository;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RReplyTest {
    private final EntityManager entityManager;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;


    @Autowired
    RReplyTest(EntityManager entityManager,
               PostRepository postRepository,
               MemberRepository memberRepository,
               ReplyRepository replyRepository) {
        this.entityManager = entityManager;
        this.postRepository = postRepository;
        this.replyRepository = replyRepository;
        this.memberRepository = memberRepository;
    }

    @BeforeAll
    void setUp(){
        Member member = memberRepository.save(new Member());
        Member replier = memberRepository.save(new Member());
        Post post = Post.builder().writer(member).build();
        post = postRepository.save(post);

        Reply l1 = null;
        Reply l2 = null;
        Reply l3 = null;
        List<Reply> messages = new ArrayList<>();

        for (int i = 0; i<10; i++){
            Reply.ReplyBuilder replyBuilder = Reply.builder()
                    .post(post)
                    .writer(replier)
                    .message("message" + i);

            switch (i%3){
                case 0: {
                    l1 = replyBuilder.build();
                    messages.add(l1);
                    break;
                }
                case 1: {
                    l2 = replyBuilder
                            .topic(l1)
                            .build();
                    l1.setReplies(Lists.newArrayList(l2));
                    messages.add(l2);
                    break;
                }
                case 2: {
                    l3 = replyBuilder
                            .topic(l2)
                            .build();
                    l2.setReplies(Lists.newArrayList(l3));
                    messages.add(l3);
                    break;
                }
            }
        }

        replyRepository.saveAll(messages);
    }

    @Nested
    @Tag("query")
    class Query_that{

    }
}

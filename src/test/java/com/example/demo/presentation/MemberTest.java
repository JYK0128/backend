package com.example.demo.presentation;

import com.example.demo.config.security.OAuthServerProvider;
import com.example.demo.domain.member.Member;
import com.example.demo.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({RestDocumentationExtension.class})
public class MemberTest {
    private final MemberRepository memberRepository;
    private MockMvc mockMvc;

    @Autowired
    MemberTest(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @BeforeAll
    public void setUp() {
        Member member = Member.builder()
                .email("test@test.com")
                .provider(OAuthServerProvider.GOOGLE)
                .build();
        memberRepository.save(member);
    }

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .snippets().withDefaults()
                        .and()
                        .operationPreprocessors()
                        .withRequestDefaults(
                                modifyUris().scheme("https").host("api.jyworld.tk").removePort(),
                                prettyPrint()
                        )
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    void crud_service_tests() throws Exception {
        read();
        update();
        delete();
    }

    void read() throws Exception {
        read_user_list();
        read_user();
        read_posts_from_user();
        read_messages_from_user();
    }

    void update() {
        // will implement, you need more detail user info.
    }

    void delete() {
        // not implement.
    }

    void read_user_list() throws Exception {
        mockMvc.perform(get("/member").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get/member",
                        httpRequest()
                ));

        mockMvc.perform(get("/member?page=0&size=10&sort=email,asc&sort=id,desc").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get/member",
                        requestParameters(
                                parameterWithName("page").description("page start from 0"),
                                parameterWithName("size").description("page size"),
                                parameterWithName("sort").description("sort and order on columns")
                        ),
                        responseFields(
                                subsectionWithPath("_embedded.members").description("member list"),
                                subsectionWithPath("_links").ignored().description("list of resource profiles"),
                                subsectionWithPath("page").ignored().description("paging info")
                        )
                ));
    }

    void read_user() throws Exception {
        mockMvc.perform(get("/member/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get/member/id",
                        pathParameters(
                                parameterWithName("id").description("user id in DB")
                        ),
                        responseFields(
                                fieldWithPath("email").description("user email from auth providers"),
                                fieldWithPath("provider").description("provider name"),
                                subsectionWithPath("_links.posts").description("list of posts"),
                                subsectionWithPath("_links.messages").description("list of messages"),
                                subsectionWithPath("_links").ignored().description("list of resource profiles")
                        )
                ));
    }

    void read_posts_from_user() {
        //paging
    }

    void read_messages_from_user() {
        //paging
    }
}
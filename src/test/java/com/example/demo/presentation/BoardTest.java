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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({RestDocumentationExtension.class})
public class BoardTest {
    private final MemberRepository memberRepository;
    private MockMvc mockMvc;

    @Autowired
    BoardTest(MemberRepository memberRepository) {
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
    public void setMockMvc(WebApplicationContext webApplicationContext,
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
        create();
        read();
        update();
        delete();
    }

    void create() throws Exception {
        create_upload();
        create_post();
        create_topic();
        create_reply();
    }

    void read() throws Exception {
        read_post_list();
        read_post();
        read_upload_list();
        read_message_list();
    }

    void update() throws Exception {
        update_upload_list();
        update_post();
        update_message();
    }

    void delete() throws Exception {
        delete_post();
        delete_message();
    }

    void create_upload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                "multipart/form-data", StandardCharsets.UTF_8.encode("test").array());

        mockMvc.perform(multipart("/upload").file(file).contentType("multipart/form-data"))
                .andExpect(status().isCreated())
                .andDo(document("post/upload",
                        requestParts(partWithName("file").description("The file to upload"))
                ));
    }

    void create_post() throws Exception {
        mockMvc.perform(post("/post").content("{\n" +
                "  \"id\" : 1,\n" +
                "  \"writer\" : \"http://localhost/member/1\",\n" +
                "  \"tag\" : \"test\",\n" +
                "  \"title\" : \"test\",\n" +
                "  \"contents\" : \"test\",\n" +
                "  \"uploads\" : [\"http://localhost/upload/1\"] \n" +
                "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("post/post",
                        requestFields(
                                fieldWithPath("id").optional().description("id when update"),
                                fieldWithPath("writer").description("writer"),
                                fieldWithPath("tag").description("tag"),
                                fieldWithPath("title").description("title"),
                                fieldWithPath("contents").description("contents"),
                                fieldWithPath("uploads").description("file list")
                        )
                ));
    }

    void create_topic() throws Exception {
        mockMvc.perform(post("/message").content("{\n" +
                "    \"message\" : \"test\",\n" +
                "    \"writer\" : \"http://localhost/member/1\",\n" +
                "    \"post\" : \"http://localhost/post/1\",\n" +
                "    \"topic\" : null\n" +
                "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("post/message",
                        requestFields(
                                fieldWithPath("message").description("message"),
                                fieldWithPath("writer").description("writer"),
                                fieldWithPath("post").description("post"),
                                fieldWithPath("topic").description("topic")
                        )
                ));
    }

    void create_reply() throws Exception {
        mockMvc.perform(post("/message").content("{\n" +
                "    \"message\" : \"test\",\n" +
                "    \"writer\" : \"http://localhost/member/1\",\n" +
                "    \"post\" : \"http://localhost/post/1\",\n" +
                "    \"topic\" : \"http://localhost/post/1/messages/1\"\n" +
                "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    void read_post_list() throws Exception{
        mockMvc.perform(get("/post").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get/post",
                        httpRequest()
                ));

        mockMvc.perform(get("/post?page=0&size=10&sort=email,asc&sort=id,desc").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get/post",
                        requestParameters(
                                parameterWithName("page").description("page start from 0"),
                                parameterWithName("size").description("page size"),
                                parameterWithName("sort").description("sort and order on columns")
                        ),
                        responseFields(
                                subsectionWithPath("_embedded.posts").description("list of posts"),
                                subsectionWithPath("_links").ignored().description("list of resource profiles"),
                                subsectionWithPath("page").ignored().description("paging info")
                        )
                ));
    }

    void read_post() throws Exception {
        mockMvc.perform(get("/post/{id}", 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get/board/id",
                        responseFields(
                                fieldWithPath("tag").description("tag"),
                                fieldWithPath("title").description("title"),
                                fieldWithPath("contents").description("contents"),
                                fieldWithPath("updated").optional().description("updated"),
                                fieldWithPath("views").optional().description("views"),
                                subsectionWithPath("_links.writer").description("writer"),
                                subsectionWithPath("_links.uploads").description("list of uploads"),
                                subsectionWithPath("_links.messages").description("list of messages"),
                                subsectionWithPath("_links").ignored().description("list of resource profiles")
                        )
                ));
    }

    void read_message_list() throws Exception {
        //paging
    }

    void read_upload_list() throws Exception{
        //paging
    }

    private void update_upload_list() throws Exception {
        delete_upload();
        create_upload();
        create_topic();
    }

    void update_post() throws Exception {
        mockMvc.perform(put("/post/{id}", 1).content("{\n" +
                "  \"writer\" : \"http://localhost/member/1\",\n" +
                "  \"tag\" : \"test1\",\n" +
                "  \"title\" : \"test1\",\n" +
                "  \"content\" : \"test1\"\n" +
                "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("put/post/id",
                        httpRequest()
                ));
    }

    private void update_message() {
    }

    private void delete_post() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/post/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("delete/post/id",
                        httpRequest()
                ));
    }

    private void delete_upload() {
    }

    private void delete_message() {
    }
}
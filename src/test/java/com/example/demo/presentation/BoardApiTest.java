package com.example.demo.presentation;

import com.example.demo.config.security.OAuthProvider;
import com.example.demo.domain.board.reply.ReplyRepository;
import com.example.demo.domain.board.post.PostRepository;
import com.example.demo.domain.board.upload.UploadRepository;
import com.example.demo.domain.member.member.Member;
import com.example.demo.domain.member.member.MemberRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static com.example.demo.utils.SnippetUtils.responseFieldsCustom;
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({RestDocumentationExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BoardApiTest {
    private final WebApplicationContext webApplicationContext;

    private final MemberRepository memberRepository;
    private Member member;
    private Member hacker;
    private MockMvc mockMvc;

    @Autowired
    BoardApiTest(WebApplicationContext webApplicationContext,
                 MemberRepository memberRepository, PostRepository postRepository,
                 UploadRepository uploadRepository, ReplyRepository replyRepository) {
        this.webApplicationContext = webApplicationContext;
        this.memberRepository = memberRepository;
    }

    @BeforeAll
    public void setUp() {
        member = Member.builder()
                .email("test@test.com")
                .provider(OAuthProvider.GOOGLE)
                .build();
        memberRepository.save(member);
        hacker = Member.builder()
                .email("test@test.com")
                .provider(OAuthProvider.NAVER)
                .build();
        memberRepository.save(hacker);
    }

    @BeforeEach
    public void setMockMvc(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
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
    @Order(1)
    @Rollback(value = false)
    void create_upload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "test.txt",
                "multipart/form-data", StandardCharsets.UTF_8.encode("test").array());

        mockMvc.perform(multipart("/upload")
                .file(file).contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(member))
        )
                .andExpect(status().isCreated())
                .andDo(document("post/upload",
                        httpRequest(),
                        requestParts(partWithName("files").description("files to upload")),
                        responseFields(
                                fieldWithPath("_embedded.uploads[].filename").description("filename"),
                                fieldWithPath("_embedded.uploads[].uuid").description("uuid"),
                                subsectionWithPath("_embedded.uploads[]._links").ignored()
                        ),
                        responseFieldsCustom("response-links", null,
                                fieldWithPath("_embedded.uploads[]._links.self").description("link to self"),
                                fieldWithPath("_embedded.uploads[]._links.upload").ignored().description("link to upload"),
                                fieldWithPath("_embedded.uploads[]._links.post").description("link to post"),
                                subsectionWithPath("_embedded.uploads[]").ignored()
                        )
                ));
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void create_post() throws Exception {
        mockMvc.perform(post("/post").with(user(member))
                .content("{\n" +
                        "   \"tag\" : \"test\",\n" +
                        "   \"title\" : \"test\",\n" +
                        "   \"content\" : \"test\",\n" +
                        "   \"uploads\" : [\"http://localhost/upload/1\"]\n" +
                        "}").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andDo(document("post/post",
                        httpRequest(),
                        requestFields(
                                fieldWithPath("tag").description("tag"),
                                fieldWithPath("title").description("title"),
                                fieldWithPath("content").description("content"),
                                fieldWithPath("uploads").description("file list").optional()
                        ),
                        responseFields(
                                fieldWithPath("tag").description("tag"),
                                fieldWithPath("title").description("title"),
                                fieldWithPath("content").description("content"),
                                fieldWithPath("views").description("views"),
                                fieldWithPath("createDate").description("created date"),
                                fieldWithPath("modifiedDate").description("modified date"),
                                subsectionWithPath("_links").ignored()
                        ),
                        responseFieldsCustom("response-links", null,
                                subsectionWithPath("_links.self").description("link to self"),
                                subsectionWithPath("_links.post").ignored().description("link to post"),
                                subsectionWithPath("_links.writer").description("link to writer"),
                                subsectionWithPath("_links.uploads").description("link to file list"),
                                subsectionWithPath("_links.replies").description("link to message list"),
                                subsectionWithPath("*").ignored()
                        )
                ));
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    void create_reply() throws Exception {
        mockMvc.perform(post("/reply").with(user(member))
                .content("{\n" +
                        "   \"message\" : \"test\",\n" +
                        "   \"post\" : \"http://localhost/post/1\",\n" +
                        "   \"topic\" : null\n" +
                        "}").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/reply").with(user(member))
                .content("{\n" +
                        "    \"message\" : \"test\",\n" +
                        "    \"post\" : \"http://localhost/post/1\",\n" +
                        "    \"topic\" : \"http://localhost/message/1\"\n" +
                        "}").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andDo(document("post/reply",
                        httpRequest(),
                        requestFields(
                                fieldWithPath("message").description("message text"),
                                fieldWithPath("post").description("post"),
                                fieldWithPath("topic").description("topic message")
                        ),
                        responseFields(
                                fieldWithPath("message").description("message text"),
                                fieldWithPath("createDate").description("create date"),
                                fieldWithPath("modifiedDate").description("modified date"),
                                subsectionWithPath("_links").ignored()
                        ),
                        responseFieldsCustom("response-links", null,
                                subsectionWithPath("_links.self").description("link to self"),
                                subsectionWithPath("_links.reply").ignored().description("link to reply"),
                                subsectionWithPath("_links.topic").description("link to topic"),
                                subsectionWithPath("_links.writer").description("link to writer"),
                                subsectionWithPath("_links.post").description("link to post"),
                                subsectionWithPath("*").ignored()
                        )
                ));
    }

    @Test
    @Order(4)
    @Rollback(value = false)
    void read_post_list() throws Exception {
        mockMvc.perform(get("/post")
        )
                .andExpect(status().isOk())
                .andDo(document("get/post",
                        httpRequest()
                ));

        mockMvc.perform(get("/post?page=0&size=10&sort=email,asc&sort=id,desc")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andDo(document("get/post",
                        httpRequest(),
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

    @Test
    @Order(5)
    @Rollback(value = false)
    void read_post() throws Exception {
        mockMvc.perform(get("/post/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andDo(document("get/post/id",
                        httpRequest(),
                        responseFields(
                                fieldWithPath("tag").description("tag"),
                                fieldWithPath("title").description("title"),
                                fieldWithPath("content").description("content"),
                                fieldWithPath("views").description("views"),
                                fieldWithPath("createDate").description("created date"),
                                fieldWithPath("modifiedDate").description("modified date"),
                                subsectionWithPath("_links").ignored()
                        ),
                        responseFieldsCustom("response-links", null,
                                subsectionWithPath("_links.self").description("link to self"),
                                subsectionWithPath("_links.post").ignored().description("link to post"),
                                subsectionWithPath("_links.writer").description("link to writer"),
                                subsectionWithPath("_links.uploads").description("link to file list"),
                                subsectionWithPath("_links.replies").description("link to message list"),
                                subsectionWithPath("*").ignored()
                        )
                ));
    }

    @Test
    @Order(6)
    @Rollback(value = false)
    void read_replies() throws Exception {
        mockMvc.perform(get("/post/{id}/replies/{idx}", 1, 1)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andDo(document("get/post/id/replies",
                        httpRequest(),
                        responseFields(
                                fieldWithPath("message").description("reply text"),
                                fieldWithPath("createDate").description("create date"),
                                fieldWithPath("modifiedDate").description("modified date"),
                                subsectionWithPath("_links").ignored()
                        ),
                        responseFieldsCustom("response-links", null,
                                subsectionWithPath("_links.self").description("link to self"),
                                subsectionWithPath("_links.reply").ignored().description("link to reply"),
                                subsectionWithPath("_links.topic").description("link to topic"),
                                subsectionWithPath("_links.writer").description("link to writer"),
                                subsectionWithPath("_links.post").description("link to post"),
                                subsectionWithPath("*").ignored()
                        )
                ));
    }

    @Test
    @Order(7)
    @Rollback(value = false)
    void update_post() throws Exception {
        mockMvc.perform(put("/post/{id}", 1).with(user(member))
                .content("{\n" +
                        "  \"tag\": \"title\",\n" +
                        "  \"title\": \"title\",\n" +
                        "  \"content\": \"content\",\n" +
                        "   \"uploads\" : [\"http://localhost/upload/1\"]\n" +
                        "}").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andDo(document("put/post/id",
                        httpRequest(),
                        requestFields(
                                fieldWithPath("tag").description("tag"),
                                fieldWithPath("title").description("title"),
                                fieldWithPath("content").description("content"),
                                fieldWithPath("uploads").description("file list").optional()
                        )
                ));
    }

    @Test
    @Order(8)
    @Rollback(value = false)
    void update_reply() throws Exception {
        mockMvc.perform(put("/reply/{id}", 1).with(user(member))
                .content("{\"message\": \"reply\"}").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andDo(document("put/reply/id",
                        httpRequest(),
                        requestFields(
                                fieldWithPath("message").description("message")
                        )
                ));
    }

    @Test
    @Order(997)
    void delete_reply() throws Exception {
        mockMvc.perform(delete("/reply/{id}", 1).with(user(member))
        ).andExpect(status().isNoContent())
                .andDo(document("delete/reply/id",
                        httpRequest()
                ));
    }

    @Test
    @Order(998)
    void delete_upload() throws Exception {
        mockMvc.perform(delete("/upload/{id}", 1).with(user(member))
        ).andExpect(status().isNoContent())
                .andDo(document("delete/upload/id",
                        httpRequest()
                ));
    }

    @Test
    @Order(999)
    @Rollback(value = false)
    void delete_post() throws Exception {
        mockMvc.perform(delete("/post/{id}", 1).with(user(member))
        ).andExpect(status().isNoContent())
                .andDo(document("/delete/post/id",
                        httpRequest()
                ));
    }
}
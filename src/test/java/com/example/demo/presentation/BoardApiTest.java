package com.example.demo.presentation;

import com.example.demo.config.security.OAuthServerProvider;
import com.example.demo.domain.board.message.MessageRepository;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
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
    private final PostRepository postRepository;
    private final UploadRepository uploadRepository;
    private Member member;
    private final MessageRepository messageRepository;
    private MockMvc mockMvc;

    @Autowired
    BoardApiTest(WebApplicationContext webApplicationContext,
                 MemberRepository memberRepository, PostRepository postRepository,
                 UploadRepository uploadRepository, MessageRepository messageRepository) {
        this.webApplicationContext = webApplicationContext;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.uploadRepository = uploadRepository;
        this.messageRepository = messageRepository;
    }

    @BeforeAll
    public void setUp() {
        member = Member.builder()
                .email("test@test.com")
                .provider(OAuthServerProvider.GOOGLE)
                .build();
        memberRepository.save(member);
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
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("post/upload",
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
                        "   \"contents\" : \"test\",\n" +
                        "   \"uploads\" : [\"http://localhost/upload/1\"]\n" +
                        "}").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("post/post",
                        requestFields(
                                fieldWithPath("tag").description("tag"),
                                fieldWithPath("title").description("title"),
                                fieldWithPath("contents").description("contents"),
                                fieldWithPath("uploads").description("file list")
                        ),
                        responseFields(
                                fieldWithPath("tag").description("tag"),
                                fieldWithPath("title").description("title"),
                                fieldWithPath("contents").description("contents"),
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
                                subsectionWithPath("_links.messages").description("link to message list"),
                                subsectionWithPath("*").ignored()
                        )
                ));
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    void create_message() throws Exception {
        mockMvc.perform(post("/message").with(user(member))
                .content("{\n" +
                        "   \"message\" : \"test\",\n" +
                        "   \"post\" : \"http://localhost/post/1\",\n" +
                        "   \"topic\" : null\n" +
                        "}").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/message").with(user(member))
                .content("{\n" +
                        "    \"message\" : \"test\",\n" +
                        "    \"writer\" : \"http://localhost/member/1\",\n" +
                        "    \"post\" : \"http://localhost/post/1\",\n" +
                        "    \"topic\" : \"http://localhost/message/1\"\n" +
                        "}").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("post/reply",
                        requestFields(
                                fieldWithPath("message").description("message text"),
                                fieldWithPath("writer").description("writer"),
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
                                subsectionWithPath("_links.message").ignored().description("link to message"),
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

    @Test
    @Order(5)
    @Rollback(value = false)
    void read_post() throws Exception {
        mockMvc.perform(get("/post/{id}", 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get/board/id",
                        responseFields(
                                fieldWithPath("tag").description("tag"),
                                fieldWithPath("title").description("title"),
                                fieldWithPath("contents").description("contents"),
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
                                subsectionWithPath("_links.messages").description("link to message list")
                        )
                ));
    }

    @Test
    @Order(6)
    @Rollback(value = false)
    void read_message() throws Exception {
        mockMvc.perform(get("/post/{id}", 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get/board/id",
                        responseFields(
                                fieldWithPath("message").description("message text"),
                                fieldWithPath("createDate").description("create date"),
                                fieldWithPath("modifiedDate").description("modified date"),
                                subsectionWithPath("_links").ignored()
                        ),
                        responseFieldsCustom("response-links", null,
                                subsectionWithPath("_links.self").description("link to self"),
                                subsectionWithPath("_links.message").ignored().description("link to message"),
                                subsectionWithPath("_links.topic").description("link to topic"),
                                subsectionWithPath("_links.writer").description("link to writer"),
                                subsectionWithPath("_links.post").description("link to post"),
                                subsectionWithPath("*").ignored()
                        )
                ));
    }
}
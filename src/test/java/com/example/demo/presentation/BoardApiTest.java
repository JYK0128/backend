package com.example.demo.presentation;

import com.example.demo.config.security.OAuthServerProvider;
import com.example.demo.domain.member.member.Member;
import com.example.demo.domain.board.message.MessageRepository;
import com.example.demo.domain.board.post.PostRepository;
import com.example.demo.domain.board.upload.UploadRepository;
import com.example.demo.domain.member.member.MemberRepository;
import com.example.demo.utils.SnippetUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;
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
public class BoardApiTest {
    private final WebApplicationContext webApplicationContext;

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final UploadRepository uploadRepository;
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
        Member member = Member.builder()
                .email("test@test.com")
                .provider(OAuthServerProvider.GOOGLE)
                .build();
        memberRepository.save(member);
    }

    @BeforeEach
    public void setMockMvc(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
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
                            SnippetUtils.responseFieldsCustom("response-links", null,
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
            mockMvc.perform(post("/post")
                    .content("{\n" +
                    "   \"id\" : null,\n" +
                    "   \"writer\" : \"http://localhost/member/1\",\n" +
                    "   \"tag\" : \"test\",\n" +
                    "   \"title\" : \"test\",\n" +
                    "   \"contents\" : \"test\",\n" +
                    "   \"uploads\" : [\"http://localhost/upload/1\"]\n" +
                    "}").contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andDo(document("post/post",
                            requestFields(
                                    fieldWithPath("id").optional().description("insert id when update"),
                                    fieldWithPath("writer").description("writer"),
                                    fieldWithPath("tag").description("tag"),
                                    fieldWithPath("title").description("title"),
                                    fieldWithPath("contents").description("contents"),
                                    fieldWithPath("uploads").description("file list")
                            ),
                            responseFields(
                                    fieldWithPath("tag").description("tag"),
                                    fieldWithPath("title").description("title"),
                                    fieldWithPath("contents").description("contents"),
                                    fieldWithPath("updated").description("updated date"),
                                    fieldWithPath("views").description("views"),
                                    subsectionWithPath("_links").ignored()
                            ),
                            SnippetUtils.responseFieldsCustom("response-links", null,
                                    fieldWithPath("_links.self").description("link to self"),
                                    fieldWithPath("_links.post").ignored().description("link to post"),
                                    fieldWithPath("_links.writer").description("link to writer"),
                                    fieldWithPath("_links.uploads").description("link to file list"),
                                    fieldWithPath("_links.messages").description("link to message list"),
                                    subsectionWithPath("*").ignored()
                            )
                    ));
        }

        @Test
        @Order(3)
        @Rollback(value = false)
        void create_topic() throws Exception {
            mockMvc.perform(post("/message")
                    .content("{\n" +
                    "   \"message\" : \"test\",\n" +
                    "   \"writer\" : \"http://localhost/member/1\",\n" +
                    "   \"post\" : \"http://localhost/post/1\",\n" +
                    "   \"topic\" : null\n" +
                    "}").contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
        }

        @Test
        @Order(4)
        @Rollback(value = false)
        void create_reply() throws Exception {
            mockMvc.perform(post("/message").content("{\n" +
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
                            )
                    ));

            assertThat(messageRepository.findById(1L).get().getWriter().getId()).isEqualTo(1L);
            assertThat(messageRepository.findById(1L).get().getPost().getId()).isEqualTo(1L);
            assertThat(messageRepository.findById(1L).get().getMessage()).isEqualTo("test");
//            assertThat(messageRepository.findById(1L).get().getTopic().getId()).isEqualTo(1L);

//            assertThat(memberRepository.findById(1L).get().getMessages().get(1).getId()).isEqualTo(2L);
        }

    @Nested
    class Read {
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

        void read_upload_list() throws Exception {
            //paging
        }
    }

    @Nested
    class Update {
        void update_upload_list() throws Exception {

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
    }

    @Nested
    class Delete{
        private void delete_post() throws Exception {
/*        mockMvc.perform(RestDocumentationRequestBuilders.delete("/post/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("delete/post/id",
                        httpRequest()
                ));*/
        }

        private void delete_upload() {
        }

        private void delete_message() {
        }
    }
}
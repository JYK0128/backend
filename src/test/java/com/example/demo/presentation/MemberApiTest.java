package com.example.demo.presentation;

import com.example.demo.config.security.OAuthProvider;
import com.example.demo.domain.member.member.Member;
import com.example.demo.domain.member.member.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.example.demo.utils.SnippetUtils.responseFieldsCustom;
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({RestDocumentationExtension.class})
public class MemberApiTest {
    private final MemberRepository memberRepository;
    private final ClientRegistrationRepository registrationRepository;
    private Member member;
    private MockMvc mockMvc;

    @Autowired
    MemberApiTest(MemberRepository memberRepository,
                  ClientRegistrationRepository registrationRepository) {
        this.memberRepository = memberRepository;
        this.registrationRepository = registrationRepository;
    }

    @BeforeAll
    public void setUp() {
        member = Member.builder()
                .email("test@test.com")
                .provider(OAuthProvider.GOOGLE)
                .build();
        memberRepository.save(member);
    }

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .addFilters(new OAuth2AuthorizationRequestRedirectFilter(registrationRepository))
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

    @ParameterizedTest
    @EnumSource(OAuthProvider.class)
    void create_user(OAuthProvider provider) throws Exception {
        // signup and login
        mockMvc.perform(get("/oauth2/authorization/" + provider.name().toLowerCase()))
                .andExpect(status().is3xxRedirection())
                .andDo(document("get/login",
                        httpRequest()
                ));
    }

    @Test
    void read_user() throws Exception {
        mockMvc.perform(get("/member").with(user(member)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get/member",
                        httpRequest(),
                        responseFields(
                                fieldWithPath("email").description("user email from auth providers"),
                                fieldWithPath("provider").description("provider name"),
                                fieldWithPath("nickname").description("nickname"),
                                subsectionWithPath("_links").ignored()
                        ),
                        responseFieldsCustom("response-links", null,
                                subsectionWithPath("_links.posts").description("list of posts"),
                                subsectionWithPath("_links.messages").description("list of messages"),
                                subsectionWithPath("*").ignored()
                        )
                ));
    }

    @Test
    void update_user() throws Exception {
        mockMvc.perform(put("/member").with(user(member))
                .content("{\"nickname\" : \"test\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("put/member",
                        httpRequest(),
                        requestFields(
                                fieldWithPath("nickname").description("nickname")
                        )
                ));
    }

    //TODO: implement revoke endpoint
    @Test
    void delete_user() throws Exception {
        //https://nid.naver.com/oauth2.0/token?grant_type=delete&client_id=CLIENT_ID&client_secret=CLIENT_SECRET&access_token=ACCESS_TOKEN
    }

    //TODO: implement revoke endpoint
    @Test
    void logout_user() throws Exception {
        // 세션에 로그인 정보를 발행하거나 쿠키로 로그인 정보를 발행하여 로그인 상태로 만들 수 있습니다.
    }
}
package com.sparta.newspeed.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.fixturemonkey.FixtureMonkeyUtil;
import com.sparta.mock.MockSpringSecurityFilter;
import com.sparta.mock.MockUtil;
import com.sparta.newspeed.config.WebSecurityConfig;
import com.sparta.newspeed.domain.comment.dto.CommentRequestDto;
import com.sparta.newspeed.domain.comment.repository.CommentRepository;
import com.sparta.newspeed.domain.comment.service.CommentService;
import com.sparta.newspeed.domain.newsfeed.service.NewsfeedService;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = {CommentController.class},
        // 제외 필터 지정
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        // security config 제외
                        classes = WebSecurityConfig.class
                )
        }
)
class CommentControllerTest {
    private MockMvc mockMvc;
    private Principal principal;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    CommentService commentService;

    @MockBean
    NewsfeedService newsfeedService;

    @MockBean
    CommentRepository commentRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
        principal = MockUtil.makePrincipal();
    }

    @DisplayName("댓글 생성")
    @Test
    void test1() throws Exception {
        // given
        Long newsfeedId = 1L;
        CommentRequestDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(CommentRequestDto.class)
                .set("content", Arbitraries.strings().ofMinLength(1))
                .sample();

        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when - then
        mockMvc.perform(post("/api/newsfeeds/" + newsfeedId + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .principal(principal)
        ).andExpect(status().is2xxSuccessful());
    }

    @DisplayName("댓글 전체 조회")
    @Test
    void test2() throws Exception {
        // given
        Long newsfeedId = 1L;

        // when - then
        mockMvc.perform(get("/api/newsfeeds/" + newsfeedId + "/comments")
        ).andExpect(status().is2xxSuccessful());
    }

    @DisplayName("댓글 수정")
    @Test
    void test3() throws Exception {
        // given
        Long newsfeedId = 1L;
        Long commentId = 1L;

        CommentRequestDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(CommentRequestDto.class)
                .set("content", Arbitraries.strings().ofMinLength(1))
                .sample();

        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when - then
        mockMvc.perform(put("/api/newsfeeds/" + newsfeedId + "/comments/" + commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .principal(principal)
        ).andExpect(status().is2xxSuccessful());
    }

    @DisplayName("댓글 삭제")
    @Test
    void test4() throws Exception {
        // given
        Long newsfeedId = 1L;
        Long commentId = 1L;

        // when - then
        mockMvc.perform(delete("/api/newsfeeds/" + newsfeedId + "/comments/" + commentId)
                .principal(principal)
        ).andExpect(status().is2xxSuccessful());
    }
}
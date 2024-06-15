package com.sparta.newspeed.domain.newsfeed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.fixturemonkey.FixtureMonkeyUtil;
import com.sparta.mock.MockSpringSecurityFilter;
import com.sparta.mock.MockUtil;
import com.sparta.newspeed.config.WebSecurityConfig;
import com.sparta.newspeed.domain.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newspeed.domain.newsfeed.repository.NewsfeedRespository;
import com.sparta.newspeed.domain.newsfeed.repository.OttRepository;
import com.sparta.newspeed.domain.newsfeed.service.NewsfeedService;
import net.jqwik.api.Arbitraries;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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
        controllers = {NewsfeedController.class},
        // 제외 필터 지정
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        // security config 제외
                        classes = WebSecurityConfig.class
                )
        }
)
class NewsfeedControllerTest {
    private MockMvc mockMvc;
    private Principal principal;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    NewsfeedService newsfeedService;

    @MockBean
    NewsfeedRespository newsfeedRespository;

    @MockBean
    OttRepository ottRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
        principal = MockUtil.makePrincipal();
    }

    @Order(1)
    @DisplayName("뉴스피드 게시물 생성")
    @Test
    void test1() throws Exception {
        // given
        NewsfeedRequestDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(NewsfeedRequestDto.class)
                .set("title", Arbitraries.strings().ofMinLength(1))
                .set("content", Arbitraries.strings().ofMinLength(1))
                .set("ottName", Arbitraries.strings().ofMinLength(1))
                .set("remainMember", Arbitraries.integers().between(1, 4))
                .sample();

        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when - then
        mockMvc.perform(post("/api/newsfeeds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .principal(principal)
        ).andExpect(status().is2xxSuccessful());
    }

    @Order(2)
    @DisplayName("뉴스피드 게시물 전체 조회")
    @Test
    void test2() throws Exception {
        // given
        int page = 1;
        String sortBy = "createAt";
        LocalDate startDate = new LocalDateTime().toLocalDate();
        LocalDate endDate = new LocalDateTime().toLocalDate().plusDays(1);

        // when - then
        mockMvc.perform(get("/api/newsfeeds")
                        .param("page", Integer.toString(page))
                        .param("sortBy", sortBy)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                ).andExpect(status().is2xxSuccessful());
    }

    @Order(3)
    @DisplayName("뉴스피드 게시물 수정")
    @Test
    void test3() throws Exception {
        // given
        Long newsfeedSeq = 1L;

        NewsfeedRequestDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(NewsfeedRequestDto.class)
                .set("title", Arbitraries.strings().ofMinLength(1))
                .set("content", Arbitraries.strings().ofMinLength(1))
                .set("ottName", Arbitraries.strings().ofMinLength(1))
                .set("remainMember", Arbitraries.integers().between(1, 4))
                .sample();

        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when - then
        mockMvc.perform(put("/api/newsfeeds/" + newsfeedSeq)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .principal(principal)
        ).andExpect(status().is2xxSuccessful());
    }

    @Order(4)
    @DisplayName("뉴스피드 게시물 선택조회")
    @Test
    void test5() throws Exception {
        // given
        Long newsfeedSeq = 1L;

        // when - then
        mockMvc.perform(get("/api/newsfeeds/" + newsfeedSeq)
        ).andExpect(status().is2xxSuccessful());
    }

    @Order(5)
    @DisplayName("뉴스피드 게시물 삭제")
    @Test
    void test4() throws Exception {
        // given
        Long newsfeedSeq = 1L;

        // when - then
        mockMvc.perform(delete("/api/newsfeeds/" + newsfeedSeq)
                .principal(principal)
        ).andExpect(status().is2xxSuccessful());
    }
}

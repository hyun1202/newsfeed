package com.sparta.newspeed.domain.newsfeed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.fixturemonkey.FixtureMonkeyUtil;
import com.sparta.mock.MockSpringSecurityFilter;
import com.sparta.mock.MockUtil;
import com.sparta.newspeed.config.WebSecurityConfig;
import com.sparta.newspeed.domain.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newspeed.domain.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.newspeed.domain.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.domain.newsfeed.entity.Ott;
import com.sparta.newspeed.domain.newsfeed.repository.NewsfeedRespository;
import com.sparta.newspeed.domain.newsfeed.repository.OttRepository;
import com.sparta.newspeed.domain.newsfeed.service.NewsfeedService;
import com.sparta.newspeed.domain.user.entity.User;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    private User user;

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
        user = MockUtil.getPrincipalUserDetails(principal).getUser();
    }

    @Order(1)
    @DisplayName("뉴스피드 게시물 생성")
    @Test
    void test1() throws Exception {
        // given
        String title = FixtureMonkeyUtil.getRandomStringArbitrary(5, 20).sample();
        String content = FixtureMonkeyUtil.getRandomStringArbitrary(5, 20).sample();
        String ottName = FixtureMonkeyUtil.getRandomStringArbitrary(5, 5).sample();
        int remainMember = Arbitraries.integers().between(1, 4).sample();

        NewsfeedRequestDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(NewsfeedRequestDto.class)
                .set("title", title)
                .set("content", content)
                .set("ottName", ottName)
                .set("remainMember", remainMember)
                .sample();

        Long newsFeedSeq = Arbitraries.longs().between(1L, 50L).sample();

        NewsfeedResponseDto responseDto = new NewsfeedResponseDto(
                Newsfeed.builder()
                        .newsFeedSeq(newsFeedSeq)
                        .title(title)
                        .content(content)
                        .ott(new Ott(ottName, 1, 4))
                        .user(user)
                        .remainMember(remainMember)
                        .build()
        );

        given(newsfeedService.createNewsFeed(any(), any())).willReturn(responseDto);

        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when - then
        mockMvc.perform(post("/api/newsfeeds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .principal(principal))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.newsFeedSeq").value(newsFeedSeq))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.ottName").value(ottName))
                .andExpect(jsonPath("$.remainMember").value(remainMember))
        ;
    }

    @Order(2)
    @DisplayName("뉴스피드 게시물 전체 조회")
    @Test
    void test2() throws Exception {
        // given
        int page = 1;
        String sortBy = "createAt";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDateTime.now().toLocalDate().plusDays(1);

        List<Newsfeed> newsfeeds = FixtureMonkeyUtil.Entity.toNewsfeeds(5);

        List<NewsfeedResponseDto> listNewsfeedDto = newsfeeds.stream().map(NewsfeedResponseDto::new).toList();

        given(newsfeedService.getNewsfeeds(page, sortBy, startDate, endDate))
                .willReturn(listNewsfeedDto);

        // when - then
        mockMvc.perform(get("/api/newsfeeds")
                        .param("page", Integer.toString(page))
                        .param("sortBy", sortBy)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                ).andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$..['newsFeedSeq']").exists())
                .andExpect(jsonPath("$..['title']").exists())
                .andExpect(jsonPath("$..['content']").exists())
                .andExpect(jsonPath("$..['remainMember']").exists())
                .andExpect(jsonPath("$..['userName']").exists())
                .andExpect(jsonPath("$..['ottName']").exists())
                .andExpect(jsonPath("$[0].title").value(listNewsfeedDto.get(0).getTitle()))
                .andExpect(jsonPath("$[1].content").value(listNewsfeedDto.get(1).getContent()))
                .andExpect(jsonPath("$[2].remainMember").value(listNewsfeedDto.get(2).getRemainMember()))
                .andExpect(jsonPath("$[2].userName").value(listNewsfeedDto.get(2).getUserName()))
                .andExpect(jsonPath("$[2].ottName").value(listNewsfeedDto.get(2).getOttName()))
                ;
    }

    @Order(3)
    @DisplayName("뉴스피드 게시물 수정")
    @Test
    void test3() throws Exception {
        // given
        Long newsfeedSeq = 1L;
        String title = FixtureMonkeyUtil.getRandomStringArbitrary(5, 20).sample();
        String content = FixtureMonkeyUtil.getRandomStringArbitrary(5, 20).sample();
        String ottName = FixtureMonkeyUtil.getRandomStringArbitrary(5, 5).sample();
        int remainMember = Arbitraries.integers().between(1, 4).sample();

        NewsfeedRequestDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(NewsfeedRequestDto.class)
                .set("title", title)
                .set("content", content)
                .set("ottName", ottName)
                .set("remainMember", remainMember)
                .sample();

        NewsfeedResponseDto responseDto = new NewsfeedResponseDto(
                Newsfeed.builder()
                        .newsFeedSeq(newsfeedSeq)
                        .title(title)
                        .content(content)
                        .ott(new Ott(ottName, 1, 4))
                        .user(user)
                        .remainMember(remainMember)
                        .build()
        );

        // 컨트롤러에 @RequestBody가 있으면 any()를 사용해야함
        // 내부 값은 같으나 실제로 컨트롤러가 실행이 될 때는 다른 객체로(주소값 상이) 판단됨
        given(newsfeedService.updateNewsFeed(any(), any(), any())).willReturn(responseDto);

        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when - then
        mockMvc.perform(put("/api/newsfeeds/" + newsfeedSeq)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .principal(principal))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(requestDto.getTitle()))
        ;
    }

    @Order(4)
    @DisplayName("뉴스피드 게시물 선택조회")
    @Test
    void test5() throws Exception {
        // given
        Long newsfeedSeq = 1L;

        NewsfeedResponseDto responseDto = new NewsfeedResponseDto(FixtureMonkeyUtil.Entity.toNewsfeed(newsfeedSeq));

        given(newsfeedService.getNewsfeed(any())).willReturn(responseDto);

        // when - then
        mockMvc.perform(get("/api/newsfeeds/" + newsfeedSeq))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.newsFeedSeq").value(newsfeedSeq))
                .andExpect(jsonPath("$.title").value(responseDto.getTitle()))
                .andExpect(jsonPath("$.content").value(responseDto.getContent()))
        ;
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


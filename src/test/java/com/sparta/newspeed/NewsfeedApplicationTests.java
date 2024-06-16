package com.sparta.newspeed;

import com.sparta.fixturemonkey.FixtureMonkeyUtil;
import com.sparta.newspeed.domain.comment.entity.Comment;
import com.sparta.newspeed.domain.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.domain.newsfeed.entity.Ott;
import com.sparta.newspeed.domain.newsfeed.repository.OttRepository;
import com.sparta.newspeed.domain.user.entity.User;
import com.sparta.newspeed.domain.user.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // @Order 기능 활성화
@ExtendWith(MockitoExtension.class) // @Mock 사용을 위해 설정합니다.
@ActiveProfiles("test")
@SpringBootTest
public class NewsfeedApplicationTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OttRepository ottRepository;

    private List<User> users;

    protected List<User> userDataInit() {
        List<User> users = FixtureMonkeyUtil.Entity.toUsers(5);
        this.users = userRepository.saveAll(users);
        return this.users;
    }

    protected List<Newsfeed> getNewsfeedDataInit() {
        return getNewsfeedDataInit(5);
    }

    protected List<Newsfeed> getNewsfeedDataInit(int count) {
        List<Ott> otts = ottRepository.findAll();
        userDataInit();
        return FixtureMonkeyUtil.Entity.toNewsfeeds(count, this.users, otts);
    }

    protected List<Comment> getCommentDataInit(int count, List<Newsfeed> newsfeeds) {
        return FixtureMonkeyUtil.Entity.toComments(count, this.users, newsfeeds);
    }

    public User getUser() {
        return users.get(0);
    }

    public User getUser(int i) {
        return users.get(i);
    }
}

package com.sparta.fixturemonkey;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FailoverIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.sparta.newspeed.domain.comment.entity.Comment;
import com.sparta.newspeed.domain.likes.entity.Like;
import com.sparta.newspeed.domain.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.domain.newsfeed.entity.Ott;
import com.sparta.newspeed.domain.user.entity.User;
import com.sparta.newspeed.domain.user.entity.UserRoleEnum;
import net.jqwik.api.Arbitraries;
import net.jqwik.web.api.EmailArbitrary;
import net.jqwik.web.api.Web;

import java.util.Arrays;
import java.util.List;

public class FixtureMonkeyUtil {

    public static FixtureMonkey monkey() {
        return FixtureMonkey.builder()
                .objectIntrospector(new FailoverIntrospector((
                        Arrays.asList(
                                FieldReflectionArbitraryIntrospector.INSTANCE,
                                ConstructorPropertiesArbitraryIntrospector.INSTANCE,
                                BuilderArbitraryIntrospector.INSTANCE
                        )
                    )
                ))
                .build();
    }

    public static class Entity {
        public static User toUser() {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(User.class)
                    .set("userSeq", Arbitraries.longs().between(1L, 50L))
                    .sample();
        }

        public static List<User> toUsers(int count) {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(User.class)
                    .set("userSeq", Arbitraries.longs().between(1L, 50L))
                    .set("userId", Arbitraries.strings()
                            .alpha()
                            .numeric()
                            .ofMinLength(5)
                    )
                    .set("userPassword", Arbitraries.strings()
                            .alpha()
                            .numeric()
                            .withChars('!', '@', '#', '$', '~')
                            .ofMinLength(10)
                    )
                    .set("userEmail", "test1@test.com")
                    .set("role", UserRoleEnum.USER)
                    .sampleList(count);
        }

        public static Newsfeed toNewsfeed() {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(Newsfeed.class)
                    .set("newsFeedSeq", Arbitraries.longs().between(1L, 50L))
                    .sample();
        }

        public static Comment toComment() {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(Comment.class)
                    .set("commentSeq", Arbitraries.longs().between(1L, 50L))
                    .sample();
        }

        public static Like toLike() {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(Like.class)
                    .set("likeSeq", Arbitraries.longs().between(1L, 50L))
                    .sample();
        }
    }
}

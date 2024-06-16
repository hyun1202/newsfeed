package com.sparta.fixturemonkey;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
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
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.arbitraries.StringArbitrary;
import net.jqwik.web.api.EmailArbitrary;
import net.jqwik.web.api.Web;

import java.time.DateTimeException;
import java.time.LocalDate;
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
                    .set("userName", getRandomStringArbitrary(20))
                    .sample();
        }

        public static List<User> toUsers(int count) {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(User.class)
                    .set("userSeq", Arbitraries.longs().between(1L, 50L))
                    .set("userId", getRandomStringArbitrary(5)
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
            return getNewsfeedArbitraryBuilder()
                    .sample();
        }

        public static Newsfeed toNewsfeed(Long newsFeedSeq) {
            return getNewsfeedArbitraryBuilder(newsFeedSeq,
                    toUser(),
                    new Ott(Arbitraries.of("Netflix", "Disney+", "watcha", "wavve", "tiving").sample(), 17000, 4))
                    .sample();
        }

        public static List<Newsfeed> toNewsfeeds(int count) {
            return getNewsfeedArbitraryBuilder()
                    .sampleList(count);
        }

        public static List<Newsfeed> toNewsfeeds(int count, List<User> users, List<Ott> otts) {
            return getNewsfeedArbitraryBuilder(Arbitraries.longs().between(1L, 50L).sample(), Arbitraries.of(users), Arbitraries.of(otts))
                    .sampleList(count);
        }

        public static Comment toComment() {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(Comment.class)
                    .set("commentSeq", Arbitraries.longs().between(1L, 50L))
                    .sample();
        }

        public static Comment toComment(Long commentSeq, Long newsFeedSeq, User user, String content) {
            return getCommentArbitraryBuilder(commentSeq, newsFeedSeq, user, content)
                    .sample();
        }

        public static List<Comment> toComments(int count, User user) {
            return getCommentArbitraryBuilder(Arbitraries.longs().between(1L, 1000L).sample(), user)
                    .sampleList(count);
        }

        public static List<Comment> toComments(int count, List<User> users, List<Newsfeed> newsfeeds) {
            return getCommentArbitraryBuilder(Arbitraries.longs().between(1L, 1000L).sample(), Arbitraries.of(users), Arbitraries.of(newsfeeds))
                    .sampleList(count);
        }

        public static Comment toComment(Long commentSeq, Long newsFeedSeq, User user) {
            return getCommentArbitraryBuilder(commentSeq, user, toNewsfeed(newsFeedSeq))
                    .sample();
        }

        public static Like toLike() {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(Like.class)
                    .set("likeSeq", Arbitraries.longs().between(1L, 50L))
                    .sample();
        }

        private static ArbitraryBuilder<Newsfeed> getNewsfeedArbitraryBuilder() {
            return getNewsfeedArbitraryBuilder(Arbitraries.longs().between(1L, 50L).sample(),
                    toUser(),
                    new Ott(Arbitraries.of("Netflix", "Disney+", "watcha", "wavve", "tiving").sample(), 17000, 4));
        }

        private static ArbitraryBuilder<Newsfeed> getNewsfeedArbitraryBuilder(Long newsFeedSeq, Object user, Object otts) {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(Newsfeed.class)
                    .set("newsFeedSeq", newsFeedSeq)
                    .set("title", getRandomStringArbitrary(5, 20))
                    .set("content", getRandomStringArbitrary(5, 100))
                    .set("userName", getRandomStringArbitrary(5, 20))
                    .set("ott", otts)
                    .set("user", user)
                    .set("remainMember", Arbitraries.integers().between(1, 4))
                    .set("like", Arbitraries.longs().between(1L, 500L));
        }

        private static ArbitraryBuilder<Comment> getCommentArbitraryBuilder(Long newsFeedSeq, User user) {
            return getCommentArbitraryBuilder(Arbitraries.longs().between(1L, 50L).sample(), user, toNewsfeed(newsFeedSeq));
        }

        private static ArbitraryBuilder<Comment> getCommentArbitraryBuilder(Long commentSeq, Object user, Object newsfeeds) {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(Comment.class)
                    .set("commentSeq", commentSeq)
                    .set("content", getRandomStringArbitrary(5, 100))
                    .set("like", Arbitraries.longs().between(1L, 500L))
                    .set("user", user)
                    .set("newsfeed", newsfeeds);
        }

        private static ArbitraryBuilder<Comment> getCommentArbitraryBuilder(Long commentSeq, Long newsFeedSeq, User user, String content) {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(Comment.class)
                    .set("commentSeq", commentSeq)
                    .set("content", content)
                    .set("like", Arbitraries.longs().between(1L, 500L))
                    .set("user", user)
                    .set("newsfeed", toNewsfeed(newsFeedSeq));
        }
    }

    public static StringArbitrary getRandomStringArbitrary(int max) {
        return  getRandomStringArbitrary(1, max);
    }

    public static StringArbitrary getRandomStringArbitrary(int min, int max) {
        return  Arbitraries.strings()
                .alpha()
                .numeric()
                .ofMinLength(min)
                .ofMaxLength(max);
    }

    public static Arbitrary<LocalDate> datesBetween(int startYear, int endYear) {
        Arbitrary<Integer> years = Arbitraries.integers().between(startYear, endYear);
        Arbitrary<Integer> months = Arbitraries.integers().between(1, 12);
        Arbitrary<Integer> days = Arbitraries.integers().between(1, 31);

        return Combinators.combine(years, months, days)
                .as(LocalDate::of)
                .ignoreException(DateTimeException.class);
    }
}

package com.sparta.newspeed.domain.likes.entity;

public enum LikeEnum {
    COMMENT(ContentType.COMMENT),
    NEWSFEED(ContentType.NEWSFEED);

    private final String contentType;
    LikeEnum(String contentType) {this.contentType = contentType; }

    public static class ContentType{
        public static final String COMMENT = "COMMENT";
        public static final String NEWSFEED = "NEWSFEED";
    }
}

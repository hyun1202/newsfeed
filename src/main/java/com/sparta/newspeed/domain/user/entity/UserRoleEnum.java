package com.sparta.newspeed.domain.user.entity;

public enum UserRoleEnum {
    USER(Authority.USER),  // 사용자 권한
    ADMIN(Authority.ADMIN),  // 관리자 권한
    WITHDRAW(Authority.WITHDRAW), // 휴면 계정
    UNCHECKED(Authority.UNCHECKED), //이메일 인증 전
    SOCIAL(Authority.SOCIAL); // 소셜 계정

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String WITHDRAW  = "ROLE_WITHDRAW";
        public static final String UNCHECKED = "ROLE_UNCHECKED";
        public static final String SOCIAL = "ROLE_SOCIAL";
    }
}

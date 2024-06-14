package com.sparta.newspeed.domain.user.repository;

import com.sparta.newspeed.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
   // Optional<User> findByUserIdAndRole(String userId, UserRoleEnum role);
    Optional<User> findByRefreshToken(String refreshToken);
    Optional<User> findByUserEmail(String email);
}

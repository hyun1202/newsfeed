package com.sparta.newspeed.security.service;

import com.sparta.newspeed.domain.user.entity.User;
import com.sparta.newspeed.domain.user.entity.UserRoleEnum;
import com.sparta.newspeed.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId).orElseThrow(() ->
                new UsernameNotFoundException("Not Found " + userId));

        // 이미 탈퇴 처리된 회원일 경우
        if (user.getRole() == UserRoleEnum.WITHDRAW) {
            throw new UsernameNotFoundException("User has withdrawn " + userId);
        }

        return new UserDetailsImpl(user);
    }
}
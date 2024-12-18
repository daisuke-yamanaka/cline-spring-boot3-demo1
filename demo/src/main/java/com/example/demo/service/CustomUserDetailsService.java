package com.example.demo.service;

import com.example.demo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("ユーザー認証を試行: {}", username);

        User user = userService.findByUsername(username);
        if (user == null) {
            logger.warn("ユーザーが見つかりません: {}", username);
            throw new UsernameNotFoundException("ユーザーが見つかりません: " + username);
        }

        logger.debug("ユーザーが見つかりました: {}", username);
        logger.debug("ユーザーの役割: {}", user.getRole());
        logger.debug("アカウントのブロック状態: {}", user.getIsBlocked());

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        logger.debug("付与された権限: {}", authority.getAuthority());

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            !user.getIsBlocked(),  // アカウントが有効か
            true,                  // アカウントが期限切れでないか
            true,                  // クレデンシャルが期限切れでないか
            true,                  // アカウントがロックされていないか
            Collections.singleton(authority)
        );
    }
}

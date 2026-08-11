package com.fitnesscenter.auth.security;

import com.fitnesscenter.user.entity.User;
import com.fitnesscenter.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {

        User user = userRepository
                .findByEmailIgnoreCase(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        )
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(
                        user.getRoles()
                                .stream()
                                .map(role ->
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + role.getName()
                                        )
                                )
                                .toList()
                )
                .disabled(!user.isEnabled())
                .accountLocked(!user.isAccountNonLocked())
                .build();
    }
}
package com.fitnesscenter.auth.service;

import com.fitnesscenter.auth.dto.*;
import com.fitnesscenter.auth.entity.RefreshToken;
import com.fitnesscenter.auth.security.JwtProperties;
import com.fitnesscenter.auth.security.JwtService;
import com.fitnesscenter.common.exception.ConflictException;
import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.user.dto.UserResponse;
import com.fitnesscenter.user.entity.Role;
import com.fitnesscenter.user.entity.User;
import com.fitnesscenter.user.mapper.UserMapper;
import com.fitnesscenter.user.repository.RoleRepository;
import com.fitnesscenter.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl
        implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    private final UserMapper userMapper;

    private final JwtProperties jwtProperties;

    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthResponse register(
            RegisterRequest request
    ) {

        String email =
                normalizeEmail(request.email());

        if (
                userRepository
                        .existsByEmailIgnoreCase(email)
        ) {

            throw new ConflictException(
                    "A user with this email already exists"
            );
        }

        Role memberRole =
                roleRepository
                        .findByNameIgnoreCase("MEMBER")
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "MEMBER role was not found"
                                )
                        );

        User user = new User();

        user.setFirstName(
                request.firstName().trim()
        );

        user.setLastName(
                request.lastName().trim()
        );

        user.setEmail(email);

        user.setPhone(
                request.phone() == null
                        ? null
                        : request.phone().trim()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.password()
                )
        );

        user.setEnabled(true);

        user.setAccountNonLocked(true);

        user.getRoles().add(memberRole);

        User savedUser =
                userRepository.save(user);

        String accessToken =
                jwtService.generateAccessToken(
                        savedUser
                );

        RefreshTokenResult refreshToken =
                refreshTokenService.createToken(
                        savedUser
                );

        return buildAuthResponse(
                savedUser,
                accessToken,
                refreshToken.rawToken()
        );
    }


    @Override
    @Transactional
    public AuthResponse login(
            LoginRequest request
    ) {

        String email =
                normalizeEmail(request.email());

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.password()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        User user =
                userRepository
                        .findByEmailIgnoreCase(
                                userDetails.getUsername()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        String accessToken =
                jwtService.generateAccessToken(
                        user
                );

        RefreshTokenResult refreshToken =
                refreshTokenService.createToken(
                        user
                );

        return buildAuthResponse(
                user,
                accessToken,
                refreshToken.rawToken()
        );
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(
            RefreshTokenRequest request
    ) {

        RefreshToken storedToken =
                refreshTokenService.validateToken(
                        request.refreshToken()
                );

        User user =
                storedToken.getUser();

        /*
         * Token rotation:
         * revoke the old token before creating a new one.
         */
        refreshTokenService.revokeToken(
                request.refreshToken()
        );

        String accessToken =
                jwtService.generateAccessToken(
                        user
                );

        RefreshTokenResult newRefreshToken =
                refreshTokenService.createToken(
                        user
                );

        return buildAuthResponse(
                user,
                accessToken,
                newRefreshToken.rawToken()
        );
    }

    private AuthResponse buildAuthResponse(
            User user,
            String accessToken,
            String refreshToken
    ) {

        UserResponse userResponse =
                userMapper.toResponse(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtProperties.accessTokenExpiration(),
                userResponse
        );
    }

    private String normalizeEmail(
            String email
    ) {

        return email
                .trim()
                .toLowerCase();
    }


    @Override
    @Transactional
    public void logout(
            LogoutRequest request
    ) {

        refreshTokenService.revokeToken(
                request.refreshToken()
        );
    }
}
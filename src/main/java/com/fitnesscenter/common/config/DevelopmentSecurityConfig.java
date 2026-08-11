//package com.fitnesscenter.common.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
//
//
////this class was created to permit all url when the actual security configuration have not been configured in the folder
//@Configuration
//@Profile("dev")
//public class DevelopmentSecurityConfig {
//
//    @Bean
//    public SecurityFilterChain developmentSecurityFilterChain(
//            HttpSecurity http
//    ) throws Exception {
//
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//
//                .authorizeHttpRequests(auth -> auth
//
//                        .requestMatchers(
//                                "/api/v1/health",
//                                "/actuator/health",
//                                "/swagger-ui/**",
//                                "/swagger-ui.html",
//                                "/v3/api-docs/**"
//                        )
//                        .permitAll()
//
//                        .anyRequest()
//                        .permitAll()
//                );
//
//        return http.build();
//    }
//}
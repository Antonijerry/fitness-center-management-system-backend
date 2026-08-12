//package com.fitnesscenter.auth.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter
//        extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//
//    private final CustomUserDetailsService userDetailsService;
//
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    )
//            throws ServletException, IOException {
//
//        String authorizationHeader =
//                request.getHeader("Authorization");
//
//        if (
//                authorizationHeader == null
//                        || !authorizationHeader.startsWith("Bearer ")
//        ) {
//
//            filterChain.doFilter(
//                    request,
//                    response
//            );
//
//            return;
//        }
//
//        String token =
//                authorizationHeader.substring(7);
//
//        try {
//
//            String username =
//                    jwtService.extractUsername(token);
//
//            if (
//                    username != null
//                            && SecurityContextHolder
//                            .getContext()
//                            .getAuthentication() == null
//            ) {
//
//                UserDetails userDetails =
//                        userDetailsService
//                                .loadUserByUsername(username);
//
//                if (
//                        jwtService.isTokenValid(
//                                token,
//                                userDetails.getUsername()
//                        )
//                ) {
//
//                    UsernamePasswordAuthenticationToken authentication =
//                            new UsernamePasswordAuthenticationToken(
//                                    userDetails,
//                                    null,
//                                    userDetails.getAuthorities()
//                            );
//
//                    authentication.setDetails(
//                            new WebAuthenticationDetailsSource()
//                                    .buildDetails(request)
//                    );
//
//                    SecurityContextHolder
//                            .getContext()
//                            .setAuthentication(authentication);
//                }
//            }
//
//        } catch (Exception ignored) {
//
//            /*
//             * Invalid JWT.
//             *
//             * We don't authenticate the request.
//             * Spring Security will subsequently reject
//             * protected endpoints.
//             */
//        }
//
//        filterChain.doFilter(
//                request,
//                response
//        );
//    }
//}













package com.fitnesscenter.auth.security;

        import jakarta.servlet.FilterChain;
        import jakarta.servlet.ServletException;
        import jakarta.servlet.http.HttpServletRequest;
        import jakarta.servlet.http.HttpServletResponse;
        import lombok.RequiredArgsConstructor;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.security.core.userdetails.UserDetails;
        import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
        import org.springframework.stereotype.Component;
        import org.springframework.web.filter.OncePerRequestFilter;

        import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        if (
                authorizationHeader == null
                        || !authorizationHeader.startsWith("Bearer ")
        ) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String token =
                authorizationHeader.substring(7);

        try {

            String username =
                    jwtService.extractUsername(token);

            if (
                    username != null
                            && SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null
            ) {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(username);

                if (
                        jwtService.isTokenValid(
                                token,
                                userDetails.getUsername()
                        )
                ) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (Exception exception) {

            log.error(
                    "JWT authentication failed for request {} {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    exception
            );
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}
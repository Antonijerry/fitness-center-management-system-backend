package com.fitnesscenter.common.config;

import com.fitnesscenter.user.entity.Role;
import com.fitnesscenter.user.entity.User;
import com.fitnesscenter.user.repository.RoleRepository;
import com.fitnesscenter.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:admin@fitnesscenter.com}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:ChangeMe123!}")
    private String adminPassword;

    @Bean
    CommandLineRunner initializeAdmin() {
        return args -> {

            String email = adminEmail.trim().toLowerCase();

            System.out.println(
                    "Checking initial ADMIN account: " + email
            );

            if (userRepository.existsByEmailIgnoreCase(email)) {
                System.out.println(
                        "Initial ADMIN account already exists: " + email
                );
                return;
            }

            System.out.println(
                    "Initial ADMIN account does not exist. Creating..."
            );

            Role adminRole = roleRepository
                    .findByNameIgnoreCase("ADMIN")
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "ADMIN role was not found. " +
                                            "Create the ADMIN role before starting the application."
                            )
                    );

            User admin = new User();

            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail(email);

            admin.setPassword(
                    passwordEncoder.encode(adminPassword)
            );

            admin.setEnabled(true);
            admin.setAccountNonLocked(true);

            admin.getRoles().add(adminRole);

            userRepository.save(admin);

            System.out.println(
                    "Initial ADMIN account created successfully: " + email
            );
        };
    }
}
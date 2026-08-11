package com.fitnesscenter.user.service;

import com.fitnesscenter.common.exception.ConflictException;
import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.user.dto.AssignRoleRequest;
import com.fitnesscenter.user.dto.CreateUserRequest;
import com.fitnesscenter.user.dto.UpdateUserRequest;
import com.fitnesscenter.user.dto.UpdateUserStatusRequest;
import com.fitnesscenter.user.dto.UserResponse;
import com.fitnesscenter.user.entity.Role;
import com.fitnesscenter.user.entity.User;
import com.fitnesscenter.user.mapper.UserMapper;
import com.fitnesscenter.user.repository.RoleRepository;
import com.fitnesscenter.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public UserResponse createUser(
            CreateUserRequest request
    ) {

        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException(
                    "A user with this email already exists"
            );
        }

        User user = new User();

        user.setFirstName(request.firstName().trim());

        user.setLastName(request.lastName().trim());

        user.setEmail(email);

        user.setPhone(
                request.phone() == null
                        ? null
                        : request.phone().trim()
        );

        /*
         * IMPORTANT:
         * Password hashing will be moved to the authentication
         * security implementation in the next phase.
         *
         * For the moment we store the supplied value so the
         * user module can be tested. Do NOT expose it through
         * UserResponse.
         */
        user.setPassword(
                passwordEncoder.encode(
                        request.password()
                )
        );

        user.setEnabled(true);

        user.setAccountNonLocked(true);

        Role memberRole = roleRepository
                .findByNameIgnoreCase("MEMBER")
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Default MEMBER role was not found"
                        )
                );

        user.getRoles().add(memberRole);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }


    @Override
    public Page<UserResponse> getUsers(
            String search,
            Pageable pageable
    ) {

        Page<User> users;

        if (search == null || search.isBlank()) {

            users = userRepository.findAll(pageable);

        } else {

            String normalizedSearch = search.trim();

            users =
                    userRepository
                            .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                                    normalizedSearch,
                                    normalizedSearch,
                                    pageable
                            );
        }

        return users.map(userMapper::toResponse);
    }


    @Override
    public UserResponse getUserById(Long id) {

        User user = findUser(id);

        return userMapper.toResponse(user);
    }


    @Override
    @Transactional
    public UserResponse updateUser(
            Long id,
            UpdateUserRequest request
    ) {

        User user = findUser(id);

        if (request.firstName() != null) {

            user.setFirstName(
                    request.firstName().trim()
            );
        }

        if (request.lastName() != null) {

            user.setLastName(
                    request.lastName().trim()
            );
        }

        if (request.email() != null) {

            String newEmail =
                    normalizeEmail(request.email());

            if (!newEmail.equalsIgnoreCase(user.getEmail())
                    && userRepository.existsByEmailIgnoreCase(newEmail)) {

                throw new ConflictException(
                        "A user with this email already exists"
                );
            }

            user.setEmail(newEmail);
        }

        if (request.phone() != null) {

            user.setPhone(
                    request.phone().trim()
            );
        }

        return userMapper.toResponse(user);
    }


    @Override
    @Transactional
    public UserResponse updateUserStatus(
            Long id,
            UpdateUserStatusRequest request
    ) {

        User user = findUser(id);

        user.setEnabled(
                request.enabled()
        );

        return userMapper.toResponse(user);
    }


    @Override
    @Transactional
    public UserResponse assignRole(
            Long id,
            AssignRoleRequest request
    ) {

        User user = findUser(id);

        Role role = findRole(request.roleName());

        user.getRoles().add(role);

        return userMapper.toResponse(user);
    }


    @Override
    @Transactional
    public UserResponse removeRole(
            Long id,
            AssignRoleRequest request
    ) {

        User user = findUser(id);

        Role role = findRole(request.roleName());

        if (user.getRoles().size() <= 1) {

            throw new ConflictException(
                    "A user must have at least one role"
            );
        }

        user.getRoles().remove(role);

        return userMapper.toResponse(user);
    }


    @Override
    @Transactional
    public void deleteUser(Long id) {

        User user = findUser(id);

        userRepository.delete(user);
    }


    private User findUser(Long id) {

        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );
    }


    private Role findRole(String roleName) {

        return roleRepository
                .findByNameIgnoreCase(
                        roleName.trim()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found: " + roleName
                        )
                );
    }


    private String normalizeEmail(String email) {

        return email
                .trim()
                .toLowerCase();
    }
}
package com.fitnesscenter.user.service;

import com.fitnesscenter.common.exception.AccessDeniedException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;


    /*
     * ============================================================
     * ROLE HIERARCHY
     * ============================================================
     *
     * MEMBER       = 1
     * RECEPTIONIST = 2
     * TRAINER      = 3
     * MANAGER      = 4
     * ADMIN        = 5
     *
     * A higher-level user can manage lower-level users.
     */
    private static final int MEMBER_LEVEL = 1;
    private static final int RECEPTIONIST_LEVEL = 2;
    private static final int TRAINER_LEVEL = 3;
    private static final int MANAGER_LEVEL = 4;
    private static final int ADMIN_LEVEL = 5;


    @Override
    @Transactional
    public UserResponse createUser(
            CreateUserRequest request
    ) {

        requireUserManagementPermission();

        String email =
                normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(email)) {

            throw new ConflictException(
                    "A user with this email already exists"
            );
        }

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

        /*
         * Every newly created user receives MEMBER
         * as the default role.
         */
        Role memberRole =
                roleRepository
                        .findByNameIgnoreCase("MEMBER")
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Default MEMBER role was not found"
                                )
                        );

        user.getRoles().add(memberRole);

        User savedUser =
                userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }


    @Override
    public Page<UserResponse> getUsers(
            String search,
            Pageable pageable
    ) {

        requireUserManagementPermission();

        Page<User> users;

        if (
                search == null ||
                        search.isBlank()
        ) {

            users =
                    userRepository.findAll(
                            pageable
                    );

        } else {

            String normalizedSearch =
                    search.trim();

            users =
                    userRepository
                            .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                                    normalizedSearch,
                                    normalizedSearch,
                                    pageable
                            );
        }

        return users.map(
                userMapper::toResponse
        );
    }


    @Override
    public UserResponse getUserById(
            Long id
    ) {

        requireUserManagementPermission();

        User user =
                findUser(id);

        return userMapper.toResponse(user);
    }


    @Override
    @Transactional
    public UserResponse updateUser(
            Long id,
            UpdateUserRequest request
    ) {

        User currentUser =
                getCurrentUser();

        User user =
                findUser(id);

        requireCanManageTarget(
                currentUser,
                user
        );

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
                    normalizeEmail(
                            request.email()
                    );

            if (
                    !newEmail.equalsIgnoreCase(
                            user.getEmail()
                    )
                            &&
                            userRepository
                                    .existsByEmailIgnoreCase(
                                            newEmail
                                    )
            ) {

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

        User currentUser =
                getCurrentUser();

        User user =
                findUser(id);

        requireCanManageTarget(
                currentUser,
                user
        );

        /*
         * Prevent an administrator or manager from
         * accidentally disabling their own account.
         */
        if (
                currentUser.getId().equals(
                        user.getId()
                )
        ) {

            throw new AccessDeniedException(
                    "You cannot change your own account status"
            );
        }

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

        User currentUser =
                getCurrentUser();

        User targetUser =
                findUser(id);

        Role role =
                findRole(
                        request.roleName()
                );

        /*
         * Verify that the current user can manage
         * the target user.
         */
        requireCanManageTarget(
                currentUser,
                targetUser
        );

        /*
         * Verify that the current user can assign
         * the requested role.
         */
        requireCanAssignRole(
                currentUser,
                role
        );

        /*
         * Prevent duplicate assignments.
         */
        boolean alreadyHasRole =
                targetUser.getRoles()
                        .stream()
                        .anyMatch(
                                existingRole ->
                                        existingRole
                                                .getName()
                                                .equalsIgnoreCase(
                                                        role.getName()
                                                )
                        );

        if (alreadyHasRole) {

            throw new ConflictException(
                    "User already has the "
                            + role.getName()
                            + " role"
            );
        }

        /*
         * Prevent a user from changing their own
         * privilege level.
         */
        if (
                currentUser.getId().equals(
                        targetUser.getId()
                )
        ) {

            throw new AccessDeniedException(
                    "You cannot assign roles to your own account"
            );
        }

        targetUser
                .getRoles()
                .add(role);

        return userMapper.toResponse(
                targetUser
        );
    }


    @Override
    @Transactional
    public UserResponse removeRole(
            Long id,
            AssignRoleRequest request
    ) {

        User currentUser =
                getCurrentUser();

        User targetUser =
                findUser(id);

        Role role =
                findRole(
                        request.roleName()
                );

        /*
         * Verify target-user hierarchy.
         */
        requireCanManageTarget(
                currentUser,
                targetUser
        );

        /*
         * Verify that the current user can manage
         * the requested role.
         */
        requireCanAssignRole(
                currentUser,
                role
        );

        /*
         * Prevent self role modification.
         */
        if (
                currentUser.getId().equals(
                        targetUser.getId()
                )
        ) {

            throw new AccessDeniedException(
                    "You cannot remove roles from your own account"
            );
        }

        /*
         * Verify that the target actually has
         * this role.
         */
        boolean hasRole =
                targetUser.getRoles()
                        .stream()
                        .anyMatch(
                                existingRole ->
                                        existingRole
                                                .getName()
                                                .equalsIgnoreCase(
                                                        role.getName()
                                                )
                        );

        if (!hasRole) {

            throw new ConflictException(
                    "User does not have the "
                            + role.getName()
                            + " role"
            );
        }

        /*
         * Every user must retain at least one role.
         */
        if (
                targetUser.getRoles().size() <= 1
        ) {

            throw new ConflictException(
                    "A user must have at least one role"
            );
        }

        targetUser
                .getRoles()
                .removeIf(
                        existingRole ->
                                existingRole
                                        .getName()
                                        .equalsIgnoreCase(
                                                role.getName()
                                        )
                );

        return userMapper.toResponse(
                targetUser
        );
    }


    @Override
    @Transactional
    public void deleteUser(
            Long id
    ) {

        User currentUser =
                getCurrentUser();

        User targetUser =
                findUser(id);

        requireCanManageTarget(
                currentUser,
                targetUser
        );

        /*
         * Never allow an administrator/manager to
         * delete their own account from the user
         * management endpoint.
         */
        if (
                currentUser.getId().equals(
                        targetUser.getId()
                )
        ) {

            throw new AccessDeniedException(
                    "You cannot delete your own account"
            );
        }

        userRepository.delete(
                targetUser
        );
    }


    /*
     * ============================================================
     * AUTHENTICATION
     * ============================================================
     */

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                authentication == null ||
                        !authentication.isAuthenticated()
        ) {

            throw new AccessDeniedException(
                    "Authentication is required"
            );
        }

        String email =
                authentication.getName();

        return userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user was not found"
                        )
                );
    }


    /*
     * ============================================================
     * GENERAL USER MANAGEMENT PERMISSION
     * ============================================================
     */

    private void requireUserManagementPermission() {

        User currentUser =
                getCurrentUser();

        if (
                hasRole(
                        currentUser,
                        "ADMIN"
                )
                        ||
                        hasRole(
                                currentUser,
                                "MANAGER"
                        )
        ) {

            return;
        }

        throw new AccessDeniedException(
                "You do not have permission to manage users"
        );
    }


    /*
     * ============================================================
     * TARGET USER PERMISSION
     * ============================================================
     */

    private void requireCanManageTarget(
            User currentUser,
            User targetUser
    ) {

        if (
                hasRole(
                        currentUser,
                        "ADMIN"
                )
        ) {

            return;
        }

        if (
                hasRole(
                        currentUser,
                        "MANAGER"
                )
        ) {

            /*
             * Manager cannot manage themselves.
             */
            if (
                    currentUser.getId().equals(
                            targetUser.getId()
                    )
            ) {

                throw new AccessDeniedException(
                        "You cannot manage your own account"
                );
            }

            /*
             * Manager may only manage users whose
             * highest role is below MANAGER.
             */
            boolean targetIsBelowManager =
                    targetUser.getRoles()
                            .stream()
                            .allMatch(
                                    role ->
                                            getRoleLevel(
                                                    role.getName()
                                            ) < MANAGER_LEVEL
                            );

            if (targetIsBelowManager) {
                return;
            }
        }

        throw new AccessDeniedException(
                "You do not have permission to manage this user"
        );
    }


    /*
     * ============================================================
     * ROLE ASSIGNMENT PERMISSION
     * ============================================================
     */

    private void requireCanAssignRole(
            User currentUser,
            Role role
    ) {

        String roleName =
                normalizeRoleName(
                        role.getName()
                );

        /*
         * ADMIN can assign any role.
         */
        if (
                hasRole(
                        currentUser,
                        "ADMIN"
                )
        ) {

            return;
        }

        /*
         * MANAGER can only assign:
         *
         * TRAINER
         * RECEPTIONIST
         * MEMBER
         */
        if (
                hasRole(
                        currentUser,
                        "MANAGER"
                )
        ) {

            if (
                    roleName.equals("TRAINER")
                            ||
                            roleName.equals("RECEPTIONIST")
                            ||
                            roleName.equals("MEMBER")
            ) {

                return;
            }
        }

        throw new AccessDeniedException(
                "You do not have permission to assign the "
                        + roleName
                        + " role"
        );
    }


    /*
     * ============================================================
     * ROLE HELPERS
     * ============================================================
     */

    private boolean hasRole(
            User user,
            String roleName
    ) {

        return user.getRoles()
                .stream()
                .anyMatch(
                        role ->
                                normalizeRoleName(
                                        role.getName()
                                )
                                        .equals(
                                                normalizeRoleName(
                                                        roleName
                                                )
                                        )
                );
    }


    private int getRoleLevel(
            String roleName
    ) {

        return switch (
                normalizeRoleName(roleName)
                ) {

            case "MEMBER" ->
                    MEMBER_LEVEL;

            case "RECEPTIONIST" ->
                    RECEPTIONIST_LEVEL;

            case "TRAINER" ->
                    TRAINER_LEVEL;

            case "MANAGER" ->
                    MANAGER_LEVEL;

            case "ADMIN" ->
                    ADMIN_LEVEL;

            default ->
                    0;
        };
    }


    private Role findRole(
            String roleName
    ) {

        if (
                roleName == null ||
                        roleName.isBlank()
        ) {

            throw new ResourceNotFoundException(
                    "Role name is required"
            );
        }

        return roleRepository
                .findByNameIgnoreCase(
                        roleName.trim()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found: "
                                        + roleName
                        )
                );
    }


    private User findUser(
            Long id
    ) {

        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: "
                                        + id
                        )
                );
    }


    private String normalizeRoleName(
            String roleName
    ) {

        return roleName
                .trim()
                .toUpperCase(Locale.ROOT);
    }


    private String normalizeEmail(
            String email
    ) {

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
package com.fitnesscenter.user.service;

import com.fitnesscenter.user.dto.AssignRoleRequest;
import com.fitnesscenter.user.dto.CreateUserRequest;
import com.fitnesscenter.user.dto.UpdateUserRequest;
import com.fitnesscenter.user.dto.UpdateUserStatusRequest;
import com.fitnesscenter.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    Page<UserResponse> getUsers(
            String search,
            Pageable pageable
    );

    UserResponse getUserById(Long id);

    UserResponse updateUser(
            Long id,
            UpdateUserRequest request
    );

    UserResponse updateUserStatus(
            Long id,
            UpdateUserStatusRequest request
    );

    UserResponse assignRole(
            Long id,
            AssignRoleRequest request
    );

    UserResponse removeRole(
            Long id,
            AssignRoleRequest request
    );

    void deleteUser(Long id);
}
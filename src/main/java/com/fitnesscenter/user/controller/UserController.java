package com.fitnesscenter.user.controller;

import com.fitnesscenter.common.response.ApiResponse;
import com.fitnesscenter.user.dto.AssignRoleRequest;
import com.fitnesscenter.user.dto.CreateUserRequest;
import com.fitnesscenter.user.dto.UpdateUserRequest;
import com.fitnesscenter.user.dto.UpdateUserStatusRequest;
import com.fitnesscenter.user.dto.UserResponse;
import com.fitnesscenter.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {

        UserResponse response =
                userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "User created successfully",
                                response
                        )
                );
    }


    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(

            @RequestParam(required = false)
            String search,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String direction
    ) {

        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(sortDirection, sortBy)
                );

        Page<UserResponse> response =
                userService.getUsers(
                        search,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Users retrieved successfully",
                        response
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @PathVariable Long id
    ) {

        UserResponse response =
                userService.getUserById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User retrieved successfully",
                        response
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,

            @Valid
            @RequestBody
            UpdateUserRequest request
    ) {

        UserResponse response =
                userService.updateUser(
                        id,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User updated successfully",
                        response
                )
        );
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable Long id,

            @Valid
            @RequestBody
            UpdateUserStatusRequest request
    ) {

        UserResponse response =
                userService.updateUserStatus(
                        id,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User status updated successfully",
                        response
                )
        );
    }


    @PostMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<UserResponse>> assignRole(
            @PathVariable Long id,

            @Valid
            @RequestBody
            AssignRoleRequest request
    ) {

        UserResponse response =
                userService.assignRole(
                        id,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Role assigned successfully",
                        response
                )
        );
    }


    @DeleteMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<UserResponse>> removeRole(
            @PathVariable Long id,

            @Valid
            @RequestBody
            AssignRoleRequest request
    ) {

        UserResponse response =
                userService.removeRole(
                        id,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Role removed successfully",
                        response
                )
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id
    ) {

        userService.deleteUser(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User deleted successfully"
                )
        );
    }
}
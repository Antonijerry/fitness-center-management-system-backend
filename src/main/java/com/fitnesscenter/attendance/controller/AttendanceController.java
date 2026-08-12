package com.fitnesscenter.attendance.controller;

import com.fitnesscenter.attendance.dto.AttendanceResponse;
import com.fitnesscenter.attendance.dto.CheckInRequest;
import com.fitnesscenter.attendance.service.AttendanceService;
import com.fitnesscenter.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;


    @PostMapping("/check-in")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<AttendanceResponse>>
    checkIn(
            @Valid @RequestBody
            CheckInRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Member checked in successfully",
                                attendanceService.checkIn(
                                        request
                                )
                        )
                );
    }


    @PostMapping("/check-out/{memberId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<AttendanceResponse>>
    checkOut(
            @PathVariable Long memberId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Member checked out successfully",
                        attendanceService.checkOut(
                                memberId
                        )
                )
        );
    }


    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<AttendanceResponse>>
    getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance retrieved successfully",
                        attendanceService.getById(id)
                )
        );
    }


    @GetMapping("/member/{memberId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>>
    getByMember(
            @PathVariable Long memberId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Member attendance retrieved successfully",
                        attendanceService.getByMember(
                                memberId
                        )
                )
        );
    }


    @GetMapping("/member/{memberId}/date/{date}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>>
    getMemberAttendanceByDate(
            @PathVariable Long memberId,

            @PathVariable
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate date
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Member attendance retrieved successfully",
                        attendanceService
                                .getMemberAttendanceByDate(
                                        memberId,
                                        date
                                )
                )
        );
    }


    @GetMapping("/date/{date}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>>
    getByDate(
            @PathVariable
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate date
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance retrieved successfully",
                        attendanceService.getByDate(
                                date
                        )
                )
        );
    }


    @GetMapping("/today")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>>
    getToday() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Today's attendance retrieved successfully",
                        attendanceService.getToday()
                )
        );
    }


    @GetMapping("/currently-checked-in")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>>
    getCurrentlyCheckedIn() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Currently checked-in members retrieved successfully",
                        attendanceService
                                .getCurrentlyCheckedIn()
                )
        );
    }
}
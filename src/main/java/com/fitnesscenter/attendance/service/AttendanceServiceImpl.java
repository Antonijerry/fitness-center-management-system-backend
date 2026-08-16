package com.fitnesscenter.attendance.service;

import com.fitnesscenter.access.dto.AccessValidationResponse;
import com.fitnesscenter.access.service.AccessControlService;
import com.fitnesscenter.attendance.dto.AttendanceResponse;
import com.fitnesscenter.attendance.dto.CheckInRequest;
import com.fitnesscenter.attendance.entity.Attendance;
import com.fitnesscenter.attendance.entity.AttendanceMethod;
import com.fitnesscenter.attendance.entity.AttendanceStatus;
import com.fitnesscenter.attendance.mapper.AttendanceMapper;
import com.fitnesscenter.attendance.repository.AttendanceRepository;
import com.fitnesscenter.common.exception.AccessDeniedException;
import com.fitnesscenter.common.exception.ConflictException;
import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.member.entity.MemberProfile;
import com.fitnesscenter.member.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceServiceImpl
        implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    private final MemberProfileRepository memberProfileRepository;

    private final AttendanceMapper attendanceMapper;

    private final AccessControlService accessControlService;


    /**
     * Check a member into the fitness center.
     *
     * Access validation is delegated to the Access Control module.
     */
    @Override
    @Transactional
    public AttendanceResponse checkIn(
            CheckInRequest request
    ) {

        Long memberId = request.memberId();

        /*
         * 1. Validate member access.
         *
         * AccessControlService is responsible for:
         *
         * - Member existence
         * - Member active status
         * - Membership existence
         * - Membership status
         * - Membership start date
         * - Membership expiry
         * - Current membership validity
         */
        AccessValidationResponse access =
                accessControlService.validateAccess(
                        memberId
                );

        if (!access.isAllowed()) {

            throw new AccessDeniedException(
                    access.message()
            );
        }


        /*
         * 2. Find the member profile.
         */
        MemberProfile member =
                memberProfileRepository.findById(memberId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Member not found with id: "
                                                + memberId
                                )
                        );


        /*
         * 3. Prevent duplicate active check-ins.
         */
        boolean alreadyCheckedIn =
                attendanceRepository
                        .existsByMemberIdAndStatus(
                                memberId,
                                AttendanceStatus.CHECKED_IN
                        );

        if (alreadyCheckedIn) {

            throw new ConflictException(
                    "Member already has an active attendance"
            );
        }


        /*
         * 4. Create attendance record.
         */
        Attendance attendance =
                Attendance.builder()
                        .member(member)
                        .attendanceDate(LocalDate.now())
                        .checkInTime(LocalDateTime.now())
                        .status(AttendanceStatus.CHECKED_IN)
                        .method(
                                request.method() != null
                                        ? request.method()
                                        : AttendanceMethod.MANUAL
                        )
                        .notes(request.notes())
                        .build();


        /*
         * 5. Save attendance.
         */
        Attendance saved =
                attendanceRepository.save(
                        attendance
                );


        /*
         * 6. Convert entity to response DTO.
         */
        return attendanceMapper.toResponse(
                saved
        );
    }


    /**
     * Check a member out of the fitness center.
     */
    @Override
    @Transactional
    public AttendanceResponse checkOut(
            Long memberId
    ) {

        /*
         * Find the member's latest active attendance.
         */
        Attendance attendance =
                attendanceRepository
                        .findFirstByMemberIdAndStatusOrderByCheckInTimeDesc(
                                memberId,
                                AttendanceStatus.CHECKED_IN
                        )
                        .orElseThrow(() ->
                                new ConflictException(
                                        "Member is not currently checked in"
                                )
                        );


        /*
         * Set checkout time.
         */
        attendance.setCheckOutTime(
                LocalDateTime.now()
        );


        /*
         * Change attendance status.
         */
        attendance.setStatus(
                AttendanceStatus.CHECKED_OUT
        );


        /*
         * Because the entity is managed inside the
         * transaction, an explicit save is not strictly
         * required. Saving explicitly keeps the operation
         * clear and consistent.
         */
        Attendance saved =
                attendanceRepository.save(
                        attendance
                );


        return attendanceMapper.toResponse(
                saved
        );
    }


    /**
     * Get an attendance record by ID.
     */
    @Override
    public AttendanceResponse getById(
            Long id
    ) {

        return attendanceMapper.toResponse(
                getAttendance(id)
        );
    }


    /**
     * Get all attendance records for a member.
     */
    @Override
    public List<AttendanceResponse> getByMember(
            Long memberId
    ) {

        return attendanceRepository
                .findAllByMemberIdOrderByCheckInTimeDesc(
                        memberId
                )
                .stream()
                .map(attendanceMapper::toResponse)
                .toList();
    }


    /**
     * Get all attendance records for a specific date.
     */
    @Override
    public List<AttendanceResponse> getByDate(
            LocalDate date
    ) {

        return attendanceRepository
                .findAllByAttendanceDateOrderByCheckInTimeDesc(
                        date
                )
                .stream()
                .map(attendanceMapper::toResponse)
                .toList();
    }


    /**
     * Get today's attendance records.
     */
    @Override
    public List<AttendanceResponse> getToday() {

        return getByDate(
                LocalDate.now()
        );
    }


    /**
     * Get all members who are currently checked in.
     */
    @Override
    public List<AttendanceResponse> getCurrentlyCheckedIn() {

        return attendanceRepository
                .findAllByStatusOrderByCheckInTimeDesc(
                        AttendanceStatus.CHECKED_IN
                )
                .stream()
                .map(attendanceMapper::toResponse)
                .toList();
    }


    /**
     * Get a member's attendance records for a specific date.
     */
    @Override
    public List<AttendanceResponse>
    getMemberAttendanceByDate(
            Long memberId,
            LocalDate date
    ) {

        return attendanceRepository
                .findAllByMemberIdAndAttendanceDateOrderByCheckInTimeDesc(
                        memberId,
                        date
                )
                .stream()
                .map(attendanceMapper::toResponse)
                .toList();
    }


    /**
     * Find an attendance entity by ID.
     */
    private Attendance getAttendance(
            Long id
    ) {

        return attendanceRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance record not found with id: "
                                        + id
                        )
                );
    }
}
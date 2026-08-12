package com.fitnesscenter.attendance.service;

import com.fitnesscenter.attendance.dto.AttendanceResponse;
import com.fitnesscenter.attendance.dto.CheckInRequest;
import com.fitnesscenter.attendance.entity.Attendance;
import com.fitnesscenter.attendance.entity.AttendanceMethod;
import com.fitnesscenter.attendance.entity.AttendanceStatus;
import com.fitnesscenter.attendance.mapper.AttendanceMapper;
import com.fitnesscenter.attendance.repository.AttendanceRepository;
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

    private final MemberProfileRepository memberRepository;

    private final AttendanceMapper attendanceMapper;


    @Override
    @Transactional
    public AttendanceResponse checkIn(
            CheckInRequest request
    ) {

        MemberProfile member =
                memberRepository.findById(
                        request.memberId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Member not found"
                        )
                );


        /*
         * Prevent multiple open attendance records.
         */
        if (
                attendanceRepository.existsByMemberIdAndStatus(
                        member.getId(),
                        AttendanceStatus.CHECKED_IN
                )
        ) {

            throw new ConflictException(
                    "Member is already checked in"
            );
        }


        /*
         * Membership validation will be connected
         * to the existing membership module.
         *
         * Do not remove this business rule when
         * connecting the repository.
         */
        validateMembership(member);


        Attendance attendance =
                new Attendance();

        attendance.setMember(member);

        attendance.setAttendanceDate(
                LocalDate.now()
        );

        attendance.setCheckInTime(
                LocalDateTime.now()
        );

        attendance.setStatus(
                AttendanceStatus.CHECKED_IN
        );

        attendance.setMethod(
                request.method() != null
                        ? request.method()
                        : AttendanceMethod.MANUAL
        );

        attendance.setNotes(
                request.notes()
        );


        Attendance saved =
                attendanceRepository.save(
                        attendance
                );


        return attendanceMapper.toResponse(
                saved
        );
    }


    @Override
    @Transactional
    public AttendanceResponse checkOut(
            Long memberId
    ) {

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


        attendance.setCheckOutTime(
                LocalDateTime.now()
        );

        attendance.setStatus(
                AttendanceStatus.CHECKED_OUT
        );


        return attendanceMapper.toResponse(
                attendance
        );
    }


    @Override
    public AttendanceResponse getById(
            Long id
    ) {

        return attendanceMapper.toResponse(
                getAttendance(id)
        );
    }


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


    @Override
    public List<AttendanceResponse> getToday() {

        return getByDate(
                LocalDate.now()
        );
    }


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


    private Attendance getAttendance(
            Long id
    ) {

        return attendanceRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance record not found"
                        )
                );
    }


    private void validateMembership(
            MemberProfile member
    ) {

        /*
         * Connect this method to the existing
         * MembershipService / MembershipRepository
         * from the membership module.
         *
         * The required business rule is:
         *
         * member must have an active and currently
         * valid membership.
         */
    }
}
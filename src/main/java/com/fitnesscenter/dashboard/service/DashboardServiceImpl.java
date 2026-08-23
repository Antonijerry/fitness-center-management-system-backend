package com.fitnesscenter.dashboard.service;

import com.fitnesscenter.attendance.repository.AttendanceRepository;
import com.fitnesscenter.dashboard.dto.DashboardStatsResponse;
import com.fitnesscenter.member.entity.MemberStatus;
import com.fitnesscenter.member.repository.MemberProfileRepository;
import com.fitnesscenter.membership.entity.MembershipStatus;
import com.fitnesscenter.membership.repository.MembershipRepository;
import com.fitnesscenter.payment.entity.PaymentStatus;
import com.fitnesscenter.payment.repository.PaymentRepository;
import com.fitnesscenter.trainer.repository.TrainerProfileRepository;
import com.fitnesscenter.training.repository.TrainingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl
        implements DashboardService {

    private static final int EXPIRING_WITHIN_DAYS = 7;

    private final MemberProfileRepository memberProfileRepository;

    private final TrainerProfileRepository trainerProfileRepository;

    private final MembershipRepository membershipRepository;

    private final TrainingSessionRepository trainingSessionRepository;

    private final AttendanceRepository attendanceRepository;

    private final PaymentRepository paymentRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {

        /*
         * Member statistics.
         */
        long totalMembers =
                memberProfileRepository.count();

        long activeMembers =
                memberProfileRepository.countByStatus(
                        MemberStatus.ACTIVE
                );

        /*
         * Trainer statistics.
         */
        long totalTrainers =
                trainerProfileRepository.count();

        /*
         * Membership statistics.
         */
        long totalMemberships =
                membershipRepository.count();

        long activeMemberships =
                membershipRepository.countByStatus(
                        MembershipStatus.ACTIVE
                );

        /*
         * Training session statistics.
         */
        long totalTrainingSessions =
                trainingSessionRepository.count();

        /*
         * Today's attendance.
         */
        LocalDate today =
                LocalDate.now();

        long todayAttendance =
                attendanceRepository.countByAttendanceDate(
                        today
                );

        /*
         * Memberships expiring within the next
         * seven days.
         *
         * Includes today and the following seven days.
         */
        LocalDate expirationEndDate =
                today.plusDays(
                        EXPIRING_WITHIN_DAYS
                );

        long expiringMemberships =
                membershipRepository
                        .countByStatusAndEndDateBetween(
                                MembershipStatus.ACTIVE,
                                today,
                                expirationEndDate
                        );

        /*
         * Successful payment revenue.
         */
        BigDecimal totalRevenue =
                paymentRepository.sumAmountByStatus(
                        PaymentStatus.SUCCESSFUL
                );

        /*
         * Defensive null protection.
         */
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        return new DashboardStatsResponse(
                totalMembers,
                activeMembers,
                totalTrainers,
                totalMemberships,
                activeMemberships,
                totalTrainingSessions,
                todayAttendance,
                expiringMemberships,
                totalRevenue
        );
    }
}
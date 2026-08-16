package com.fitnesscenter.membership.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import com.fitnesscenter.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "memberships",
        indexes = {
                @Index(
                        name = "idx_membership_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_membership_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_membership_start_date",
                        columnList = "start_date"
                ),
                @Index(
                        name = "idx_membership_end_date",
                        columnList = "end_date"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Membership extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_membership_user"
            )
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "membership_plan_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_membership_plan"
            )
    )
    private MembershipPlan plan;

    @Column(
            name = "start_date",
            nullable = false
    )
    private LocalDate startDate;

    @Column(
            name = "end_date",
            nullable = false
    )
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private MembershipStatus status;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal price;

    @Column(
            nullable = false
    )
    private boolean autoRenewable = false;

    @Column(
            length = 500
    )
    private String notes;


    //added during access control
    public boolean isCurrentlyValid() {

        LocalDate today = LocalDate.now();

        return status == MembershipStatus.ACTIVE
                && !today.isBefore(startDate)
                && !today.isAfter(endDate);
    }
}
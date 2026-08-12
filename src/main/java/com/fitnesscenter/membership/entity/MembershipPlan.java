package com.fitnesscenter.membership.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "membership_plans",
        indexes = {
                @Index(
                        name = "idx_membership_plan_name",
                        columnList = "name"
                ),
                @Index(
                        name = "idx_membership_plan_active",
                        columnList = "active"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MembershipPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            unique = true,
            length = 100
    )
    private String name;

    @Column(
            length = 500
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private MembershipType type;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal price;

    @Column(
            nullable = false
    )
    private Integer durationInDays;

    @Column(
            nullable = false
    )
    private Integer maxVisitsPerMonth;

    @Column(
            nullable = false
    )
    private boolean active = true;

    @Column(
            nullable = false
    )
    private boolean autoRenewable = false;
}
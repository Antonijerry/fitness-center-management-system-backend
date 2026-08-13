package com.fitnesscenter.workout.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "workout_program_days",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_program_day",
                        columnNames = {
                                "workout_program_id",
                                "day_number"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class WorkoutProgramDay extends BaseEntity {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "workout_program_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_program_day_program"
            )
    )
    private WorkoutProgram workoutProgram;

    @Column(
            name = "day_number",
            nullable = false
    )
    private Integer dayNumber;

    @Column(
            nullable = false,
            length = 150
    )
    private String name;

    @Column(length = 1000)
    private String notes;
}
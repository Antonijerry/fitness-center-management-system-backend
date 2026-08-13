package com.fitnesscenter.workout.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "exercises",
        indexes = {
                @Index(
                        name = "idx_exercise_name",
                        columnList = "name"
                ),
                @Index(
                        name = "idx_exercise_category",
                        columnList = "category"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Exercise extends BaseEntity {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Column(
            nullable = false,
            unique = true,
            length = 150
    )
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 40
    )
    private ExerciseCategory category;

    @Column(length = 100)
    private String muscleGroup;

    @Column(length = 2000)
    private String instructions;

    @Column(length = 1000)
    private String equipment;

    @Column(
            nullable = false
    )
    private boolean active = true;
}
package com.fitnesscenter.workout.repository;

import com.fitnesscenter.workout.entity.Exercise;
import com.fitnesscenter.workout.entity.ExerciseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository
        extends JpaRepository<Exercise, Long> {

    Optional<Exercise> findByNameIgnoreCase(
            String name
    );

    boolean existsByNameIgnoreCase(
            String name
    );

    List<Exercise> findAllByActiveTrueOrderByNameAsc();

    List<Exercise> findAllByCategoryAndActiveTrueOrderByNameAsc(
            ExerciseCategory category
    );
}
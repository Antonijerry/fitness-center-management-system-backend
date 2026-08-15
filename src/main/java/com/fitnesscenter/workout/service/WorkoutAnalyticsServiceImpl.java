package com.fitnesscenter.workout.service;


import com.fitnesscenter.workout.dto.ExerciseProgressResponse;
import com.fitnesscenter.workout.dto.PersonalRecordResponse;
import com.fitnesscenter.workout.dto.WorkoutProgressSummaryResponse;
import com.fitnesscenter.workout.entity.Exercise;
import com.fitnesscenter.workout.entity.WorkoutExerciseLog;
import com.fitnesscenter.workout.entity.WorkoutSession;
import com.fitnesscenter.workout.entity.WorkoutSetLog;
import com.fitnesscenter.workout.entity.WorkoutSessionStatus;
import com.fitnesscenter.workout.repository.WorkoutExerciseLogRepository;
import com.fitnesscenter.workout.repository.WorkoutSessionRepository;
import com.fitnesscenter.workout.repository.WorkoutSetLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkoutAnalyticsServiceImpl
        implements WorkoutAnalyticsService {

    private final WorkoutSessionRepository sessionRepository;

    private final WorkoutExerciseLogRepository exerciseLogRepository;

    private final WorkoutSetLogRepository setLogRepository;


    @Override
    public WorkoutProgressSummaryResponse getProgressSummary(
            Long memberId,
            LocalDate fromDate,
            LocalDate toDate
    ) {

        validateDates(fromDate, toDate);


        ////
        List<WorkoutSession> sessions =
                getAllSessions(
                        memberId,
                        fromDate,
                        toDate
                );

        List<WorkoutSession> completedSessions =
                sessions.stream()
                        .filter(session ->
                                session.getStatus()
                                        == WorkoutSessionStatus.COMPLETED
                        )
                        .toList();


//        List<WorkoutSession> sessions =
//                getCompletedSessions(
//                        memberId,
//                        fromDate,
//                        toDate
//                );
//


        long totalWorkouts = sessions.size();

        long completedWorkouts =
                sessions.stream()
                        .filter(session ->
                                session.getStatus()
                                        == WorkoutSessionStatus.COMPLETED
                        )
                        .count();

        long cancelledWorkouts =
                sessions.stream()
                        .filter(session ->
                                session.getStatus()
                                        == WorkoutSessionStatus.CANCELLED
                        )
                        .count();

        ///

        List<WorkoutExerciseLog> exerciseLogs =
                getExerciseLogs(completedSessions);


//        List<WorkoutExerciseLog> exerciseLogs =
//                getExerciseLogs(sessions);


        //
        List<WorkoutSetLog> sets =
                getSetLogs(exerciseLogs);

//        List<WorkoutSetLog> sets =
//                getSetLogs(exerciseLogs);

        long completedSets =
                sets.stream()
                        .filter(WorkoutSetLog::isCompleted)
                        .count();

        long totalRepetitions =
                sets.stream()
                        .filter(WorkoutSetLog::isCompleted)
                        .mapToLong(set ->
                                set.getRepetitions() == null
                                        ? 0
                                        : set.getRepetitions()
                        )
                        .sum();

        BigDecimal totalVolume =
                calculateTotalVolume(sets);

        BigDecimal averageWorkoutVolume =
                completedWorkouts == 0
                        ? BigDecimal.ZERO
                        : totalVolume.divide(
                        BigDecimal.valueOf(completedWorkouts),
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal averageWorkoutDuration =
                calculateAverageWorkoutDuration(
                        sessions
                );

        return new WorkoutProgressSummaryResponse(
                memberId,
                fromDate,
                toDate,
                totalWorkouts,
                completedWorkouts,
                cancelledWorkouts,
                exerciseLogs.size(),
                sets.size(),
                completedSets,
                totalRepetitions,
                totalVolume,
                averageWorkoutVolume,
                averageWorkoutDuration
        );
    }



    //add this helper for the workout analytics
    private List<WorkoutSession> getAllSessions(
            Long memberId,
            LocalDate fromDate,
            LocalDate toDate
    ) {

        LocalDateTime from =
                fromDate.atStartOfDay();

        LocalDateTime to =
                toDate.plusDays(1)
                        .atStartOfDay();

        return sessionRepository
                .findAllByMemberIdAndStartedAtGreaterThanEqualAndStartedAtLessThanOrderByStartedAtAsc(
                        memberId,
                        from,
                        to
                );
    }

    @Override
    public List<ExerciseProgressResponse> getExerciseProgress(
            Long memberId,
            LocalDate fromDate,
            LocalDate toDate
    ) {

        validateDates(fromDate, toDate);

        List<WorkoutSession> sessions =
                getCompletedSessions(
                        memberId,
                        fromDate,
                        toDate
                );

        List<WorkoutExerciseLog> exerciseLogs =
                getExerciseLogs(sessions);

        Map<Long, List<WorkoutExerciseLog>> grouped =
                new HashMap<>();

        for (WorkoutExerciseLog log : exerciseLogs) {

            Long exerciseId =
                    log.getExercise().getId();

            grouped
                    .computeIfAbsent(
                            exerciseId,
                            ignored -> new ArrayList<>()
                    )
                    .add(log);
        }

        List<ExerciseProgressResponse> result =
                new ArrayList<>();

        for (Map.Entry<Long, List<WorkoutExerciseLog>> entry
                : grouped.entrySet()) {

            result.add(
                    buildExerciseProgress(
                            entry.getValue()
                    )
            );
        }

        return result.stream()
                .sorted(
                        Comparator.comparing(
                                ExerciseProgressResponse::exerciseName
                        )
                )
                .toList();
    }


    @Override
    public List<PersonalRecordResponse> getPersonalRecords(
            Long memberId,
            LocalDate fromDate,
            LocalDate toDate
    ) {

        validateDates(fromDate, toDate);

        List<WorkoutSession> sessions =
                getCompletedSessions(
                        memberId,
                        fromDate,
                        toDate
                );

        List<WorkoutExerciseLog> exerciseLogs =
                getExerciseLogs(sessions);

        List<WorkoutSetLog> sets =
                getSetLogs(exerciseLogs);

        Map<Long, PersonalRecordCandidate> records =
                new HashMap<>();

        for (WorkoutSetLog set : sets) {

            if (!set.isCompleted()) {
                continue;
            }

            if (set.getWeight() == null) {
                continue;
            }

            if (set.getRepetitions() == null) {
                continue;
            }

            WorkoutExerciseLog exerciseLog =
                    set.getWorkoutExerciseLog();

            Exercise exercise =
                    exerciseLog.getExercise();

            Long exerciseId =
                    exercise.getId();

            BigDecimal estimatedOneRepMax =
                    calculateEstimatedOneRepMax(
                            set.getWeight(),
                            set.getRepetitions()
                    );

            PersonalRecordCandidate candidate =
                    new PersonalRecordCandidate(
                            exerciseId,
                            exercise.getName(),
                            set.getWeight(),
                            set.getRepetitions(),
                            estimatedOneRepMax,
                            exerciseLog
                                    .getWorkoutSession()
                                    .getStartedAt()
                    );

            records.merge(
                    exerciseId,
                    candidate,
                    this::selectBetterRecord
            );
        }

        return records.values()
                .stream()
                .map(candidate ->
                        new PersonalRecordResponse(
                                candidate.exerciseId(),
                                candidate.exerciseName(),
                                candidate.weight(),
                                candidate.repetitions(),
                                candidate.estimatedOneRepMax(),
                                candidate.achievedAt()
                        )
                )
                .sorted(
                        Comparator.comparing(
                                PersonalRecordResponse::exerciseName
                        )
                )
                .toList();
    }


    private List<WorkoutSession> getCompletedSessions(
            Long memberId,
            LocalDate fromDate,
            LocalDate toDate
    ) {

        LocalDateTime from =
                fromDate.atStartOfDay();

        LocalDateTime to =
                toDate.plusDays(1)
                        .atStartOfDay();

        return sessionRepository
                .findAllByMemberIdAndStartedAtGreaterThanEqualAndStartedAtLessThanAndStatusOrderByStartedAtAsc(
                        memberId,
                        from,
                        to,
                        WorkoutSessionStatus.COMPLETED
                );
    }


    private List<WorkoutExerciseLog> getExerciseLogs(
            List<WorkoutSession> sessions
    ) {

        if (sessions.isEmpty()) {
            return List.of();
        }

        List<Long> sessionIds =
                sessions.stream()
                        .map(WorkoutSession::getId)
                        .toList();

        return exerciseLogRepository
                .findAllByWorkoutSessionIdIn(
                        sessionIds
                );
    }


    private List<WorkoutSetLog> getSetLogs(
            List<WorkoutExerciseLog> exerciseLogs
    ) {

        if (exerciseLogs.isEmpty()) {
            return List.of();
        }

        List<Long> exerciseLogIds =
                exerciseLogs.stream()
                        .map(WorkoutExerciseLog::getId)
                        .toList();

        return setLogRepository
                .findAllByWorkoutExerciseLogIdIn(
                        exerciseLogIds
                );
    }


    private BigDecimal calculateTotalVolume(
            List<WorkoutSetLog> sets
    ) {

        BigDecimal total =
                BigDecimal.ZERO;

        for (WorkoutSetLog set : sets) {

            if (!set.isCompleted()) {
                continue;
            }

            if (set.getWeight() == null) {
                continue;
            }

            if (set.getRepetitions() == null) {
                continue;
            }

            BigDecimal volume =
                    set.getWeight()
                            .multiply(
                                    BigDecimal.valueOf(
                                            set.getRepetitions()
                                    )
                            );

            total =
                    total.add(volume);
        }

        return total.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    private BigDecimal calculateAverageWorkoutDuration(
            List<WorkoutSession> sessions
    ) {

        if (sessions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long totalMinutes = 0;

        int sessionsWithDuration = 0;

        for (WorkoutSession session : sessions) {

            if (session.getStartedAt() == null) {
                continue;
            }

            if (session.getCompletedAt() == null) {
                continue;
            }

            long minutes =
                    Duration.between(
                            session.getStartedAt(),
                            session.getCompletedAt()
                    ).toMinutes();

            totalMinutes += minutes;

            sessionsWithDuration++;
        }

        if (sessionsWithDuration == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(totalMinutes)
                .divide(
                        BigDecimal.valueOf(
                                sessionsWithDuration
                        ),
                        2,
                        RoundingMode.HALF_UP
                );
    }


    private ExerciseProgressResponse buildExerciseProgress(
            List<WorkoutExerciseLog> logs
    ) {

        WorkoutExerciseLog first =
                logs.stream()
                        .min(
                                Comparator.comparing(
                                        log -> log
                                                .getWorkoutSession()
                                                .getStartedAt()
                                )
                        )
                        .orElseThrow();

        WorkoutExerciseLog latest =
                logs.stream()
                        .max(
                                Comparator.comparing(
                                        log -> log
                                                .getWorkoutSession()
                                                .getStartedAt()
                                )
                        )
                        .orElseThrow();

        List<WorkoutSetLog> sets =
                getSetLogs(logs);

        List<WorkoutSetLog> completedSets =
                sets.stream()
                        .filter(WorkoutSetLog::isCompleted)
                        .toList();

        BigDecimal bestWeight =
                completedSets.stream()
                        .map(WorkoutSetLog::getWeight)
                        .filter(weight ->
                                weight != null
                        )
                        .max(
                                Comparator.naturalOrder()
                        )
                        .orElse(BigDecimal.ZERO);

        int bestRepetitions =
                completedSets.stream()
                        .map(WorkoutSetLog::getRepetitions)
                        .filter(repetitions ->
                                repetitions != null
                        )
                        .max(
                                Comparator.naturalOrder()
                        )
                        .orElse(0);

        long totalRepetitions =
                completedSets.stream()
                        .mapToLong(set ->
                                set.getRepetitions() == null
                                        ? 0
                                        : set.getRepetitions()
                        )
                        .sum();

        BigDecimal totalVolume =
                calculateTotalVolume(
                        completedSets
                );

        return new ExerciseProgressResponse(
                first.getExercise().getId(),
                first.getExercise().getName(),
                first.getWorkoutSession()
                        .getStartedAt()
                        .toLocalDate(),
                latest.getWorkoutSession()
                        .getStartedAt()
                        .toLocalDate(),
                bestWeight,
                bestRepetitions,
                totalVolume,
                sets.size(),
                completedSets.size(),
                totalRepetitions
        );
    }


    private BigDecimal calculateEstimatedOneRepMax(
            BigDecimal weight,
            int repetitions
    ) {

        if (repetitions <= 0) {
            return BigDecimal.ZERO;
        }

        /*
         * Epley formula:
         *
         * 1RM = weight × (1 + reps / 30)
         */

        BigDecimal reps =
                BigDecimal.valueOf(repetitions);

        BigDecimal factor =
                BigDecimal.ONE.add(
                        reps.divide(
                                BigDecimal.valueOf(30),
                                6,
                                RoundingMode.HALF_UP
                        )
                );

        return weight
                .multiply(factor)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }


    private PersonalRecordCandidate selectBetterRecord(
            PersonalRecordCandidate first,
            PersonalRecordCandidate second
    ) {

        int comparison =
                second.estimatedOneRepMax()
                        .compareTo(
                                first.estimatedOneRepMax()
                        );

        if (comparison > 0) {
            return second;
        }

        if (comparison < 0) {
            return first;
        }

        return second.achievedAt()
                .isAfter(first.achievedAt())
                ? second
                : first;
    }


    private void validateDates(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        if (fromDate == null || toDate == null) {

            throw new IllegalArgumentException(
                    "fromDate and toDate are required"
            );
        }

        if (fromDate.isAfter(toDate)) {

            throw new IllegalArgumentException(
                    "fromDate cannot be after toDate"
            );
        }
    }


    private record PersonalRecordCandidate(

            Long exerciseId,

            String exerciseName,

            BigDecimal weight,

            Integer repetitions,

            BigDecimal estimatedOneRepMax,

            LocalDateTime achievedAt
    ) {
    }
}
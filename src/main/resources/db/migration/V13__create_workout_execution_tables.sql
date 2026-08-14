CREATE TABLE workout_sessions (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                  member_id BIGINT NOT NULL,

                                  workout_program_id BIGINT NOT NULL,

                                  started_at TIMESTAMP NOT NULL,

                                  completed_at TIMESTAMP,

                                  status VARCHAR(30) NOT NULL,

                                  notes TEXT,

                                  created_at TIMESTAMP NOT NULL,

                                  updated_at TIMESTAMP NOT NULL,

                                  CONSTRAINT fk_workout_session_member
                                      FOREIGN KEY (member_id)
                                          REFERENCES member_profiles(id),

                                  CONSTRAINT fk_workout_session_program
                                      FOREIGN KEY (workout_program_id)
                                          REFERENCES workout_programs(id),

                                  CONSTRAINT chk_workout_session_status
                                      CHECK (
                                              status IN (
                                                         'IN_PROGRESS',
                                                         'COMPLETED',
                                                         'CANCELLED'
                                              )
                                          )
);

CREATE INDEX idx_workout_session_member
    ON workout_sessions(member_id);

CREATE INDEX idx_workout_session_program
    ON workout_sessions(workout_program_id);

CREATE INDEX idx_workout_session_started_at
    ON workout_sessions(started_at);


CREATE TABLE workout_exercise_logs (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                       workout_session_id BIGINT NOT NULL,

                                       exercise_id BIGINT NOT NULL,

                                       exercise_order INTEGER NOT NULL,

                                       notes TEXT,

                                       created_at TIMESTAMP NOT NULL,

                                       updated_at TIMESTAMP NOT NULL,

                                       CONSTRAINT fk_exercise_log_session
                                           FOREIGN KEY (workout_session_id)
                                               REFERENCES workout_sessions(id)
                                               ON DELETE CASCADE,

                                       CONSTRAINT fk_exercise_log_exercise
                                           FOREIGN KEY (exercise_id)
                                               REFERENCES exercises(id),

                                       CONSTRAINT uk_session_exercise
                                           UNIQUE (
                                                   workout_session_id,
                                                   exercise_id
                                           ),

                                       CONSTRAINT chk_exercise_log_order
                                           CHECK (exercise_order > 0)
);

CREATE INDEX idx_exercise_log_session
    ON workout_exercise_logs(workout_session_id);

CREATE INDEX idx_exercise_log_exercise
    ON workout_exercise_logs(exercise_id);


CREATE TABLE workout_set_logs (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                  workout_exercise_log_id BIGINT NOT NULL,

                                  set_number INTEGER NOT NULL,

                                  repetitions INTEGER,

                                  weight NUMERIC(10, 2),

                                  duration_seconds INTEGER,

                                  distance NUMERIC(10, 2),

                                  completed BOOLEAN NOT NULL DEFAULT FALSE,

                                  notes TEXT,

                                  created_at TIMESTAMP NOT NULL,

                                  updated_at TIMESTAMP NOT NULL,

                                  CONSTRAINT fk_set_log_exercise_log
                                      FOREIGN KEY (workout_exercise_log_id)
                                          REFERENCES workout_exercise_logs(id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT uk_exercise_log_set
                                      UNIQUE (
                                              workout_exercise_log_id,
                                              set_number
                                      ),

                                  CONSTRAINT chk_set_number
                                      CHECK (set_number > 0),

                                  CONSTRAINT chk_set_repetitions
                                      CHECK (
                                              repetitions IS NULL
                                              OR repetitions > 0
                                          ),

                                  CONSTRAINT chk_set_weight
                                      CHECK (
                                              weight IS NULL
                                              OR weight >= 0
                                          ),

                                  CONSTRAINT chk_set_duration
                                      CHECK (
                                              duration_seconds IS NULL
                                              OR duration_seconds > 0
                                          ),

                                  CONSTRAINT chk_set_distance
                                      CHECK (
                                              distance IS NULL
                                              OR distance >= 0
                                          )
);

CREATE INDEX idx_set_log_exercise_log
    ON workout_set_logs(workout_exercise_log_id);
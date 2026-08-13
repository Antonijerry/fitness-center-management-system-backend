CREATE TABLE exercises (
                           id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,

                           name VARCHAR(150) NOT NULL UNIQUE,

                           category VARCHAR(40) NOT NULL,

                           muscle_group VARCHAR(100),

                           instructions VARCHAR(2000),

                           equipment VARCHAR(1000),

                           active BOOLEAN NOT NULL DEFAULT TRUE,

                           created_at TIMESTAMP NOT NULL,

                           updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_exercise_name
    ON exercises(name);

CREATE INDEX idx_exercise_category
    ON exercises(category);


CREATE TABLE workout_programs (
                                  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,

                                  member_id BIGINT NOT NULL,

                                  trainer_id BIGINT NOT NULL,

                                  name VARCHAR(200) NOT NULL,

                                  description VARCHAR(2000),

                                  goal VARCHAR(255),

                                  start_date DATE NOT NULL,

                                  end_date DATE,

                                  status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

                                  created_at TIMESTAMP NOT NULL,

                                  updated_at TIMESTAMP NOT NULL,

                                  CONSTRAINT fk_workout_program_member
                                      FOREIGN KEY (member_id)
                                          REFERENCES member_profiles(id),

                                  CONSTRAINT fk_workout_program_trainer
                                      FOREIGN KEY (trainer_id)
                                          REFERENCES trainer_profiles(id),

                                  CONSTRAINT chk_workout_program_dates
                                      CHECK (
                                              end_date IS NULL
                                              OR end_date >= start_date
                                          )
);

CREATE INDEX idx_workout_program_member
    ON workout_programs(member_id);

CREATE INDEX idx_workout_program_trainer
    ON workout_programs(trainer_id);

CREATE INDEX idx_workout_program_status
    ON workout_programs(status);


CREATE TABLE workout_program_days (
                                      id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,

                                      workout_program_id BIGINT NOT NULL,

                                      day_number INTEGER NOT NULL,

                                      name VARCHAR(150) NOT NULL,

                                      notes VARCHAR(1000),

                                      created_at TIMESTAMP NOT NULL,

                                      updated_at TIMESTAMP NOT NULL,

                                      CONSTRAINT fk_program_day_program
                                          FOREIGN KEY (workout_program_id)
                                              REFERENCES workout_programs(id)
                                              ON DELETE CASCADE,

                                      CONSTRAINT uk_program_day
                                          UNIQUE (
                                                  workout_program_id,
                                                  day_number
                                          ),

                                      CONSTRAINT chk_program_day_number
                                          CHECK (day_number > 0)
);


CREATE TABLE workout_exercises (
                                   id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,

                                   program_day_id BIGINT NOT NULL,

                                   exercise_id BIGINT NOT NULL,

                                   exercise_order INTEGER NOT NULL,

                                   exercise_type VARCHAR(20) NOT NULL,

                                   sets INTEGER,

                                   repetitions INTEGER,

                                   weight DECIMAL(10,2),

                                   duration_seconds INTEGER,

                                   distance DECIMAL(10,2),

                                   rest_seconds INTEGER,

                                   instructions VARCHAR(2000),

                                   created_at TIMESTAMP NOT NULL,

                                   updated_at TIMESTAMP NOT NULL,

                                   CONSTRAINT fk_workout_exercise_day
                                       FOREIGN KEY (program_day_id)
                                           REFERENCES workout_program_days(id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT fk_workout_exercise_exercise
                                       FOREIGN KEY (exercise_id)
                                           REFERENCES exercises(id),

                                   CONSTRAINT uk_day_exercise_order
                                       UNIQUE (
                                               program_day_id,
                                               exercise_order
                                       ),

                                   CONSTRAINT chk_exercise_order
                                       CHECK (exercise_order > 0),

                                   CONSTRAINT chk_sets
                                       CHECK (
                                               sets IS NULL
                                               OR sets > 0
                                           ),

                                   CONSTRAINT chk_repetitions
                                       CHECK (
                                               repetitions IS NULL
                                               OR repetitions > 0
                                           ),

                                   CONSTRAINT chk_duration
                                       CHECK (
                                               duration_seconds IS NULL
                                               OR duration_seconds > 0
                                           ),

                                   CONSTRAINT chk_distance
                                       CHECK (
                                               distance IS NULL
                                               OR distance > 0
                                           ),

                                   CONSTRAINT chk_rest
                                       CHECK (
                                               rest_seconds IS NULL
                                               OR rest_seconds >= 0
                                           )
);

CREATE INDEX idx_workout_exercise_day
    ON workout_exercises(program_day_id);

CREATE INDEX idx_workout_exercise_exercise
    ON workout_exercises(exercise_id);
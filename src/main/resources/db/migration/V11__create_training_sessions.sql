CREATE TABLE training_sessions (
                                   id BIGINT NOT NULL AUTO_INCREMENT,

                                   trainer_id BIGINT NOT NULL,

                                   member_id BIGINT NOT NULL,

                                   session_date DATE NOT NULL,

                                   start_time TIME NOT NULL,

                                   end_time TIME NOT NULL,

                                   session_type VARCHAR(40) NOT NULL,

                                   status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',

                                   location VARCHAR(255),

                                   notes VARCHAR(2000),

                                   cancellation_reason VARCHAR(1000),

                                   started_at TIMESTAMP NULL,

                                   completed_at TIMESTAMP NULL,

                                   created_at TIMESTAMP NOT NULL,

                                   updated_at TIMESTAMP NOT NULL,

                                   CONSTRAINT pk_training_sessions
                                       PRIMARY KEY (id),

                                   CONSTRAINT fk_session_trainer
                                       FOREIGN KEY (trainer_id)
                                           REFERENCES trainer_profiles(id),

                                   CONSTRAINT fk_session_member
                                       FOREIGN KEY (member_id)
                                           REFERENCES member_profiles(id)
);


CREATE INDEX idx_session_trainer
    ON training_sessions(trainer_id);

CREATE INDEX idx_session_member
    ON training_sessions(member_id);

CREATE INDEX idx_session_date
    ON training_sessions(session_date);

CREATE INDEX idx_session_status
    ON training_sessions(status);
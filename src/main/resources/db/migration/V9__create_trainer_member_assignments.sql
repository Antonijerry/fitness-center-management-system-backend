CREATE TABLE trainer_member_assignments (
                                            id BIGINT NOT NULL AUTO_INCREMENT,

                                            trainer_id BIGINT NOT NULL,

                                            member_id BIGINT NOT NULL,

                                            assigned_at TIMESTAMP NOT NULL,

                                            ended_at TIMESTAMP NULL,

                                            status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

                                            notes VARCHAR(2000),

                                            primary_trainer BOOLEAN NOT NULL DEFAULT FALSE,

                                            created_at TIMESTAMP NOT NULL,

                                            updated_at TIMESTAMP NOT NULL,

                                            CONSTRAINT pk_trainer_member_assignments
                                                PRIMARY KEY (id),

                                            CONSTRAINT fk_assignment_trainer
                                                FOREIGN KEY (trainer_id)
                                                    REFERENCES trainer_profiles(id),

                                            CONSTRAINT fk_assignment_member
                                                FOREIGN KEY (member_id)
                                                    REFERENCES member_profiles(id)
);


CREATE INDEX idx_assignment_trainer
    ON trainer_member_assignments(trainer_id);

CREATE INDEX idx_assignment_member
    ON trainer_member_assignments(member_id);

CREATE INDEX idx_assignment_status
    ON trainer_member_assignments(status);
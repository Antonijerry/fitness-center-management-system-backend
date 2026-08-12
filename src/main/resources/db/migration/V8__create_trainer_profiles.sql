CREATE TABLE trainer_profiles (
                                  id BIGINT NOT NULL AUTO_INCREMENT,

                                  user_id BIGINT NOT NULL,

                                  employee_number VARCHAR(30) NOT NULL,

                                  specialization VARCHAR(150) NOT NULL,

                                  certifications VARCHAR(1000),

                                  years_of_experience INT,

                                  bio VARCHAR(2000),

                                  hourly_rate DECIMAL(12, 2),

                                  status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

                                  created_at TIMESTAMP NOT NULL,

                                  updated_at TIMESTAMP NOT NULL,

                                  CONSTRAINT pk_trainer_profiles
                                      PRIMARY KEY (id),

                                  CONSTRAINT uk_trainer_profile_user
                                      UNIQUE (user_id),

                                  CONSTRAINT uk_trainer_profile_employee_number
                                      UNIQUE (employee_number),

                                  CONSTRAINT fk_trainer_profile_user
                                      FOREIGN KEY (user_id)
                                          REFERENCES users(id)
);


CREATE INDEX idx_trainer_profile_status
    ON trainer_profiles(status);

CREATE INDEX idx_trainer_profile_specialization
    ON trainer_profiles(specialization);

CREATE INDEX idx_trainer_profile_employee_number
    ON trainer_profiles(employee_number);
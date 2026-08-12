CREATE TABLE member_profiles (
                                 id BIGINT NOT NULL AUTO_INCREMENT,

                                 user_id BIGINT NOT NULL,

                                 member_number VARCHAR(30) NOT NULL,

                                 phone VARCHAR(30) NOT NULL,

                                 gender VARCHAR(20),

                                 date_of_birth DATE,

                                 address VARCHAR(255),

                                 emergency_contact_name VARCHAR(150),

                                 emergency_contact_phone VARCHAR(30),

                                 emergency_contact_relationship VARCHAR(100),

                                 fitness_goals VARCHAR(1000),

                                 fitness_notes VARCHAR(2000),

                                 status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

                                 created_at TIMESTAMP NOT NULL,

                                 updated_at TIMESTAMP NOT NULL,

                                 CONSTRAINT pk_member_profiles
                                     PRIMARY KEY (id),

                                 CONSTRAINT uk_member_profile_user
                                     UNIQUE (user_id),

                                 CONSTRAINT uk_member_profile_member_number
                                     UNIQUE (member_number),

                                 CONSTRAINT fk_member_profile_user
                                     FOREIGN KEY (user_id)
                                         REFERENCES users(id)
);


CREATE INDEX idx_member_profile_status
    ON member_profiles(status);

CREATE INDEX idx_member_profile_member_number
    ON member_profiles(member_number);

CREATE INDEX idx_member_profile_phone
    ON member_profiles(phone);
CREATE TABLE attendance (
                            id BIGINT NOT NULL AUTO_INCREMENT,

                            member_id BIGINT NOT NULL,

                            attendance_date DATE NOT NULL,

                            check_in_time TIMESTAMP NOT NULL,

                            check_out_time TIMESTAMP NULL,

                            status VARCHAR(30) NOT NULL DEFAULT 'CHECKED_IN',

                            method VARCHAR(30) NOT NULL DEFAULT 'MANUAL',

                            notes VARCHAR(1000),

                            created_at TIMESTAMP NOT NULL,

                            updated_at TIMESTAMP NOT NULL,

                            CONSTRAINT pk_attendance
                                PRIMARY KEY (id),

                            CONSTRAINT fk_attendance_member
                                FOREIGN KEY (member_id)
                                    REFERENCES member_profiles(id)
);


CREATE INDEX idx_attendance_member
    ON attendance(member_id);

CREATE INDEX idx_attendance_date
    ON attendance(attendance_date);

CREATE INDEX idx_attendance_status
    ON attendance(status);

CREATE INDEX idx_attendance_check_in
    ON attendance(check_in_time);
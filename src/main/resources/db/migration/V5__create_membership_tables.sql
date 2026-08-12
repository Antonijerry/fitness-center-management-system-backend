CREATE TABLE membership_plans (
                                  id BIGINT NOT NULL AUTO_INCREMENT,

                                  name VARCHAR(100) NOT NULL,

                                  description VARCHAR(500),

                                  type VARCHAR(30) NOT NULL,

                                  price DECIMAL(12,2) NOT NULL,

                                  duration_in_days INT NOT NULL,

                                  max_visits_per_month INT NOT NULL,

                                  active BOOLEAN NOT NULL DEFAULT TRUE,

                                  auto_renewable BOOLEAN NOT NULL DEFAULT FALSE,

                                  created_at TIMESTAMP NOT NULL,

                                  updated_at TIMESTAMP NOT NULL,

                                  CONSTRAINT pk_membership_plans
                                      PRIMARY KEY (id),

                                  CONSTRAINT uk_membership_plans_name
                                      UNIQUE (name)
);


CREATE INDEX idx_membership_plan_name
    ON membership_plans(name);

CREATE INDEX idx_membership_plan_active
    ON membership_plans(active);


CREATE TABLE memberships (
                             id BIGINT NOT NULL AUTO_INCREMENT,

                             user_id BIGINT NOT NULL,

                             membership_plan_id BIGINT NOT NULL,

                             start_date DATE NOT NULL,

                             end_date DATE NOT NULL,

                             status VARCHAR(30) NOT NULL,

                             price DECIMAL(12,2) NOT NULL,

                             auto_renewable BOOLEAN NOT NULL DEFAULT FALSE,

                             notes VARCHAR(500),

                             created_at TIMESTAMP NOT NULL,

                             updated_at TIMESTAMP NOT NULL,

                             CONSTRAINT pk_memberships
                                 PRIMARY KEY (id),

                             CONSTRAINT fk_membership_user
                                 FOREIGN KEY (user_id)
                                     REFERENCES users(id),

                             CONSTRAINT fk_membership_plan
                                 FOREIGN KEY (membership_plan_id)
                                     REFERENCES membership_plans(id)
);


CREATE INDEX idx_membership_user_id
    ON memberships(user_id);

CREATE INDEX idx_membership_status
    ON memberships(status);

CREATE INDEX idx_membership_start_date
    ON memberships(start_date);

CREATE INDEX idx_membership_end_date
    ON memberships(end_date);
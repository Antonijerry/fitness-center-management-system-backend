CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,

                       first_name VARCHAR(100) NOT NULL,

                       last_name VARCHAR(100) NOT NULL,

                       email VARCHAR(255) NOT NULL,

                       phone VARCHAR(30),

                       password VARCHAR(255) NOT NULL,

                       enabled BOOLEAN NOT NULL DEFAULT TRUE,

                       account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE INDEX idx_users_phone
    ON users(phone);

CREATE INDEX idx_users_enabled
    ON users(enabled);
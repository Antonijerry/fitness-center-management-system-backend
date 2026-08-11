CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,

                       name VARCHAR(50) NOT NULL,

                       description VARCHAR(255),

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT uk_roles_name UNIQUE (name)
);

INSERT INTO roles (
    name,
    description
)
VALUES
    (
        'ADMIN',
        'Full system administrator'
    ),
    (
        'MANAGER',
        'Fitness center manager'
    ),
    (
        'TRAINER',
        'Fitness trainer'
    ),
    (
        'RECEPTIONIST',
        'Front desk/reception staff'
    ),
    (
        'MEMBER',
        'Fitness center member'
    );
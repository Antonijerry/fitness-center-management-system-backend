-- =========================================================
-- NOTIFICATIONS
-- =========================================================

CREATE TABLE notifications (

                               id BIGINT NOT NULL AUTO_INCREMENT,

                               user_id BIGINT NOT NULL,

                               type VARCHAR(50) NOT NULL,

                               channel VARCHAR(20) NOT NULL,

                               status VARCHAR(20) NOT NULL,

                               title VARCHAR(255) NOT NULL,

                               message TEXT NOT NULL,

                               reference_id BIGINT NULL,

                               created_at DATETIME NOT NULL,

                               read_at DATETIME NULL,

                               sent_at DATETIME NULL,

                               failed_at DATETIME NULL,

                               failure_reason TEXT NULL,

                               retry_count INT NOT NULL DEFAULT 0,

                               PRIMARY KEY (id),

                               CONSTRAINT fk_notifications_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id)
                                       ON DELETE CASCADE
                                       ON UPDATE CASCADE,

                               INDEX idx_notification_user (user_id),

                               INDEX idx_notification_status (status),

                               INDEX idx_notification_created_at (created_at),

                               INDEX idx_notification_user_status (user_id, status),

                               INDEX idx_notification_type (type)
);


-- =========================================================
-- NOTIFICATION PREFERENCES
-- =========================================================

CREATE TABLE notification_preferences (

                                          id BIGINT NOT NULL AUTO_INCREMENT,

                                          user_id BIGINT NOT NULL,

                                          notification_type VARCHAR(50) NOT NULL,

                                          in_app_enabled BOOLEAN NOT NULL DEFAULT TRUE,

                                          email_enabled BOOLEAN NOT NULL DEFAULT TRUE,

                                          PRIMARY KEY (id),

                                          CONSTRAINT uk_notification_preference_user_type
                                              UNIQUE (
                                                      user_id,
                                                      notification_type
                                              ),

                                          CONSTRAINT fk_notification_preferences_user
                                              FOREIGN KEY (user_id)
                                                  REFERENCES users(id)
                                                  ON DELETE CASCADE
                                                  ON UPDATE CASCADE,

                                          INDEX idx_notification_preferences_user (user_id)
);
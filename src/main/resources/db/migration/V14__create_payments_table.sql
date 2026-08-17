CREATE TABLE payments (
                          id BIGINT NOT NULL AUTO_INCREMENT,

                          membership_id BIGINT NOT NULL,

                          amount DECIMAL(19, 2) NOT NULL,

                          currency VARCHAR(3) NOT NULL,

                          reference VARCHAR(100) NOT NULL,

                          gateway_reference VARCHAR(100),

                          status VARCHAR(30) NOT NULL,

                          payment_method VARCHAR(30) NOT NULL,

                          gateway_response TEXT,

                          paid_at DATETIME NULL,

                          created_at DATETIME NOT NULL,

                          updated_at DATETIME NOT NULL,

                          PRIMARY KEY (id),

                          CONSTRAINT uk_payments_reference
                              UNIQUE (reference),

                          CONSTRAINT fk_payments_membership
                              FOREIGN KEY (membership_id)
                                  REFERENCES memberships(id)
                                  ON DELETE RESTRICT
                                  ON UPDATE CASCADE,

                          INDEX idx_payment_membership (membership_id),

                          INDEX idx_payment_status (status),

                          INDEX idx_gateway_reference (gateway_reference)
);
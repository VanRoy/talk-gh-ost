-- Create test table
DROP TABLE IF EXISTS transactions;
CREATE TABLE transactions
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_uuid VARCHAR(36) NULL,
    amount DOUBLE NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(255) NULL,
    debit TINYINT NOT NULL,
    timestamp_last_update BIGINT NOT NULL
);

CREATE INDEX transactions_user_idx ON transactions (user_uuid, date);

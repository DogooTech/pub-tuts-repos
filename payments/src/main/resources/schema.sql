DROP TABLE IF EXISTS account;

CREATE TABLE account (
     id BIGSERIAL PRIMARY KEY,
     ref_id VARCHAR(255),
     account_reference_type VARCHAR(50),
     customer_id VARCHAR(255) NOT NULL,
     initial_balance NUMERIC(19, 4) NOT NULL,
     amount NUMERIC(19, 4) NOT NULL,
     event_type VARCHAR(50) NOT NULL,
     version BIGINT NOT NULL,
     created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
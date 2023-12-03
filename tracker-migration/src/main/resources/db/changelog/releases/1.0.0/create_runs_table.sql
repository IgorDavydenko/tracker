CREATE TABLE IF NOT EXISTS run_entity (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    start_date_time TIMESTAMP,
    finish_date_time TIMESTAMP,
    start_latitude DOUBLE PRECISION,
    start_longitude DOUBLE PRECISION,
    finish_latitude DOUBLE PRECISION,
    finish_longitude DOUBLE PRECISION,
    distance DOUBLE PRECISION,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
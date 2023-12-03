CREATE TABLE IF NOT EXISTS runs (
    id BIGSERIAL PRIMARY KEY,
    start_date_time TIMESTAMP NOT NULL,
    finish_date_time TIMESTAMP,
    start_latitude DOUBLE PRECISION NOT NULL,
    finish_latitude DOUBLE PRECISION,
    start_longitude DOUBLE PRECISION NOT NULL,
    finish_longitude DOUBLE PRECISION,
    distance INTEGER,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
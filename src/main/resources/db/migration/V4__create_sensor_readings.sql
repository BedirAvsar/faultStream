CREATE TABLE sensor_readings (
    id BIGSERIAL PRIMARY KEY,
    sensor_id UUID REFERENCES sensors(id),
    value DOUBLE PRECISION NOT NULL,
    recorded_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_sensor_readings_sensor_id_recorded_at
    ON sensor_readings(sensor_id, recorded_at DESC);
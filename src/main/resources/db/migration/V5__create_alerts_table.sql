CREATE TABLE IF NOT EXISTS alerts (
    id UUID PRIMARY KEY,
    sensor_id UUID NOT NULL,
    equipment_id UUID NOT NULL,
    level VARCHAR(50) NOT NULL,
    message VARCHAR(500) NOT NULL,
    triggered_at TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alerts_sensor FOREIGN KEY (sensor_id) REFERENCES sensors(id) ON DELETE CASCADE,
    CONSTRAINT fk_alerts_equipment FOREIGN KEY (equipment_id) REFERENCES equipments(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_alerts_status ON alerts(status);
CREATE INDEX IF NOT EXISTS idx_alerts_equipment_id ON alerts(equipment_id);
CREATE INDEX IF NOT EXISTS idx_alerts_sensor_id ON alerts(sensor_id);

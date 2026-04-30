-- Add indexing strategy for performance optimization

-- Indexes for equipments
CREATE INDEX IF NOT EXISTS idx_equipments_type ON equipments(type);
CREATE INDEX IF NOT EXISTS idx_equipments_status ON equipments(status);

-- Indexes for sensors
CREATE INDEX IF NOT EXISTS idx_sensors_equipment_id ON sensors(equipment_id);
CREATE INDEX IF NOT EXISTS idx_sensors_type ON sensors(type);

-- Assuming sensor_readings table exists from V4, we index the recorded_at timestamp
CREATE INDEX IF NOT EXISTS idx_sensor_readings_sensor_id ON sensor_readings(sensor_id);
CREATE INDEX IF NOT EXISTS idx_sensor_readings_recorded_at ON sensor_readings(recorded_at DESC);



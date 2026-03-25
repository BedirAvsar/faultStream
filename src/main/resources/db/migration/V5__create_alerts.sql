CREATE TABLE alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sensor_id UUID REFERENCES sensors(id),
    equipment_id UUID REFERENCES equipments(id),
    value DOUBLE PRECISION NOT NULL,
    threshold_exceeded TEXT NOT NULL CHECK (threshold_exceeded IN ('MIN','MAX')),
    severity TEXT NOT NULL CHECK (severity IN ('LOW','MEDIUM','HIGH','CRITICAL')),
    message TEXT,
    is_acknowledged BOOLEAN DEFAULT false,
    acknowledged_by UUID REFERENCES users(id),
    acknowledged_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);

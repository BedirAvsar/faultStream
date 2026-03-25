CREATE TABLE sensors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    equipment_id UUID REFERENCES equipments(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    type TEXT NOT NULL CHECK (type IN ('TEMPERATURE','VIBRATION','PRESSURE','CURRENT','HUMIDITY')),
    unit TEXT NOT NULL,
    threshold_min DOUBLE PRECISION,
    threshold_max DOUBLE PRECISION,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW()
);

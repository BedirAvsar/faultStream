CREATE TABLE equipments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    type TEXT NOT NULL CHECK (type IN ('COMPRESSOR','PUMP','HEAT_EXCHANGER','AHU','CHILLER')),
    location TEXT NOT NULL,
    building TEXT,
    floor TEXT,
    manufacturer TEXT,
    model TEXT,
    serial_number TEXT UNIQUE,
    install_date DATE,
    last_maintenance_date DATE,
    status TEXT DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','IDLE','FAULT','MAINTENANCE')),
    created_at TIMESTAMP DEFAULT NOW()
);

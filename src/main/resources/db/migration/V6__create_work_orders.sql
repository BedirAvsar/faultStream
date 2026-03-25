CREATE TABLE work_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    equipment_id UUID REFERENCES equipments(id),
    alert_id UUID REFERENCES alerts(id),
    title TEXT NOT NULL,
    description TEXT,
    type TEXT NOT NULL CHECK (type IN ('CORRECTIVE','PREVENTIVE','INSPECTION')),
    priority TEXT DEFAULT 'MEDIUM' CHECK (priority IN ('LOW','MEDIUM','HIGH','CRITICAL')),
    status TEXT DEFAULT 'OPEN' CHECK (status IN ('OPEN','IN_PROGRESS','WAITING_PARTS','CLOSED')),
    assigned_to UUID REFERENCES users(id),
    created_by UUID REFERENCES users(id),
    due_date TIMESTAMP,
    closed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);

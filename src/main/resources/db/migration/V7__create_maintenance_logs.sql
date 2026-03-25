CREATE TABLE maintenance_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    work_order_id UUID REFERENCES work_orders(id),
    technician_id UUID REFERENCES users(id),
    action_taken TEXT NOT NULL,
    parts_used TEXT,
    labor_hours DOUBLE PRECISION,
    notes TEXT,
    logged_at TIMESTAMP DEFAULT NOW()
);

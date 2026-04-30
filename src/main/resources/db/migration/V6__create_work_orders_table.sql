CREATE TABLE IF NOT EXISTS work_orders (
    id UUID PRIMARY KEY,
    alert_id UUID NOT NULL,
    assigned_technician_id UUID,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT fk_work_orders_alert FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE CASCADE,
    CONSTRAINT fk_work_orders_technician FOREIGN KEY (assigned_technician_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_work_orders_status ON work_orders(status);
CREATE INDEX IF NOT EXISTS idx_work_orders_technician_id ON work_orders(assigned_technician_id);
CREATE INDEX IF NOT EXISTS idx_work_orders_alert_id ON work_orders(alert_id);

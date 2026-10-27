
CREATE TABLE monitor_alerts (
    id UUID PRIMARY KEY,
    monitor_id UUID NOT NULL,
    alert_type VARCHAR(20) NOT NULL,
    message VARCHAR(500) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_monitor_alerts_monitor
        FOREIGN KEY (monitor_id)
        REFERENCES monitors(id)
);

CREATE INDEX idx_monitor_alerts_monitor_created_at
    ON monitor_alerts (monitor_id, created_at DESC);

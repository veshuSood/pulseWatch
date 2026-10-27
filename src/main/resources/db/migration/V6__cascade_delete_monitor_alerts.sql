ALTER TABLE monitor_alerts
DROP CONSTRAINT fk_monitor_alerts_monitor;

ALTER TABLE monitor_alerts
    ADD CONSTRAINT fk_monitor_alerts_monitor
        FOREIGN KEY (monitor_id)
            REFERENCES monitors(id)
            ON DELETE CASCADE;
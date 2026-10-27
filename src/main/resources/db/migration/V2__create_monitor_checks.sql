
CREATE TABLE monitor_checks (
                                id UUID PRIMARY KEY,

                                monitor_id UUID NOT NULL,

                                checked_at TIMESTAMPTZ NOT NULL,

                                status_code INTEGER,

                                response_time_ms BIGINT NOT NULL,

                                successful BOOLEAN NOT NULL,

                                CONSTRAINT fk_monitor_checks_monitor
                                    FOREIGN KEY (monitor_id)
                                        REFERENCES monitors(id)
                                        ON DELETE CASCADE
);

CREATE INDEX idx_monitor_checks_monitor_checked_at
    ON monitor_checks (monitor_id, checked_at DESC);

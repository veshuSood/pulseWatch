
CREATE TABLE monitors (
                          id UUID PRIMARY KEY,
                          name VARCHAR(120) NOT NULL,
                          url VARCHAR(2048) NOT NULL,
                          check_interval_seconds INTEGER NOT NULL DEFAULT 60,
                          enabled BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT chk_monitors_name_not_blank
                              CHECK (LENGTH(TRIM(name)) > 0),

                          CONSTRAINT chk_monitors_url_not_blank
                              CHECK (LENGTH(TRIM(url)) > 0),

                          CONSTRAINT chk_monitors_check_interval
                              CHECK (check_interval_seconds >= 30)
);

CREATE INDEX idx_monitors_enabled
    ON monitors (enabled);

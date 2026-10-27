
ALTER TABLE monitors
    ADD COLUMN user_id UUID;

-- Assign existing monitors to the earliest registered user.
UPDATE monitors
SET user_id = (
    SELECT id
    FROM users
    ORDER BY created_at ASC, id ASC
    LIMIT 1
    );

-- Stop migration if any monitor could not be assigned.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM monitors
        WHERE user_id IS NULL
    ) THEN
        RAISE EXCEPTION
            'Cannot assign monitor ownership: no registered user exists';
END IF;
END $$;

ALTER TABLE monitors
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE monitors
    ADD CONSTRAINT fk_monitors_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;

CREATE INDEX idx_monitors_user_id
    ON monitors(user_id);

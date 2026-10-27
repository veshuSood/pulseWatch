
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       name VARCHAR(120) NOT NULL,
                       password_hash VARCHAR(255),
                       auth_provider VARCHAR(20) NOT NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'USER',
                       created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT chk_users_name_not_blank
                           CHECK (LENGTH(TRIM(name)) > 0),

                       CONSTRAINT chk_users_email_not_blank
                           CHECK (LENGTH(TRIM(email)) > 0),

                       CONSTRAINT chk_users_auth_provider
                           CHECK (auth_provider IN ('LOCAL', 'GOOGLE')),

                       CONSTRAINT chk_users_role
                           CHECK (role IN ('USER', 'ADMIN')),

                       CONSTRAINT chk_users_password_provider
                           CHECK (
                               (auth_provider = 'LOCAL' AND password_hash IS NOT NULL)
                                   OR
                               (auth_provider = 'GOOGLE' AND password_hash IS NULL)
                               )
);

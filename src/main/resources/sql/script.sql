CREATE TABLE IF NOT EXISTS pastebin.users(
    id BIGSERIAL PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    rating DOUBLE PRECISION DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS pastebin.pastes(
    id BIGSERIAL PRIMARY KEY,
    paste_link VARCHAR(50) NOT NULL UNIQUE,
    google_file_id VARCHAR(128) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    tags TEXT[],
    expiration VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP,
    access VARCHAR(50) NOT NULL,
    password VARCHAR(255),
    title VARCHAR(128) NOT NULL,
    views BIGINT DEFAULT 0,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_pastes_tags ON pastebin.pastes USING GIN (tags);
CREATE INDEX idx_pastes_user_id ON pastebin.pastes(user_id);
CREATE INDEX idx_pastes_created_at ON pastebin.pastes(created_at DESC);
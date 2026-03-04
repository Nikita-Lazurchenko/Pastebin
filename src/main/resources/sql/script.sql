CREATE TABLE IF NOT EXISTS users(
    id BIGSERIAL PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    total_views BIGINT DEFAULT 0,
    avg_views DOUBLE PRECISION DEFAULT 0.0,
    rating DOUBLE PRECISION DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS pastes(
    id BIGSERIAL PRIMARY KEY,
    paste_link VARCHAR(50) NOT NULL UNIQUE,
    google_file_id VARCHAR(128) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    tags TEXT[],
    expiration VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NOT NULL,
    access VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    title VARCHAR(128) NOT NULL,
    views BIGINT NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id)
);


insert into users(id, firstname, lastname, username, email, password, role)
values (1, 'No', 'Name', 'no_null1111' ,'email@gmail.com', 787898, USER)
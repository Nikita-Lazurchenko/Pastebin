CREATE TABLE IF NOT EXISTS users(
    id BIGSERIAL PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
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
    user_id BIGINT NOT NULL REFERENCES users(id)
);

-- drop table if exists pastes;
-- drop table if exists users;

-- insert into users(id, firstname, lastname, email, password, role)
-- values (1, 'No', 'Name','email@gmail.com', 787898, USER)
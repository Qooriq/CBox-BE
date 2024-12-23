--liquibase formatted sql

--changeset Qooriq:1
CREATE TABLE IF NOT EXISTS files
(
    id         BIGSERIAL PRIMARY KEY,
    link       VARCHAR(128) UNIQUE NOT NULL,
    acces_type VARCHAR(128)        NOT NULL DEFAULT 'open'::VARCHAR
);

--changeset Qooriq:2
CREATE TABLE IF NOT EXISTS link_restrictions_bypass
(
    id      BIGSERIAL PRIMARY KEY,
    file_id BIGINT   NOT NULL,
    user_id uuid NOT NULL
);

--changeset Qooriq:3
CREATE TABLE IF NOT EXISTS users
(
    id           uuid PRIMARY KEY,
    created_at   TIMESTAMP           ,
    modified_at  TIMESTAMP           ,
    created_by   VARCHAR(128)        ,
    modified_by  VARCHAR(128)        ,
    status VARCHAR(8),
    role VARCHAR(8),
    password VARCHAR(256),
    username     VARCHAR(128)        NOT NULL
);

--changeset Qooriq:4
ALTER TABLE IF EXISTS link_restrictions_bypass
    ADD CONSTRAINT files_table FOREIGN KEY (file_id)
        REFERENCES files (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID;

--changeset Qooriq:5
ALTER TABLE IF EXISTS link_restrictions_bypass
    ADD CONSTRAINT user_table FOREIGN KEY (user_id)
        REFERENCES users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID;
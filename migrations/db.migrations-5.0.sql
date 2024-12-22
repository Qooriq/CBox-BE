--liquibase formatted sql

--changeset Qooriq:1
ALTER TABLE files
DROP CONSTRAINT files_link_key;

--changeset Qooriq:1
ALTER TABLE files
ADD UNIQUE(link, created_by)
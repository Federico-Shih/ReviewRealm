CREATE TABLE IF NOT EXISTS tokens
(
    id SERIAL PRIMARY KEY ,
    token character varying(16) UNIQUE,
    password varchar,
    expiration timestamp,
    userid integer,
    CONSTRAINT userforeignkey FOREIGN KEY (userid)
        REFERENCES users (id)
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

ALTER TABLE users ADD COLUMN enabled boolean DEFAULT false;

UPDATE users SET enabled = true WHERE true;
UPDATE users SET enabled = false WHERE password = '';

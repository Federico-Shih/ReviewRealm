CREATE TABLE IF NOT EXISTS images(
    id varchar(16) PRIMARY KEY,
    data bytea NOT NULL,
    mediatype varchar(50) NOT NULL
);
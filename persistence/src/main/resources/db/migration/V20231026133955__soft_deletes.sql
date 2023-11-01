ALTER TABLE reports ADD COLUMN state varchar(255) NOT NULL DEFAULT 'UNRESOLVED';
UPDATE reports SET state = 'REJECTED' WHERE resolved = true;
UPDATE reports SET state = 'DELETED_REVIEW' WHERE reviewid IS null;
ALTER TABLE reports DROP COLUMN resolved;
ALTER TABLE reviews ADD COLUMN deleted boolean NOT NULL DEFAULT false;
ALTER TABLE games ADD COLUMN deleted boolean NOT NULL DEFAULT false;
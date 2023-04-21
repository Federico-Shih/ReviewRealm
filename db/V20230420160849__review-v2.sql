ALTER TABLE reviews
    ADD COLUMN difficulty varchar(10) NULL,
    ADD COLUMN platform varchar(10) NULL,
    ADD COLUMN gamelength DOUBLE PRECISION NULL,
    ADD COLUMN replayability BOOLEAN NULL,
    ADD COLUMN completed BOOLEAN NULL
;
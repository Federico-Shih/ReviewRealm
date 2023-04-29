ALTER TABLE games
    ADD COLUMN imageId varchar;

UPDATE games
SET imageId=gamesPrev.imageId
FROM (
         SELECT id, SPLIT_PART(imageurl, '/', 3) as imageId FROM games
     ) AS gamesPrev
WHERE games.id = gamesPrev.id;

ALTER TABLE games
    ADD CONSTRAINT imageForeignKey
        FOREIGN KEY (imageid)
            REFERENCES images(id);

ALTER TABLE games
    DROP COLUMN imageurl;
ALTER TABLE games
    ADD COLUMN ratingsum INT DEFAULT 0,
    ADD COLUMN reviewcount INT DEFAULT 0;

UPDATE games
SET ratingsum=reviewsInfo.sum,
    reviewcount=reviewsInfo.count
FROM (
         SELECT count(r.id) as count, coalesce(sum(r.rating),0) as sum, r.gameid as gameid
         FROM reviews as r  GROUP BY gameid
     ) AS reviewsInfo
WHERE games.id = reviewsInfo.gameid;
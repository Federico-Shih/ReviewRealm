SET DATABASE SQL SYNTAX PGS TRUE;

INSERT INTO reviews(id, authorid, gameid, title, content, createddate, rating, difficulty, platform, gamelength, replayability, completed, likes, dislikes, deleted)
VALUES (1,1,1,'title1','content1',DATE '2090-07-15',7,'EASY','PC',10.0,true,true,10,1, false),
       (2,2,2,'title2','content2',DATE '2090-07-18',10,'EASY','PS',5.0,true,false,5,15, false),
       (3,2,3,'title3','content3',DATE '2090-07-31',5,'MEDIUM','PS',30.0,false,true,0,1, false);
ALTER SEQUENCE reviews_id_seq RESTART WITH 4;
INSERT INTO images(id, data, mediatype)
VALUES ('id1', '\x013d7d16d7ad4fefb61bd95b765c8ceb', 'image/jpeg');

INSERT INTO games (id,name, description, developer, publisher,suggestion,imageid, publishdate, ratingsum, reviewcount)
VALUES (1,'Super Game A', 'A thrilling adventure', 'Game Studios', 'Game Publisher', false,'id1', DATE '2090-07-15', 0, 0),
       (2,'Super Game B', 'BBBBBBBBBBBBBB', 'Game Studios', 'Game Publisher', false,'id1', DATE '2090-07-15', 0, 0),
       (3,'Subnautica', 'Juegos', 'Game Studios', 'Game Publisher', true,'id1', DATE '2090-07-15', 3, 3),
       (4,'Subnautica 2', 'Juegos', 'Game Studios', 'Game Publisher', false,'id1', DATE '2090-07-15', 1, 1);


INSERT INTO genreforgames (gameid,
                           genreid)
VALUES (1, 1);

INSERT INTO genreforgames (genreid, gameid)
values (2, 1);

INSERT INTO genreforgames (genreid, gameid)
values (2, 2);

INSERT INTO genreforgames (genreid, gameid)
values (3, 2);


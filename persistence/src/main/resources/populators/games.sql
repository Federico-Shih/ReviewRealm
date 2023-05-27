INSERT INTO images(id, data, mediatype)
VALUES ('id1', '\x013d7d16d7ad4fefb61bd95b765c8ceb', 'image/jpeg');
INSERT INTO games (id,name, description, developer, publisher,suggestion,imageid)

VALUES (1,'Super Game', 'A thrilling adventure', 'Game Studios', 'Game Publisher', false,'id1'),
       (2,'Super Game', 'A thrilling adventure', 'Game Studios', 'Game Publisher', false,'id1');


INSERT INTO genreforgames (gameid,
                           genreid)
VALUES (1,
        1);

INSERT INTO genreforgames (genreid, gameid)
values (2, 1);

INSERT INTO genreforgames (genreid, gameid)
values (2, 2);

INSERT INTO genreforgames (genreid, gameid)
values (3, 2);


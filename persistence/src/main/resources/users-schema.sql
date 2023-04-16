CREATE TABLE IF NOT EXISTS users (
    id serial PRIMARY KEY ,
    email varchar(100) NOT NULL UNIQUE ,
    password varchar(100) NOT NULL
    -- todo: username
    );

CREATE TABLE IF NOT EXISTS games (
    id SERIAL primary key,
    name varchar(100) NOT NULL,
    description text,
    developer varchar(100) NOT NULL,
    publisher varchar(100) NOT NULL,
    imageUrl text NOT NULL,
    publishDate timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS genreForGames (
    genreId int,
    gameId int,
    FOREIGN KEY(gameId) REFERENCES games(id) ON DELETE CASCADE,
    PRIMARY KEY(genreId, gameId)
);

CREATE TABLE IF NOT EXISTS reviews(
    id serial PRIMARY KEY,
    authorId int NOT NULL,
    gameId int NOT NULL,
    title varchar(100) NOT NULL,
    content text NOT NULL,
    createdDate timestamp NOT NULL,
    rating int NOT NULL CHECK (rating > 0 and rating <= 10),
    FOREIGN KEY(authorId) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(gameId) REFERENCES games(id) ON DELETE CASCADE
);

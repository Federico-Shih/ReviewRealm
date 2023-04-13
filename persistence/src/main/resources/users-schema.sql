CREATE TABLE IF NOT EXISTS users (
    id SERIAL primary key,
    email VARCHAR(100) not null unique,
    password varchar(100) not null
    -- todo: username
    );

CREATE TABLE IF NOT EXISTS genres (
    id SERIAL primary key,
    name VARCHAR(100) not null
);

CREATE TABLE IF NOT EXISTS games (
    id SERIAL primary key,
    name VARCHAR(100) not null,
    description text,
    developer VARCHAR(100) not null,
    publisher VARCHAR(100) not null,
    imageUrl text not null,
    publishDate timestamp not null
);

CREATE TABLE IF NOT EXISTS genreForGames (
    genreId int,
    gameId int,
    FOREIGN KEY(genreId) REFERENCES genres(id) ON DELETE CASCADE,
    FOREIGN KEY(gameId) REFERENCES games(id) ON DELETE CASCADE,
    PRIMARY KEY(genreId, gameId)
);

CREATE TABLE IF NOT EXISTS reviews(
    id SERIAL PRIMARY KEY,
    authorId INT NOT NULL,
    gameId INT NOT NULL,
    title VARCHAR(100) not null,
    content text not null,
    createdDate DATE not null,
    rating int not null check (rating > 0 and rating <= 10),
    FOREIGN KEY(authorId) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(gameId) REFERENCES games(id) ON DELETE CASCADE
);

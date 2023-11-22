import {Difficulty, Platform} from "../shared.enums";
import {GameResponse, GameResponseLinks} from "../shared.responses";


export class GameLinks {
  self: string;
  reviews: string;
  userReview?: string;
  addToFavoriteGames?: string;
  deleteFromFavoriteGames?: string;
  image: string;
  genres: string;

  constructor(self: string, reviews: string, image: string, genres: string, userReview?: string, addToFavoriteGames?:
    string, deleteFromFavoriteGames?: string) {
    this.self = self;
    this.reviews = reviews;
    this.userReview = userReview;
    this.addToFavoriteGames = addToFavoriteGames;
    this.deleteFromFavoriteGames = deleteFromFavoriteGames;
    this.image = image;
    this.genres = genres;
  }

  static fromResponse({
                        self,
                        reviews,
                        userReview,
                        addToFavoriteGames,
                        deleteFromFavoriteGames,
                        image,
                        genres
                      }: GameResponseLinks): GameLinks {
    return new GameLinks(self,
      reviews,
      image,
      genres,
      userReview,
      addToFavoriteGames,
      deleteFromFavoriteGames);
  }
}

export class Game {
  id: number;
  name: string;
  description: string;
  developer: string;
  publisher: string;
  publishDate: Date;
  ratingSum: number;
  reviewCount: number;
  isFavourite: boolean;
  averageDifficulty?: Difficulty;
  averagePlatform?: Platform;
  averageGameTime?: number;
  replayability?: number;
  completability?: number;
  links: GameLinks;

  constructor(id: number,
              name: string,
              description: string,
              developer: string,
              publisher: string,
              publishDate: Date,
              ratingSum: number,
              reviewCount: number,
              isFavourite: boolean,
              links: GameLinks,
              averageDifficulty?: Difficulty,
              averagePlatform?: Platform,
              averageGameTime?: number,
              replayability?: number,
              completability?: number) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.developer = developer;
    this.publisher = publisher;
    this.publishDate = publishDate;
    this.ratingSum = ratingSum;
    this.reviewCount = reviewCount;
    this.isFavourite = isFavourite;
    this.links = links;
    this.averageDifficulty = averageDifficulty;
    this.averagePlatform = averagePlatform;
    this.averageGameTime = averageGameTime;
    this.replayability = replayability;
    this.completability = completability;
  }

  static fromResponse(response: GameResponse): Game {
    return new Game(
      response.id,
      response.name,
      response.description,
      response.developer,
      response.publisher,
      new Date(response.publishDate),
      response.ratingSum,
      response.reviewCount,
      response.isFavorite,
      GameLinks.fromResponse(response.links),
      response.averageDifficulty,
      response.platform,
      response.averageGameTime,
      response.replayability,
      response.completability
    );
  }
}

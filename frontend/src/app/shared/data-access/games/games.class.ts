import {Difficulty, Platform} from "../shared.enums";
import {GameResponse} from "../shared.responses";

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


  constructor(id: number,
              name: string,
              description: string,
              developer: string,
              publisher: string,
              publishDate: Date,
              ratingSum: number,
              reviewCount: number,
              isFavourite: boolean,
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
      response.averageDifficulty,
      response.platform,
      response.averageGameTime,
      response.replayability,
      response.completability
    );
  }
}

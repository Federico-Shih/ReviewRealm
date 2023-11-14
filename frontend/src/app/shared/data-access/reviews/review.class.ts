import {Difficulty, Platform} from "../enums";
import {ReviewLinksResponse, ReviewResponse} from "../responses";

export class ReviewLinks {
  feedback: string;
  game: string;
  author: string;
  self: string;

  private constructor(feedback: string, game: string, author: string, self: string) {
    this.feedback = feedback;
    this.game = game;
    this.author = author;
    this.self = self;
  }

  static fromResponse({feedback, game, author, self}: ReviewLinksResponse): ReviewLinks {
    return new ReviewLinks(feedback, game, author, self);
  }
}

export class Review {
  id: number;
  title: string;
  content: string;
  rating: number;
  authorId: number;
  gameId: number;
  difficulty: Difficulty;
  gameLength: number;
  platform: Platform;
  likes: number;
  dislikes: number;
  completed: boolean;
  replayable: boolean;
  links: ReviewLinks;


  private constructor(id: number,
                      title: string,
                      content: string,
                      rating: number,
                      authorId: number,
                      gameId: number,
                      difficulty: Difficulty,
                      gameLength: number,
                      platform: Platform,
                      likes: number,
                      dislikes: number,
                      completed: boolean,
                      replayable: boolean,
                      links: ReviewLinks) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.rating = rating;
    this.authorId = authorId;
    this.gameId = gameId;
    this.difficulty = difficulty;
    this.gameLength = gameLength;
    this.platform = platform;
    this.likes = likes;
    this.dislikes = dislikes;
    this.completed = completed;
    this.replayable = replayable;
    this.links = links;
  }

  static fromResponse({
                        id,
                        title,
                        content,
                        rating,
                        authorId,
                        gameId,
                        difficulty,
                        gameLength,
                        platform,
                        likes,
                        dislikes,
                        completed,
                        replayable,
                        links
                      }: ReviewResponse): Review {
    return new Review(id,
      title,
      content,
      rating,
      authorId,
      gameId,
      difficulty as Difficulty,
      gameLength,
      platform as Platform,
      likes,
      dislikes,
      completed,
      replayable,
      ReviewLinks.fromResponse(links));
  }
}

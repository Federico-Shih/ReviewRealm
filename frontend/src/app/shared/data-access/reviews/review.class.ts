import {Difficulty, Platform} from "../shared.enums";
import {ReviewFeedbackResponse, ReviewLinksResponse, ReviewResponse} from "../shared.responses";
import {Game} from "../games/games.class";
import {User} from "../users/users.class";

export class ReviewLinks {
  feedback?: string;
  giveFeedback?: string;
  report?: string;
  game: string;
  author: string;
  self: string;

  private constructor(game: string, author: string, self: string, feedback?: string, giveFeedback?: string, report?: string) {
    this.feedback = feedback;
    this.game = game;
    this.author = author;
    this.self = self;
    this.giveFeedback = giveFeedback;
    this.report = report;
  }

  static fromResponse({feedback, game, author, self, giveFeedback, report}: ReviewLinksResponse): ReviewLinks {
    return new ReviewLinks(game, author, self, feedback, giveFeedback, report);
  }
}

type GameLength = {
    units: string;
    value: number;
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
  created: Date;
  links: ReviewLinks;

  // Debido a que no si o si te queres pedir el juego y autor, siempre chequear si es nulo
  game?: Game;
  author?: User;


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
                      created: string,
                      links: ReviewLinks, game?: Game, author?: User) {
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
    this.game = game;
    this.author = author;
    this.created = new Date(created);
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
                        links,
                        created
                      }: ReviewResponse, game: Game, user: User): Review {
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
      created,
      ReviewLinks.fromResponse(links),
      game,
      user
    );
  }

  getPopularity(): number {
      return this.likes - this.dislikes;
  }

  getGameLengthInUnits(): GameLength {
      if (this.gameLength === null) {
          return {
              units: '',
              value: 0
          }
      }
      let ans: GameLength;
      if (this.gameLength > 3600) {
          ans = {
              units: 'time.hours',
              value: this.gameLength / 3600.0
          }
      } else {
          ans = {
              units: 'time.minutes',
              value: this.gameLength / 60.0
          }
      }
      return ans;
  }
}

export enum FeedbackType {
    LIKE = "LIKE",
    DISLIKE = "DISLIKE"
}

export class Feedback {
    feedback: FeedbackType | null;

    isLike(): boolean {
        return this.feedback === FeedbackType.LIKE;
    }

    isDislike(): boolean {
        return this.feedback === FeedbackType.DISLIKE;
    }

    constructor(feedback: FeedbackType | null) {
        this.feedback = feedback;
    }

    static fromResponse(reviewFeedbackDto: ReviewFeedbackResponse): Feedback {
        return new Feedback(reviewFeedbackDto.feedbackType)
    }
}

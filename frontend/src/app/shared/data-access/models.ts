import {Difficulty, Platform} from "./enums";
import {JwtPayload} from "jwt-decode";


export interface Game {
  id: number,
  name: string;
  description: string;
  developer: string;
  publisher: string;
  publishDate: Date;
  ratingSum: number;
  reviewCount: number;
  isFavourite: boolean;
  averageDifficulty: Difficulty;
  averagePlatform: Platform;
  averageGameTime: number;
  replayability: number;
  completability: number;
}

export type PaginatedLinks = {
  self: string;
  next?: string;
  prev?: string;
}

export type Paginated<T> = {
  content: T[];
  totalPages: number;
  links: PaginatedLinks;
}

// Sucede cuando error 400 y es solo un valor
export type ExceptionResponse = {
  message: string;
}

// Sucede cuando error 400 y es multiples valores
export type ValidationResponse = {
  message: string;
  property: string;
  value: unknown;
}

export class RequestError {
  status: number;
  exceptions: ValidationResponse[] | ExceptionResponse | null;

  constructor(status: number, exceptions: ValidationResponse[] | ExceptionResponse | null) {
    this.status = status;
    this.exceptions = exceptions;
  }
}

export type UserJwtPayload = {
  username: string;
  email: string;
  id: number;
} & JwtPayload;

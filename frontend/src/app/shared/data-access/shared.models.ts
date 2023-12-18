import { JwtPayload } from 'jwt-decode';

export type PaginatedLinks = {
  self: string;
  next?: string;
  prev?: string;
};

export type Paginated<T> = {
  content: T[];
  totalPages: number;
  totalElements: number;
  links: PaginatedLinks;
};

// Sucede cuando error 400 y es solo un valor
export type ExceptionResponse = {
  message: string;
};

// Sucede cuando error 400 y es multiples valores
export type ValidationResponse = {
  message: string;
  property: string;
  value: unknown;
};

export class ValidationError {
  status: number;
  exceptions: ValidationResponse[] | null;

  constructor(status: number, exceptions: ValidationResponse[] | null) {
    this.status = status;
    this.exceptions = exceptions;
  }
}

export class RequestError {
  exceptions: ExceptionResponse | null;
  status: number;

  constructor(status: number, exceptions: ExceptionResponse | null) {
    this.status = status;
    this.exceptions = exceptions;
  }
}

export type UserJwtPayload = {
  username: string;
  email: string;
  id: number;
} & JwtPayload;

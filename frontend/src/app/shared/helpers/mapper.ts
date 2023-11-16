import {HttpResponse} from "@angular/common/http";
import {ExceptionResponse, Paginated, RequestError, ValidationResponse} from "../data-access/models";
import {throwError} from "rxjs";

const TOTAL_PAGES_HEADER = 'X-Reviewrealm-TotalPages';

export const responseMapper = <T, K>(fromResponse: (param: T) => K) => (httpResponse: HttpResponse<T>): K => {
  const reviewResponse = httpResponse.body as T;
  return fromResponse(reviewResponse);
}
const linkRegex = new RegExp(/<(.*)>/);
const linkNameRegex = new RegExp(/rel="(.*)"/);

// Separa header de Link en un mapa de nombre de link a url
const splitLinks = (links: string): Map<string, string> => {
  const map = new Map<string, string>();
  links.split(',').forEach((link) => {
    // link de formato <https://api.github.com/repos?page=3&per_page=100>; rel="next"
    const url = link.match(linkRegex);
    const name = link.match(linkNameRegex);
    if (name === null || url === null) return;
    map.set(name[1], url[1]);
  });
  return map;
}

export const paginatedResponseMapper = <T, K>(fromResponse: (param: T) => K) => (httpResponse: HttpResponse<T[]>): Paginated<K> => {
  const paginatedResponse = httpResponse.body as T[];
  const headers = httpResponse.headers;
  const pagesHeader = headers.get(TOTAL_PAGES_HEADER);
  const totalPages = pagesHeader !== null ? parseInt(pagesHeader) : 0;
  const links = splitLinks(headers.get('Link') || "");

  return {
    content: paginatedResponse.map(fromResponse),
    totalPages,
    links: {
      self: links.get('self') || "",
      next: links.get('next'),
      prev: links.get('prev')
    }
  };
}

export const exceptionMapper = (error: HttpResponse<ValidationResponse[] | ExceptionResponse>) => {
  return throwError(() => (new RequestError(error.status, error.body)));
}

export const validationExceptionMapper = (errorCode: number, error: ValidationResponse[]) => {
  return throwError(() => (new RequestError(errorCode, error)));
}

export const customExceptionMapper = (errorCode: number, message: string) => {
  return throwError(() => (new RequestError(errorCode, {message})));
}

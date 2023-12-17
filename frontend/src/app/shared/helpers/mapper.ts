import {HttpResponse} from "@angular/common/http";
import {throwError} from "rxjs";
import {
  ExceptionResponse,
  Paginated,
  RequestError,
  ValidationError,
  ValidationResponse
} from "../data-access/shared.models";

const TOTAL_PAGES_HEADER = 'X-Reviewrealm-TotalPages';
const TOTAL_ELEMENTS_HEADER = 'X-Reviewrealm-TotalElements';

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

export const arrayResponseMapper = <T, K>(fromResponse: (param: T) => K) => (httpResponse: HttpResponse<T[]>): K[] => {
  const reviewResponse = httpResponse.body as T[];
  console.log(reviewResponse)
  return reviewResponse.map(fromResponse);
}

export const paginatedResponseMapper = <T, K>(fromResponse: (param: T) => K) => (httpResponse: HttpResponse<T[]>): Paginated<K> => {
  const paginatedResponse = httpResponse.status === 204 ? [] : httpResponse.body as T[];
  const headers = httpResponse.headers;
  const pagesHeader = headers.get(TOTAL_PAGES_HEADER);
  const elementsHeader = headers.get(TOTAL_ELEMENTS_HEADER);
  const totalPages = pagesHeader !== null ? parseInt(pagesHeader) : 0;
  const totalElements = elementsHeader !== null ? parseInt(elementsHeader) : 0;
  const links = splitLinks(headers.get('Link') || "");

  return {
    content: paginatedResponse.map(fromResponse),
    totalPages,
    totalElements,
    links: {
      self: links.get('self') || "",
      next: links.get('next'),
      prev: links.get('prev')
    }
  };
}

export const paginatedObjectMapper = <T>(httpResponse: HttpResponse<unknown>, objectArray: T[]): Paginated<T> => {
  const paginatedResponse = httpResponse.status === 204 ? [] : objectArray as T[];
  const headers = httpResponse.headers;
  const pagesHeader = headers.get(TOTAL_PAGES_HEADER);
  const elementsHeader = headers.get(TOTAL_ELEMENTS_HEADER);
  const totalPages = pagesHeader !== null ? parseInt(pagesHeader) : 0;
  const totalElements = elementsHeader !== null ? parseInt(elementsHeader) : 0;
  const links = splitLinks(headers.get('Link') || "");

  return {
    content: paginatedResponse,
    totalPages,
    totalElements,
    links: {
      self: links.get('self') || "",
      next: links.get('next'),
      prev: links.get('prev')
    }
  };
}

export const emptyResponseMapper = <K>() => (httpResponse: HttpResponse<unknown>): Paginated<K> => {
  return {
    content: [],
    totalPages: 0,
    totalElements: 0,
    links: {
      self: "",
    }
  }
}

export const exceptionMapper = (error: HttpResponse<ExceptionResponse>) => {
  return throwError(() => (new RequestError(error.status, error.body)));
}

export const validationExceptionMapper = (errorCode: number, error: ValidationResponse[]) => {
  return throwError(() => (new ValidationError(errorCode, error)));
}

export const customExceptionMapper = (errorCode: number, message: string) => {
  return throwError(() => (new RequestError(errorCode, {message})));
}

/*
Recibe un objeto y lo convierte en un query string de esta forma:
{a: 1, b: 2} -> ?a=1&b=2
{a: [1, 2]} -> ?a=1&a=2
 */

export const queryMapper = <T extends Record<string, unknown>>(query: T, url: string): string => {
  if (Object.keys(query).length === 0) return '';
  const queryParams = new URLSearchParams();
  Object.entries(query).forEach(([key, value]) => {
    if (Array.isArray(value)) {
      value.forEach((item) => queryParams.append(key, item.toString()));
      return;
    } else if (value === undefined) {
      return;
    } else {
      // @ts-ignore
      queryParams.set(key, value.toString());
    }
  });

  return (url.indexOf('?') === -1 ? '?' : '&') + queryParams.toString();
}

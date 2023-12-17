import {
  arrayResponseMapper,
  exceptionMapper,
  paginatedObjectMapper,
  queryMapper,
  responseMapper,
  splitLinks,
  TOTAL_PAGES_HEADER,
  validationExceptionMapper,
} from './mapper';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import {
  ExceptionResponse,
  RequestError,
  ValidationError,
  ValidationResponse,
} from '../data-access/shared.models';

describe('Mapper tests', () => {
  describe('responseMapper given an http response and a mapper', () => {
    it('should apply map function', () => {
      const createdMapper = responseMapper((x: number) => x + 1);
      expect(createdMapper(new HttpResponse<number>({ body: 1 }))).toEqual(2);
    });
  });

  describe('arrayResponseMapper given an http response and a mapper', () => {
    it('should apply map function to each element', () => {
      const createdMapper = arrayResponseMapper((x: number) => x + 1);
      expect(
        createdMapper(new HttpResponse<number[]>({ body: [1, 2, 3] }))
      ).toEqual([2, 3, 4]);
    });
  });

  describe('split links', () => {
    it('given valid link format should return it split', () => {
      const links =
        '<https://api.github.com/repos?page=3&per_page=100>; rel="next", <https://api.github.com/repos?page=50&per_page=100>; rel="last"';
      const map = new Map<string, string>();
      map.set('next', 'https://api.github.com/repos?page=3&per_page=100');
      map.set('last', 'https://api.github.com/repos?page=50&per_page=100');
      expect(splitLinks(links)).toEqual(map);
    });

    it('given malformed link format should return partially valid map', () => {
      const links =
        'https://api.github.com/repos?page=3&per_page=100>; rel="next", <https://api.github.com/repos?page=50&per_page=100>; rel="last"';
      const map = new Map<string, string>();
      map.set('last', 'https://api.github.com/repos?page=50&per_page=100');
      expect(splitLinks(links)).toEqual(map);
    });

    it('given empty link format should return empty map', () => {
      const links = '';
      const map = new Map<string, string>();
      expect(splitLinks(links)).toEqual(map);
    });
  });

  describe('paginatedObjectMapper', () => {
    it('given a 204 response should return a paginated with empty', () => {
      const response = new HttpResponse({ status: 204 });
      const result = paginatedObjectMapper(response, []);
      expect(result.content.length).toEqual(0);
      expect(result.totalPages).toEqual(0);
      expect(result.links).toEqual({
        self: '',
        next: undefined,
        prev: undefined,
      });
    });

    it('given a 200 response should return a paginated with content', () => {
      const links =
        '<https://api.github.com/repos?page=3&per_page=100>; rel="next", <https://api.github.com/repos?page=50&per_page=100>; rel="prev"';

      let httpHeaders = new HttpHeaders({
        Link: links,
        [TOTAL_PAGES_HEADER]: '10',
      });

      httpHeaders = httpHeaders.set('Link', links);
      httpHeaders = httpHeaders.set(TOTAL_PAGES_HEADER, '10');
      const response = new HttpResponse({ status: 200, headers: httpHeaders });
      const result = paginatedObjectMapper(response, [1, 2, 3]);
      expect(result.content).toEqual([1, 2, 3]);
      expect(result.totalPages).toEqual(10);

      expect(result.links).toEqual({
        self: '',
        next: 'https://api.github.com/repos?page=3&per_page=100',
        prev: 'https://api.github.com/repos?page=50&per_page=100',
      });
    });
  });

  describe('exceptionMapper', () => {
    describe('given a 400 response', () => {
      it('should return a RequestError', () => {
        const response = new HttpResponse<ExceptionResponse>({
          status: 400,
          body: {
            message: 'message',
          },
        });
        const result = exceptionMapper(response);
        result.subscribe({
          error: error => {
            console.log(error);
            expect(error).toBeInstanceOf(RequestError);
            expect(error.exceptions.message).toEqual('message');
          },
        });
      });
    });
  });

  describe('validationExceptionMapper', () => {
    describe('given a 400 response', () => {
      it('should return a ValidationError', () => {
        const response = new HttpResponse<ValidationResponse[]>({
          status: 400,
          body: [
            {
              message: 'message',
              property: 'property',
              value: 'value',
            },
          ],
        });
        if (response.body === null) return;
        const result = validationExceptionMapper(
          response.status,
          response.body
        );
        result.subscribe({
          error: error => {
            expect(error).toBeInstanceOf(ValidationError);
            expect(error.exceptions[0].message).toEqual('message');
          },
        });
      });
    });
  });

  describe('queryMapper', () => {
    it('given a query object should return a query string', () => {
      const query = {
        page: 1,
        per_page: 10,
      };
      const result = queryMapper(query, '');
      expect(result).toEqual('?page=1&per_page=10');
    });

    it('given a query object with array should return a query string repeated', () => {
      const query = {
        filter: [1, 2, 3],
      };
      const result = queryMapper(query, '');
      expect(result).toEqual('?filter=1&filter=2&filter=3');
    });

    it('given an undefined value in the record, ignore that value', () => {
      const query = {
        filter: [1, 2, 3],
        page: undefined,
        per_page: null,
      };
      const result = queryMapper(query, '');
      expect(result).toEqual('?filter=1&filter=2&filter=3');
    });

    it('given an empty record, return empty string', () => {
      const query = {};
      const result = queryMapper(query, '');
      expect(result).toEqual('');
    });

    it('given an already existing url with filter, return with appended query', () => {
      const query = {
        filter: [1, 2, 3],
      };
      const result = queryMapper(query, 'https://api.github.com/repos?');
      expect(result).toEqual('&filter=1&filter=2&filter=3');
    });
  });
});

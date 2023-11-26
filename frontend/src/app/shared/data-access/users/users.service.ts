import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {
  customExceptionMapper,
  exceptionMapper,
  paginatedObjectMapper,
  queryMapper,
  responseMapper,
  validationExceptionMapper
} from "../../helpers/mapper";
import {User} from "./users.class";
import {catchError, forkJoin, map, mergeMap, Observable, of} from "rxjs";
import {UserResponse} from "../shared.responses";
import {UserCreateDto, UserMediaTypes, UserSearchDto} from "./users.dtos";
import {Paginated, ValidationResponse} from "../shared.models";
import {EnumsService} from "../enums/enums.service";

@Injectable()
export class UsersService {
  constructor(private http: HttpClient, private genreService: EnumsService) {
  }

  getUserById(url: string): Observable<User> {
    return this.http.get<UserResponse>(url, {
      observe: "response",
      responseType: "json"
    }).pipe(catchError(exceptionMapper)).pipe(mergeMap((response) => {
      const user = response.body!!;
      return forkJoin({
        preferences: this.genreService.getGenres(user.links.preferences)
      }).pipe(map(({preferences}) => User.fromResponse(user, preferences)));
    }));
  }

  // map(responseMapper(User.fromResponse))

  getUsers(url: string, searchQuery: UserSearchDto): Observable<Paginated<User>> {
    return this.http.get<UserResponse[]>(url + queryMapper(searchQuery, url), {
      observe: "response",
      responseType: "json",
    }).pipe(catchError(exceptionMapper))
      .pipe(mergeMap((response) => {
        if (response.body === null || response.status === 202) return of({
          content: [],
          totalPages: 0,
          links: {self: ""}
        });
        return forkJoin(response.body.map((user) => forkJoin({
            preferences: this.genreService.getGenres(user.links.preferences)
          }).pipe(
            map(({preferences}) =>
              User.fromResponse(user, preferences))
          )
        )).pipe(map(users => paginatedObjectMapper(response, users)))
      }));
  }

  createUser(url: string, userCreateDto: UserCreateDto): Observable<User | never> {
    return this.http.post<User>(url, userCreateDto, {
      observe: "response",
      responseType: "json",
      headers: {
        'Content-Type': UserMediaTypes.CREATEUSER
      }
    }).pipe(catchError((err) => {
      if (err instanceof HttpErrorResponse && err.status === 400 && err.error?.length > 0)
        return validationExceptionMapper(err.status as number, err.error as ValidationResponse[]);
      return customExceptionMapper(500, 'Unknown error');
    }), map(
      responseMapper(User.fromResponse)
    ));
  }
}

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
import {CredentialsDto, UserCreateDto, UserMediaTypes, UserPatchDto, UserSearchDto} from "./users.dtos";
import {Paginated, ValidationResponse} from "../shared.models";
import {EnumsService} from "../enums/enums.service";
import {AuthenticationService} from "../authentication/authentication.service";

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
          totalElements: 0,
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

  changePasswordRequest(email: string): Observable<boolean> {
    return this.http.post<void>(AuthenticationService.AUTHENTICATION_ENDPOINT, {
      email
    }, {
      headers: {
        'Content-Type': UserMediaTypes.CHANGE_PASSWORD
      }
    }).pipe(map(() => true), catchError(() => of(false)));
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

  patchUser(url: string, userPatchDto: UserPatchDto, credentials?: CredentialsDto): Observable<void | never> {
    let headers: Record<string, string> = {};
    if (credentials) {
      headers['Authorization'] = `Basic ${btoa(`${credentials?.email}:${credentials?.token}`)}`
    }
    return this.http.patch<void>(url, userPatchDto, {
      headers,
      observe: "response",
      responseType: "json",
    }).pipe(catchError((err) => {
      if (err instanceof HttpErrorResponse) {
        if (err.status === 400) return validationExceptionMapper(err.status as number, err.error as ValidationResponse[]);
        if (err.status === 401) return customExceptionMapper(401, err.error);
        if (err.status === 403) return customExceptionMapper(403, err.error);
      }
      return customExceptionMapper(500, 'Unknown error');
    })).pipe(map(() => {
    }));
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import {
  customExceptionMapper,
  exceptionMapper,
  paginatedObjectMapper,
  queryMapper,
  responseMapper,
  validationExceptionMapper,
} from '../../helpers/mapper';
import { User } from './users.class';
import { catchError, forkJoin, map, mergeMap, Observable, of } from 'rxjs';
import { UserResponse } from '../shared.responses';
import {
    AvatarDto,
    FavoriteGameCreateDto,
    GenresDto,
    CredentialsDto,
    UserCreateDto,
    UserMediaTypes,
    UserPatchDto,
    UserSearchDto,
    NotificationsDto, FollowDto,
} from './users.dtos';
import { Paginated, ValidationResponse } from '../shared.models';
import { EnumsService } from '../enums/enums.service';
import { Genre } from '../enums/enums.class';

import { AuthenticationService } from '../authentication/authentication.service';

@Injectable()
export class UsersService {
  constructor(
    private http: HttpClient,
    private genreService: EnumsService
  ) {}

  getUserById(url: string): Observable<User> {
    return this.http
      .get<UserResponse>(url, {
        observe: 'response',
        responseType: 'json',
      })
      .pipe(catchError(exceptionMapper))
      .pipe(
        mergeMap(response => {
          if (!response.body) {
            return customExceptionMapper(500, 'Unknown error');
          }
          const user = response.body;
          return forkJoin({
            preferences: this.genreService.getGenres(user.links.preferences),
          }).pipe(
            map(({ preferences }) => User.fromResponse(user, preferences))
          );
        })
      );
  }

  // map(responseMapper(User.fromResponse))

  getUsers(
    url: string,
    searchQuery?: UserSearchDto
  ): Observable<Paginated<User>> {
    return this.http
      .get<UserResponse[]>(url + queryMapper(searchQuery ?? {}, url), {
        observe: 'response',
        responseType: 'json',
      })
      .pipe(catchError(exceptionMapper))
      .pipe(
        mergeMap(response => {
          if (response.body === null || response.status === 202)
            return of({
              content: [],
              totalPages: 0,
              totalElements: 0,
              links: { self: '' },
            });
          return forkJoin(
            response.body.map(user =>
              forkJoin({
                preferences: this.genreService.getGenres(
                  user.links.preferences
                ),
              }).pipe(
                map(({ preferences }) => User.fromResponse(user, preferences))
              )
            )
          ).pipe(map(users => paginatedObjectMapper(response, users)));
        })
      );
  }

  changePasswordRequest(email: string): Observable<boolean> {
    return this.http
      .post<void>(
        AuthenticationService.AUTHENTICATION_ENDPOINT,
        {
          email,
        },
        {
          headers: {
            'Content-Type': UserMediaTypes.CHANGE_PASSWORD,
          },
        }
      )
      .pipe(
        map(() => true),
        catchError(() => of(false))
      );
  }

  createUser(
    url: string,
    userCreateDto: UserCreateDto
  ): Observable<User | never> {
    return this.http
      .post<User>(url, userCreateDto, {
        observe: 'response',
        responseType: 'json',
        headers: {
          'Content-Type': UserMediaTypes.CREATEUSER,
        },
      })
      .pipe(
        catchError(err => {
          if (
            err instanceof HttpErrorResponse &&
            err.status === 400 &&
            err.error?.length > 0
          )
            return validationExceptionMapper(
              err.status as number,
              err.error as ValidationResponse[]
            );
          if (err instanceof HttpErrorResponse && err.status === 409) {
            return customExceptionMapper(409, err.error.message);
          }
          return customExceptionMapper(500, 'Unknown error');
        }),
        map(responseMapper(User.fromResponse))
      );
  }

  editUserGenres(url: string, genres: GenresDto) {
    return this.http
      .patch<Genre[]>(url, genres, {
        responseType: 'json',
        observe: 'response',
        headers: {
          'Content-Type': UserMediaTypes.EDIT_USER,
        }
      })
      .pipe(catchError(exceptionMapper))
      .pipe(map(() => true));
  }

  deleteFavoriteGame(url: string): Observable<unknown> {
    return this.http
      .delete(url, {
        observe: 'response',
        responseType: 'json',
      })
      .pipe(catchError(exceptionMapper));
  }

  addFavoriteGame(
    url: string,
    game: FavoriteGameCreateDto
  ): Observable<unknown> {
    return this.http
      .post(url, game, {
        observe: 'response',
        responseType: 'json',
        headers: {
          'Content-Type': UserMediaTypes.CREATE_FAVORITE_GAME,
        },
      })
      .pipe(catchError(exceptionMapper));
  }
  editUserAvatar(url: string, avatar: AvatarDto) {
    return this.http
      .patch<AvatarDto>(url, avatar, {
        responseType: 'json',
        observe: 'response',
        headers: {
          'Content-Type': UserMediaTypes.EDIT_USER,
        }
      })
      .pipe(catchError(exceptionMapper));
  }

  editUserNotifications(url: string, notifs: NotificationsDto) {
    return this.http
      .put<boolean>(url, notifs, {
        responseType: 'json',
        observe: 'response',
        headers: {
          'Content-Type': 'application/vnd.notifications-form.v1+json',
        },
      })
      .pipe(catchError(exceptionMapper))
      .pipe(map(() => true));
  }

  getIfFollowing(url: string) {
      return this.http
        .get<boolean>(url, {
            responseType: 'json',
            observe: 'response',
        }).pipe(map(()=> true)).pipe(catchError(map(() => false)))
  }

  follow(url: string, followDto: FollowDto) {
      return this.http
          .post<boolean>(url, followDto,{
              responseType: 'json',
              observe: 'response',
              headers: {
                  'Content-Type': 'application/vnd.following-form.v1+json',
              },
          }).pipe(catchError(map(() => false))).pipe(map(()=> true))
  }

  unfollow(url: string) {
      return this.http
          .delete<boolean>(url,{
              responseType: 'json',
              observe: 'response',
          }).pipe(catchError(map(() => false))).pipe(map(()=> true))
  }

  patchUser(
    url: string,
    userPatchDto: UserPatchDto,
    credentials?: CredentialsDto
  ): Observable<unknown | never> {
    const headers: Record<string, string> = {};
    if (credentials) {
      headers['Authorization'] = `Basic ${btoa(
        `${credentials?.email}:${credentials?.token}`
      )}`;
    }
    return this.http
      .patch<void>(url, userPatchDto, {
        headers,
        observe: 'response',
        responseType: 'json',
      })
      .pipe(
        catchError(err => {
          if (err instanceof HttpErrorResponse) {
            if (err.status === 400)
              return validationExceptionMapper(
                err.status as number,
                err.error as ValidationResponse[]
              );
            if (err.status === 401)
              return customExceptionMapper(401, err.error);
            if (err.status === 403)
              return customExceptionMapper(403, err.error);
          }
          return customExceptionMapper(500, 'Unknown error');
        })
      );
  }
}

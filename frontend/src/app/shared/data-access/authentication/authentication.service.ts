import {Injectable} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {catchError, map, Observable, of, ReplaySubject, switchMap} from "rxjs";
import {User} from "../users/users.class";
import {AuthenticationDto} from "./authentication.dtos";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {customExceptionMapper} from "../../helpers/mapper";
import {jwtDecode} from "jwt-decode";
import {UserJwtPayload} from "../shared.models";
import {UsersService} from "../users/users.service";

export const AUTHORIZATION_TOKEN_LABEL = 'X-RR-AUTHORIZATION';
export const REFRESH_TOKEN_LABEL = 'X-RR-REFRESH';

@Injectable({providedIn: 'root'})
export class AuthenticationService {
  static AUTHENTICATION_ENDPOINT = environment.API_ENDPOINT + '/users';
  private loggedUser$ = new ReplaySubject<User | null>(1);

  constructor(private readonly http: HttpClient, private readonly userService: UsersService) {
    const token = localStorage.getItem(AUTHORIZATION_TOKEN_LABEL)
    console.log(token);
    if (token) {
      this.isValidSession().pipe((catchError((err) => {
        this.logout();
        return of(null);
      }))).subscribe((user) => {
        this.loggedUser$.next(user);
      });
    } else {
      this.loggedUser$.next(null);
    }
  }

  login({username, password}: AuthenticationDto): Observable<User | never> {
    return this.http.get<unknown>(AuthenticationService.AUTHENTICATION_ENDPOINT, {
      headers: {
        'Authorization': 'Basic ' + btoa(`${username}:${password}`),
      },
      observe: "response",
      responseType: "json"
    }).pipe(switchMap(({headers}) => {
      return this.getAuthenticatedUserFromHeader(headers);
    }));
  }

  isValidSession(): Observable<User | never> {
    return this.http.get<unknown>(AuthenticationService.AUTHENTICATION_ENDPOINT, {
      observe: "response",
      responseType: "json"
    }).pipe(switchMap(({headers}) => {
      return this.getAuthenticatedUserFromHeader(headers);
    }));
  }

  enableUser(email: string, token: string) {
    return this.http.get(AuthenticationService.AUTHENTICATION_ENDPOINT, {
      headers: {
        'Authorization': 'Basic ' + btoa(`${email}:${token}`),
      },
      observe: "response",
      responseType: "json",
    }).pipe(switchMap(({headers}) => {
      return this.getAuthenticatedUserFromHeader(headers);
    }))
  }

  logout() {
    localStorage.removeItem(AUTHORIZATION_TOKEN_LABEL);
    localStorage.removeItem(REFRESH_TOKEN_LABEL);
    this.loggedUser$.next(null);
  }

  getLoggedUser(): Observable<User | null> {
    return this.loggedUser$;
  }

  /*
    If Token is invalid, returns ExceptionResponse with 401.
   */
  private getAuthenticatedUserFromHeader(headers: HttpHeaders): Observable<User | never> {
    const authorization = headers.get('Authorization');
    if (!authorization) {
      return customExceptionMapper(401, 'Unauthorized');
    }
    const token = authorization.split(' ')[1];
    localStorage.setItem(AUTHORIZATION_TOKEN_LABEL, token);

    const refreshToken = headers.get('X-Refresh')?.split(' ')[1];
    localStorage.setItem(REFRESH_TOKEN_LABEL, refreshToken || '');

    const payload = jwtDecode<UserJwtPayload>(token);
    if (!payload.id) {
      return customExceptionMapper(500, 'Invalid token');
    }

    return this.userService.getUsers(AuthenticationService.AUTHENTICATION_ENDPOINT, {id: payload.id}).pipe(map(users => {
      this.loggedUser$.next(users.content[0]);
      return users.content[0];
    }));
  }
}

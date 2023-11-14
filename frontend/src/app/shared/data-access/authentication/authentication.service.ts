import {Injectable} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {BehaviorSubject, map, Observable, switchMap} from "rxjs";
import {User} from "../users/user.class";
import {AuthenticationDto} from "./authentication.dtos";
import {HttpClient} from "@angular/common/http";
import {customExceptionMapper} from "../../helpers/mapper";
import {jwtDecode} from "jwt-decode";
import {UserJwtPayload} from "../models";
import {UsersService} from "../users/users.service";

export const AUTHORIZATION_TOKEN_LABEL = 'authorizationToken';
export const REFRESH_TOKEN_LABEL = 'refreshToken';

@Injectable()
export class AuthenticationService {
  static AUTHENTICATION_ENDPOINT = environment.API_ENDPOINT + '/users';
  loggedUser$ = new BehaviorSubject<User | null>(null);

  constructor(private readonly http: HttpClient, private readonly userService: UsersService) {
  }

  login({username, password}: AuthenticationDto): Observable<User | never> {
    return this.http.get<unknown>(AuthenticationService.AUTHENTICATION_ENDPOINT, {
      headers: {
        'Authorization': 'Basic ' + btoa(`${username}:${password}`),
      },
      observe: "response",
      responseType: "json"
    }).pipe(switchMap(({headers}) => {
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
    }));
  }

  getLoggedUser(): Observable<User | null> {
    return this.loggedUser$.asObservable();
  }
}

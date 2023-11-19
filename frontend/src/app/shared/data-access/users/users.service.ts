import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {
  customExceptionMapper,
  paginatedResponseMapper, queryMapper,
  responseMapper,
  validationExceptionMapper
} from "../../helpers/mapper";
import {User} from "./users.class";
import {catchError, map, Observable} from "rxjs";
import {UserResponse} from "../shared.responses";
import {UserCreateDto, UserMediaTypes, UserSearchDto} from "./users.dtos";
import {Paginated, ValidationResponse} from "../shared.models";

@Injectable()
export class UsersService {
  constructor(private http: HttpClient) {
  }

  getUserById(url: string) {
    return this.http.get<UserResponse>(url, {
      observe: "response",
      responseType: "json"
    }).pipe(map(responseMapper(User.fromResponse)));
  }

  getUsers(url: string, searchQuery: UserSearchDto): Observable<Paginated<User>> {
    return this.http.get<UserResponse[]>(url + queryMapper(searchQuery), {
      observe: "response",
      responseType: "json",
    }).pipe(map(paginatedResponseMapper(User.fromResponse)));
  }

  createUser(url: string, userCreateDto: UserCreateDto): Observable<User | never> {
    return this.http.post<User>(url, userCreateDto, {
      observe: "response",
      responseType: "json",
      headers: {
        'Content-Type': UserMediaTypes.CREATEUSER
      }
    }).pipe(map(
      responseMapper(User.fromResponse)
    ), catchError((err) => {
      if (err instanceof HttpErrorResponse && err.status === 400 && err.error?.length > 0)
        return validationExceptionMapper(err.status as number, err.error as ValidationResponse[]);
      return customExceptionMapper(500, 'Unknown error');
    }));
  }
}

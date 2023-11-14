import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {paginatedResponseMapper, responseMapper} from "../../helpers/mapper";
import {User} from "./user.class";
import {map, Observable} from "rxjs";
import {UserResponse} from "../responses";
import {UserSearchDto} from "./users.dtos";
import {Paginated} from "../models";

@Injectable()
export class UsersService {
  constructor(private http: HttpClient) {
  }

  private static getSearchQuery(searchQuery: UserSearchDto): string {
    if (Object.keys(searchQuery).length === 0) return '';
    const {
      direction,
      email,
      followers,
      following,
      gamesPlayed,
      id,
      page,
      pageSize,
      preferences,
      search,
      sort,
      username
    } = searchQuery;
    const urlSearch = new URLSearchParams();
    if (direction) urlSearch.set('direction', direction);
    if (email) urlSearch.set('email', email);
    if (followers) urlSearch.set('followers', followers.toString());
    if (following) urlSearch.set('following', following.toString());
    if (gamesPlayed) {
      gamesPlayed.forEach((game) => urlSearch.append('gamesPlayed', game.toString()));
    }
    if (id) urlSearch.set('id', id.toString());
    if (page) urlSearch.set('page', page.toString());
    if (pageSize) urlSearch.set('pageSize', pageSize.toString());
    if (preferences) {
      preferences.forEach((preference) => urlSearch.append('preferences', preference.toString()));
    }
    if (search) urlSearch.set('search', search);
    if (sort) urlSearch.set('sort', sort);
    if (username) urlSearch.set('username', username);
    return "?" + urlSearch.toString();
  }

  getUserById(url: string) {
    return this.http.get<UserResponse>(url, {
      observe: "response",
      responseType: "json"
    }).pipe(map(responseMapper(User.fromResponse)));
  }

  getUsers(url: string, searchQuery: UserSearchDto): Observable<Paginated<User>> {
    return this.http.get<UserResponse[]>(url + UsersService.getSearchQuery(searchQuery), {
      observe: "response",
      responseType: "json",
    }).pipe(map(paginatedResponseMapper(User.fromResponse)));
  }
}

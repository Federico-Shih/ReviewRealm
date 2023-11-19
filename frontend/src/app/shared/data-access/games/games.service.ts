import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {GameSearchDto} from "./games.dtos";
import {paginatedResponseMapper, queryMapper, responseMapper} from "../../helpers/mapper";
import {map, Observable} from "rxjs";
import {Paginated} from "../shared.models";
import {Game} from "./games.class";

@Injectable()
export class GamesService {
  constructor(private readonly http: HttpClient) {

  }

  getGames(url: string, query: GameSearchDto): Observable<Paginated<Game>> {
    return this.http.get(url + queryMapper(query), {
      responseType: "json",
      observe: "response"
    }).pipe(map(paginatedResponseMapper(Game.fromResponse)));
  }

  getGame(url: string): Observable<Game> {
    return this.http.get(url, {
      responseType: "json",
      observe: "response"
    }).pipe(map(responseMapper(Game.fromResponse)));
  }
}

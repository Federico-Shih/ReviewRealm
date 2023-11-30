import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {GameExclusiveSearchDto, GameSearchDto} from "./games.dtos";
import {exceptionMapper, paginatedObjectMapper, queryMapper,} from "../../helpers/mapper";
import {catchError, filter, forkJoin, map, mergeMap, Observable, of} from "rxjs";
import {Paginated} from "../shared.models";
import {Game} from "./games.class";
import {GameResponse} from "../shared.responses";
import {EnumsService} from "../enums/enums.service";

@Injectable()
export class GamesService {
  constructor(private readonly http: HttpClient, private readonly genreService: EnumsService) {

  }

  getGames(url: string, query: GameSearchDto| GameExclusiveSearchDto): Observable<Paginated<Game>> {
    return this.http.get<GameResponse[]>(url + queryMapper(query, url), {
      responseType: "json",
      observe: "response"
    }).pipe(catchError(exceptionMapper)).pipe(
      mergeMap(
        response => {
          if (response.status === 202 || response.body === null) return of({
            content: [],
            totalPages: 0,
            links: {self: ""}
          });
          return forkJoin(
            response.body.map(game =>
              this.genreService.getGenres(game.links.genres).pipe(map((genres) => Game.fromResponse(game, genres)))
            )
          ).pipe(map(games => paginatedObjectMapper(response, games)));
        }
      )
    );
  }

  getGame(url: string): Observable<Game> {
    return this.http.get<GameResponse>(url, {
      responseType: "json",
      observe: "response"
    }).pipe(catchError(exceptionMapper))
      .pipe(filter(response => response.body !== null))
      .pipe(
        mergeMap(
          (response) => {
            const game = response.body!!;
            return this.genreService.getGenres(game.links.genres).pipe(map((genres) => Game.fromResponse(game, genres)));
          }
        )
      );
  }
}

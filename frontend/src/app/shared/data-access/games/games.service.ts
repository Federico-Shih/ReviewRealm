import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {
  GameExclusiveSearchDto, GameMediaTypes,
  GameNotReviewedBySearchDto,
  GameSearchDto, GameSubmissionHandleDto
} from "./games.dtos";
import {
  customExceptionMapper,
  exceptionMapper,
  paginatedObjectMapper,
  queryMapper,
  validationExceptionMapper,
} from "../../helpers/mapper";
import {catchError, filter, forkJoin, map, mergeMap, Observable, of, pipe, switchMap} from "rxjs";
import {Paginated, ValidationResponse} from "../shared.models";
import {Game} from "./games.class";
import {GameResponse} from "../shared.responses";
import {EnumsService} from "../enums/enums.service";

@Injectable()
export class GamesService {
  constructor(private readonly http: HttpClient, private readonly genreService: EnumsService) {

  }

  getGames(url: string, query: GameSearchDto| GameExclusiveSearchDto | GameNotReviewedBySearchDto): Observable<Paginated<Game>> {
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

  createGame(url:string,gameDto:FormData) {
    return this.http.post<boolean>(url, gameDto, {
      responseType: "json",
      observe: "response"
    }).pipe(catchError(exceptionMapper),
      map((response) => {
      return response.status === 201;
      }));
  }

  editGame(url:string,gameDto:FormData){//TODO:Error
    return this.http.patch<GameResponse>(url,gameDto,{
      responseType:"json",
      observe:"response"
    }).pipe(catchError(exceptionMapper))
      .pipe(filter(response => response.body !== null))
      .pipe(
        mergeMap(
          (response) => {
              const game = response.body!!;
              return this.genreService.getGenres(game.links.genres).pipe(map((genres) => Game.fromResponse(game, genres)));
          }
      ));
  }
  deleteGame(url:string){
    return this.http.delete<boolean>(url,{
      responseType:"json",
      observe:"response"
    }).pipe(catchError(exceptionMapper),
      map((response) => {
      return response.status === 200;
    }));
  }
  handleGameSubmission(url:string,state:GameSubmissionHandleDto):Observable<boolean>{
    return this.http.patch<boolean>(url,state,{
      responseType:"json",
      observe:"response",
      headers:{
        'Content-Type': GameMediaTypes.APPLICATION_GAME_SUGGESTION_FORM
      }
    }).pipe(catchError(exceptionMapper), map(response => response.status === 204))};
}

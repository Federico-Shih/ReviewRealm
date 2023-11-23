import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, filter, forkJoin, map, mergeMap, Observable, of} from "rxjs";
import {ReviewResponse} from "../shared.responses";
import {Review} from "./review.class";
import {exceptionMapper, paginatedObjectMapper, queryMapper,} from "../../helpers/mapper";
import {Paginated} from "../shared.models";
import {ReviewSearchDto} from "./reviews.dtos";
import {GamesService} from "../games/games.service";
import {UsersService} from "../users/users.service";

@Injectable()
export class ReviewsService {
  constructor(private http: HttpClient, private usersService: UsersService, private gamesService: GamesService) {
  }

  /*
    Popula Review con Game y User.
    Esto se realiza mediante una union de observables con operador forkJoin.
    Para el caso de muchas, se tiene que realizar un forkJoin con un forkJoin, debido a que se hace una llamada por objeto.
   */

  getReviewById(url: string): Observable<Review> {
    return this.http.get<ReviewResponse>(url, {
      observe: "response",
      responseType: "json"
    }).pipe(catchError(exceptionMapper))
      .pipe(filter(response => response.body !== null))
      .pipe(
        mergeMap((response) => {
          const review = response.body!!;
          return (forkJoin({
            game: this.gamesService.getGame(review.links.game),
            user: this.usersService.getUserById(review.links.author)
          }).pipe(map(({game, user}) => Review.fromResponse(review, game, user))));
        }),
      );
  }

  getReviews(url: string, queryDto: ReviewSearchDto): Observable<Paginated<Review>> {
    return this.http.get<ReviewResponse[]>(url + queryMapper(queryDto), {
      observe: "response",
      responseType: "json"
    }).pipe(catchError(exceptionMapper))
      .pipe(
        mergeMap(
          response => {
            if (response.status === 202 || response.body === null) return of({
              content: [],
              totalPages: 0,
              links: {self: ""}
            });
            return forkJoin(
              response.body.map(review =>
                forkJoin({
                  game: this.gamesService.getGame(review.links.game),
                  user: this.usersService.getUserById(review.links.author)
                }).pipe(
                  map(({game, user}) => Review.fromResponse(review, game, user))
                )
              )
            ).pipe(map(reviews => paginatedObjectMapper(response, reviews)));
          }
        )
      );
  }
}

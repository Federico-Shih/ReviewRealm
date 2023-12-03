import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {catchError, filter, forkJoin, map, mergeMap, Observable, of, pipe} from "rxjs";
import {ReviewFeedbackResponse, ReviewResponse} from "../shared.responses";
import {Feedback, Review} from "./review.class";
import {
    customExceptionMapper,
    exceptionMapper,
    paginatedObjectMapper,
    queryMapper, responseMapper,
    validationExceptionMapper,
} from "../../helpers/mapper";
import {Paginated, ValidationResponse} from "../shared.models";
import {ReportReviewDto, ReviewMediaTypes, ReviewSearchDto} from "./reviews.dtos";
import {GamesService} from "../games/games.service";
import {UsersService} from "../users/users.service";
import {UserCreateDto, UserMediaTypes} from "../users/users.dtos";
import {User} from "../users/users.class";
import {ReportReason} from "../shared.enums";

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
    return this.http.get<ReviewResponse[]>(url + queryMapper(queryDto, url), {
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

  getReviewFeedbackFromUser(url: string): Observable<Feedback> {
      return this.http.get<ReviewFeedbackResponse>(url, {
          observe: "response",
          responseType: "json"
      }).pipe(catchError(exceptionMapper))
          .pipe(filter(response => response.body !== null))
          .pipe(
              map((response) => Feedback.fromResponse(response.body!!))
          );
  }

  deleteReview(url: string): Observable<boolean> {
    return this.http.delete(url, {
      observe: "response",
      responseType: "json"
    }).pipe(catchError(exceptionMapper))
      .pipe(map((response) => {
        return response.status === 200;
      }));
  }

  reportReview(url: string, reviewId: number, reportReason: ReportReason) {
    const reportReviewDto: ReportReviewDto = {
      reviewId: reviewId,
      reason: reportReason
    }
    return this.http.post<boolean>(url, reportReviewDto, {
      observe: "response",
      responseType: "json",
      headers: {
        'Content-Type': ReviewMediaTypes.REPORTREVIEW
      }
    }).pipe(catchError((err) => {
      return customExceptionMapper(err.status, err.error?.message);
    }), map( x => x.status === 201
    ));
  }

  //TODO: giveFeedback() cuando esté hecho con el body

}

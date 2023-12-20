import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {
  catchError,
  filter,
  forkJoin,
  map,
  mergeMap,
  Observable,
  of,
  switchMap,
} from 'rxjs';
import {ReviewFeedbackResponse, ReviewResponse} from '../shared.responses';
import {Feedback, FeedbackType, Review} from './review.class';
import {
  customExceptionMapper,
  exceptionMapper,
  paginatedObjectMapper,
  queryMapper,
} from '../../helpers/mapper';
import {Paginated, RequestError} from '../shared.models';
import {
  ReportReviewDto, ReviewFeedbackDto,
  ReviewMediaTypes,
  ReviewSearchDto,
  ReviewSubmitDto,
} from './reviews.dtos';
import {GamesService} from '../games/games.service';
import {UsersService} from '../users/users.service';
import {ReportReason} from '../reports/reports.class';

@Injectable()
export class ReviewsService {
  constructor(
    private http: HttpClient,
    private usersService: UsersService,
    private gamesService: GamesService
  ) {
  }

  /*
    Popula Review con Game y User.
    Esto se realiza mediante una union de observables con operador forkJoin.
    Para el caso de muchas, se tiene que realizar un forkJoin con un forkJoin, debido a que se hace una llamada por objeto.
   */

  getReviewById(url: string): Observable<Review> {
    return this.http
      .get<ReviewResponse>(url, {
        observe: 'response',
        responseType: 'json',
      })
      .pipe(catchError(exceptionMapper))
      .pipe(filter(response => response.body !== null))
      .pipe(
        mergeMap(response => {
          if (!response.body)
            return customExceptionMapper(500, 'Empty body response');
          const review = response.body;
          return forkJoin({
            game: this.gamesService.getGame(review.links.game),
            user: this.usersService.getUserById(review.links.author),
          }).pipe(
            map(({game, user}) => Review.fromResponse(review, game, user))
          );
        })
      );
  }

  getReviews(
    url: string,
    queryDto?: ReviewSearchDto
  ): Observable<Paginated<Review>> {
    return this.http
      .get<ReviewResponse[]>(url + queryMapper(queryDto || {}, url), {
        observe: 'response',
        responseType: 'json',
      })
      .pipe(catchError(exceptionMapper))
      .pipe(
        mergeMap(response => {
          if (response.status === 202 || response.body === null)
            return of({
              content: [],
              totalPages: 0,
              totalElements: 0,
              links: {self: ''},
            });
          return forkJoin(
            response.body.map(review =>
              forkJoin({
                game: this.gamesService.getGame(review.links.game),
                user: this.usersService.getUserById(review.links.author),
              }).pipe(
                map(({game, user}) => Review.fromResponse(review, game, user))
              )
            )
          ).pipe(map(reviews => paginatedObjectMapper(response, reviews)));
        })
      );
  }

  getReviewFeedbackFromUser(url: string): Observable<Feedback> {
    return this.http
      .get<ReviewFeedbackResponse>(url, {
        observe: 'response',
        responseType: 'json',
      })
      .pipe(
        map(response => {
          if (!response.body)
            throw new RequestError(500, {
              message: 'Empty body response',
            });
          return Feedback.fromResponse(response.body);
        })
      )
      .pipe(catchError((error) => {
        if (error.status === 404) {
          return of(new Feedback(null));
        }
        return exceptionMapper(error);
      }));
  }

  deleteReview(url: string): Observable<boolean> {
    return this.http
      .delete(url, {
        observe: 'response',
        responseType: 'json',
      })
      .pipe(catchError(exceptionMapper))
      .pipe(
        map(response => {
          return response.status === 204;
        })
      );
  }

  reportReview(url: string, reviewId: number, reportReason: ReportReason) {
    const reportReviewDto: ReportReviewDto = {
      reviewId: reviewId,
      reason: reportReason,
    };
    return this.http
      .post<boolean>(url, reportReviewDto, {
        observe: 'response',
        responseType: 'json',
        headers: {
          'Content-Type': ReviewMediaTypes.REPORTREVIEW,
        },
      })
      .pipe(
        catchError(err => {
          return customExceptionMapper(err.status, err.error?.message);
        }),
        map(x => x.status === 201)
      );
  }

  giveFeedback(url: string, feedback: FeedbackType | null): Observable<boolean> {
    if (feedback === null) {
      return this.http.delete<boolean>(url, {
        observe: 'response',
        responseType: 'json',
      }).pipe(
        catchError(err => {
          return customExceptionMapper(err.status, err.error?.message);
        }),
        map(x => x.status === 200)
      );
    }
    const reviewFeedbackDto: ReviewFeedbackDto = {
      feedbackType: feedback,
    };
    return this.http.put<boolean>(url, reviewFeedbackDto, {
      observe: 'response',
      responseType: 'json',
      headers: {
        'Content-Type': ReviewMediaTypes.FEEDBACKREVIEW,
      },
    }).pipe(
      catchError(err => {
        return customExceptionMapper(err.status, err.error?.message);
      }),
      map(x => x.status === 200 || x.status === 201)
    );
  }

  createReview(url: string, reviewDto: ReviewSubmitDto): Observable<number> {
    return this.http
      .post<ReviewResponse>(url, reviewDto, {
        responseType: 'json',
        observe: 'response',
        headers: {
          'Content-Type': ReviewMediaTypes.CREATEREVIEW,
        },
      })
      .pipe(
        map(({body}) => {
          if (body === null)
            throw new RequestError(500, {message: 'Empty body response'});
          return body.id;
        }),
        catchError(exceptionMapper),
      );
  }

  editReview(url: string, reviewDto: ReviewSubmitDto) {
    return this.http
      .put<boolean>(url, reviewDto, {
        responseType: 'json',
        observe: 'response',
        headers: {
          'Content-Type': ReviewMediaTypes.EDITREVIEW,
        },
      })
      .pipe(
        catchError(exceptionMapper),
        switchMap(() => {
          return of(true);
        })
      );
  }
}

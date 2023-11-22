import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, map, Observable} from "rxjs";
import {ReviewResponse} from "../shared.responses";
import {Review} from "./review.class";
import {exceptionMapper, paginatedResponseMapper, queryMapper, responseMapper} from "../../helpers/mapper";
import {Paginated} from "../shared.models";
import {ReviewSearchDto} from "./reviews.dtos";

@Injectable()
export class ReviewsService {
  constructor(private http: HttpClient) {
  }

  // TODO: call games and users
  getReviewById(url: string): Observable<Review> {
    return this.http.get<ReviewResponse>(url, {
      observe: "response",
      responseType: "json"
    }).pipe(
      map(responseMapper(Review.fromResponse)),
    ).pipe(catchError(exceptionMapper));
  }

  getReviews(url: string, queryDto: ReviewSearchDto): Observable<Paginated<Review>> {
    return this.http.get<ReviewResponse[]>(url + queryMapper(queryDto), {
      observe: "response",
      responseType: "json"
    }).pipe(
      map(paginatedResponseMapper(Review.fromResponse))
    ).pipe(catchError(exceptionMapper));
  }
}

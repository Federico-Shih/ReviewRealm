import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, map, Observable} from "rxjs";
import {ReviewResponse} from "../responses";
import {Review} from "./review.class";
import {exceptionMapper, responseMapper} from "../../helpers/mapper";

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
}

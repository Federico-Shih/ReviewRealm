import {Router, CanActivateFn} from '@angular/router';
import {inject} from "@angular/core";
import {AuthenticationService} from "../data-access/authentication/authentication.service";
import {combineLatest, map, Observable, of} from "rxjs";
import {ReviewsService} from "../data-access/reviews/reviews.service";
import {environment} from "../../../environments/environment";

export const isReviewAuthorGuard: CanActivateFn = (route, state): Observable<boolean> => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);
  if (authService.getLoggedUser() === null) {
    router.navigate(['/login']);
    return of(false);
  }
  const reviewService = inject(ReviewsService);
  const reviewId = route.paramMap.get('id');
  if (reviewId === null) {
    router.navigate(['/404']);
    return of(false);
  }
  const review$ = reviewService.getReviewById(`${environment.API_ENDPOINT}/reviews/${reviewId}`);
  return combineLatest([review$, authService.getLoggedUser()]).pipe(
    map(([review, user]) => {
      const isAuthor = user !== null && review.authorId === user.id;
      if(!isAuthor)
        router.navigate(['/403']);
      return isAuthor;
    })
  );
};

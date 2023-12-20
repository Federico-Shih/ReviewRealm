import { Router, CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { AuthenticationService } from '../data-access/authentication/authentication.service';
import {catchError, combineLatest, map, Observable, of, switchMap} from 'rxjs';
import { ReviewsService } from '../data-access/reviews/reviews.service';
import { environment } from '../../../environments/environment';

export const isReviewAuthorGuard: CanActivateFn = (
  route
): Observable<boolean> => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);
  const reviewService = inject(ReviewsService);

  return authService.getLoggedUser().pipe(switchMap((user) => {
    if (user === null) {
      router.navigate(['/login']);
      return of(false);
    }

    const reviewId = route.paramMap.get('id');
    if (reviewId === null) {
      router.navigate(['/errors/not-found']);
      return of(false);
    }
    const review$ = reviewService.getReviewById(
      `${environment.API_ENDPOINT}/reviews/${reviewId}`
    );
    return combineLatest([review$, authService.getLoggedUser()]).pipe(
      map(([review, user]) => {
        const isAuthor = user !== null && review.authorId === user.id;
        if (!isAuthor) router.navigate(['/errors/forbidden']);
        return isAuthor;
      })
    ).pipe(catchError(() => {
      router.navigate(['/errors/not-found']);
      return of(false);
    }));
  }));


};

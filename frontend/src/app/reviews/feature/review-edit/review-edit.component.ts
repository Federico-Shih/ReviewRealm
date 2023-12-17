import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ReviewFormType } from '../../../shared/data-access/shared.enums';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthenticationService } from '../../../shared/data-access/authentication/authentication.service';
import { ReviewsService } from '../../../shared/data-access/reviews/reviews.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { environment } from '../../../../environments/environment';
import { Review } from '../../../shared/data-access/reviews/review.class';
import { ReviewSubmitDto } from '../../../shared/data-access/reviews/reviews.dtos';
import { TranslateService } from '@ngx-translate/core';
import { Location } from '@angular/common';

@Component({
  selector: 'app-review-edit',
  templateUrl: './review-edit.component.html',
  styleUrls: ['./review-edit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewEditComponent {
  activeUser$ = this.authService.getLoggedUser();

  loading$ = new BehaviorSubject(false);

  review$ = this.getReview();

  constructor(
    private readonly authService: AuthenticationService,
    private readonly reviewService: ReviewsService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private _snackBar: MatSnackBar,
    private readonly translate: TranslateService,
    private readonly location: Location
  ) {}

  getReview(): Observable<Review> {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    return this.reviewService.getReviewById(
      `${environment.API_ENDPOINT}/reviews/${id}`
    );
  }

  editReview(dto: ReviewSubmitDto | null) {
    if (dto === null) {
      this.location.back();
      return;
    }
    this.loading$.next(true);
    this.review$.subscribe(review => {
      this.reviewService.editReview(review.links.self, dto).subscribe({
        error: () => {
          this.loading$.next(false);
          this._snackBar.open(
            this.translate.instant('errors.unknown'),
            this.translate.instant('errors.dismiss'),
            {
              panelClass: ['red-snackbar'],
            }
          );
        },
        next: () => {
          this.loading$.next(false);
          this._snackBar.open(
            this.translate.instant('review.edited'),
            undefined,
            {
              panelClass: ['green-snackbar'],
            }
          );
          this.router.navigate([`/reviews/${review.id}`]);
        },
      });
    });
  }

  protected readonly ReviewFormType = ReviewFormType;
}

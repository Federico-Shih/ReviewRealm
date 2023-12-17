import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ReviewsService} from '../../../shared/data-access/reviews/reviews.service';
import {Feedback, Review,} from '../../../shared/data-access/reviews/review.class';
import {BehaviorSubject, combineLatest, distinctUntilChanged, map, Observable, switchMap, tap,} from 'rxjs';
import {AuthenticationService} from '../../../shared/data-access/authentication/authentication.service';
import {DifficultyToLocale, Role,} from '../../../shared/data-access/shared.enums';
import {environment} from '../../../../environments/environment';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {SharedModule} from '../../../shared/shared.module';
import {FormsModule} from '@angular/forms';
import {MatRadioModule} from '@angular/material/radio';
import {NgForOf} from '@angular/common';
import {MatSnackBar} from '@angular/material/snack-bar';
import {TranslateService} from '@ngx-translate/core';
import {getMessageFromReason, ReportReason,} from '../../../shared/data-access/reports/reports.class';

@Component({
  selector: 'app-review-detail',
  templateUrl: './review-detail.component.html',
  styleUrls: ['./review-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewDetailComponent implements OnInit {
  constructor(
    private readonly reviewsService: ReviewsService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly authService: AuthenticationService,
    public dialog: MatDialog,
    public snackBar: MatSnackBar,
    private readonly translate: TranslateService
  ) {}

  loading = true;

  review$: Observable<Review> = this.route.paramMap
    .pipe(map((params) => params.get('id')))
    .pipe(distinctUntilChanged())
    .pipe(
      tap(() => this.loading = true),
      switchMap(id => {
        return this.reviewsService.getReviewById(
          `${environment.API_ENDPOINT}/reviews/${id}`
        );
      }),
      tap(() => this.loading = false)
    );

  loadedReview$ = new BehaviorSubject<Review | null>(null);

  currentUser$ = this.authService.getLoggedUser();
  userAndReview$ = combineLatest([this.currentUser$, this.loadedReview$]);

  reviewFeedback$ = new BehaviorSubject<Feedback | null>(null);
  canEdit$ = this.userAndReview$.pipe(
    map(([user, review]) => {
      return user != null && user.id === review?.authorId;
    })
  );
  canReport$ = this.userAndReview$.pipe(
    map(([user, review]) => {
      return user != null && user.id !== review?.authorId;
    })
  );
  canDelete$ = this.userAndReview$.pipe(
    map(([user, review]) => {
      return (
        user != null &&
        (user.id === review?.authorId || user.role === Role.MODERATOR)
      );
    })
  );

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (!(id && Number.isInteger(parseInt(id)) && parseInt(id) > 0)) {
        this.router.navigate(['/errors/not-found']);
      }
    });
    this.review$.subscribe({
      error: () => {
        this.router.navigate(['/errors/not-found']);
      },
      next: (review) => {
        this.loadedReview$.next(review);
        this.currentUser$.subscribe((user) => {
          if (user !== null && review.links.feedback) {
            this.reviewsService.getReviewFeedbackFromUser(
              review.links.feedback
            ).subscribe((feedback) => this.reviewFeedback$.next(feedback));
          } else {
            this.reviewFeedback$.next(null);
          }
        })
      }
    });
  }

  openDeleteDialog() {
    const dialogRef = this.dialog.open(DeleteConfirmationDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        //confirmed
        console.log('deleting');
        this.review$.subscribe(review => {
          if (!review) return;
          this.reviewsService.deleteReview(review.links.self).subscribe({
            error: () => {
              this.snackBar.open(
                this.translate.instant('errors.unknown'),
                this.translate.instant('errors.dismiss'),
                {
                  panelClass: ['red-snackbar'],
                }
              );
            },
            next: deleted => {
              if (deleted) {
                this.snackBar.open(
                  this.translate.instant('review.deleted'),
                  undefined,
                  {
                    panelClass: ['red-snackbar'],
                  }
                );
                this.router.navigate(['/']);
              }
            },
          });
        });
      }
    });
  }

  openReportDialog() {
    const dialogRef = this.dialog.open(ReportDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      if (result != null) {
        this.review$.subscribe(review => {
          if (!review?.links.report) return;
          this.reviewsService
            .reportReview(review.links.report, review.id, result)
            .subscribe({
              error: err => {
                if (err.status !== 400 || err.exceptions === null) {
                  this.snackBar.open(
                    this.translate.instant('errors.unknown'),
                    this.translate.instant('errors.dismiss'),
                    {
                      panelClass: ['red-snackbar'],
                    }
                  );
                  return;
                }
                const errorMessage = err.exceptions.message;
                this.snackBar.open(
                  errorMessage,
                  this.translate.instant('errors.dismiss'),
                  {
                    panelClass: ['red-snackbar'],
                  }
                );
              },
              next: reported => {
                if (reported) {
                  this.snackBar.open(
                    this.translate.instant('review.reported'),
                    undefined,
                    { panelClass: ['green-snackbar'] }
                  );
                }
              },
            });
        });
      }
    });
  }

  protected readonly DifficultyToLocale = DifficultyToLocale;
}

@Component({
  selector: 'app-review-delete-dialog',
  templateUrl: 'dialog-review-delete-dialog.html',
  imports: [MatDialogModule, SharedModule],
  standalone: true,
})
export class DeleteConfirmationDialogComponent {}

@Component({
  selector: 'app-report-dialog',
  templateUrl: 'dialog-report-review-dialog.html',
  imports: [
    MatDialogModule,
    SharedModule,
    MatRadioModule,
    FormsModule,
    NgForOf,
  ],
  standalone: true,
})
export class ReportDialogComponent {
  reportReason: ReportReason = ReportReason.IRRELEVANT;
  reasons: ReportReason[] = Object.values(ReportReason);

  protected readonly ReasonToLocale = getMessageFromReason;
}

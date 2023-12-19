import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {BehaviorSubject, Observable, ReplaySubject} from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ReviewsService } from '../../../shared/data-access/reviews/reviews.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from '../../../shared/data-access/authentication/authentication.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { GamesService } from '../../../shared/data-access/games/games.service';
import { Game } from '../../../shared/data-access/games/games.class';
import { ReviewFormType } from '../../../shared/data-access/shared.enums';
import { ReviewSubmitDto } from '../../../shared/data-access/reviews/reviews.dtos';
import { TranslateService } from '@ngx-translate/core';
import { Location } from '@angular/common';
import {RequestError} from "../../../shared/data-access/shared.models";

@Component({
  selector: 'app-review-submit',
  templateUrl: './review-submit.component.html',
  styleUrls: ['./review-submit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewSubmitComponent implements OnInit {
  loading$ = new BehaviorSubject(false);
  checkingReview$ = new BehaviorSubject(true);

  game$ = this.getGame();

  constructor(
    private readonly authService: AuthenticationService,
    private readonly gameService: GamesService,
    private readonly reviewService: ReviewsService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private _snackBar: MatSnackBar,
    private readonly translate: TranslateService,
    private readonly location: Location
  ) {}


  ngOnInit(): void {
    this.game$.subscribe((game) => {
      if (game.links.userReview) {
        this.reviewService.getReviews(game.links.userReview, {}).subscribe(paginatedReview => {
          if (paginatedReview.totalElements > 0) {
            this.router.navigate(['/reviews', paginatedReview.content[0].id]);
            return;
          }
          this.checkingReview$.next(false);
        });
      }
    });
  }

  getGame(): Observable<Game> {
    const gameId = Number(this.route.snapshot.paramMap.get('game_id'));
    return this.gameService.getGame(
      `${environment.API_ENDPOINT}/games/${gameId}`
    );
  }

  createReview(dto: ReviewSubmitDto | null) {
    if (dto === null) {
      this.location.back();
      this.location.back();
      return;
    }
    this.loading$.next(true);
    this.reviewService
      .createReview(`${environment.API_ENDPOINT}/reviews`, dto)
      .subscribe({
        error: (err) => {
          this.loading$.next(false);
          if (err instanceof RequestError && err.status === 409) {
            this._snackBar.open(
              err.exceptions?.message || '',
              this.translate.instant('errors.dismiss'),
              {
                panelClass: ['red-snackbar'],
              }
            );
          } else {
            this._snackBar.open(
              this.translate.instant('errors.unknown'),
              this.translate.instant('errors.dismiss'),
              {
                panelClass: ['red-snackbar'],
              }
            );
          }
        },
        next: reviewId => {
          this.loading$.next(false);
          this._snackBar.open(
            this.translate.instant('review.created'),
            undefined,
            {
              panelClass: ['green-snackbar'],
            }
          );
          this.router.navigate([`/reviews/${reviewId}`]);
        },
      });
  }

  protected readonly ReviewFormType = ReviewFormType;

}

import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {GamesService} from "../../../shared/data-access/games/games.service";
import {ReviewsService} from "../../../shared/data-access/reviews/reviews.service";
import {AuthenticationService} from "../../../shared/data-access/authentication/authentication.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Game} from "../../../shared/data-access/games/games.class";
import {BehaviorSubject, catchError, map, Observable, of, pipe, Subject, switchMap, tap} from "rxjs";
import {Review} from "../../../shared/data-access/reviews/review.class";
import {Paginated, PaginatedLinks} from "../../../shared/data-access/shared.models";
import {environment} from "../../../../environments/environment";
import {Role} from "../../../shared/data-access/shared.enums";
import {PageEvent} from "@angular/material/paginator";
import {DeleteConfirmationDialogComponent} from "../../../reviews/feature/review-detail/review-detail.component";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import {SharedModule} from "../../../shared/shared.module";

@Component({
  selector: 'app-game-detail',
  templateUrl: './game-detail.component.html',
  styleUrls: ['./game-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameDetailComponent implements OnInit {

  loggedUser$ = this.authService.getLoggedUser();

  isModerator$ = this.loggedUser$.pipe(map((user) => {
    return user && user.role === Role.MODERATOR;
  }));

  game$: Observable<Game> = this.activatedRoute.paramMap.pipe(
    switchMap((params) => {
      return this.gameService.getGame(`${environment.API_ENDPOINT}/games/` + params.get('id'));
    }), catchError((err, caught) => {
      this.router.navigate(['/games']); //TODO:404
      return caught;
    }),
  );

  initialReviews$: Observable<Paginated<Review>> = this.game$.pipe(switchMap((game) => {
    if (game.links.reviewsExcludingUser) {
      return this.reviewService.getReviews(game.links.reviewsExcludingUser, {});//TODO:infinite scrolling
    }
    return this.reviewService.getReviews(game.links.reviews, {});
  }));

  paginatedReviews$: BehaviorSubject<Paginated<Review>|null> = new BehaviorSubject<Paginated<Review> | null>(null);

  userReview$: Observable<Paginated<Review> | null> = this.game$.pipe(switchMap((game) => {
    if (game.links.userReview) {
      return this.reviewService.getReviews(game.links.userReview, {});
    }
    return of(null);
  }));

  constructor(private readonly gameService: GamesService,
              private readonly reviewService: ReviewsService,
              private readonly authService: AuthenticationService,
              private readonly activatedRoute: ActivatedRoute,
              private readonly router: Router,
              public dialog: MatDialog,
              public snackBar: MatSnackBar,
              private readonly translate: TranslateService
  ) {
  }
  showMore(next: string): void {
    this.reviewService.getReviews(next,{}).subscribe((pageInfo) => {
      const currentReviews = this.paginatedReviews$.getValue();
      if (currentReviews!== null)
      {
        const newReviews = currentReviews.content.concat(pageInfo.content)
        this.paginatedReviews$.next({content: newReviews, links: pageInfo.links, totalPages: pageInfo.totalPages})
      }
    });
  }

  ngOnInit(): void {
    this.activatedRoute.paramMap.subscribe((params) => {
      const id = params.get('id');
      if (!(id && Number.isInteger(parseInt(id)) && parseInt(id) > 0)){
        this.router.navigate(['/games']); //TODO:404
      }
    });
    this.initialReviews$.subscribe(
        (pageInfo) => this.paginatedReviews$.next(pageInfo)
    )
  }
  openDeleteDialog() {
    const dialogRef = this.dialog.open(GameDeleteDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      if (result) { //confirmed
        console.log('deleting')
        this.game$.subscribe(
          (game) => {
            this.gameService.deleteGame(game.links.self).subscribe(
              {
                error: (err) => {
                  this.snackBar.open(this.translate.instant('errors.unknown'), this.translate.instant('errors.dismiss'),{
                    panelClass: ['red-snackbar']
                  });
                },
                next: (deleted) => {
                  if(deleted) {
                    this.snackBar.open(
                      this.translate.instant('game.deleted'), undefined,
                      {
                        panelClass: ['red-snackbar']
                      });
                    this.router.navigate(['/games']);
                  }
                }
              }
            )
          }
        )
      }
    });
  }
}
@Component({
  selector: 'app-game-delete-dialog',
  templateUrl: './game-delete-dialog.html',
  imports: [
    MatDialogModule,
    SharedModule
  ],
  standalone: true
})
export class GameDeleteDialogComponent {}


import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from '../../../shared/data-access/authentication/authentication.service';
import { GamesService } from '../../../shared/data-access/games/games.service';
import {
  BehaviorSubject,
  combineLatest,
  map,
  Observable,
  of,
  ReplaySubject,
  switchMap,
  tap,
} from 'rxjs';
import { Game } from '../../../shared/data-access/games/games.class';
import {
  Paginated,
  PaginatedLinks,
} from '../../../shared/data-access/shared.models';
import { Review } from '../../../shared/data-access/reviews/review.class';
import { ReviewsService } from '../../../shared/data-access/reviews/reviews.service';

@Component({
  selector: 'app-recommendations-page',
  templateUrl: './recommendations-page.component.html',
  styleUrls: ['./recommendations-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendationsPageComponent implements OnInit {
  loggedInUser$ = this.authService.getLoggedUser();
  gameRecommendationLink$ = new ReplaySubject<string>();
  isLoadingReviews$ = new BehaviorSubject<boolean>(true);

  game$: Observable<{ game: Game | null; links: PaginatedLinks }> =
    this.gameRecommendationLink$
      .pipe(tap(() => this.isLoadingReviews$.next(true)))
      .pipe(
        switchMap(link => this.gameService.getGames(link, { pageSize: 1 })),
        map(games => ({
          game: games.content[0] ?? null,
          links: games.links,
        }))
      );

  initialReviews$: Observable<Paginated<Review> | null> = this.game$.pipe(
    switchMap(({ game }) => {
      if (!game) return of(null);
      if (game.links.reviewsExcludingUser) {
        return this.reviewService.getReviews(
          game.links.reviewsExcludingUser,
          {}
        ); //TODO:infinite scrolling
      }
      return this.reviewService.getReviews(game.links.reviews, {});
    })
  );

  paginatedReviews$: BehaviorSubject<Paginated<Review> | null> =
    new BehaviorSubject<Paginated<Review> | null>(null);
  userReview$: Observable<Review | null> = this.game$.pipe(
    switchMap(({ game }) => {
      if (game && game.links.userReview) {
        return this.reviewService
          .getReviews(game.links.userReview, {})
          .pipe(map(paginated => paginated.content[0]));
      }
      return of(null);
    })
  );
  state$: Observable<{
    gameState: { game: Game | null; links: PaginatedLinks };
    userReview: Review | null;
    initialReviews: Paginated<Review> | null;
    isLoadingReviews: boolean;
  }> = combineLatest({
    gameState: this.game$,
    userReview: this.userReview$,
    initialReviews: this.initialReviews$,
    isLoadingReviews: this.isLoadingReviews$,
  });

  constructor(
    private readonly route: ActivatedRoute,
    private readonly authService: AuthenticationService,
    private readonly gameService: GamesService,
    private readonly router: Router,
    private readonly reviewService: ReviewsService
  ) {}

  ngOnInit() {
    if (this.route.snapshot.queryParamMap.get('position') === null) {
      this.router.navigate([], { queryParams: { position: 1 } });
    }
    const position = this.route.snapshot.queryParamMap.get('position') ?? '1';
    this.loggedInUser$.subscribe(user => {
      if (user !== null && user.links.recommendedGames) {
        this.gameRecommendationLink$.next(
          user.links.recommendedGames + '&page=' + position
        );
      }
    });
  }

  showMore(next: string): void {
    this.isLoadingReviews$.next(true);
    this.reviewService.getReviews(next, {}).subscribe(pageInfo => {
      const currentReviews = this.paginatedReviews$.getValue();
      if (currentReviews !== null) {
        const newReviews = currentReviews.content.concat(pageInfo.content);
        this.paginatedReviews$.next({
          content: newReviews,
          links: pageInfo.links,
          totalPages: pageInfo.totalPages,
          totalElements: pageInfo.totalElements,
        });
        this.isLoadingReviews$.next(false);
      }
    });
  }

  changeLink(link: string, next: boolean) {
    this.gameRecommendationLink$.next(link);
    const current = this.route.snapshot.queryParamMap.get('position');
    if (current) {
      if (next)
        this.router.navigate([], { queryParams: { position: +current + 1 } });
      else
        this.router.navigate([], { queryParams: { position: +current - 1 } });
    }
  }
}

import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import { UsersService } from '../../../shared/data-access/users/users.service';
import { ReviewsService } from '../../../shared/data-access/reviews/reviews.service';
import { GamesService } from '../../../shared/data-access/games/games.service';
import {ActivatedRoute, Router} from '@angular/router';
import { combineLatest, map, Observable, switchMap } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ReviewSortType } from '../../../shared/data-access/reviews/reviews.dtos';
import { GameSortType } from '../../../shared/data-access/games/games.dtos';
import { UserSortCriteria } from '../../../shared/data-access/users/users.dtos';
import { Paginated } from '../../../shared/data-access/shared.models';
import { Game } from '../../../shared/data-access/games/games.class';
import { Review } from '../../../shared/data-access/reviews/review.class';
import { User } from '../../../shared/data-access/users/users.class';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SearchComponent implements OnInit {
  breakpoint: number = 3;
  reviewBreakpoint: number = 2;

  onResize(event: Event) {
    const target = event.target as Window;
    if (target === null || target.innerWidth === null) return;
    this.breakpoint =
      target.innerWidth <= 1100 ? 1 : target.innerWidth <= 1800 ? 2 : 3;
    this.reviewBreakpoint = target.innerWidth <= 1100 ? 1 : 2;
  }

  search$ = this.route.queryParamMap.pipe(
    map(param => param.get('search') || ''));

  games$ = this.route.queryParamMap.pipe(
    switchMap(param =>
      this.gameService.getGames(`${environment.API_ENDPOINT}/games`, {
        search: param.get('search') || '',
        sort: GameSortType.AVERAGE_RATING,
        pageSize: 6,
      })
    )
  );
  reviews$ = this.route.queryParamMap.pipe(
    switchMap(param =>
      this.reviewService.getReviews(`${environment.API_ENDPOINT}/reviews`, {
        search: param.get('search') || '',
        sort: ReviewSortType.CREATED,
        pageSize: 6,
      })
    )
  );
  users$ = this.route.queryParamMap.pipe(
    switchMap(param =>
      this.userService.getUsers(`${environment.API_ENDPOINT}/users`, {
        search: param.get('search') || '',
        sort: UserSortCriteria.REPUTATION,
        pageSize: 6,
      })
    )
  );

  combinedResults$: Observable<{
    games: Paginated<Game>;
    reviews: Paginated<Review>;
    users: Paginated<User>;
    hasResults: boolean;
  }> = combineLatest([this.games$, this.reviews$, this.users$]).pipe(
    map(([games, reviews, users]) => ({
      games,
      reviews,
      users,
      hasResults:
        games.totalPages > 0 || reviews.totalPages > 0 || users.totalPages > 0,
    }))
  );

  navigateToReview(search: string) {
    this.router.navigate(['/'], { queryParams: {
      search
    }});
  }

  navigateToGames(search: string) {
    this.router.navigate(['/games'], { queryParams: {
      search
    }});
  }

  navigateToUsers(search: string) {
    this.router.navigate(['/community'], { queryParams: {
      search
    }});
  }

  constructor(
    private readonly userService: UsersService,
    private readonly reviewService: ReviewsService,
    private readonly gameService: GamesService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.breakpoint =
      window.innerWidth <= 1100 ? 1 : window.innerWidth <= 2100 ? 2 : 3;
    this.reviewBreakpoint = window.innerWidth <= 1100 ? 1 : 2;
  }
}

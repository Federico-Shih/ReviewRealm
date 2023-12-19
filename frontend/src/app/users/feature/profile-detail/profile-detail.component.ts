import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UsersService} from '../../../shared/data-access/users/users.service';
import {BehaviorSubject, catchError, combineLatest, map, Observable, of, ReplaySubject, switchMap, tap,} from 'rxjs';
import {User} from '../../../shared/data-access/users/users.class';
import {GamesService} from '../../../shared/data-access/games/games.service';
import {GameExclusiveSearchDto} from '../../../shared/data-access/games/games.dtos';
import {environment} from '../../../../environments/environment';
import {Game} from '../../../shared/data-access/games/games.class';
import {Paginated} from '../../../shared/data-access/shared.models';
import {ReviewsService} from '../../../shared/data-access/reviews/reviews.service';
import {Review} from '../../../shared/data-access/reviews/review.class';
import {ReviewSearchDto} from '../../../shared/data-access/reviews/reviews.dtos';
import {AuthenticationService} from '../../../shared/data-access/authentication/authentication.service';
import {ReviewInfiniteLoadService} from "../../../shared/stores/infinite-load.service";

@Component({
  selector: 'app-profile-detail',
  templateUrl: './profile-detail.component.html',
  styleUrls: ['./profile-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileDetailComponent implements OnInit, OnDestroy {
  constructor(
    private readonly userService: UsersService,
    private readonly reviewService: ReviewsService,
    private readonly gameService: GamesService,
    private readonly router: Router,
    private route: ActivatedRoute,
    private readonly authService: AuthenticationService,
    private readonly infiniteLoadService: ReviewInfiniteLoadService
  ) {
    this.user$.subscribe(user => {
      this.loadedUser$.next(user);
    });

    this.infiniteLoadService.registerPagination(this.reviewService.getReviews.bind(this.reviewService));
  }

  protected breakpoint = 2;
  protected breakpointFavgames = 3;

  ngOnInit(): void {
    this.breakpoint =
      window.innerWidth <= 1100 ? 1 : window.innerWidth <= 2100 ? 2 : 3;
    this.breakpointFavgames =
      window.innerWidth <= 630 ? 1 : window.innerWidth <= 900 ? 2 : 3;
    this.user$.subscribe(user => {
      this.infiniteLoadService.reset();
      this.infiniteLoadService.loadMore(user.links.reviews, { pageSize: 4 });
    });
  }

  loadedUser$ = new ReplaySubject<User>(1);

  user$: Observable<User> = this.route.paramMap.pipe(
    switchMap(params => {
      return this.userService.getUserById(
        `${environment.API_ENDPOINT}/users/` + params.get('id')
      );
    }),
    catchError((err, caught) => {
      this.router.navigate(['errors/not-found']);
      return caught;
    })
  );

  isFollowing$: Observable<boolean> = this.loadedUser$.pipe(map((user) => {
    return !user.links.follow;
  }));

  loggedUser$ = this.authService.getLoggedUser();

  combinedUsers$ = combineLatest([this.loggedUser$, this.loadedUser$, this.isFollowing$]);

  favgames$: Observable<Paginated<Game>> = this.route.paramMap.pipe(
    switchMap(params => {
      const numericId = Number(params.get('id'));
      const query: GameExclusiveSearchDto = {
        favoriteOf: numericId,
      };
      return this.gameService.getGames(
        `${environment.API_ENDPOINT}/games`,
        query
      );
    }),
    catchError(() => {
      return of({
        content: [],
        links: {self: ''},
        totalPages: 0,
        totalElements: 0,
      })
    }),
  );

  followLoading = new BehaviorSubject(false);

  reviewState$ = this.infiniteLoadService.getState$();

  followToggle() {
    this.followLoading.next(true);
    combineLatest([this.loggedUser$, this.user$]).subscribe(([loggedUser, loadeduser]) => {
      if (!loggedUser) return;
      if (loadeduser.links.unfollow) {
        this.userService.unfollow(loadeduser.links.unfollow).subscribe({
          next: () => {
            this.user$.subscribe(user => {
              this.loadedUser$.next(user);
              this.followLoading.next(false);
            });
          }
        });
      } else if (loadeduser.links.follow) {
        this.userService.follow(loadeduser.links.follow, {userId: loadeduser.id}).subscribe({
          next: () => {
            this.user$.subscribe(user => {
              this.loadedUser$.next(user);
              this.followLoading.next(false);
            });
          }
        });
      }
    });
  }

  showMore(next: string): void {
    this.infiniteLoadService.loadMore(next, {});
  }

  onResize(event: Event) {
    const target = event.target as Window;
    if (target === null || target.innerWidth === null) return;
    this.breakpoint =
      target.innerWidth <= 1100 ? 1 : target.innerWidth <= 2100 ? 2 : 3;
  }

  onResizeFavGame(event: Event) {
    const target = event.target as Window;
    if (target === null || target.innerWidth === null) return;
    this.breakpointFavgames =
      target.innerWidth <= 630 ? 1 : target.innerWidth <= 900 ? 2 : 3;
  }

  ngOnDestroy(): void {
    this.infiniteLoadService.deregisterPagination();
  }
}

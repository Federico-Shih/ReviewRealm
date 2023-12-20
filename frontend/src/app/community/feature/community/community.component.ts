import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UsersService } from '../../../shared/data-access/users/users.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl } from '@angular/forms';
import { User } from '../../../shared/data-access/users/users.class';
import { Paginated } from '../../../shared/data-access/shared.models';
import {
  BehaviorSubject,
  combineLatest,
  map,
  Observable,
  of,
  switchMap,
  tap,
} from 'rxjs';
import { paramsMapToUserSearchDto } from '../../../home/utils/mappers';
import {
  UserSearchDto,
  UserSortCriteria,
} from '../../../shared/data-access/users/users.dtos';
import { environment } from '../../../../environments/environment';
import { SortDirection } from '../../../shared/data-access/shared.enums';
import { PageEvent } from '@angular/material/paginator';
import { AuthenticationService } from '../../../shared/data-access/authentication/authentication.service';
import { ReviewsService } from '../../../shared/data-access/reviews/reviews.service';

@Component({
  selector: 'app-community',
  templateUrl: './community.component.html',
  styleUrls: ['./community.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CommunityComponent implements OnInit {
  constructor(
    private readonly userService: UsersService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly authService: AuthenticationService,
    private readonly reviewService: ReviewsService
  ) {}

  search = new FormControl('');
  orderBy = new FormControl(UserSortCriteria.LEVEL);
  orderDirection = new FormControl(SortDirection.DESC);

  loadingResults$ = new BehaviorSubject<boolean>(false);
  loadingSamePreferencesUsers$ = new BehaviorSubject<boolean>(false);
  loadingSameGamesUsers$ = new BehaviorSubject<boolean>(false);

  userSearchDto$: Observable<UserSearchDto> = this.route.queryParamMap.pipe(
    map(qpm => {
      return paramsMapToUserSearchDto(
        qpm,
        this.orderBy.value?.toString(),
        this.orderDirection.value?.toString()
      );
    })
  );

  users$: Observable<Paginated<User>> = this.userSearchDto$
    .pipe(
      tap(() => {
        console.log('loading results = true');
        this.loadingResults$.next(true);
      })
    )
    .pipe(
      switchMap(query => {
        return this.userService.getUsers(
          `${environment.API_ENDPOINT}/users`,
          query
        );
      })
    )
    .pipe(
      tap(() => {
        console.log('loading results = false');
        this.loadingResults$.next(false);
      })
    );

  pagination$ = this.users$.pipe(
    map(users => {
      return {
        totalPages: users.totalPages,
        totalElements: users.totalElements,
        links: users.links,
      };
    })
  );

  combinedPagination$ = combineLatest([this.userSearchDto$, this.pagination$]);

  currentUser$ = this.authService.getLoggedUser();

  noGamesPlayed$ = this.currentUser$.pipe(
    switchMap(user => {
      if (user === null) return of(false);
      return this.reviewService
        .getReviews(`${environment.API_ENDPOINT}/reviews`, {
          authors: [user.id],
        })
        .pipe(map(reviews => reviews.totalElements === 0));
    })
  );

  samePreferencesUsers$: Observable<Paginated<User>> = combineLatest([
    this.currentUser$,
    this.userSearchDto$,
  ])
    .pipe(tap(() => this.loadingSamePreferencesUsers$.next(true)))
    .pipe(
      switchMap(([user, dto]) => {
        if (user) {
          return this.userService.getUsers(
            `${environment.API_ENDPOINT}/users`,
            {
              samePreferencesAs: user.id,
              page: 1,
              pageSize: 3,
              sort: dto.sort,
              direction: dto.direction,
            }
          );
        }
        return of();
      })
    )
    .pipe(tap(() => this.loadingSamePreferencesUsers$.next(false)));

  sameGamesUsers$: Observable<Paginated<User>> = combineLatest([
    this.currentUser$,
    this.userSearchDto$,
  ])
    .pipe(tap(() => this.loadingSameGamesUsers$.next(true)))
    .pipe(
      switchMap(([user, dto]) => {
        if (user) {
          return this.userService.getUsers(
            `${environment.API_ENDPOINT}/users`,
            {
              sameGamesPlayedAs: user.id,
              page: 1,
              pageSize: 3,
              sort: dto.sort,
              direction: dto.direction,
            }
          );
        }
        return of();
      })
    )
    .pipe(tap(() => this.loadingSameGamesUsers$.next(false)));

  handlePageEvent(e: PageEvent) {
    if (!e.previousPageIndex || e.previousPageIndex < e.pageIndex) {
      this.router.navigate([], {
        queryParams: {
          page: e.pageIndex + 1,
        },
        queryParamsHandling: 'merge',
      });
    } else {
      this.router.navigate([], {
        queryParams: {
          page: e.pageIndex + 1,
        },
        queryParamsHandling: 'merge',
      });
    }
  }

  setSearch(value: string) {
    this.search.setValue(value);
  }

  setOrderBy(value: UserSortCriteria) {
    this.orderBy.setValue(value);
  }

  setOrderDirection(value: SortDirection) {
    this.orderDirection.setValue(value);
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(qpm => {
      this.setSearch(qpm.get('search') || '');
      const orderByParam = qpm.get('sort');
      if (orderByParam && orderByParam != this.orderBy.value) {
        this.setOrderBy(orderByParam as UserSortCriteria);
      }
      const orderDirectionParam = qpm.get('direction');
      if (
        orderDirectionParam &&
        orderDirectionParam != this.orderDirection.value
      ) {
        this.setOrderDirection(orderDirectionParam as SortDirection);
      }
    });
    this.orderBy.valueChanges.subscribe(sort => {
      this.router.navigate([], {
        queryParams: {
          sort: sort,
        },
        queryParamsHandling: 'merge',
      });
    });
    this.orderDirection.valueChanges.subscribe(direction => {
      this.router.navigate([], {
        queryParams: {
          direction: direction,
        },
        queryParamsHandling: 'merge',
      });
    });
  }

  submitSearch() {
    if (this.search.value !== null) {
      this.router.navigate([], {
        queryParams: {
          search: this.search.value,
        },
        queryParamsHandling: 'merge',
      });
    }
  }

  protected readonly UserSortCriteria = UserSortCriteria;
  protected readonly SortDirection = SortDirection;
}

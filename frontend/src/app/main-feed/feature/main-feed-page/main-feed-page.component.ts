import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormControl} from '@angular/forms';
import {BehaviorSubject, combineLatest, map} from 'rxjs';
import {Review} from '../../../shared/data-access/reviews/review.class';
import {Paginated} from '../../../shared/data-access/shared.models';
import {ReviewsService} from '../../../shared/data-access/reviews/reviews.service';
import {AuthenticationService} from '../../../shared/data-access/authentication/authentication.service';
import {User} from '../../../shared/data-access/users/users.class';

enum Tabs {
  FOLLOWING = 'following',
  RECOMMENDED = 'recommended',
  NEW = 'new',
}

@Component({
  selector: 'app-main-feed-page',
  templateUrl: './main-feed-page.component.html',
  styleUrls: ['./main-feed-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MainFeedPageComponent implements OnInit {
  static INITIAL_LOAD_COUNT = 6;
  static PERCENTAGE_SCROLLED = 0.8;
  tabs = Object.values(Tabs);
  selectedTab = new FormControl(Tabs.FOLLOWING);
  reviews$: BehaviorSubject<Paginated<Review>> = new BehaviorSubject<
    Paginated<Review>
  >({
    content: [],
    links: {
      self: '',
    },
    totalPages: 0,
    totalElements: 0,
  });

  loggedInUser$ = this.authService.getLoggedUser();
  hasPreferences$ = this.loggedInUser$.pipe(
    map(user => !!user && user.preferences.length > 0)
  );

  loading$ = new BehaviorSubject<boolean>(true);
  loadingInfinite$ = new BehaviorSubject<boolean>(false);
  loadingReviews$ = combineLatest({
    loading: this.loading$,
    reviews: this.reviews$,
    loadingInfinite: this.loadingInfinite$,
    user: this.loggedInUser$
  });

  protected breakpoint = 2;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly reviewService: ReviewsService,
    private readonly authService: AuthenticationService
  ) {}

  getLinkFromTab(tab: string, user: User): string {
    switch (tab) {
      case Tabs.FOLLOWING:
        return user.links.followingReviews || '';
      case Tabs.RECOMMENDED:
        return user.links.recommendedReviews || '';
      case Tabs.NEW:
        return user.links.newReviews || '';
    }
    return '';
  }

  getTabLabel(tab: Tabs | null): string {
    switch (tab) {
      case Tabs.FOLLOWING:
        return 'mainfeed.header.following';
      case Tabs.RECOMMENDED:
        return 'mainfeed.header.recommended';
      case Tabs.NEW:
        return 'mainfeed.header.new';
    }
    return '';
  }

  ngOnInit(): void {
    this.selectedTab.valueChanges.subscribe(tab => {
      this.router.navigate([], {
        queryParams: {
          tab,
        },
      });
    });

    this.route.queryParams.subscribe(params => {
      if (params['tab'] && this.tabs.includes(params['tab'])) {
        this.selectedTab.setValue(params['tab']);
        this.loading$.next(true);
        this.loggedInUser$.subscribe(user => {
          if (user) {
            this.reviewService
              .getReviews(this.getLinkFromTab(params['tab'], user), {
                pageSize: MainFeedPageComponent.INITIAL_LOAD_COUNT,
              })
              .subscribe({
                next: reviews => {
                  this.reviews$.next(reviews);
                  this.loading$.next(false);
                },
                error: () => {
                  this.loading$.next(false);
                },
              });
          }
        });
      } else {
        this.router.navigate([], {
          queryParams: {
            tab: this.selectedTab.value,
          },
        });
      }
    });
    this.breakpoint =
        window.innerWidth <= 720 ? 1 : 2;
  }

  nextPage(event: Event) {
    if (
      event.target instanceof HTMLElement &&
      event.target.scrollTop /
        (event.target.scrollHeight - event.target.clientHeight) >
        MainFeedPageComponent.PERCENTAGE_SCROLLED &&
      this.reviews$.value.links.next &&
      !this.loadingInfinite$.value &&
      !this.loading$.value
    ) {
      this.loadingInfinite$.next(true);
      this.reviewService
        .getReviews(this.reviews$.value.links.next, {})
        .subscribe({
          next: reviews => {
            this.reviews$.next({
              content: this.reviews$.value.content.concat(reviews.content),
              links: reviews.links,
              totalPages: reviews.totalPages,
              totalElements: reviews.totalElements,
            });
            this.loadingInfinite$.next(false);
          },
          error: () => {
            this.loadingInfinite$.next(false);
          },
        });
    }
  }

  onResize(event: Event) {
    const target = event.target as Window;
    if (target === null || target.innerWidth === null) return;
    this.breakpoint =
      target.innerWidth <= 720 ? 1 : 2;
  }
}

import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  OnInit,
  Renderer2,
  RendererFactory2,
  ViewChild,
} from '@angular/core';
import {ReviewsService} from '../../../shared/data-access/reviews/reviews.service';
import {BehaviorSubject, combineLatest, map, Observable, switchMap, tap} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {mapCheckedToType, paramsMapToReviewSearchDto,} from '../../utils/mappers';
import {Difficulty, Platform, SortDirection,} from '../../../shared/data-access/shared.enums';
import {ReviewSortType} from '../../../shared/data-access/reviews/reviews.dtos';
import {EnumType} from '../../../shared/ui/filter-drawer/filter-drawer.component';
import {EnumsService} from '../../../shared/data-access/enums/enums.service';
import {Genre} from '../../../shared/data-access/enums/enums.class';
import {FormArray, FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {MatSidenav} from '@angular/material/sidenav';
import {formatNumber} from '@angular/common';
import {PageEvent} from '@angular/material/paginator';
import {environment} from '../../../../environments/environment';
import {Review} from '../../../shared/data-access/reviews/review.class';
import {AuthenticationService} from "../../../shared/data-access/authentication/authentication.service";

@Component({
  selector: 'app-review-search',
  templateUrl: './review-search.component.html',
  styleUrls: ['./review-search.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewSearchComponent implements OnInit, AfterViewInit {
  favoritedGenres$: Observable<Genre[]> = this.authService.getLoggedUser().pipe(switchMap((user) => {
    if (user === null) return [];
    return this.genreService.getGenres(user.links.preferences);
  }));

  filter: FormGroup;
  loading = false;
  filtersLoaded = new BehaviorSubject<boolean>(false);
  genres$ = this.genreService.getGenres(`${environment.API_ENDPOINT}/genres`);
  reviewSearchDto$ = this.route.queryParamMap.pipe(
    map(paramsMapToReviewSearchDto)
  );

  paginatedReviews$ = this.reviewSearchDto$
    .pipe(tap(() => (this.loading = true)))
    .pipe(
      switchMap(query =>
        this.reviewsService.getReviews(
          `${environment.API_ENDPOINT}/reviews`,
          query
        )
      )
    )
    .pipe(tap(() => (this.loading = false)));

  pagination$ = this.paginatedReviews$.pipe(
    map(reviews => {
      return {
        totalPages: reviews.totalPages,
        totalElements: reviews.totalElements,
        links: reviews.links,
      };
    })
  );

  combinedPagination$ = combineLatest({
    reviewSearch: this.reviewSearchDto$,
    pagination: this.pagination$,
  });

  constructor(
    private readonly reviewsService: ReviewsService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly genreService: EnumsService,
    private readonly formBuilder: FormBuilder,
    private readonly rendererFactory: RendererFactory2,
    private readonly authService: AuthenticationService,
  ) {
    this.filter = this.formBuilder.group({
      gameGenres: new FormArray([]),
      userPreferences: new FormArray([]),
      sort: new FormControl(''),
      direction: new FormControl(''),
      platform: new FormArray([]),
      difficulty: new FormArray([]),
      _gameGenresMeta: [],
      mintimeplayed: new FormControl(0),
      completed: new FormControl(undefined),
      replayable: new FormControl(undefined),
      search: new FormControl(''),
    });
    this.renderer = rendererFactory.createRenderer(null, null);
  }


  orderDirections: EnumType<ReviewSortType>[] = Object.values(
    ReviewSortType
  ).map(str => ({
    translateKey: `review-filter.${str}`,
    selectKey: str as ReviewSortType,
  }));

  platforms: Platform[] = Object.values(Platform) as Platform[];
  difficulties: Difficulty[] = Object.values(Difficulty) as Difficulty[];
  protected readonly SortDirection = SortDirection;
  protected readonly ReviewSortType = ReviewSortType;
  protected readonly Object = Object;
  protected readonly formatNumber = formatNumber;
  private renderer: Renderer2;
  protected breakpoint = 1;

  @ViewChild('sidenav')
  sidenav!: MatSidenav;

  ngOnInit() {
    // Carga los generes elegidos y sus selecciones
    combineLatest([this.genres$, this.reviewSearchDto$]).subscribe(
      ([genres, reviewSearchDto]) => {
        const selected = reviewSearchDto.gameGenres || [];
        const selectedPreferences = reviewSearchDto.authorPreferences || [];

        // Actualiza los valores de forms cada vez que hay un cambio en tu reviewSearch.
        genres.forEach((genre, index) => {
          const isSelected =
            selected.find(selection => selection === genre.id) !== undefined;
          const genreControl = this.filter.get('gameGenres') as FormArray;
          if (genreControl.length < genres.length) {
            genreControl.push(new FormControl(isSelected));
          } else {
            genreControl.at(index).setValue(isSelected);
          }

          const isSelectedPreference =
            selectedPreferences.find(selection => selection === genre.id) !==
            undefined;

          const prefControl = this.filter.get('userPreferences') as FormArray;
          if (prefControl.length < genres.length) {
            prefControl.push(new FormControl(isSelectedPreference));
          } else {
            prefControl.at(index).setValue(isSelectedPreference);
          }
        });
        this.filter.get('_gameGenresMeta')?.setValue(genres);
      }
    );

    // Actualiza los valores de forms cada vez que hay un cambio en tu reviewSearch.
    this.reviewSearchDto$.subscribe(
      ({
         sort,
         timeplayed,
         completed,
         replayable,
         direction,
         difficulty,
         platforms,
       }) => {
        this.filter.get('sort')?.setValue(sort || ReviewSortType.CREATED);
        this.filter.get('direction')?.setValue(direction || SortDirection.DESC);
        const difficultyControl = this.filter.get('difficulty') as FormArray;
        const platformControl = this.filter.get('platform') as FormArray;
        this.filter.get('completed')?.setValue(completed);
        this.filter.get('replayable')?.setValue(replayable);

        this.filter.get('mintimeplayed')?.setValue(timeplayed || 0);
        this.difficulties.forEach(diff => {
          const selected = difficulty?.find(d => d === diff) !== undefined;
          if (difficultyControl.length < this.difficulties.length) {
            difficultyControl?.push(new FormControl(selected));
          } else {
            const index = this.difficulties.findIndex(d => d === diff);
            difficultyControl?.at(index).setValue(selected);
          }
        });

        this.platforms.forEach(platform => {
          const selected = platforms?.find(d => d === platform) !== undefined;
          if (platformControl.length < this.platforms.length) {
            platformControl.push(new FormControl(selected));
          } else {
            const index = this.platforms.findIndex(d => d === platform);
            platformControl.at(index).setValue(selected);
          }
        });
        this.filtersLoaded.next(true);
      }
    );

    this.breakpoint =
      window.innerWidth <= 1100 ? 1 : window.innerWidth <= 2100 ? 2 : 3;
  }

  search(value: string) {
    this.filter.get('search')?.setValue(value);
  }

  submitSearch() {
    if (this.filter.get('search')?.value !== undefined) {
      this.router.navigate([], {
        queryParams: {
          search: this.filter.get('search')?.value,
        },
        queryParamsHandling: 'merge',
      });
    }
  }

  navigateToReview(review: Review) {
    this.router.navigate(['/reviews', review.id]);
  }

  selectFavoriteGameGenres(genres: Genre[]) {
    if (genres.length > 0) {
      this.filter.value._gameGenresMeta.forEach((genre: Genre, index: number) => {
        if (genres.find((g) => g.id === genre.id) !== undefined) {
          ((this.filter.get('gameGenres') as FormArray).at(index))?.setValue(true);
        }
      });
    }
  }

  selectUserPreferences(genres: Genre[]) {
    if (genres.length > 0) {
      this.filter.value._gameGenresMeta.forEach((genre: Genre, index: number) => {
        if (genres.find((g) => g.id === genre.id) !== undefined) {
          this.filter.get('userPreferences')?.get(`${index}`)?.setValue(true);
        }
      });
    }
  }

  onResize(event: Event) {
    const target = event.target as Window;
    if (target === null || target.innerWidth === null) return;
    this.breakpoint =
      target.innerWidth <= 1100 ? 1 : target.innerWidth <= 2100 ? 2 : 3;
  }

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

  selectSortDirection(sortDirection: SortDirection) {
    this.filter.get('direction')?.setValue(sortDirection);
  }

  selectSortType(sortType: ReviewSortType) {
    this.filter.get('sort')?.setValue(sortType);
  }

  check(formLabel: string, value: boolean | undefined) {
    this.filter.get(formLabel)?.setValue(value);
  }

  resetFilters() {
    this.router.navigate([], {});
  }

  applyFilters() {
    const {
      gameGenres,
      userPreferences,
      platform,
      difficulty,
      _gameGenresMeta,
      sort,
      direction,
      mintimeplayed,
      replayable,
      completed,
    } = this.filter.value;
    const queryParams: Record<string, unknown> = {
      gameGenres: mapCheckedToType(
        gameGenres as boolean[],
        _gameGenresMeta as Genre[],
        genre => genre.id
      ),
      userPreferences: mapCheckedToType(
        userPreferences as boolean[],
        _gameGenresMeta as Genre[],
        genre => genre.id
      ),
      platforms: mapCheckedToType(
        platform as boolean[],
        this.platforms,
        platform => platform
      ),
      difficulty: mapCheckedToType(
        difficulty as boolean[],
        this.difficulties,
        difficulty => difficulty
      ),
      timeplayed: mintimeplayed,
      sort,
      direction,
    };
    if (replayable !== undefined) {
      queryParams['replayable'] = replayable;
    }
    if (completed !== undefined) {
      queryParams['completed'] = completed;
    }

    this.router.navigate([], {
      queryParams,
    });
  }

  ngAfterViewInit(): void {
    // Usado para eliminar el scrollbar en el sidenav
    this.renderer.setStyle(
      this.sidenav._content.nativeElement,
      'scrollbar-width',
      'none'
    );
  }

  formatLabel(value: number): string {
    return `${value}`;
  }
}

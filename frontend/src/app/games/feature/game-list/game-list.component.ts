import {ChangeDetectionStrategy, Component, OnInit, Renderer2, RendererFactory2, ViewChild} from '@angular/core';
import {GamesService} from "../../../shared/data-access/games/games.service";
import {GameSearchDto, GameSortType, isGameSortType, RatingType} from "../../../shared/data-access/games/games.dtos";
import {ActivatedRoute, Router} from "@angular/router";
import {combineLatest, map, switchMap} from "rxjs";
import {isSortDirection, SortDirection} from "../../../shared/data-access/shared.enums";
import {mapCheckedToType, paramsMapToGameSearchDto} from "../../../home/utils/mappers";
import {EnumsService} from "../../../shared/data-access/enums/enums.service";
import {FormArray, FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {EnumType} from "../../../shared/ui/filter-drawer/filter-drawer.component";
import {MatSidenav} from "@angular/material/sidenav";
import {PageEvent} from "@angular/material/paginator";
import {Genre} from "../../../shared/data-access/enums/enums.class";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-game-list',
  templateUrl: './game-list.component.html',
  styleUrls: ['./game-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameListComponent implements OnInit{

    gameSearchDto$ = this.route.queryParamMap.pipe(map(paramsMapToGameSearchDto));
    genres$ = this.genreService.getGenres(`${environment.API_ENDPOINT}/genres`);
    filter:FormGroup;
    private renderer: Renderer2;

    paginatedGames$ = this.gameSearchDto$.pipe(
      switchMap((query) => this.gameService.getGames(`${environment.API_ENDPOINT}/games`, query)));

    pagination$ = this.paginatedGames$.pipe(map((games) => {
      return {
        totalPages: games.totalPages,
        totalElements: games.totalElements,
        links: games.links
      };
    }));

    combinedPagination$ = combineLatest([this.gameSearchDto$, this.pagination$]);

    @ViewChild('sidenav')
    sidenav!: MatSidenav;

    constructor(private readonly gameService:GamesService,
                private readonly router: Router,
                private readonly genreService: EnumsService,
                private readonly formBuilder: FormBuilder,
                private route: ActivatedRoute,
                private readonly rendererFactory: RendererFactory2){
    this.filter = this.formBuilder.group({
      search: new FormControl(''),
      excludeNoRating: new FormControl(false),
      genres: new FormArray([]),
      lowRating: new FormControl(0),
      highRating: new FormControl(10),
      _gameGenresMeta:[],
      sort: new FormControl(''),
      direction: new FormControl('')
    });
      this.renderer = rendererFactory.createRenderer(null, null);
    }
  orderDirections: EnumType<GameSortType>[] = Object.values(GameSortType).map((str) => ({
    translateKey: `game-filter.${str}`,
    selectKey: str
  }));

  protected readonly SortDirection = SortDirection;
  protected readonly GameSortType = GameSortType;


  ngOnInit(): void {
    combineLatest([this.genres$, this.gameSearchDto$]).
    subscribe(([genres, gameSearchDto]) => {
      const selectedGenres = gameSearchDto.genres || []

      genres.forEach((genre,index) => {
        const isSelected =  selectedGenres.find((selection) => selection === genre.id) !== undefined;
        const genreControl = (this.filter.get('genres') as FormArray)
        if (genreControl.length < genres.length) {
          genreControl.push(new FormControl(isSelected));
        } else {
          genreControl.at(index).setValue(isSelected);
        }
      });
      this.filter.get('_gameGenresMeta')?.setValue(genres);
    });

    this.gameSearchDto$.subscribe(({sort,direction,rating,excludeNoRating}) => {
      this.filter.get('sort')?.setValue(sort || GameSortType.PUBLISH_DATE);
      this.filter.get('direction')?.setValue(direction || SortDirection.DESC);
      const ratingRange = rating?.toString().split('t');
      this.filter.get('lowRating')?.setValue(ratingRange ? parseFloat(ratingRange[0]) : 0.0);
      this.filter.get('highRating')?.setValue(ratingRange ? parseFloat(ratingRange[1]) : 10.0);
      this.filter.get('excludeNoRating')?.setValue(excludeNoRating || false);
    });
  }

  resetFilters() {
    this.router.navigate([], {});
  }
  handlePageEvent(e: PageEvent) {
    if (!e.previousPageIndex || e.previousPageIndex < e.pageIndex) {
      this.router.navigate([], {
        queryParams: {
          page: e.pageIndex + 1
        },
        queryParamsHandling: 'merge'
      });
    } else {
      this.router.navigate([], {
        queryParams: {
          page: e.pageIndex + 1,
        },
        queryParamsHandling: 'merge'
      });
    }
  }
  applyFilters() {
    const {
      genres,
      lowRating,
      highRating,
      excludeNoRating,
      search,
      sort,
      direction,
      _gameGenresMeta,
    } = this.filter.value;
    const rating = `${lowRating}t${highRating}`;
    const queryParams:Record<string,unknown> = {
      genres: mapCheckedToType((genres as boolean[]), _gameGenresMeta as Genre[],(genre) => genre.id),
      rating,
      excludeNoRating,
      search,
      sort,
      direction
    };
    console.log(queryParams);
    this.router.navigate([], {
      queryParams,
    });
  }
  check(formLabel: string, value: boolean | undefined) {
    this.filter.get(formLabel)?.setValue(value);
  }
  selectSortDirection(sortDirection: SortDirection) {
    this.filter.get('direction')?.setValue(sortDirection);
  }

  selectSortType(sortType: GameSortType) {
    this.filter.get('sort')?.setValue(sortType);
  }

  ngAfterViewInit(): void {
    // Usado para eliminar el scrollbar en el sidenav
    this.renderer.setStyle(this.sidenav._content.nativeElement, 'scrollbar-width', 'none')
  }

  formatLabel(value: number): string {
    return `${value}`;
  }
  search(value: string) {
    this.filter.get('search')?.setValue(value);
  }
  submitSearch() {
    if (this.filter.get('search')?.value !== undefined) {
      this.router.navigate([], {
        queryParams: {
          search: this.filter.get('search')?.value
        },
        queryParamsHandling: 'merge'
      });
    }
  }

}

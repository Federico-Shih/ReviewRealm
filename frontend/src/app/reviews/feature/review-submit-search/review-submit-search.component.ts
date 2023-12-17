import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { GamesService } from '../../../shared/data-access/games/games.service';
import { environment } from '../../../../environments/environment';
import { BehaviorSubject, map, switchMap, tap } from 'rxjs';
import { gameSearchForSubmitReviewParamsToDto } from '../../../home/utils/mappers';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl } from '@angular/forms';
import { AuthenticationService } from '../../../shared/data-access/authentication/authentication.service';
import { combineLatest } from 'rxjs';

@Component({
  selector: 'app-review-submit-search',
  templateUrl: './review-submit-search.component.html',
  styleUrls: ['./review-submit-search.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewSubmitSearchComponent implements OnInit {
  constructor(
    private readonly gameService: GamesService,
    private route: ActivatedRoute,
    private readonly router: Router,
    private readonly authService: AuthenticationService
  ) {}
  search = new FormControl('');

  loadingGames$ = new BehaviorSubject<boolean>(false);

  currentUser$ = this.authService.getLoggedUser();
  gameSearchDto$ = combineLatest([
    this.currentUser$,
    this.route.queryParamMap,
  ]).pipe(
    map(([user, qpm]) => {
      return gameSearchForSubmitReviewParamsToDto(qpm, user?.id);
    })
  );
  games$ = this.gameSearchDto$
    .pipe(tap(() => this.loadingGames$.next(true)))
    .pipe(
      switchMap(query =>
        this.gameService.getGames(`${environment.API_ENDPOINT}/games`, query)
      )
    )
    .pipe(tap(() => this.loadingGames$.next(false)));

  setSearch(value: string) {
    this.search.setValue(value);
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(qpm => {
      this.setSearch(qpm.get('search') || '');
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
}

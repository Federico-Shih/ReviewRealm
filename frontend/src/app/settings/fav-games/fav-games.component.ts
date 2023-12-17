import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {GamesService} from "../../shared/data-access/games/games.service";
import {
  BehaviorSubject,
  debounceTime,
  distinctUntilChanged,
  lastValueFrom,
  map,
  Observable,
  switchMap,
  tap
} from "rxjs";
import {Paginated} from "../../shared/data-access/shared.models";
import {Game} from "../../shared/data-access/games/games.class";
import {environment} from "../../../environments/environment";
import {AuthenticationService} from '../../shared/data-access/authentication/authentication.service';
import {User} from '../../shared/data-access/users/users.class';
import {ReviewsService} from '../../shared/data-access/reviews/reviews.service';
import {UsersService} from "../../shared/data-access/users/users.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-fav-games',
  templateUrl: './fav-games.component.html',
  styleUrls: ['./fav-games.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FavGamesComponent implements OnInit {
  loggedInUser$: Observable<User | null> = this.authService.getLoggedUser();
  favGames$ = new BehaviorSubject<Paginated<Game> | null>(null);
  search$ = new BehaviorSubject('');
  searchedGames$ = new BehaviorSubject<Paginated<Game> | null>(null);
  loadingSearch$ = new BehaviorSubject(false);
  searchResults$ = this.search$.pipe(
    tap(() => this.loadingSearch$.next(true)),
    debounceTime(250),
    distinctUntilChanged(),
    switchMap((query) => this.gamesService.getGames(environment.API_ENDPOINT + '/games', {search: query, pageSize: 6})),
    tap(() => this.loadingSearch$.next(false)),
  );
  searchCount$ = this.searchedGames$.pipe(map((paginatedGames) => {
    if (paginatedGames === null) return 0;
    return paginatedGames.content.reduce((acc, game) => acc + (!game.isFavourite ? 1 : 0), 0);
  }));

  constructor(private readonly gamesService: GamesService,
              private readonly authService: AuthenticationService,
              private readonly reviewsService: ReviewsService,
              private readonly userService: UsersService,
              private readonly snackbar: MatSnackBar,
              private readonly translate: TranslateService
  ) {

  }

  loadNextPage(nextPage: string | undefined) {
    if (nextPage) {
      this.gamesService.getGames(nextPage, {}).subscribe((paginatedGames) => {
        const prev = this.favGames$.getValue()!;
        this.favGames$.next({
          ...prev,
          content: prev.content.concat(paginatedGames.content),
          links: paginatedGames.links
        });
      });
    }
  }

  ngOnInit(): void {
    this.loggedInUser$.pipe(
      tap(() => this.loadingSearch$.next(true)),
      switchMap((user) => this.gamesService.getGames(user?.links.favoriteGames!, {pageSize: 10}))
    ).subscribe((paginatedGames) => {
      this.loadingSearch$.next(false);
      this.favGames$.next(paginatedGames);
    });

    this.searchResults$.subscribe((paginatedGames) => {
      this.searchedGames$.next(paginatedGames);
    });

    this.searchCount$.subscribe((hasResults) => {
      const curr = this.searchedGames$.getValue();
      if (hasResults < 4 && curr?.links.next) {
        this.gamesService.getGames(curr.links.next, {}).subscribe((paginatedGames) =>
          this.searchedGames$.next({...paginatedGames, content: curr.content.concat(paginatedGames.content)}));
      }
    });
  }

  addFavoriteGame(game: Game): void {
    if (game.links.addToFavoriteGames) {
      this.userService.addFavoriteGame(game.links.addToFavoriteGames, {gameId: game.id}).subscribe({
        next: async () => {
          const prev = this.favGames$.getValue()!;
          const updatedGame = await lastValueFrom(this.gamesService.getGame(game.links.self));

          const previousSearch = this.searchedGames$.getValue()!;
          this.searchedGames$.next({
            ...previousSearch,
            content: previousSearch.content.filter((g) => g.id !== game.id)
          });
          this.favGames$.next({...prev, content: [updatedGame].concat(prev.content)});
          this.snackbar.open(this.translate.instant('fav-games.success.add'), undefined, {
            panelClass: 'green-snackbar'
          });
        },
        error: () => {
          this.snackbar.open(this.translate.instant('fav-games.errors.add'), undefined, {
            panelClass: 'red-snackbar'
          });
        }
      });
    }
  }

  deleteFavoriteGame(game: Game): void {
    if (game.links.deleteFromFavoriteGames) {
      this.userService.deleteFavoriteGame(game.links.deleteFromFavoriteGames).subscribe({
        next: () => {
          const prev = this.favGames$.getValue()!;
          this.favGames$.next({...prev, content: prev.content.filter((g) => g.id !== game.id)});
          this.snackbar.open(this.translate.instant('fav-games.success.delete'), undefined, {
            panelClass: 'green-snackbar'
          });
        },
        error: () => {
          this.snackbar.open(this.translate.instant('fav-games.errors.delete'), undefined, {
            panelClass: 'red-snackbar'
          });
        }
      });
    }
  }
}

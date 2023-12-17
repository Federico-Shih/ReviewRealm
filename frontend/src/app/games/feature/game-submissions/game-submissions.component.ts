import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { SharedModule } from '../../../shared/shared.module';
import { AsyncPipe, CommonModule } from '@angular/common';
import { Game } from '../../../shared/data-access/games/games.class';
import { Paginated } from '../../../shared/data-access/shared.models';
import { BehaviorSubject, combineLatest } from 'rxjs';
import { GamesService } from '../../../shared/data-access/games/games.service';
import { environment } from '../../../../environments/environment';
import { GameSubmissionEvent } from '../../ui/game-submission-card/game-submission-card.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';
import { FormControl } from '@angular/forms';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
@Component({
  selector: 'app-game-submissions',
  templateUrl: './game-submissions.component.html',
  styleUrls: ['./game-submissions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GameSubmissionsComponent implements OnInit {
  static PERCENTAGE_SCROLLED = 0.8;

  submissions$ = new BehaviorSubject<Paginated<Game>>({
    content: [],
    links: {
      self: '',
    },
    totalPages: 0,
    totalElements: 0,
  });
  loading$ = new BehaviorSubject<boolean>(true);
  loadingInfinite$ = new BehaviorSubject<boolean>(false);
  loadingSubmissions$ = combineLatest({
    loading: this.loading$,
    submissions: this.submissions$,
    loadingInfinite: this.loadingInfinite$,
  });
  constructor(
    private gameService: GamesService,
    private snackBar: MatSnackBar,
    private readonly translate: TranslateService,
    public dialog: MatDialog
  ) {}
  ngOnInit() {
    this.reloadSubmisions();
  }

  reloadSubmisions() {
    this.gameService
      .getGames(`${environment.API_ENDPOINT}/games/`, { suggested: true })
      .subscribe({
        next: submissions => {
          this.submissions$.next(submissions);
          this.loading$.next(false);
        },
        error: () => {
          this.loading$.next(false);
        },
      });
  }
  nextPage(event: Event) {
    if (
      event.target instanceof HTMLElement &&
      event.target.scrollTop /
        (event.target.scrollHeight - event.target.clientHeight) >
        GameSubmissionsComponent.PERCENTAGE_SCROLLED &&
      this.submissions$.value.links.next &&
      !this.loadingInfinite$.value &&
      !this.loading$.value
    ) {
      this.loadingInfinite$.next(true);
      this.gameService
        .getGames(this.submissions$.value.links.next, { suggested: true })
        .subscribe({
          next: submissions => {
            this.submissions$.next({
              content: this.submissions$.value.content.concat(
                submissions.content
              ),
              links: submissions.links,
              totalPages: submissions.totalPages,
              totalElements: submissions.totalElements,
            });
            this.loadingInfinite$.next(false);
          },
          complete: () => {
            this.loadingInfinite$.next(false);
          },
        });
    }
  }
  handleSubmission(event: GameSubmissionEvent) {
    if (event.game.links.self) {
      this.gameService
        .handleGameSubmission(event.game.links.self, { accept: event.accept })
        .subscribe(() => {
          this.loading$.next(true);
          this.snackBar.open(
            this.translate.instant('submission.handled'),
            undefined,
            {
              panelClass: ['green-snackbar'],
            }
          );
          this.reloadSubmisions();
        });
    }
  }
  openSearch() {
    this.dialog.open(GameSubmissionSearchDialogComponent, {
      maxHeight: '100vh',
      height: '100%',
      minWidth: '80%',
    });
  }
}

@Component({
  selector: 'app-game-submission-search-dialog',
  templateUrl: './game-submission-search-dialog.html',
  imports: [
    MatDialogModule,
    SharedModule,
    AsyncPipe,
    CommonModule,
    MatProgressSpinnerModule,
  ],
  standalone: true,
})
export class GameSubmissionSearchDialogComponent {
  search = new FormControl('');

  loading$ = new BehaviorSubject<boolean>(false);

  games$ = new BehaviorSubject<Paginated<Game>>({
    content: [],
    links: {
      self: '',
    },
    totalPages: 0,
    totalElements: 0,
  });

  setSearch(value: string) {
    this.search.setValue(value);
  }
  submitSearch() {
    if (this.search.value) {
      this.loading$.next(true);
      this.gameService
        .getGames(`${environment.API_ENDPOINT}/games`, {
          search: this.search.value,
        })
        .subscribe({
          next: games => {
            this.games$.next(games);
            this.loading$.next(false);
          },
          complete: () => {
            this.loading$.next(false);
          },
        });
    }
  }
  constructor(private readonly gameService: GamesService) {}
}

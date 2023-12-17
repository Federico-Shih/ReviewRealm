import { ChangeDetectionStrategy, Component } from '@angular/core';
import { AuthenticationService } from '../../../shared/data-access/authentication/authentication.service';
import { Router } from '@angular/router';
import { BehaviorSubject, map, of, switchMap } from 'rxjs';
import { GameFormType, Role } from '../../../shared/data-access/shared.enums';
import { EnumsService } from '../../../shared/data-access/enums/enums.service';
import { environment } from '../../../../environments/environment';
import { MatSnackBar } from '@angular/material/snack-bar';
import { GamesService } from '../../../shared/data-access/games/games.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { SharedModule } from '../../../shared/shared.module';
import { AsyncPipe, CommonModule } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-game-submit',
  templateUrl: './game-submit.component.html',
  styleUrls: ['./game-submit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GameSubmitComponent {
  activeUser$ = this.authService.getLoggedUser();
  isMod = false;

  isModerator$ = this.activeUser$.pipe(
    map(user => {
      this.isMod = user !== null && user.role === Role.MODERATOR;
      return this.isMod;
    })
  );

  genres$ = this.genreService.getGenres(`${environment.API_ENDPOINT}/genres`);

  loading$ = new BehaviorSubject(false);

  constructor(
    private readonly authService: AuthenticationService,
    private readonly genreService: EnumsService,
    private readonly gameService: GamesService,
    private readonly router: Router,
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
    private readonly translate: TranslateService
  ) {}

  createGame(formData: FormData) {
    const dialogRef = this.dialog.open(GameSubmitDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log(formData);
        this.loading$.next(true);
        this.gameService
          .createGame(`${environment.API_ENDPOINT}/games`, formData)
          .subscribe({
            error: err => {
              this.snackBar.open(
                this.translate.instant('errors.unknown'),
                this.translate.instant('errors.dismiss'),
                {
                  panelClass: ['red-snackbar'],
                }
              );
            },
            next: () => {
              this.loading$.next(false);
              this.snackBar.open(
                this.translate.instant(
                  this.isMod ? 'game.created' : 'game.submitted'
                ),
                undefined,
                {
                  panelClass: ['green-snackbar'],
                }
              );
              this.router.navigate(['/games']);
            },
          });
      }
    });
  }
  protected readonly GameFormType = GameFormType;
}
@Component({
  selector: 'app-game-submit-dialog',
  templateUrl: './game-submit-dialog.html',
  imports: [MatDialogModule, SharedModule, AsyncPipe, CommonModule],
  standalone: true,
})
export class GameSubmitDialogComponent {
  activeUser$ = this.authService.getLoggedUser();
  isModerator$ = this.activeUser$.pipe(
    map(user => {
      return user !== null && user.role === Role.MODERATOR;
    })
  );
  constructor(private readonly authService: AuthenticationService) {}
}

import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {BehaviorSubject, catchError, Observable, of, switchMap, tap} from "rxjs";
import {Role,GameFormType} from "../../../shared/data-access/shared.enums";
import {environment} from "../../../../environments/environment";
import {AuthenticationService} from "../../../shared/data-access/authentication/authentication.service";
import {GamesService} from "../../../shared/data-access/games/games.service";
import {EnumsService} from "../../../shared/data-access/enums/enums.service";
import {ActivatedRoute, Router} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Game} from "../../../shared/data-access/games/games.class";
import {TranslateService} from "@ngx-translate/core";
import {SharedModule} from "../../../shared/shared.module";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {AsyncPipe, CommonModule} from "@angular/common";

@Component({
  selector: 'app-game-edit',
  templateUrl: './game-edit.component.html',
  styleUrls: ['./game-edit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameEditComponent implements OnInit{

  activeUser$ = this.authService.getLoggedUser();

  isModerator$ = this.activeUser$.pipe(switchMap(user => {
    return of(user !== null && user.role === Role.MODERATOR);
  }));

  genres$ = this.genreService.getGenres(`${environment.API_ENDPOINT}/genres`);

  loading$ = new BehaviorSubject(false);

  routeId: number | null = null;

  game$: Observable<Game> = this.activatedRoute.paramMap.pipe(
    switchMap((params) => {
      return this.gameService.getGame(`${environment.API_ENDPOINT}/games/` + params.get('id'));
    }), catchError((err, caught) => {
      this.router.navigate(['errors/not-found']);
      return caught;
    }),tap((game) => {
      this.game = game;
    }),
  );
  game: Game | null = null;

  constructor(private readonly authService:AuthenticationService,
              private readonly gameService:GamesService,
              private readonly genreService:EnumsService,
              private readonly activatedRoute:ActivatedRoute,
              private readonly router:Router,
              public  dialog: MatDialog,
              private snackBar:MatSnackBar,
              private readonly translate:TranslateService,) {
  }
  ngOnInit() {
    this.activatedRoute.paramMap.subscribe((params) => {
      const id = params.get('id');
      if (!(id && Number.isInteger(parseInt(id)) && parseInt(id) > 0)){
        this.router.navigate(['/errors/not-found']);
      }
      this.routeId = parseInt(id!!);
    });
  }

    editGame(formData:FormData){
        const dialogRef = this.dialog.open(GameEditDialogComponent);
        dialogRef.afterClosed().subscribe((result) => {
          if(result && this.game !== null){
            console.log(formData);
            this.loading$.next(true);
            this.gameService.editGame(this.game.links.self,formData).subscribe((game) =>{
              this.loading$.next(false);
              this.snackBar.open(
                this.translate.instant('game.edited'), undefined,
                {
                  panelClass: ['green-snackbar']
                });
              this.router.navigate(['/games',`${game.id}`],);
            });
          }
        });
  }
  protected readonly GameFormType = GameFormType

}
@Component({
  selector: 'app-game-edit-dialog',
  templateUrl: './game-edit-dialog.html',
  imports: [
    MatDialogModule,
    SharedModule,
    AsyncPipe,
    CommonModule,
  ],
  standalone: true
})
export class GameEditDialogComponent {
  constructor() {
  }
}

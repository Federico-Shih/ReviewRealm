import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AuthenticationService} from "../../../shared/data-access/authentication/authentication.service";
import {Router} from "@angular/router";
import {BehaviorSubject, of, switchMap} from "rxjs";
import {GameFormType, Role} from "../../../shared/data-access/shared.enums";
import {EnumsService} from "../../../shared/data-access/enums/enums.service";
import {environment} from "../../../../environments/environment";
import {MatSnackBar} from "@angular/material/snack-bar";
import {GamesService} from "../../../shared/data-access/games/games.service";

@Component({
  selector: 'app-game-submit',
  templateUrl: './game-submit.component.html',
  styleUrls: ['./game-submit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameSubmitComponent {

  activeUser$ = this.authService.getLoggedUser();

  isModerator$ = this.activeUser$.pipe(switchMap(user => {
    return of(user !== null && user.role === Role.MODERATOR);
  }));

  genres$ = this.genreService.getGenres(`${environment.API_ENDPOINT}/genres`);

  loading$ = new BehaviorSubject(false);

  constructor(private readonly authService:AuthenticationService,
              private readonly genreService:EnumsService,
              private readonly gameService:GamesService,
              private readonly router:Router,
              private _snackBar:MatSnackBar) {
  }

  createGame(formData:FormData){
    this.loading$.next(true);
    this.gameService.createGame(`${environment.API_ENDPOINT}/games`,formData).subscribe(() =>{
      this.loading$.next(false);
      this.router.navigate(['/games']);//TODO:success snackbar
    });

  }
  protected readonly GameFormType = GameFormType;
}

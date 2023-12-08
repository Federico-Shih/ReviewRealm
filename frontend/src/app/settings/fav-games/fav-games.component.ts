import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {GamesService} from "../../shared/data-access/games/games.service";
import {catchError, Observable, switchMap} from "rxjs";
import {Paginated} from "../../shared/data-access/shared.models";
import {Game} from "../../shared/data-access/games/games.class";
import {GameExclusiveSearchDto} from "../../shared/data-access/games/games.dtos";
import {environment} from "../../../environments/environment";
import {query} from "@angular/animations";

@Component({
  selector: 'app-fav-games',
  templateUrl: './fav-games.component.html',
  styleUrls: ['./fav-games.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FavGamesComponent implements OnInit{
  favGamesCandidate= [];
  favGames= [];



  constructor(gamesService: GamesService) {

  }

  ngOnInit(): void {
  }
}

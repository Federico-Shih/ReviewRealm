import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {GamesService} from "../../../shared/data-access/games/games.service";
import {Game} from "../../../shared/data-access/games/games.class";
import {
  GameFiltersDto,
  GameSearchDto,
  GameSortType,
  isGameSortType,
  RatingType
} from "../../../shared/data-access/games/games.dtos";
import {ActivatedRoute, Router} from "@angular/router";
import {BehaviorSubject, map, switchMap} from "rxjs";
import {Paginated} from "../../../shared/data-access/shared.models";
import {isSortDirection, SortDirection} from "../../../shared/data-access/shared.enums";

@Component({
  selector: 'app-game-list',
  templateUrl: './game-list.component.html',
  styleUrls: ['./game-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameListComponent{

    games$ = this.route.queryParamMap.pipe(map((params) => {
      const sort = params.get('sort') || '';
      const direction = params.get('direction') || '';
      let query:GameSearchDto  = {
        search: params.get('search') || '',
        excludeNoRating: params.get('excludeNoRating') === 'true',
        genres: params.getAll('genres').map((genre) => parseInt(genre)) || undefined,
        rating: params.get('rating') as RatingType || '0t10',
        page: parseInt(params.get('page') || '1') || 1,
        pageSize: parseInt(params.get('pageSize') || '10') || 10,
        sort: isGameSortType(sort) ? sort : undefined,
        direction: isSortDirection(direction) ? direction : undefined
      };
      return query;
      }),switchMap((query) => this.gameService.getGames('http://localhost:8080/paw-2023a-04/api/games/',query)));


    constructor(private readonly gameService:GamesService, private route: ActivatedRoute){

    }
}

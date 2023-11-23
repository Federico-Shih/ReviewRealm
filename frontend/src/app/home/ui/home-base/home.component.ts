import {Component, OnInit} from '@angular/core';
import {UsersService} from "../../../shared/data-access/users/users.service";
import {AuthenticationService} from "../../../shared/data-access/authentication/authentication.service";
import {SortDirection} from "../../../shared/data-access/shared.enums";
import {UserSortCriteria} from "../../../shared/data-access/users/users.dtos";
import {ReviewsService} from "../../../shared/data-access/reviews/reviews.service";
import {GamesService} from "../../../shared/data-access/games/games.service";

export type EnumType<T> = {
  translateKey: string;
  selectKey: T;
}


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  constructor(private readonly userService: UsersService,
              private readonly auth: AuthenticationService,
              private readonly reviewService: ReviewsService,
              private readonly gameService: GamesService) {
  }

  readonly sortDirections = Object.values(SortDirection);

  orderDirections: EnumType<string>[] = Object.values(UserSortCriteria).map((str) => ({
    translateKey: str,
    selectKey: str
  }))

  ngOnInit(): void {
    // this.gameService.getGames('http://localhost:8080/paw-2023a-04/api/games', {}).subscribe(console.log);
    // this.reviewService.getReviews('http://localhost:8080/paw-2023a-04/api/reviews', {}).subscribe(console.log);
  }

  log(any: unknown) {
    console.log(any);
  }
}

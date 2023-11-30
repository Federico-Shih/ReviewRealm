import { ChangeDetectionStrategy, Component } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {UsersService} from "../../../shared/data-access/users/users.service";
import {catchError, Observable, switchMap} from "rxjs";
import {User} from "../../../shared/data-access/users/users.class";
import {GamesService} from "../../../shared/data-access/games/games.service";
import {
    GameExclusiveSearchDto
} from "../../../shared/data-access/games/games.dtos";
import {environment} from "../../../../environments/environment";
import {Game} from "../../../shared/data-access/games/games.class";
import {Paginated} from "../../../shared/data-access/shared.models";
import {ReviewsService} from "../../../shared/data-access/reviews/reviews.service";
import {Review} from "../../../shared/data-access/reviews/review.class";
import {ReviewSearchDto} from "../../../shared/data-access/reviews/reviews.dtos";


@Component({
  selector: 'app-profile-detail',
  templateUrl: './profile-detail.component.html',
  styleUrls: ['./profile-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProfileDetailComponent {

  user$: Observable<User> = this.route.paramMap.pipe(
      switchMap((params) => {
          return this.userService.getUserById(`${environment.API_ENDPOINT}/users/` + params.get('id'));
      }), catchError((err, caught) => {
          this.router.navigate(['/']); //TODO:404
          return caught;
      }),
  );

  favgames$: Observable<Paginated<Game>> = this.route.paramMap.pipe(
    switchMap((params) => {
      var numericId = Number(params.get('id'))
      let query:GameExclusiveSearchDto  = {
        favoriteOf: numericId
      };
      return this.gameService.getGames(`${environment.API_ENDPOINT}/games`, query);
    }), catchError((err, caught) => {
      this.router.navigate(['/']); //TODO: Qué hago acá?
      return caught;
    }),
  );

  reviewsbyuser$: Observable<Paginated<Review>> = this.route.paramMap.pipe(
    switchMap((params) => {
      var numericId = [Number(params.get('id'))]
      let query:ReviewSearchDto  = {
        authors: numericId
      };
      return this.reviewService.getReviews(`${environment.API_ENDPOINT}/reviews`, query);
    }), catchError((err, caught) => {
      this.router.navigate(['/']); //TODO: Qué hago acá?
      return caught;
    }),
  );

  constructor(private readonly userService: UsersService,private readonly reviewService: ReviewsService, private readonly gameService: GamesService, private readonly router: Router, private route: ActivatedRoute) {
  }

}

import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {GamesService} from "../../../shared/data-access/games/games.service";
import {ReviewsService} from "../../../shared/data-access/reviews/reviews.service";
import {AuthenticationService} from "../../../shared/data-access/authentication/authentication.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Game} from "../../../shared/data-access/games/games.class";
import {catchError, map, Observable, of, pipe, Subject, switchMap, tap} from "rxjs";
import {Review} from "../../../shared/data-access/reviews/review.class";
import {Paginated, PaginatedLinks} from "../../../shared/data-access/shared.models";
import {environment} from "../../../../environments/environment";
import {Role} from "../../../shared/data-access/shared.enums";
import {PageEvent} from "@angular/material/paginator";

@Component({
  selector: 'app-game-detail',
  templateUrl: './game-detail.component.html',
  styleUrls: ['./game-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameDetailComponent implements OnInit {

  loggedUser$ = this.authService.getLoggedUser();

  isModerator$ = this.loggedUser$.pipe(map((user) => {
    return user && user.role === Role.MODERATOR;
  }));

  game$: Observable<Game> = this.activatedRoute.paramMap.pipe(
    switchMap((params) => {
      return this.gameService.getGame(`${environment.API_ENDPOINT}/games/` + params.get('id'));
    }), catchError((err, caught) => {
      this.router.navigate(['/games']); //TODO:404
      return caught;
    }),
  );
  paginatedReviews$: Observable<Paginated<Review>> = this.game$.pipe(switchMap((game) => {
    if (game.links.reviewsExcludingUser) {
      return this.reviewService.getReviews(game.links.reviewsExcludingUser, {});
    }
    return this.reviewService.getReviews(game.links.reviews, {});
  }));

  userReview$: Observable<Paginated<Review> | null> = this.game$.pipe(switchMap((game) => {
    if (game.links.userReview) {
      return this.reviewService.getReviews(game.links.userReview, {});
    }
    return of(null);
  }));

  constructor(private readonly gameService: GamesService,
              private readonly reviewService: ReviewsService,
              private readonly authService: AuthenticationService,
              private readonly activatedRoute: ActivatedRoute,
              private readonly router: Router
  ) {
  }

  ngOnInit(): void {
    this.activatedRoute.paramMap.subscribe((params) => {
      const id = params.get('id');
      if (!(id && Number.isInteger(parseInt(id)) && parseInt(id) > 0)){
        this.router.navigate(['/games']); //TODO:404
      }
    });
  }

}

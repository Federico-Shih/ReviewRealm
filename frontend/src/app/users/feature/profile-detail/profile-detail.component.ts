import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {UsersService} from "../../../shared/data-access/users/users.service";
import {BehaviorSubject, catchError, combineLatest, Observable, Subject, switchMap} from "rxjs";
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
import {AuthenticationService} from "../../../shared/data-access/authentication/authentication.service";
import {query} from "@angular/animations";


@Component({
  selector: 'app-profile-detail',
  templateUrl: './profile-detail.component.html',
  styleUrls: ['./profile-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProfileDetailComponent implements OnInit{

  user$: Observable<User> = this.route.paramMap.pipe(
      switchMap((params) => {
          return this.userService.getUserById(`${environment.API_ENDPOINT}/users/` + params.get('id'));
      }), catchError((err, caught) => {
          this.router.navigate(['/']); //TODO:404
          return caught;
      }),
  );

  loggedUser$ = this.authService.getLoggedUser();

  combinedUsers$  = combineLatest([this.loggedUser$, this.user$])


  favgames$: Observable<Paginated<Game>> = this.route.paramMap.pipe(
    switchMap((params) => {
      const numericId = Number(params.get('id'))
      let query:GameExclusiveSearchDto  = {
        favoriteOf: numericId
      };
      return this.gameService.getGames(`${environment.API_ENDPOINT}/games`, query);
    }), catchError((err, caught) => {
      this.router.navigate(['/']); //TODO: Qué hago acá?
      return caught;
    }),
  );

  initialReviews$: Observable<Paginated<Review>> = this.route.paramMap.pipe(
    switchMap((params) => {
      const numericId = [Number(params.get('id'))];
      let query:ReviewSearchDto  = {
        authors: numericId,
        pageSize: 10
      };
      return this.reviewService.getReviews(`${environment.API_ENDPOINT}/reviews`, query);
    }), catchError((err, caught) => {
      this.router.navigate(['/']); //TODO: Qué hago acá?
      return caught;
    }),
  );


  userReviews$ :BehaviorSubject<Paginated<Review> | null> = new BehaviorSubject<Paginated<Review>|null>(null);

  constructor(private readonly userService: UsersService,private readonly reviewService: ReviewsService, private readonly gameService: GamesService, private readonly router: Router, private route: ActivatedRoute, private readonly authService: AuthenticationService) {
  }

    showMore(next: string): void {
      this.reviewService.getReviews(next,{}).subscribe((pageInfo) => {
         const currentReviews = this.userReviews$.getValue();
         if (currentReviews!== null)
         {
             const newReviews = currentReviews.content.concat(pageInfo.content)
             this.userReviews$.next({content: newReviews, links: pageInfo.links, totalPages: pageInfo.totalPages})
         }
      });
    }


    ngOnInit(): void {
        this.initialReviews$.subscribe(
        (pageInfo) => this.userReviews$.next(pageInfo)
        )
    }

}

import { Injectable } from '@angular/core';
import {InfiniteLoadService} from "./infinite-load.interface";
import {Game} from "../data-access/games/games.class";
import {GameSearchDto} from "../data-access/games/games.dtos";
import {Review} from "../data-access/reviews/review.class";
import {ReviewSearchDto} from "../data-access/reviews/reviews.dtos";
import {UserSearchDto} from "../data-access/users/users.dtos";
import {User} from "../data-access/users/users.class";

@Injectable()
export class GameInfiniteLoadService extends InfiniteLoadService<Game, GameSearchDto> {
  constructor() {
    super();
  }
}

@Injectable()
export class ReviewInfiniteLoadService extends InfiniteLoadService<Review, ReviewSearchDto> {
  constructor() {
    super();
  }
}


@Injectable()
export class UserInfiniteLoadService extends InfiniteLoadService<User, UserSearchDto> {
  constructor() {
    super();
  }
}

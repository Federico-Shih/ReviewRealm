import {Paginated} from "../../app/shared/data-access/shared.models";
import {Game} from "../../app/shared/data-access/games/games.class";
import {gameMock} from "./games.mock";
import {Observable, of} from "rxjs";

export const gameServiceMock = {
  getGames: (): Observable<Paginated<Game>> => {
    return of({
      totalElements: 1, totalPages: 1,
      content: [gameMock],
      links: {
        self: '',
        next: 'more',
        prev: '',
      }
    });
  }
}

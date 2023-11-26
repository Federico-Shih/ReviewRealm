import {PaginatedDto, SortedDto} from "../shared.dtos";
import {Difficulty, Platform} from "../shared.enums";

/*created|rating|popularity|controversial*/
export enum ReviewSortType {
  CREATED = "created",
  RATING = "rating",
  POPULARITY = "popularity",
  CONTROVERSIAL = "controversial"
}

export const isReviewSortType = (reviewSortType: unknown): reviewSortType is ReviewSortType => {
  return Object.values(ReviewSortType).find((type) => type === reviewSortType) !== undefined;
}

export type ReviewFiltersDto = {
  gameGenres: number[];

  excludeAuthors: number[];
  authors: number[];
  authorPreferences: number[];

  platforms: Platform[];
  difficulty: Difficulty[];
  completed: boolean;
  replayable: boolean;
  timeplayed: number;

  search: string;
  gameId: number;
}


export type ReviewSearchDto = Partial<ReviewFiltersDto & PaginatedDto & SortedDto<ReviewSortType>>

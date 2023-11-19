/* name|averageRating|publishDate*/
import {PaginatedDto, SortedDto} from "../shared.dtos";

export enum GameSortType {
  NAME = "name",
  AVERAGE_RATING = "averageRating",
  PUBLISH_DATE = "publishDate"
}

export type RatingType = `${number}t${number}`;

export type GameFiltersDto = {
  search: string;
  excludeNoRating
  suggested: boolean;
  genres: number[];
  rating: RatingType;
  favoriteOf: number;
}

export type GameRecommendedFilterDto = {
  recommendedFor: number;
}

export type GameSearchDto = Partial<(GameFiltersDto | GameRecommendedFilterDto) & PaginatedDto & SortedDto<GameSortType>>

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
  excludeNoRating: boolean;
  suggested: boolean;
  genres: number[];
  rating: RatingType;
}

export type GameRecommendedFilterDto = {
  recommendedFor: number;
}
export type GameFavoriteFilterDto = {
  favoriteOf: number;
}

export type GameSearchDto = Partial<(GameFiltersDto | GameRecommendedFilterDto | GameFavoriteFilterDto) & PaginatedDto & SortedDto<GameSortType>>

/* name|averageRating|publishDate*/
import {PaginatedDto, SortedDto} from "../shared.dtos";
import {isFloat} from "../../helpers/utils";

export enum GameSortType {
  NAME = "name",
  AVERAGE_RATING = "averageRating",
  PUBLISH_DATE = "publishDate"
}
export function isGameSortType(sort: string): sort is GameSortType {
  return Object.values(GameSortType).includes(sort as GameSortType);
}

export type RatingType = `${number}t${number}`;

export const isRatingType = (rating: unknown): rating is RatingType => {
  if( rating === undefined || rating === null || typeof rating !== 'string') return false;
  const range = rating.split('t');
  if(range.length !== 2) return false;
  const [min, max] = range;
  const floatMin = parseFloat(min);
  const floatMax = parseFloat(max);
  return isFloat(floatMin) && isFloat(floatMax) && floatMin <= floatMax;
}

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

export type GameNotReviewedByFilterDto = {
  notReviewedBy: number;
  search: string;
}

export type GameSearchDto = Partial<(GameFiltersDto & PaginatedDto & SortedDto<GameSortType>)>;

export type GameNotReviewedBySearchDto = Partial<(GameNotReviewedByFilterDto & PaginatedDto & SortedDto<GameSortType>)>;

export type GameExclusiveSearchDto = Partial<( (GameRecommendedFilterDto|GameFavoriteFilterDto ) & PaginatedDto)>;

export type GameSubmissionHandleDto = {
  accept: boolean;
}

export enum GameMediaTypes {
  APPLICATION_GAME_SUGGESTION_FORM = "application/vnd.game-suggestion-form.v1+json",
  APPLICATION_GAME= "application/vnd.game.v1+json",
  APPLICATION_FAVORITE_GAME_FORM = "application/vnd.favorite-game-form.v1+json",
  APPLICATION_GAME_LIST = "application/vnd.game-list.v1+json"
}

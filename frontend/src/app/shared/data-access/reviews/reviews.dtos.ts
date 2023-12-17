import { PaginatedDto, SortedDto } from '../shared.dtos';
import { Difficulty, Platform } from '../shared.enums';

export enum ReviewMediaTypes {
  REPORTREVIEW = 'application/vnd.report-form.v1+json',
  CREATEREVIEW = 'application/vnd.review-form.v1+json',
  EDITREVIEW = 'application/vnd.review-update.v1+json',
}

/*created|rating|popularity|controversial*/
export enum ReviewSortType {
  CREATED = 'created',
  RATING = 'rating',
  POPULARITY = 'popularity',
  CONTROVERSIAL = 'controversial',
}

export const isReviewSortType = (
  reviewSortType: unknown
): reviewSortType is ReviewSortType => {
  return (
    Object.values(ReviewSortType).find(type => type === reviewSortType) !==
    undefined
  );
};

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
};

export type ReviewSearchDto = Partial<
  ReviewFiltersDto & PaginatedDto & SortedDto<ReviewSortType>
>;

export type ReportReviewDto = {
  reviewId: number;
  reason: string;
};

export type ReviewSubmitDto = {
  reviewTitle: string;
  reviewContent: string;
  replayability: boolean;
  completed: boolean;
  reviewRating: number;
  platform?: string;
  difficulty?: string;
  gameLength?: number;
  unit?: string;
  gameId?: number;
};

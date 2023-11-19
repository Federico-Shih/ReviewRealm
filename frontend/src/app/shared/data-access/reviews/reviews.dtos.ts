
import {PaginatedDto, SortedDto} from "../shared.dtos";

/*created|rating|popularity|controversial*/
export enum ReviewSortType {
    CREATED = "created",
    RATING = "rating",
    POPULARITY = "popularity",
    CONTROVERSIAL = "controversial"
}

export type ReviewFiltersDto = {
    gameGenres: number[];

    excludeAuthors: number[];
    authors: number[];
    authorPreferences: number[];

    platformsFilter: string[];
    difficultyFilter: string[];
    completedFilter: boolean;
    replayableFilter: boolean;

    timePlayedFilter: string;

    search: string;
    gameId: number;
}

export type ReviewRecommendedFilterDto = {
    recommendedFor: number;
}

export type ReviewSearchDto = Partial<(ReviewFiltersDto | ReviewRecommendedFilterDto) & PaginatedDto & SortedDto<ReviewSortType>>

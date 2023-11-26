import {ParamMap} from "@angular/router";
import {isReviewSortType, ReviewSearchDto} from "../../shared/data-access/reviews/reviews.dtos";
import {isDifficulty, isPlatform, isSortDirection} from "../../shared/data-access/shared.enums";
import {isBoolean} from "../../shared/helpers/utils";

// Precondición: checked.length === total.length
export const mapCheckedToType = <T, K>(checked: boolean[], total: T[], selector: (type: T) => K): K[] => {
  return checked.map((checked, i) => ({checked, type: total[i]}))
    .filter((genre) => genre.checked)
    .map((genre) => {
      return selector(genre.type);
    });
}

export const paramsMapToReviewSearchDto = (params: ParamMap): ReviewSearchDto => {
  const genres = params.getAll('gameGenres')
    .filter((genre) => !isNaN(genre as unknown as number))
    .map((genre) => parseInt(genre));
  const excludedAuthors = params.getAll('excludedAuthors')
    .filter((userId) => !isNaN(userId as unknown as number))
    .map((genre) => parseInt(genre));
  const authorPrefs = params.getAll('preferences')
    .filter((genre) => !isNaN(genre as unknown as number))
    .map((genre) => parseInt(genre));
  const sort = params.get('sort');
  const direction = params.get('direction');
  const platforms = params.getAll('platforms')
    .filter(isPlatform);
  const difficulties = params.getAll('difficulty')
    .filter(isDifficulty);
  const completed = isBoolean(params.get('completed')) ? params.get('completed') === 'true' : undefined;
  const replayable = isBoolean(params.get('replayable')) ? params.get('replayable') === 'true' : undefined;
  const timeplayedRaw = (params.get('timeplayed')) || '0';

  const timeplayed = parseFloat(timeplayedRaw) || undefined;
  const search = params.get('search') ? params.get('search')! : undefined;

  return {
    page: parseInt(params.get('page') || '1') || 1,
    pageSize: parseInt(params.get('pageSize') || '10') || 10,
    gameGenres: genres.length !== 0 ? genres : undefined,
    excludeAuthors: excludedAuthors.length !== 0 ? excludedAuthors : undefined,
    authorPreferences: authorPrefs.length !== 0 ? authorPrefs : undefined,
    sort: isReviewSortType(sort) ? sort : undefined,
    direction: isSortDirection(direction) ? direction : undefined,
    platforms,
    difficulty: difficulties,
    completed,
    replayable,
    timeplayed,
    search
  };
}

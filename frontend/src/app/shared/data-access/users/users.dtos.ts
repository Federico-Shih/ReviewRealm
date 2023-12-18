import {PaginatedDto, SortedDto} from '../shared.dtos';

export enum UserMediaTypes {
  CREATEUSER = 'application/vnd.user-form.v1+json',
  CREATE_FAVORITE_GAME = 'application/vnd.favorite-game-form.v1+json',
  CHANGE_PASSWORD = 'application/vnd.password-reset.v1+json',
}

export type UserSearchDto = Partial<
  {
    followers: number;
    search: string;
    username: string;
    email: string;
    id: number;
    preferences: number[];
    gamesPlayed: number[];
    following: number;
    samePreferencesAs: number;
    sameGamesPlayedAs: number;
  } & SortedDto<UserSortCriteria> &
    PaginatedDto
>;

export enum UserSortCriteria {
  FOLLOWERS = 'followers',
  LEVEL = 'level',
  REPUTATION = 'reputation',
}

export const isUserSortType = (
  userSortType: unknown
): userSortType is UserSortCriteria => {
  return (
    Object.values(UserSortCriteria).find(type => type === userSortType) !==
    undefined
  );
};

export type UserPatchDto = Partial<{
  password: string;
  genres: number[];
}>;

export type CredentialsDto = {
  email: string;
  token: string;
};

export type UserCreateDto = {
  username: string;
  email: string;
  password: string;
};

export type FavoriteGameCreateDto = {
  gameId: number;
};

export type GenresDto = {
  genres: number[];
};

export type NotificationsDto = {
  userIFollowWritesReview: boolean;
  myReviewIsDeleted: boolean;
};

export type AvatarDto = {
  avatarId: number;
};

export type FollowDto = {
    userId: number;
};

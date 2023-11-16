import {PaginatedDto, SortedDto} from "../dtos";

// TODO: add all user media types that are relevant
export enum UserMediaTypes {
  CREATEUSER = "application/vnd.user-form.v1+json",
}

export type UserSearchDto = Partial<{
  followers: number;
  search: string;
  username: string;
  email: string;
  id: number;
  preferences: number[];
  gamesPlayed: number[];
  following: number;
} & SortedDto<SortCriteria> & PaginatedDto>;

export enum SortCriteria {
  FOLLOWERS = "followers",
  LEVEL = "level",
  REPUTATION = "reputation",
}


export type UserCreateDto = {
  username: string;
  email: string;
  password: string;
}

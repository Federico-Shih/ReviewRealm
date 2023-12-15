// TODO: add all user media types that are relevant
import {PaginatedDto, SortedDto} from "../shared.dtos";

export enum UserMediaTypes {
  CREATEUSER = "application/vnd.user-form.v1+json",
  CHANGE_PASSWORD = "application/vnd.password-reset.v1+json"
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
} & SortedDto<UserSortCriteria> & PaginatedDto>;

export enum UserSortCriteria {
  FOLLOWERS = "followers",
  LEVEL = "level",
  REPUTATION = "reputation",
}

export type UserPatchDto = Partial<{
  password: string;
  genres: number[];
}>

export type CredentialsDto = {
  email: string;
  token: string;
}

export type UserCreateDto = {
  username: string;
  email: string;
  password: string;
}

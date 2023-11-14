import {PaginatedDto, SortedDto} from "../dtos";

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

import {SortDirection} from "./enums";

export type SortedDto<T> = {
  sort: T;
  direction: SortDirection;
};

export type PaginatedDto = {
  page: number;
  pageSize: number;
}

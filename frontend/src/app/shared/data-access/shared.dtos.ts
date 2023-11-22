import {SortDirection} from "./shared.enums";

export type SortedDto<T> = {
  sort: T;
  direction: SortDirection;
};

export type PaginatedDto = {
  page: number;
  pageSize: number;
}

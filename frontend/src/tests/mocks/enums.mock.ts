import { Genre } from '../../app/shared/data-access/enums/enums.class';

export const genresMock1 = [];
export const genresMock2 = [
  new Genre(1, 'name', 'localized', { self: 'self' }),
];
export const genresMock4 = [
  new Genre(1, 'name', 'localized', { self: 'self' }),
  new Genre(2, 'name', 'localized', { self: 'self' }),
];

export const genresMock3 = [
  new Genre(1, 'name', 'localized', { self: 'self' }),
  new Genre(2, 'name', 'localized', { self: 'self' }),
  new Genre(3, 'name', 'localized', { self: 'self' }),
  new Genre(4, 'name', 'localized', { self: 'self' }),
  new Genre(5, 'name', 'localized', { self: 'self' }),
];

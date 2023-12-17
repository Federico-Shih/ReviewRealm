import { Game } from '../../app/shared/data-access/games/games.class';
import { genresMock1, genresMock2 } from './enums.mock';
import {
  Difficulty,
  Platform,
} from '../../app/shared/data-access/shared.enums';

export const gameMock = Game.fromResponse(
  {
    id: 1,
    description: 'description',
    averageDifficulty: Difficulty.Easy,
    averageGameTime: 0,
    completability: 0,
    developer: '',
    isFavorite: false,
    name: 'GameMock1',
    platform: Platform.PC,
    publishDate: '',
    publisher: '',
    ratingSum: 0,
    replayability: 0,
    reviewCount: 0,
    links: {
      self: 'self',
      genres: 'genres',
      reviews: 'reviews',
      image: 'images',
    },
  },
  genresMock1
);

export const gameMock2 = Game.fromResponse(
  {
    id: 1,
    description: 'description',
    averageDifficulty: Difficulty.Hard,
    averageGameTime: 0,
    completability: 0,
    developer: '',
    isFavorite: false,
    name: 'GameMock2',
    platform: Platform.NINTENDO,
    publishDate: '',
    publisher: '',
    ratingSum: 0,
    replayability: 0,
    reviewCount: 0,
    links: {
      self: 'self',
      genres: 'genres',
      reviews: 'reviews',
      image: 'images',
    },
  },
  genresMock2
);

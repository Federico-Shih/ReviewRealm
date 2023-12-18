import { Review } from '../../app/shared/data-access/reviews/review.class';
import {
  Difficulty,
  Platform,
} from '../../app/shared/data-access/shared.enums';
import { gameMock, gameMock2 } from './games.mock';
import { userMock1, userMock2 } from './user.mock';

export const ReviewMock = Review.fromResponse(
  {
    id: 1,
    title: 'title',
    content: 'content',
    rating: 5,
    authorId: 1,
    gameId: 1,
    difficulty: Difficulty.Easy,
    gameLength: 10,
    platform: Platform.PC,
    likes: 10,
    dislikes: 10,
    completed: true,
    replayable: true,
    created: '2020-10-10',
    links: {
      self: 'self',
      game: 'game',
      author: 'author',
    },
  },
  gameMock,
  userMock1
);

export const ReviewMock2 = Review.fromResponse(
  {
    id: 2,
    title: 'title',
    content: 'content',
    rating: 5,
    authorId: 1,
    gameId: 1,
    difficulty: Difficulty.Hard,
    gameLength: 10,
    platform: Platform.PC,
    likes: 10,
    dislikes: 10,
    completed: false,
    replayable: false,
    created: '2020-10-10',
    links: {
      self: 'self',
      game: 'game',
      author: 'author',
    },
  },
  gameMock2,
  userMock2
);

import { User } from '../../app/shared/data-access/users/users.class';
import { Role } from '../../app/shared/data-access/shared.enums';

export const userMock1 = User.fromResponse({
  followers: 0,
  following: 0,
  avatar: '',
  email: 'test@gmail.com',
  enabled: true,
  id: 0,
  links: {
    self: '',
    followers: '',
    following: '',
    preferences: '',
    favoriteGames: '',
    reviews: '',
  },
  reputation: 0,
  role: Role.USER,
  username: 'test1',
  xp: 0,
});

export const userMock2 = User.fromResponse({
  followers: 0,
  following: 0,
  avatar: '',
  email: 'test2@gmail.com',
  enabled: true,
  id: 1,
  links: {
    self: '',
    followers: '',
    following: '',
    preferences: '',
    favoriteGames: '',
    reviews: '',
  },
  reputation: 100,
  role: Role.MODERATOR,
  username: 'test2',
  xp: 10000,
});

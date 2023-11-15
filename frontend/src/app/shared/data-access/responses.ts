import {Role} from "./enums";

export type ReviewLinksResponse = {
  feedback: string,
  game: string,
  author: string,
  self: string
}

export type ReviewResponse = {
  id: number;
  title: string;
  content: string;
  rating: number;
  authorId: number;
  gameId: number;
  difficulty: string;
  gameLength: number;
  platform: string;
  likes: number;
  dislikes: number;
  completed: boolean;
  replayable: boolean;
  links: ReviewLinksResponse;
}

export type UserLinksResponse = {
  self: string;
  followers: string;
  following: string;
  preferences: string;
  favoriteGames: string;
  patchUser?: string;
  updateNotifications?: string;
  unfollow?: string;
  follow?: string;
}

export type UserResponse = {
  id: number;
  username: string;
  email: string;
  enabled: boolean;
  reputation: number;
  avatar: string;
  language?: string;
  xp: number;
  preferences: string[];
  links: UserLinksResponse;
  role: Role;
}

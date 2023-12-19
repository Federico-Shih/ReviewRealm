import { UserLinksResponse, UserResponse } from '../shared.responses';
import { Role } from '../shared.enums';
import { Genre } from '../enums/enums.class';

export class UserLinks {
  self: string;
  followers: string;
  following: string;
  preferences: string;
  favoriteGames: string;
  patchUser?: string;
  updateNotifications?: string;
  missionProgresses?: string;
  unfollow?: string;
  follow?: string;
  recommendedReviews?: string;
  newReviews?: string;
  followingReviews?: string;
  recommendedGames?: string;
  reviews: string;

  constructor(
    self: string,
    followers: string,
    following: string,
    preferences: string,
    favoriteGames: string,
    reviews: string,
    patchUser?: string,
    updateNotifications?: string,
    missionProgresses?: string,
    unfollow?: string,
    follow?: string,
    recommendedReviews?: string,
    newReviews?: string,
    followingReviews?: string,
    recommendedGames?: string
  ) {
    this.reviews = reviews;
    this.self = self;
    this.followers = followers;
    this.following = following;
    this.preferences = preferences;
    this.favoriteGames = favoriteGames;
    this.patchUser = patchUser;
    this.updateNotifications = updateNotifications;
    this.missionProgresses = missionProgresses;
    this.unfollow = unfollow;
    this.follow = follow;
    this.recommendedReviews = recommendedReviews;
    this.newReviews = newReviews;
    this.followingReviews = followingReviews;
    this.recommendedGames = recommendedGames;
  }

  static fromResponse({
    favoriteGames,
    follow,
    followers,
    following,
    patchUser,
    preferences,
    self,
    unfollow,
    reviews,
    updateNotifications,
    missionProgresses,
    newReviews,
    recommendedReviews,
    followingReviews,
    recommendedGames,
  }: UserLinksResponse): UserLinks {
    return new UserLinks(
      self,
      followers,
      following,
      preferences,
      favoriteGames,
      reviews,
      patchUser,
      updateNotifications,
      missionProgresses,
      unfollow,
      follow,
      recommendedReviews,
      newReviews,
      followingReviews,
      recommendedGames
    );
  }
}

export class User {
  id: number;
  username: string;
  email: string;
  enabled: boolean;
  reputation: number;
  avatar: string;
  language?: string;
  xp: number;
  preferences: Genre[];
  links: UserLinks;
  role: Role;
  followers: number;
  following: number;

  constructor(
    id: number,
    username: string,
    email: string,
    enabled: boolean,
    reputation: number,
    avatar: string,
    links: UserLinks,
    role: Role,
    followers: number,
    following: number,
    language?: string,
    xp = 0,
    preferences: Genre[] = []
  ) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.enabled = enabled;
    this.reputation = reputation;
    this.avatar = avatar;
    this.language = language;
    this.xp = xp;
    this.preferences = preferences;
    this.links = links;
    this.role = role;
    this.followers = followers;
    this.following = following;
  }

  static fromResponse(
    {
      id,
      username,
      email,
      enabled,
      reputation,
      avatar,
      language,
      xp,
      links,
      role,
      following,
      followers,
    }: UserResponse,
    preferences: Genre[] = []
  ): User {
    return new User(
      id,
      username,
      email,
      enabled,
      reputation,
      avatar,
      UserLinks.fromResponse(links),
      role,
      followers,
      following,
      language,
      xp,
      preferences
    );
  }
}

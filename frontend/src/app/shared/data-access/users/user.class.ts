import {UserLinksResponse, UserResponse} from "../responses";

export class UserLinks {
  self: string;
  followers: string;
  following: string;
  preferences: string;
  favoriteGames: string;
  patchUser?: string;
  updateNotifications?: string;
  unfollow?: string;
  follow?: string;


  constructor(self: string, followers: string, following: string, preferences: string, favoriteGames: string, patchUser?: string, updateNotifications?: string, unfollow?: string, follow?: string) {
    this.self = self;
    this.followers = followers;
    this.following = following;
    this.preferences = preferences;
    this.favoriteGames = favoriteGames;
    this.patchUser = patchUser;
    this.updateNotifications = updateNotifications;
    this.unfollow = unfollow;
    this.follow = follow;
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
                        updateNotifications
                      }: UserLinksResponse): UserLinks {
    return new UserLinks(self,
      followers,
      following,
      preferences,
      favoriteGames,
      patchUser,
      updateNotifications,
      unfollow,
      follow)
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
  preferences: string[];
  links: UserLinks;

  constructor(id: number, username: string, email: string, enabled: boolean, reputation: number, avatar: string, links: UserLinks, language?: string, xp: number = 0, preferences: string[] = []) {
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
  }

  static fromResponse({
                        id,
                        username,
                        email,
                        enabled,
                        reputation,
                        avatar,
                        language,
                        xp,
                        preferences,
                        links
                      }: UserResponse): User {
    return new User(id,
      username,
      email,
      enabled,
      reputation,
      avatar,
      UserLinks.fromResponse(links),
      language,
      xp,
      preferences);
  }
}

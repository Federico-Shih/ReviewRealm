import {Difficulty, Platform, Role} from "./shared.enums";
import {FeedbackType} from "./reviews/review.class";

export type ReviewLinksResponse = {
  feedback?: string;
  game: string;
  author: string;
  self: string;
  giveFeedback?: string;
  report?: string;
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
  created: string;
  links: ReviewLinksResponse;
}

export type ReviewFeedbackResponse = {
  links: {
    review: string;
    liker: string;
    self: string;
  }
  feedbackType: FeedbackType | null;
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
  recommendedReviews?: string;
  newReviews?: string;
  followingReviews?: string;
  recommendedGames?: string;
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
  links: UserLinksResponse;
  role: Role;
}

export type GameResponseLinks = {
  self: string;
  reviews: string;
  userReview?: string;
  reviewsExcludingUser?: string;
  addToFavoriteGames?: string;
  deleteFromFavoriteGames?: string;
  image: string;
  genres: string;
}

export type GameResponse = {
  id: number;
  name: string;
  description: string;
  developer: string;
  publisher: string;
  publishDate: string;
  ratingSum: number;
  reviewCount: number;
  isFavorite: boolean;
  averageDifficulty?: Difficulty;
  platform?: Platform;
  averageGameTime?: number;
  replayability?: number;
  completability?: number;
  links: GameResponseLinks;
}


export type GenreResponse = {
  id: number;
  name: string;
  localized: string;
  links: { self: string; };
}
/*
{
        "links": {
            "self": "http://localhost:8080/paw-2023a-04/api/missions/REPUTATION_GOAL"
        },
        "id": "REPUTATION_GOAL",
        "title": "LVL 100 influencer",
        "description": "Get 5 reputation",
        "xp": 20.0,
        "target": 5.0,
        "repeatable": true,
        "frequency": null
    },
 */

export type Frequency = {
  localized: string;
  name: string;
}

export type MissionResponse = {
  id: string;
  title: string;
  description: string;
  xp: number;
  target: number;
  repeatable: boolean;
  frequency: Frequency | null;
  links: {
    self: string;
  };
}

/*
    {
        "links": {
            "self": "http://localhost:8080/paw-2023a-04/api/notifications/userIFollowWritesReview"
        },
        "id": "userIFollowWritesReview",
        "localized": "When someone I follow writes a review"
    },
 */

export type NotificationTypeResponse = {
  id: string;
  localized: string;
  links: {
    self: string;
  }
};

export type ReportsLinksResponse = {
  self: string;
  reporter: string;
  reportedReview: string;
  reportedUser: string;
  resolve?: string;
  moderator?: string;
};

export type ReportResponse= {
  id: number;
  reason: string;
  submissionDate:string;
  state: string;
  links: ReportsLinksResponse;
  resolvedDate?: string;
};

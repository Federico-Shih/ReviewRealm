export enum Difficulty {
  Hard = "HARD",
  Medium = "MEDIUM",
  Easy = "EASY"
}
export function isDifficulty(difficulty: string): difficulty is Difficulty {
  return Object.values(Difficulty).includes(difficulty as Difficulty);
}

export enum Platform {
  PC = "PC",
  XBOX = "XBOX",
  PS = "PS",
  NINTENDO = "NINTENDO"
}
export function isPlatform(platform: string): platform is Platform {
  return Object.values(Platform).includes(platform as Platform);
}

export enum SortDirection {
  ASC = "asc",
  DESC = "desc",
}

export function isSortDirection(direction: string): direction is SortDirection {
  return Object.values(SortDirection).includes(direction as SortDirection);
}

export enum Role {
  MODERATOR = "MODERATOR",
  USER = "USER"
}

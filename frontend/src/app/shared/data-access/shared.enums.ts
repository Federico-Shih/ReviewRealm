export enum Difficulty {
  Hard = "HARD",
  Medium = "MEDIUM",
  Easy = "EASY"
}
export function isDifficulty(difficulty: unknown): difficulty is Difficulty {
  return Object.values(Difficulty).find((v) => v === difficulty) !== undefined;
}

export enum Platform {
  PC = "PC",
  XBOX = "XBOX",
  PS = "PS",
  NINTENDO = "NINTENDO"
}

export function isPlatform(platform: unknown): platform is Platform {
  return Object.values(Platform).find((v) => v === platform) !== undefined;
}

export enum SortDirection {
  ASC = "asc",
  DESC = "desc",
}

export function isSortDirection(direction: unknown): direction is SortDirection {
  return Object.values(SortDirection).find((v) => v === direction) !== undefined;
}

export enum Role {
  MODERATOR = "MODERATOR",
  USER = "USER"
}

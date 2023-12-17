import {
  Frequency,
  GenreResponse,
  MissionProgressResponse,
  MissionResponse,
  NotificationTypeResponse, NotificationValueResponse
} from "../shared.responses";

export class Genre {
  id: number;
  name: string;
  localized: string;
  links: { self: string; };


  constructor(id: number, name: string, localized: string, links: { self: string }) {
    this.id = id;
    this.name = name;
    this.localized = localized;
    this.links = links;
  }

  static fromResponse({id, links, localized, name}: GenreResponse) {
      return new Genre(id, name, localized, links);
  }
}


export class Mission {
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


  constructor(id: string,
              title: string,
              description: string,
              xp: number,
              target: number,
              repeatable: boolean,
              frequency: Frequency | null,
              links: {
                self: string
              }) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.xp = xp;
    this.target = target;
    this.repeatable = repeatable;
    this.frequency = frequency;
    this.links = links;
  }

  static fromResponse({description, frequency, id, links, repeatable, target, title, xp}: MissionResponse) {
    return new Mission(id, title, description, xp, target, repeatable, frequency, links);
  }
}

export class MissionProgress {
  progress: number;
  mission: string;
  startDate: string;
  completedTimes: number;

  constructor(progress: number,
              mission: string,
              startDate: string,
              completedTimes: number,
              ) {
    this.progress = progress;
    this.mission = mission;
    this.startDate = startDate;
    this.completedTimes = completedTimes;
  }

  static fromResponse({progress, mission, startDate, completedTimes}: MissionProgressResponse) {
    return new MissionProgress(progress,mission,startDate,completedTimes);
  }
}

export class MissionComplete {
  progress: MissionProgress;
  missionInfo: Mission;

  constructor(progress: MissionProgress, missionInfo: Mission) {
    this.progress = progress;
    this.missionInfo = missionInfo;
  }


}


export class NotificationType {
  id: string;
  localized: string;
  links: {
    self: string;
  }


  constructor(id: string, localized: string, links: { self: string }) {
    this.id = id;
    this.localized = localized;
    this.links = links;
  }

  static fromResponse({id, links, localized}: NotificationTypeResponse) {
    return new NotificationType(id, localized, links);
  }
}


export class NotificationValue {
  enabled: boolean;
  type: string;
  links: {
    notification: string;
  }


  constructor(enabled: boolean, type: string, links: { notification: string }) {
    this.enabled = enabled;
    this.type = type;
    this.links = links;
  }

  static fromResponse({enabled, type, links}: NotificationValueResponse) {
    return new NotificationValue(enabled, type, links);
  }
}


export class NotificationComplete {
  value: NotificationValue;
  notifInfo: NotificationType;

  constructor(notifInfo: NotificationType, value: NotificationValue) {
    this.value = value;
    this.notifInfo = notifInfo;
  }


}

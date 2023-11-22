import {Frequency, GenreResponse, MissionResponse, NotificationTypeResponse} from "../shared.responses";

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

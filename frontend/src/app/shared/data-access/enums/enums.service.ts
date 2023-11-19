import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {Genre, Mission, NotificationType} from "./enums.class";
import {GenreResponse, MissionResponse, NotificationTypeResponse} from "../shared.responses";
import {arrayResponseMapper, responseMapper} from "../../helpers/mapper";

@Injectable()
export class EnumsService {

  constructor(private readonly http: HttpClient) { }

  getMissions(url: string): Observable<Mission[]> {
    return this.http.get<MissionResponse[]>(url, {
      responseType: "json",
      observe: "response"
    }).pipe(map(arrayResponseMapper(Mission.fromResponse)));
  }

  getMission(url: string): Observable<Mission> {
    return this.http.get<MissionResponse>(url, {
      responseType: "json",
      observe: "response"
    }).pipe(map(responseMapper(Mission.fromResponse)));
  }

  getNotificationTypes(url: string): Observable<NotificationType[]> {
    return this.http.get<NotificationTypeResponse[]>(url, {
      responseType: "json",
      observe: "response"
    }).pipe(map(arrayResponseMapper(NotificationType.fromResponse)));
  }

  getNotificationType(url: string): Observable<NotificationType> {
    return this.http.get<NotificationTypeResponse>(url, {
      responseType: "json",
      observe: "response"
    }).pipe(map(responseMapper(NotificationType.fromResponse)));
  }

  getGenres(url: string): Observable<Genre[]> {
    return this.http.get<GenreResponse[]>(url, {
      responseType: "json",
      observe: "response"
    }).pipe(map(arrayResponseMapper(Genre.fromResponse)));
  }

  getGenre(url: string): Observable<Genre> {
    return this.http.get<GenreResponse>(url, {
      responseType: "json",
      observe: "response"
    }).pipe(map(responseMapper(Genre.fromResponse)));
  }
}

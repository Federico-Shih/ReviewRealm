import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, forkJoin, map, mergeMap, Observable, of} from "rxjs";
import {
  Genre,
  Mission,
  MissionComplete,
  MissionProgress,
  NotificationComplete,
  NotificationType,
  NotificationValue
} from "./enums.class";
import {
  GenreResponse,
  MissionProgressResponse,
  MissionResponse,
  NotificationTypeResponse,
  NotificationValueResponse
} from "../shared.responses";
import {arrayResponseMapper, exceptionMapper, responseMapper} from "../../helpers/mapper";

@Injectable()
export class EnumsService {

  constructor(private readonly http: HttpClient) { }

  getMissionsComplete(url: string): Observable<MissionComplete[]> {
    return this.http.get<MissionProgressResponse[]>(url, {
      responseType: "json",
      observe: "response"
    }).pipe(catchError(exceptionMapper))
      .pipe(
        mergeMap(
          response => {
            if(response.body===null)
              return of([]);
            return forkJoin(
              response.body.map(progress =>
                this.getMission(progress.mission).pipe(map((mission)=> new MissionComplete(MissionProgress.fromResponse(progress), mission)))
              )
            )
          }
        )
      );
  }

  getMissions(url: string): Observable<MissionProgress[]> {
    return this.http.get<MissionProgressResponse[]>(url, {
      responseType: "json",
      observe: "response"
    }).pipe(map(arrayResponseMapper(MissionProgress.fromResponse)));
  }

  getMission(url: string): Observable<Mission> {
    return this.http.get<MissionResponse>(url, {
      responseType: "json",
      observe: "response"
    }).pipe(map(responseMapper(Mission.fromResponse)));
  }

  getNotificationComplete(url: string): Observable<NotificationComplete[]> {
    return this.http.get<NotificationValueResponse[]>(url, {
      responseType: "json",
      observe: "response"
    }).pipe(catchError(exceptionMapper))
      .pipe(
        mergeMap(
          response => {
            if(response.body===null)
              return of([]);
            return forkJoin(
              response.body.map(value =>
                this.getNotificationType(value.links.notification).pipe(map((type)=> new NotificationComplete(NotificationType.fromResponse(type), value)))
              )
            )
          }
        )
      );
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

import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ReviewsService} from "../reviews/reviews.service";
import {Report} from "./reports.class";
import {Paginated} from "../shared.models";
import {catchError, forkJoin, map, mergeMap, Observable, of, switchMap} from "rxjs";
import {exceptionMapper, paginatedObjectMapper} from "../../helpers/mapper";
import {ReportResponse} from "../shared.responses";
import {ReportHandleDto, ReportMediaTypes} from "./reports.dtos";

@Injectable()
export class ReportsService {

  constructor(private http:HttpClient,private reviewsService:ReviewsService) { }

  getReports(url:string):Observable<Paginated<Report>>{
    return this.http.get<ReportResponse[]>(url,{
      observe:"response",
      responseType:"json"
    }).pipe(catchError(exceptionMapper)).pipe(
        mergeMap(
          response => {
            if (response.status === 202 || response.body === null) return of({
              content: [],
              totalPages: 0,
              links: {self: ""}
            });
            return forkJoin(
              response.body.map(report =>
                this.reviewsService.getReviewById(report.links.reportedReview).pipe(map((review) => {
                    return Report.fromResponse(report,review);
                }))
              )
            ).pipe(map(reports => paginatedObjectMapper(response, reports)))
          }
        )
      );
  }

  handleReport(url:string,state:ReportHandleDto):Observable<boolean>{
    return this.http.patch<boolean>(url,state,{
      responseType: "json",
      observe: "response",
      headers:{
        'Content-Type': ReportMediaTypes.APPLICATION_REPORT_HANDLE_FORM
      }
    }).pipe(catchError(exceptionMapper), map(response => response.status === 200));
  }

}


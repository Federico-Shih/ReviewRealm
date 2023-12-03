import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ReportsService} from "../../../shared/data-access/reports/reports.service";
import {BehaviorSubject, combineLatest} from "rxjs";
import {Paginated} from "../../../shared/data-access/shared.models";
import {Report} from "../../../shared/data-access/reports/reports.class";
import {environment} from "../../../../environments/environment";
import {STATE_CHANGE} from "../../../shared/data-access/reports/reports.dtos";
import {ReportEvent} from "../../ui/report-card/report-card.component";

@Component({
  selector: 'app-report-list',
  templateUrl: './report-list.component.html',
  styleUrls: ['./report-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReportListComponent implements OnInit{

  static PERCENTAGE_SCROLLED = 0.8;

  reports$:BehaviorSubject<Paginated<Report>> = new BehaviorSubject<Paginated<Report>>({
    content: [],
    links: {
      self: ''
    },
    totalPages: 0
  });
  loading$ = new BehaviorSubject<boolean>(true);
  loadingInfinite$ = new BehaviorSubject<boolean>(false);
  loadingReports$ = combineLatest({
    loading: this.loading$,
    reports: this.reports$,
    loadingInfinite: this.loadingInfinite$
  });


  constructor(private reportsService:ReportsService) {
  }
  ngOnInit() {
    this.reloadReports();
  }
  reloadReports() {
    this.reportsService.getReports(`${environment.API_ENDPOINT}/reports`).subscribe( {
      next: reports => {this.reports$.next(reports); console.log(reports);},
      error: () => {},
      complete: () =>{this.loading$.next(false);}
    });
  }
  nextPage(event:Event) {
    if(event.target instanceof HTMLElement
      && (event.target.scrollTop / (event.target.scrollHeight - event.target.clientHeight)) > ReportListComponent.PERCENTAGE_SCROLLED &&
      this.reports$.value.links.next && !this.loadingInfinite$.value && !this.loading$.value
    ){
      this.loadingInfinite$.next(true)
      this.reportsService.getReports(this.reports$.value.links.next)
        .subscribe({
          next:reviews => {this.reports$.next({
            content: this.reports$.value.content.concat(reviews.content),
            links: reviews.links,
            totalPages: reviews.totalPages
          });},
          error: () => {},
          complete: () => {this.loadingInfinite$.next(false);}
        })
    }
  }
  handleReport(event: ReportEvent) {
    if(event.report.links.resolve) {
      this.reportsService.handleReport(event.report.links.resolve,{state: event.new_status}).subscribe(() => {
        this.loading$.next(true);
        this.reloadReports();
      });
    }
  }

}

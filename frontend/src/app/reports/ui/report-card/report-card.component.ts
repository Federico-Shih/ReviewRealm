import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {STATE_CHANGE} from "../../../shared/data-access/reports/reports.dtos";
import {getMessageFromReason, Report} from "../../../shared/data-access/reports/reports.class";

export type ReportEvent = {
  report: Report;
  new_status: STATE_CHANGE;
}

@Component({
  selector: 'app-report-card',
  templateUrl: './report-card.component.html',
  styleUrls: ['./report-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReportCardComponent {

  @Input({required: true}) report: Report | null = null;

  @Output()
  handleReport = new EventEmitter<ReportEvent>();

  getMessage = getMessageFromReason;
  protected readonly STATE_CHANGE = STATE_CHANGE;
}

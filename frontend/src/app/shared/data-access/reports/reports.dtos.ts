export enum STATE_CHANGE{
    APPROVE = "accepted",
    REJECT = "rejected"
}
export enum ReportMediaTypes{
    APPLICATION_REPORT_HANDLE_FORM = "application/vnd.report-handle-form.v1+json",
    APPLICATION_REPORT_FORM = "application/vnd.report-form.v1+json",
    APPLICATION_REPORT_LIST = "application/vnd.report-list.v1+json",
    APPLICATION_REPORT = "application/vnd.report.v1+json"
}
export type ReportHandleDto = {
  state: STATE_CHANGE;
}

import { ReportResponse, ReportsLinksResponse } from '../shared.responses';
import { Review } from '../reviews/review.class';

export class ReportLinks {
  self: string;
  reporter: string;
  reportedReview: string;
  reportedUser: string;
  resolve?: string;
  moderator?: string;

  private constructor(
    self: string,
    reporter: string,
    reportedReview: string,
    reportedUser: string,
    resolve?: string,
    moderator?: string
  ) {
    this.self = self;
    this.reporter = reporter;
    this.reportedReview = reportedReview;
    this.reportedUser = reportedUser;
    this.resolve = resolve;
    this.moderator = moderator;
  }
  static fromResponse({
    self,
    reporter,
    reportedReview,
    reportedUser,
    resolve,
    moderator,
  }: ReportsLinksResponse): ReportLinks {
    return new ReportLinks(
      self,
      reporter,
      reportedReview,
      reportedUser,
      resolve,
      moderator
    );
  }
}
export enum ReportReason {
  DISRESPECTFUL = 'DISRESPECTFUL',
  SPAM = 'SPAM',
  IRRELEVANT = 'IRRELEVANT',
  SPOILER = 'SPOILER',
  PIRACY = 'PIRACY',
  PRIVACY = 'PRIVACY',
}
export enum ReportState {
  UNRESOLVED = 'UNRESOLVED',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED',
  DELETED_REVIEW = 'DELETED_REVIEW',
}
export function getMessageFromReason(reason: ReportReason): string {
  switch (reason) {
    case ReportReason.DISRESPECTFUL:
      return 'report.reason.disrespect';
    case ReportReason.SPAM:
      return 'report.reason.spam';
    case ReportReason.IRRELEVANT:
      return 'report.reason.irrelevant';
    case ReportReason.SPOILER:
      return 'report.reason.spoiler';
    case ReportReason.PIRACY:
      return 'report.reason.piracy';
    case ReportReason.PRIVACY:
      return 'report.reason.privacy';
  }
}
export class Report {
  id: number;
  reason: ReportReason;
  submissionDate: Date;
  state: ReportState;
  links: ReportLinks;
  resolvedDate?: Date;
  //No siempre queres la review y en los reports viejos no esta la review
  reportedReview?: Review;

  private constructor(
    id: number,
    reason: ReportReason,
    submissionDate: Date,
    state: ReportState,
    links: ReportLinks,
    resolvedDate?: Date,
    reportedReview?: Review
  ) {
    this.id = id;
    this.reason = reason;
    this.submissionDate = submissionDate;
    this.state = state;
    this.links = links;
    this.resolvedDate = resolvedDate;
    this.reportedReview = reportedReview;
  }
  static fromResponse(
    { id, reason, submissionDate, state, links, resolvedDate }: ReportResponse,
    reportedReview: Review
  ): Report {
    return new Report(
      id,
      reason as ReportReason,
      new Date(submissionDate),
      state as ReportState,
      ReportLinks.fromResponse(links),
      resolvedDate ? new Date(resolvedDate) : undefined,
      reportedReview
    );
  }
}

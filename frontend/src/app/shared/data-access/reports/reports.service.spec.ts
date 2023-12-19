import { TestBed } from '@angular/core/testing';

import { ReportsService } from './reports.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {ReviewsService} from "../reviews/reviews.service";
import {ReviewMock} from "../../../../tests/mocks/reviews.mock";
import {of} from "rxjs";
import {SharedModule} from "../../shared.module";

describe('ReportsService', () => {
  let service: ReportsService;
  let httpTesting: HttpTestingController;
  const MOCK = 'https://reviewrealm.com/api/reports?pageSize=10';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, SharedModule],
      providers: [
        {
          provide: ReviewsService,
          useValue: {
            getReviewById: () => {
              return of(ReviewMock);
            }
          }
        }
      ]
    });
    httpTesting = TestBed.inject(HttpTestingController);
    service = TestBed.inject(ReportsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get reports', () => {
    service.getReports(MOCK).subscribe((response) => {
      expect(response.content.length).toBe(1);
    });
    const req = httpTesting.expectOne(MOCK);
    expect(req.request.method).toBe('GET');
    req.flush([{ links: {
        reportedReview: 'https://reviewrealm.com/api/reviews/1'
    }}]);
  });
});

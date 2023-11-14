import {TestBed} from '@angular/core/testing';

import {ReviewsService} from './reviews.service';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {SharedModule} from "../../shared.module";

describe('ReviewsService', () => {
  let service: ReviewsService;

  beforeEach(() => {
    TestBed.configureTestingModule({imports: [HttpClientTestingModule, SharedModule]});
    service = TestBed.inject(ReviewsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

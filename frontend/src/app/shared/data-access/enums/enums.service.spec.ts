import { TestBed } from '@angular/core/testing';

import { EnumsService } from './enums.service';

describe('EnumsService', () => {
  let service: EnumsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EnumsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

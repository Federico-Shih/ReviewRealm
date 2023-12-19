import { TestBed } from '@angular/core/testing';

import { NavigationHistoryService } from './navigation-history.service';

describe('NavigationHistoryService', () => {
  let service: NavigationHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NavigationHistoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

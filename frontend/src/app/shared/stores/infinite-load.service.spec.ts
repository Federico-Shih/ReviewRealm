import { TestBed } from '@angular/core/testing';

import { InfiniteLoadService } from './infinite-load.service';

describe('InfiniteLoadService', () => {
  let service: InfiniteLoadService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InfiniteLoadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

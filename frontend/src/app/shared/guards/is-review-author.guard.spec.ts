import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { isReviewAuthorGuard } from './is-review-author.guard';

describe('isReviewAuthorGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => isReviewAuthorGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});

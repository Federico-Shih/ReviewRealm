import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { isModeratorGuard } from './is-moderator.guard';

describe('isModeratorGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => isModeratorGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});

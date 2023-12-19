import { TestBed } from '@angular/core/testing';

import { NavigationHistoryService } from './navigation-history.service';
import {NavigationEnd, Router} from "@angular/router";
import {BehaviorSubject, filter, Observable} from "rxjs";
import {SharedModule} from "../../shared.module";

class MockRouter {
  public ne = new NavigationEnd(0, 'http://localhost:4200/login', 'http://localhost:4200/login');
  public events = new BehaviorSubject(this.ne);
}

describe('NavigationHistoryService', () => {
  let service: NavigationHistoryService;
  let router: MockRouter;

  beforeEach(() => {
    router = new MockRouter();
    TestBed.configureTestingModule({
      imports: [
        SharedModule
      ],
      providers: [
        {
          provide: Router,
          useValue: router
        }
      ]
    });
    service = TestBed.inject(NavigationHistoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with null previous url', () => {
    service.previousUrl.subscribe((url) => {
      expect(url).toBe(null);
    });
  });

  it('after router event, update previous', () => {
    service.previousUrl.pipe(filter((el) => el !== null)).subscribe((url) => {
      expect(url).toBe('http://localhost:4200/login');
    });

    router.events.next(new NavigationEnd(0, 'http://localhost:4200/login', 'http://localhost:4200/login'));
  })
});

import { Injectable } from '@angular/core';
import {BehaviorSubject, filter, Observable, ReplaySubject, tap} from "rxjs";
import {NavigationEnd, Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class NavigationHistoryService {
  private _previousUrl = new BehaviorSubject<string | null>(null);
  private _currentUrl = new BehaviorSubject<string | null>(null);
  initialized = false;

  constructor(private readonly router: Router) {
    this.router.events.pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd)).subscribe((event) => {
      if (this.initialized) {
        this._previousUrl.next(this._currentUrl.value);
      } else {
        this.initialized = true;
      }
      this._currentUrl.next(event.urlAfterRedirects);
    })
  }


  get previousUrl(): Observable<string | null> {
    return this._previousUrl;
  }

  clear() {
    this._previousUrl.next(null);
  }
}

import {BehaviorSubject, combineLatest, Observable} from "rxjs";
import {Paginated} from "../data-access/shared.models";


export class InfiniteLoadService<T, K> {
  private paginationFunction: (next: string, params?: K) => Observable<Paginated<T>> = () => new Observable<Paginated<T>>();
  private loadedItems$ = new BehaviorSubject<Paginated<T> | null>(null);
  private loading$ = new BehaviorSubject(false);
  private _hasMore$ = new BehaviorSubject(false);


  private state$: Observable<{
    loading: boolean,
    paginatedItems: Paginated<T> | null,
    hasMore: boolean,
  }> = combineLatest({
    loading: this.loading$,
    paginatedItems: this.loadedItems$,
    hasMore: this._hasMore$,
  });

  registerPagination(paginationFunction: (next: string, params?: K) => Observable<Paginated<T>>) {
    this.paginationFunction = paginationFunction;
    this.loadedItems$.next(null);
    this._hasMore$.next(true);
    this.loading$.next(true);
  }

  deregisterPagination() {
    this.paginationFunction = () => new Observable<Paginated<T>>();
    this.loadedItems$.next(null);
    this._hasMore$.next(true);
    this.loading$.next(false);
  }

  loadMore(next: string, params?: K) {
    if (this._hasMore$.value) {
      this.loading$.next(true);
      this.paginationFunction(next, params).subscribe(pageInfo => {
        const currentItems = this.loadedItems$.getValue();
        if (!pageInfo.links.next) {
          this._hasMore$.next(false);
        } else {
          this._hasMore$.next(true);
        }
        if (currentItems !== null) {
          const newItems = currentItems.content.concat(pageInfo.content);
          this.loadedItems$.next({
            content: newItems,
            links: pageInfo.links,
            totalPages: pageInfo.totalPages,
            totalElements: pageInfo.totalElements,
          });
        } else {
          this.loadedItems$.next(pageInfo);
        }
        this.loading$.next(false);
      });
    }
  }

  reset() {
   this.loadedItems$.next(null);
   this._hasMore$.next(true);
   this.loading$.next(false);
  }

  getLoadedItems$(): Observable<Paginated<T> | null> {
    return this.loadedItems$.asObservable();
  }

  getLoading$(): Observable<boolean> {
    return this.loading$.asObservable();
  }

  getState$() {
    return this.state$;
  }

  getHasMore$(): Observable<boolean> {
    return this._hasMore$;
  }
}

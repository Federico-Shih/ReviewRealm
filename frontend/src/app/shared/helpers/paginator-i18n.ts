import {MatPaginatorIntl} from "@angular/material/paginator";
import {Injectable} from "@angular/core";
import {Subject} from "rxjs";
import {TranslateService} from "@ngx-translate/core";

@Injectable()
export class MyCustomPaginatorIntl implements MatPaginatorIntl {
  constructor(private readonly translateService: TranslateService) {

  }


  changes = new Subject<void>();
  // For internationalization, the `$localize` function from
  // the `@angular/localize` package can be used.
  firstPageLabel = this.translateService.instant('paginator.first-page');
  itemsPerPageLabel = this.translateService.instant('paginator.items-per-page');
  lastPageLabel = this.translateService.instant('paginator.last-page');

  // You can set labels to an arbitrary string too, or dynamically compute
  // it through other third-party internationalization libraries.
  nextPageLabel = 'Next page';
  previousPageLabel = 'Previous page';

  getRangeLabel(page: number, pageSize: number, length: number): string {
    if (length === 0) {
      return this.translateService.instant('paginator.no-results');
    }
    const amountPages = Math.ceil(length / pageSize);
    return this.translateService.instant('paginator.page-of', {page: page + 1, amountPages})
    // return  `Page ${page + 1} of ${amountPages}`;
  }
}

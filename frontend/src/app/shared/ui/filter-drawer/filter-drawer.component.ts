import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {SortDirection} from "../../data-access/shared.enums";

export type EnumType<T> = {
  translateKey: string;
  selectKey: T;
}

@Component({
  selector: 'app-filter-drawer',
  templateUrl: './filter-drawer.component.html',
  styleUrls: ['./filter-drawer.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FilterDrawerComponent<T> {
  @Input({required: true})
  orderDirections: EnumType<T>[] = [];

  @Output()
  selectOrder = new EventEmitter<T>();

  @Output()
  selectOrderDirection = new EventEmitter<string>();

  readonly sortDirections = Object.values(SortDirection);
}

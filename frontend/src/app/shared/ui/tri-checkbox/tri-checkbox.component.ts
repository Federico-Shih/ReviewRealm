import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {MAT_CHECKBOX_DEFAULT_OPTIONS, MatCheckboxDefaultOptions} from "@angular/material/checkbox";

@Component({
  selector: 'app-tri-checkbox',
  templateUrl: './tri-checkbox.component.html',
  styleUrls: ['./tri-checkbox.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {provide: MAT_CHECKBOX_DEFAULT_OPTIONS, useValue: {clickAction: 'noop'} as MatCheckboxDefaultOptions}
  ],
})
export class TriCheckboxComponent {
  @Input()
  label: string | undefined = undefined;

  @Input()
  value: boolean | undefined = undefined;
  @Output()
  checkboxClick = new EventEmitter<boolean | undefined>();

  check() {
    if (this.value === undefined) {
      this.checkboxClick.emit(true);
    } else if (this.value) {
      this.checkboxClick.emit(false);
    } else {
      this.checkboxClick.emit(undefined);
    }
  }
}

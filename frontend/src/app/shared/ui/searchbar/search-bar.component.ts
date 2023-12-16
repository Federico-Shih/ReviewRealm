import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchBarComponent {
  @Input()
  label: string = '';

  @Input()
  value: string = '';

  @Output()
  search = new EventEmitter<string>();

  @Output()
  submit = new EventEmitter();

  onChange(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.value = value;
    this.search.emit(value);
  }

  onSubmit() {
    this.submit.emit();
  }
}

import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { subscribeOn } from 'rxjs';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SearchBarComponent {
  text = '';

  @Input()
  label = '';

  @Input()
  value = '';

  @Output()
  search = new EventEmitter<string>();

  @Output()
  searchSubmit = new EventEmitter();

  onChange(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.value = value;
    this.search.emit(value);
  }

  onSubmit() {
    this.searchSubmit.emit();
  }

  protected readonly subscribeOn = subscribeOn;
}

import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output,} from '@angular/core';
import {User} from '../../data-access/users/users.class';
import {Role} from '../../data-access/shared.enums';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavbarComponent {
  @Input() user: User | null = null;
  @Input() currentRoute: string | null = null;

  @Output() logout = new EventEmitter<void>();
  @Output() search = new EventEmitter<string>();
  @Output() searchSubmit = new EventEmitter();

  get isLoggedIn() {
    return this.user !== null;
  }

  protected readonly Role = Role;
}

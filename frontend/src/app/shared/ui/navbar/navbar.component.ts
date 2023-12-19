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
  // user undefined === no session
  @Input() user: User | null | undefined = null;
  @Input() currentRoute: string | null = null;
  @Input() loading: boolean = true;

  @Output() logout = new EventEmitter<void>();
  @Output() search = new EventEmitter<string>();
  @Output() searchSubmit = new EventEmitter();

  get isLoggedIn() {
    return this.user !== undefined && this.user !== null;
  }

  protected readonly Role = Role;
}

import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {User} from "../../data-access/users/users.class";
import {Role} from "../../data-access/shared.enums";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent {
  @Input() isLoggedIn: boolean = false;
  @Input() user: User | null = null;

  @Output() logout = new EventEmitter<void>();

  protected readonly Role = Role;
}

import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {User} from "../../data-access/users/user.class";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent {
  @Input() isLoggedIn: boolean = false;
  @Input() user: User | null = null;
}

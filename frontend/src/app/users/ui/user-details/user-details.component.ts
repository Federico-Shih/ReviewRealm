import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {User} from "../../../shared/data-access/users/users.class";
import {Role} from "../../../shared/data-access/shared.enums";
import {Observable} from "rxjs";

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailsComponent {
  @Input({required: true}) userInfo:User | null = null;
  protected readonly Role = Role;
  @Input({required: true}) loggedUser:User | null = null;
}

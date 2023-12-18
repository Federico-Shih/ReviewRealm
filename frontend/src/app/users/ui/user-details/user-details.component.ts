import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {User} from '../../../shared/data-access/users/users.class';
import {Role} from '../../../shared/data-access/shared.enums';

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserDetailsComponent {
  @Input({required: true}) userInfo!: User;
  protected readonly Role = Role;
  @Input({required: true}) loggedUser: User | null = null;
  @Input({required: true}) isFollowing!: boolean;
  @Input() loading = false;


  // returns if it was following
  @Output() followClick = new EventEmitter<boolean>();

  onSubmit(value: boolean) {
    this.followClick.emit(value);
  }

}

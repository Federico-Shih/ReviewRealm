import {ChangeDetectionStrategy, Component, computed, EventEmitter, Input, Output, signal} from '@angular/core';
import {User} from "../../data-access/users/users.class";
import {Genre} from "../../data-access/enums/enums.class";
import {Role} from "../../data-access/shared.enums";

@Component({
  selector: 'app-profile-card',
  templateUrl: './profile-card.component.html',
  styleUrls: ['./profile-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProfileCardComponent {

  @Output()
  userClick = new EventEmitter<User>();
  @Output()
  genreClick = new EventEmitter<Genre>();
  @Output()
  genreGenericClick = new EventEmitter();

  _user = signal<User | null>(null);

  @Input({required: true})
  set user(user: User) {
    this._user.set(user);
  }

  userRank = computed(() => {
    if (!(this._user() || !this._user()?.xp)) return "basic";
    const level = this._user()!!.xp!! / 100;
    if (level > 40) {
      return "epic";
    } else if (level > 30) {
      return "gold";
    } else if (level > 20) {
      return "silver";
    } else if (level > 10) {
      return "bronze";
    }
    return "basic";
  })
  protected readonly Role = Role;
}

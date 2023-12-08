import { ChangeDetectionStrategy, Component } from '@angular/core';
import {UsersService} from "../../shared/data-access/users/users.service";
import {ReasonToLocale} from "../../shared/data-access/shared.enums";

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NotificationsComponent {

  currentNotificationSettings = [{"key": "settings.notification.deletion", "val": false},
                                                        {"key": "settings.notification.following", "val": true}];
  constructor(userService: UsersService) {
  }

  protected readonly ReasonToLocale = ReasonToLocale;
}

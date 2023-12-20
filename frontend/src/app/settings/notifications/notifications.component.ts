import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UsersService } from '../../shared/data-access/users/users.service';
import { EnumsService } from '../../shared/data-access/enums/enums.service';
import { AuthenticationService } from '../../shared/data-access/authentication/authentication.service';
import { Observable, of, switchMap } from 'rxjs';
import { NotificationComplete } from '../../shared/data-access/enums/enums.class';
import { environment } from '../../../environments/environment';
import { NotificationsDto } from '../../shared/data-access/users/users.dtos';
import { Router } from '@angular/router';
import {User} from "../../shared/data-access/users/users.class";

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationsComponent implements OnInit {
  loggedInUser$ = this.authService.getLoggedUser();

  user: User | null = null;

  currentNotificationSettings$!: Observable<NotificationComplete[]>;

  checkboxValues: { [key: string]: boolean } = {};

  constructor(
    private readonly userService: UsersService,
    private readonly enumService: EnumsService,
    private readonly authService: AuthenticationService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.currentNotificationSettings$ = this.loggedInUser$.pipe(
      switchMap(user => {
        if (user !== null && user.links && user.links.updateNotifications) {
          this.user = user;
          return this.enumService.getNotificationComplete(
            user.links.updateNotifications
          );
        } else {
          return of([]);
        }
      })
    );

    this.currentNotificationSettings$.subscribe(notifs => {
      notifs.forEach(notif => {
        this.checkboxValues[notif.notifInfo.id] = notif.value.enabled;
      });
    });
  }

  onSubmit() {

    const dto: NotificationsDto = {
      userIFollowWritesReview: this.checkboxValues['userIFollowWritesReview'],
      myReviewIsDeleted: this.checkboxValues['myReviewIsDeleted'],
    };
    if(this.user===null || this.user.links.updateNotifications===undefined)
      return;
    this.userService
      .editUserNotifications(
        this.user.links.updateNotifications,
        dto
      )
      .subscribe(() => {
        this.router.navigate(['/profile', `${this.user?.id}`]);
      });
  }

  changingCheckbox(id: string, checked: boolean) {
    this.checkboxValues[id] = checked;
  }
}

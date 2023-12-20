import { ChangeDetectionStrategy, Component } from '@angular/core';
import { environment } from '../../../environments/environment';
import { UsersService } from '../../shared/data-access/users/users.service';
import { AvatarDto } from '../../shared/data-access/users/users.dtos';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../shared/data-access/authentication/authentication.service';
import { map } from 'rxjs';
import {User} from "../../shared/data-access/users/users.class";

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AvatarComponent {
  avatars = [
    `${environment.IMAGE_ENDPOINT}/1.png`,
    `${environment.IMAGE_ENDPOINT}/2.png`,
    `${environment.IMAGE_ENDPOINT}/3.png`,
    `${environment.IMAGE_ENDPOINT}/4.png`,
    `${environment.IMAGE_ENDPOINT}/5.png`,
    `${environment.IMAGE_ENDPOINT}/6.png`,
  ];
  user: User | null = null;
  loggedInUser$ = this.authService
    .getLoggedUser()
    .subscribe(user => {
      this.user = user;
    });

  constructor(
    private readonly userService: UsersService,
    private readonly authService: AuthenticationService,
    private readonly router: Router
  ) {}

  changeAvatar(avatarId: number) {
    const dto: AvatarDto = { avatarId: avatarId };
    if(this.user===null || this.user.links.patchUser===undefined)
      return;
    this.userService
      .editUserAvatar(this.user.links.patchUser, dto)
      .subscribe(() => {
        this.router.navigate(['/profile', this.user?.id]);
      });
  }
}

import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {environment} from "../../../environments/environment.development";
import {UsersService} from "../../shared/data-access/users/users.service";
import {AvatarDto} from "../../shared/data-access/users/users.dtos";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../shared/data-access/authentication/authentication.service";
import {map, of, switchMap} from "rxjs";

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AvatarComponent {
  avatars= [`${environment.IMAGE_ENDPOINT}/1.png`, `${environment.IMAGE_ENDPOINT}/2.png`, `${environment.IMAGE_ENDPOINT}/3.png`, `${environment.IMAGE_ENDPOINT}/4.png`, `${environment.IMAGE_ENDPOINT}/5.png`, `${environment.IMAGE_ENDPOINT}/6.png`];
  userId: Number|null = null;
  loggedInUser$ = this.authService.getLoggedUser().pipe(
      map(user => user ? user.id : null)
      ).subscribe(userId => {
      this.userId = userId;
      });

  constructor(private readonly userService: UsersService, private readonly authService:AuthenticationService, private readonly router:Router) {
  }

  changeAvatar(avatarId: number) {
    const dto:AvatarDto = { avatarId: avatarId }

    this.userService.editUserAvatar(`${environment.API_ENDPOINT}/users/${this.userId}`, dto).subscribe(
        (genres) => {
          this.router.navigate(['/profile',`${this.userId}`]);
        },
    )

  }

}

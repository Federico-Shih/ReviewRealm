import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import { User } from '../../../shared/data-access/users/users.class';
import { Role } from '../../../shared/data-access/shared.enums';
import {UsersService} from "../../../shared/data-access/users/users.service";
import {environment} from "../../../../environments/environment";
import {map, Observable} from "rxjs";
import {AvatarDto, FollowDto} from "../../../shared/data-access/users/users.dtos";

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserDetailsComponent{
  @Input({ required: true }) userInfo!: User;
  protected readonly Role = Role;
  @Input({ required: true }) loggedUser: User | null = null;

  @Input({required: true}) isFollowing!: boolean;

  constructor(private readonly userService: UsersService) {
  }

  onSubmit(value: boolean) {
    const dto: FollowDto = { userId: this.userInfo.id };
    if(this.loggedUser!==null) {
      if (!value) {
        this.userService.follow(`${environment.API_ENDPOINT}/users/${this.loggedUser.id}/following`, dto).subscribe()
      }
      else if (this.userInfo.links.unfollow !== undefined) {
        this.userService.unfollow(this.userInfo.links.unfollow).subscribe();
      }
      this.isFollowing = !this.isFollowing;
    }
  }

}

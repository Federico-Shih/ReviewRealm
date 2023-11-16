import {ChangeDetectionStrategy, Component} from '@angular/core';
import {UserCreateDto} from "../../shared/data-access/users/users.dtos";
import {UsersService} from "../../shared/data-access/users/users.service";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RegisterComponent {
  constructor(private readonly userService: UsersService) {
  }

  createUser(userCreateDto: UserCreateDto) {
    this.userService.createUser(environment.API_ENDPOINT + '/users', userCreateDto).subscribe();
  }
}

import {Component, OnInit} from '@angular/core';
import {UsersService} from "../../../shared/data-access/users/users.service";
import {AuthenticationService} from "../../../shared/data-access/authentication/authentication.service";
import {SortDirection} from "../../../shared/data-access/shared.enums";
import {UserSortCriteria} from "../../../shared/data-access/users/users.dtos";

export type EnumType<T> = {
  translateKey: string;
  selectKey: T;
}


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  constructor(private readonly userService: UsersService, private readonly auth: AuthenticationService) {
  }

  readonly sortDirections = Object.values(SortDirection);

  orderDirections: EnumType<string>[] = Object.values(UserSortCriteria).map((str) => ({
    translateKey: str,
    selectKey: str
  }))

  ngOnInit(): void {
  }

  log(any: unknown) {
    console.log(any);
  }
}

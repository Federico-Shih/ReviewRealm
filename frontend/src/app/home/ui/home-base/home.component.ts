import {Component, OnInit} from '@angular/core';
import {UsersService} from "../../../shared/data-access/users/users.service";
import {AuthenticationService} from "../../../shared/data-access/authentication/authentication.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  constructor(private readonly userService: UsersService, private readonly auth: AuthenticationService) {
  }

  ngOnInit(): void {
    this.auth.login({username: 'fedeshih@gmail.com', password: 'holahola'}).subscribe(console.log);
  }
}

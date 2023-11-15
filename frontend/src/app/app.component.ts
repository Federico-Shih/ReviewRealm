import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "./shared/data-access/authentication/authentication.service";
import {BehaviorSubject} from "rxjs";
import {User} from "./shared/data-access/users/user.class";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'frontend';
  loggedInUser$: BehaviorSubject<User | null> = new BehaviorSubject<User | null>(null);

  constructor(readonly authService: AuthenticationService) {

  }

  logout() {
    this.authService.logout();
  }

  ngOnInit(): void {
    this.authService.getLoggedUser().subscribe((user) => {
      this.loggedInUser$.next(user);
    });
  }
}

import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "./shared/data-access/authentication/authentication.service";
import {BehaviorSubject} from "rxjs";
import {User} from "./shared/data-access/users/users.class";
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'frontend';
  loggedInUser$: BehaviorSubject<User | null> = new BehaviorSubject<User | null>(null);
  searchValue = new BehaviorSubject('');

  constructor(readonly authService: AuthenticationService, private readonly router: Router) {

  }

  search(searchValue: string) {
    this.searchValue.next(searchValue);
  }

  submit() {
    this.router.navigate(['/search'], {queryParams: {search: this.searchValue.value}});
  }

  logout() {
    this.authService.logout();
  }

  ngOnInit(): void {
    this.authService.getLoggedUser().subscribe((user) => {
      this.loggedInUser$.next(user);
    });
  }

  log(value: any) {
    console.log(value);
  }
}

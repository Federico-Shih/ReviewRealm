import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from './shared/data-access/authentication/authentication.service';
import {BehaviorSubject, map} from 'rxjs';
import {User} from './shared/data-access/users/users.class';
import {Router} from '@angular/router';
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  title = 'frontend';
  loggedInUser$ = this.authService.getLoggedUser()
    .pipe(map(user => user === null ? undefined : user));
  searchValue = new BehaviorSubject('');

  constructor(
    readonly authService: AuthenticationService,
    private readonly router: Router,
    private readonly translateService: TranslateService
  ) {}

  search(searchValue: string) {
    this.searchValue.next(searchValue);
  }

  submit() {
    this.router.navigate(['/search'], {
      queryParams: { search: this.searchValue.value },
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login'], { replaceUrl: true });
  }

  ngOnInit(): void {
    this.translateService.use(this.translateService.getBrowserLang() ?? this.translateService.getDefaultLang());
  }
}

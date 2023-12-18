import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {UsersService} from "../../../shared/data-access/users/users.service";
import {ActivatedRoute, Router} from "@angular/router";
import {BehaviorSubject, catchError, Observable, switchMap} from "rxjs";
import {Paginated} from "../../../shared/data-access/shared.models";
import {User} from "../../../shared/data-access/users/users.class";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-followers',
  templateUrl: './followers.component.html',
  styleUrls: ['./followers.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FollowersComponent implements OnInit {

  userName: string | undefined;
  userId: number | undefined;

  initialFollowers$: Observable<Paginated<User>> = this.route.paramMap.pipe(
      switchMap(params => {
          return this.userService.getUserById(
              `${environment.API_ENDPOINT}/users/` + params.get('id')
          );
      }),
      catchError((err, caught) => {
          this.router.navigate(['errors/not-found']);
          return caught;
      })
  ).pipe(switchMap(user => {
      this.userId = user.id;
      this.userName = user.username;

      return this.userService.getUsers(user.links.followers, {pageSize:6});
  }));

  constructor(
    private readonly userService: UsersService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

    followers$: BehaviorSubject<Paginated<User> | null> =
        new BehaviorSubject<Paginated<User> | null>(null);

    showMore(next: string) {
        this.userService.getUsers(next, {pageSize:6}).subscribe(pageInfo => {
            const currentFollowers = this.followers$.getValue();
            if (currentFollowers !== null) {
                const newFollowers = currentFollowers.content.concat(pageInfo.content);
                this.followers$.next({
                    content: newFollowers,
                    links: pageInfo.links,
                    totalPages: pageInfo.totalPages,
                    totalElements: pageInfo.totalElements,
                });
            }
        });
    }

    ngOnInit(): void {
        this.initialFollowers$.subscribe(pageInfo =>
            this.followers$.next(pageInfo)
        );
    }
}

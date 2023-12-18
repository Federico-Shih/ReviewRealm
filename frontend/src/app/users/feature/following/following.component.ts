import { ChangeDetectionStrategy, Component } from '@angular/core';
import {BehaviorSubject, catchError, Observable, switchMap} from "rxjs";
import {Paginated} from "../../../shared/data-access/shared.models";
import {User} from "../../../shared/data-access/users/users.class";
import {environment} from "../../../../environments/environment";
import {UsersService} from "../../../shared/data-access/users/users.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-following',
  templateUrl: './following.component.html',
  styleUrls: ['./following.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FollowingComponent {
  userName: string | undefined;
  userId: number | undefined;

  initialFollowing$: Observable<Paginated<User>> = this.route.paramMap.pipe(
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

    return this.userService.getUsers(user.links.following, {pageSize:6});
  }));

  constructor(
      private readonly userService: UsersService,
      private readonly route: ActivatedRoute,
      private readonly router: Router,
  ) {}

  following$: BehaviorSubject<Paginated<User> | null> =
      new BehaviorSubject<Paginated<User> | null>(null);

  showMore(next: string) {
    this.userService.getUsers(next, {pageSize:6}).subscribe(pageInfo => {
      const currentFollowing = this.following$.getValue();
      if (currentFollowing !== null) {
        const newFollowing = currentFollowing.content.concat(pageInfo.content);
        this.following$.next({
          content: newFollowing,
          links: pageInfo.links,
          totalPages: pageInfo.totalPages,
          totalElements: pageInfo.totalElements,
        });
      }
    });
  }

  ngOnInit(): void {
    this.initialFollowing$.subscribe(pageInfo =>
        this.following$.next(pageInfo)
    );
  }
}

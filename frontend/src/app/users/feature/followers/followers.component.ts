import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {UsersService} from "../../../shared/data-access/users/users.service";
import {ActivatedRoute, Router} from "@angular/router";
import {BehaviorSubject, catchError, Observable, switchMap} from "rxjs";
import {Paginated} from "../../../shared/data-access/shared.models";
import {User} from "../../../shared/data-access/users/users.class";
import {environment} from "../../../../environments/environment";
import {UserInfiniteLoadService} from "../../../shared/stores/infinite-load.service";

@Component({
  selector: 'app-followers',
  templateUrl: './followers.component.html',
  styleUrls: ['./followers.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FollowersComponent implements OnInit {

  userName: string | undefined;
  userId: number | undefined;

  constructor(
    private readonly userService: UsersService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly infiniteUserService: UserInfiniteLoadService
  ) {
    infiniteUserService.registerPagination(this.userService.getUsers.bind(this.userService));
  }

  state$ = this.infiniteUserService.getState$();

  showMore(next: string) {
    this.infiniteUserService.loadMore(next);
  }

  ngOnInit(): void {
    this.route.paramMap.pipe(
      switchMap(params => {
        return this.userService.getUserById(
          `${environment.API_ENDPOINT}/users/` + params.get('id')
        );
      }),
      catchError((err, caught) => {
        this.router.navigate(['errors/not-found']);
        return caught;
      })
    ).subscribe({
      next: user => {
        this.userId = user.id;
        this.userName = user.username;
        this.infiniteUserService.loadMore(user.links.followers, { pageSize: 10 })
      }
    })
  }
}

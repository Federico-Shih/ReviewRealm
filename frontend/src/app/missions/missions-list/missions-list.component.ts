import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Observable, of, switchMap } from 'rxjs';
import { MissionComplete } from '../../shared/data-access/enums/enums.class';
import { EnumsService } from '../../shared/data-access/enums/enums.service';
import { AuthenticationService } from '../../shared/data-access/authentication/authentication.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-missions-list',
  templateUrl: './missions-list.component.html',
  styleUrls: ['./missions-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MissionsListComponent {
  loggedInUser$ = this.authService.getLoggedUser();

  missionComplete$: Observable<MissionComplete[]> = this.loggedInUser$.pipe(
    switchMap(user => {
      if (user === null) {
        this.router.navigate([`/reviews`]);
        return of([]);
      }
      if(user.links.missionProgresses)
        return this.missionService.getMissionsComplete(user.links.missionProgresses);
      this.router.navigate([`/profile/${user.id}`]);
      return of([]);
    })
  );

  constructor(
    private readonly missionService: EnumsService,
    private readonly authService: AuthenticationService,
    private router: Router
  ) {}

}

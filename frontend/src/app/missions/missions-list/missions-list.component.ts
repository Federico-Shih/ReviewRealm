import { ChangeDetectionStrategy, Component } from '@angular/core';
import {combineLatest, map, Observable, of, switchMap} from 'rxjs';
import {Mission, MissionComplete, MissionProgress} from '../../shared/data-access/enums/enums.class';
import { EnumsService } from '../../shared/data-access/enums/enums.service';
import { AuthenticationService } from '../../shared/data-access/authentication/authentication.service';
import { Router } from '@angular/router';
import {environment} from "../../../environments/environment";

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
        this.router.navigate([`/`]);
        return of([]);
      }
      if(user.links.missionProgresses)
        return combineLatest<[MissionComplete[], Mission[]]>([
          this.missionService.getMissionsComplete(user.links.missionProgresses),
          this.missionService.getMissions(environment.API_ENDPOINT + '/missions')])
          .pipe(
            map(([progresses, missions]) => {
              return missions.map((mission: Mission) => {
                const progress: MissionComplete | undefined = progresses.find((progress: MissionComplete) => progress.missionInfo.id === mission.id);
                if (progress === undefined) {
                  return new MissionComplete(MissionProgress.fromResponse({
                    progress: 0,
                    mission: mission.id,
                    startDate: new Date().toString(),
                    completedTimes: 0
                  }), mission);
                }
                return new MissionComplete(progress.progress, mission);
              })
            }))
          ;
      this.router.navigate([`/profile/${user.id}`]);
      return of([]);
    })
  );

  constructor(
    private readonly missionService: EnumsService,
    private readonly authService: AuthenticationService,
    private router: Router
  ) {

  }

}

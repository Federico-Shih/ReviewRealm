import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Observable, of, switchMap} from "rxjs";
import {MissionComplete} from "../../shared/data-access/enums/enums.class";
import {AuthenticationService} from "../../shared/data-access/authentication/authentication.service";
import {EnumsService} from "../../shared/data-access/enums/enums.service";
import {environment} from "../../../environments/environment";
import {Router} from "@angular/router";

@Component({
  selector: 'app-missions',
  templateUrl: './missions.component.html',
  styleUrls: ['./missions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MissionsComponent {

  loggedInUser$ = this.authService.getLoggedUser();

  missionComplete$: Observable<MissionComplete[]> = this.loggedInUser$.pipe(switchMap(user => {
    if(user!==null)
      return this.missionService.getMissionsComplete(`${environment.API_ENDPOINT}/users/${user.id}/mission-progresses`)
    this.router.navigate(['/profile'])
    return of([])
  }));


  constructor(private readonly missionService: EnumsService, private readonly authService: AuthenticationService, private router: Router) {
  }
}

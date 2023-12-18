import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-community-guidelines',
  templateUrl: './community-guidelines.component.html',
  styleUrls: ['./community-guidelines.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CommunityGuidelinesComponent {
  guidelines = [
    'guidelines.disrespect',
    'guidelines.spam',
    'guidelines.irrelevant',
    'guidelines.spoiler',
    'guidelines.piracy',
    'guidelines.privacy',
  ];
}

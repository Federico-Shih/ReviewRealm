import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-setting-view',
  templateUrl: './setting-view.component.html',
  styleUrls: ['./setting-view.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SettingViewComponent {}

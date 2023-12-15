import {ChangeDetectionStrategy, Component, Input} from '@angular/core';

type HeaderBannerType = 'default' | 'warning';

@Component({
  selector: 'app-header-banner',
  templateUrl: './header-banner.component.html',
  styleUrls: ['./header-banner.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HeaderBannerComponent {
  @Input()
  title: string = '';

  @Input({required: true})
  icon!: string;

  @Input()
  buttonLabel: string = '';

  @Input()
  routerLink: string = '';

  @Input()
  description: string = '';

  @Input()
  bannerType: HeaderBannerType = 'default';
}

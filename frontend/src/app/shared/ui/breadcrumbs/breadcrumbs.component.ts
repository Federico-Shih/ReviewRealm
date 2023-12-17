import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

type Breadcrumb = {
  name: string | undefined;
  link: string | null;
};

@Component({
  selector: 'app-breadcrumbs',
  templateUrl: './breadcrumbs.component.html',
  styleUrls: ['./breadcrumbs.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BreadcrumbsComponent {
  @Input({ required: true })
  breadcrumbs: Breadcrumb[] = [];
}

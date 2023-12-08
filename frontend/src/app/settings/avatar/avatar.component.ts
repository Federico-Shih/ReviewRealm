import { ChangeDetectionStrategy, Component } from '@angular/core';
import {environment} from "../../../environments/environment.development";

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AvatarComponent {
  avatars= ['http://localhost:8080/paw-2023a-04/static/avatars/1.png', `${environment.IMAGE_ENDPOINT}/2.png`, `${environment.IMAGE_ENDPOINT}/3.png`, `${environment.IMAGE_ENDPOINT}/4.png`, `${environment.IMAGE_ENDPOINT}/5.png`, `${environment.IMAGE_ENDPOINT}/6.png`];

}

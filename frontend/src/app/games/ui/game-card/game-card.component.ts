import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Game} from "../../../shared/data-access/games/games.class";

@Component({
  selector: 'app-game-card',
  templateUrl: './game-card.component.html',
  styleUrls: ['./game-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameCardComponent {
  @Input({required: true}) game:Game | null = null;

  @Input() small:boolean = false;
}

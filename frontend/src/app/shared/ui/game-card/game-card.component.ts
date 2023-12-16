import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Game} from "../../data-access/games/games.class";

@Component({
  selector: 'app-game-card',
  templateUrl: './game-card.component.html',
  styleUrls: ['./game-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameCardComponent {
  @Input({required: true}) game:Game | null = null;
  @Input() nolink:boolean = false;

  @Input() small:boolean = false;
  @Input() tiny:boolean = false;
}
import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {Game} from "../../../shared/data-access/games/games.class";
import {STATE_CHANGE} from "../../../shared/data-access/reports/reports.dtos";

export type GameSubmissionEvent = {
  game: Game;
  accept: boolean;
}

@Component({
  selector: 'app-game-submission-card',
  templateUrl: './game-submission-card.component.html',
  styleUrls: ['./game-submission-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameSubmissionCardComponent {

  @Input({required: true})
  game:Game | null = null;

  @Output()
  handleSubmission = new EventEmitter<GameSubmissionEvent>();
  constructor() { }


  protected readonly STATE_CHANGE = STATE_CHANGE;
}

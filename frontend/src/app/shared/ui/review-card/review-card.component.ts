import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {Review} from "../../data-access/reviews/review.class";

@Component({
  selector: 'app-review-card',
  templateUrl: './review-card.component.html',
  styleUrls: ['./review-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReviewCardComponent {
  @Input({required: true})
  review!: Review;

  @Input()
  hideActions = false;

  @Output()
  click = new EventEmitter();
}

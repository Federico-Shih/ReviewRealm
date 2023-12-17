import {ChangeDetectionStrategy, Component, computed, EventEmitter, Input, Output} from '@angular/core';
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

  userRank = () => {
    if (!(this.review.author)) return "basic";
    const level = this.review.author.xp / 100;
    if (level > 40) {
      return "epic";
    } else if (level > 30) {
      return "gold";
    } else if (level > 20) {
      return "silver";
    } else if (level > 10) {
      return "bronze";
    }
    return "basic";
  }

}

import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output,} from '@angular/core';
import {Game} from '../../../shared/data-access/games/games.class';
import {Review} from '../../../shared/data-access/reviews/review.class';
import {Paginated} from '../../../shared/data-access/shared.models';

@Component({
  selector: 'app-game-detail-page',
  templateUrl: './game-detail-page.component.html',
  styleUrls: ['./game-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GameDetailPageComponent {
  @Input({required: true}) game: Game | null = null;
  @Input() isModerator = false;
  @Input() userReview: Review | null = null;
  @Input() paginatedReviews: Paginated<Review> | null = null;
  @Input() loadingReviews: boolean | null = false;

  @Output() deleteGame = new EventEmitter<void>();
  @Output() showMoreReviews = new EventEmitter<string>();

  showMore(link: string) {
    this.showMoreReviews.emit(link);
  }
}

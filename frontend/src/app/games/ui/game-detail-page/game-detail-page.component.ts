import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { Game } from '../../../shared/data-access/games/games.class';
import { Review } from '../../../shared/data-access/reviews/review.class';
import { Paginated } from '../../../shared/data-access/shared.models';

@Component({
  selector: 'app-game-detail-page',
  templateUrl: './game-detail-page.component.html',
  styleUrls: ['./game-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GameDetailPageComponent implements OnChanges {
  @Input({ required: true }) game: Game | null = null;
  @Input() isModerator = false;
  @Input() userReview: Review | null = null;
  @Input() paginatedReviews: Paginated<Review> | null = null;
  @Input() loadingReviews: boolean | null = false;

  @Output() deleteGame = new EventEmitter<void>();
  @Output() showMoreReviews = new EventEmitter<string>();

  ngOnChanges(changes: SimpleChanges): void {
    console.log(changes);
  }

  showMore(link: string) {
    this.showMoreReviews.emit(link);
  }
}

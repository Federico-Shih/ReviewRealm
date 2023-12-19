import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output,} from '@angular/core';
import {Game} from '../../../shared/data-access/games/games.class';
import {Review} from '../../../shared/data-access/reviews/review.class';
import {Paginated} from '../../../shared/data-access/shared.models';

@Component({
  selector: 'app-game-detail-page',
  templateUrl: './game-detail-page.component.html',
  styleUrls: ['./game-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GameDetailPageComponent implements OnInit{
  @Input({required: true}) game: Game | null = null;
  @Input() isModerator = false;
  @Input() userReview: Review | null = null;
  @Input() paginatedReviews: Paginated<Review> | null = null;
  @Input() loadingReviews: boolean | null = false;

  @Output() deleteGame = new EventEmitter<void>();
  @Output() showMoreReviews = new EventEmitter<string>();

  protected breakpoint = 1;

  showMore(link: string) {
    this.showMoreReviews.emit(link);
  }

  ngOnInit() {
    this.breakpoint =
      window.innerWidth <= 1100 ? 1 : window.innerWidth <= 2100 ? 2 : 3;
  }

  onResize(event: Event) {
    const target = event.target as Window;
    if (target === null || target.innerWidth === null) return;
    this.breakpoint =
      target.innerWidth <= 1100 ? 1 : target.innerWidth <= 2100 ? 2 : 3;
  }
}

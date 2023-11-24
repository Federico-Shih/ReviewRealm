import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ReviewsService} from "../../../shared/data-access/reviews/reviews.service";
import {BehaviorSubject} from "rxjs";
import {Review} from "../../../shared/data-access/reviews/review.class";

@Component({
  selector: 'app-review-search',
  templateUrl: './review-search.component.html',
  styleUrls: ['./review-search.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReviewSearchComponent implements OnInit {
  review$ = new BehaviorSubject<Review | null>(null);

  constructor(private readonly reviewsService: ReviewsService) {
  }

  ngOnInit() {
    this.reviewsService.getReviewById('http://localhost:8080/paw-2023a-04/api/reviews/1').subscribe((review) => {
      this.review$.next(review);
    });
  }
}

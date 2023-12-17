import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewSubmitSearchComponent } from './review-submit-search.component';

describe('ReviewSubmitSearchComponent', () => {
  let component: ReviewSubmitSearchComponent;
  let fixture: ComponentFixture<ReviewSubmitSearchComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReviewSubmitSearchComponent],
    });
    fixture = TestBed.createComponent(ReviewSubmitSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

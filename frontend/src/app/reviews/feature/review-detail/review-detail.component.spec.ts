import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewDetailComponent } from './review-detail.component';

describe('ReviewDetailComponent', () => {
  let component: ReviewDetailComponent;
  let fixture: ComponentFixture<ReviewDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReviewDetailComponent]
    });
    fixture = TestBed.createComponent(ReviewDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

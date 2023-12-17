import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameSubmissionCardComponent } from './game-submission-card.component';

describe('GameSubmissionCardComponent', () => {
  let component: GameSubmissionCardComponent;
  let fixture: ComponentFixture<GameSubmissionCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GameSubmissionCardComponent],
    });
    fixture = TestBed.createComponent(GameSubmissionCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

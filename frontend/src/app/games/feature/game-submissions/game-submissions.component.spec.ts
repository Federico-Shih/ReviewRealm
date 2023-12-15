import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameSubmissionsComponent } from './game-submissions.component';

describe('GameSubmissionsComponent', () => {
  let component: GameSubmissionsComponent;
  let fixture: ComponentFixture<GameSubmissionsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GameSubmissionsComponent]
    });
    fixture = TestBed.createComponent(GameSubmissionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

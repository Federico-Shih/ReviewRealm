import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameDetailPageComponent } from './game-detail-page.component';

describe('GameDetailPageComponent', () => {
  let component: GameDetailPageComponent;
  let fixture: ComponentFixture<GameDetailPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GameDetailPageComponent],
    });
    fixture = TestBed.createComponent(GameDetailPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

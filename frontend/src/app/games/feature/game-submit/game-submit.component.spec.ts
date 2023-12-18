import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameSubmitComponent } from './game-submit.component';

describe('GameSubmitComponent', () => {
  let component: GameSubmitComponent;
  let fixture: ComponentFixture<GameSubmitComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GameSubmitComponent],
    });
    fixture = TestBed.createComponent(GameSubmitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

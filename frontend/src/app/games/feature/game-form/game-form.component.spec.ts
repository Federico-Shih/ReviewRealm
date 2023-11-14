import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameFormComponent } from './game-form.component';

describe('GameFormComponent', () => {
  let component: GameFormComponent;
  let fixture: ComponentFixture<GameFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GameFormComponent]
    });
    fixture = TestBed.createComponent(GameFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

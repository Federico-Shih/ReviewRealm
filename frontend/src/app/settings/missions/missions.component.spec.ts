import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MissionsComponent } from './missions.component';

describe('MissionsComponent', () => {
  let component: MissionsComponent;
  let fixture: ComponentFixture<MissionsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MissionsComponent]
    });
    fixture = TestBed.createComponent(MissionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

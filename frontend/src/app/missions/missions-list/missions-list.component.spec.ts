import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MissionsListComponent } from './missions-list.component';

describe('MissionsListComponent', () => {
  let component: MissionsListComponent;
  let fixture: ComponentFixture<MissionsListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MissionsListComponent]
    });
    fixture = TestBed.createComponent(MissionsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

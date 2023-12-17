import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileDetailComponent } from './profile-detail.component';
import { UsersService } from '../../../shared/data-access/users/users.service';

describe('ProfileDetailComponent', () => {
  let component: ProfileDetailComponent;
  let fixture: ComponentFixture<ProfileDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProfileDetailComponent],
      imports: [UsersService],
    });
    fixture = TestBed.createComponent(ProfileDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { TestBed } from '@angular/core/testing';

import { UsersService } from './users.service';
import { SharedModule } from '../../shared.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('UsersService', () => {
  let service: UsersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, SharedModule],
    });
    service = TestBed.inject(UsersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

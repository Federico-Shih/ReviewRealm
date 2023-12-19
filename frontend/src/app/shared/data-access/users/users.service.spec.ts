import {TestBed, waitForAsync} from '@angular/core/testing';

import { UsersService } from './users.service';
import { SharedModule } from '../../shared.module';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {userMock1} from "../../../../tests/mocks/user.mock";
import {EnumsService} from "../enums/enums.service";
import {of} from "rxjs";
import {genresMock2} from "../../../../tests/mocks/enums.mock";

describe('UsersService', () => {
  const MOCK = 'MOCK_ENDPOINT';
  let service: UsersService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, SharedModule],
    });
    TestBed.overrideProvider(EnumsService, {
      useValue: {
        getGenres: () => {
          return of(genresMock2);
        }
      }
    });
    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(UsersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get users', waitForAsync(() => {
    const req = httpTestingController.match(MOCK);
    const users = service.getUsers(MOCK);

    req.forEach((r) => {
      r.flush([userMock1], { headers: {
          'Link': '<https://api.github.com/repos?page=3&per_page=100>; rel="next", <https://api.github.com/repos?page=50&per_page=100>; rel="prev"'
        }});
    })

    expect(users).toBeTruthy();
    users.subscribe((response) => {
      expect(response.content.length).toBe(1);
      expect(response.content).toEqual([userMock1]);
    });
  }));

  it('should get user by id', waitForAsync(() => {
    const req = httpTestingController.match(MOCK);
    const user = service.getUserById(MOCK);

    req.forEach((r) => {
      r.flush(userMock1, { headers: {
          'Link': '<https://api.github.com/repos?page=3&per_page=100>; rel="next", <https://api.github.com/repos?page=50&per_page=100>; rel="prev"'
        }});
    })

    expect(user).toBeTruthy();
    user.subscribe((response) => {
      expect(response).toEqual(userMock1);
    });
  }));

});

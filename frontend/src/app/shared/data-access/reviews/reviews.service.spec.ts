import { TestBed } from '@angular/core/testing';

import { ReviewsService } from './reviews.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import { SharedModule } from '../../shared.module';
import {UsersService} from "../users/users.service";
import {userMock1} from "../../../../tests/mocks/user.mock";
import {of} from "rxjs";
import {GamesService} from "../games/games.service";
import {gameMock} from "../../../../tests/mocks/games.mock";
import {ReviewMock} from "../../../../tests/mocks/reviews.mock";

describe('ReviewsService', () => {
  let service: ReviewsService;
  let httpTesting: HttpTestingController;
  const MOCK = 'https://reviewrealm.com/api/reviews?pageSize=10';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, SharedModule],
      providers: [
        {
          provide: UsersService,
          useValue: {
            getUserById: () => {
              return of(userMock1);
            }
          }
        },
        {
          provide: GamesService,
          useValue: {
            getGame: () => {
              return of(gameMock);
            }
          }
        }
      ]
    });
    service = TestBed.inject(ReviewsService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get reviews', () => {
    service.getReviews(MOCK).subscribe((response) => {
      expect(response.content.length).toBe(1);
      expect(response.content).toEqual([ReviewMock]);
    });
    const req = httpTesting.expectOne(MOCK);
    expect(req.request.method).toBe('GET');
    req.flush([ReviewMock]);
  });

  it('should get review by id', () => {
    service.getReviewById(MOCK).subscribe((response) => {
      expect(response).toEqual(ReviewMock);
    });
    const req = httpTesting.expectOne(MOCK);
    expect(req.request.method).toBe('GET');
    req.flush(ReviewMock);
  });

  it('return empty paginated on 204', () => {
    service.getReviews(MOCK).subscribe((response) => {
      expect(response.content.length).toBe(0);
      expect(response.content).toEqual([]);
    });
    const req = httpTesting.expectOne(MOCK);
    expect(req.request.method).toBe('GET');
    req.flush(null, { status: 204, statusText: 'aaa' });
  });

  it('throw error on 404 by review id', () => {
    service.getReviewById(MOCK).subscribe(() => {}, (error) => {
      expect(error.status).toBe(404);
    });
    const req = httpTesting.expectOne(MOCK);
    expect(req.request.method).toBe('GET');
    req.flush(null, { status: 404, statusText: 'aaa' });
  })
});

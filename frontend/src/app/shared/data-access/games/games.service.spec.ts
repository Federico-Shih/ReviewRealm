import { TestBed } from '@angular/core/testing';

import { GamesService } from './games.service';
import {SharedModule} from "../../shared.module";
import {EnumsService} from "../enums/enums.service";
import {of} from "rxjs";
import {genresMock3} from "../../../../tests/mocks/enums.mock";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {gameMock} from "../../../../tests/mocks/games.mock";
import {TOTAL_ELEMENTS_HEADER, TOTAL_PAGES_HEADER} from "../../helpers/mapper";
import {HttpHeaders} from "@angular/common/http";

describe('GameService', () => {
  const MOCK = 'test';

  let httpController: HttpTestingController;
  let service: GamesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SharedModule, HttpClientTestingModule],
      providers: [{
        provide: EnumsService,
        useValue: {
          getGenres: () => {
            return of(genresMock3);
          }
        }
      }]
    });
    httpController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(GamesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('return paginated games', () => {
    const games = service.getGames(MOCK, {page: 1, pageSize: 10});
    games.subscribe(value => {
      expect(value.content.length).toBe(1);
      expect(value.totalElements).toBe(1);
      expect(value.totalPages).toBe(1);
      expect(value.content[0].genres).toBe(genresMock3);
    });

    const req = httpController.expectOne(`${MOCK}?page=1&pageSize=10`);

    let headers = new HttpHeaders();
    headers = headers.append(TOTAL_PAGES_HEADER, '1');
    headers = headers.append(TOTAL_ELEMENTS_HEADER, '1');

    req.flush([gameMock], {
      headers
    });
  });
});

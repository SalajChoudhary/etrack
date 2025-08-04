import { TestBed } from '@angular/core/testing';

import { FoilService } from './foil.service';

describe('FoilService', () => {
  let service: FoilService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FoilService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

import { TestBed } from '@angular/core/testing';

import { GwteService } from './gwte.service';

describe('GwteService', () => {
  let service: GwteService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GwteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

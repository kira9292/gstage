import { TestBed } from '@angular/core/testing';

import { DemandeStageService } from './demande-stage.service';

describe('DemandeStageService', () => {
  let service: DemandeStageService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DemandeStageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

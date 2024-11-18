import { TestBed } from '@angular/core/testing';

import { AssistantgwteService } from './assistantgwte.service';

describe('AssistantgwteService', () => {
  let service: AssistantgwteService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AssistantgwteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

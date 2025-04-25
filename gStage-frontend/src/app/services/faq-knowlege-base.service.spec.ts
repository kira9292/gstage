import { TestBed } from '@angular/core/testing';

import { FaqKnowlegeBaseService } from './faq-knowlege-base.service';

describe('FaqKnowlegeBaseService', () => {
  let service: FaqKnowlegeBaseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FaqKnowlegeBaseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

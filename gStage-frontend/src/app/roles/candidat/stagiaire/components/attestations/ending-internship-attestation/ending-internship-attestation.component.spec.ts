import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EndingInternshipAttestationComponent } from './ending-internship-attestation.component';

describe('EndingInternshipAttestationComponent', () => {
  let component: EndingInternshipAttestationComponent;
  let fixture: ComponentFixture<EndingInternshipAttestationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EndingInternshipAttestationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EndingInternshipAttestationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

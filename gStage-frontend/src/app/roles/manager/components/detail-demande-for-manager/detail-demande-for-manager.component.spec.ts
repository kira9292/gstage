import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailDemandeForManagerComponent } from './detail-demande-for-manager.component';

describe('DetailDemandeForManagerComponent', () => {
  let component: DetailDemandeForManagerComponent;
  let fixture: ComponentFixture<DetailDemandeForManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailDemandeForManagerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetailDemandeForManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

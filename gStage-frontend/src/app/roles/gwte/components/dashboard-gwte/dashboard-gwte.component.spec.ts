import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardGwteComponent } from './dashboard-gwte.component';

describe('DashboardGwteComponent', () => {
  let component: DashboardGwteComponent;
  let fixture: ComponentFixture<DashboardGwteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardGwteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardGwteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PoposerManagerComponent } from './poposer-manager.component';

describe('PoposerManagerComponent', () => {
  let component: PoposerManagerComponent;
  let fixture: ComponentFixture<PoposerManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PoposerManagerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PoposerManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

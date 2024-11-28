import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationsGwteComponent } from './notifications-gwte.component';

describe('NotificationsGwteComponent', () => {
  let component: NotificationsGwteComponent;
  let fixture: ComponentFixture<NotificationsGwteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotificationsGwteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotificationsGwteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

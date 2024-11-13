import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SidebarGwteComponent } from './sidebar-gwte.component';

describe('SidebarGwteComponent', () => {
  let component: SidebarGwteComponent;
  let fixture: ComponentFixture<SidebarGwteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SidebarGwteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SidebarGwteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

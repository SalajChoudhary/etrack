import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VirtualDesktopGridComponent } from './virtual-desktop-grid.component';

describe('VirtualDesktopGridComponent', () => {
  let component: VirtualDesktopGridComponent;
  let fixture: ComponentFixture<VirtualDesktopGridComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VirtualDesktopGridComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VirtualDesktopGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

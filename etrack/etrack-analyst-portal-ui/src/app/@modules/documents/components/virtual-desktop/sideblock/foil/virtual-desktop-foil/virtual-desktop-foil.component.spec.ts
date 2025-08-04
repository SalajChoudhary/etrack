import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VirtualDesktopFoilComponent } from './virtual-desktop-foil.component';

describe('VirtualDesktopFoilComponent', () => {
  let component: VirtualDesktopFoilComponent;
  let fixture: ComponentFixture<VirtualDesktopFoilComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VirtualDesktopFoilComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VirtualDesktopFoilComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

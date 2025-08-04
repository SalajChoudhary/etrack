import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VirtualDesktopFoilTableComponent } from './virtual-desktop-foil-table.component';

describe('VirtualDesktopFoilTableComponent', () => {
  let component: VirtualDesktopFoilTableComponent;
  let fixture: ComponentFixture<VirtualDesktopFoilTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VirtualDesktopFoilTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VirtualDesktopFoilTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

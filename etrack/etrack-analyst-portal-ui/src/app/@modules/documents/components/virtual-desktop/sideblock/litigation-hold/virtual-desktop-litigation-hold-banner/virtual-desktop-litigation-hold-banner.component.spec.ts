import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VirtualDesktopLitigationHoldBannerComponent } from './virtual-desktop-litigation-hold-banner.component';

describe('VirtualDesktopLitigationHoldBannerComponent', () => {
  let component: VirtualDesktopLitigationHoldBannerComponent;
  let fixture: ComponentFixture<VirtualDesktopLitigationHoldBannerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VirtualDesktopLitigationHoldBannerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VirtualDesktopLitigationHoldBannerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VirtualDesktopLitigationHoldConfirmModalComponent } from './virtual-desktop-litigation-hold-confirm-modal.component';

describe('VirtualDesktopLitigationHoldConfirmModalComponent', () => {
  let component: VirtualDesktopLitigationHoldConfirmModalComponent;
  let fixture: ComponentFixture<VirtualDesktopLitigationHoldConfirmModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VirtualDesktopLitigationHoldConfirmModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VirtualDesktopLitigationHoldConfirmModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

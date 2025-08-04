import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VirtualDesktopTableFeesNInvoiceComponent } from './virtual-desktop-table-fees-n-invoice.component';

describe('VirtualDesktopTableFeesNInvoiceComponent', () => {
  let component: VirtualDesktopTableFeesNInvoiceComponent;
  let fixture: ComponentFixture<VirtualDesktopTableFeesNInvoiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VirtualDesktopTableFeesNInvoiceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VirtualDesktopTableFeesNInvoiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

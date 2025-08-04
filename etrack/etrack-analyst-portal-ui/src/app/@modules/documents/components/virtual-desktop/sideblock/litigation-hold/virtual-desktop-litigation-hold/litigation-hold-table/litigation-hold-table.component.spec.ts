import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VirtualDesktopLitigationHoldTableComponent } from './litigation-hold-table.component';

describe('LitigationHoldTableComponent', () => {
  let component: LitigationHoldTableComponent;
  let fixture: ComponentFixture<LitigationHoldTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VirtualDesktopLitigationHoldTableComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VirtualDesktopLitigationHoldTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PermitSelectionSummaryModifyPopupComponentComponent } from './permit-selection-summary-modify-popup-component.component';

describe('PermitSelectionSummaryModifyPopupComponentComponent', () => {
  let component: PermitSelectionSummaryModifyPopupComponentComponent;
  let fixture: ComponentFixture<PermitSelectionSummaryModifyPopupComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PermitSelectionSummaryModifyPopupComponentComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PermitSelectionSummaryModifyPopupComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PermitSelectionSummaryCommonPopupComponentComponent } from './permit-selection-summary-common-popup-component.component';

describe('PermitSelectionSummaryCommonPopupComponentComponent', () => {
  let component: PermitSelectionSummaryCommonPopupComponentComponent;
  let fixture: ComponentFixture<PermitSelectionSummaryCommonPopupComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PermitSelectionSummaryCommonPopupComponentComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PermitSelectionSummaryCommonPopupComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

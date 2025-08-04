import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PermitSelectionSummaryComponent } from './permit-selection-summary.component';

describe('PermitSelectionSummaryComponent', () => {
  let component: PermitSelectionSummaryComponent;
  let fixture: ComponentFixture<PermitSelectionSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PermitSelectionSummaryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PermitSelectionSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

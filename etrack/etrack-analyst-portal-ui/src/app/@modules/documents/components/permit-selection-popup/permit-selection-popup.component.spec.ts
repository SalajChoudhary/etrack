import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PermitSelectionPopupComponent } from './permit-selection-popup.component';

describe('PermitSelectionPopupComponent', () => {
  let component: PermitSelectionPopupComponent;
  let fixture: ComponentFixture<PermitSelectionPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PermitSelectionPopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PermitSelectionPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

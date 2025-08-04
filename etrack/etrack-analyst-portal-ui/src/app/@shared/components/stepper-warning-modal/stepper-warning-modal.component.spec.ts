import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StepperWarningModalComponent } from './stepper-warning-modal.component';

describe('StepperWarningModalComponent', () => {
  let component: StepperWarningModalComponent;
  let fixture: ComponentFixture<StepperWarningModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StepperWarningModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StepperWarningModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

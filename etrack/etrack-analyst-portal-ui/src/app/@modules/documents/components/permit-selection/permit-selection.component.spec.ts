import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PermitSelectionComponent } from './permit-selection.component';

describe('PermitSelectionComponent', () => {
  let component: PermitSelectionComponent;
  let fixture: ComponentFixture<PermitSelectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PermitSelectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PermitSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

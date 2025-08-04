import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValidateCheckboxComponent } from './validate-checkbox.component';

describe('ValidateCheckboxComponent', () => {
  let component: ValidateCheckboxComponent;
  let fixture: ComponentFixture<ValidateCheckboxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ValidateCheckboxComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ValidateCheckboxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

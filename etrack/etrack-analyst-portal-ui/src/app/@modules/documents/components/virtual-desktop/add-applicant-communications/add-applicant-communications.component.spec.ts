import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddApplicantCommunicationsComponent } from './add-applicant-communications.component';

describe('AddApplicantCommunicationsComponent', () => {
  let component: AddApplicantCommunicationsComponent;
  let fixture: ComponentFixture<AddApplicantCommunicationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddApplicantCommunicationsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddApplicantCommunicationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

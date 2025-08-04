import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplicantInformationsComponent } from './applicant-informations.component';

describe('ApplicantInformationsComponent', () => {
  let component: ApplicantInformationsComponent;
  let fixture: ComponentFixture<ApplicantInformationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ApplicantInformationsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicantInformationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

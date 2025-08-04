import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplicantInformationsSearchComponent } from './applicant-informations-search.component';

describe('ApplicantInformationsSearchComponent', () => {
  let component: ApplicantInformationsSearchComponent;
  let fixture: ComponentFixture<ApplicantInformationsSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ApplicantInformationsSearchComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicantInformationsSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

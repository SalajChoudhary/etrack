import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardApplicantResponseComponent } from './dashboard-applicant-response.component';

describe('DashboardApplicantResponseComponent', () => {
  let component: DashboardApplicantResponseComponent;
  let fixture: ComponentFixture<DashboardApplicantResponseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DashboardApplicantResponseComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardApplicantResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

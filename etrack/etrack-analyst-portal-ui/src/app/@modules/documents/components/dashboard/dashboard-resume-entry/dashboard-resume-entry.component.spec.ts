import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardResumeEntryComponent } from './dashboard-resume-entry.component';

describe('DashboardResumeEntryComponent', () => {
  let component: DashboardResumeEntryComponent;
  let fixture: ComponentFixture<DashboardResumeEntryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DashboardResumeEntryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardResumeEntryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

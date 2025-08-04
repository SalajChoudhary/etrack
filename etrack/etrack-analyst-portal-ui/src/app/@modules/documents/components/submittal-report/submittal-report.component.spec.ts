import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubmittalReportComponent } from './submittal-report.component';

describe('SubmittalReportComponent', () => {
  let component: SubmittalReportComponent;
  let fixture: ComponentFixture<SubmittalReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubmittalReportComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SubmittalReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

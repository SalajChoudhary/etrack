import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CandidateKeywordsReportComponent } from './candidate-keywords-report.component';

describe('CandidateKeywordsReportComponent', () => {
  let component: CandidateKeywordsReportComponent;
  let fixture: ComponentFixture<CandidateKeywordsReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CandidateKeywordsReportComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CandidateKeywordsReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

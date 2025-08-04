import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgramReviewCorrespondenceComponent } from './program-review-correspondence.component';

describe('ProgramReviewCorrespondenceComponent', () => {
  let component: ProgramReviewCorrespondenceComponent;
  let fixture: ComponentFixture<ProgramReviewCorrespondenceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProgramReviewCorrespondenceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgramReviewCorrespondenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

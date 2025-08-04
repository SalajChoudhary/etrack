import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddReviewRequestsComponent } from './add-review-requests.component';

describe('AddReviewRequestsComponent', () => {
  let component: AddReviewRequestsComponent;
  let fixture: ComponentFixture<AddReviewRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddReviewRequestsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddReviewRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InquiryDocumentationComponent } from './inquiry-documentation.component';

describe('InquiryDocumentationComponent', () => {
  let component: InquiryDocumentationComponent;
  let fixture: ComponentFixture<InquiryDocumentationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InquiryDocumentationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InquiryDocumentationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

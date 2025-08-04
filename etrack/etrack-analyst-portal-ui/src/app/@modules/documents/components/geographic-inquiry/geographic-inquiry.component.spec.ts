import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeographicInquiryComponent } from './geographic-inquiry.component';

describe('GeographicInquiryComponent', () => {
  let component: GeographicInquiryComponent;
  let fixture: ComponentFixture<GeographicInquiryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GeographicInquiryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GeographicInquiryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

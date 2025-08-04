import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ApplyForInquiryComponent } from './apply-for-inquiry.component';

describe('ApplyForInquiryComponent', () => {
  let component: ApplyForInquiryComponent;
  let fixture: ComponentFixture<ApplyForInquiryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ApplyForInquiryComponent ],
      imports:[
        HttpClientModule
      ]
    })
    .compileComponents();
  });
  
  beforeEach(() => {
    fixture = TestBed.createComponent(ApplyForInquiryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ApplyForPermitDetailsComponent } from './apply-for-permit-details.component';

describe('ApplyForPermitDetailsComponent', () => {
  let component: ApplyForPermitDetailsComponent;
  let fixture: ComponentFixture<ApplyForPermitDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ApplyForPermitDetailsComponent ],
      imports:[
        HttpClientModule
      ]
    })
    .compileComponents();
  });
  
  beforeEach(() => {
    fixture = TestBed.createComponent(ApplyForPermitDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

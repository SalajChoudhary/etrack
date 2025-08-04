import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ApplyForPermitComponent } from './apply-for-permit.component';

describe('ApplyForPermitComponent', () => {
  let component: ApplyForPermitComponent;
  let fixture: ComponentFixture<ApplyForPermitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ApplyForPermitComponent ],
      imports:[ReactiveFormsModule]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplyForPermitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('applicationReceipts should be just online for non-dec users', () => {
      fixture = TestBed.createComponent(ApplyForPermitComponent);
      component = fixture.componentInstance;
      component.userRoles=["Online Submitter"];
      fixture.detectChanges();
      expect(component.applicationReceipts).toEqual(["Online"]);
    });

    it('applicationReceipts should be just paper and email for dec users', () => {
      fixture = TestBed.createComponent(ApplyForPermitComponent);
      component = fixture.componentInstance;
      component.userRoles=["Analyst","Admin"];
      fixture.detectChanges();
      expect(component.applicationReceipts).toEqual(["Paper","Email"]);
    });


});

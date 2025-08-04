import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Location } from "@angular/common";
import { RouterTestingModule } from "@angular/router/testing"
import { Router } from "@angular/router";
import { AssociatedApplicantsComponent } from './associated-applicants.component';
import { DocumentsModule } from '../../documents.module';
import { ApplyForPermitDetailsComponent } from '../apply-for-permit-details/apply-for-permit-details.component';
import { ProjectService } from 'src/app/@shared/services/projectService';

describe('AssociatedApplicantsComponent', () => {
  let component: AssociatedApplicantsComponent;
  let fixture: ComponentFixture<AssociatedApplicantsComponent>;
  let projectService: ProjectService;
  let router: Router;
  let routes = [
    {
      path: 'apply-for-permit-details',
      component: ApplyForPermitDetailsComponent,
    },
  ]

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes(routes)],
      declarations: [ AssociatedApplicantsComponent ],
      providers: [ProjectService]
    })
    .compileComponents();

    router = TestBed.inject(Router);
    projectService = TestBed.inject(ProjectService);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssociatedApplicantsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should check stepperClickEvent', () => {
    component.stepperClickEvent(0);
    expect(component.popupData.title).toEqual(component.popUpTitles[0]);

    component.stepperClickEvent(1);
    expect(component.popupData.title).toEqual(component.popUpTitles[1]);

    component.stepperClickEvent(2);
    expect(component.popupData.title).toEqual(component.popUpTitles[2]);
  });

  it('should check navigateAccountInfo', fakeAsync(() => {
    component.navigateAccountInfo();
    expect(location.pathname).toBe("/apply-for-permit-details");
  }));

  it('should check when page load', fakeAsync(() => {
    spyOn(projectService, "getAssociateDetails").and.returnValues(Promise.resolve({}));
    component.ngOnInit();
    //component.addressDetails
    expect(component.addressDetails.projectName).toEqual("main Project");
    expect(component.addressDetails.street).toEqual("11 Main Street");
    expect(component.addressDetails.address).toEqual("Albany NY 11111");
    
    let applicationId = 10;
    expect(projectService.getAssociateDetails).toHaveBeenCalledOnceWith(applicationId);
  }));

});

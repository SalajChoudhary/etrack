import { Component, OnInit, SimpleChange, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import Graphic from '@arcgis/core/Graphic';
import { GisService } from '../../../../@shared/services/gisService';
import { TitleCasePipe } from '@angular/common';
import { Router } from '@angular/router';
import { ProjectLocation } from 'src/app/@store/models/projectLocation';
import { CommonService } from 'src/app/@shared/services/commonService';
import {
  ModalDismissReasons,
  NgbModal,
  NgbModalRef,
} from '@ng-bootstrap/ng-bootstrap';
import { MatStepper } from '@angular/material/stepper';
import Polygon from '@arcgis/core/geometry/Polygon';
import { environment } from 'src/environments/environment';
import { GisMapEditorComponent } from '../gis-map-editor/gis-map-editor.component';
import { ApprovedFacility } from 'src/app/@store/models/facility';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { takeUntil } from 'rxjs/operators';
import { fromEvent, Subject } from 'rxjs';
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { Utils } from 'src/app/@shared/services/utils';
import { CivilDivision } from 'src/app/@store/models/civilDivision';
import { GISLocation } from 'src/app/@store/models/gisLocation';
import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { truncate } from 'lodash';

@Component({
  selector: 'app-project-location-details',
  templateUrl: './project-location-details.component.html',
  styleUrls: ['./project-location-details.component.scss'],
  providers: [
    TitleCasePipe,
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { displayDefaultIndicatorType: false },
    },
  ],
})
export class ProjectLocationDetailsComponent implements OnInit {
  yieldSignPath = 'assets/icons/yieldsign.svg';
  editSignPath = 'assets/icons/revert_symbol.svg';
  mode: any = localStorage.getItem('mode');
  userRoles: any[] = [];
  errorMsgObj: any = {};
  private unSubscriber: Subject<void> = new Subject<void>();

  @ViewChild('stepper', { static: false }) stepper!: MatStepper;
  @ViewChild(GisMapEditorComponent, { static: true })
  gisMapEdit!: GisMapEditorComponent;
  @ViewChild('pendingPopup', { static: true })
  warningModal!: PendingChangesPopupComponent;
  @ViewChild('submitConfirmModal', { static: true }) submitConfirm!: NgbModal;
  @ViewChild('decIdChangeConfirmModal', { static: true }) decIdChangeConfirm!: NgbModal;
  @ViewChild('decIdConfirmModal', { static: true }) decIdConfirm!: NgbModal;

  modalReference!: NgbModalRef;

  closeResult = '';
  //stepper control data
  selectedIndex: number = 0;

  //roles
  isAnalyst: boolean = false;
  isApplicant: boolean = false;

  isSubmittalPoly: boolean = false;
  isApplicantPoly: boolean = false;
  isAnalystPoly: boolean = false;
  isAnalystPolyEdit: boolean = true;

  serviceError: boolean = false;
  serviceErrorMessage: string = '';

  projectLocationServiceError: boolean = false;
  projectLocationServiceErrorMessage: string = '';

  projectDetailsForm!: FormGroup;
  toggleDetailsForm!: FormGroup;

  decIdSearchForm!: FormGroup;
  decIdSearched: boolean = false;

  projectLocation!: ProjectLocation;
  originalProject!: ProjectLocation;

  projectLocationHistory!: any;

  isLoading = false;
  isSavedSubmitted = false;

  projectGraphic!: Graphic;
  workAreaGraphic!: Graphic;
  polygonType = '';
  eFindDECID!: string;
  facilityPolygonExist: boolean = false;
  workAreaPolygonModified: boolean = false;

  selectedFacility!: ApprovedFacility;
  isValidate: boolean = false;
  primaryMunicipalities!: Set<CivilDivision>;
  decIdChangeConfirmMessages: any[] = [];
  mapProperties = {
    basemap: 'streets',
    center: [-75.62757627797825, 42.98572311852962],
    zoom: 5,
  };
  //Permit Modes
  projectLocationValidated: boolean = false;

  get isReadonly() {
    return this.mode == 'read' || this.projectLocationValidated;
  }

  get isFacilityNameChanged() {
    if (
      this.isValidate &&
      this.projectLocationHistory?.hfacilityName !== undefined && this.projectLocationHistory?.hfacilityName !== null
    ) {
      return (
        this.projectLocation?.facility?.facilityName?.toUpperCase() !==
        this.projectLocationHistory?.hfacilityName?.toUpperCase()
      );
    } else {
      return false;
    }
  }

  get isFacilityNameToggle() {
    return (
      this.isValidate &&
      this.toggleDetailsForm.controls.projectName?.value !== undefined &&
      this.toggleDetailsForm.controls.projectName?.value !== null &&
      this.toggleDetailsForm.controls.projectName?.value !== ''
    );
  }

  get isFacilityStreet1Changed() {
    if (
      this.isValidate &&
      this.projectLocationHistory?.hstreet1 !== undefined && this.projectLocationHistory?.hstreet1 !== null
    ) {
      return (
        this.projectLocation?.facility?.address?.street1?.toUpperCase() !==
        this.projectLocationHistory?.hstreet1?.toUpperCase()
      );
    } else {
      return false;
    }
  }

  get isFacilityStreet1Toggle() {
    return (
      this.isValidate &&
      this.toggleDetailsForm.controls.address1?.value !== undefined &&
      this.toggleDetailsForm.controls.address1?.value !== null &&
      this.toggleDetailsForm.controls.address1?.value !== ''
    );
  }

  get isFacilityCityChanged() {
    if (this.isValidate && this.projectLocationHistory?.hcity !== undefined && this.projectLocationHistory?.hcity !== null) {
      return (
        this.projectLocation?.facility?.address?.city?.toUpperCase() !==
        this.projectLocationHistory?.hcity?.toUpperCase()
      );
    } else {
      return false;
    }
  }

  get isFacilityCityToggle() {
    return (
      this.isValidate &&
      this.toggleDetailsForm.controls.city?.value !== undefined &&
      this.toggleDetailsForm.controls.city?.value !== null &&
      this.toggleDetailsForm.controls.city?.value !== ''
    );
  }

  get isFacilityZipChanged() {
    if (this.isValidate && this.projectLocationHistory?.hzip !== undefined && this.projectLocationHistory?.hzip !== null) {
      return (
        this.projectLocation?.facility?.address?.zip?.toUpperCase() !==
        this.projectLocationHistory?.hzip?.toUpperCase()
      );
    } else {
      return false;
    }
  }

  get isFacilityZipToggle() {
    return (
      this.isValidate &&
      this.toggleDetailsForm.controls.zip?.value !== undefined &&
      this.toggleDetailsForm.controls.zip?.value !== null &&
      this.toggleDetailsForm.controls.zip?.value !== ''
    );
  }

  get isFacilityStreet2Changed() {
    if (
      this.isValidate &&
      this.projectLocationHistory?.hstreet2 !== undefined && this.projectLocationHistory?.hstreet2 !== null
    ) {
      return (
        this.projectLocation?.facility?.address?.street2?.toUpperCase() !==
        this.projectLocationHistory?.hstreet2?.toUpperCase()
      );
    } else {
      return false;
    }
  }

  get isFacilityStreet2Toggle() {
    return (
      this.isValidate &&
      this.toggleDetailsForm.controls.address2?.value !== undefined &&
      this.toggleDetailsForm.controls.address2?.value !== null &&
      this.toggleDetailsForm.controls.address2?.value !== ''
    );
  }

  get isFacilityboundaryChangeReasonChanged() {
    if (
      this.isValidate &&
      this.projectLocationHistory?.hboundaryChangeReason !== undefined && this.projectLocationHistory?.hboundaryChangeReason !== null
    ) {
      return (
        this.projectLocation?.boundaryChangeReason?.toUpperCase() !==
        this.projectLocationHistory.hboundaryChangeReason?.toUpperCase()
      );
    } else {
      return false;
    }
  }

  get isFacilityboundaryChangeReasonToggle() {
    return (
      this.isValidate &&
      this.toggleDetailsForm.controls.boundaryChangeReason?.value !==
      undefined &&
      this.toggleDetailsForm.controls.boundaryChangeReason?.value !== null &&
      this.toggleDetailsForm.controls.boundaryChangeReason?.value !== ''
    );
  }

  get isFacilityReasonChanged() {
    if (this.isValidate && this.projectLocationHistory?.hreason !== undefined && this.projectLocationHistory?.hreason !== null) {
      return (
        this.projectLocation?.reason?.toUpperCase() !==
        this.projectLocationHistory.hreason?.toUpperCase()
      );
    } else {
      return false;
    }
  }

  get isFacilityReasonToggle() {
    return (
      this.isValidate &&
      this.toggleDetailsForm.controls.reason?.value !== undefined &&
      this.toggleDetailsForm.controls.reason?.value !== null &&
      this.toggleDetailsForm.controls.reason?.value !== ''
    );
  }

  get getEfindUrl(): string {
    return this.projectLocation?.facility.edbDistrictId ? (
      `${environment.facilityNameUrl}` +
      this.projectLocation.facility.edbDistrictId
    ) : (
      environment.facilityNameUrl.slice(0, -1) + 's'
    );
  }
  get getEfindDECID(): string {
    if (
      this.eFindDECID === undefined ||
      this.eFindDECID === null ||
      this.eFindDECID === ''
    ) {
      return 'New';
    } else {
      return this.eFindDECID;
    }
  }

  get isEFindDECIDEmpty(): boolean {
    if (
      this.eFindDECID === undefined ||
      this.eFindDECID === null ||
      this.eFindDECID === ''
    ) {
      return true;
    } else {
      return false;
    }
  }

  get taxParcelValue():string{
    if(this.projectDetailsForm.controls.taxMap.value !==undefined && this.projectDetailsForm.controls.taxMap.value !==null){
      if(this.projectDetailsForm.controls.taxMap.value.length ==2000)
		return  "Multiple Tax Parcels"
      else{
        return this.projectDetailsForm.controls.taxMap.value
      }
    }else{
      return "";
    }
  }

  constructor(
    private gisService: GisService,
    private router: Router,
    public utils: Utils,
    private _formBuilder: FormBuilder,
    public commonService: CommonService,
    private titleCasePipe: TitleCasePipe,
    private modalService: NgbModal,
    private errorService: ErrorService,
  ) { }

  ngOnInit(): void {
    this.userRoles = this.commonService.roles;
    this.mode = localStorage?.getItem('mode');
    if (this.mode == 'validate') {
      this.isValidate = true;
    }
    if (this.userRoles !== undefined) {
      this.isAnalyst =      this.isAnalyst = this.userRoles.includes(UserRole.Admin) || this.userRoles.includes(UserRole.Analyst) || this.userRoles.includes(UserRole.Override_Admin) || this.userRoles.includes(UserRole.System_Analyst) || this.userRoles.includes(UserRole.System_Admin) || this.userRoles.includes(UserRole.Override_Analyst);
      this.isApplicant = !this.isAnalyst;
    }
    let projectId = localStorage.getItem('projectId');
    this.initializeFrom();
    this.projectDetailsForm.controls['city'].valueChanges.subscribe((res: string) => {
      if (res && res.length >= 65) {
        const str = res.substring(0,65);
        this.projectDetailsForm.controls['city'].patchValue(str);
      }
    })
    if (projectId !== undefined && projectId !== null && projectId !== '') {
      this.initializeLocationDetails(projectId);
    } else {
      this.router.navigate(['/project-location']);
    }
    this.getAllErrorMsgs();

    history.pushState(null, '');
    //disables browsers back button
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unSubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }

  ngOnDestroy(): void {
    this.unSubscriber.next();
    this.unSubscriber.complete();
  }

  async initializeLocationDetails(projectId: string): Promise<void> {
    this.utils.emitLoadingEmitter(true);
    this.gisService.getProjectLocation(projectId).subscribe(
      async (data: any) => {
        this.eFindDECID = data.decIdFormatted;
        if (
          this.eFindDECID !== undefined &&
          this.eFindDECID !== null &&
          this.eFindDECID !== ''
        ) {
          await this.gisService.getDECPolygonByDecId(this.eFindDECID).subscribe(
            (data2: any) => {
              if (data2?.features !== null && data2?.features?.length > 0) {
                this.utils.emitLoadingEmitter(false);
                if (data2.features[0]?.attributes?.VALIDATED_LOCATION == 1) {
                  this.facilityPolygonExist = true;
                  if (this.isValidate) {
                    this.gisMapEdit.addToggleFacilityButton(this.eFindDECID);
                  }
                }
              } else {
                this.utils.emitLoadingEmitter(false);
              }
            },
            (error: any) => {
              this.utils.emitLoadingEmitter(false);
              this.handleError(error);
            }
          );
        } else {
          this.utils.emitLoadingEmitter(false);
        }
      },
      (error: any) => {
        this.utils.emitLoadingEmitter(false);
        this.handleError(error);
      }
    );

    this.gisService.getProjectDetails(projectId).subscribe(
      async (data: any) => {
        if (
          data.polygonId !== undefined &&
          data.polygonId !== '' &&
          data.polygonId !== null
        ) {
          if (this.isValidate) {
            //@ts-ignore
            await this.gisService
              .getProjectHistoryDetails(data.projectId)
              .toPromise()
              .then((history: any) => {
                this.projectLocationHistory = history?.facilityHistory;
              })
              .catch((error: any) => {
                this.handleError(error);
              });
          }
          this.copyProjectDataToDetailsForm(data);
          let polygonId=data.polygonId;
          if (this.projectLocation.polygonStatus === 'APPLICANT_SCRATCH') {
            this.loadApplicantMap(polygonId);
          }
          if (this.projectLocation.polygonStatus === 'APPLICANT_SUBMITTED') {
            this.loadSubmittedMap(polygonId);
          }
          if (this.projectLocation.polygonStatus === 'ANALYST_SCRATCH' || this.projectLocation.polygonStatus === 'ANALYST_APPROVED') {
            this.loadAnalystMap(polygonId);
          }
        } else {
          this.router.navigate(['/project-location']);
        }

        if (
          data.workAreaId !== undefined &&
          data.workAreaId !== '' &&
          data.workAreaId !== null
        ) {
          this.loadWorkAreaMap(data.workAreaId);
        }
      },
      (error: any) => {
        this.handleError(error);
      }
    );
  }

  private loadAnalystMap(polygonId:string) {
    this.utils.emitLoadingEmitter(true);
    this.isSubmittalPoly = false;
    this.isApplicantPoly = false;
    this.isAnalystPoly = true;
    this.gisService
      .searchAnalystPolygon(polygonId)
      .toPromise()
      .then((polygon: any) => {
        this.getProjectGraphic(polygon);
        if (polygon?.features[0]?.geometry !== undefined) {
          this.isAnalystPolyEdit = polygon.features[0].attributes?.EFINDSTATUS === 0;
          if (polygon.features[0].attributes?.EFINDSTATUS === 0) {
            if (this.isValidate) {
              this.gisMapEdit.addToggleButton();
              this.utils.emitLoadingEmitter(false);
            }
            this.gisMapEdit.setUpGraphicClickHandler();
            this.gisMapEdit.addSketchTool();
            this.utils.emitLoadingEmitter(false);
          } else {
            this.utils.emitLoadingEmitter(false);
          }
        } else {
          this.utils.emitLoadingEmitter(false);
        }
        if (polygon.features[0].attributes?.EFINDSTATUS === 1) {
          this.isSubmittalPoly = true;
        }
      })
      .catch((error: any) => {
        this.utils.emitLoadingEmitter(false);
        this.handleError(error);
      });
  }

  private loadApplicantMap(polygonId:string) {
    this.isSubmittalPoly = false;
    this.isApplicantPoly = true;
    this.isAnalystPoly = false;
    this.gisService
      .searchApplicantPolygon(polygonId)
      .toPromise()
      .then((polygon: any) => {
        this.getProjectGraphic(polygon);
        this.gisMapEdit.setUpGraphicClickHandler();
        this.gisMapEdit.addSketchTool();
      })
      .catch((error: any) => {
        this.handleError(error);
      });
  }

  private loadSubmittedMap(polygonId:string) {
    this.isSubmittalPoly = true;
    this.isApplicantPoly = false;
    this.isAnalystPoly = false;
    this.gisService
      .searchSubmittedPolygon(polygonId)
      .toPromise()
      .then((polygon: any) => {
        this.getProjectGraphic(polygon);
      })
      .catch((error: any) => {
        this.handleError(error);
      });
  }

  private loadWorkAreaMap(workAreaId: string) {
    this.gisService
      .searchWorkAreaPolygon(workAreaId)
      .toPromise()
      .then((polygon: any) => {
        this.getWorkAreaGraphic(polygon);
      })
      .catch((error: any) => {
        this.handleError(error);
      });
  }

  open(content: any, modelSize = '25vw') {
    this.modalReference = this.modalService.open(content, {
      ariaLabelledBy: 'modal-basic-title',
      size: modelSize,
    });
    this.modalReference.result.then(
      (result) => {
        this.closeResult = `Closed with: ${result}`;
        this.stepper.next();
      },
      (reason) => {
        this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
        this.stepper.next();
      }
    );
  }
  closeModal(e: any) {
    this.modalService.dismissAll();
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  backToMain() {
    this.router.navigate(['/apply-for-permit-details']);
  }

  backToLocation() {
    this.router.navigate(['/associated-applicants']);
  }
  async cancel() {
    await this.warningModal.open();
  }
  async getAllErrorMsgs() {
    try {
      let response = await this.commonService.getAllErrorMessages();
      if (!!response) {
        this.errorMsgObj = response['en-US'];
      }
    } catch (e) { }
  }

  private initializeFrom(): void {
    this.projectDetailsForm = this._formBuilder.group({
      decId: [''],
      projectName: ['', [Validators.required,this.utils.facilityNameValidator, Validators.maxLength(100)]],
      address1: ['', [Validators.required, Validators.maxLength(200)]],
      address2: [''],
      city: [
        '',
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(65),
        ],
      ],
      state: ['NY'],
      zip: ['', [Validators.minLength(5)]],
      direction: [''],
      reason: [''],
      boundaryChangeReason: [''],
      municipalityName: [''],
      municipality: [''],
      municipalitySwis: [''],
      primaryMunicipality: [''],
      primaryMunicipalityView: [''],
      countyName: [''],
      county: ['', [Validators.required]],
      countySwis: [''],
      taxMap: [''],
      latitude: [''],
      longitude: [''],
      nytmx: [''],
      nytmy: [''],
      region: [''],
      primaryRegion: [''],
      inquiryId1:[''],
      inquiryId2:[''],
      inquiryId3:[''],
      inquiryId4:[''],
      inquiryId5:[''],
    });
    this.decIdSearchForm = this._formBuilder.group({
      decId: ['', [Validators.required]],
    });
    //this form is to hold data for toggle
    this.toggleDetailsForm = this._formBuilder.group({
      projectName: [''],
      address1: [''],
      address2: [''],
      city: [''],
      zip: [''],
      reason: [''],
      boundaryChangeReason: [''],
    });
  }

  private getProjectGraphic(features: any): void {
    this.projectGraphic = this.buildGraphicFromFeature(features);
  }

  private getWorkAreaGraphic(features: any): void {
    this.workAreaGraphic = this.buildGraphicFromFeature(features);
  }

  private buildGraphicFromFeature(features: any): Graphic {
    let polygon = new Polygon({
      rings: (features.features[0]?.geometry as Polygon).rings,
      spatialReference: features.spatialReference,
    });
    return new Graphic({
      geometry: polygon,
      attributes: features.features[0]?.attributes,
    });
  }

  toggleValue(formControlName: any, value: any, value2: any) {
    if (this.isReadonly) {
      return;
    }
    //@ts-ignore
    this.toggleDetailsForm?.get(formControlName).setValue(value2);
    //@ts-ignore
    this.projectDetailsForm?.get(formControlName)?.setValue(value);
  }

  revertValue(formControlName: any) {
    if (this.isReadonly) {
      return;
    }
    //@ts-ignore
    this.projectDetailsForm
      ?.get(formControlName)
      ?.setValue(this.toggleDetailsForm?.get(formControlName)?.value);
    //@ts-ignore
    this.toggleDetailsForm?.get(formControlName)?.setValue();
  }

  decIdChange(event: any): void {
    this.open(this.decIdConfirm);
  }

  public async submitConform(): Promise<void> {
    if (this.isApplicantPoly) {
      this.open(this.submitConfirm);
    } else {
      if (this.isSubmittalPoly) {
        this.saveProjectLocationDetailsOnly();
      }
      if (this.isAnalystPoly) {
        if(this.projectLocationValidated){
          this.gisMapEdit.getPrintUrl().then((printUrl:string)=>{
            if(printUrl!==''){
              this.saveAnalystProjectLocation(this.projectLocationValidated,0,printUrl);
            }
          });
        }else{
          this.saveAnalystProjectLocation(this.projectLocationValidated);
        }
      }
    }
  }

  onValidateChange(validateFlag:boolean){
    if(!validateFlag){
      this.gisMapEdit.resetSketchWidget();
    }
  }

  saveOnly() {
    if (this.isApplicantPoly) {
      this.saveProjectLocation();
    }
  }

  saveAndSubmit() {
    if (this.isApplicantPoly) {
      this.gisMapEdit.getPrintUrl().then((printUrl:string)=>{
        if(printUrl!==''){
        this.saveProjectLocation(true,printUrl);
        }
      });
    }
  }

  public async saveProjectLocationDetailsOnly(): Promise<void> {
    this.utils.emitLoadingEmitter(true);
    if (!this.projectDetailsForm.invalid) {
      if (this.isProjectDataUpdated()) {
        this.gisService.updateProjectLocation(this.projectLocation).subscribe(
          (response: any) => {
            this.utils.emitLoadingEmitter(false);
            this.backToMain();
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            //delete the Polygon if save Project fail with Object id
            this.handleError(error);
          }
        );
      } else {
        this.utils.emitLoadingEmitter(false);
        this.backToMain();
        console.log('There are no updates to project Details');
      }
    } else {
      this.utils.emitLoadingEmitter(false);
    }
  }

  private updateProject(isSubmit: boolean, skipUpdate: boolean = false,printUrl:string='') {
    this.utils.emitLoadingEmitter(true);
    if (this.isProjectDataUpdated() || isSubmit) {
      if(printUrl !==''){
        this.projectLocation.printUrl=printUrl;
      }
      this.gisService.updateProjectLocation(this.projectLocation).subscribe(
        (response: any) => {
          this.combineAndUpdateGeometry(isSubmit);
          if (!skipUpdate) {
            this.combineAndUpdateWorkAreaGeometry();
          }
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.handleError(error);
        }
      );
    } else {
      this.utils.emitLoadingEmitter(false);
      this.backToMain();
    }
  }


  private updateAnalystProject(isSubmit: boolean, skipUpdate: boolean = false) {
    this.utils.emitLoadingEmitter(true);
    if(isSubmit){
      this.projectLocation.polygonStatus = 'ANALYST_APPROVED';
    }else{
      this.projectLocation.polygonStatus = 'ANALYST_SCRATCH';
    }
    this.gisService.updateProjectLocation(this.projectLocation).subscribe(
      (response: any) => {
        this.updateAnalystPolygon(isSubmit);
        if (!skipUpdate) {
          this.combineAndUpdateWorkAreaGeometry();
        }
        this.utils.emitLoadingEmitter(false);
        this.backToMain();
      },
      (error: HttpErrorResponse) => {
        this.utils.emitLoadingEmitter(false);
        //turn back status to ANALYST_SCRATCH
        this.projectLocation.polygonStatus = 'ANALYST_SCRATCH';
        if (error.status === HttpStatusCode.Conflict) {
          this.decIdChangeConfirmMessages = error.error;
          this.open(this.decIdChangeConfirm);
        } else {
          this.handleError(error);
        }
      }
    );
  }

  private combineAndUpdateWorkAreaGeometry(): void {
    if (this.workAreaPolygonModified) {
      this.utils.emitLoadingEmitter(true);
      let graphic = this.gisMapEdit.combineWorkAreaGeometry();
      if ((graphic.geometry as Polygon)?.rings !==null && (graphic.geometry as Polygon)?.rings?.length > 0) {
        if(this.workAreaGraphic?.attributes ===undefined){
          let attributes = {
            WORK_AREA_ID: this.projectLocation.projectId,
            //CREATED_USER: localStorage.getItem('loggedUserName'),
          };
          graphic.attributes =attributes;
        }else{
           graphic.attributes =this.workAreaGraphic?.attributes;
        }
        if (this.isValidate) {
          graphic.attributes.STATUS = 1;
        } else {
          graphic.attributes.STATUS = 0;
        }
        this.gisService
          .saveWorkAreaPolygon(this.getGraphicAsJsonString(graphic), 'U')
          .subscribe(
            (data: any) => {
              this.utils.emitLoadingEmitter(false);
            },
            (error: any) => {
              this.utils.emitLoadingEmitter(false);
              this.handleError(error);
            }
          );
      }else{
        this.utils.emitLoadingEmitter(false);
      }
    }
  }

  private deleteWorkAreaGeometry(isSubmit: boolean, isAnalystProcess: boolean = false,printUrl:string=''): void {
    if (this.workAreaPolygonModified && this.projectLocation.workAreaId !== undefined && this.projectLocation.workAreaId !== null && this.projectLocation.workAreaId?.trim() !== '') {
      this.utils.emitLoadingEmitter(true);
      this.gisService
        .deleteWorkAreaPolygon(this.projectLocation.workAreaId)
        .subscribe(
          (data: any) => {
            this.projectLocation.workAreaId = '';
            if (isAnalystProcess) {
              this.updateAnalystProject(isSubmit, true);
              this.utils.emitLoadingEmitter(false);
            }
            else {
              this.updateProject(isSubmit, true,printUrl);
              this.utils.emitLoadingEmitter(false);
            }
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.handleError(error);
          }
        );
    }
  }

  private combineAndCreateWorkAreaGeometry(isSubmit: boolean, isAnalystProcess: boolean = false,printUrl:string=''): void {
    if (this.workAreaPolygonModified) {
      this.utils.emitLoadingEmitter(true);
      let graphic = this.gisMapEdit.combineWorkAreaGeometry();
      let attributes = {
        WORK_AREA_ID: this.projectLocation.projectId,
       //CREATED_USER: localStorage.getItem('loggedUserName'),
      };
      graphic.attributes = attributes;
      if (this.isValidate) {
        graphic.attributes.STATUS = 1;
      } else {
        graphic.attributes.STATUS = 0;
      }
      this.gisService
        .saveWorkAreaPolygon(this.getGraphicAsJsonString(graphic), 'S')
        .subscribe(
          (data: any) => {
            this.projectLocation.workAreaId = data.addResults[0]?.objectId;
            if (isAnalystProcess) {
              this.updateAnalystProject(isSubmit, true);
              this.utils.emitLoadingEmitter(false);
            }
            else {
              this.updateProject(isSubmit, true,printUrl);
              this.utils.emitLoadingEmitter(false);
            }
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.handleError(error);
          }
        );
    }
  }

  private combineAndUpdateGeometry(isSubmit: boolean): void {
    this.utils.emitLoadingEmitter(true);
    let graphic = this.gisMapEdit.combineAllGeometry();
    graphic.attributes = this.projectGraphic.attributes;
    this.gisService
      .saveApplicantPolygon(this.getGraphicAsJsonString(graphic), 'U')
      .subscribe(
        async (data: any) => {
          if (isSubmit) {
            await this.copyPolygonToSubmittedLayer();
            this.utils.emitLoadingEmitter(false);
          } else {
            this.utils.emitLoadingEmitter(false);
            console.log('There are no updates to project Details');
            this.backToMain();
          }
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.handleError(error);
        }
      );
  }

  private copyPolygonToSubmittedLayer(): void {
    this.utils.emitLoadingEmitter(true);
    let submit_attributes = {
      APPL_SUB_ID: localStorage.getItem('projectId'),
      //CREATED_USER: localStorage.getItem('loggedUserName'),
    };
    this.gisService
      .searchApplicantPolygon(this.projectLocation.polygonId)
      .toPromise()
      .then(
        (polygon: any) => {
          this.getProjectGraphic(polygon);
          this.projectGraphic = this.gisMapEdit.combineAllGeometry();
          this.projectGraphic.attributes = submit_attributes;
          this.gisService
            .saveSubmittedPolygon(
              this.getGraphicAsJsonString(this.projectGraphic),
              'S'
            )
            .subscribe(
              (data: any) => {
                if (data.addResults[0]) {
                  let objectId = data.addResults[0]?.objectId;
                  this.projectLocation.polygonId = objectId;
                  this.projectLocation.polygonStatus = 'APPLICANT_SUBMITTED';
                  this.gisService
                    .updateProjectLocation(this.projectLocation)
                    .subscribe(
                      (response: any) => {
                        this.utils.emitLoadingEmitter(false);
                        this.backToMain();
                      },
                      (error: any) => {
                        this.gisService
                          .deleteSubmittedPolygon(objectId)
                          .subscribe((data: any) => {
                            this.utils.emitLoadingEmitter(false);
                          });
                        this.utils.emitLoadingEmitter(false);
                        //delete the Polygon if save Project fail with Object id
                        this.handleError(error);
                      }
                    );
                } else {
                  this.utils.emitLoadingEmitter(false);
                  this.handleError(this.errorMsgObj?.UNABLE_TO_PROCESS_NOW);
                }
              },
              (error: any) => {
                this.utils.emitLoadingEmitter(false);
                this.handleError(error);
              }
            );
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.handleError(error);
        }
      );
  }

  public async saveProjectLocation(isSubmit: boolean = false,printUrl:string=''): Promise<void> {
    this.utils.emitLoadingEmitter(true);
    this.isSavedSubmitted = true;
    if (!this.projectDetailsForm.invalid) {
      this.projectLocation.mode = 0;
      if (this.gisMapEdit.isWorkAreaPolygonExist()) {
        if (!this.isWorkAreaExistInProject()) {
          this.combineAndCreateWorkAreaGeometry(isSubmit,false,printUrl);
        } else {
          //updated project in DB if there are any changes in UI
          this.updateProject(isSubmit,false,printUrl);
        }
      } else {
        if (this.isWorkAreaExistInProject()) {
          this.deleteWorkAreaGeometry(isSubmit,false,printUrl);
        } else {
          console.log("updateProject")
          //updated project in DB if there are any changes in UI
          this.updateProject(isSubmit,false,printUrl);
        }
      }
    } else {
      this.utils.emitLoadingEmitter(false);
    }
  }

  private isWorkAreaExistInProject() {
    return (
      this.projectLocation.workAreaId !== undefined &&
      this.projectLocation.workAreaId !== null &&
      this.projectLocation.workAreaId !== ''
    );
  }

  private getGraphicAsJsonString(graphic: Graphic): string {
    let jsonString = '';
    if (graphic?.geometry) {
      jsonString = JSON.stringify(graphic);
    }
    return jsonString;
  }

  public async onMapInitialized(): Promise<void> { }

  public async onTaxMapUpdated(taxMaps: string): Promise<void> {
    if(taxMaps !==null && taxMaps?.length >2000){
    this.projectDetailsForm.controls.taxMap.setValue(taxMaps.substring(0,2000));
    }else{
      this.projectDetailsForm.controls.taxMap.setValue(taxMaps);
    }
  }
  public async onRegionsUpdated(regions: string): Promise<void> {
    this.projectDetailsForm.controls.region.setValue(regions);
  }
  public async onPrimaryRegionUpdated(region: string): Promise<void> {
    this.projectDetailsForm.controls.primaryRegion.setValue(region);
  }
  public async onCountyUpdated(counties: Set<CivilDivision>): Promise<void> {
    if (counties !== undefined) {
      let countyDetails = new Set<string>();
      let countyNames = new Set<string>();
      let countySiws = new Set<string>();
      if(counties.size>50){
        let municipalityDetails = new Set<string>();
        let municipalityNames = new Set<string>();
        let municipalitySiws = new Set<string>();
        this.primaryMunicipalities = new Set<CivilDivision>();
        let stateWideCivilDivision = new CivilDivision("NEW YORK", "0000", 0);
        municipalityNames.add(stateWideCivilDivision.name);
        municipalitySiws.add(stateWideCivilDivision.swis);
        municipalityDetails.add(stateWideCivilDivision.fullMunicipalityDetails);
        this.primaryMunicipalities.add(stateWideCivilDivision);
        this.projectDetailsForm.controls.municipality.setValue(
          [...municipalityDetails].join(',')
        );
        this.projectDetailsForm.controls.municipalitySwis.setValue(
          [...municipalitySiws].join(',')
        );
        this.projectDetailsForm.controls.municipalityName.setValue(
          [...municipalityNames].join(', ')
        );
      }
      counties.forEach((civilDivision: CivilDivision) => {
        countyNames.add(civilDivision.name);
        countySiws.add(civilDivision.swis);
        countyDetails.add(civilDivision.fullDetails);
      });
      this.projectDetailsForm.controls.countyName.setValue(
        [...countyNames].join(', ')
      );
      this.projectDetailsForm.controls.countySwis.setValue(
        [...countySiws].join(',')
      );
      this.projectDetailsForm.controls.county.setValue(
        [...countyDetails].join(',')
      );
    } else {
      this.projectDetailsForm.controls.countyName.setValue('');
      this.projectDetailsForm.controls.countySwis.setValue('');
      this.projectDetailsForm.controls.county.setValue('');
    }
  }

  public async onMunicipalityUpdated(
    municipalities: Set<CivilDivision>
  ): Promise<void> {
    if (municipalities !== undefined) {
      let municipalityDetails = new Set<string>();
      let municipalityNames = new Set<string>();
      let municipalitySiws = new Set<string>();
      this.primaryMunicipalities = new Set<CivilDivision>();
      if(this.projectLocation?.facility?.decIdFormatted !==null && this.projectLocation?.facility?.decIdFormatted?.startsWith("0-0000")){
        let stateWideCivilDivision=new CivilDivision("NEW YORK","0000",0);
        municipalityNames.add(stateWideCivilDivision.name);
        municipalitySiws.add(stateWideCivilDivision.swis);
        municipalityDetails.add(stateWideCivilDivision.fullMunicipalityDetails);
        this.primaryMunicipalities.add(stateWideCivilDivision);
        this.projectDetailsForm.controls.municipality.setValue(
          [...municipalityDetails].join(',')
        );
        this.projectDetailsForm.controls.municipalitySwis.setValue(
          [...municipalitySiws].join(',')
        );
        this.projectDetailsForm.controls.municipalityName.setValue(
          [...municipalityNames].join(', ')
        );
      }else{
        /*if (municipalities.size >= 50) {
          let stateWideCivilDivision = new CivilDivision("NEW YORK", "0000", 0);
          municipalityNames.add(stateWideCivilDivision.name);
          municipalitySiws.add(stateWideCivilDivision.swis);
          municipalityDetails.add(stateWideCivilDivision.fullMunicipalityDetails);
        } else {*/
          municipalities.forEach((civilDivision: CivilDivision) => {
            municipalityNames.add(civilDivision.name);
            if (civilDivision.swis !== undefined && civilDivision.swis !== null) {
              municipalitySiws.add(civilDivision.swis);
            } else {
              //swis code is null for NYC
              let NewYorkSWISCode = '620000'
              civilDivision.swis = NewYorkSWISCode;
              municipalitySiws.add(NewYorkSWISCode);
            }
            municipalityDetails.add(civilDivision.fullMunicipalityDetails);
            this.primaryMunicipalities.add(civilDivision);
          });
        //}
        if(this.projectDetailsForm.controls.countyName.value?.split(',').length >50){
          let stateWideCivilDivision = new CivilDivision("NEW YORK", "0000", 0);
          municipalityNames.add(stateWideCivilDivision.name);
          municipalitySiws.add(stateWideCivilDivision.swis);
          municipalityDetails.add(stateWideCivilDivision.fullMunicipalityDetails);
        }
        this.projectDetailsForm.controls.municipality.setValue(
          [...municipalityDetails].join(',')
        );
        this.projectDetailsForm.controls.municipalitySwis.setValue(
          [...municipalitySiws].join(',')
        );
        this.projectDetailsForm.controls.municipalityName.setValue(
          [...municipalityNames].join(', ')
        );
      }

    } else {
      this.projectDetailsForm.controls.municipality.setValue('');
      this.projectDetailsForm.controls.municipalitySwis.setValue('');
      this.projectDetailsForm.controls.municipalityName.setValue('');
    }
  }

  public async onPrimaryMunicipalityUpdate(
    civilDivisionData: CivilDivision
  ): Promise<void> {
    this.projectDetailsForm.controls.primaryMunicipality.setValue(civilDivisionData.fullMunicipalityDetails);
    this.projectDetailsForm.controls.primaryMunicipalityView.setValue(civilDivisionData.fullDescription);
  }

  public async onGisLocationUpdate(
    gisLocationData: GISLocation
  ): Promise<void> {
    if (gisLocationData !== undefined) {
      this.projectDetailsForm.controls.latitude.setValue(gisLocationData.latitude);
      this.projectDetailsForm.controls.longitude.setValue(gisLocationData.longitude);
      this.projectDetailsForm.controls.nytmx.setValue(gisLocationData?.nytmx);
      this.projectDetailsForm.controls.nytmy.setValue(gisLocationData?.nytmy);
    }
  }

  public async onWorkAreaPolygonModified(modified: boolean): Promise<void> {
    this.workAreaPolygonModified = true;
  }

  private copyProjectDataToDetailsForm(data: any): void {
    this.projectLocation = data as ProjectLocation;
    //have copy of project if the project location got updated
    this.originalProject = JSON.parse(JSON.stringify(this.projectLocation));
    this.projectDetailsForm.controls.reason.setValue(data.reason);
    this.projectDetailsForm.controls.boundaryChangeReason.setValue(data.boundaryChangeReason);
    if (data.facility !== undefined) {

      this.projectDetailsForm.controls.decId.setValue(data.facility.facilityId);
      this.projectDetailsForm.controls.projectName.setValue(
        data.facility.facilityName
      );
      this.projectDetailsForm.controls.address1.setValue(
        data.facility.address?.street1
      );
      this.projectDetailsForm.controls.address2.setValue(
        data.facility.address?.street2
      );
      this.projectDetailsForm.controls.city.setValue(
        (data.facility.address?.city).trim().substring(0, 65)
      );
      this.projectDetailsForm.controls.state.setValue(
        data.facility.address?.state
      );
      this.projectDetailsForm.controls.zip.setValue(data.facility.address?.zip);
    }
    if (
      data.regions !== undefined &&
      data.regions !== null &&
      data.regions !== ''
    ) {
      let regionList = data.regions.split(',');
      let regions = new Set<string>();
      regionList.forEach((region: string) => {
        regions.add(region);
      });
      this.projectDetailsForm.controls.region.setValue([...regions].join(', '));
    }

    this.projectDetailsForm.controls.county.setValue(data.counties);
    if (
      data.counties !== undefined &&
      data.counties !== null &&
      data.counties !== ''
    ) {
      let countyList = data.counties.split(',');
      let countyNames = new Set<string>();
      countyList.forEach((county: string) => {
        countyNames.add(county.split('-')[0]?.trim());
      });
      this.projectDetailsForm.controls.countyName.setValue(
        [...countyNames].join(', ')
      );
    }

    this.projectDetailsForm.controls.municipality.setValue(data.municipalities);

    if (
      data.municipalities !== undefined &&
      data.municipalities !== null &&
      data.municipalities !== ''
    ) {
      let municipalityList = data.municipalities.split(',');
      let municipalityNames = new Set<string>();
      this.primaryMunicipalities = new Set<CivilDivision>();
      municipalityList.forEach((municipality: string) => {
        this.primaryMunicipalities.add(this.buildCivilDivision(municipality));
        municipalityNames.add(municipality.split('-')[0]?.trim());
      });
      this.projectDetailsForm.controls.municipalityName.setValue(
        [...municipalityNames].join(', ')
      );
    }
    this.projectDetailsForm.controls.primaryMunicipality.setValue(data.primaryMunicipality);
    this.projectDetailsForm.controls.primaryMunicipalityView.setValue(this.buildCivilDivision(data.primaryMunicipality).fullDescription);
    this.projectDetailsForm.controls.taxMap.setValue(data.taxmaps);
    this.projectDetailsForm.controls.latitude.setValue(data.lat);
    this.projectDetailsForm.controls.longitude.setValue(data.long);
    this.projectDetailsForm.controls.nytmx.setValue(data.nytmx);
    this.projectDetailsForm.controls.nytmy.setValue(data.nytmy);
    let inquiries:string[]=[];
    if(this.projectLocation.inquiries!==undefined && this.projectLocation.inquiries !==null && this.projectLocation.inquiries.length >0){
      inquiries=this.projectLocation.inquiries;
    }
    if(inquiries.length==1){
      this.projectDetailsForm.controls.inquiryId1.setValue(inquiries[0]);
    }
    if(inquiries.length==2){
      this.projectDetailsForm.controls.inquiryId1.setValue(inquiries[0]);
      this.projectDetailsForm.controls.inquiryId2.setValue(inquiries[1]);
    }
    if(inquiries.length==3){
      this.projectDetailsForm.controls.inquiryId1.setValue(inquiries[0]);
      this.projectDetailsForm.controls.inquiryId2.setValue(inquiries[1]);
      this.projectDetailsForm.controls.inquiryId3.setValue(inquiries[2]);
    }
    if(inquiries.length==4){
      this.projectDetailsForm.controls.inquiryId1.setValue(inquiries[0]);
      this.projectDetailsForm.controls.inquiryId2.setValue(inquiries[1]);
      this.projectDetailsForm.controls.inquiryId3.setValue(inquiries[2]);
      this.projectDetailsForm.controls.inquiryId4.setValue(inquiries[4]);
    }
    if(inquiries.length==5){
      this.projectDetailsForm.controls.inquiryId1.setValue(inquiries[0]);
      this.projectDetailsForm.controls.inquiryId2.setValue(inquiries[1]);
      this.projectDetailsForm.controls.inquiryId3.setValue(inquiries[2]);
      this.projectDetailsForm.controls.inquiryId4.setValue(inquiries[3]);
      this.projectDetailsForm.controls.inquiryId5.setValue(inquiries[4]);
    }
    if (data.validatedInd !== undefined && data.validatedInd === 'Y') {
      this.projectLocationValidated = true;
    } else {
      this.projectLocationValidated = false;
    }
  }

  private buildCivilDivision(fullDescription: string): CivilDivision {
    if (fullDescription !== undefined && fullDescription !== null) {
      if (fullDescription.split('-')[2]?.trim() !== undefined) {
        return new CivilDivision(fullDescription.split('-')[0]?.trim(), fullDescription.split('-')[1]?.trim(), Number(fullDescription.split('-')[2]?.trim()));
      } else {
        return new CivilDivision(fullDescription.split('-')[0]?.trim(), fullDescription.split('-')[1]?.trim());
      }
    } else {
      return new CivilDivision("unknown place", "0000");
    }
  }

  private isProjectDataUpdated(): boolean {
    let nameUpdated = this.checkProjectNameUpdated();
    let addressUpdated = this.checkProjectAddressUpdated();
    let gisUpdated = this.checkGISDataUpdated();
    let reasonUpdated = this.checkReasonsUpdated();
    let validateUpdated = this.checkValidationUpdated();
    let inquiriesUpdated= this.checkInquiriesUpdated();
    if (
      nameUpdated ||
      addressUpdated ||
      gisUpdated ||
      reasonUpdated ||
      this.workAreaPolygonModified ||
      validateUpdated ||
      inquiriesUpdated
    ) {
      return true;
    } else {
      return false;
    }
  }

  private checkInquiriesUpdated():boolean{
    let isInquiriesUpdated = false;
    let inquiries:string[]=[];
    if(this.projectDetailsForm.controls.inquiryId1?.value !==undefined && this.projectDetailsForm.controls.inquiryId1?.value !== null && this.projectDetailsForm.controls.inquiryId1?.value.trim() !== '' ){
      inquiries.push(this.projectDetailsForm.controls.inquiryId1.value.replace(/^0+/, ""));
    }
    if(this.projectDetailsForm.controls.inquiryId2?.value !==undefined && this.projectDetailsForm.controls.inquiryId2?.value !== null && this.projectDetailsForm.controls.inquiryId2?.value.trim() !== '' ){
      inquiries.push(this.projectDetailsForm.controls.inquiryId2.value.replace(/^0+/, ""));
    }
    if(this.projectDetailsForm.controls.inquiryId3?.value !==undefined && this.projectDetailsForm.controls.inquiryId3?.value !== null && this.projectDetailsForm.controls.inquiryId3?.value.trim() !== '' ){
      inquiries.push(this.projectDetailsForm.controls.inquiryId3.value.replace(/^0+/, ""));
    }
    if(this.projectDetailsForm.controls.inquiryId4?.value !==undefined && this.projectDetailsForm.controls.inquiryId4?.value !== null && this.projectDetailsForm.controls.inquiryId4?.value.trim() !== '' ){
      inquiries.push(this.projectDetailsForm.controls.inquiryId4.value.replace(/^0+/, ""));
    }
    if(this.projectDetailsForm.controls.inquiryId5?.value !==undefined && this.projectDetailsForm.controls.inquiryId5?.value !== null && this.projectDetailsForm.controls.inquiryId5?.value.trim() !== '' ){
      inquiries.push(this.projectDetailsForm.controls.inquiryId5.value.replace(/^0+/, ""));
    }
    if (this.originalProject.inquiries?.join(',') !== inquiries.join(',')
    ) {
      this.projectLocation.inquiries =inquiries;
        isInquiriesUpdated = true;
    }
    return isInquiriesUpdated;
  }

  private checkValidationUpdated(): boolean {
    if (this.projectLocationValidated) {
      if (
        this.projectLocation.validatedInd === 'N' ||
        this.projectLocation.validatedInd === null || this.projectLocation.validatedInd === undefined
      ) {
        this.projectLocation.validatedInd = 'Y';
        return true;
      } else {
        return false;
      }
    } else {
      if (this.projectLocation.validatedInd === 'Y') {
        this.projectLocation.validatedInd = 'N';
        return true;
      } else {
        return false;
      }
    }
  }

  private checkReasonsUpdated(): boolean {
    let isReasonsUpdated = false;
    if (
      this.originalProject.reason !==
      this.projectDetailsForm.controls.reason.value
    ) {
      this.projectLocation.reason =
        this.projectDetailsForm.controls.reason.value;
      isReasonsUpdated = true;
    }
    if (
      this.originalProject.boundaryChangeReason !==
      this.projectDetailsForm.controls.boundaryChangeReason.value
    ) {
      this.projectLocation.boundaryChangeReason =
        this.projectDetailsForm.controls.boundaryChangeReason.value;
      isReasonsUpdated = true;
    }
    return isReasonsUpdated;
  }

  private checkProjectNameUpdated(): boolean {
    if (
      this.originalProject.facility.facilityName !==
      this.projectDetailsForm.controls.projectName.value
    ) {
      this.projectLocation.facility.facilityName =
        this.projectDetailsForm.controls.projectName.value;
      return true;
    } else {
      return false;
    }
  }

  private checkProjectAddressUpdated(): boolean {
    let isAddressUpdated = false;
    if (
      this.originalProject.facility.address.street1 !==
      this.projectDetailsForm.controls.address1.value
    ) {
      this.projectLocation.facility.address.street1 =
        this.projectDetailsForm.controls.address1.value;
      isAddressUpdated = true;
    }
    if (
      this.originalProject.facility.address.street2 !==
      this.projectDetailsForm.controls.address2.value
    ) {
      this.projectLocation.facility.address.street2 =
        this.projectDetailsForm.controls.address2.value;
      isAddressUpdated = true;
    }
    if (
      this.originalProject.facility.address.city !==
      this.projectDetailsForm.controls.city.value
    ) {
      this.projectLocation.facility.address.city =
        this.projectDetailsForm.controls.city.value;
      isAddressUpdated = true;
    }
    if (
      this.originalProject.facility.address.zip !==
      this.projectDetailsForm.controls.zip.value
    ) {
      this.projectLocation.facility.address.zip =
        this.projectDetailsForm.controls.zip.value;
      isAddressUpdated = true;
    }
    return isAddressUpdated;
  }

  private checkGISDataUpdated(): boolean {
    let isGISUpdated = false;
    if (
      this.originalProject.counties !==
      this.projectDetailsForm.controls.county.value
    ) {
      this.projectLocation.counties =
        this.projectDetailsForm.controls.county.value;
      this.projectLocation.countySwis =
        this.projectDetailsForm.controls.countySwis.value;
      isGISUpdated = true;
    }else{
      if(this.projectLocation.municipalities !== this.projectDetailsForm.controls.county.value){
        this.projectLocation.municipalities=this.projectDetailsForm.controls.county.value;
        isGISUpdated = true;
      }
    }
    if (
      this.originalProject.taxmaps !==
      this.projectDetailsForm.controls.taxMap.value
    ) {
      this.projectLocation.taxmaps =
        this.projectDetailsForm.controls.taxMap.value;
      isGISUpdated = true;
    }else{
      if(this.projectLocation.taxmaps !==  this.projectDetailsForm.controls.taxMap.value){
        this.projectLocation.taxmaps =  this.projectDetailsForm.controls.taxMap.value;
        isGISUpdated = true;
      }
    }
    if (
      this.originalProject.regions !==
      this.projectDetailsForm.controls.region.value
    ) {
      this.projectLocation.regions =
        this.projectDetailsForm.controls.region.value;
      isGISUpdated = true;
    }else{
      if(this.projectLocation.regions !==  this.projectDetailsForm.controls.region.value){
        this.projectLocation.regions = this.projectDetailsForm.controls.region.value;
        isGISUpdated = true;
      }
    }
    if (
      this.originalProject.municipalities !==
      this.projectDetailsForm.controls.municipality.value
    ) {
      this.projectLocation.municipalities =
        this.projectDetailsForm.controls.municipality.value;
      this.projectLocation.municipalitySwis =
        this.projectDetailsForm.controls.municipalitySwis.value;
      isGISUpdated = true;
    }else{
      if(this.projectLocation.municipalities !== this.projectDetailsForm.controls.municipality.value){
        this.projectLocation.municipalities =  this.projectDetailsForm.controls.municipality.value;
        this.projectLocation.municipalitySwis = this.projectDetailsForm.controls.municipalitySwis.value;
        isGISUpdated = true;
      }
    }
    if (
      this.originalProject.primaryMunicipality !==
      this.projectDetailsForm.controls.primaryMunicipality.value
    ) {
      this.projectLocation.primaryMunicipality =
        this.projectDetailsForm.controls.primaryMunicipality.value;
      isGISUpdated = true;
    }else{
      if( this.projectLocation.primaryMunicipality !==  this.projectDetailsForm.controls.primaryMunicipality.value){
        this.projectLocation.primaryMunicipality =
        this.projectDetailsForm.controls.primaryMunicipality.value;
        isGISUpdated = true;
      }
    }
    if (
      this.originalProject.lat !==
      this.projectDetailsForm.controls.latitude.value
    ) {
      this.projectLocation.lat =
        this.projectDetailsForm.controls.latitude.value;
      isGISUpdated = true;
    }else{
      if( this.projectLocation.lat  !== this.projectDetailsForm.controls.latitude.value){
        this.projectLocation.lat =
        this.projectDetailsForm.controls.latitude.value;
        isGISUpdated = true;
      }
    }
    if (
      this.originalProject.long !==
      this.projectDetailsForm.controls.longitude.value
    ) {
      this.projectLocation.long =
        this.projectDetailsForm.controls.longitude.value;
      isGISUpdated = true;
    }else{
      if(this.projectLocation.long !== this.projectDetailsForm.controls.longitude.value){
        this.projectLocation.long =this.projectDetailsForm.controls.longitude.value;
        isGISUpdated = true;
      }
    }
    if (
      this.originalProject.nytmx !==
      this.projectDetailsForm.controls.nytmx.value
    ) {
      this.projectLocation.nytmx =
        this.projectDetailsForm.controls.nytmx.value;
      isGISUpdated = true;
    }else{
      if(this.projectLocation.nytmx !== this.projectDetailsForm.controls.nytmx.value){
        this.projectLocation.nytmx = this.projectDetailsForm.controls.nytmx.value;
        isGISUpdated = true;
      }
    }
    if (
      this.originalProject.nytmy !==
      this.projectDetailsForm.controls.nytmy.value
    ) {
      this.projectLocation.nytmy =
        this.projectDetailsForm.controls.nytmy.value;
      isGISUpdated = true;
    }else{
      if( this.projectLocation.nytmy !== this.projectDetailsForm.controls.nytmy.value){
        this.projectLocation.nytmy = this.projectDetailsForm.controls.nytmy.value;
        isGISUpdated = true;
      }
    }
    return isGISUpdated;
  }

  public selectionChange(event: any): void {
    this.selectedIndex = event.selectedIndex;
  }

  private updateAnalystPolygon(isSubmit: boolean = false) {
    let graphic = this.gisMapEdit.combineAllGeometry();
    graphic.attributes = this.projectGraphic.attributes;
    graphic.attributes.LAST_EDITED_USER =
      localStorage.getItem('loggedUserName');
    graphic.attributes.EFINDSTATUS = isSubmit ? 1 : 0;
    this.gisService
      .saveAnalystPolygon(this.getGraphicAsJsonString(graphic), 'U')
      .subscribe(
        (data: any) => {
          this.backToMain();
        },
        (error: any) => {
          this.handleError(error);
        }
      );
  }

  private handleError(error: any) {
    this.serviceErrorMessage = this.errorService.getServerMessage(error);
    this.serviceError = true;
    throw error;
  }



  public async saveAnalystProjectOnDECIdConflict(
    isSubmit: boolean = false
  ): Promise<void> {
    this.saveAnalystProjectLocation(isSubmit, 1,this.projectLocation.printUrl);
  }


  //save analyst project
  public async saveAnalystProjectLocation(
    isSubmit: boolean = false, ignoreDecIdMismatch: number = 0,printUrl:string=''
  ): Promise<void> {
    this.isSavedSubmitted = true;
    this.projectLocation.ignoreDecIdMismatch = ignoreDecIdMismatch;
    if(printUrl!==''){
      this.projectLocation.printUrl=printUrl;
    }
    if (!this.projectDetailsForm.invalid) {
      //if it is analyst then it should be validate mode
      if (this.mode === 'validate') {
        this.projectLocation.mode = 1;
        this.projectLocation.hasSameGeometry = this.gisMapEdit.isProjectAndFacilityHasSamePolygon();
        let inquiries:string[]=[];
        if(this.projectDetailsForm.controls.inquiryId1?.value !==undefined && this.projectDetailsForm.controls.inquiryId1?.value !== null && this.projectDetailsForm.controls.inquiryId1?.value.trim() !== '' ){
          inquiries.push(this.projectDetailsForm.controls.inquiryId1.value.replace(/^0+/, ""));
        }
        if(this.projectDetailsForm.controls.inquiryId2?.value !==undefined && this.projectDetailsForm.controls.inquiryId2?.value !== null && this.projectDetailsForm.controls.inquiryId2?.value.trim() !== '' ){
          inquiries.push(this.projectDetailsForm.controls.inquiryId2.value.replace(/^0+/, ""));
        }
        if(this.projectDetailsForm.controls.inquiryId3?.value !==undefined && this.projectDetailsForm.controls.inquiryId3?.value !== null && this.projectDetailsForm.controls.inquiryId3?.value.trim() !== '' ){
          inquiries.push(this.projectDetailsForm.controls.inquiryId3.value.replace(/^0+/, ""));
        }
        if(this.projectDetailsForm.controls.inquiryId4?.value !==undefined && this.projectDetailsForm.controls.inquiryId4?.value !== null && this.projectDetailsForm.controls.inquiryId4?.value.trim() !== '' ){
          inquiries.push(this.projectDetailsForm.controls.inquiryId4.value.replace(/^0+/, ""));
        }
        if(this.projectDetailsForm.controls.inquiryId5?.value !==undefined && this.projectDetailsForm.controls.inquiryId5?.value !== null && this.projectDetailsForm.controls.inquiryId5?.value.trim() !== '' ){
          inquiries.push(this.projectDetailsForm.controls.inquiryId5.value.replace(/^0+/, ""));
        }
        this.projectLocation.inquiries=inquiries;
      }
      if (this.isProjectDataUpdated()) {
        if (this.gisMapEdit.isWorkAreaPolygonExist()) {
          if (!this.isWorkAreaExistInProject()) {
            this.combineAndCreateWorkAreaGeometry(isSubmit, true);
          } else {
            //updated project in DB if there are any changes in UI
            if(ignoreDecIdMismatch>0){
              this.updateAnalystProject(isSubmit,true);
            }else{
              this.updateAnalystProject(isSubmit);
            }
          }
        } else {
          if (this.isWorkAreaExistInProject()) {
            this.deleteWorkAreaGeometry(isSubmit, true);
          } else {
            //updated project in DB if there are any changes in UI
            this.updateAnalystProject(isSubmit);
          }
        }
      } else {
        console.log('There are no updates to project Details');
        this.utils.emitLoadingEmitter(false);
        this.backToMain();
      }
    }
  }

  public async ondecIdSearchSubmit(): Promise<void> {
    let decId = this.decIdSearchForm.controls.decId.value;
    this.decIdSearched = false;
    if (
      decId !== undefined &&
      decId !== '' &&
      decId !== null &&
      decId?.length > 0
    ) {
      this.getDECPolygonForId(this.decIdSearchForm.controls.decId.value);
    }
  }

  private async getDECPolygonForId(decId: string): Promise<void> {
    this.selectedFacility = new ApprovedFacility();
    this.utils.emitLoadingEmitter(true);
    this.gisService.getDECPolygonByDecId(decId).subscribe(
      async (data: any) => {
        this.utils.emitLoadingEmitter(false);
        if (data.features?.length > 0) {
          let latitude = (data.features[0] as Polygon).centroid?.y;
          let longitude = (data.features[0] as Polygon).centroid?.x;
          let taxMapNumber = await this.gisService.findTaxParcelAt(
            longitude,
            latitude
          );
          this.selectedFacility = this.buildApprovedFacility(
            data.features[0],
            data.spatialReference.wkid,
            latitude,
            longitude
          );
          this.selectedFacility.taxMapNumber = taxMapNumber;
          this.decIdSearched=true;
        }else{
          this.decIdSearched=true;
        }
      },
      (error: any) => {
        this.utils.emitLoadingEmitter(false);
        this.decIdSearched = true;
        this.handleError(error);
      }
    );
  }

  private buildApprovedFacility(
    facility: Graphic,
    wkid: number,
    latitude: number,
    longitude: number
  ): ApprovedFacility {
    let approvedFacility = new ApprovedFacility();
    approvedFacility.SITE_ID = facility.attributes.SITE_ID;
    approvedFacility.SITE_TYPE = this.titleCasePipe.transform(
      facility.attributes.SITE_TYPE
    );
    approvedFacility.SITE_NAME = this.titleCasePipe.transform(
      facility.attributes.SITE_NAME
    );
    approvedFacility.PRIMARY_ID = facility.attributes.PRIMARY_ID;
    approvedFacility.PRIMARY_SWIS = facility.attributes.PRIMARY_SWIS;
    approvedFacility.LOCATION_DIRECTIONS_1 = this.titleCasePipe.transform(
      facility.attributes.LOCATION_DIRECTIONS_1
    );
    approvedFacility.LOCATION_DIRECTIONS_2 = this.titleCasePipe.transform(
      facility.attributes.LOCATION_DIRECTIONS_2
    );
    approvedFacility.CITY = this.titleCasePipe.transform(
      facility.attributes.CITY
    );
    approvedFacility.STATE = facility.attributes.STATE;
    approvedFacility.ZIP = facility.attributes.ZIP;
    approvedFacility.MUNICIPALITIES = this.titleCasePipe.transform(
      facility.attributes.MUNICIPALITIES
    );
    approvedFacility.COUNTIES = this.titleCasePipe.transform(
      facility.attributes.COUNTIES
    );
    approvedFacility.REGIONS = facility.attributes.REGIONS;
    approvedFacility.wkid = wkid;
    approvedFacility.geometry = facility;
    approvedFacility.latitude = latitude;
    approvedFacility.longitude = longitude;
    if(facility.attributes.VALIDATED_LOCATION !==1){
      approvedFacility.isValidLocation =0;
    }else{
      approvedFacility.isValidLocation = facility.attributes.VALIDATED_LOCATION;
    }
    return approvedFacility;
  }

  public copyFacility(): void {
    if (
      this.selectedFacility?.PRIMARY_ID !== undefined &&
      this.selectedFacility?.PRIMARY_ID !== ''
    ) {
      this.eFindDECID = this.selectedFacility?.PRIMARY_ID;
      this.projectLocation.facility.decIdFormatted =
        this.selectedFacility?.PRIMARY_ID;
      this.projectLocation.facility.decId = Number(
        this.selectedFacility?.PRIMARY_ID.replace('-', '')
      );
      this.projectLocation.facility.facilityId = Number(
        this.selectedFacility?.PRIMARY_ID.replace('-', '')
      );
      this.projectLocationHistory = {
        hfacilityName: this.selectedFacility?.SITE_NAME,
        hstreet1: this.selectedFacility?.LOCATION_DIRECTIONS_1,
        hstreet2: this.selectedFacility?.LOCATION_DIRECTIONS_2,
        hcity: this.selectedFacility?.CITY,
        hzip: this.selectedFacility?.ZIP,
      };
      if (this.selectedFacility?.isValidLocation == 1) {
        this.facilityPolygonExist = true;
        this.gisMapEdit.addToggleFacilityButton(this.eFindDECID);
      }
    }
    this.resetFlags();
  }

  public resetFlags(): void {
    this.decIdSearched = false;
    this.decIdSearchForm.controls.decId.setValue('');
    this.selectedFacility = new ApprovedFacility();
  }

  get customTextElements() {
    // If there are multiple municipalities, change it to 'NEW YORK'.
    let muniNames:string = this.projectDetailsForm.controls.municipalityName?.value;
    if(muniNames?.split(', ').length > 1){
      muniNames = 'NEW YORK';
    }
    return [
      { CountyName: this.projectDetailsForm.controls.countyName?.value },
      { MuniName: muniNames },
      { DECID: this.eFindDECID },
      { TaxParcelID: this.projectDetailsForm.controls.taxMap?.value },
      {
        Address:
          this.projectDetailsForm.controls.address1?.value +
          ' ' +
          this.projectDetailsForm.controls.city?.value,
      },
      { FacilityName: this.projectDetailsForm.controls.projectName?.value },
      { ProjectID: localStorage.getItem('projectId') },
    ];
  }
}

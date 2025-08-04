import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, UntypedFormGroup, Validators } from '@angular/forms';
import { GisService } from '../../../../@shared/services/gisService';
import { Utils } from '../../../../@shared/services/utils';
import { TitleCasePipe } from '@angular/common';
import { GisMapSiComponent } from '../gis-map-si/gis-map-si.component';
import { MatStepper } from '@angular/material/stepper';
import Graphic from '@arcgis/core/Graphic';
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { CommonService } from 'src/app/@shared/services/commonService';
import { AuthService } from 'src/app/core/auth/auth.service';
import { SIProject } from 'src/app/@store/models/siProject';
import { Router } from '@angular/router';
import Polygon from '@arcgis/core/geometry/Polygon';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { TaxMap } from 'src/app/@store/models/TaxMap';
import { ApplicantInfoServiceService } from 'src/app/@shared/services/applicant-info-service.service';
import { InquiryService } from '../../../../@shared/services/inquiryService';
import { UserRole } from 'src/app/@shared/constants/UserRole';

export interface DropDownType {
  id: string;
  value: string;
}
// Search Type Interface
export interface SIType {
  activeInd: string;
  categoryAvailTo: string;
  displayOrder:number;
  spatialInqCategoryCode:string;
  spatialInqCategoryDesc:string;
  spatialInqCategoryId:string;
}

export class Address {
  street!: string;
  city!: string;
  state!: string;
  zip!: string;
}
export class CodedAddress {
  address!: string;
  x!: string;
  y!: string;
}

@Component({
  selector: 'app-geographic-inquiry',
  templateUrl: './geographic-inquiry.component.html',
  styleUrls: ['./geographic-inquiry.component.scss'],
  providers: [
    TitleCasePipe,
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { displayDefaultIndicatorType: false },
    },
  ],
})
export class GeographicInquiryComponent implements OnInit {
  userRoles: any[] = [];
  errorMsgObj: any = {};
  siSearchForm!: FormGroup;
  inquiryReasonDetails!: FormGroup;
  siDocumentUploadForm!: FormGroup;
  isAddressSearched = false;
  isTaxMapSearched = false;
  isTaxMapSearchedSuccessful = false;
  isAddressSearchedSuccessful = false;
  isInquiryLoad = false;
  serviceError: boolean = false;
  serviceErrorMessage: string = '';
  reasonsList: Array<any> = [];
  reasonsListApplicant:Array<any> = [];
  reasonsListAnalyst:Array<any> = [];
  projectGraphic!: Graphic;
  //stepper control data
  selectedIndex: number = 0;
  siProject!: SIProject;
  selectedCounty!: string;
  selectedMunicipality!: string;
  municipalities!: Set<string>;
  depProjectManager: Array<any> = [];
  isReadonly: boolean = false;
  projectLocationServiceError: boolean = false;
  projectLocationServiceErrorMessage: string = '';
  isInquirySaveClicked: boolean = false;
  taxParcelLoading = false;
  //roles
  isAnalyst: boolean = false;
  isApplicant: boolean = false;
  counties!: string[];
  //address call timer
  timer: any = 0;
  NYC = ['Queens', 'New York', 'Kings', 'Richmond', 'Bronx'];

  @ViewChild(GisMapSiComponent, { static: true }) gisMapSi!: GisMapSiComponent;
  @ViewChild('stepper', { static: false }) stepper!: MatStepper;

  closeResult: string = '';
  modalReference: any;
  modalDocumentNameTitle: any;
  modalDocumentNameBody: any;
  @ViewChild('redirectInfo', { static: true }) modalRedirectInfo!: any;
  @ViewChild('dataLostWarning', { static: true }) modalDataLostWarning!: any;
  @ViewChild('submitConfirmModal', { static: true }) submitConfirmWarning!: any;
  @ViewChild('error') errorDiv!: ElementRef;
  @ViewChild('pendingPopup', { static: true })
  warningModal!: PendingChangesPopupComponent;

  //address errors
  showAddressServerError = false
  addressServerErrorMessage = '';
  addressNotFoundError = false;
  mapProperties = {
    basemap: 'streets',
    center: [-75.62757627797825, 42.98572311852962],
    zoom: 7,
  };

  si_reasons: any = {};

  search_types: DropDownType[] = [
    { id: 'address', value: 'Property Address' },
    { id: 'zoom', value: 'Zoom (or Pan) on Map' },
    { id: 'taxmap', value: 'Tax Parcel ID' },
  ];

  constructor(
    private _formBuilder: FormBuilder,
    private gisService: GisService,
    private inquiryService: InquiryService,
    private applicantInfoService: ApplicantInfoServiceService,
    private utils: Utils,
    private commonService: CommonService,
    private authService: AuthService,
    private router: Router,
    private errorService: ErrorService,
    private modalService: NgbModal
  ) { }

  ngOnInit(): any {
    this.getCurrentUserRole();
    this.getCountiesData();
    this.getAnalysts();
    this.initializeForms();
    this.initializeCategories();
    this.loadInquiryProject();
    this.changeListener();
    this.inquiryTypeChangeListener();
  }

  getCurrentUserRole() {
    let userInfo = this.authService.getUserInfo();
    this.commonService
      .getUsersRoleAndPermissions(userInfo.ppid)
      .then((response) => {
        this.userRoles = response.roles;
        if (this.userRoles !== undefined) {
          this.isAnalyst = this.userRoles.includes(UserRole.Admin) || this.userRoles.includes(UserRole.Analyst) || this.userRoles.includes(UserRole.Override_Admin) || this.userRoles.includes(UserRole.System_Analyst) || this.userRoles.includes(UserRole.System_Admin) || this.userRoles.includes(UserRole.Override_Analyst);
          this.isApplicant = !this.isAnalyst;
        }
      });
  }

  private initializeForms(): void {
    this.siSearchForm = this._formBuilder.group({
      searchBy: ['', [Validators.required]],
      street: [
        '',
        [
          Validators.required,
          Validators.minLength(5),
          Validators.maxLength(100),
        ],
      ],
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
      taxMapNumber: ['', [Validators.required]],
      county: ['', [Validators.required]],
      municipality: ['', [Validators.required]],
      reason: ['', [Validators.required]],
      region: ['', [Validators.required]],
      polygon: ['', [Validators.required]],
    });
    this.inquiryReasonDetails = this._formBuilder.group({
      borough: [''],
      block: [''],
      lot: [''],
      requestor_name: [''],
      street_address: [''],
      mailing_address_street1: [''],
      mailing_address_street2: [''],
      mailing_address_zip: [''],
      mailing_address_state: [''],
      mailing_address_city: [''],
      phone_number: [''],
      project_name: [''],
      project_description: [''],
      project_sponsor: [''],
      issues_questions: [''],
      lead_agency_name: [''],
      lead_agency_contact: [''],
      efc_contact: [''],
      plan_name: [''],
      plan_description: [''],
      extender_name: [''],
      dow_contact: [''],
      developer: [''],
      owner: [''],
      dep_project_manager: [''],
      psc_docket_num: [''],
      comments: [''],
      email: [''],
      geometry: [''],
      taxParcel: [''],
      county: [''],
      municipality: [''],
    });
    this.siDocumentUploadForm = this._formBuilder.group({});
  }

  reasonValue(): string {
    return this.si_reasons.filter((v: SIType) => v.spatialInqCategoryCode === this.siSearchForm.controls['reason']?.value)[0]?.spatialInqCategoryDesc;
  }

  private initializeCategories() {
    this.si_reasons = [];
    this.gisService.getSiCategories().subscribe((data: any) => {
        data?.PA.forEach((sitype:any)=>{
          let si_reason: SIType = {
            activeInd: sitype.activeInd,
            categoryAvailTo:  sitype.categoryAvailTo,
            displayOrder: sitype.displayOrder,
            spatialInqCategoryCode: sitype.spatialInqCategoryCode,
            spatialInqCategoryDesc: sitype.spatialInqCategoryDesc,
            spatialInqCategoryId: sitype.spatialInqCategoryId,
          }
          this.si_reasons.push(si_reason);
        });
        data?.A.forEach((sitype:any)=>{
          let si_reason: SIType = {
            activeInd: sitype.activeInd,
            categoryAvailTo:  sitype.categoryAvailTo,
            displayOrder: sitype.displayOrder,
            spatialInqCategoryCode: sitype.spatialInqCategoryCode,
            spatialInqCategoryDesc: sitype.spatialInqCategoryDesc,
            spatialInqCategoryId: sitype.spatialInqCategoryId,
          }
          this.si_reasons.push(si_reason);
        });
        this.reasonsList = this.getReasons('0', this.isAnalyst);
        this.filterReasonListByCategory('0');
    });
    this.getAllErrorMsgs();
  }
  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val) => {
      if (val) this.errorMsgObj = this.commonService.getErrorMsgsObj();
    });
  }

  private loadInquiryProject(): void {
    let inquiryId = localStorage.getItem('inquiryId');
    if (
      inquiryId !== undefined &&
      inquiryId !== null &&
      inquiryId !== '' &&
      inquiryId !== '0'
    ) {
      this.isReadonly = true;
      this.utils.emitLoadingEmitter(true);
      this.gisService.getInquiryProject(inquiryId).subscribe(
        (data: any) => {
          if (data?.searchBy === 'taxmap') {
            this.municipalities = new Set<string>();
            this.gisService
              .getMunicipalities(data?.county)
              .subscribe(
                (data: any) => {
                  data.features?.forEach((element: any) => {
                    this.municipalities.add(element.attributes.NAME);
                  });
                  this.utils.emitLoadingEmitter(false);
                },
                (error: any) => {
                  this.handleServiceError(error);
                  this.utils.emitLoadingEmitter(false);
                }
              );
          }
          this.copyDataToSiProject(data);
          localStorage.setItem('inquiryCategoryCode', data.reason);
          //@ts-ignore
          this.loadInquiryPolygon(inquiryId);
          this.utils.emitLoadingEmitter(false);
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.handleServiceError(error);
        }
      );
    } else {
      console.log('inquiryId: ' + inquiryId);
    }
  }
  private loadInquiryPolygon(inquiryId: string): void {
    this.utils.emitLoadingEmitter(true);
    this.gisService
      .searchSpatialInquiryPolygon(inquiryId)
      .toPromise()
      .then((polygon: any) => {
        this.utils.emitLoadingEmitter(false);
        this.getProjectGraphic(polygon);
      })
      .catch((error: any) => {
        this.utils.emitLoadingEmitter(false);
        this.handleServiceError(error);
        console.log(error);
      });
  }

  onSelectCountyChange(event: any): void {
    this.selectedCounty = event.target.value;
    this.siSearchForm.controls.municipality.setValue('');
    this.siSearchForm.controls.taxMapNumber.setValue('');
    if (!this.NYC.includes(this.selectedCounty?.trim())) {
      this.siSearchForm
        .get('municipality')
        ?.setValidators([Validators.required]);
      this.getMunicipalitiesInCounty();
      this.siSearchForm.get('municipality')?.updateValueAndValidity();
    } else {
      this.siSearchForm.get('municipality')?.clearValidators();
      this.siSearchForm.get('municipality')?.updateValueAndValidity();
    }
  }

  public getMunicipalitiesInCounty(): void {
    this.utils.emitLoadingEmitter(true);
    this.isTaxMapSearched = false;
    this.selectedMunicipality = '';
    this.municipalities = new Set<string>();
    this.gisService
      .getMunicipalities(this.siSearchForm.controls.county.value)
      .subscribe(
        (data: any) => {
          data.features?.forEach((element: any) => {
            this.municipalities.add(element.attributes.NAME);
          });
          this.utils.emitLoadingEmitter(false);
        },
        (error: any) => {
          this.handleServiceError(error);
          this.utils.emitLoadingEmitter(false);
        }
      );
  }

  onSelectMunicipalityChange(event: any): void {
    this.selectedMunicipality = event.target.value;
    this.isTaxMapSearched = false;
  }
  taxMapSearch(): void {
    this.resetTaxSearch();
    this.utils.emitLoadingEmitter(true);
    this.isTaxMapSearched = true;
    this.isTaxMapSearchedSuccessful = false;
    this.taxParcelLoading = true;
    this.gisService
      .getTaxParcel(
        this.siSearchForm.value.taxMapNumber,
        this.siSearchForm.value.county?.replace(/ /g, ''),
        this.siSearchForm.value.municipality
      )
      .subscribe(
        (response: any) => {
          this.utils.emitLoadingEmitter(false);
          this.isTaxMapSearchedSuccessful = true;
          if (
            response === undefined ||
            response === null ||
            response.features.length === 0
          ) {
            if (
              this.selectedMunicipality !== undefined &&
              this.selectedMunicipality !== null &&
              this.selectedMunicipality !== ''
            ) {
              this.isTaxMapSearchedSuccessful = true;
              this.gisMapSi.zoomToMunicipalityGeometry(
                this.selectedMunicipality,
                this.selectedCounty
              );
            } else {
              this.gisMapSi.zoomToCountyGeometry(this.selectedCounty);
            }
            return;
          }
          let taxParcel = new TaxMap();
          taxParcel.geometry = response.features[0].geometry;
          this.gisMapSi.gotoTaxParcel(taxParcel);
          this.taxParcelLoading = false;
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.taxParcelLoading = false;
          this.handleProjectLocationServiceError(error);
        }
      );
  }

  public async clearTaxMap(): Promise<void> {
    this.isTaxMapSearched = false;
    this.isTaxMapSearchedSuccessful = false;
    this.taxParcelLoading = false;
    this.siSearchForm.get('street')?.setValue('');
    this.siSearchForm.get('city')?.setValue('');
    this.siSearchForm.get('zip')?.setValue('');
    this.siSearchForm.get('reason')?.setValue('');
    this.siSearchForm.get('region')?.setValue('');
    this.siSearchForm.get('taxMapNumber')?.setValue('');
    this.siSearchForm.get('county')?.setValue('');
    this.siSearchForm.get('municipality')?.setValue('');
    this.gisMapSi.cleanProjectGraphic();
  }

  private handleProjectLocationServiceError(error: any) {
    this.projectLocationServiceErrorMessage =
      this.errorService.getServerMessage(error);
    this.projectLocationServiceError = true;
    throw error;
  }

  copyToSIProject(data: any) {
    console.log(data);
    this.siProject = new SIProject();
    this.siProject.block = data.block;
    this.siProject.borough = data.borough;
    this.siProject.city = data.city;
    this.siProject.comments = data.comments;
    this.siProject.county = data.county;
    this.siProject.developer = data.developer;
    this.siProject.depProjectManager = data.depProjectManager;
    this.siProject.dowContact = data.dowContact;
    this.siProject.efcContact = data.efcContact;
    this.siProject.email = data.email;
    this.siProject.extenderName = data.extenderName;
    this.siProject.inquiryId = data.inquiryId;
    this.siProject.issuesQuestions = data.issuesQuestions;
    this.siProject.leadAgencyContact = data.leadAgencyContact;
    this.siProject.leadAgencyName = data.leadAgencyName;
    this.siProject.lot = data.lot;
    this.siProject.mailingAddress = data.mailingAddress;
    this.siProject.mailingAddressCity = data.mailingAddressCity;
    this.siProject.mailingAddressState = data.mailingAddressState;
    this.siProject.mailingAddressStreet1 = data.mailingAddressStreet1;
    this.siProject.mailingAddressStreet2 = data.mailingAddressStreet2;
    this.siProject.mailingAddressZip = data.mailingAddressZip;
    this.siProject.municipality = data.municipality;
    this.siProject.owner = data.owner;
    this.siProject.phoneNumber = data.phoneNumber;
    this.siProject.planDescription = data.planDescription;
    this.siProject.planName = data.planName;
    this.siProject.polygonId = data.polygonId;
    this.siProject.projectDescription = data.projectDescription;
    this.siProject.projectName = data.projectName;
    this.siProject.projectSponsor = data.projectSponsor;
    this.siProject.pscDocketNum = data.pscDocketNum;
    this.siProject.reason = data.reason;
    this.siProject.region = data.region;
    this.siProject.requestorName = data.requestorName;
    this.siProject.searchBy = data.searchBy;
    this.siProject.state = data.state;
    this.siProject.street = data.street;
    this.siProject.streetAddress = data.streetAddress;
    this.siProject.taxParcel = data.taxParcel;
    this.siProject.zip = data.zip;
    this.siProject.searchByMunicipality = data.searchByMunicipality;
    this.siProject.searchByCounty = data.searchByCounty;
    this.siProject.searchByTaxParcel = data.searchByTaxParcel;
    this.siProject.response = data.response;
    this.siProject.responseDate = data.responseDate;
  }

  copyDataToSiProject(data: any) {
    this.copyToSIProject(data);
    this.isInquiryLoad = true;
    this.siSearchForm.controls['searchBy'].setValue(this.siProject.searchBy);
    this.siSearchForm.controls['street'].setValue(this.siProject.street);
    this.siSearchForm.controls['city'].setValue(this.siProject.city);
    this.siSearchForm.controls['state'].setValue(this.siProject.state);
    this.siSearchForm.controls['zip'].setValue(this.siProject.zip);
    this.siSearchForm.controls['taxMapNumber'].setValue(this.siProject.searchByTaxParcel);
    this.siSearchForm.controls['county'].setValue(this.siProject.searchByCounty);
    this.siSearchForm.controls['municipality'].setValue(this.siProject.searchByMunicipality);
    this.siSearchForm.controls['reason'].setValue(this.siProject.reason);
    this.siSearchForm.controls['region'].setValue(this.siProject.region);
    this.inquiryReasonDetails.controls['borough'].setValue(
      this.siProject.borough
    );
    this.inquiryReasonDetails.controls['block'].setValue(
      this.siProject.block
    );
    this.inquiryReasonDetails.controls['lot'].setValue(
      this.siProject.lot
    );
    this.inquiryReasonDetails.controls['requestor_name'].setValue(
      this.siProject.requestorName
    );
    this.inquiryReasonDetails.controls['street_address'].setValue(
      this.siProject.streetAddress
    );
    this.inquiryReasonDetails.controls['mailing_address_street1']?.setValue(
      this.siProject.mailingAddressStreet1
    );
    this.inquiryReasonDetails.controls['mailing_address_street2']?.setValue(
      this.siProject.mailingAddressStreet2
    );
    this.inquiryReasonDetails.controls['mailing_address_zip']?.setValue(
      this.siProject.mailingAddressZip
    );
    this.inquiryReasonDetails.controls['mailing_address_state']?.setValue(
      this.siProject.mailingAddressState
    );
    this.inquiryReasonDetails.controls['mailing_address_city']?.setValue(
      this.siProject.mailingAddressCity
    );
    this.inquiryReasonDetails.controls['phone_number'].setValue(
      this.siProject.phoneNumber
    );
    this.inquiryReasonDetails.controls['project_name'].setValue(
      this.siProject.projectName
    );
    this.inquiryReasonDetails.controls['project_description'].setValue(
      this.siProject.projectDescription
    );
    this.inquiryReasonDetails.controls['project_sponsor'].setValue(
      this.siProject.projectSponsor
    );
    this.inquiryReasonDetails.controls['issues_questions'].setValue(
      this.siProject.issuesQuestions
    );
    this.inquiryReasonDetails.controls['lead_agency_name'].setValue(
      this.siProject.leadAgencyName
    );
    this.inquiryReasonDetails.controls['lead_agency_contact'].setValue(
      this.siProject.leadAgencyContact
    );
    this.inquiryReasonDetails.controls['efc_contact'].setValue(
      this.siProject.efcContact
    );
    this.inquiryReasonDetails.controls['plan_name'].setValue(
      this.siProject.planName
    );
    this.inquiryReasonDetails.controls['plan_description'].setValue(
      this.siProject.planDescription
    );
    this.inquiryReasonDetails.controls['extender_name'].setValue(
      this.siProject.extenderName
    );
    this.inquiryReasonDetails.controls['dow_contact'].setValue(
      this.siProject.dowContact
    );
    this.inquiryReasonDetails.controls['developer'].setValue(
      this.siProject.developer
    );
    this.inquiryReasonDetails.controls['owner'].setValue(this.siProject.owner);
    this.inquiryReasonDetails.controls['dep_project_manager'].setValue(this.siProject.depProjectManager);
    this.inquiryReasonDetails.controls['psc_docket_num'].setValue(
      this.siProject.pscDocketNum
    );
    this.inquiryReasonDetails.controls['comments'].setValue(
      this.siProject.comments
    );
    this.inquiryReasonDetails.controls['email'].setValue(this.siProject.email);
    this.inquiryReasonDetails.controls['taxParcel'].setValue(
      this.siProject.taxParcel
    );
    this.inquiryReasonDetails.controls['county'].setValue(
      this.siProject.county
    );
    this.inquiryReasonDetails.controls['municipality'].setValue(
      this.siProject.municipality
    );
  }

  requestIdentifier(): string {
    let requestIdentifier = '';
    switch (this.siSearchForm.controls['reason']?.value) {
      case 'BOROUGH_DETERMINATION': requestIdentifier = this.inquiryReasonDetails.controls['borough'].value + ' ' + this.inquiryReasonDetails.controls['block'].value + ' ' + this.inquiryReasonDetails.controls['lot'].value;
        break;
      case 'JURISDICTION_DETERMINATION': requestIdentifier = this.inquiryReasonDetails.controls['project_name'].value;
        break;
      case 'SEQR_LA_REQ': requestIdentifier = this.inquiryReasonDetails.controls['project_name'].value;
        break;
      case 'PRE_APPLN_REQ': requestIdentifier = this.inquiryReasonDetails.controls['project_name'].value;
        break;
      case 'SERP_CERT': requestIdentifier = this.inquiryReasonDetails.controls['project_name'].value;
        break;
      case 'MGMT_COMPRE_PLAN': requestIdentifier = this.inquiryReasonDetails.controls['plan_name'].value;
        break;
      case 'SANITARY_SEWER_EXT': requestIdentifier = this.inquiryReasonDetails.controls['extender_name'].value;
        break;
      case 'ENERGY_PROJ': requestIdentifier = this.inquiryReasonDetails.controls['project_name'].value;
        break;
    }
    return requestIdentifier;
  }

  entity(): string {
    let entityName = '';
    switch (this.siSearchForm.controls['reason']?.value) {
      case 'BOROUGH_DETERMINATION': entityName = this.inquiryReasonDetails.controls['requestor_name'].value;
        break;
      case 'JURISDICTION_DETERMINATION': entityName = this.inquiryReasonDetails.controls['project_sponsor'].value;
        break;
      case 'SEQR_LA_REQ': entityName = this.inquiryReasonDetails.controls['project_sponsor'].value;
        break;
      case 'PRE_APPLN_REQ': entityName = this.inquiryReasonDetails.controls['project_sponsor'].value;
        break;
      case 'SERP_CERT': entityName = this.inquiryReasonDetails.controls['efc_contact'].value;
        break;
      case 'MGMT_COMPRE_PLAN': entityName = this.inquiryReasonDetails.controls['requestor_name'].value;
        break;
      case 'SANITARY_SEWER_EXT': entityName = this.inquiryReasonDetails.controls['dow_contact'].value;
        break;
      case 'ENERGY_PROJ': entityName = this.inquiryReasonDetails.controls['developer'].value;
        break;
    }
    return entityName;
  }


  projectName(): string {
    let projectName = '';
    switch (this.siSearchForm.controls['reason']?.value) {
      case 'BOROUGH_DETERMINATION': projectName = this.inquiryReasonDetails.controls['borough'].value + ' ' + this.inquiryReasonDetails.controls['block'].value + ' ' + this.inquiryReasonDetails.controls['lot'].value;
        break;
      case 'JURISDICTION_DETERMINATION': projectName = this.inquiryReasonDetails.controls['project_name'].value;
        break;
      case 'SEQR_LA_REQ': projectName = this.inquiryReasonDetails.controls['project_name'].value;
        break;
      case 'PRE_APPLN_REQ': projectName = this.inquiryReasonDetails.controls['project_name'].value;
        break;
      case 'SERP_CERT': projectName = this.inquiryReasonDetails.controls['project_name'].value;
        break;
      case 'MGMT_COMPRE_PLAN': projectName = this.inquiryReasonDetails.controls['plan_name'].value;
        break;
      case 'SANITARY_SEWER_EXT': projectName = this.inquiryReasonDetails.controls['extender_name'].value;
        break;
      case 'ENERGY_PROJ': projectName = this.inquiryReasonDetails.controls['project_name'].value;
        break;
    }
    return projectName;
  }


  async onInquirySubmit(): Promise<void> {
    if (!this.isReadonly) {
        if (
          this.siProject?.inquiryId !== undefined &&
          this.siProject?.inquiryId !== null &&
          this.siProject?.inquiryId !== 0
        ) {
          await this.updateSIProject();
        } else {
          await this.saveSIProject();
        }
    } else {
      await this.updateInquiryOnly();
    }
  }
  onZipInput(el: HTMLInputElement) {
    el.value = el.value.replace(/[^[0-9.]/g, '').replace(/(\..*)\./g, '$1');
    this.inquiryReasonDetails.controls['mailing_address_zip'].setValue(el.value);
  }

  callAddressZipLookUp(): void {
    this.showAddressServerError = false;
    if (this.inquiryReasonDetails.controls['mailing_address_zip'].value && this.inquiryReasonDetails.controls['mailing_address_street1'].value) {
      if (this.inquiryReasonDetails.controls['mailing_address_zip'].value?.length == 5 && this.inquiryReasonDetails.controls['mailing_address_street1'].value != "") {
        this.applicantInfoService.getCityAndState(this.inquiryReasonDetails.controls['mailing_address_street1'].value, this.inquiryReasonDetails.controls['mailing_address_street2'].value,this.inquiryReasonDetails.controls['mailing_address_zip'].value).subscribe((response) => {
          if (response) {
            let responseCode = response.status;
            let responseBody = response.body;
            if (responseCode == 204) {
              this.addressNotFoundError = true;
            }
            if (responseBody) {
              this.inquiryReasonDetails.controls['mailing_address_state'].setValue(responseBody.state);
              this.inquiryReasonDetails.controls['mailing_address_city'].setValue(responseBody.city.toUpperCase());
              this.addressNotFoundError = false;
              this.showAddressServerError = false;
            }
          }
        },
          (error: any) => {
            this.addressServerErrorMessage = this.errorService.getServerMessage(error);
            this.showAddressServerError = true;
            throw error;
          }

        );
      } else if ((this.inquiryReasonDetails.controls['mailing_address_zip'].value?.length < 5 && this.inquiryReasonDetails.controls['mailing_address_street1'].value != "") && this.inquiryReasonDetails.controls['mailing_address_zip'].value?.length > 0) {
        this.addressNotFoundError = true;
      }
    }
  }

  zipKeyup() {
    this.inquiryReasonDetails.controls['mailing_address_state'].setValue(''),
      this.inquiryReasonDetails.controls['mailing_address_city'].setValue(''),
      clearTimeout(this.timer);
    this.timer = setTimeout(() => {
      this.callAddressZipLookUp();
    }, 1500); //half a second 1 second == 1000
  }

  open(content: any, modelSize = '40vw') {
    this.modalReference = this.modalService.open(content, {
      ariaLabelledBy: 'modal-basic-title',
      size: modelSize,
    });
    this.modalReference.result.then(
      (result: any) => {
        this.closeResult = `Closed with: ${result}`;
      },
      (reason: any) => {
        console.log(reason);
        this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
      }
    );
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

  public openRedirectInfoModel() {
    this.open(this.modalRedirectInfo, '20vh');
  }

  sendToPermits() {
    this.router.navigate(['/apply-for-permit-details']);
  }

  onZoomCountyChange(event: any): void {
    if (event.target.value !== undefined && event.target.value !== '') {
      this.gisMapSi.zoomToCountyGeometry(event.target.value);
    } else {
      this.gisMapSi.resetExtent();
    }
  }

  private async updateInquiryOnly(): Promise<void> {
    this.utils.emitLoadingEmitter(true);
    if (!this.inquiryReasonDetails.invalid) {
      if (this.inquiryReasonDetails.dirty) {
        this.copyFormToSiProject();
        await this.gisService.saveInquiryProject(this.siProject).subscribe(
          (response: any) => {
            this.utils.emitLoadingEmitter(false);
            this.router.navigate(['/apply-for-inquiry']);
          },
          (error: any) => {
            this.handleServiceError(error);
          }
        );
      } else {
        this.utils.emitLoadingEmitter(false);
        this.router.navigate(['/apply-for-inquiry']);
      }
    }
    this.utils.emitLoadingEmitter(false);
  }

  private updateSIProject(): void {
    this.utils.emitLoadingEmitter(true);
    if (!this.inquiryReasonDetails.invalid) {
      this.copyFormToSiProject();
      let attributes = {
        OBJECTID: Number(this.siProject.polygonId),
        SI_ID: this.siProject.inquiryId,
        SI_TYPE: this.reasonValue(),
        REGION: this.siSearchForm.controls['region'].value,
        MUNICIPALITY: this.inquiryReasonDetails.controls['municipality'].value,
        Request_Identifier: this.requestIdentifier(),
      };
      this.gisService
        .saveSpatialInquiryPolygon(
          this.gisMapSi.getGraphicAsJsonString(attributes),
          'U'
        )
        .subscribe(
          (data: any) => {
            this.gisService.saveInquiryProject(this.siProject).subscribe(
              async (response: any) => {
                await this.uploadMapToDMS();
                this.utils.emitLoadingEmitter(false);
                this.router.navigate(['/apply-for-inquiry']);
              },
              (error: any) => {
                this.handleServiceError(error)
              }
            );
          },
          (error: any) => {
            this.handleServiceError(error)
          }
        );
    } else {
      this.utils.emitLoadingEmitter(false);
    }
  }

  private async uploadMapToDMS() {
    if (!this.isReadonly) {
      await this.gisMapSi.getPrintUrl(this.customTextElements).then((printUrl: string) => {
        this.inquiryService.uploadMapToGI(printUrl);
      });
    }
  }

  private async saveSIProject(): Promise<void> {
    this.utils.emitLoadingEmitter(true);
    if (!this.inquiryReasonDetails.invalid) {
      let attributes = {
        SI_ID: '0',
        SI_TYPE: this.reasonValue(),
        REGION: this.siSearchForm.controls['region'].value,
        MUNICIPALITY: this.inquiryReasonDetails.controls['municipality'].value,
        Request_Identifier: this.requestIdentifier(),
      };
      this.gisService
        .saveSpatialInquiryPolygon(
          this.gisMapSi.getGraphicAsJsonString(attributes),
          'S'
        )
        .subscribe(
          (data: any) => {
            if (data.addResults[0]) {
              let objectId = data.addResults[0]?.objectId;
              this.copyFormToSiProject();
              this.siProject.polygonId = objectId;
              this.gisService.saveInquiryProject(this.siProject).subscribe(
                (response: any) => {
                  this.siProject.inquiryId = response.inquiryId;
                  let updateAttributes = {
                    OBJECTID: Number(objectId),
                    SI_ID: response.inquiryId,
                    SI_TYPE: this.reasonValue(),
                    REGION: this.siSearchForm.controls['region'].value,
                    MUNICIPALITY: this.inquiryReasonDetails.controls['municipality'].value,
                    Request_Identifier: this.requestIdentifier(),
                  };
                  //set inquiryId value in memory
                  localStorage.setItem('inquiryId', response.inquiryId);
                  localStorage.setItem('inquiryCategoryCode', response.reason);
                  this.gisService
                    .saveSpatialInquiryPolygon(
                      this.gisMapSi.getGraphicAsJsonString(updateAttributes),
                      'U'
                    )
                    .subscribe(
                      async (data: any) => {
                        await this.uploadMapToDMS();
                        this.utils.emitLoadingEmitter(false);
                        this.router.navigate(['/apply-for-inquiry']);
                      },
                      (error: any) => {
                        this.utils.emitLoadingEmitter(false);
                        this.handleServiceError(error);
                      }
                    );
                },
                (error: any) => {
                  this.utils.emitLoadingEmitter(false);
                  this.gisService
                    .deleteSpatialInquiryPolygon(objectId)
                    .subscribe(
                      (data: any) => {
                        this.utils.emitLoadingEmitter(false);
                        console.log(data);
                      },
                      (error: any) => {
                        this.handleServiceError(error);
                      }
                    );
                  this.handleServiceError(error);
                }
              );
            }
          },
          (error: any) => {
            this.handleServiceError(error);
          }
        );
    } else {
      this.utils.emitLoadingEmitter(false);
    }
  }

  private getProjectGraphic(features: any): void {
    let polygon = new Polygon({
      rings: (features.features[0].geometry as Polygon).rings,
      spatialReference: features.spatialReference,
    });
    this.projectGraphic = new Graphic({
      geometry: polygon,
      attributes: features.features[0].attributes,
    });
  }

  private copyFormToSiProject() {
    if (this.siProject === undefined) {
      this.siProject = new SIProject();
      this.siProject.inquiryId = 0;
      this.siProject.polygonId = 0;
    }
    this.siProject.searchBy = this.siSearchForm.controls['searchBy'].value;
    this.siProject.street = this.siSearchForm.controls['street'].value;
    this.siProject.city = this.siSearchForm.controls['city'].value;
    this.siProject.state = this.siSearchForm.controls['state'].value;
    this.siProject.zip = this.siSearchForm.controls['zip'].value;
    this.siProject.searchByCounty = this.siSearchForm.controls['county'].value;
    this.siProject.searchByMunicipality = this.siSearchForm.controls['municipality'].value;
    this.siProject.searchByTaxParcel = this.siSearchForm.controls['taxMapNumber'].value;
    this.siProject.reason = this.siSearchForm.controls['reason'].value;
    this.siProject.region = this.siSearchForm.controls['region'].value;

    this.siProject.borough =
      this.inquiryReasonDetails.controls['borough'].value;
    this.siProject.block =
      this.inquiryReasonDetails.controls['block'].value;
    this.siProject.lot =
      this.inquiryReasonDetails.controls['lot'].value;
    this.siProject.requestorName =
      this.inquiryReasonDetails.controls['requestor_name'].value;
    this.siProject.streetAddress =
      this.inquiryReasonDetails.controls['street_address'].value;
    this.siProject.mailingAddressStreet1 =
      this.inquiryReasonDetails.controls['mailing_address_street1']?.value;
    this.siProject.mailingAddressStreet2 =
      this.inquiryReasonDetails.controls['mailing_address_street2']?.value;
    this.siProject.mailingAddressZip =
      this.inquiryReasonDetails.controls['mailing_address_zip']?.value;
    this.siProject.mailingAddressState =
      this.inquiryReasonDetails.controls['mailing_address_state']?.value;
    this.siProject.mailingAddressCity =
      this.inquiryReasonDetails.controls['mailing_address_city']?.value;
    this.siProject.phoneNumber =
      this.inquiryReasonDetails.controls['phone_number'].value;
    this.siProject.projectName =
      this.inquiryReasonDetails.controls['project_name'].value;
    this.siProject.projectDescription =
      this.inquiryReasonDetails.controls['project_description'].value;
    this.siProject.projectSponsor =
      this.inquiryReasonDetails.controls['project_sponsor'].value;
    this.siProject.issuesQuestions =
      this.inquiryReasonDetails.controls['issues_questions'].value;
    this.siProject.leadAgencyName =
      this.inquiryReasonDetails.controls['lead_agency_name'].value;
    this.siProject.leadAgencyContact =
      this.inquiryReasonDetails.controls['lead_agency_contact'].value;
    this.siProject.efcContact =
      this.inquiryReasonDetails.controls['efc_contact'].value;
    this.siProject.planName =
      this.inquiryReasonDetails.controls['plan_name'].value;
    this.siProject.planDescription =
      this.inquiryReasonDetails.controls['plan_description'].value;
    this.siProject.extenderName =
      this.inquiryReasonDetails.controls['extender_name'].value;
    this.siProject.dowContact =
      this.inquiryReasonDetails.controls['dow_contact'].value;
    this.siProject.developer =
      this.inquiryReasonDetails.controls['developer'].value;
    this.siProject.owner = this.inquiryReasonDetails.controls['owner'].value;
    this.siProject.depProjectManager = this.inquiryReasonDetails.controls['dep_project_manager'].value;
    this.siProject.pscDocketNum =
      this.inquiryReasonDetails.controls['psc_docket_num'].value;
    this.siProject.comments =
      this.inquiryReasonDetails.controls['comments'].value;
    this.siProject.email = this.inquiryReasonDetails.controls['email'].value;
    this.siProject.taxParcel =
      this.inquiryReasonDetails.controls['taxParcel'].value;
    this.siProject.county = this.inquiryReasonDetails.controls['county'].value;
    this.siProject.municipality =
      this.inquiryReasonDetails.controls['municipality'].value;
    console.log(this.siProject);
  }

  async onAddressSubmit() {
    this.resetAddress();
    if (this.isInquiryLoad) {
      this.open(this.modalDataLostWarning, '20vh');
    } else {
      this.geocodeAddress();
    }
  }

  async onInquirySave() {
    this.isInquirySaveClicked = true;
    if (this.inquiryReasonDetails.valid) {
      if (!this.isReadonly) {
        let region = this.siSearchForm.controls['region'].value;
        let reason = this.siSearchForm.controls['reason'].value;
        if ((region.includes('2') || region.includes('1')) && reason === 'JURISDICTION_DETERMINATION') {
          this.openRedirectInfoModel();
        } else {
          this.open(this.submitConfirmWarning, '20vh');
        }
      } else {
        this.updateInquiryOnly();
      }
    } else {
      //throw validators
    }
  }
  geocodeAddress() {
    this.isAddressSearchedSuccessful = false;
    if (
      this.siSearchForm.controls.street.invalid ||
      this.siSearchForm.controls.city.invalid
    ) {
      this.isAddressSearched = true;
    } else {
      this.getGeoCode(
        this.siSearchForm.controls.street.value ,
        this.siSearchForm.controls.city.value, this.siSearchForm.controls.zip.value
      );
    }
  }

  clearSiSearchForm() {
    this.isInquirySaveClicked =false;
    this.isAddressSearchedSuccessful = false;
    this.isAddressSearched = false;
    this.siSearchForm.get('street')?.setValue('');
    this.siSearchForm.get('city')?.setValue('');
    this.siSearchForm.get('zip')?.setValue('');
    this.siSearchForm.get('reason')?.setValue('');
    this.siSearchForm.get('region')?.setValue('');
    this.siSearchForm.get('taxMapNumber')?.setValue('');
    this.siSearchForm.get('county')?.setValue('');
    this.siSearchForm.get('municipality')?.setValue('');
    this.gisMapSi.cleanProjectGraphic();
  }

  resetAddress(){
    this.isInquirySaveClicked =false;
    this.isAddressSearchedSuccessful = false;
    this.isAddressSearched = false;
    this.siSearchForm.get('reason')?.setValue('');
    this.siSearchForm.get('region')?.setValue('');
    this.siSearchForm.get('taxMapNumber')?.setValue('');
    this.siSearchForm.get('county')?.setValue('');
    this.siSearchForm.get('municipality')?.setValue('');
    this.gisMapSi.cleanProjectGraphic();
  }
  resetTaxSearch(){
    this.isTaxMapSearched = false;
    this.isTaxMapSearchedSuccessful = false;
    this.taxParcelLoading = false;
    this.siSearchForm.get('street')?.setValue('');
    this.siSearchForm.get('city')?.setValue('');
    this.siSearchForm.get('zip')?.setValue('');
    this.siSearchForm.get('reason')?.setValue('');
    this.siSearchForm.get('region')?.setValue('');
    this.gisMapSi.cleanProjectGraphic();
  }

  resetForm() {
    this.isInquirySaveClicked =false;
    let tempSiProject = new SIProject();
    tempSiProject.inquiryId = this.siProject.inquiryId;
    tempSiProject.polygonId = this.siProject.polygonId;
    this.siProject = tempSiProject;
    this.projectGraphic = new Graphic();
    this.siSearchForm.get('region')?.setValue('');
    this.siSearchForm.get('reason')?.setValue('');
    this.geocodeAddress();
  }

  reloadPage() {
    window.location.reload();
  }

  cancel() {
    if (this.siSearchForm.dirty || this.inquiryReasonDetails.dirty) {
      this.warningModal.open();
    } else {
      this.router.navigate(['/apply-for-inquiry']);
    }
  }

  gotoDashBoard() {
    this.router.navigate(['/dashboard']);
  }

  private async getGeoCode(address: string, city: string, zip: string): Promise<void> {
  this.gisService
      .getEsriAddresses(
        address?.replace(/&/g, 'and').replace(/[^a-zA-Z0-9 ]/g, ''), city?.replace(/&/g, 'and').replace(/[^a-zA-Z0-9 ]/g, ''), zip
      ).subscribe(async (responseData: any) => {
        if (
          responseData !== undefined &&
          responseData.candidates?.length > 0
        ) {
          this.utils.emitLoadingEmitter(false);
          this.isAddressSearchedSuccessful = true;
          await this.gisMapSi.gotoGeoCodedAddress(
            responseData.candidates[0].location.x,
            responseData.candidates[0].location.y
          );
        } else {
          this.utils.emitLoadingEmitter(false);
          this.serviceError = true;
          this.serviceErrorMessage =
            'Error: Please provide valid address';
        }
      },
      (err: any) => {
        this.handleServiceError(err);
      }
    );
  }

 cleanupDataOnSearchByChange() {
    let streetControl = this.siSearchForm.get('street');
    let cityControl = this.siSearchForm.get('city');
    let countyControl =  this.siSearchForm.get('county');
    let taxMapNumberControl= this.siSearchForm.get('taxMapNumber');;

    let searchBy = this.siSearchForm.get('searchBy')?.value;
    this.clearServiceErrors();
    this.clearSiSearchForm();
    if (searchBy === 'zoom') {
      streetControl?.clearValidators();
      streetControl?.updateValueAndValidity();
      cityControl?.clearValidators();
      cityControl?.updateValueAndValidity();
      countyControl?.clearValidators();
      countyControl?.updateValueAndValidity();
      taxMapNumberControl?.clearValidators();
      taxMapNumberControl?.updateValueAndValidity();
    }
    if (searchBy === 'address') {
      streetControl?.setValidators([
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(100),
      ]);
      streetControl?.updateValueAndValidity();
      cityControl?.setValidators([
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(65),
      ]);
      cityControl?.updateValueAndValidity();
    }
    if (searchBy === 'taxmap') {
      countyControl?.setValidators([
        Validators.required,
      ]);
      countyControl?.updateValueAndValidity();
      taxMapNumberControl?.setValidators([
        Validators.required,
      ]);
      taxMapNumberControl?.updateValueAndValidity();
    }
  }

  private changeListener(): void {
    this.siSearchForm
      .get('searchBy')
      ?.valueChanges.subscribe((searchBy: any) => {
        this.cleanupDataOnSearchByChange();
      });
      this.siSearchForm
      .get('reason')
      ?.valueChanges.subscribe((reason: any) => {
        let region = this.siSearchForm.controls['region'].value;
        if ((region.includes('2') || region.includes('1')) && reason === 'JURISDICTION_DETERMINATION') {
          this.openRedirectInfoModel();
        }
      });

  }


  private handleServiceError(error: any) {
    this.utils.emitLoadingEmitter(false);
    this.serviceErrorMessage = this.errorService.getServerMessage(error);
    this.serviceError = true;
    this.errorDiv.nativeElement.focus();
    throw error;
  }

  private inquiryTypeChangeListener(): void {
    let borough = this.inquiryReasonDetails.get('borough');
    let block = this.inquiryReasonDetails.get('block');
    let lot = this.inquiryReasonDetails.get('lot');
    let requestor_name = this.inquiryReasonDetails.get('requestor_name');
    let street_address = this.inquiryReasonDetails.get('street_address');
    let mailing_address_street1 = this.inquiryReasonDetails.get('mailing_address_street1');
    let mailing_address_zip = this.inquiryReasonDetails.get('mailing_address_zip');
    let mailing_address_state = this.inquiryReasonDetails.get('mailing_address_state');
    let mailing_address_city = this.inquiryReasonDetails.get('mailing_address_city');
    let phone_number = this.inquiryReasonDetails.get('phone_number');
    let project_name = this.inquiryReasonDetails.get('project_name');
    let project_description = this.inquiryReasonDetails.get(
      'project_description'
    );
    let project_sponsor = this.inquiryReasonDetails.get('project_sponsor');
    let issues_questions = this.inquiryReasonDetails.get('issues_questions');
    let lead_agency_name = this.inquiryReasonDetails.get('lead_agency_name');
    let lead_agency_contact = this.inquiryReasonDetails.get(
      'lead_agency_contact'
    );
    let email = this.inquiryReasonDetails.get('email');
    this.siSearchForm.get('reason')?.valueChanges.subscribe((siType: any) => {
      this.clearServiceErrors();
      this.resetValues();
      if (siType === 'BOROUGH_DETERMINATION') {
        borough?.setValidators([Validators.required]);
        borough?.updateValueAndValidity();
        block?.setValidators([Validators.required]);
        block?.updateValueAndValidity();
        lot?.setValidators([Validators.required]);
        lot?.updateValueAndValidity();
        requestor_name?.setValidators([Validators.required]);
        requestor_name?.updateValueAndValidity();
        street_address?.setValidators([Validators.required]);
        street_address?.updateValueAndValidity();
        mailing_address_street1?.setValidators([Validators.required]);
        mailing_address_street1?.updateValueAndValidity();
        mailing_address_zip?.setValidators([Validators.required]);
        mailing_address_zip?.updateValueAndValidity();
        mailing_address_state?.setValidators([Validators.required]);
        mailing_address_state?.updateValueAndValidity();
        mailing_address_city?.setValidators([Validators.required]);
        mailing_address_city?.updateValueAndValidity();
        phone_number?.setValidators([
          Validators.pattern('^[0-9]*$'),
          Validators.minLength(10),
          Validators.maxLength(10),
        ]);
        phone_number?.updateValueAndValidity();
        email?.setValidators([
          Validators.email,
          Validators.pattern('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$'),
        ]);
        email?.updateValueAndValidity();
      }
      if (siType === 'JURISDICTION_DETERMINATION') {
        project_name?.setValidators([Validators.required]);
        project_name?.updateValueAndValidity();
        project_description?.setValidators([Validators.required]);
        project_description?.updateValueAndValidity();
        project_sponsor?.setValidators([Validators.required]);
        project_sponsor?.updateValueAndValidity();
        requestor_name?.setValidators([Validators.required]);
        requestor_name?.updateValueAndValidity();
        phone_number?.setValidators([
          Validators.pattern('^[0-9]*$'),
          Validators.minLength(10),
          Validators.maxLength(10),
        ]);
        phone_number?.updateValueAndValidity();
        email?.setValidators([
          Validators.email,
          Validators.pattern('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$'),
        ]);
        email?.updateValueAndValidity();
        issues_questions?.setValidators([Validators.required]);
        issues_questions?.updateValueAndValidity();
      }
      if (siType === 'SEQR_LA_REQ') {
        project_description?.setValidators([Validators.required]);
        project_description?.updateValueAndValidity();
        lead_agency_name?.setValidators([Validators.required]);
        lead_agency_name?.updateValueAndValidity();
        lead_agency_contact?.setValidators([Validators.required]);
        lead_agency_contact?.updateValueAndValidity();
        project_name?.setValidators([Validators.required]);
        project_name?.updateValueAndValidity();
        project_sponsor?.setValidators([Validators.required]);
        project_sponsor?.updateValueAndValidity();
        phone_number?.setValidators([
          Validators.pattern('^[0-9]*$'),
          Validators.minLength(10),
          Validators.maxLength(10)
        ]);
        phone_number?.updateValueAndValidity();
        email?.setValidators([
          Validators.email,
          Validators.pattern('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$'),
        ]);
        email?.updateValueAndValidity();
      }
      if (siType === 'PRE_APPLN_REQ') {
        project_name?.setValidators([Validators.required]);
        project_name?.updateValueAndValidity();
        project_description?.setValidators([Validators.required]);
        project_description?.updateValueAndValidity();
        requestor_name?.setValidators([Validators.required]);
        requestor_name?.updateValueAndValidity();
        project_sponsor?.setValidators([Validators.required]);
        project_sponsor?.updateValueAndValidity();
        issues_questions?.setValidators([Validators.required]);
        issues_questions?.updateValueAndValidity();
        phone_number?.setValidators([
          Validators.pattern('^[0-9]*$'),
          Validators.minLength(10),
          Validators.maxLength(10)
        ]);
        phone_number?.updateValueAndValidity();
        email?.setValidators([
          Validators.email,
          Validators.pattern('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$'),
        ]);
        email?.updateValueAndValidity();
      }
      if (siType === 'SERP_CERT') {
        phone_number?.setValidators([
          Validators.pattern('^[0-9]*$'),
          Validators.minLength(10),
          Validators.maxLength(10)
        ]);
        phone_number?.updateValueAndValidity();
        email?.setValidators([
          Validators.email,
          Validators.pattern('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$'),
        ]);
        email?.updateValueAndValidity();
      }
      if (siType === 'MGMT_COMPRE_PLAN') {
        phone_number?.setValidators([
          Validators.pattern('^[0-9]*$'),
          Validators.minLength(10),
          Validators.maxLength(10)
        ]);
        phone_number?.updateValueAndValidity();
        email?.setValidators([
          Validators.email,
          Validators.pattern('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$'),
        ]);
        email?.updateValueAndValidity();
      }
      if (siType === 'SANITARY_SEWER_EXT') {
        email?.setValidators([
          Validators.email,
          Validators.pattern('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$'),
        ]);
        email?.updateValueAndValidity();
      }
      if (siType === 'ENERGY_PROJ') {
        //Do nothing
      }
    });
  }

  resetValues() {
    let borough = this.inquiryReasonDetails.get('borough');
    let block = this.inquiryReasonDetails.get('block');
    let lot = this.inquiryReasonDetails.get('lot');
    let requestor_name = this.inquiryReasonDetails.get('requestor_name');
    let street_address = this.inquiryReasonDetails.get('street_address');
    let mailing_address_street1 = this.inquiryReasonDetails.get('mailing_address_street1');
    let mailing_address_zip = this.inquiryReasonDetails.get('mailing_address_zip');
    let mailing_address_state = this.inquiryReasonDetails.get('mailing_address_state');
    let mailing_address_city = this.inquiryReasonDetails.get('mailing_address_city');
    let phone_number = this.inquiryReasonDetails.get('phone_number');
    let project_name = this.inquiryReasonDetails.get('project_name');
    let project_description = this.inquiryReasonDetails.get(
      'project_description'
    );
    let project_sponsor = this.inquiryReasonDetails.get('project_sponsor');
    let issues_questions = this.inquiryReasonDetails.get('issues_questions');
    let lead_agency_name = this.inquiryReasonDetails.get('lead_agency_name');
    let lead_agency_contact = this.inquiryReasonDetails.get(
      'lead_agency_contact'
    );
    let efc_contact = this.inquiryReasonDetails.get('efc_contact');
    let plan_name = this.inquiryReasonDetails.get('plan_name');
    let plan_description = this.inquiryReasonDetails.get('plan_description');
    let extender_name = this.inquiryReasonDetails.get('extender_name');
    let dow_contact = this.inquiryReasonDetails.get('dow_contact');
    let developer = this.inquiryReasonDetails.get('developer');
    let depProjectMngr = this.inquiryReasonDetails.get('dep_project_manager');
    let owner = this.inquiryReasonDetails.get('owner');
    let psc_docket_num = this.inquiryReasonDetails.get('psc_docket_num');
    let comments = this.inquiryReasonDetails.get('comments');
    let email = this.inquiryReasonDetails.get('email');
    borough?.setValue('');
    borough?.setValidators(null);
    borough?.updateValueAndValidity();
    block?.setValue('');
    block?.setValidators(null);
    block?.updateValueAndValidity();
    lot?.setValue('');
    lot?.setValidators(null);
    lot?.updateValueAndValidity();
    requestor_name?.setValue('');
    requestor_name?.setValidators(null);
    requestor_name?.updateValueAndValidity();
    street_address?.setValue('');
    street_address?.setValidators(null);
    street_address?.updateValueAndValidity();
    mailing_address_street1?.setValue('');
    mailing_address_street1?.setValidators(null);
    mailing_address_street1?.updateValueAndValidity();
    mailing_address_zip?.setValue('');
    mailing_address_zip?.setValidators(null);
    mailing_address_zip?.updateValueAndValidity();
    mailing_address_state?.setValue('');
    mailing_address_state?.setValidators(null);
    mailing_address_state?.updateValueAndValidity();
    mailing_address_city?.setValue('');
    mailing_address_city?.setValidators(null);
    mailing_address_city?.updateValueAndValidity();
    phone_number?.setValue('');
    phone_number?.setValidators(null);
    phone_number?.updateValueAndValidity();
    project_name?.setValue('');
    project_name?.setValidators(null);
    project_name?.updateValueAndValidity();
    project_description?.setValue('');
    project_description?.setValidators(null);
    project_description?.updateValueAndValidity();
    project_sponsor?.setValue('');
    project_sponsor?.setValidators(null);
    project_sponsor?.updateValueAndValidity();
    issues_questions?.setValue('');
    issues_questions?.setValidators(null);
    issues_questions?.updateValueAndValidity();
    lead_agency_name?.setValue('');
    lead_agency_name?.setValidators(null);
    lead_agency_name?.updateValueAndValidity();
    lead_agency_contact?.setValue('');
    lead_agency_contact?.setValidators(null);
    lead_agency_contact?.updateValueAndValidity();
    efc_contact?.setValue('');
    efc_contact?.setValidators(null);
    efc_contact?.updateValueAndValidity();
    plan_name?.setValue('');
    plan_name?.setValidators(null);
    plan_name?.updateValueAndValidity();
    plan_description?.setValue('');
    plan_description?.setValidators(null);
    plan_description?.updateValueAndValidity();
    extender_name?.setValue('');
    extender_name?.setValidators(null);
    extender_name?.updateValueAndValidity();
    dow_contact?.setValue('');
    dow_contact?.setValidators(null);
    dow_contact?.updateValueAndValidity();
    developer?.setValue('');
    developer?.setValidators(null);
    developer?.updateValueAndValidity();
    depProjectMngr?.setValue('');
    depProjectMngr?.setValidators(null);
    depProjectMngr?.updateValueAndValidity();
    owner?.setValue('');
    owner?.setValidators(null);
    owner?.updateValueAndValidity();
    psc_docket_num?.setValue('');
    psc_docket_num?.setValidators(null);
    psc_docket_num?.updateValueAndValidity();
    comments?.setValue('');
    comments?.setValidators(null);
    comments?.updateValueAndValidity();
    email?.setValue('');
    email?.setValidators(null);
    email?.updateValueAndValidity();
    console.log(this.inquiryReasonDetails);
  }

  clearServiceErrors() {
    this.isInquirySaveClicked =false;
    this.serviceError = false;
    this.serviceErrorMessage = '';
  }

  get customTextElements() {
    // If there are multiple municipalities, change it to 'NEW YORK'.
    let muniNames:string = this.inquiryReasonDetails.controls['municipality'].value;
    if(muniNames?.split(', ').length > 1){
      muniNames = 'NEW YORK';
    }
    return [
      {"ProjectName": this.projectName() },
      {"MuniName": muniNames },
      {"TaxParcelID": this.inquiryReasonDetails.controls['taxParcel'].value},
      {"InquiryID": this.siProject?.inquiryId !== undefined ? this.inquiryService.formatInquiryId(this.siProject?.inquiryId) : 0},
      {"InquiryType": this.reasonValue()},
      {"EntityName": this.entity()}
    ];
  }

  getReasons(region: string, isAnalyst: boolean = false) {
    if (isAnalyst) {
      if (region === '2') {
        return this.si_reasons;
      } else {
        return this.si_reasons.filter((v: SIType) => v.spatialInqCategoryCode !== 'BOROUGH_DETERMINATION');
      }
    } else {
      if (region === '2') {
        return this.si_reasons.filter((v: SIType) => v.categoryAvailTo === 'PA' );
      } else {
        return this.si_reasons.filter((v: SIType) => v.categoryAvailTo === 'PA' && v.spatialInqCategoryCode !== 'BOROUGH_DETERMINATION');
      }
    }
  }
  filterReasonListByCategory(region: string){
    if(region==='2'){
      this.reasonsListApplicant =this.si_reasons.filter((v: SIType) => v.categoryAvailTo === 'PA' );
    }else{
      this.reasonsListApplicant =this.si_reasons.filter((v: SIType) => v.categoryAvailTo === 'PA' && v.spatialInqCategoryCode !== 'BOROUGH_DETERMINATION');
    }
    this.reasonsListAnalyst =this.si_reasons.filter((v: SIType) => v.categoryAvailTo === 'A' );
  }

  public async onTaxMapUpdated(taxmaps: string): Promise<void> {
    this.inquiryReasonDetails.controls['taxParcel'].setValue(taxmaps);
  }

  public async onRegionsUpdated(regions: string): Promise<void> {
    this.siSearchForm.controls['region'].setValue(regions);
    if (regions?.includes('2')) {
      this.reasonsList = this.getReasons('2', this.isAnalyst);
      this.filterReasonListByCategory('2');
    } else {
      this.reasonsList = this.getReasons('0', this.isAnalyst);
      this.filterReasonListByCategory('0');
    }
  }

  private getAnalysts() {
    this.inquiryService.getAnalystsByRegion('0').subscribe((data: any) => {
      this.depProjectManager = data;
    });
  }

  public async onPolygonExistUpdated(polygonExist: number): Promise<void> {
    if (polygonExist > 0) {
      this.siSearchForm.controls['polygon'].setValue(polygonExist);
    } else {
      this.siSearchForm.controls['polygon'].setValue('');
    }
  }

  public async getCountiesData(): Promise<void> {
    this.counties = [];
    this.gisService.getCounties().subscribe((data: any) => {
      data.features?.forEach((element: any) => {
        this.counties.push(element.attributes.NAME);
      });
    });
  }

  public async onCountyUpdated(counties: string): Promise<void> {
    this.inquiryReasonDetails.controls['county'].setValue(counties);
  }
  public async onMunicipalityUpdated(municipalities: string): Promise<void> {
    this.inquiryReasonDetails.controls['municipality'].setValue(municipalities);
  }

  public selectionChange(event: any): void {
    this.selectedIndex = event.selectedIndex;
    switch (event.selectedIndex) {
      case 0:
        break;
      case 1:
        break;
    }
  }
}

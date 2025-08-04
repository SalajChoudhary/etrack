import {
  Component,
  ElementRef,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import Polygon from '@arcgis/core/geometry/Polygon';
import Graphic from '@arcgis/core/Graphic';
import { GeoCodedAddress } from 'src/app/@store/models/geo-coded-address';
import { GisService } from '../../../../@shared/services/gisService';
import { TitleCasePipe } from '@angular/common';
import { ApprovedFacility } from 'src/app/@store/models/facility';
import { Router } from '@angular/router';
import { TaxParcel } from 'src/app/@store/models/TaxParcel';
import {
  FacilityAddress,
  ProjectFacility,
  ProjectLocation,
} from 'src/app/@store/models/projectLocation';
import { IdResponse } from 'src/app/@store/models/IdResponse';
import { GisMapComponent } from '../gis-map/gis-map.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { AddressResponse } from 'src/app/@store/models/addressResponse';
import {
  ModalDismissReasons,
  NgbModal,
  NgbModalRef,
} from '@ng-bootstrap/ng-bootstrap';
import { MatStepper } from '@angular/material/stepper';
import { Utils } from 'src/app/@shared/services/utils';
import { GisMapViewComponent } from '../gis-map-view/gis-map-view.component';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { takeUntil } from 'rxjs/operators';
import { fromEvent, Subject } from 'rxjs';
import { TaxMap } from 'src/app/@store/models/TaxMap';
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { CivilDivision } from 'src/app/@store/models/civilDivision';
import SpatialReference from '@arcgis/core/geometry/SpatialReference';
import * as projection from "@arcgis/core/geometry/projection.js";
import Point from '@arcgis/core/geometry/Point';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { connectableObservableDescriptor } from 'rxjs/internal/observable/ConnectableObservable';

// Search Type Interface
export interface SearchType {
  id: string;
  name: string;
  priorPermit: string;
}

export class Address {
  street!: string;
  city!: string;
  state!: string;
  zip!: string;
}

@Component({
  selector: 'app-project-location',
  templateUrl: './project-location.component.html',
  styleUrls: ['./project-location.component.scss'],
  providers: [TitleCasePipe, {
    provide: STEPPER_GLOBAL_OPTIONS, useValue: { displayDefaultIndicatorType: false }
  }]
})
export class ProjectLocationComponent implements OnInit {
  userRoles: any[] = [];
  errorMsgObj: any = {};
  NAD83 = new SpatialReference({ wkid: 26918 });
  private unsubscriber: Subject<void> = new Subject<void>();

  @ViewChild(GisMapComponent, { static: true }) gisMap!: GisMapComponent;
  @ViewChild(GisMapViewComponent, { static: true }) gisMapView!: GisMapViewComponent;
  @ViewChild('uploadstatus', { static: false }) uploadStatus!: ElementRef;
  @ViewChild('facilityConfirmModal', { static: true }) facilityConfirm!: NgbModal;
  @ViewChild('pendingPopup', { static: true }) warningModal!: PendingChangesPopupComponent;
  @ViewChild('submitConfirmModal', { static: true }) submitConfirm!: NgbModal;
  @ViewChild('stepper', { static: false }) stepper!: MatStepper;
  @ViewChild('inFile', { static: false }) shapeFile!: ElementRef;
  @ViewChild('nextBtn') nextBtn!: ElementRef;

  //stepper control data
  selectedIndex: number = 0;

  //roles
  isAnalyst: boolean = false;
  isApplicant: boolean = false;

  //model related variables
  modalReference!: NgbModalRef;
  closeResult = '';

  @Input() disableNext: boolean = true;

  serviceError: boolean = false;
  serviceErrorMessage: string = '';

  projectLocationServiceError: boolean = false;
  projectLocationServiceErrorMessage: string = '';

  projectLocationForm!: FormGroup;
  projectLocationDetailsForm!: FormGroup;

  isSubmitted = false;

  isAddressSearched = false;
  isFacilitySearched = false;
  isSearchedByIdAttempted = false;
  isFacilitySearchedByDecId = false;
  isTaxMapSearched = false;
  isSavedSubmitted = false;
  notApplicable = false;
  taxParcelNotApplicable = false;
  canProceedToDetails = new FormControl('', [Validators.required]);

  taxGeometryList!: Graphic[];
  taxMapGeometryList!: Graphic[];
  facilityGeometryList!: Graphic[];
  sketchGeometryList!: Graphic[];
  shapeFileGeometryList!: Graphic[];
  workAreaGeometryList!: Graphic[];

  facilityGeometry!: Graphic;
  finalSketchGraphic!: Graphic;
  finalTaxGraphic!: Graphic;
  finalTaxMapGraphic!: Graphic;
  finalShapeFileGraphic!: Graphic;
  finalWorkAreaGraphic!: Graphic;

  taxParcelLoading = false;

  mapProperties = {
    basemap: 'streets',
    center: [-75.62757627797825, 42.98572311852962],
    zoom: 5,
  };

  searchtypes: SearchType[] = [
    { id: 'address', name: 'Property Address', priorPermit: 'N' },
    { id: 'taxmap', name: 'Tax Parcel ID', priorPermit: 'N' },
    { id: 'zoom', name: 'Zoom (or Pan) on Map', priorPermit: 'N' },
    { id: 'shapefile', name: 'Upload Shapefile', priorPermit: 'N' },
    { id: 'dec_id', name: 'DEC ID', priorPermit: 'Y' },
    { id: 'spdes_id', name: 'SPDES ID', priorPermit: 'Y' },
    { id: 'mine_land_id', name: 'Mined Land ID', priorPermit: 'Y' },
    { id: 'solid_waste_id', name: 'Solid Waste ID', priorPermit: 'Y' },
  ];

  //Hold list of counties and municipalities by selected county
  counties!: string[];
  municipalities!: Set<string>;

  address!: string;

  // holds selected values from the view
  selectedCounty!: string;
  selectedMunicipality!: string;

  selectedAddress!: AddressResponse;
  finalSelectedAddress!: AddressResponse;

  selectedFacility!: ApprovedFacility;
  selectedFacilityForDecId!: ApprovedFacility;
  selectedTaxParcel!: TaxParcel;

  addressFacilities!: Array<AddressResponse>;

  //TaxMap data holders
  taxMapResponse!: AddressResponse;
  selectedFacilityForTaxMap!: ApprovedFacility;
  approvedFacilitiesForTaxMap!: Array<ApprovedFacility>;
  codedAddressesForTaxMap!: Array<AddressResponse>;

  approvedFacilities!: Array<ApprovedFacility>;

  approvedFacilitiesForDecId!: Array<ApprovedFacility>;
  codedAddressesForId!: Array<AddressResponse>;

  project!: ProjectLocation;
  idResponse!: IdResponse;

  approvedFacility!: ApprovedFacility;

  //Hold data for dialog for facilities at given geometry
  facilitiesAtGeometry!: Array<ApprovedFacility>;
  selectedFacilityAtGeometry!: ApprovedFacility;
  stepOneCompleted: boolean = false;

  mode: any = localStorage.getItem('mode');
  applyForPermitData: any = localStorage.getItem('applyForPermitData');
  applicantTypeCode!: number;
  mailInInd!: number;
  receivedDate!: Date;
  classifiedUnderSeqr!: string;
  NYC = ['Queens', 'New York', 'Kings', 'Richmond', 'Bronx'];


  constructor(
    private gisService: GisService,
    private router: Router,
    public utils: Utils,
    private _formBuilder: FormBuilder,
    private titleCasePipe: TitleCasePipe,
    public commonService: CommonService,
    private modalService: NgbModal,
    private errorService: ErrorService,
  ) { }

  ngOnInit(): void {
    this.getUserRoles();
    this.getApplyPermitData();
    this.initializeForms();
    this.projectLocationForm.controls['city'].valueChanges.subscribe((res: string) => {
      if (res && res.length >= 65) {
        const str = res.substring(0,65);
        this.projectLocationForm.controls['city'].patchValue(str);
      }
    })
    let projectId = localStorage.getItem('projectId');
    if (projectId !== undefined && projectId !== null && projectId !== '') {
      this.initializeProjectLocation(projectId);
    } else {
      this.setProjectLocationFormValidators();
      this.getCountiesData();
      this.changeListener();
      this.getAllErrorMsgs();
      this.setControlHasErrors(this.canProceedToDetails, true);
    }

    history.pushState(null, '');
    //disables browsers back button
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }
  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }

  private getUserRoles(): void {
    this.userRoles = this.commonService.roles;
    if (this.userRoles !== undefined) {
      this.isAnalyst = this.userRoles.includes(UserRole.Admin) || this.userRoles.includes(UserRole.Analyst) || this.userRoles.includes(UserRole.Override_Admin) || this.userRoles.includes(UserRole.System_Analyst) || this.userRoles.includes(UserRole.System_Admin) || this.userRoles.includes(UserRole.Override_Analyst);
      this.isApplicant = !this.isAnalyst;
    }
  }
  private getApplyPermitData(): void {
    if (
      this.applyForPermitData !== undefined &&
      this.applyForPermitData !== null &&
      this.applyForPermitData !== ''
    ) {
      this.applicantTypeCode = JSON.parse(
        this.applyForPermitData
      )?.applicantTypeCode;
      this.mailInInd = JSON.parse(this.applyForPermitData)?.mailInInd;
      this.receivedDate = JSON.parse(this.applyForPermitData)?.receivedDate;
      this.classifiedUnderSeqr = JSON.parse(this.applyForPermitData)?.classifiedUnderSeqr;
    } else {
      this.applyForPermitData = localStorage.getItem('applyForPermitData');
      this.applicantTypeCode = JSON.parse(
        this.applyForPermitData
      )?.applicantTypeCode;
      this.mailInInd = JSON.parse(this.applyForPermitData)?.mailInInd;
      this.receivedDate = JSON.parse(this.applyForPermitData)?.receivedDate;
      this.classifiedUnderSeqr = JSON.parse(this.applyForPermitData)?.classifiedUnderSeqr;
    }
  }

  private initializeProjectLocation(projectId: string): void {
    this.utils.emitLoadingEmitter(true);
    if (this.mode == 'validate') {
      this.gisService.getProjectDetails(projectId).subscribe(
        async (data: any) => {
          let polygonId = data.polygonId;
          if (
            polygonId !== undefined &&
            polygonId !== null &&
            polygonId !== ''
          ) {
            if (this.isAnalyst && data.polygonStatus === 'APPLICANT_SUBMITTED') {
              await this.copySubmitToAnalyst(data);
            } else {
              this.utils.emitLoadingEmitter(false);
              this.router.navigate(['/project-location-details']);
            }
          }
          this.utils.emitLoadingEmitter(false);
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.handleProjectLocationServiceError(error);
        }
      );
    } else {
      this.utils.emitLoadingEmitter(false);
      this.router.navigate(['/project-location-details']);
    }
  }

  ngAfterViewInit(): void {
    this.nextBtn.nativeElement.disabled = true;
  }
  backToMain() {
    this.router.navigate(['/apply-for-permit-details']);
  }

  async saveAndSubmit() {
    this.gisMapView.getPrintUrl().then((printUrl: string) => {
      if (printUrl !== '') {
        this.saveProjectLocation(true, printUrl);
      }
    });
  }

  saveOnly() {
    this.saveProjectLocation();
  }

  async cancel() {
    if (this.projectLocationForm.dirty) {
      await this.warningModal.open();
    } else {
      this.backToMain();
    }
  }

  backToLocation() {
    this.stepper.previous();
  }
  forward() {
    this.router.navigate(['/associated-applicants']);
  }
  async getAllErrorMsgs() {
    try {
      let response = await this.commonService.getAllErrorMessages();
      if (!!response) {
        this.errorMsgObj = response['en-US'];
      }
    } catch (e) { }
  }

  get isNYC() {
    return;
  }

  private initializeForms(): void {
    this.projectLocationForm = this._formBuilder.group({
      priorPermit: ['N'],
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
      zip: [''],
      idValue: ['', [Validators.required]],
      taxMapNumber: ['', [Validators.required]],
      county: ['', [Validators.required]],
      municipality: ['', [Validators.required]],
      geometry: [''],
    });
    this.projectLocationDetailsForm = this._formBuilder.group({
      decId: [''],
      projectName: ['', [Validators.required,this.utils.facilityNameValidator, Validators.maxLength(100)]],
      address1: ['', [Validators.required]],
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
      municipality: [''],
      municipalityNames: [''],
      municipalitySwis: [''],
      primaryMunicipality: [''],
      county: [''],
      countyNames: [''],
      countySwis: [''],
      taxMap: [''],
      latitude: [''],
      longitude: [''],
      nytmx: [''],
      nytmy: [''],
      region: [''],
      primaryRegion: [''],
      workAreaId: ['']
    });
    //this.projectLocationDetailsForm.controls.address1.valueChanges.subscribe((val: any) => {console.log(val)})
  }

  private setControlHasErrors(control: FormControl, state: boolean): void {
    if (!this.nextBtn || !this.nextBtn.nativeElement) {
      return;
    }
    if (state) {
      control.setValue('');
      this.nextBtn.nativeElement.disabled = true;
    } else {
      control.setValue(0);
      this.nextBtn.nativeElement.disabled = false;
    }
  }

  private changeListener(): void {
    this.projectLocationForm
      .get('street')
      ?.valueChanges.subscribe((val: any) => {
        if (this.projectLocationForm.controls.searchBy.value === 'address') {
          this.isAddressSearched = false;
          this.notApplicable = false;
        }
      });
    this.projectLocationForm.get('city')?.valueChanges.subscribe((val: any) => {
      if (this.projectLocationForm.controls.searchBy.value === 'address') {
        this.isAddressSearched = false;
        this.notApplicable = false;
      }
    });
    this.projectLocationForm
      .get('taxMapNumber')
      ?.valueChanges.subscribe((val: any) => {
        if (this.projectLocationForm.controls.searchBy.value === 'taxmap') {
          this.isTaxMapSearched = false;
        }
      });
    this.projectLocationForm
      .get('municipality')
      ?.valueChanges.subscribe((val: any) => {
        if (this.projectLocationForm.controls.searchBy.value === 'taxmap') {
          this.isTaxMapSearched = false;
        }
      });
    this.projectLocationForm
      .get('idValue')
      ?.valueChanges.subscribe((val: any) => {
        if (
          this.projectLocationForm.controls.searchBy.value === 'dec_id' ||
          this.projectLocationForm.controls.searchBy.value === 'spdes_id' ||
          this.projectLocationForm.controls.searchBy.value === 'mine_land_id' ||
          this.projectLocationForm.controls.searchBy.value === 'solid_waste_id'
        ) {
          this.isSearchedByIdAttempted = false;
          this.isFacilitySearchedByDecId = false;
        }
      });
  }

  private resetProjectLocationErrors(): void {
    this.projectLocationServiceError = false;
    this.projectLocationServiceErrorMessage = '';
  }

  private setProjectLocationFormValidators(): void {
    let addressControl = this.projectLocationForm.get('street');
    let cityControl = this.projectLocationForm.get('city');
    let zipControl = this.projectLocationForm.get('zip');
    let idValueControl = this.projectLocationForm.get('idValue');
    let taxMapNumberControl = this.projectLocationForm.get('taxMapNumber');
    let countyControl = this.projectLocationForm.get('county');
    let municipalityControl = this.projectLocationForm.get('municipality');
    this.projectLocationForm.get('priorPermit')?.valueChanges.subscribe(() => {
      this.projectLocationForm.controls.searchBy.markAsUntouched();
      this.projectLocationForm.controls.searchBy.markAsPristine();
      this.resetAllData();
      this.clearProjectDetails();
      this.resetAllGeometry();
      this.setControlHasErrors(
        this.canProceedToDetails,
        !this.isGraphicExist()
      );
      if (!this.isGraphicExist()) {
        this.projectLocationForm.controls.geometry.setValue('');
      }
    });
    this.projectLocationForm
      .get('searchBy')
      ?.valueChanges.subscribe((searchBy) => {
        this.isSubmitted = false;
        this.isAddressSearched = false;
        this.notApplicable = false;
        this.isTaxMapSearched = false;
        this.isSearchedByIdAttempted = false;
        this.resetAllGeometry();
        this.setControlHasErrors(
          this.canProceedToDetails,
          !this.isGraphicExist()
        );
        if (!this.isGraphicExist()) {
          this.projectLocationForm.controls.geometry.setValue('');
        }
        this.markAllUntouched();
        this.resetAllData();
        this.clearProjectDetails();
        this.clearProjectDetails();
        this.resetLocationForm();

        if (searchBy === 'address') {
          addressControl?.setValidators([
            Validators.required,
            Validators.minLength(5),
            Validators.maxLength(100),
          ]);
          cityControl?.setValidators([
            Validators.required,
            Validators.minLength(3),
            Validators.maxLength(65),
          ]);
          zipControl?.setValidators([Validators.minLength(5)]);
          idValueControl?.setValidators(null);
          taxMapNumberControl?.setValidators(null);
          countyControl?.setValidators(null);
          municipalityControl?.setValidators(null);
        }
        if (
          searchBy === 'dec_id' ||
          searchBy === 'spdes_id' ||
          searchBy === 'mine_land_id' ||
          searchBy === 'solid_waste_id'
        ) {
          addressControl?.setValidators(null);
          cityControl?.setValidators(null);
          zipControl?.setValidators(null);
          idValueControl?.setValue('');
          if (searchBy === 'dec_id') {
            idValueControl?.setValidators([
              Validators.required,
              Validators.minLength(12),
            ]);
          }
          if (searchBy === 'spdes_id') {
            idValueControl?.setValidators([
              Validators.required,
              Validators.minLength(9),
            ]);
          }
          if (searchBy === 'mine_land_id') {
            idValueControl?.setValidators([
              Validators.required,
              Validators.minLength(5),
            ]);
          }
          if (searchBy === 'solid_waste_id') {
            idValueControl?.setValidators([
              Validators.required,
              Validators.minLength(5),
            ]);
          }
          taxMapNumberControl?.setValidators(null);
          countyControl?.setValidators(null);
          municipalityControl?.setValidators(null);
        }

        if (searchBy === 'taxmap') {
          addressControl?.setValidators(null);
          cityControl?.setValidators(null);
          zipControl?.setValidators(null);
          idValueControl?.setValidators(null);
          taxMapNumberControl?.setValidators([Validators.required]);
          countyControl?.setValidators([Validators.required]);
          municipalityControl?.setValidators([Validators.required]);
        }
        if (searchBy === 'zoom' || searchBy === 'shapefile') {
          addressControl?.setValidators(null);
          cityControl?.setValidators(null);
          zipControl?.setValidators(null);
          idValueControl?.setValidators(null);
          taxMapNumberControl?.setValidators(null);
          countyControl?.setValidators(null);
          municipalityControl?.setValidators(null);
        }
        addressControl?.updateValueAndValidity();
        cityControl?.updateValueAndValidity();
        zipControl?.updateValueAndValidity();
        idValueControl?.updateValueAndValidity();
        taxMapNumberControl?.updateValueAndValidity();
        countyControl?.updateValueAndValidity();
        municipalityControl?.updateValueAndValidity();
      });
  }

  get isDisableNext(): boolean {
    return this.canProceedToDetails.invalid;
  }

  get isFacilityGeometry(): boolean {
    let isFacilityGraphicExist = false;
    if (
      this.facilityGeometry?.geometry !== undefined &&
      this.facilityGeometry?.geometry !== null
    ) {
      isFacilityGraphicExist = true;
    }
    return isFacilityGraphicExist;
  }
  get isFinalSketchGeometry(): boolean {
    let isFinalSketchGraphicExist = false;
    if (
      this.finalSketchGraphic?.geometry !== undefined &&
      this.finalSketchGraphic?.geometry !== null
    ) {
      isFinalSketchGraphicExist = true;
    }
    return isFinalSketchGraphicExist;
  }
  get isFinalTaxGeometry(): boolean {
    let isFinalTaxGraphicExist = false;
    if (
      this.finalTaxGraphic?.geometry !== undefined &&
      this.finalTaxGraphic?.geometry !== null
    ) {
      isFinalTaxGraphicExist = true;
    }
    return isFinalTaxGraphicExist;
  }

  get isFinalTaxMapGeometry(): boolean {
    let isFinalTaxMapGraphicExist = false;
    if (
      this.finalTaxMapGraphic?.geometry !== undefined &&
      this.finalTaxMapGraphic?.geometry !== null
    ) {
      isFinalTaxMapGraphicExist = true;
    }
    return isFinalTaxMapGraphicExist;
  }

  get isFinalShapeFileGeometry(): boolean {
    let isFinalShapeFileGraphicExist = false;
    if (
      this.finalShapeFileGraphic?.geometry !== undefined &&
      this.finalShapeFileGraphic?.geometry !== null
    ) {
      isFinalShapeFileGraphicExist = true;
    }
    return isFinalShapeFileGraphicExist;
  }
  // search by list filter based on yes or no selection on prior permit value
  get filteredSearchTypes(): SearchType[] {
    return this.searchtypes.filter(
      (v) => v.priorPermit === this.projectLocationForm.value.priorPermit
    );
  }
  // Mask format for ID
  get maskForId(): string {
    let mask = '';
    if (this.projectLocationForm.controls.searchBy.value === 'dec_id') {
      mask = '0-0000-00000';
    }
    if (this.projectLocationForm.controls.searchBy.value === 'spdes_id') {
      mask = 'AAAAAAA';
    }
    if (this.projectLocationForm.controls.searchBy.value === 'mine_land_id') {
      mask = '00000';
    }
    if (this.projectLocationForm.controls.searchBy.value === 'solid_waste_id') {
      mask = 'AAAAAAAA';
    }
    return mask;
  }

  // Placeholder for ID
  get placeholderForId(): string {
    let placeholder = '';
    if (this.projectLocationForm.controls.searchBy.value === 'dec_id') {
      placeholder = '9-9999-99999';
    }
    if (this.projectLocationForm.controls.searchBy.value === 'mine_land_id') {
      placeholder = '99999';
    }
    return placeholder;
  }
  // Prefix as NY for SPDES ID
  get prefixForId(): string {
    return this.projectLocationForm.controls.searchBy.value === 'spdes_id'
      ? 'NY'
      : '';
  }

  //clear data on address not applicable option
  onNotApplicable(event: any): void {
    this.gisMap.resetExtent();
    this.resetAllGeometry();
    this.notApplicable = event.currentTarget.checked;
    if (this.notApplicable) {
      this.getGeoCode(
        this.projectLocationForm.controls.street.value?.trim(),
        this.projectLocationForm.controls.city.value?.trim().substring(0,65),
        this.projectLocationForm.controls.zip.value?.trim()
      ).then((geoCodedAddress) => {
        if (
          geoCodedAddress?.x !== undefined &&
          geoCodedAddress?.y !== undefined
        ) {
          this.gisMap.gotoGeoCodedAddress(geoCodedAddress.x, geoCodedAddress.y);
        }
      });
    }
  }
  isNotApplicable(): boolean {
    return this.notApplicable;
  }

  onTaxParcelNotAvailable(event: any): void {
    this.gisMap.resetExtent();
    this.resetAllGeometry();
    this.utils.emitLoadingEmitter(true);
    this.taxParcelNotApplicable = event.currentTarget.checked;
    if (this.taxParcelNotApplicable) {
      this.taxParcelLoading = true;
      this.gisService
        .getTaxParcel(this.projectLocationForm.value.taxMapNumber,
          this.projectLocationForm.value.county?.replace(/ /g, ''),
          this.projectLocationForm.value.municipality).subscribe(
            (response: any) => {
              this.utils.emitLoadingEmitter(false);
              if (response === undefined || response === null || response.features.length === 0) {
                if (this.selectedMunicipality !== undefined && this.selectedMunicipality !== null && this.selectedMunicipality !== '') {
                  this.gisMap.zoomToMunicipalityGeometry(this.selectedMunicipality, this.selectedCounty);
                }
                else {
                  this.gisMap.zoomToCountyGeometry(this.selectedCounty);
                }
                return;
              }
              let taxParcel = new TaxMap();
              taxParcel.geometry = response.features[0].geometry;
              this.gisMap.gotoTaxParcel(taxParcel);
              this.taxParcelLoading = false;
            }, (error: any) => {
              this.utils.emitLoadingEmitter(false);
              this.taxParcelLoading = false;
              //this.serviceError = true;
              this.handleProjectLocationServiceError(error);
            });
    } else {
      this.utils.emitLoadingEmitter(false);
      this.taxParcelLoading = false;
    }
  }

  private handleProjectLocationServiceError(error: any) {
    this.projectLocationServiceErrorMessage = this.errorService.getServerMessage(error);
    this.projectLocationServiceError = true;
    throw error;
  }

  private handleValidationError(message: string) {
    this.projectLocationServiceError = true;
    if (message !== undefined && message?.trim() !== '') {
      this.projectLocationServiceErrorMessage = message;
    } else {
      this.projectLocationServiceErrorMessage = this.errorMsgObj?.UNABLE_TO_PROCESS_NOW;
    }
  }
  isTaxParcelNotApplicable(): boolean {
    return this.taxParcelNotApplicable;
  }

  onPriorPermitSelectChange(): void {
    this.projectLocationForm.controls.searchBy.setValue('');
  }

  onSelectCountyChange(event: any): void {
    this.selectedCounty = event.target.value;
    this.projectLocationForm.controls.municipality.setValue('');
    this.projectLocationForm.controls.taxMapNumber.setValue('');
    if (!this.NYC.includes(this.selectedCounty?.trim())) {
      this.projectLocationForm.get('municipality')?.setValidators([Validators.required]);
      this.getMunicipalitiesInCounty();
      this.projectLocationForm.get('municipality')?.updateValueAndValidity();
    } else {
      this.projectLocationForm.get('municipality')?.clearValidators();
      this.projectLocationForm.get('municipality')?.updateValueAndValidity();
    }
  }

  onZoomCountyChange(event: any): void {
    if (event.target.value !== undefined && event.target.value !== '') {
      this.gisMap.zoomToCountyGeometry(event.target.value);
    } else {
      this.gisMap.resetExtent();
    }
  }
  onSelectMunicipalityChange(event: any): void {
    this.selectedMunicipality = event.target.value;
    this.isTaxMapSearched = false;
  }

  public async onAddressSubmit(): Promise<void> {
    this.resetProjectLocationErrors();
    this.isAddressSearched = true;
    this.notApplicable = false;
    this.resetAllGeometry();
    if (this.projectLocationForm.invalid) {
      this.projectLocationForm.controls.street.markAsTouched();
      this.projectLocationForm.controls.city.markAsTouched();
      return;
    }
    this.utils.emitLoadingEmitter(true);
    this.addressFacilities = new Array<AddressResponse>();
    this.approvedFacilities = new Array<ApprovedFacility>();
    this.selectedAddress = new AddressResponse();
    this.finalSelectedAddress = new AddressResponse();
    this.gisService
      .getFacilityByAddress(
        this.projectLocationForm.value.street?.trim(),
        this.projectLocationForm.value.city?.trim().substring(0,65),
      )
      .subscribe(
        (responseData: any) => {
          if (responseData !== undefined && responseData.length > 0) {
            responseData?.forEach((facility: any) => {
              this.addressFacilities.push(this.buildAddressResponse(facility));
            });
            if (this.addressFacilities?.length === 1) {
              this.selectedAddress = this.addressFacilities[0];
              if (this.selectedFacility !== undefined) {
                this.selectedFacility = new ApprovedFacility();
              }
              this.getDECPolygonForId(this.addressFacilities[0].decIdFormatted);
            }
          }
          this.isSubmitted = true;
          this.setControlHasErrors(
            this.canProceedToDetails,
            !this.isGraphicExist()
          );
          if (!this.isGraphicExist()) {
            this.projectLocationForm.controls.geometry.setValue('');
          }
          this.utils.emitLoadingEmitter(false);
        },
        (error: any) => {
          //this.serviceError = true;
          this.utils.emitLoadingEmitter(false);
          this.handleProjectLocationServiceError(error);
        }
      );
  }

  private buildAddressResponse(facilityByAddress: any): AddressResponse {
    let addressResponse = new AddressResponse();
    addressResponse.decIdFormatted = facilityByAddress.decIdFormatted;
    addressResponse.facilityName = facilityByAddress.districtName;
    addressResponse.districtId = facilityByAddress.districtId;
    addressResponse.standardCode = facilityByAddress.standardCode;
    if(facilityByAddress.locationDirections!== undefined && facilityByAddress.locationDirections!== null){
      let directions=facilityByAddress.locationDirections?.split('|');
      addressResponse.locationDirections = directions[0];
    }
    addressResponse.city = facilityByAddress.city.trim().substring(0,65);
    addressResponse.state = facilityByAddress.state;
    addressResponse.zip = facilityByAddress.zip;
    addressResponse.zipExtension = facilityByAddress.zipExtension;
    addressResponse.lastKnownAppl = facilityByAddress.lastKnownAppl;
    if (facilityByAddress.longLat !== undefined && facilityByAddress.longLat !== null) {
      let longLat = facilityByAddress.longLat.split('^');
      addressResponse.long = '-' + longLat[0];
      addressResponse.lat = longLat[1];
    }
    return addressResponse;
  }

  private async markAllUntouched(): Promise<void> {
    this.projectLocationForm.controls.street.markAsUntouched();
    this.projectLocationForm.controls.city.markAsUntouched();
    this.projectLocationForm.controls.zip.markAsUntouched();
    this.projectLocationForm.controls.idValue.markAsUntouched();
    this.projectLocationForm.controls.county.markAsUntouched();
    this.projectLocationForm.controls.municipality.markAsUntouched();
    this.projectLocationForm.controls.taxMapNumber.markAsUntouched();
  }

  private async clearProjectDetails(): Promise<void> {
    this.projectLocationDetailsForm.controls.decId.setValue('');
    this.projectLocationDetailsForm.controls.projectName.setValue('');
    this.projectLocationDetailsForm.controls.address1.setValue('');
    this.projectLocationDetailsForm.controls.address2.setValue('');
    this.projectLocationDetailsForm.controls.city.setValue('');
    this.projectLocationDetailsForm.controls.zip.setValue('');
    this.projectLocationDetailsForm.controls.direction.setValue('');
    this.projectLocationDetailsForm.controls.reason.setValue('');
    this.projectLocationDetailsForm.controls.boundaryChangeReason.setValue('');
    this.projectLocationDetailsForm.controls.municipality.setValue('');
    this.projectLocationDetailsForm.controls.municipalityNames.setValue('');
    this.projectLocationDetailsForm.controls.municipalitySwis.setValue('');
    this.projectLocationDetailsForm.controls.primaryMunicipality.setValue('');
    this.projectLocationDetailsForm.controls.county.setValue('');
    this.projectLocationDetailsForm.controls.countyNames.setValue('');
    this.projectLocationDetailsForm.controls.countySwis.setValue('');
    this.projectLocationDetailsForm.controls.taxMap.setValue('');
    this.projectLocationDetailsForm.controls.latitude.setValue('');
    this.projectLocationDetailsForm.controls.longitude.setValue('');
    this.projectLocationDetailsForm.controls.nytmx.setValue('');
    this.projectLocationDetailsForm.controls.nytmy.setValue('');
    this.projectLocationDetailsForm.controls.region.setValue('');
    this.projectLocationDetailsForm.controls.primaryRegion.setValue('');
    this.projectLocationDetailsForm.controls.workAreaId.setValue('');
  }
  public async clearAddressSearch(): Promise<void> {
    this.projectLocationForm.controls.street.setValue('');
    this.projectLocationForm.controls.city.setValue('');
    this.projectLocationForm.controls.zip.setValue('');
    this.projectLocationForm.controls.street.markAsPristine();
    this.projectLocationForm.controls.city.markAsPristine();
    this.projectLocationForm.controls.zip.markAsPristine();
    this.projectLocationForm.controls.street.markAsUntouched();
    this.projectLocationForm.controls.city.markAsUntouched();
    this.projectLocationForm.controls.zip.markAsUntouched();
    this.isAddressSearched = false;
    this.notApplicable = false;
    this.resetAllData();
    this.clearProjectDetails();
    this.resetAllGeometry();
    this.setControlHasErrors(this.canProceedToDetails, !this.isGraphicExist());
    if (!this.isGraphicExist()) {
      this.projectLocationForm.controls.geometry.setValue('');
    }
  }
  public async clearId(): Promise<void> {
    this.projectLocationForm.controls.idValue.setValue('');
    this.projectLocationForm.controls.idValue.markAsPristine();
    this.projectLocationForm.controls.idValue.markAsUntouched();
    this.isFacilitySearchedByDecId = false;
    this.isSearchedByIdAttempted = false;
    this.resetAllData();
    this.clearProjectDetails();
    this.resetAllGeometry();
    this.setControlHasErrors(this.canProceedToDetails, !this.isGraphicExist());
    if (!this.isGraphicExist()) {
      this.projectLocationForm.controls.geometry.setValue('');
    }
  }

  public async clearTaxMap(): Promise<void> {
    this.projectLocationForm.controls.county.setValue('');
    this.projectLocationForm.controls.municipality.setValue('');
    this.projectLocationForm.controls.taxMapNumber.setValue('');
    this.projectLocationForm.controls.county.markAsPristine();
    this.projectLocationForm.controls.municipality.markAsPristine();
    this.projectLocationForm.controls.taxMapNumber.markAsPristine();
    this.projectLocationForm.controls.county.markAsUntouched();
    this.projectLocationForm.controls.municipality.markAsUntouched();
    this.projectLocationForm.controls.taxMapNumber.markAsUntouched();
    this.isTaxMapSearched = false;
    this.taxParcelLoading = false;
    this.taxParcelNotApplicable = false;
    this.municipalities = new Set<string>();
    this.resetAllData();
    this.clearProjectDetails();
    this.resetAllGeometry();
    this.setControlHasErrors(this.canProceedToDetails, !this.isGraphicExist());
    if (!this.isGraphicExist()) {
      this.projectLocationForm.controls.geometry.setValue('');
    }
  }

  public async clearShapeFile(): Promise<void> {
    this.shapeFile.nativeElement.value = '';
    this.uploadStatus.nativeElement.innerHTML = '';
    this.resetAllData();
    this.clearProjectDetails();
    this.resetAllGeometry();
    this.setControlHasErrors(this.canProceedToDetails, !this.isGraphicExist());
    if (!this.isGraphicExist()) {
      this.projectLocationForm.controls.geometry.setValue('');
    }
  }

  public getSearchByValue(): string {
    let searchBy = this.searchtypes.find(
      (x) => x.id === this.projectLocationForm.value.searchBy
    );
    if (searchBy !== undefined) {
      return searchBy.name;
    } else {
      return '';
    }
  }

  public onFacilitySelectForId(event: any): void {
    this.facilityGeometry = event.data.geometry;
    this.selectedFacility = event.data;
    this.approvedFacility = event.data;
    if (this.approvedFacility.isValidLocation === 1) {
      this.setControlHasErrors(this.canProceedToDetails, false);
      this.projectLocationForm.controls.geometry.setValue('1');
    }
  }

  public onFacilitySelectForTaxMap(event: any): void {
    this.isTaxMapSearched = true;
    this.facilityGeometry = event.data.geometry;
    this.selectedFacility = event.data;
    this.approvedFacility = event.data;
    this.setControlHasErrors(this.canProceedToDetails, false);
    this.projectLocationForm.controls.geometry.setValue('1');
  }

  public onFacilityUnselectForId(event: any): void {
    this.selectedFacility = new ApprovedFacility();
    this.facilityGeometry = new Graphic();
    this.setControlHasErrors(this.canProceedToDetails, !this.isGraphicExist());
    if (!this.isGraphicExist()) {
      this.projectLocationForm.controls.geometry.setValue('');
    }
  }

  public onFacilityUnselectForTaxMap(event: any): void {
    this.isTaxMapSearched = false;
    this.selectedFacility = new ApprovedFacility();
    this.facilityGeometry = new Graphic();
    this.setControlHasErrors(this.canProceedToDetails, !this.isGraphicExist());
    if (!this.isGraphicExist()) {
      this.projectLocationForm.controls.geometry.setValue('');
    }
  }

  public async onAddressUnselect(event: any): Promise<void> {
    this.finalSelectedAddress = new AddressResponse();
    this.selectedFacility = new ApprovedFacility();
    this.selectedAddress = new AddressResponse();
    this.finalSelectedAddress = new AddressResponse();
    this.facilityGeometry = new Graphic();
    this.resetAllGeometry();
    this.setControlHasErrors(this.canProceedToDetails, !this.isGraphicExist());
    if (!this.isGraphicExist()) {
      this.projectLocationForm.controls.geometry.setValue('');
    }
    this.isFacilitySearched = false;
  }

  public async onFacilitiesAtGeometryUnselect(event: any): Promise<void> {
    this.selectedFacilityAtGeometry = new ApprovedFacility();
  }

  private async getDECPolygonForId(decId: string): Promise<void> {
    this.utils.emitLoadingEmitter(true);
    this.gisService.getDECPolygonByDecId(decId).subscribe(
      async (data: any) => {
        if (data.features.length === 0) {
          this.approvedFacility = new ApprovedFacility();
          this.setControlHasErrors(
            this.canProceedToDetails,
            !this.isGraphicExist()
          );
          if (!this.isGraphicExist()) {
            this.projectLocationForm.controls.geometry.setValue('');
          }
          await this.updateGeocode(this.selectedAddress);
          this.finalSelectedAddress = this.selectedAddress;
          this.utils.emitLoadingEmitter(false);
        }
        if (data.features.length > 0) {
          if((data.features[0] as Polygon) !==undefined && (data.features[0] as Polygon).centroid !==undefined){
          let latitude = (data.features[0] as Polygon).centroid?.y;
          let longitude = (data.features[0] as Polygon).centroid?.x;
          this.selectedAddress.lat = latitude?.toString();
          this.selectedAddress.long = longitude?.toString();
          projection.load().then(() => {
            let point = (projection.project((data.features[0] as Polygon).centroid, this.NAD83) as Point);
            this.selectedAddress.nytmx = point?.x;
            this.selectedAddress.nytmy = point?.y;
          });
          this.selectedAddress.geometry = data.features[0];
          this.selectedAddress.wkid = data.spatialReference.wkid;
          if( data.features[0].attributes.VALIDATED_LOCATION !==1){
            this.selectedAddress.isValidLocation = 0;
          }else{
            this.selectedAddress.isValidLocation =
            data.features[0].attributes.VALIDATED_LOCATION;
          }
          let taxMapNumber = await this.gisService.findTaxParcelAt(
            longitude,
            latitude
          );
          this.selectedAddress.taxMapNumber = taxMapNumber;
          this.approvedFacility = this.buildApprovedFacility(
            data.features[0],
            data.spatialReference.wkid,
            latitude,
            longitude
          );
          this.approvedFacility.taxMapNumber = taxMapNumber;
          if (this.selectedAddress.isValidLocation == 1) {
            this.setControlHasErrors(this.canProceedToDetails, false);
            this.projectLocationForm.controls.geometry.setValue('1');
          } else {
            this.setControlHasErrors(this.canProceedToDetails, true);
            this.projectLocationForm.controls.geometry.setValue('');
          }
          this.finalSelectedAddress = this.selectedAddress;
          this.utils.emitLoadingEmitter(false);
        }else{
          this.approvedFacility = new ApprovedFacility();
          this.setControlHasErrors(
            this.canProceedToDetails,
            !this.isGraphicExist()
          );
          if (!this.isGraphicExist()) {
            this.projectLocationForm.controls.geometry.setValue('');
          }
          await this.updateGeocode(this.selectedAddress);
          this.finalSelectedAddress = this.selectedAddress;
          this.utils.emitLoadingEmitter(false);
        }
        }
      },
      (error: any) => {
        this.utils.emitLoadingEmitter(false);
        this.handleProjectLocationServiceError(error);
      }
    );
  }

  private async updateGeocode(address: AddressResponse): Promise<void> {
    return await this.getGeoCode(
      address.locationDirections?.split('|')[0].trim(),
      address.city?.trim().substring(0,65), address.zip?.trim()
    ).then((geoCodedAddress) => {
      if (
        geoCodedAddress?.x !== undefined &&
        geoCodedAddress?.y !== undefined
      ) {
        address.long = geoCodedAddress.x.toString();
        address.lat = geoCodedAddress.y.toString();
        address.isValidLocation = 0;
      }
    });
  }

  public async onAddressSelect(event: any): Promise<void> {
    this.notApplicable = false;
    let addressResponse = event.data;
    this.facilityGeometry = new Graphic();
    if (this.selectedFacility !== undefined) {
      this.selectedFacility = new ApprovedFacility();
    }
    this.getDECPolygonForId(addressResponse.decIdFormatted);
  }

  public async taxMapSearch(): Promise<void> {
    this.resetProjectLocationErrors();
    this.taxParcelLoading = false;
    this.taxParcelNotApplicable = false;
    this.gisMap.resetExtent();
    this.setControlHasErrors(this.canProceedToDetails, !this.isGraphicExist());
    if (!this.isGraphicExist()) {
      this.projectLocationForm.controls.geometry.setValue('');
    }
    if (this.projectLocationForm.invalid) {
      this.projectLocationForm.controls.county.markAsTouched();
      this.projectLocationForm.controls.municipality.markAsTouched();
      this.projectLocationForm.controls.taxMapNumber.markAsTouched();
      return;
    }
    this.approvedFacilitiesForTaxMap = new Array<ApprovedFacility>();
    this.codedAddressesForTaxMap = new Array<AddressResponse>();
    if (this.selectedFacilityForTaxMap !== undefined) {
      this.selectedFacilityForTaxMap = new ApprovedFacility();
    }

    this.utils.emitLoadingEmitter(true);
    this.gisService
      .getFacilityByTaxMap(
        this.projectLocationForm.value.county,
        this.projectLocationForm.value.municipality,
        this.projectLocationForm.value.taxMapNumber
      )
      .subscribe(
        (response: any) => {
          if (response === null || response ===undefined) {
            this.isTaxMapSearched = true;
            this.utils.emitLoadingEmitter(false);
            return;
          }
          if (response.length >0) {
          this.taxMapResponse = this.buildAddressResponse(response[0]);
            this.gisService
              .getDECPolygonByDecId(response[0]?.decIdFormatted)
              .subscribe(
                async (data: any) => {
                  if (data.features?.length > 0) {
                    await this.buildFacilityForTaxMap(data);
                  } else {
                    await this.buildAddressResponseForTaxMap();
                  }
                  this.isTaxMapSearched = true;
                  this.utils.emitLoadingEmitter(false);
                },
                (error: any) => {
                  //this.serviceError = true;
                  this.utils.emitLoadingEmitter(false);
                  this.handleProjectLocationServiceError(error);
                }
              );
          }else{
            this.isTaxMapSearched = true;
          }
        },
        (error: any) => {
          //this.serviceError = true;
          this.utils.emitLoadingEmitter(false);
          this.handleProjectLocationServiceError(error);
        }
      );
  }
  private async buildFacilityForTaxMap(facilityResults: any): Promise<void> {
    if((facilityResults.features[0] as Polygon) !==undefined && (facilityResults.features[0] as Polygon).centroid !==undefined){
    let latitude = (facilityResults.features[0] as Polygon).centroid?.y;
    let longitude = (facilityResults.features[0] as Polygon).centroid?.x;
    let approvedFacility = this.buildApprovedFacility(
      facilityResults.features[0],
      facilityResults.spatialReference.wkid,
      latitude,
      longitude
    );
    approvedFacility.lastKnownAppl =
      this.taxMapResponse.lastKnownAppl;
    approvedFacility.taxMapNumber =
      await this.gisService.findTaxParcelAt(longitude, latitude);
    this.approvedFacilitiesForTaxMap.push(approvedFacility);
    if (this.approvedFacilitiesForTaxMap?.length === 1) {
      this.approvedFacility = this.approvedFacilitiesForTaxMap[0];
      this.selectedFacilityForTaxMap =this.approvedFacilitiesForTaxMap[0];
      this.selectedFacility = this.approvedFacilitiesForTaxMap[0];
      if (this.approvedFacility.isValidLocation === 1) {
        this.setControlHasErrors(this.canProceedToDetails, false);
        this.projectLocationForm.controls.geometry.setValue('1');
      }
    }
  }else{
    await this.buildAddressResponseForTaxMap();
  }
  }

  private async buildAddressResponseForTaxMap(): Promise<void> {
    let addressResponse = new AddressResponse();
    addressResponse.decIdFormatted =
      this.taxMapResponse.decIdFormatted;
    addressResponse.standardCode = this.taxMapResponse.standardCode;
    addressResponse.districtId = this.taxMapResponse.districtId;
    addressResponse.facilityName =
      this.taxMapResponse.facilityName;
    if(this.taxMapResponse.locationDirections!==undefined && this.taxMapResponse.locationDirections!== null){
      let directions= this.taxMapResponse.locationDirections.split('|');
      addressResponse.locationDirections =directions[0];
    }

    addressResponse.city = this.taxMapResponse.city.trim().substring(0,65);
    addressResponse.state = this.taxMapResponse.state;
    addressResponse.zip = this.taxMapResponse.zip;
    addressResponse.zipExtension =
      this.taxMapResponse.zipExtension;
    addressResponse.lastKnownAppl =
      this.taxMapResponse.lastKnownAppl;
    await this.getGeoCode(
      addressResponse.locationDirections?.trim(),
      this.taxMapResponse.city?.trim().substring(0,65),
      this.taxMapResponse.zip?.trim()
    ).then((geoCodedAddress) => {
      if (
        geoCodedAddress?.x !== undefined &&
        geoCodedAddress?.y !== undefined
      ) {
        addressResponse.long = geoCodedAddress.x.toString();
        addressResponse.lat = geoCodedAddress.y.toString();
      }
    });
    this.codedAddressesForTaxMap.push(addressResponse);
    if (this.codedAddressesForTaxMap?.length === 1) {
      this.finalSelectedAddress = this.codedAddressesForTaxMap[0];
    }
  }


  private clearErrors() {
    this.serviceError = false;
    this.serviceErrorMessage = '';
  }

  public async onTaxMapSelect(event: any): Promise<void> {
    this.setControlHasErrors(this.canProceedToDetails, false);
    this.projectLocationForm.controls.geometry.setValue('1');
    //Needs to code this and figure out how to get data from enterprise data servcie approved polygon
  }

  public async onTaxMapUnSelect(event: any): Promise<void> {
    this.selectedFacilityForTaxMap = new ApprovedFacility();
    this.approvedFacilities = new Array<ApprovedFacility>();
    this.selectedFacility = new ApprovedFacility();
    this.isFacilitySearched = false;
    if (!this.isGraphicExist()) {
      this.projectLocationForm.controls.geometry.setValue('');
    }
    this.setControlHasErrors(this.canProceedToDetails, !this.isGraphicExist());
  }

  private async getGeoCode(address: string, city: string, zip: string): Promise<GeoCodedAddress> {
    let codedAddress = new GeoCodedAddress();
    let responseData = await this.gisService
      .getEsriAddresses(
        address?.replace(/&/g, 'and').replace(/[^a-zA-Z0-9 ]/g, ''), city?.replace(/&/g, 'and').replace(/[^a-zA-Z0-9 ]/g, ''), zip
      )
      .toPromise();
    if (responseData !== undefined && responseData.candidates?.length > 0) {
      codedAddress.x = responseData.candidates[0].location.x;
      codedAddress.y = responseData.candidates[0].location.y;
    }
    return codedAddress;
  }

  private isIdSearchValid(): boolean {
    let isValid = true;
    if (this.isSearchedByIdAttempted) {
      if (
        this.idResponse?.facilityName === '' ||
        this.idResponse?.facilityName === null ||
        this.idResponse?.facilityName === undefined
      ) {
        isValid = false;
      }
      if (
        this.approvedFacilitiesForDecId?.length > 0 &&
        this.selectedFacilityForDecId?.SITE_ID === undefined
      ) {
        isValid = false;
      }
      if (
        this.codedAddressesForId?.length > 0 &&
        this.finalSelectedAddress?.standardCode === undefined
      ) {
        isValid = false;
      }
    }
    return isValid;
  }

  private isTaxMapSearchValid(): boolean {
    let isValid = true;
    if (this.isTaxMapSearched) {
      if (
        this.approvedFacilitiesForTaxMap?.length > 0 &&
        this.selectedFacilityForTaxMap?.SITE_ID === undefined
      ) {
        isValid = false;
      }
      if (
        this.codedAddressesForTaxMap?.length > 0 &&
        this.finalSelectedAddress?.standardCode === undefined
      ) {
        isValid = false;
      }
    }
    return isValid;
  }

  async onIdFormSubmit(): Promise<void> {
    // get approved polygon data from DEC polygon Service for the entered DEC ID
    this.resetProjectLocationErrors();
    this.resetAllGeometry();
    this.isSearchedByIdAttempted = true;
    this.isFacilitySearchedByDecId = false;
    this.idResponse = new IdResponse();
    if (this.projectLocationForm.invalid) {
      this.projectLocationForm.controls.idValue.markAsTouched();
      return;
    }
    this.approvedFacilitiesForDecId = new Array<ApprovedFacility>();
    this.codedAddressesForId = new Array<AddressResponse>();
    this.selectedFacilityForDecId = new ApprovedFacility();
    this.utils.emitLoadingEmitter(true);
    this.clearErrors();
    this.gisService
      .getAddressById(
        this.getProgramType(),
        this.projectLocationForm.controls.idValue.value
      )
      .subscribe(
        (response: any) => {
          if (response === null) {
            this.utils.emitLoadingEmitter(false);
            this.isFacilitySearchedByDecId = true;
            return;
          }
          this.idResponse = this.buildIdResponse(response);
          this.gisService
            .getDECPolygonByDecId(response?.decIdFormatted)
            .subscribe(
              async (data: any) => {
                if (data.features?.length > 0) {
                  await this.convertAndSelectFacilityForIdSearch(data);
                } else {
                  this.selectAddressForIdSearch();
                }
                this.utils.emitLoadingEmitter(false);
                this.isFacilitySearchedByDecId = true;
              },
              (error: any) => {
                this.utils.emitLoadingEmitter(false);
                this.handleProjectLocationServiceError(error);
              }
            );
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.handleProjectLocationServiceError(error);
        }
      );
  }

  private async convertAndSelectFacilityForIdSearch(serviceResult: any): Promise<void> {
    if((serviceResult?.features[0] as Polygon) !==undefined && (serviceResult?.features[0] as Polygon).centroid !==undefined){
    let latitude = (serviceResult.features[0] as Polygon).centroid?.y;
    let longitude = (serviceResult.features[0] as Polygon).centroid?.x;
    let approvedFacility = this.buildApprovedFacility(
      serviceResult.features[0],
      serviceResult.spatialReference.wkid,
      latitude,
      longitude
    );
    approvedFacility.lastKnownAppl = this.idResponse.lastKnownAppl;
    approvedFacility.taxMapNumber = await this.gisService.findTaxParcelAt(longitude, latitude);
    this.approvedFacilitiesForDecId.push(approvedFacility);
    if (this.approvedFacilitiesForDecId?.length === 1) {
      this.approvedFacility = this.approvedFacilitiesForDecId[0];
      this.selectedFacilityForDecId =
        this.approvedFacilitiesForDecId[0];
      this.selectedFacility = this.approvedFacilitiesForDecId[0];
      if (this.approvedFacility.isValidLocation === 1) {
        this.setControlHasErrors(this.canProceedToDetails, false);
        this.projectLocationForm.controls.geometry.setValue('1');
      }
    }
  }else{
    this.selectAddressForIdSearch();
  }
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
    if(facility.attributes?.LOCATION_DIRECTIONS_1 !==undefined && facility.attributes?.LOCATION_DIRECTIONS_1 !==null){
      let directions=facility.attributes?.LOCATION_DIRECTIONS_1.split('|');
      approvedFacility.LOCATION_DIRECTIONS_1 = this.titleCasePipe.transform(
        directions[0]
      );
      if(directions[1] !==undefined){
        approvedFacility.LOCATION_DIRECTIONS_2 = this.titleCasePipe.transform(
          directions[1]
        );
      }
    }
    if(approvedFacility?.LOCATION_DIRECTIONS_2 ===undefined){
      approvedFacility.LOCATION_DIRECTIONS_2 = this.titleCasePipe.transform(
        facility.attributes.LOCATION_DIRECTIONS_2
      );
    }
    approvedFacility.CITY = this.titleCasePipe.transform(
      facility.attributes.CITY.trim().substring(0,65)
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
    approvedFacility.nytmx = facility.attributes.NYTME;
    approvedFacility.nytmy = facility.attributes.NYTMN;
    if(facility.attributes.VALIDATED_LOCATION !==1){
      approvedFacility.isValidLocation =0;
    }else{
      approvedFacility.isValidLocation = facility.attributes.VALIDATED_LOCATION;
    }
    return approvedFacility;
  }


  private async selectAddressForIdSearch() {
    let addressResponse = new AddressResponse();
    addressResponse.decIdFormatted = this.idResponse.decIdFormatted;
    addressResponse.standardCode = this.idResponse.decId;
    addressResponse.districtId = this.idResponse.districtId;
    addressResponse.facilityName = this.idResponse.facilityName;
    if(this.idResponse.locationDirections!==undefined && this.idResponse.locationDirections !==null){
      let directions= this.idResponse.locationDirections.split('|');
      addressResponse.locationDirections = directions[0];
    }
    addressResponse.city = this.idResponse.city.trim().substring(0,65);
    addressResponse.state = this.idResponse.state;
    addressResponse.zip = this.idResponse.zip;
    addressResponse.zipExtension = this.idResponse.zipExtension;
    addressResponse.lastKnownAppl = this.idResponse.lastKnownAppl;
    await this.getGeoCode(
      addressResponse.locationDirections,
      this.idResponse.city.trim().substring(0,65), this.idResponse.zip
    ).then((geoCodedAddress) => {
      if (
        geoCodedAddress?.x !== undefined &&
        geoCodedAddress?.y !== undefined
      ) {
        addressResponse.long = geoCodedAddress.x.toString();
        addressResponse.lat = geoCodedAddress.y.toString();
      }
    });
    this.codedAddressesForId.push(addressResponse);
    if (this.codedAddressesForId?.length === 1) {
      this.finalSelectedAddress = this.codedAddressesForId[0];
    }
  }


  private buildIdResponse(response: any): IdResponse {
    let idResponse = new IdResponse();
    idResponse.districtId = response.districtId;
    idResponse.decId = response.standardCode;
    idResponse.decIdFormatted = response.decIdFormatted;
    idResponse.facilityName = response.districtName;
    idResponse.city = response.city.trim().substring(0,65);
    if(response.locationDirections !==undefined && response.locationDirections !==null){
      let directions =response.locationDirections.split('|');
      idResponse.locationDirections = directions[0];
    }
    idResponse.phoneNumber = response.phoneNumber;
    idResponse.projectId = response.projectId;
    idResponse.rolePrimaryInd = response.rolePrimaryInd;
    idResponse.roleTypeDesc = response.roleTypeDesc;
    idResponse.longLat = response.longLat;
    idResponse.zip = response.zip;
    idResponse.zipExtension = response.zipExtension;
    idResponse.lastKnownAppl = response.lastKnownAppl;
    return idResponse;
  }

  private isItIdSearch(): boolean {
    return (
      this.projectLocationForm.get('searchBy')?.value === 'dec_id' ||
      this.projectLocationForm.get('searchBy')?.value === 'spdes_id' ||
      this.projectLocationForm.get('searchBy')?.value === 'solid_waste_id' ||
      this.projectLocationForm.get('searchBy')?.value === 'mine_land_id'
    );
  }
  private getProgramType(): string {
    let programType = '';
    switch (this.projectLocationForm.controls.searchBy.value) {
      case 'dec_id':
        programType = 'DEC';
        break;
      case 'spdes_id':
        programType = 'SPDES';
        break;
      case 'solid_waste_id':
        programType = 'SW';
        break;
      case 'mine_land_id':
        programType = 'ML';
        break;
      case 'taxmap':
        programType = 'TXMAP';
        break;
    }
    return programType;
  }

  public async onFacilityTaxParcel(taxParcelNumber: TaxParcel): Promise<void> {
    this.approvedFacility.taxMapNumber = taxParcelNumber.taxMapNumber;
    this.selectedAddress.taxMapNumber = taxParcelNumber.taxMapNumber;
    this.selectedAddress.county = taxParcelNumber.county;
    this.selectedAddress.municipality = taxParcelNumber.municipality;
  }

  public async onSketchGeometryChange(geometry: Graphic[]): Promise<void> {
    this.sketchGeometryList = geometry;
    if (this.sketchGeometryList?.length > 0) {
      let rings: number[][][] = [];
      let spatialReference;
      this.sketchGeometryList.forEach((sketch: Graphic) => {
        if (sketch.geometry !== undefined) {
          spatialReference = sketch.geometry.spatialReference;
          (sketch.geometry as Polygon).rings.forEach((ring) => {
            rings.push(ring);
          });
        }
      });
      let polygon = new Polygon({
        rings: rings,
        spatialReference: spatialReference,
      });
      let graphic = new Graphic({
        geometry: polygon,
      });
      this.finalSketchGraphic = graphic;
      this.setControlHasErrors(this.canProceedToDetails, false);
      this.projectLocationForm.controls.geometry.setValue('1');
    } else {
      this.finalSketchGraphic = new Graphic();
      if (!this.isGraphicExist()) {
        this.projectLocationForm.controls.geometry.setValue('');
      }
      this.setControlHasErrors(
        this.canProceedToDetails,
        !this.isGraphicExist()
      );
    }
    this.resetProjectLocationErrors();
  }

  public async onWorkAreaGeometryChange(geometry: Graphic[]): Promise<void> {
    this.workAreaGeometryList = geometry;
    if (this.workAreaGeometryList?.length > 0) {
      let rings: number[][][] = [];
      let spatialReference;
      this.workAreaGeometryList.forEach((sketch: Graphic) => {
        if (sketch.geometry !== undefined) {
          spatialReference = sketch.geometry.spatialReference;
          (sketch.geometry as Polygon).rings.forEach((ring) => {
            rings.push(ring);
          });
        }
      });
      let polygon = new Polygon({
        rings: rings,
        spatialReference: spatialReference,
      });
      let graphic = new Graphic({
        geometry: polygon,
      });
      this.finalWorkAreaGraphic = graphic;
    } else {
      this.finalWorkAreaGraphic = new Graphic();
    }
    this.resetProjectLocationErrors();
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
        if (reason === 'Yes' || reason === 'No') {
          this.stepper.next();
        }
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

  private combineAllGeometry(): Graphic {
    let rings: number[][][] = [];
    let spatialReference = { wkid: 102100 };
    if (
      this.finalSketchGraphic?.geometry !== undefined &&
      this.finalSketchGraphic?.geometry !== null
    ) {
      (this.finalSketchGraphic.geometry as Polygon).rings.forEach((ring) => {
        rings.push(ring);
      });
    }
    if (
      this.finalTaxGraphic?.geometry !== undefined &&
      this.finalTaxGraphic?.geometry !== null
    ) {
      (this.finalTaxGraphic.geometry as Polygon).rings.forEach((ring) => {
        rings.push(ring);
      });
    }
    if (
      this.finalTaxMapGraphic?.geometry !== undefined &&
      this.finalTaxMapGraphic?.geometry !== null
    ) {
      (this.finalTaxMapGraphic.geometry as Polygon).rings.forEach((ring) => {
        rings.push(ring);
      });
    }
    if (
      this.facilityGeometry?.geometry !== undefined &&
      this.facilityGeometry?.geometry !== null
    ) {
      (this.facilityGeometry.geometry as Polygon).rings.forEach((ring) => {
        rings.push(ring);
      });
    }
    if (
      this.finalShapeFileGraphic?.geometry !== undefined &&
      this.finalShapeFileGraphic?.geometry !== null
    ) {
      (this.finalShapeFileGraphic.geometry as Polygon).rings.forEach((ring) => {
        rings.push(ring);
      });
    }
    let polygon = new Polygon({
      rings: rings,
      spatialReference: spatialReference,
    });
    let graphic = new Graphic({
      geometry: polygon,
    });
    return graphic;
  }

  public isFacilityBoundaryModified(): boolean {
    return (
      ((this.finalSketchGraphic?.geometry !== undefined &&
        this.finalSketchGraphic?.geometry !== null) ||
        (this.finalTaxGraphic?.geometry !== undefined &&
          this.finalTaxGraphic?.geometry !== null) ||
        (this.finalTaxMapGraphic?.geometry !== undefined &&
          this.finalTaxMapGraphic?.geometry !== null) ||
        (this.finalShapeFileGraphic?.geometry !== undefined &&
          this.finalShapeFileGraphic?.geometry !== null)) &&
      this.facilityGeometry?.geometry !== undefined &&
      this.facilityGeometry?.geometry !== null
    );
  }

  public isGraphicExist(): boolean {
    return (
      this.isFinalTaxGeometry ||
      this.isFinalSketchGeometry ||
      this.isFacilityGeometry ||
      this.isFinalTaxMapGeometry ||
      this.isFinalShapeFileGeometry
    );
  }

  private isValidAddressGeometry(): boolean {
    let isValid = true;
    if (
      this.projectLocationForm.controls.searchBy.value === 'address' &&
      this.finalSelectedAddress?.decIdFormatted === undefined &&
      !this.notApplicable
    ) {
      isValid = false;
    }
    return isValid;
  }

  public async onTaxMapGeometryChange(geometry: Graphic[]): Promise<void> {
    this.resetProjectLocationErrors();
    this.taxMapGeometryList = geometry;
    if (this.taxMapGeometryList?.length > 0) {
      let graphic = this.buildGraphicFromArray(this.taxMapGeometryList);
      this.finalTaxMapGraphic = graphic;
      this.setControlHasErrors(this.canProceedToDetails, false);
      this.projectLocationForm.controls.geometry.setValue('1');
    } else {
      this.finalTaxMapGraphic = new Graphic();
      this.setControlHasErrors(
        this.canProceedToDetails,
        !this.isGraphicExist()
      );
    }
  }

  private buildGraphicFromArray(graphicsList: Graphic[]): Graphic {
    let rings: number[][][] = [];
    let spatialReference;
    let attributes;
    graphicsList.forEach((sketch: Graphic) => {
      attributes = sketch.attributes;
      if (sketch.geometry !== undefined) {
        spatialReference = sketch.geometry.spatialReference;
        (sketch.geometry as Polygon).rings.forEach((ring) => {
          rings.push(ring);
        });
      }
    });
    let polygon = new Polygon({
      rings: rings,
      spatialReference: spatialReference,
    });
    let graphic = new Graphic({
      geometry: polygon,
      attributes: attributes,
    });
    return graphic;
  }
  public async updateFacilityGeometry(graphics: Graphic[]): Promise<void> {
    this.resetProjectLocationErrors();
    this.facilityGeometryList = graphics;
    if (this.facilityGeometryList?.length > 0) {
      let graphic = this.buildGraphicFromArray(this.facilityGeometryList);
      this.facilityGeometry = graphic;
      this.approvedFacility.wkid = graphic.geometry.spatialReference.wkid;
      this.approvedFacility.geometry = graphic.geometry;
      let latitude = (this.facilityGeometry.geometry as Polygon).centroid
        ?.latitude;
      let longitude = (this.facilityGeometry.geometry as Polygon).centroid
        ?.longitude;
      this.approvedFacility.taxMapNumber = await this.gisService.findTaxParcelAt(
        longitude,
        latitude
      );
    } else {
      this.facilityGeometry = new Graphic();
      this.setControlHasErrors(
        this.canProceedToDetails,
        !this.isGraphicExist()
      );
    }
  }

  public async onFacilityGeometryChange(graphics: Graphic[]): Promise<void> {
    this.resetProjectLocationErrors();
    this.facilityGeometryList = graphics;
    if (this.facilityGeometryList?.length > 0) {
      let rings: number[][][] = [];
      let spatialReference;
      let attributes;
      this.facilityGeometryList.forEach((sketch: Graphic) => {
        attributes = sketch.attributes;
        if (sketch.geometry !== undefined) {
          spatialReference = sketch.geometry.spatialReference;
          (sketch.geometry as Polygon).rings.forEach((ring) => {
            rings.push(ring);
          });
        }
      });
      let polygon = new Polygon({
        rings: rings,
        spatialReference: spatialReference,
      });
      let graphic = new Graphic({
        geometry: polygon,
        attributes: attributes,
      });
      this.facilityGeometry = graphic;
      this.setControlHasErrors(this.canProceedToDetails, false);
      this.projectLocationForm.controls.geometry.setValue('1');
      let latitude = (this.facilityGeometry.geometry as Polygon).centroid
        ?.latitude;
      let longitude = (this.facilityGeometry.geometry as Polygon).centroid
        ?.longitude;
      this.approvedFacility = this.buildApprovedFacility(
        this.facilityGeometry,
        graphic.geometry.spatialReference.wkid,
        latitude,
        longitude
      );
      this.approvedFacility.taxMapNumber = await this.gisService.findTaxParcelAt(
        longitude,
        latitude
      );
    } else {
      this.approvedFacility = new ApprovedFacility();
      this.facilityGeometry = new Graphic();
      this.setControlHasErrors(
        this.canProceedToDetails,
        !this.isGraphicExist()
      );
    }
  }

  public async onTaxParcelGeometryChange(geometry: Graphic[]): Promise<void> {
    this.resetProjectLocationErrors();
    this.taxGeometryList = geometry;
    this.selectedTaxParcel = new TaxParcel();
    if (this.taxGeometryList?.length > 0) {
      let rings: number[][][] = [];
      let spatialReference;
      let attributeCounties = new Set<string>();
      let attributeTaxParcels = new Set<string>();
      let attributeMunicipalities = new Set<string>();
      let attributeStreets = new Set<string>();
      this.taxGeometryList.forEach((sketch: Graphic) => {
        if (sketch.geometry !== undefined) {
          spatialReference = sketch.geometry.spatialReference;
          (sketch.geometry as Polygon).rings.forEach((ring) => {
            rings.push(ring);
          });
        }
        attributeMunicipalities.add(sketch.attributes.MUNI_NAME);
        attributeStreets.add(sketch.attributes.PARCEL_ADDR);
        if (sketch.attributes.PRINT_KEY !== undefined && sketch.attributes.PRINT_KEY !== null && sketch.attributes.PRINT_KEY !== '') {
          attributeTaxParcels.add(sketch.attributes.PRINT_KEY);
        } else {
          attributeTaxParcels.add(sketch.attributes.SBL);
        }
        attributeCounties.add(sketch.attributes.COUNTY_NAME);
      });
      let polygon = new Polygon({
        rings: rings,
        spatialReference: spatialReference,
      });
      let graphic = new Graphic({
        geometry: polygon,
      });
      this.selectedTaxParcel.taxMapNumber = [...attributeTaxParcels].join(',');
      this.selectedTaxParcel.county = [...attributeCounties].join(',');
      this.selectedTaxParcel.municipality = [...attributeMunicipalities].join(',');
      this.selectedTaxParcel.mailAddress = [...attributeStreets].join(',');
      this.finalTaxGraphic = graphic;
      this.setControlHasErrors(this.canProceedToDetails, false);
      this.projectLocationForm.controls.geometry.setValue('1');
    } else {
      this.finalTaxGraphic = new Graphic();
      this.setControlHasErrors(
        this.canProceedToDetails,
        !this.isGraphicExist()
      );
    }
  }

  public async uploadedShapeFileGeometry(geometry: Graphic[]) {
    this.resetProjectLocationErrors();
    this.shapeFileGeometryList = geometry;
    if (this.shapeFileGeometryList?.length > 0) {
      let rings: number[][][] = [];
      let spatialReference;
      this.shapeFileGeometryList.forEach((sketch: Graphic) => {
        if (sketch.geometry !== undefined) {
          spatialReference = sketch.geometry.spatialReference;
          (sketch.geometry as Polygon).rings.forEach((ring) => {
            rings.push(ring);
          });
        }
      });
      let polygon = new Polygon({
        rings: rings,
        spatialReference: spatialReference,
      });
      let graphic = new Graphic({
        geometry: polygon,
      });
      this.finalShapeFileGraphic = graphic;
      this.setControlHasErrors(this.canProceedToDetails, false);
      this.projectLocationForm.controls.geometry.setValue('1');
    } else {
      this.finalShapeFileGraphic = new Graphic();
      this.setControlHasErrors(
        this.canProceedToDetails,
        !this.isGraphicExist()
      );
    }
  }

  public onFileChange(event: any) {
    this.resetAllGeometry();
    if (event.target.files.length > 0) {
      let file = event.target.files[0];
      let fileName = event.target.value.toLowerCase();
      if (fileName.indexOf('.zip') !== -1) {
        //is file a zip - if not notify user
        this.generateFeatureCollection(fileName, file);
      } else {
        this.uploadStatus.nativeElement.innerHTML =
          '<p style="color:red">' +
          this.errorMsgObj?.PRJ_LOC_SHAPEFILE_TYPE +
          '</p>';
      }
    }
  }

  private generateFeatureCollection(fileName: any, file: any) {
    let name = fileName.split('.');
    // Chrome adds c:\fakepath to the value - we need to remove it
    name = name[0].replace('c:\\fakepath\\', '');
    this.uploadStatus.nativeElement.innerHTML = '<strong>Loading </strong>' + name;
    this.gisService.shapeFileUpload(name, file, 102100).subscribe(
      (response: any) => {
        if (response?.featureCollection !== undefined) {
          let layerName =
            response.featureCollection.layers[0]?.layerDefinition?.name;
          this.uploadStatus.nativeElement.innerHTML =
            '<strong>Loaded: </strong>' + layerName;
          let sourceGraphics: Graphic[] = this.extractAllFeatures(
            response.featureCollection
          );
          if (this.hasValidFeatures(sourceGraphics)) {
            this.gisMap.addShapeFileToMap(response.featureCollection);
            this.uploadStatus.nativeElement.innerHTML = '';
          } else {
            this.uploadStatus.nativeElement.innerHTML =
              "<p style='color:red;max-width: 500px;'>" +
              this.errorMsgObj?.PRJ_LOC_INVALID_SHAPEFILE +
              '</p>';
          }
        } else {
          if (response.error !== undefined) {
            this.uploadStatus.nativeElement.innerHTML = "<p style='color:red;max-width: 500px;'>" + response.error.message + '</p>';
          } else {
            this.uploadStatus.nativeElement.innerHTML = "<p style='color:red;max-width: 500px;'>" + this.errorMsgObj?.PRJ_LOC_INVALID_SHAPEFILE + '</p>';
          }
        }
      },
      (error: any) => {
        this.errorHandler(error);
      }
    );
  }
  private extractAllFeatures(featureCollection: any): Graphic[] {
    let sourceGraphics: Graphic[] = [];
    featureCollection.layers.map((layer: any) => {
      let graphics = layer.featureSet.features.map((feature: any) => {
        return Graphic.fromJSON(feature);
      });
      sourceGraphics = sourceGraphics.concat(graphics);
    });
    return sourceGraphics;
  }

  private hasValidFeatures(sourceGraphics: Graphic[]): boolean {
    if (sourceGraphics.some((graphic: Graphic) => graphic.geometry.type === 'polygon')) {
      return true;
    } else {
      return false;
    }
  }

  private errorHandler(error: any) {
    this.uploadStatus.nativeElement.innerHTML =
      "<p style='color:red;max-width: 500px;'>" + error.message + '</p>';
  }

  public async getCountiesData(): Promise<void> {
    this.counties = [];
    this.gisService.getCounties().subscribe((data: any) => {
      data.features?.forEach((element: any) => {
        this.counties.push(element.attributes.NAME);
      });
    }, (error) => {
      //this.serviceError = true;
      this.utils.emitLoadingEmitter(false);
      this.handleProjectLocationServiceError(error);
    });
  }

  public async onProjectLocationSubmit(): Promise<void> {
    if (this.projectLocationForm.invalid) {
      return;
    }
    this.isSubmitted = true;
  }

  public getMunicipalitiesInCounty(): void {
    this.utils.emitLoadingEmitter(true);
    this.isTaxMapSearched = false;
    this.selectedMunicipality = '';
    this.municipalities = new Set<string>();
    this.gisService
      .getMunicipalities(this.projectLocationForm.controls.county.value)
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

  private getGraphicAsJsonString(graphic: Graphic): string {
    let jsonString = '';
    if (graphic?.geometry) {
      jsonString = JSON.stringify(graphic);
    }
    return jsonString;
  }

  get hideMap(): boolean {
    let hideMap = true;
    if (
      this.projectLocationForm.valid &&
      this.isValidAddressGeometry() &&
      this.isIdSearchValid() &&
      this.isTaxMapSearchValid() &&
      (this.isAddressSearched ||
        this.isSearchedByIdAttempted ||
        this.projectLocationForm.get('searchBy')?.value === 'zoom' ||
        this.projectLocationForm.get('searchBy')?.value === 'shapefile' ||
        this.isTaxMapSearched)
    ) {
      hideMap = false;
    }
    return hideMap;
  }

  get isGeometry(): boolean {
    return this.isGraphicExist();
  }

  private async resetAllGeometry(): Promise<void> {
    this.taxGeometryList = new Array<Graphic>();
    this.facilityGeometry = new Graphic();
    this.sketchGeometryList = new Array<Graphic>();
    this.finalSketchGraphic = new Graphic();
    this.workAreaGeometryList = new Array<Graphic>();
    this.finalWorkAreaGraphic = new Graphic();
    this.finalTaxGraphic = new Graphic();
    this.finalTaxMapGraphic = new Graphic();
    this.shapeFileGeometryList = new Array<Graphic>();
    this.finalShapeFileGraphic = new Graphic();
    this.gisMap.clearAllGraphics();
    this.gisMap.resetExtent();
  }

  private async resetAllData(): Promise<void> {
    this.resetProjectLocationErrors();
    this.utils.emitLoadingEmitter(false);
    this.approvedFacility = new ApprovedFacility();
    this.selectedAddress = new AddressResponse();
    this.selectedFacility = new ApprovedFacility();
    this.addressFacilities = new Array<AddressResponse>();
    this.approvedFacilities = new Array<ApprovedFacility>();
    this.approvedFacilitiesForDecId = new Array<ApprovedFacility>();
    this.codedAddressesForId = new Array<AddressResponse>();
    this.approvedFacilitiesForTaxMap = new Array<ApprovedFacility>();
    this.codedAddressesForTaxMap = new Array<AddressResponse>();
    this.selectedFacilityForDecId = new ApprovedFacility();
    this.selectedFacilityForTaxMap = new ApprovedFacility();
    this.idResponse = new IdResponse();
    this.selectedTaxParcel = new TaxParcel();
    this.facilitiesAtGeometry = new Array<ApprovedFacility>();
    this.selectedFacilityAtGeometry = new ApprovedFacility();
  }
  private async resetLocationForm(): Promise<void> {
    this.projectLocationForm.controls.street.setValue('');
    this.projectLocationForm.controls.city.setValue('');
    this.projectLocationForm.controls.zip.setValue('');
    this.projectLocationForm.controls.idValue.setValue('');
    this.projectLocationForm.controls.taxMapNumber.setValue('');
    this.projectLocationForm.controls.county.setValue('');
    this.projectLocationForm.controls.municipality.setValue('');
    this.projectLocationForm.controls.geometry.setValue('');
  }

  public async submitConform(): Promise<void> {
    this.open(this.submitConfirm, '35vw');
  }

  private async copySubmitToAnalyst(
    projectLocation: ProjectLocation
  ): Promise<void> {
    let attributes = {
      ANALYST_ID: projectLocation.projectId,
      //CREATED_USER: localStorage.getItem('loggedUserName'),
    };
    this.utils.emitLoadingEmitter(true);
    this.gisService
      .searchSubmittedPolygon(projectLocation.polygonId)
      .toPromise()
      .then(async (polygon: any) => {
        let poly = new Polygon({
          rings: (polygon.features[0].geometry as Polygon).rings,
          spatialReference: polygon.spatialReference,
        });
        let tempGraphic = new Graphic({
          geometry: poly,
          attributes: attributes,
        });
        this.gisService
          .saveAnalystPolygon(this.getGraphicAsJsonString(tempGraphic), 'S')
          .subscribe((data: any) => {
            if (data.addResults) {
              let objectId = data.addResults[0]?.objectId;
              projectLocation.polygonId = objectId;
              projectLocation.polygonStatus = 'ANALYST_SCRATCH';
              this.gisService
                .updateProjectLocation(projectLocation)
                .subscribe(
                  (response: any) => {
                    this.utils.emitLoadingEmitter(false);
                    this.router.navigate(['/project-location-details']);
                  },
                  (error: any) => {
                    this.utils.emitLoadingEmitter(false);
                    this.handleServiceError(error);
                  }
                );
            }
          },
            (error: any) => {
              this.utils.emitLoadingEmitter(false);
              this.handleServiceError(error);
            }
          );
      })
      .catch((error: any) => {
        this.utils.emitLoadingEmitter(false);
        this.handleServiceError(error);
      });
  }

  private handleServiceError(error: any) {
    this.serviceErrorMessage = this.errorService.getServerMessage(error);
    this.serviceError = true;
    throw error;
  }


  private deleteSubmittedPolygon(polygonId: number): void {
    this.utils.emitLoadingEmitter(true);
    this.gisService
      .deleteSubmittedPolygon(polygonId)
      .subscribe((data: any) => {
        console.log(
          'Successfully cleaned up Polygon on failure'
        );
        this.utils.emitLoadingEmitter(
          false
        );
      });
  }

  private deleteApplicantPolygon(polygonId: number): void {
    this.utils.emitLoadingEmitter(true);
    this.gisService
      .deleteApplicantPolygon(polygonId)
      .subscribe((data: any) => {
        console.log(
          'Successfully cleaned up Polygon on failure'
        );
        this.utils.emitLoadingEmitter(false);
      });
  }

  private updateProject(polygonId: number, projectId: number, workAreaId: string = '', printUrl: string = ''): void {
    this.utils.emitLoadingEmitter(true);
    this.gisService
      .updateProjectLocation(
        this.getProjectLocationFromForm(
          polygonId,
          projectId,
          'APPLICANT_SUBMITTED',
          workAreaId,
          printUrl
        )
      )
      .subscribe(
        (response: any) => {
          localStorage.removeItem(
            'applyForPermitData'
          );
          this.utils.emitLoadingEmitter(false);
          this.backToMain();
        },
        (error: any) => {
          this.deleteSubmittedPolygon(polygonId);
          this.utils.emitLoadingEmitter(false);
          //delete the Polygon if save Project fail with Object id
          this.serviceError = true;
          this.serviceErrorMessage =
            error?.error?.resultMessage;

        }
      );
  }

  private saveSubmittalPolygon(projectId: number, tempGraphic: Graphic, workAreaId: string = '', printUrl: string = ''): void {
    this.utils.emitLoadingEmitter(true);
    let submit_attributes = {
      APPL_SUB_ID: projectId.toString(),
      //CREATED_USER:localStorage.getItem('loggedUserName'),
    };
    tempGraphic.attributes = submit_attributes;
    this.gisService
      .saveSubmittedPolygon(
        this.getGraphicAsJsonString(tempGraphic),
        'S'
      )
      .subscribe(
        (data: any) => {
          if (data.addResults[0]) {
            let objectId = data.addResults[0]?.objectId;
            let projectId =
              localStorage.getItem('projectId') !== null
                ? Number(
                  localStorage.getItem('projectId')
                )
                : 0;
            this.updateProject(objectId, projectId, workAreaId, printUrl);
          } else {
            this.utils.emitLoadingEmitter(false);
            this.serviceError = true;
            this.serviceErrorMessage = this.errorMsgObj?.UNABLE_TO_PROCESS_NOW;
          }
        },
        (error: any) => {
          this.serviceError = true;
          this.serviceErrorMessage = this.errorMsgObj?.UNABLE_TO_PROCESS_NOW;
          this.utils.emitLoadingEmitter(false);
        }
      );
  }

  private saveWorkAreaPolygon(): Promise<any> {
    return new Promise((resolve, reject) => {
      let attributes = {
        WORK_AREA_ID: 0,
        //CREATED_USER: localStorage.getItem('loggedUserName'),
      };
      let workAreaTempGraphic = this.finalWorkAreaGraphic;
      workAreaTempGraphic.attributes = attributes;
      this.gisService
        .saveWorkAreaPolygon(this.getGraphicAsJsonString(workAreaTempGraphic), 'S').subscribe(
          (data: any) => {
            if (data.addResults[0]) {
              resolve(data.addResults[0]);
            }
          }, (error: any) => {
            this.utils.emitLoadingEmitter(false);
            reject(error);
          }
        );
    });
  }

  private updateWorkAreaPolygon(objectId: string, projectId: number): Promise<any> {
    return new Promise((resolve, reject) => {
      let attributes = {
        OBJECTID: new Number(objectId),
        WORK_AREA_ID: projectId,
        //CREATED_USER: localStorage.getItem('loggedUserName'),
      };
      let workAreaTempGraphic = this.finalWorkAreaGraphic;
      workAreaTempGraphic.attributes = attributes;
      this.gisService
        .saveWorkAreaPolygon(this.getGraphicAsJsonString(workAreaTempGraphic), 'U').subscribe(
          (data: any) => {
            if (data.updateResults[0]) {
              resolve(data.updateResults[0]);
            }
          }, (error: any) => {
            this.utils.emitLoadingEmitter(false);
            reject(error);
          }
        );
    });
  }

  private updateProjectPolygon(projectGraphic: Graphic, isSubmit: boolean, projectId: number, workAreaId: string = '', printUrl: string = '') {
    this.gisService
      .saveApplicantPolygon(
        this.getGraphicAsJsonString(projectGraphic),
        'U'
      )
      .subscribe(
        (data: any) => {
          if (!isSubmit) {
            this.utils.emitLoadingEmitter(false);
            this.backToMain();
          } else {
            //save submittal polygon
            this.saveSubmittalPolygon(projectId, projectGraphic, workAreaId, printUrl);
          }
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
        }
      );
  }

  public async saveProjectLocation(isSubmit: boolean = false, printUrl: string = ''): Promise<void> {
    this.isSavedSubmitted = true;
    this.utils.emitLoadingEmitter(true);
    if (!this.projectLocationDetailsForm.invalid) {
      let attributes = {
        APPL_ID: '0',
        //CREATED_USER: localStorage.getItem('loggedUserName'),
      };
      let tempGraphic = this.combineAllGeometry();
      tempGraphic.attributes = attributes;
      this.clearErrors();
      let projectId = 0;
      this.gisService
        .saveApplicantPolygon(this.getGraphicAsJsonString(tempGraphic), 'S')
        .subscribe(
          async (data: any) => {
            if (data.addResults[0]) {
              let objectId = data.addResults[0]?.objectId;
              let attribute = {
                OBJECTID: new Number(objectId),
                APPL_ID: '0',
              };
              let projectLocation = this.getProjectLocationFromForm(objectId);
              if (this.workAreaGeometryList.length > 0) {
                let result = await this.saveWorkAreaPolygon();
                projectLocation.workAreaId = result.objectId;
              }
              this.gisService
                .saveProjectLocation(projectLocation)
                .subscribe(
                  async (response: any) => {
                    projectId = response.projectId;
                    attribute.APPL_ID = projectId.toString();
                    localStorage.setItem('projectId', projectId.toString());
                    tempGraphic.attributes = attribute;
                    if (this.workAreaGeometryList.length > 0) {
                      await this.updateWorkAreaPolygon(projectLocation.workAreaId, projectId);
                    }
                    if (projectLocation.workAreaId !== undefined && projectLocation.workAreaId !== null) {
                      this.updateProjectPolygon(tempGraphic, isSubmit, projectId, projectLocation.workAreaId, printUrl);
                    } else {
                      this.updateProjectPolygon(tempGraphic, isSubmit, projectId, '', printUrl);
                    }
                  },
                  (error: any) => {
                    this.deleteApplicantPolygon(objectId);
                    //delete the Polygon if save Project fail with Object id
                    this.handleServiceError(error);
                  }
                );
            } else {
              this.utils.emitLoadingEmitter(false);
              this.serviceError = true;
              this.serviceErrorMessage = this.errorMsgObj?.UNABLE_TO_PROCESS_NOW;
            }
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.serviceError = true;
            this.serviceErrorMessage = this.errorMsgObj?.UNABLE_TO_PROCESS_NOW;

          }
        );
    } else {
      this.utils.emitLoadingEmitter(false);
    }
  }

  private getProjectLocationFromForm(
    polygonId: number,
    projectId: number = 0,
    polygonStatus: string = 'APPLICANT_SCRATCH', workAreaId: string = '', printUrl: string = ''): ProjectLocation {
    let projectLocation = new ProjectLocation();
    if (projectId > 0) {
      projectLocation.projectId = projectId;
    }
    //non Validate Mode
    projectLocation.mode = 0;
    projectLocation.receivedDate = this.receivedDate;
    projectLocation.classifiedUnderSeqr = this.classifiedUnderSeqr;
    projectLocation.mailInInd = Number(this.mailInInd);
    projectLocation.polygonStatus = polygonStatus;
    projectLocation.locDirections =
      this.projectLocationDetailsForm.controls.direction.value;
    projectLocation.applicantTypeCode = this.applicantTypeCode;
    projectLocation.polygonId = polygonId?.toString();
    this.buildProjectFacility(projectLocation);
    projectLocation.regions =
      this.projectLocationDetailsForm.controls.region.value;
    projectLocation.primaryRegion =
      this.projectLocationDetailsForm.controls.primaryRegion?.value;
    projectLocation.municipalities =
      this.projectLocationDetailsForm.controls.municipality.value;
    projectLocation.municipalitySwis =
      this.projectLocationDetailsForm.controls.municipalitySwis?.value;
    projectLocation.primaryMunicipality =
      this.projectLocationDetailsForm.controls.primaryMunicipality?.value;
    projectLocation.counties =
      this.projectLocationDetailsForm.controls.county.value;
    projectLocation.countySwis =
      this.projectLocationDetailsForm.controls.countySwis?.value;
    projectLocation.taxmaps =
      this.projectLocationDetailsForm.controls.taxMap.value;
    projectLocation.reason =
      this.projectLocationDetailsForm.controls.reason.value;
    projectLocation.boundaryChangeReason =
      this.projectLocationDetailsForm.controls.boundaryChangeReason.value;
    projectLocation.lat =
      this.projectLocationDetailsForm.controls.latitude.value;
    projectLocation.long =
      this.projectLocationDetailsForm.controls.longitude.value;
    projectLocation.nytmx =
      this.projectLocationDetailsForm.controls.nytmx.value;
    projectLocation.nytmy =
      this.projectLocationDetailsForm.controls.nytmy.value;
    if (workAreaId !== '') {
      projectLocation.workAreaId = workAreaId;
    }
    if (printUrl !== '') {
      projectLocation.printUrl = printUrl;
    }
    return projectLocation;
  }

  private buildProjectFacility(projectLocation: ProjectLocation) {
    let projectFacility = new ProjectFacility();
    projectFacility.facilityName = this.projectLocationDetailsForm.controls.projectName.value;

    if (this.isItIdSearch()) {
      projectFacility.edbDistrictId = this.idResponse?.districtId;
      projectFacility.facilityId = this.idResponse?.decId;
      projectFacility.decId = this.idResponse?.decId
      projectFacility.decIdFormatted = this.idResponse?.decIdFormatted;
      projectFacility.districtId = this.idResponse?.districtId;
    }
    if (
      this.projectLocationForm.get('searchBy')?.value === 'address' &&
      !this.notApplicable
    ) {
      projectFacility.edbDistrictId = this.finalSelectedAddress?.districtId;
      projectFacility.facilityId = this.convertDECIDtoNumber(this.finalSelectedAddress?.decIdFormatted);
      projectFacility.decId = this.convertDECIDtoNumber(this.finalSelectedAddress?.decIdFormatted);
      projectFacility.decIdFormatted = this.finalSelectedAddress?.decIdFormatted;
      projectFacility.districtId = this.finalSelectedAddress?.districtId;
    }
    if (
      ((this.projectLocationForm.get('searchBy')?.value === 'address' &&
        this.notApplicable) ||
        (this.projectLocationForm.get('searchBy')?.value === 'zoom')) &&
      this.selectedFacilityAtGeometry?.PRIMARY_ID !== undefined &&
      this.selectedFacilityAtGeometry?.PRIMARY_ID !== ''
    ) {
      projectFacility.facilityId = Number(
        this.convertDECIDtoNumber(this.selectedFacilityAtGeometry?.PRIMARY_ID)
      );
      projectFacility.decId = Number(
        this.convertDECIDtoNumber(this.selectedFacilityAtGeometry?.PRIMARY_ID)
      );
      projectFacility.decIdFormatted = this.selectedFacilityAtGeometry?.PRIMARY_ID;
    }
    this.buildFacilityAddress(projectFacility);
    projectLocation.facility = projectFacility;
  }

  private convertDECIDtoNumber(decid: string): number {
    return Number(decid?.replace(/-/g, ''));
  }
  private buildFacilityAddress(projectFacility: ProjectFacility) {
    let facilityAddress = new FacilityAddress();
    facilityAddress.street1 = this.projectLocationDetailsForm.controls.address1.value;
    facilityAddress.street2 = this.projectLocationDetailsForm.controls.address2.value;
    facilityAddress.city = this.projectLocationDetailsForm.controls.city.value.trim().substring(0,65);
    facilityAddress.country = 'USA';
    facilityAddress.state = 'NY';
    facilityAddress.zip = this.projectLocationDetailsForm.controls.zip.value;
    if (this.isItIdSearch()) {
      facilityAddress.zipExtension = this.idResponse?.zipExtension;
      facilityAddress.phoneNumber = this.idResponse?.phoneNumber;
    }
    if (
      this.projectLocationForm.get('searchBy')?.value === 'address' &&
      !this.notApplicable
    ) {
      facilityAddress.zipExtension = this.finalSelectedAddress?.zipExtension;
    }
    projectFacility.address = facilityAddress;
  }

  private copyIdSearchValues(): void {
    if (this.isItIdSearch()) {
      if (this.idResponse !== undefined && this.idResponse !== null) {
        this.projectLocationDetailsForm.controls.projectName.setValue(
          this.idResponse?.facilityName
        );
        if(this.idResponse?.locationDirections !==undefined && this.idResponse?.locationDirections !== null){
          let directions=this.idResponse.locationDirections?.split('|');
          this.projectLocationDetailsForm.controls.address1.setValue(
            directions[0]
          );
          if(directions[1]!==undefined){
            this.projectLocationDetailsForm.controls.address2.setValue(
              directions[1]
            );
          }
        }

        this.projectLocationDetailsForm.controls.city.setValue(
          (this.idResponse?.city).trim().substring(0,65)
        );
        this.projectLocationDetailsForm.controls.zip.setValue(
          this.idResponse?.zip
        );
        this.projectLocationDetailsForm.controls.decId.setValue(
          this.idResponse?.decIdFormatted
        );
      }
      if (
        this.approvedFacility !== undefined &&
        this.approvedFacility.geometry !== undefined
      ) {
        this.projectLocationDetailsForm.controls.decId.setValue(
          this.approvedFacility?.PRIMARY_ID
        );
        this.projectLocationDetailsForm.controls.address2.setValue(
          this.approvedFacility?.LOCATION_DIRECTIONS_2
        );
        this.projectLocationDetailsForm.controls.municipality.setValue(
          this.approvedFacility?.MUNICIPALITIES
        );
        this.projectLocationDetailsForm.controls.municipalityNames.setValue(
          this.approvedFacility?.MUNICIPALITIES
        );
        this.projectLocationDetailsForm.controls.county.setValue(
          this.approvedFacility?.COUNTIES
        );
        this.projectLocationDetailsForm.controls.countyNames.setValue(
          this.approvedFacility?.COUNTIES
        );
        this.projectLocationDetailsForm.controls.latitude.setValue(
          this.approvedFacility?.latitude
        );
        this.projectLocationDetailsForm.controls.longitude.setValue(
          this.approvedFacility?.longitude
        );
        this.projectLocationDetailsForm.controls.nytmx.setValue(
          this.approvedFacility?.nytmx
        );
        this.projectLocationDetailsForm.controls.nytmy.setValue(
          this.approvedFacility?.nytmy
        );
      }
      if (
        this.finalSelectedAddress !== undefined &&
        this.finalSelectedAddress.long !== undefined &&
        this.finalSelectedAddress.lat !== undefined
      ) {
        this.projectLocationDetailsForm.controls.latitude.setValue(
          this.finalSelectedAddress?.lat
        );
        this.projectLocationDetailsForm.controls.longitude.setValue(
          this.finalSelectedAddress?.long
        );
        this.projectLocationDetailsForm.controls.nytmx.setValue(
          this.finalSelectedAddress?.nytmx
        );
        this.projectLocationDetailsForm.controls.nytmy.setValue(
          this.finalSelectedAddress?.nytmy
        );
      }
    }
  }

  private copyAddressSearchValues(): void {
    if (this.projectLocationForm.get('searchBy')?.value === 'address') {
      if (!this.notApplicable) {
        this.copyAddress();
      } else if (
        this.selectedFacilityAtGeometry?.LOCATION_DIRECTIONS_1 === undefined ||
        this.selectedFacilityAtGeometry?.LOCATION_DIRECTIONS_1 === ''
      ) {
        this.projectLocationDetailsForm.controls.address1.setValue(
          this.projectLocationForm.controls.street.value
        );
        this.projectLocationDetailsForm.controls.city.setValue(
          (this.projectLocationForm.controls.city.value).trim().substring(0,65)
        );
      }
    }
  }

  private copyAddress() {
    if (
      this.finalSelectedAddress !== undefined &&
      this.finalSelectedAddress.decIdFormatted !== undefined
    ) {
      this.projectLocationDetailsForm.controls.decId.setValue(
        this.finalSelectedAddress?.decIdFormatted
      );
      this.projectLocationDetailsForm.controls.projectName.setValue(
        this.finalSelectedAddress?.facilityName
      );
      if(this.finalSelectedAddress?.locationDirections !==undefined && this.finalSelectedAddress?.locationDirections !== null){
        let directions=this.finalSelectedAddress.locationDirections?.split('|');
        this.projectLocationDetailsForm.controls.address1.setValue(
          directions[0]
        );
        if(directions[1]!==undefined){
          this.projectLocationDetailsForm.controls.address2.setValue(
            directions[1]
          );
        }
      }
      this.projectLocationDetailsForm.controls.city.setValue(
        (this.finalSelectedAddress?.city).trim().substring(0,65)
      );
      this.projectLocationDetailsForm.controls.zip.setValue(
        this.finalSelectedAddress?.zip
      );
      this.projectLocationDetailsForm.controls.municipality.setValue(
        this.finalSelectedAddress?.municipality
      );
      this.projectLocationDetailsForm.controls.county.setValue(
        this.finalSelectedAddress?.county
      );
      this.projectLocationDetailsForm.controls.latitude.setValue(
        this.finalSelectedAddress?.lat
      );
      this.projectLocationDetailsForm.controls.longitude.setValue(
        this.finalSelectedAddress?.long
      );
      this.projectLocationDetailsForm.controls.nytmx.setValue(
        this.finalSelectedAddress?.nytmx
      );
      this.projectLocationDetailsForm.controls.nytmy.setValue(
        this.finalSelectedAddress?.nytmy
      );
    }
  }

  private copyToDetailsForm(): void {
    this.copyIdSearchValues();
    this.copyAddressSearchValues();
    if (this.projectLocationForm.get('searchBy')?.value === 'taxmap') {
      this.copyAddress();
    }
    this.buildAllGeometryData();
  }

  private async buildAllGeometryData(){
    let finalGraphic = this.combineAllGeometry();
    //let geometry = (finalGraphic as Polygon).geometry;
    let polygon = finalGraphic.geometry as Polygon;
    //getting regions data as per polygon
    await this.gisService.queryRegionsAt(finalGraphic).then(
      (regions) => {
        this.projectLocationDetailsForm.controls.region.setValue(regions);
        if (regions?.indexOf(',') < 0) {
          this.projectLocationDetailsForm.controls.primaryRegion.setValue(regions);
        } else {
          this.gisService.getRegionAt(polygon?.centroid?.longitude, polygon.centroid?.latitude).then((region) => {
            this.projectLocationDetailsForm.controls.primaryRegion.setValue(region);
          });
        }
      },
      (error) => {
        //this.serviceError = true;
        this.handleProjectLocationServiceError(error);
      }
    );

    if (
      this.isItIdSearch() &&
      this.idResponse?.decIdFormatted !== null &&
      this.idResponse?.decIdFormatted?.startsWith('0-0000')
    ) {
      let municipalityNames = new Set<string>();
      let municipalitySiws = new Set<string>();
      let municipalityWithSiws = new Set<string>();
      let stateWideCivilDivision = new CivilDivision('NEW YORK', '0000', 0);
      municipalityNames.add(stateWideCivilDivision.name);
      municipalitySiws.add(stateWideCivilDivision.swis);
      municipalityWithSiws.add(stateWideCivilDivision.fullMunicipalityDetails);
      this.projectLocationDetailsForm.controls.primaryMunicipality.setValue([...municipalityWithSiws][0]);
      this.projectLocationDetailsForm.controls.municipality.setValue([...municipalityWithSiws].join(','));
      this.projectLocationDetailsForm.controls.municipalitySwis.setValue([...municipalitySiws].join(','));
      this.projectLocationDetailsForm.controls.municipalityNames.setValue([...municipalityNames].join(', '));
    } else {
      //getting municipalities data as per polygon
      await this.gisService.queryMunicipalitiesAt(finalGraphic).then(
        (municipalities) => {
          let municipalityNames = new Set<string>();
          let municipalitySiws = new Set<string>();
          let municipalityWithSiws = new Set<string>();
          // commented to remove municipalities as basis for StateWide
          /*if(municipalities.size >=50){
        let stateWideCivilDivision=new CivilDivision("NEW YORK","0000",0);
        municipalityNames.add(stateWideCivilDivision.name);
        municipalitySiws.add(stateWideCivilDivision.swis);
        municipalityWithSiws.add(stateWideCivilDivision.fullMunicipalityDetails);
      }else{*/
          municipalities.forEach((civilDivision: CivilDivision) => {
            municipalityNames.add(civilDivision.name);
            if (civilDivision.swis !== undefined && civilDivision.swis !== null) {
              municipalitySiws.add(civilDivision.swis);
            }
            municipalityWithSiws.add(civilDivision.fullMunicipalityDetails);
          });
          //}
          if (municipalityWithSiws.size === 1) {
            this.projectLocationDetailsForm.controls.primaryMunicipality.setValue([...municipalityWithSiws][0]);
          }
          this.projectLocationDetailsForm.controls.municipality.setValue([...municipalityWithSiws].join(','));
          this.projectLocationDetailsForm.controls.municipalitySwis.setValue([...municipalitySiws].join(','));
          this.projectLocationDetailsForm.controls.municipalityNames.setValue([...municipalityNames].join(', '));
          /*if ([...municipalityNames].join(',').indexOf(',') < 0) {
        this.projectLocationDetailsForm.controls.city.setValue([...municipalityNames].join(','));
       }*/
          if (
            this.selectedFacility?.MUNICIPALITIES !== undefined &&
            this.selectedFacility?.MUNICIPALITIES !== '' &&
            this.selectedFacility?.MUNICIPALITIES !== null
          ) {
            this.projectLocationDetailsForm.controls.city.setValue(
              (this.selectedFacility?.MUNICIPALITIES).trim().substring(0, 65)
            );
          } else {
            let filteredMunicipality = '';
            if (municipalityNames.size > 1) {
              municipalityNames.forEach((municipality) => {
                if (filteredMunicipality.length <= 65) {
                  if (filteredMunicipality.length === 0) {
                    filteredMunicipality += municipality;
                  } else {
                    filteredMunicipality = filteredMunicipality + ',' + municipality;
                  }
                }
                if (filteredMunicipality.length > 65) {
                  filteredMunicipality = filteredMunicipality.slice(0, filteredMunicipality.lastIndexOf(','));
                }
              });
              this.projectLocationDetailsForm.controls.city.setValue(filteredMunicipality.trim().substring(0, 65));
            } else {
              this.projectLocationDetailsForm.controls.city.setValue([...municipalityNames][0].trim().substring(0, 65));
            }
          }
        },
        (error) => {
          //this.serviceError = true;
          this.handleProjectLocationServiceError(error);
        }
      );
    }

    //getting counties data as per polygon
    await this.gisService.queryCountiesAt(finalGraphic).then(
      (counties) => {
        let countyNames = new Set<string>();
        let countySiws = new Set<string>();
        let countyWithSiws = new Set<string>();
        // commented to remove municipalities as basis for StateWide
        if(counties.size >=50){
          let municipalityNames = new Set<string>();
          let municipalitySiws = new Set<string>();
          let municipalityWithSiws = new Set<string>();
          let stateWideCivilDivision=new CivilDivision("NEW YORK","0000",0);
          municipalityNames.add(stateWideCivilDivision.name);
          municipalitySiws.add(stateWideCivilDivision.swis);
          municipalityWithSiws.add(stateWideCivilDivision.fullMunicipalityDetails);
          this.projectLocationDetailsForm.controls.municipality.setValue([...municipalityWithSiws].join(','));
          this.projectLocationDetailsForm.controls.municipalitySwis.setValue([...municipalitySiws].join(','));
          this.projectLocationDetailsForm.controls.municipalityNames.setValue([...municipalityNames].join(', '));
        }
        counties.forEach((civilDivision: CivilDivision) => {
          countyNames.add(civilDivision.name);
          countySiws.add(civilDivision.swis);
          countyWithSiws.add(civilDivision.fullDetails);
        });
        this.projectLocationDetailsForm.controls.county.setValue([...countyWithSiws].join(','));
        this.projectLocationDetailsForm.controls.countySwis.setValue([...countySiws].join(', '));
        this.projectLocationDetailsForm.controls.countyNames.setValue([...countyNames].join(', '));
      },
      (error) => {
        //this.serviceError = true;
        this.handleProjectLocationServiceError(error);
      }
    );

    this.buildParcelsAndAddresses(finalGraphic);
    //this will generate lat and long from geometry
    //@ts-ignore
    //let geometry = (finalGraphic as Polygon).geometry;
    if (polygon !== undefined) {
      this.projectLocationDetailsForm.controls.latitude.setValue(polygon.centroid?.latitude);
      this.projectLocationDetailsForm.controls.longitude.setValue(polygon.centroid?.longitude);
      if (this.projectLocationDetailsForm.controls.municipality.value?.indexOf(',') > 0) {
        this.gisService
          .getMunicipalityAt(polygon?.centroid?.longitude, polygon.centroid?.latitude)
          .then((municipalities) => {
            municipalities.forEach((civilDivision: CivilDivision) => {
              this.projectLocationDetailsForm.controls.primaryMunicipality.setValue(
                civilDivision.fullMunicipalityDetails
              );
            });
          });
      }

      projection.load().then(() => {
        let point = projection.project(polygon.centroid, this.NAD83) as Point;
        this.projectLocationDetailsForm.controls.nytmx.setValue(point?.x);
        this.projectLocationDetailsForm.controls.nytmy.setValue(point?.y);
      });
    }
  }

  private buildParcelsAndAddresses(finalGraphic: Graphic): void {
    let taxParcelSet = new Set<string>();
    let addressSet = new Set<string>();
    if (
      (this.sketchGeometryList === undefined ||
        this.sketchGeometryList.length === 0) &&
      (this.shapeFileGeometryList === undefined ||
        this.shapeFileGeometryList.length === 0)
    ) {
      if (
        this.facilityGeometryList !== undefined &&
        this.facilityGeometryList.length > 0
      ) {
        this.facilityGeometryList.forEach((sketch: Graphic) => {
          taxParcelSet.add(this.approvedFacility.taxMapNumber);
          if (this.approvedFacility.LOCATION_DIRECTIONS_1 !== undefined && this.approvedFacility.LOCATION_DIRECTIONS_1 !== null && this.approvedFacility.LOCATION_DIRECTIONS_1 !== '') {
            addressSet.add(this.approvedFacility.LOCATION_DIRECTIONS_1?.split('|')[0]);
          }
          if (this.approvedFacility.LOCATION_DIRECTIONS_2 !== undefined && this.approvedFacility.LOCATION_DIRECTIONS_2 !== null && this.approvedFacility.LOCATION_DIRECTIONS_2 !== '') {
            this.projectLocationDetailsForm.controls.address2.setValue(this.approvedFacility.LOCATION_DIRECTIONS_2);
          }
        });
      } else {
        if (
          this.taxGeometryList !== undefined &&
          this.taxGeometryList.length > 0
        ) {
          this.taxGeometryList.forEach((sketch: Graphic) => {
            if (sketch?.attributes?.PARCEL_ADDR !== undefined && sketch.attributes.PARCEL_ADDR !== null && sketch.attributes.PARCEL_ADDR !== '') {
              addressSet.add(sketch.attributes.PARCEL_ADDR);
            }
          });
        }
      }
      if (
        this.taxGeometryList !== undefined &&
        this.taxGeometryList.length > 0
      ) {
        this.taxGeometryList.forEach((sketch: Graphic) => {
          if (sketch?.attributes?.PRINT_KEY !== undefined && sketch.attributes.PRINT_KEY !== null && sketch.attributes.PRINT_KEY !== '') {
            taxParcelSet.add(sketch.attributes.PRINT_KEY);
          } else {
            taxParcelSet.add(sketch.attributes.SBL);
          }
        });
      }
      if ([...taxParcelSet].join(',').length > 2000) {
        this.projectLocationDetailsForm.controls.taxMap.setValue(
          [...taxParcelSet].join(',').substring(0, 2000)
        );
      } else {
        this.projectLocationDetailsForm.controls.taxMap.setValue(
          [...taxParcelSet].join(',')
        );
      }
      this.buildAddresses(addressSet);
    } else {
      this.gisService.queryTaxParcelsAt(finalGraphic).then((taxParcels: any) => {
        if (taxParcels.split(':::')[0]?.trim().length > 2000) {
          this.projectLocationDetailsForm.controls.taxMap.setValue(taxParcels.split(':::')[0]?.trim().substring(0, 2000));
        } else {
          this.projectLocationDetailsForm.controls.taxMap.setValue(taxParcels.split(':::')[0]?.trim());
        }
        if (this.facilityGeometryList?.length === 0) {
          this.projectLocationDetailsForm.controls.address1.setValue(taxParcels.split(':::')[1]?.trim());
        }
      });
    }
  }
  private buildAddresses(addressSet: Set<string>): void {
    /*if (
      addressSet?.size === 1 &&
      (this.projectLocationForm.controls.searchBy.value === 'zoom' ||
        (this.projectLocationForm.controls.searchBy.value === 'address' &&
          this.notApplicable))
    ) {
      this.projectLocationDetailsForm.controls.address1.setValue(
        [...addressSet].join(',')
      );
    }*/

    if ((this.projectLocationForm.controls.searchBy.value === 'zoom' ||
      (this.projectLocationForm.controls.searchBy.value === 'address' && this.notApplicable) || (this.projectLocationForm.controls.searchBy.value === 'taxmap' && this.taxParcelNotApplicable)) && (this.selectedFacilityAtGeometry?.LOCATION_DIRECTIONS_1 === undefined || this.selectedFacilityAtGeometry?.LOCATION_DIRECTIONS_1 === '')) {
      if (addressSet?.size < 3) {
        this.projectLocationDetailsForm.controls.address1.setValue([...addressSet].join(','));
      } else {
        this.projectLocationDetailsForm.controls.address1.setValue('');
      }
    }
  }

  get customTextElements() {
    // If there are multiple municipalities, change it to 'NEW YORK'.
    let muniNames:string = this.projectLocationDetailsForm.controls.municipalityNames?.value;
    if(muniNames?.split(', ').length > 1){
      muniNames = 'NEW YORK';
    }
    return [
      { "CountyName": this.projectLocationDetailsForm.controls.countyNames?.value },
      { "MuniName": muniNames },
      { "DECID": this.projectLocationDetailsForm.controls.decId?.value },
      { "TaxParcelID": this.projectLocationDetailsForm.controls.taxMap?.value },
      { "Address": this.projectLocationDetailsForm.controls.address1?.value + ' ' + this.projectLocationDetailsForm.controls.city?.value.trim().substring(0,65) },
      { "FacilityName": this.projectLocationDetailsForm.controls.projectName?.value },
      { "ProjectID": "" }
    ];
  }

  private resetDetailsForm(): void {
    this.projectLocationDetailsForm.controls.projectName.setValue('');
    this.projectLocationDetailsForm.controls.address1.setValue('');
    this.projectLocationDetailsForm.controls.address2.setValue('');
    this.projectLocationDetailsForm.controls.city.setValue('');
    this.projectLocationDetailsForm.controls.zip.setValue('');
    this.projectLocationDetailsForm.controls.municipality.setValue('');
    this.projectLocationDetailsForm.controls.county.setValue('');
    this.projectLocationDetailsForm.controls.taxMap.setValue('');
    this.projectLocationDetailsForm.controls.latitude.setValue('');
    this.projectLocationDetailsForm.controls.longitude.setValue('');
    this.projectLocationDetailsForm.controls.nytmx.setValue('');
    this.projectLocationDetailsForm.controls.nytmy.setValue('');
    this.isSavedSubmitted = false;
  }

  public selectionChange(event: any): void {
    this.selectedIndex = event.selectedIndex;
    switch (event.selectedIndex) {
      case 0:
        this.resetDetailsForm();
        break;
      case 1:
        this.copyToDetailsForm();
        if (this.isFacilityBoundaryModified()) {
          this.addBoundaryChangeReasonValidator();
        } else {
          this.removeBoundaryChangeReasonValidator();
        }
        this.gisMapView.getExtent();
        break;
    }
  }

  private async findAnyFacilitiesExist(): Promise<void> {
    this.facilitiesAtGeometry = new Array<ApprovedFacility>();
    this.selectedFacilityAtGeometry = new ApprovedFacility();
    if (
      this.projectLocationForm.controls.searchBy.value === 'zoom' ||
      (this.projectLocationForm.controls.searchBy.value === 'address' &&
        this.notApplicable) || (this.projectLocationForm.controls.searchBy.value === 'taxmap' && this.taxParcelNotApplicable)
    ) {
      this.utils.emitLoadingEmitter(true);
      this.facilitiesAtGeometry = await this.gisMap.queryFacilitiesAt(this.combineAllGeometry());
      if (this.facilitiesAtGeometry !== undefined) {
        this.utils.emitLoadingEmitter(false);
        this.open(this.facilityConfirm, '40vw');
      } else {
        await this.buildAllGeometryData();
        this.utils.emitLoadingEmitter(false);
        this.stepper.next();
      }
    } else {
      await this.buildAllGeometryData();
      this.stepper.next();
    }
  }

  public removeBoundaryChangeReasonValidator() {
    this.projectLocationDetailsForm.controls.boundaryChangeReason?.setValidators(
      null
    );
    this.projectLocationDetailsForm.controls.boundaryChangeReason?.updateValueAndValidity();
    this.projectLocationDetailsForm.markAllAsTouched();
  }

  public addBoundaryChangeReasonValidator(): void {
    this.projectLocationDetailsForm.controls.boundaryChangeReason?.setValidators(
      [Validators.required]
    );
    this.projectLocationDetailsForm.controls.boundaryChangeReason?.updateValueAndValidity();
    this.projectLocationDetailsForm.markAllAsTouched();
  }

  public removeReasonValidator(): void {
    this.projectLocationDetailsForm.controls.reason?.setValidators(null);
    this.projectLocationDetailsForm.controls.reason.updateValueAndValidity();
    this.projectLocationDetailsForm.markAllAsTouched();
  }

  public addReasonValidator(): void {
    this.projectLocationDetailsForm.controls.reason?.setValidators([
      Validators.required,
    ]);
    this.projectLocationDetailsForm.controls.reason.updateValueAndValidity();
    this.projectLocationDetailsForm.markAllAsTouched();
  }

  public copyFacility(): void {
    if (
      this.selectedFacilityAtGeometry?.PRIMARY_ID !== undefined &&
      this.selectedFacilityAtGeometry?.PRIMARY_ID !== ''
    ) {
      this.projectLocationDetailsForm.controls.decId.setValue(
        this.selectedFacilityAtGeometry?.PRIMARY_ID
      );
      this.projectLocationDetailsForm.controls.projectName.setValue(
        this.selectedFacilityAtGeometry?.SITE_NAME
      );
      if(this.selectedFacilityAtGeometry?.LOCATION_DIRECTIONS_1 !==undefined){
        let directions =this.selectedFacilityAtGeometry?.LOCATION_DIRECTIONS_1.split('|');
        this.projectLocationDetailsForm.controls.address1.setValue(
          directions[0]
        );
        if( directions[1]!==undefined){
          this.projectLocationDetailsForm.controls.address2.setValue(
            directions[1]
          );
        }
      }
      if(this.projectLocationDetailsForm.controls.address2.value ==undefined){
        this.projectLocationDetailsForm.controls.address2.setValue(
          this.selectedFacilityAtGeometry?.LOCATION_DIRECTIONS_2
        );
      }

      this.projectLocationDetailsForm.controls.city.setValue(
        (this.selectedFacilityAtGeometry?.MUNICIPALITIES).trim().substring(0,65)
      );
      this.projectLocationDetailsForm.controls.zip.setValue(
        this.selectedFacilityAtGeometry?.ZIP
      );
    }
  }

  public async showValidationError(): Promise<void> {
    if (this.hideMap) {
      this.handleValidationError(this.errorMsgObj?.PRJ_LOC_NO_VALID_SEARCH);
    } else {
      if (!this.isGraphicExist()) {
        if (this.projectLocationForm.controls.searchBy.value === 'shapefile') {
          this.handleValidationError(this.errorMsgObj?.PRJ_LOC_SHAPEFILE_REQD);
        } else {
          this.handleValidationError(this.errorMsgObj?.PRJ_LOC_POLY_REQD);
        }
      } else {
        await this.findAnyFacilitiesExist();
      }
    }
  }
}

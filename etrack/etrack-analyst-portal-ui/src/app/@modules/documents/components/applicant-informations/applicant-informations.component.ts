import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import {
  AbstractControl,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  FormGroupDirective,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { isEmpty, get, set, isEqual } from 'lodash';
import { BehaviorSubject, fromEvent, Subject, Subscription } from 'rxjs';
import {
  atleastOneNumberValidator,
  checkBoxValidation,
  checkIfSpecialCharOrNumber,
  validateEmail,
} from 'src/app/@shared/applicationInformation.validator';
import { takeUntil } from 'rxjs/operators';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { ApplicantInfoServiceService } from 'src/app/@shared/services/applicant-info-service.service';
import { CommonService } from 'src/app/@shared/services/commonService';
import { DocumentService } from 'src/app/@shared/services/documentService';
import { EventEmitterService } from 'src/app/@shared/services/event-emitter.service';
import { ModalConfig } from 'src/app/modal.config';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { ApplicantInformationsSearchComponent } from '../applicant-informations-search/applicant-informations-search.component';
import { HttpErrorResponse } from '@angular/common/http';
import { Utils } from 'src/app/@shared/services/utils';
import { ErrorService } from '../../../../@shared/services/errorService';
import { environment } from 'src/environments/environment';
@Component({
  selector: 'app-applicant-informations',
  templateUrl: './applicant-informations.component.html',
  styleUrls: ['./applicant-informations.component.scss'],
})
export class ApplicantInformationsComponent implements OnInit {
  @ViewChild('confirmApplicant')
  confirmApplicant!: CustomModalPopupComponent;
  @ViewChild('confirmContact')
  confirmContact!: CustomModalPopupComponent;
  @ViewChild('searchModal')
  private searchModal!: CustomModalPopupComponent;
  @ViewChild('pendingPopup', { static: true })
  congirmationModal!: PendingChangesPopupComponent;
  @ViewChild('selectionModal')
  private selectionModal!: CustomModalPopupComponent;
  @ViewChild('warningModal', { static: true }) ownerWarning!: any;
  @ViewChild('confirmationWarningPopup', { static: true })
  confirmationWarningPopup!: PendingChangesPopupComponent;
  @ViewChild('closeSearchApplicantInfoModal', { static: true })
  closeSearchApplicantInfoModal!: any;
  systemParameters:any;

  publicIdIfEmpty = new UntypedFormControl(
    '',
    Validators.compose([
      Validators.required,
      Validators.maxLength(10),
      Validators.pattern('^[0-9]+$'),
    ])
  );
  @ViewChild('emptyPublicIdModal', { static: true })
  emptyPublicIdModal!: any;

  @ViewChild('ngForm') ngForm!: FormGroupDirective;
  @ViewChild('businessNameInput') businessNameInput!: ElementRef;

  modalReference!: NgbModalRef;
  closeResult!: string;
  closedAllPopup: boolean = false;

  applicantType: any = '';
  category: string = '';
  // firstFormGroup!: FormGroup;
  secondFormGroup!: UntypedFormGroup;
  isEditable = false;
  selectedApplicantType: any = '';
  basicDetailsFormGroup!: UntypedFormGroup;
  selectedApplicants: any = [];
  applicantDetails: any = {};
  errorMsgObj: any = {};
  userRoles: any = [];
  userRole = UserRole;
  hideForm: boolean = false;
  modalRef!: NgbModalRef;
  subscriptions: Subscription[] = [];
  @Input() isFromScreen: string = '';
  @Input() currentTab: any;
  @Input() selectedSearchApplicantType: string = '';
  @Input() searchedApplicantData: any = null;
  @Output() closeClicked = new EventEmitter();
  isOtherSelected: boolean = false;
  areYouIncorporatedBoolean: boolean = true;
  currentAddressType: any = 'us';
  configObject: any = {};
  stateOfIncorporationString: string = 'NY';
  popUpTitles = ['Applicant ', 'Property Owner', 'Contact/Agent'];
  popupData = {
    title: this.popUpTitles[0],
    details: '',
  };
  addApplicant: boolean = false;
  applicantData: string = '';
  @HostListener('window:keydown.tab', ['$event'])
  isBusinessVerified = '';
  addressId = '';
  addressType = '';
  @Input() applicantId = '';
  showBottomBorder: boolean = true;

  modalConfig: { title: string; showHeader: boolean };
  confirmConfig: { title: string; showHeader: boolean };
  searchConfig: { title: string; showHeader: boolean; showClose: boolean };
  closeConfig: { title: string; showHeader: boolean };
  isOpenConfirmPopUp = new BehaviorSubject<boolean>(false);
  isOpenContactPop = new BehaviorSubject<boolean>(false);
  isCloseConfirmPopUp = new BehaviorSubject<boolean>(false);
  isOpenPopUp = new BehaviorSubject<boolean>(false);
  isOpenPopupApplicants = new BehaviorSubject(false);
  //isOpenCloseSearchPopUp = new BehaviorSubject<boolean>(false);
  //iscloseSearchApplicantInfoModal = new BehaviorSubject<boolean>(false);
  headerText: string = '';
  isOwner: boolean = false;
  @Input() applicantSearchParam: string = '';
  @Input() applicantDetailsHistory!: any;
  currentApplicantData: any = {};
  verifyBusinessList: string[] = [];
  verifyBusinessSelected: string = '';
  verifiedBusinessName: string = '';
  originalBusinessName: string = '';
  showVerifyListBox: boolean = false;
  showTooManyResultsError: boolean = false;
  showUpdateBusinessNameError: boolean = false;
  showBizNameLessThanWarning: boolean = false;
  showBizLookUpRequiredError: boolean = false;
  showDOSDownError: boolean = false;
  applicantsExistingList: any;
  cloneApplicantsExistingList: any;
  contactAgentExistingList: any;
  propertyOwnerExistingList: any;
  countApplicants: number = 0;
  isClosevalidationCheck: boolean = false;
  showAddNewApplicantPopup: boolean = false;
  isValidateMode: boolean = false;
  isValidatedMode: boolean = false;
  mode = localStorage.getItem('mode');
  yieldSignPath = 'assets/icons/yieldsign.svg';
  editSignPath = 'assets/icons/revert_symbol.svg';
  applicantsValidated: boolean = false;
  activityId: any = '';
  fromSearch!: string;
  private unsubscriber: Subject<void> = new Subject<void>();
  nextClicked: boolean = false;
  duplicatePublicId: boolean = false;
  showServerError = false;
  serverErrorMessage!: string;
  @HostListener('window:keydown.tab', ['$event'])
  onTabClick(e: any) {
    let tabIndex = e.target.getAttribute('tabindex');
  }

  get applicantsReadonly() {
    if (
      this.currentTab == 1 &&
      !isEmpty(this.cloneApplicantsExistingList) &&
      !isEmpty(this.applicantDetails)
    ) {
      const existing = this.cloneApplicantsExistingList.find(
        (item: any) => item.applicantId == this.applicantDetails.applicantId
      );
      return !isEmpty(existing);
    }

    return this.mode === 'read';
  }

  get isModeValidate() {
    return this.mode == 'validate';
  }
  enteredPublicId: any;
  isValidPublicId: boolean = true;
  publicIdModalReference!: NgbModalRef;
  constructor(
    private _formBuilder: UntypedFormBuilder,
    private applicantInfoService: ApplicantInfoServiceService,
    private applicantService: ApplicantInfoServiceService,
    private commonService: CommonService,
    private docService: DocumentService,
    private modalService: NgbModal,
    public router: Router,
    private cdr: ChangeDetectorRef,
    private eventEmitterService: EventEmitterService,
    public utils: Utils,
    private errorService: ErrorService
  ) {
    this.modalConfig = {
      title: 'New York State Department of Environmental Conservation',
      showHeader: false,
    };
    this.confirmConfig = {
      title: '',
      showHeader: false,
    };
    this.searchConfig = {
      title: 'New York State Department of Environmental Conservation',
      showHeader: true,
      showClose: true,
    };
    this.closeConfig = {
      title: '',
      showHeader: false,
    };
    this.initiateBasicDetailsForm();
  }

  getConfig() {
    this.commonService.getAllConfigurations().then(
      (response) => {
        if (response) {
          this.configObject = response;
        }
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  onMiddleKeyPress(event: any) {
    return /[a-z]/i.test(event.key);
  }

  ngOnInit(): void {
    this.commonService.getSystemParameters().subscribe(data=>{
      this.systemParameters=data;
    });
    this.publicIdIfEmpty.valueChanges.subscribe(() => {
      console.log(this.publicIdIfEmpty.errors);
      this.isValidPublicId = true;
      this.enteredPublicId = true;
      this.duplicatePublicId = false;
    });
    this.fromSearch = this.commonService.getFromScreen();
    if (
      localStorage.getItem('projectId') &&
      this.selectedApplicants.length > 0
    ) {
      if (this.currentTab == 2) {
        this.getContactDetails(this.selectedApplicants[0]);
      } else {
        this.getAppicantDetails(this.selectedApplicants[0]);
      }
    }

    if (
      localStorage.getItem('mode') === 'validate' &&
      this.fromSearch !== 'search' &&
      this.isFromScreen !== 'selection'
    ) {
      this.applicantInfoService
        .viewApplicantInfo(this.applicantId)
        .toPromise()
        .then(
          (data: any) => {
            this.applicantDetailsHistory = data.applicantHistory;
            this.currentApplicantData = data.applicant;
            this.applicantDetails = data.applicant;
          },
          (error: any) => {
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          }
        );
    }
    this.countApplicants = this.selectedApplicants?.length || 0;
    this.selectedApplicantType == this.selectedSearchApplicantType;
    this.userRoles = this.commonService.roles;
    this.getAllErrorMsgs();
    this.getConfig();
    this.selectedApplicants = this.commonService.getApplicants();
    // if(this.fromSearch === 'search'){
    // //  this.addressType = '0';
    // }
    this.initiateBasicDetailsForm();
    if (this.currentTab == 0) {
      this.popupData.title = this.popUpTitles[0];
      this.headerText = 'Applicant';
      this.activityId = '7';
    } else if (this.currentTab == 1) {
      this.headerText = 'Property Owner';
      this.popupData.title = this.popUpTitles[1];
      this.activityId = '8';
    } else if (this.currentTab == 2) {
      this.popupData.title = this.popUpTitles[2];
      this.headerText = 'Contact/Agent';
      this.activityId = '9';
      this.basicDetailsFormGroup.get('');
    }
    if (this.searchedApplicantData && this.isFromScreen === 'search') {
      if (
        this.selectedSearchApplicantType === 'I' ||
        this.selectedSearchApplicantType === 'P'
      ) {
        this.formControls.firstName.setValue(
          this.searchedApplicantData.firstName
        );
        this.selectedApplicantType = this.selectedSearchApplicantType;
        this.formControls.lastName.setValue(
          this.searchedApplicantData.lastName
        );
        const sessionApplicantId = sessionStorage.getItem(
          'applicationTypeCode'
        );
        const isFirstApplicant = sessionStorage.getItem('isFirstApplicant');
        if (sessionApplicantId == '1' && isFirstApplicant == 'true') {
          this.formControls.owner.setValue(true);
          this.isOwner = true;
        }
      } else if (
        (this.selectedApplicantType === 'X' ||
          this.selectedApplicantType === 'T' ||
          this.selectedApplicantType === 'C') &&
        this.applicantDetails
      ) {
        this.formControls.businessName.setValue(
          this.searchedApplicantData.firstName
        );
      } else if (
        (this.selectedApplicantType === 'F' ||
          this.selectedApplicantType === 'S' ||
          this.selectedApplicantType === 'M') &&
        this.applicantDetails
      ) {
        this.formControls.governmentAgencyName.setValue(
          this.searchedApplicantData.firstName
        );
      }
    }
    // this.subscriptions.push(
    //   this.commonService.closeAppSearchModal.subscribe((val: boolean) => {
    //     if (val && this.modalRef) {
    //       this.modalRef.close();
    //     }
    //   })
    // );

    this.secondFormGroup = this._formBuilder.group({
      secondCtrl: [''],
    });

    history.pushState(null, '');

    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.businessNameInput?.nativeElement?.focus();
    }, 1000);
  }

  ngAfterViewChecked() {
    this.setFormControlValidations();
  }
  toggleValue(formControlName: any, value: any) {
    if (this.applicantsValidated) {
      return;
    }
    this.basicDetailsFormGroup?.get(formControlName)?.setValue(value);
    this.basicDetailsFormGroup.controls['verified'].setValue(false);
    this.basicDetailsFormGroup.controls['unverified'].setValue(false);
    this.updateVerifiedAndUnverifiedValidations(
      this.areYouIncorporatedBoolean,
      this.stateOfIncorporationString
    );
  }
  checkValuesIfValid(valObj: any) {
    let validFormData = JSON.parse(JSON.stringify(valObj));
    for (const property in validFormData) {
      if (validFormData[property] && property !== 'emailAddress' && property !== 'dba') {
        validFormData[property] = validFormData[property]
          .replace(/[^[0-9.]/g, '')
          .replace(/(\..*)\./g, '$1');
      }
    }
    return validFormData;
  }

  clearValidators(applicantType: string) {
    if (applicantType == 'I' || applicantType == 'P') {
      this.formControls.firstName.clearValidators();
      this.formControls.lastName.clearValidators();
    } else if (
      this.selectedApplicantType == 'X' ||
      this.selectedApplicantType == 'T' ||
      this.selectedApplicantType == 'C'
    ) {
      this.formControls.businessName.clearValidators();
      this.formControls.taxpayerId.clearValidators();
    } else if (
      this.selectedApplicantType == 'F' ||
      this.selectedApplicantType == 'S' ||
      this.selectedApplicantType == 'M'
    ) {
      this.formControls.governmentAgencyName.clearValidators();
      this.basicDetailsFormGroup.updateValueAndValidity();
    }
  }
  private setFormControlValidations(): void {
    if (
      this.selectedApplicantType == 'I' ||
      this.selectedApplicantType == 'P'
    ) {
      this.updateFormControlValidation(
        this.formControls.firstName,
        Validators.required
      );
      this.updateFormControlValidation(
        this.formControls.middleName,
        checkIfSpecialCharOrNumber
      );
      this.updateFormControlValidation(
        this.formControls.lastName,
        Validators.required
      );
    } else {
      this.formControls.firstName.clearValidators();
      // this.formControls.firstName.updateValueAndValidity();
      this.formControls.middleName.clearValidators();
      //this.formControls.middleName.updateValueAndValidity();
      this.formControls.lastName.clearValidators();
      // this.formControls.lastName.updateValueAndValidity();
      //  this.basicDetailsFormGroup.updateValueAndValidity();
    }
    if (
      this.selectedApplicantType == 'X' ||
      this.selectedApplicantType == 'T' ||
      this.selectedApplicantType == 'C'
    ) {
      this.updateFormControlValidation(this.formControls.businessName, [
        Validators.required,
        Validators.minLength(3),
      ]);
      if (this.areYouIncorporatedBoolean) {
        this.updateFormControlValidation(
          this.formControls.taxpayerId,
          Validators.minLength(9)
        );
      } else {
        this.formControls.taxpayerId.clearValidators();
      }
    } else {
      this.formControls.businessName.clearValidators();
      this.formControls.taxpayerId.clearValidators();
    }
    if (
      this.selectedApplicantType == 'F' ||
      this.selectedApplicantType == 'S' ||
      this.selectedApplicantType == 'M'
    ) {
      this.updateFormControlValidation(
        this.formControls.governmentAgencyName,
        Validators.required
      );
    } else {
      this.formControls.governmentAgencyName.clearValidators();
    }
    this.basicDetailsFormGroup.updateValueAndValidity();
  }

  private updateFormControlValidation(
    control: AbstractControl,
    validators: ValidatorFn | ValidatorFn[]
  ): void {
    control.setValidators(validators);
    control.updateValueAndValidity();
  }

  get formControls() {
    return this.basicDetailsFormGroup.controls;
  }

  getAppicantDetails(applicantId: any) {
    var applicantType =
      this.commonService.getFromScreen() == 'search'
        ? this.commonService.getselectedApplicantype()
        : '';

    if (applicantId != 0) {
      this.utils.emitLoadingEmitter(true);
      this.applicantInfoService
        .getApplicantDetailsById(applicantId, applicantType)
        .subscribe(
          (response) => {
            this.applicantDetails = response;
            this.utils.emitLoadingEmitter(false);
            this.addressType = this.applicantDetails?.address?.adrType;
            if (this.currentTab == 1) {
              this.getCloneApplicantSummary();
            }

            //this.applicantDetails = response.applicant;
            // if (this.isFromScreen === 'associate') {
            //   this.applicantDetailsHistory = response.applicantHistory;
            // } else {
            //   this.applicantDetailsHistory = []
            // }
            //this.applicantDetailsHistory = []
            this.setFormData();
            
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
            
          }
        );
    }
  }
  getContactDetails(applicantId: any) {
    this.utils.emitLoadingEmitter(true);
    var applicantType =
      this.commonService.getFromScreen() == 'search'
        ? this.commonService.getselectedApplicantype()
        : '';
        if(applicantId==0){
          this.utils.emitLoadingEmitter(false);
          return;
        }
    this.applicantInfoService
      .getContactDetailsById(applicantId, applicantType)
      .subscribe(
        (response) => {
          console.log("From search screen", this.utils)
          this.utils.emitLoadingEmitter(false);
          this.applicantDetails = response;
          this.addressType = this.applicantDetails?.address?.adrType;
          this.setFormData();
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      );
  }
  setFormData() {
    if (this.applicantDetails) {
      if (this.isFromScreen != 'associate') {
        this.selectedApplicantType =
          this.selectedSearchApplicantType.length > 0
            ? this.selectedSearchApplicantType
            : this.applicantDetails.publicTypeCode;
      }
      if (this.isFromScreen == 'associate') {
        this.selectedApplicantType = this.applicantDetails.publicTypeCode;
      }

      if (this.applicantDetails.validatedInd === 'Y') {
        this.applicantsValidated = true;
      }

      if (this.applicantDetails) {
        this.addressId = this.applicantDetails.address.addressId;
        this.applicantId = this.applicantDetails.applicantId;
        this.addressType = this.applicantDetails.address.adrType;
        localStorage.setItem(
          'contactForm',
          JSON.stringify({...this.applicantDetails?.contact, dba: this.applicantDetails?.dba})
        );
      }

      if (
        this.selectedApplicantType == 'I' ||
        this.selectedApplicantType == 'P'
      ) {
        this.formControls.firstName.setValue(
          this.applicantDetails.individual.firstName
        );
        this.formControls.middleName.setValue(
          this.applicantDetails.individual.middleName
        );
        this.formControls.lastName.setValue(
          this.applicantDetails.individual.lastName
        );
        this.formControls.suffix.setValue(
          this.applicantDetails.individual.suffix
        );
      }

      if (
        (this.selectedApplicantType === 'X' ||
          this.selectedApplicantType === 'T' ||
          this.selectedApplicantType === 'C') &&
        this.applicantDetails
      ) {
        this.formControls.businessName.setValue(
          this.applicantDetails.organization.busOrgName
        );

        if (this.applicantDetails.organization.isIncorporated) {
          this.formControls.incorporated.setValue(
            this.applicantDetails.organization.isIncorporated
          );
          if (this.applicantDetails.organization.isIncorporated == 'N') {
            this.areYouIncorporatedBoolean = false;
          }
          this.updateVerifiedAndUnverifiedValidations(
            this.areYouIncorporatedBoolean,
            this.stateOfIncorporationString
          );
        } else {
          this.formControls.incorporated.setValue('Y');
        }
        if (this.applicantDetails.organization.taxPayerId) {
          let unformattedTaxpayerId =
            this.applicantDetails.organization.taxPayerId;
          let formattedTaxpayerId =
            unformattedTaxpayerId.substring(0, 2) +
            '-' +
            unformattedTaxpayerId.substring(2);
          this.formControls.taxpayerId.setValue(formattedTaxpayerId);
        }

        if (this.applicantDetails.organization.incorporationState) {
          this.formControls.stateOfIncorporation.setValue(
            this.applicantDetails.organization.incorporationState
          );
          this.stateOfIncorporationString =
            this.applicantDetails.organization.incorporationState;
        }
        if (this.applicantDetails.organization.incorporationState == 'OT') {
          this.isOtherSelected = true;
        }
        this.formControls.territoryOrCountry.setValue(
          this.applicantDetails.organization.incorporateCountry
        );
        if (this.applicantDetails.organization.businessVerified === 'Y') {
          this.isBusinessVerified = 'Y';
          this.formControls.verified.setValue(true);
        } else if (
          this.applicantDetails.organization.businessVerified === 'N'
        ) {
          this.isBusinessVerified = 'N';
          this.formControls.unverified.setValue(true);
        }
        this.updateVerifiedAndUnverifiedValidations(
          this.areYouIncorporatedBoolean,
          this.stateOfIncorporationString
        );
      }

      if (
        (this.selectedApplicantType === 'F' ||
          this.selectedApplicantType === 'S' ||
          this.selectedApplicantType === 'M') &&
        this.applicantDetails
      ) {
        this.formControls.governmentAgencyName.setValue(
          this.applicantDetails.govtAgencyName
        );
      }

      const publicTypeCode =
        this.selectedSearchApplicantType.length > 0
          ? this.selectedSearchApplicantType
          : this.applicantDetails.publicTypeCode;
      this.formControls.applicantType.setValue(publicTypeCode);
      //session storage isFromScreen is set in applicant-informations-search.components.ts in onSubmit function
      //session storage applicationTypeCode is set in associated-applicants.components.ts in getApplicantSummary function
      const sessionApplicantId = sessionStorage.getItem('applicationTypeCode');
      const sessionIsScreenFrom = sessionStorage.getItem('isFromScreen');
      const isFirstApplicant = sessionStorage.getItem('isFirstApplicant');
      this.applicantDetails.propertyRelationships =
        this.applicantDetails.propertyRelationships.filter((v: any) => v);
      this.applicantDetails.propertyRelationships.forEach((id: any) => {
        if (id == '1') {
          this.formControls.owner.setValue(true);
          this.isOwner = true;
        } else if (id == '2') {
          this.formControls.operator.setValue(true);
        } else if (id == '3') {
          this.formControls.lessee.setValue(true);
        }
      });
      if (
        (isEqual(sessionIsScreenFrom, 'selection') ||
          isEqual(sessionIsScreenFrom, 'search')) &&
        !isEmpty(sessionApplicantId)
      ) {
        if (sessionApplicantId == '1' && isFirstApplicant == 'true') {
          this.formControls.operator.setValue(false);
          this.formControls.lessee.setValue(false);
          this.formControls.owner.setValue(true);
          this.isOwner = true;
        } else {
          this.formControls.operator.setValue(false);
          this.formControls.lessee.setValue(false);
          this.formControls.owner.setValue(false);
          this.isOwner = false;
        }
        // else if (sessionApplicantId == '2') {
        //   this.formControls.operator.setValue(true);
        // } else if (sessionApplicantId == '3') {
        //   this.formControls.lessee.setValue(true);
        // }
      }

      let contactGroup: any = this.formControls.contactDetailsFormGroup;

      if (this.addressType == '0') {
        if (this.applicantDetails.contact.cellNumber) {
          contactGroup.controls.cellPhNumber.setValue(
            this.applicantDetails.contact.cellNumber
          );
        }
        if (this.applicantDetails.contact.workPhoneNumber) {
          if (this.applicantDetails.contact.workPhoneNumber?.length) {
            contactGroup.controls.extension.enable();
          } else {
            contactGroup.controls.extension.disable();
            contactGroup.controls.extension.setValue('');
          }
          contactGroup.controls.workPhNumber.setValue(
            this.applicantDetails.contact.workPhoneNumber
          );
        }
        if (this.applicantDetails.contact.homePhoneNumber) {
          contactGroup.controls.homePhNumber.setValue(
            this.applicantDetails.contact.homePhoneNumber
          );
        }
      } else {
        contactGroup.controls.cellPhNumber.setValue(
          this.applicantDetails.contact.cellNumber
        );
        contactGroup.controls.workPhNumber.setValue(
          this.applicantDetails.contact.workPhoneNumber
        );
        if (this.applicantDetails.contact?.workPhoneNumber?.length) {
          contactGroup.controls.extension.enable();
        } else {
          contactGroup.controls.extension.disable();
          contactGroup.controls.extension.setValue('');
        }
        contactGroup.controls.homePhNumber.setValue(
          this.applicantDetails.contact.homePhoneNumber
        );
      }

      contactGroup.controls.emailAddress.setValue(
        this.applicantDetails.contact.emailAddress
      );
      contactGroup.controls.extension.setValue(
        this.applicantDetails.contact.workPhoneNumberExtn
      );

      if(this.selectedApplicantType == 'P') {
        if(this.applicantDetails.dba) {
          contactGroup.controls.dba.setValue(
            this.applicantDetails.dba
          );
        }
      }

      this.formControls.contactDetailsFormGroup = contactGroup;
      localStorage.setItem('contactGroup', contactGroup);

      this.formControls.streetAddress1.setValue(
        this.applicantDetails.address.streetAdr1
      );
      this.formControls.streetAddress2.setValue(
        this.applicantDetails.address.streetAdr2
      );

      this.formControls.city.setValue(this.applicantDetails.address.city);
      this.formControls.postOffice.setValue(this.applicantDetails.address.city);
      this.formControls.zipCode.setValue(this.applicantDetails.address.zipCode);
      this.formControls.state.setValue(this.applicantDetails.address.state);
      this.formControls.addressType.setValue(
        this.applicantDetails.address.adrType == '0' ? 'us' : 'nonus'
      );
      this.currentAddressType =
        this.applicantDetails.address.adrType == '0' ? 'us' : 'nonus';

      this.formControls.country.setValue(
        this.applicantDetails?.address?.country
          ? this.applicantDetails?.address?.country
          : ''
      );
      this.formControls.stateProvince.setValue(
        this.applicantDetails.address.state
      );
      this.formControls.postalCode.setValue(
        this.applicantDetails.address.postalCode
      );
      if(this.applicantDetails.address.attentionName) {
        this.formControls.attention.setValue(
          this.applicantDetails.address.attentionName
        );
      }
    }
  }

  initiateBasicDetailsForm() {
    this.basicDetailsFormGroup = this._formBuilder.group(
      {
        applicantType: [this.selectedSearchApplicantType, Validators.required],
        firstName: [''],
        middleName: [''],
        lastName: [''],
        businessName: [''],
        governmentAgencyName: [''],
        suffix: [''],
        owner: [false],
        operator: [false],
        lessee: [false],
        contactDetailsFormGroup: this._formBuilder.group({
          cellPhNumber: [''],
          workPhNumber: [''],
          homePhNumber: [''],
          emailAddress: ['', [validateEmail]],
          extension: [{ value: '', disabled: true }],
          dba: [''],
        }),
        streetAddress1: ['', Validators.required],
        streetAddress2: [''],
        postOffice: ['', Validators.required],
        state: ['', Validators.required],
        zipCode: [
          '',
          [
            Validators.minLength(5),
            Validators.maxLength(5),
            Validators.required,
          ],
        ],
        addressType: ['us'],
        city: ['', [Validators.required]],
        stateProvince: ['', [Validators.required]],
        postalCode: ['', [Validators.required]],
        country: ['', [Validators.required]],
        attention: [''],
        incorporated: ['Y'],
        taxpayerId: [''],
        stateOfIncorporation: ['NY'],
        territoryOrCountry: [''],
        verified: [false],
        unverified: [false],
      },

      {
        validators: [
          checkBoxValidation('owner', 'operator', 'lessee', this.currentTab),
        ],
      }
    );
    /*
      This is the implemntation to get the search string from applicant-information-search screen to populate first/last name,
      biz name and govt agency name


    if(this.applicantSearchParam){
      if(this.selectedSearchApplicantType == 'I' || this.selectedSearchApplicantType == 'P'){
          let nameArray = this.applicantSearchParam.split('$');
          this.basicDetailsFormGroup.controls['firstName'].setValue(nameArray[0]);
          this.basicDetailsFormGroup.controls['lastName'].setValue(nameArray[1]);
      }

      if(this.selectedSearchApplicantType == 'X' || this.selectedSearchApplicantType == 'T' || this.selectedSearchApplicantType == 'C'){
        let businessName = this.applicantSearchParam.split('$');
        this.basicDetailsFormGroup.controls['businessName'].setValue(businessName[0].trim());
      }
      if(this.selectedSearchApplicantType == 'F' || this.selectedSearchApplicantType == 'S' || this.selectedSearchApplicantType == 'M'){
        let govAgencyName = this.applicantSearchParam.split('$');
        this.basicDetailsFormGroup.controls['governmentAgencyName'].setValue(govAgencyName[0].trim());
      }
    }
    */
    this.selectedApplicantType = this.selectedSearchApplicantType;
    this.basicDetailsFormGroup.controls['verified'].disable();
    this.basicDetailsFormGroup.controls['unverified'].disable();
  }
  checkAndrePopulate(isFirst: boolean) {
    console.log('repopulating');

    if (
      localStorage.getItem('contactForm') &&
      // this.isFromScreen !== 'search' &&
      this.isFromScreen !== 'selection'
    ) {
      console.log('were in this if ');

      let val: any = localStorage.getItem('contactForm')
        ? localStorage.getItem('contactForm')
        : {};
      console.log('here is contact form', val);

      let obj = JSON.parse(val);
      let valArray = Object.values(obj);
      valArray = valArray.filter((value) => value != '');
      if (valArray.length !== 0) {
        console.log('now were here');

        let contactGroup: any = this.formControls.contactDetailsFormGroup;
        let validFormData = this.checkValuesIfValid(obj);
        let newValidFormData = {};
        set(
          newValidFormData,
          'cellPhNumber',
          get(validFormData, isFirst ? 'cellNumber' : 'cellPhNumber', '')
        );
        set(
          newValidFormData,
          'workPhNumber',
          get(validFormData, isFirst ? 'workPhoneNumber' : 'workPhNumber', '')
        );
        set(
          newValidFormData,
          'homePhNumber',
          get(validFormData, isFirst ? 'homePhoneNumber' : 'homePhNumber', '')
        );
        set(
          newValidFormData,
          'extension',
          get(validFormData, isFirst ? 'workPhoneNumberExtn' : 'extension', '')
        );
        set(newValidFormData, 'dba', get(validFormData, 'dba', ''));
        set(
          newValidFormData,
          'emailAddress',
          get(validFormData, 'emailAddress', '')
        );
        console.log('here is obj setting to contact data', newValidFormData);

        contactGroup.setValue(newValidFormData);
        console.log('here is contact group', contactGroup);

        contactGroup.updateValueAndValidity();
        this.formControls.contactDetailsFormGroup.setValue(newValidFormData);
        this.formControls.contactDetailsFormGroup.updateValueAndValidity();
        console.log(this.formControls.contactDetailsFormGroup);
      } else {
        this.formControls.contactDetailsFormGroup.setValue(obj);
        this.formControls.contactDetailsFormGroup.updateValueAndValidity();
      }
    }
  }

  onAddressChange(e: any) {
    console.log('address changed');

    let formData = JSON.parse(
      JSON.stringify(this.basicDetailsFormGroup.getRawValue())
    );
    //this.checkAndrePopulate(e.isFirst);
    this.currentAddressType = e.addressType;
    localStorage.setItem('addressType', e.addressType);
    if (!e.isFirst) {
      localStorage.setItem(
        'contactForm',
        JSON.stringify(formData.contactDetailsFormGroup)
      );
    }

    console.log(
      'address changed',
      JSON.stringify(formData.contactDetailsFormGroup)
    );
  }

  ngOnChanges(changes: any) {
    this.selectedApplicants = this.commonService.getApplicants();
    this.setFormControlValidations();
  }
  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val) => {
      if (val) this.errorMsgObj = this.commonService.getErrorMsgsObj();
    });
  }

  //This is for submitting the form
  getApiData(formData: any) {
    let data = '';
    if (this.formControls.verified.value == true) {
      this.isBusinessVerified = 'Y';
    }
    if (this.formControls.unverified.value == true) {
      this.isBusinessVerified = 'N';
    }

    if (!this.areYouIncorporatedBoolean) {
      this.isBusinessVerified = '';
    }

    data = sessionStorage?.getItem('applicantsList')
      ? sessionStorage?.getItem('applicantsList') + ''
      : '';

    let apiData: any = {};
    apiData.publicTypeCode = this.selectedApplicantType;
    apiData.propertyRelationships = [];

    if (formData.owner) apiData.propertyRelationships.push('1');
    if (formData.operator) apiData.propertyRelationships.push('2');
    if (formData.lessee) apiData.propertyRelationships.push('3');
    apiData.applicantId = this.applicantId;
    if (
      this.selectedApplicantType === 'I' ||
      this.selectedApplicantType === 'P'
    ) {
      apiData.individual = {
        firstName: formData.firstName,
        middleName: formData.middleName,
        lastName: formData.lastName,
        suffix: formData.suffix,
      };

      const currSuffix = formData.suffix ? ' ' + formData.suffix : '';
      const currMiddleName = formData.middleName ? formData.middleName + ' ' : '';
      const currName = formData.firstName + ' ' + currMiddleName + formData.lastName + currSuffix;
      if(this.applicantDetails.individual) {
        const originalMiddleName = this.applicantDetails.individual.middleName ? 
                                 this.applicantDetails.individual.middleName + ' ' : '';
        const originalSuffix = this.applicantDetails.individual.suffix ? 
                                 ' ' + this.applicantDetails.individual.suffix : '';
        const originalName = this.applicantDetails.individual.firstName + ' ' +
                           originalMiddleName +
                           this.applicantDetails.individual.lastName + originalSuffix;
        if(!formData.owner && 
          (this.isFromScreen === 'search' ||
          this.isFromScreen === 'selection' ||
          this.isFromScreen == 'associate')
          ) {
            sessionStorage.setItem('applicantsList', this.removeApplicant(data, originalName));
          }
        data = this.filterApplicantsList(data, originalName, currName);
      } 
      else {
        data = this.filterApplicantsList(data, '', currName);
      }

      apiData.dba = formData.contactDetailsFormGroup.dba;
      if (formData.addressType == 'us') {
        apiData.address = {
          addressId: this.addressId,
          streetAdr1: formData.streetAddress1,
          streetAdr2: formData.streetAddress2,
          city: formData.postOffice,
          state: formData.state,
          zipCode: formData.zipCode,
          adrType: formData.addressType == 'us' ? '0' : '1',
        };
      } else {
        apiData.address = {
          addressId: this.addressId,
          streetAdr1: formData.streetAddress1,
          streetAdr2: formData.streetAddress2,
          city: formData.city,
          state: formData.stateProvince,
          postalCode: formData.postalCode,
          country: formData.country,
          adrType: formData.addressType == 'us' ? '0' : '1',
        };
      }
    }
    if (
      this.selectedApplicantType === 'X' ||
      this.selectedApplicantType === 'T' ||
      this.selectedApplicantType === 'C'
    ) {
      if (formData.incorporated === 'N') {
        formData.stateOfIncorporation = '';
      }

      apiData.organization = {
        busOrgName: formData.businessName,
        isIncorporated: formData.incorporated,
        taxPayerId: formData.taxpayerId?.replace('-', ''),
        incorporationState: formData.stateOfIncorporation,
        incorporateCountry: formData.territoryOrCountry,
        businessVerified: this.isBusinessVerified,
        verifiedLegalName: this.verifiedBusinessName,
      };

      let formerOrgName = this.applicantDetails.organization ? 
                          this.applicantDetails.organization.busOrgName : '';

      if(!formData.owner && 
        (this.isFromScreen === 'search' ||
          this.isFromScreen === 'selection' ||
          this.isFromScreen == 'associate')
      ) {
        sessionStorage.setItem('applicantsList', this.removeApplicant(data, 
                                formerOrgName));
      }
      data = this.filterApplicantsList(data, formerOrgName,
                                        formData.businessName);
                                      
      if (formData.addressType == 'us') {
        apiData.address = {
          addressId: this.addressId,
          streetAdr1: formData.streetAddress1,
          streetAdr2: formData.streetAddress2,
          city: formData.postOffice,
          state: formData.state,
          zipCode: formData.zipCode,
          adrType: formData.addressType == 'us' ? '0' : '1',
          attentionName:
            formData.attention || sessionStorage.getItem('us-attention'),
        };
      } else {
        apiData.address = {
          addressId: this.addressId,
          streetAdr1: formData.streetAddress1,
          streetAdr2: formData.streetAddress2,
          city: formData.city,
          state: formData.stateProvince,
          postalCode: formData.postalCode,
          country: formData.country,
          adrType: formData.addressType == 'us' ? '0' : '1',
          attentionName:
            formData.attention || sessionStorage.getItem('nonus-attention'),
        };
      }
    }
    if (
      this.selectedApplicantType === 'F' ||
      this.selectedApplicantType === 'S' ||
      this.selectedApplicantType === 'M'
    ) {
      apiData.govtAgencyName = formData.governmentAgencyName;
      const formerGovtAgencyName = this.applicantDetails.govtAgencyName ? 
                                   this.applicantDetails.govtAgencyName : '';
      if(!formData.owner && 
        (this.isFromScreen === 'search' ||
          this.isFromScreen === 'selection' ||
          this.isFromScreen == 'associate')
      ) {
        sessionStorage.setItem('applicantsList', this.removeApplicant(data, 
          formerGovtAgencyName));
      }
      data = this.filterApplicantsList(data, formerGovtAgencyName, 
                                        formData.governmentAgencyName);

      if (formData.addressType == 'us') {
        apiData.address = {
          addressId: this.addressId,
          streetAdr1: formData.streetAddress1,
          streetAdr2: formData.streetAddress2,
          city: formData.postOffice,
          state: formData.state,
          zipCode: formData.zipCode,
          adrType: formData.addressType == 'us' ? '0' : '1',
          attentionName:
            formData.attention || sessionStorage.getItem('us-attention'),
        };
      } else {
        apiData.address = {
          addressId: this.addressId,
          streetAdr1: formData.streetAddress1,
          streetAdr2: formData.streetAddress2,
          city: formData.city,
          state: formData.stateProvince,
          postalCode: formData.postalCode,
          country: formData.country,
          adrType: formData.addressType == 'us' ? '0' : '1',
          attentionName:
            formData.attention || sessionStorage.getItem('nonus-attention'),
        };
      }
    }
    apiData.contact = {
      cellNumber: formData.contactDetailsFormGroup.cellPhNumber,
      workPhoneNumber: formData.contactDetailsFormGroup.workPhNumber,
      homePhoneNumber: formData.contactDetailsFormGroup.homePhNumber,
      emailAddress: formData.contactDetailsFormGroup.emailAddress,
      workPhoneNumberExtn: formData.contactDetailsFormGroup.extension,
    };
    if (this.currentTab == 1 || this.currentTab == 2) {
      delete apiData.propertyRelationships;
    }
    if (formData.owner) {
      if (
        this.basicDetailsFormGroup.value.owner &&
        (this.isFromScreen === 'search' ||
          this.isFromScreen === 'selection' ||
          this.isFromScreen == 'associate')
      )
        sessionStorage.setItem('applicantsList', data);
    }

    if (this.applicantsValidated) {
      apiData.validatedInd = 'Y';
    } else {
      apiData.validatedInd = 'N';
    }
    return apiData;
  }

  attentionKeyUp() {
    //this.applicantDetails.addressType = 'us'
    this.basicDetailsFormGroup.controls.addressType.value == 'us'
      ? sessionStorage.setItem(
          'us-attention',
          this.basicDetailsFormGroup.controls.attention.value
        )
      : sessionStorage.setItem(
          'nonus-attention',
          this.basicDetailsFormGroup.controls.attention.value
        );
  }

  filterApplicantsList(applicantsList: string, previousName: string, currentName: string) {
    if(!applicantsList.includes(currentName + ', ') && 
       !applicantsList.endsWith(currentName)) {  
        applicantsList = this.removeApplicant(applicantsList, previousName);

        if(applicantsList) {
          applicantsList += ', ';
        }
        applicantsList += currentName;
    }
    return applicantsList;
  }

  removeApplicant(applicantsList: string, applicant: string) {
    if(applicantsList.includes(applicant + ', ')) {
      applicantsList = applicantsList.replace(applicant + ', ', '');
    }
    else if (applicantsList.endsWith(', ' + applicant)) {
      applicantsList = applicantsList.replace(', ' + applicant, '');
    }
    else if(applicantsList === applicant) {
      applicantsList = '';
    }
    return applicantsList;
  }

  onFormSubmit(fromYesCloseSearchApplicantInformation = false) {
    
    this.nextClicked = true;
    this.showServerError = false;
    switch (this.formControls.applicantType.value) {
      case 'T':
      case 'X':
      case 'C':
        this.applicantData = this.formControls.businessName.value;
        break;
      case 'I':
      case 'P':
        this.applicantData =
          this.formControls.firstName.value +
          ' ' +
          this.formControls.lastName.value;
        break;
      case 'S':
      case 'F':
      case 'M':
        this.applicantData = this.formControls.governmentAgencyName.value;
        break;
    }
    if (
      !(this.formControls.verified.value && this.formControls.unverified.value)
    ) {
      this.showBizLookUpRequiredError = true;
    }
    this.isClosevalidationCheck = false;
    this.basicDetailsFormGroup.get('applicantType')?.markAsPristine();
    if (
      this.formControls.verified.errors ||
      this.formControls.unverified.errors
    ) {
      return;
    }

    if (this.basicDetailsFormGroup.valid) {
      let formData = this.basicDetailsFormGroup.getRawValue();
      let apiData = this.getApiData(formData);

      if (this.applicantDetails) {
        apiData.edbApplicantId = this.applicantDetails.edbApplicantId;

        if (this.applicantDetails.address) {
          apiData.address.edbAddressId =
            this.applicantDetails.address.edbAddressId;
        }
      }
      if (
        this.isFromScreen === 'search' &&
        !fromYesCloseSearchApplicantInformation
      ) {
        if (this.applicantDetails && this.applicantDetails.address) {
          apiData.edbApplicantId = this.applicantDetails.edbApplicantId;
          apiData.address.edbAddressId =
            this.applicantDetails.address.edbAddressId;
        }
        this.showAddNewApplicantPopup = true;
      }
      if (
        isEmpty(get(apiData, 'edbApplicantId', null)) &&
        this.publicIdIfEmpty?.value
      ) {
        set(apiData, 'edbApplicantId', this.publicIdIfEmpty?.value);
      }
      if (
        this.applicantDetails &&
        Object.keys(this.applicantDetails).length > 0 &&
        !this.addApplicant &&
        ((this.isFromScreen === 'search' &&
          this.applicantDetails.applicantId != null) ||
          this.isFromScreen === 'selection' ||
          this.isFromScreen === 'associate')
      ) {
        apiData.applicantId = this.applicantDetails.applicantId;
        apiData.edbApplicantId = this.applicantDetails.edbApplicantId;
        if (
          isEmpty(get(apiData, 'edbApplicantId', null)) &&
          this.publicIdIfEmpty?.value
        ) {
          set(apiData, 'edbApplicantId', this.publicIdIfEmpty?.value);
        }

        let category = 'P';
        if (this.currentTab && this.currentTab == 2) {
          category = 'C';
        } else if (this.currentTab && this.currentTab == 1) {
          category = 'O';
        }
        if (
          (this.isFromScreen === 'selection' ||
            this.isFromScreen === 'search' ||
            this.isFromScreen === 'associate') &&
          (this.basicDetailsFormGroup.dirty ||
            !this.basicDetailsFormGroup.dirty ||
            this.applicantsValidated)
        ) {
          this.verifyBusinessList = [];
          this.showVerifyListBox = false;
          this.utils.emitLoadingEmitter(true);
          this.applicantInfoService
            .updateApplicantInfo(apiData, category)
            .subscribe(
              (response) => {
                this.addApplicant = false;
                this.commonService.emitSubmitStatus.next(true);
                this.applicantDetails = {};
                this.utils.emitLoadingEmitter(false);
                if (this.selectedApplicants.length > 0)
                  this.selectedApplicants.splice(0, 1);
                if (
                  localStorage.getItem('projectId') &&
                  this.selectedApplicants.length > 0
                ) {
                  if (
                    this.userRoles &&
                    !this.userRoles.includes('Online Submitter')
                  ) {
                    if (this.selectedApplicants[0] != '0') {
                      this.ngForm.resetForm();
                      // this.ngForm.reset();
                      //this.basicDetailsFormGroup.reset();
                      this.getAppicantDetails(this.selectedApplicants[0]);
                      document.body.scrollTop = 0;
                      document.documentElement.scrollTop = 0;
                    } else {
                      this.isOpenConfirmPopUp.next(false);
                      this.confirmApplicant.close();
                      this.isOpenPopUp.next(true);
                      this.searchModal.open('responsive-modal1');
                    }
                  } else {
                    this.addNewApplicant();
                  }
                } else {
                  //!!TODO Show popup
                  if (this.isFromScreen !== 'associate') {
                    this.open(this.confirmApplicant);
                  }
                }
                // this.initiateBasicDetailsForm();
                this.ngForm.reset();
                this.ngForm.resetForm();
                setTimeout(() => {
                  this.basicDetailsFormGroup
                    .get('applicantType')
                    ?.setValue(this.selectedSearchApplicantType);
                });
                
              },
              (err: HttpErrorResponse) => {
                if (err.status === 409) {
                  this.utils.emitLoadingEmitter(false);
                  this.isOpenContactPop.next(true);
                  this.confirmContact.open();
                } else {
                  this.utils.emitLoadingEmitter(false);
                  this.serverErrorMessage =
                    this.errorService.getServerMessage(err);
                  this.showServerError = true;
                  throw err;
                }
              }
            );
          this.countApplicants = this.countApplicants - 1;
        } else {
          this.utils.emitLoadingEmitter(false);
          this.isOpenConfirmPopUp.next(false);
          this.confirmApplicant.close();
          return;
        }
      } else {
        this.utils.emitLoadingEmitter(false);
        let category = 'P';
        if (this.currentTab && this.currentTab == 2) {
          category = 'C';
        } else if (this.currentTab && this.currentTab == 1) {
          category = 'O';
        }

        this.utils.emitLoadingEmitter(true);
        this.applicantInfoService
          .submitApplicantInfo(apiData, category)
          .subscribe(
            (response) => {
              this.utils.emitLoadingEmitter(false);
              this.commonService.emitSubmitStatus.next(true);
              this.applicantDetails = {};
              if (this.selectedApplicants.length > 0)
                this.selectedApplicants.splice(0, 1);
              if (
                localStorage.getItem('projectId') &&
                this.selectedApplicants.length > 0
              ) {
                if (
                  this.userRoles &&
                  !this.userRoles.includes('Online Submitter')
                ) {
                  if (this.selectedApplicants[0] != '0') {
                    this.getAppicantDetails(this.selectedApplicants[0]);
                    document.body.scrollTop = 0;
                    document.documentElement.scrollTop = 0;
                  } else {
                    this.closeClicked.emit('open-search');
                  }
                } else {
                  this.addNewApplicant();
                }
              } else {
                //!!TODO Show popup
                if (this.showAddNewApplicantPopup) {
                  this.open(this.confirmApplicant);
                }
              }
              // this.selectedApplicants
              // this.initiateBasicDetailsForm();
              this.ngForm.reset();
              setTimeout(() => {
                this.basicDetailsFormGroup
                  .get('applicantType')
                  ?.setValue(this.selectedSearchApplicantType);
              });
              // if (this.countApplicants == 0) {
              //   this.isOpenConfirmPopUp.next(false);
              //   this.confirmApplicant.close();
              //   if (this.isFromScreen === 'associate') {
              //     this.closeClicked.emit('close');
              //   }
              // }
            },
            (err) => {
              //Akhila's Change
              if (err.status === 409) {
                this.utils.emitLoadingEmitter(false);
                this.isOpenContactPop.next(true);
                this.confirmContact.open();
              } else {
                this.utils.emitLoadingEmitter(false);
                this.serverErrorMessage =
                  this.errorService.getServerMessage(err);
                this.showServerError = true;
                throw err;
              }
            }
          );
        this.countApplicants = this.countApplicants - 1;
      }
      if (this.countApplicants == 0) {
        this.isOpenConfirmPopUp.next(false);
        this.confirmApplicant.close();
        if (this.isFromScreen === 'associate') {
          this.closeClicked.emit('close');
        }
      }
    }
    return;
  }

  closeSerchModal(e: any) {
    this.commonService.closeApplicantModal.next(true);
    this.cdr.detectChanges();
    this.router.navigate(['/associated-applicants']);
  }
  closePopup(e: any) {
    this.isOpenPopupApplicants.next(false);
    this.selectionModal.close();
  }
  onOkclick(e: any) {
    if (e === 'ok') this.closeModal();
  }

  onPendingChangesOkclick(e: any) {
    console.log("emit on ok ",e)
    this.onOkclick(e);
    //this.congirmationModal.close();
  }

  yesCloseSearchApplicantInformation() {
    this.addApplicant = true;
    this.isCloseConfirmPopUp.next(true);
    this.closedAllPopup = true;
    this.isOpenConfirmPopUp.next(false);
    if (this.basicDetailsFormGroup.status == 'INVALID') {
      this.isClosevalidationCheck = true;
      this.modalReference.dismiss();
    } else {
      this.isClosevalidationCheck = false;
      this.showAddNewApplicantPopup = false;
      this.onFormSubmit(true);
      this.eventEmitterService.onFirstComponentButtonClick();
      this.closeModal();
      this.modalService.dismissAll();
    }
  }

  noCloseSearchApplicantInformation() {
    this.closeClicked.emit('close');
    this.isCloseConfirmPopUp.next(true);
    this.eventEmitterService.onFirstComponentButtonClick();
    this.modalService.dismissAll();
  }

  @HostListener('window:keydown.esc', ['$event'])
  handleKeyDown(event: KeyboardEvent) {
    this.closeModal();
  }

  closeModal() {
    //User Story 32466: Public Search screen to Applicant Information's screen
    // this.ngForm.reset();
    if (this.isFromScreen == 'search') {
      // if(this.basicDetailsFormGroup.status == "INVALID"){
      //   this.isClosevalidationCheck = true;
      // } else {
      //   this.isClosevalidationCheck = false;
      //   this.isOpenConfirmPopUp.next(false);
      //   this.confirmPopup(this.closeSearchApplicantInfoModal, '20vh');
      // }
      if (!this.basicDetailsFormGroup.dirty) {
        this.closeClicked.emit('close');
      } else if (this.basicDetailsFormGroup.dirty) {
        this.congirmationModal.open();
      }

      this.congirmationModal.onOkClick.subscribe((res) => {
        if (res === 'ok') {
          this.ngForm.reset();
          this.closeClicked.emit('close');
        }
      });

      //this.isCloseConfirmPopUp.next(true);
      // this.confirmPopup(this.closeSearchApplicantInfoModal, '20vh');
    } else {
      this.basicDetailsFormGroup.get('applicantType')?.markAsPristine();
      if (this.closedAllPopup) {
        this.closeClicked.emit('close');
      } else if (
        this.basicDetailsFormGroup.dirty &&
        this.closedAllPopup == false
      ) {
        //this.confirmPopup(this.congirmationModal, '20vh');
        this.congirmationModal.open();
        this.congirmationModal.onOkClick.subscribe((res) => {
          if (res === 'ok') {
            this.closedAllPopup = true;
            this.ngForm.reset();
            this.closeClicked.emit('close');
          }
        });
      } else {
        this.closeClicked.emit('close');
      }
    }
  }

  confirmPopup(content: any, modelSize = '40vw') {
    this.closedAllPopup = true;
    this.modalReference = this.modalService.open(content, {
      ariaLabelledBy: 'modal-basic-title',
      size: modelSize,
      backdrop: 'static',
    });
    // this.modalReference.result.then(
    //   (result) => {
    //     this.closeResult = `Closed with: ${result}`;
    //   },
    //   (reason) => {
    //     this.closedAllPopup = false;
    //     this.closeResult = `Dismissed`;
    //   }
    // );
  }

  addNewApplicant() {
    //Reset form to clear the whole form

    this.basicDetailsFormGroup.reset();
    this.basicDetailsFormGroup.updateValueAndValidity();
    //To hide the form from dom and then re-appear
    this.hideForm = true;
    setTimeout(() => {
      this.hideForm = false;

      this.initiateBasicDetailsForm();
    }, 1);
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
  }

  closeSearchModal(e: any) {
    this.closedAllPopup = true;
    this.isOpenPopUp.next(false);
    this.searchModal.close();
    if (e === 'reset') {
      this.closeClicked.emit(e);
    }
  }

  getContactSummary() {
    this.utils.emitLoadingEmitter(true);
    const category = 'C';
    const associatedInd = '0';

    this.applicantService
      .getAllExistingApplicants(associatedInd, category)
      .subscribe(
        (response) => {
          this.utils.emitLoadingEmitter(false);
          this.contactAgentExistingList = response.applicants;
          if (this.contactAgentExistingList === null) {
            this.isOpenPopUp.next(true);
            this.searchModal.open('responsive-modal1');
          } else {
            this.isOpenPopupApplicants.next(true);
            this.selectionModal.open('responsive-modal');
          }
          
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      );
  }

  getCloneApplicantSummary() {
    const category = 'P';
    const associatedInd = '1';
    //!!TODO Remove project id from localstorage

    this.applicantService
      .getAllExistingApplicants(associatedInd, category)
      .subscribe(
        (response) => {
          this.cloneApplicantsExistingList = response.applicants;
        },
        (error: any) => {
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      );
  }

  getApplicantSummary() {
    const category = 'P';
    const associatedInd = '0';
    //!!TODO Remove project id from localstorage

    this.utils.emitLoadingEmitter(true);
    this.applicantService
      .getAllExistingApplicants(associatedInd, category)
      .subscribe(
        (response) => {

          this.applicantsExistingList = response.applicants;
          this.utils.emitLoadingEmitter(false);
          if (this.applicantsExistingList === null) {
            this.isOpenPopUp.next(true);
            this.searchModal.open('responsive-modal1');
          } else {
            this.isOpenPopupApplicants.next(true);
            this.selectionModal.open('responsive-modal');
          }
          
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      );
  }

  async getPropertySummary() {
    const category = 'O';
    const associatedInd = '0';
    this.applicantService
      .getAllExistingApplicants(associatedInd, category)
      .subscribe(
        (response) => {
          this.propertyOwnerExistingList = response.applicants;
          sessionStorage.removeItem('applicantsList');
          if (this.propertyOwnerExistingList === null) {
            this.isOpenPopUp.next(true);
            this.searchModal.open('responsive-modal1');
          } else {
            this.isOpenPopupApplicants.next(true);
            this.selectionModal.open('responsive-modal');
          }
        },
        (error: any) => {
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      );
  }
  yesCClicked() {
    this.closedAllPopup = true;
    this.isOpenContactPop.next(true);
    this.confirmContact.close();
    this.closeClicked.emit('reset');
  }
  async yesClicked() {
    // this.searchModal.open();

    this.closedAllPopup = true;
    this.isOpenConfirmPopUp.next(false);
    this.confirmApplicant.close();
    this.closeClicked.emit('reset');

    // this.closeModal();
    // this.eventEmitterService.onFirstComponentButtonClick();
    //  return;
    //TODO: check the if conditions
    // if (this.currentTab && this.currentTab === 2) {
    //   console.log('in current tab 2');

    //   this.category = 'C';
    //   await this.getContactSummary();
    // } else if (this.currentTab && this.currentTab === 1) {
    //   console.log('in current tab 1');

    //   this.category = 'O';
    //   await this.getPropertySummary();
    // } else {
    //   console.log('in else');

    //   this.category = 'P';
    //   //sessionStorage.removeItem('applicantsList');
    //   await this.getApplicantSummary();
    // }
    // this.closeClicked.emit('reset');
    // this.closeModal();
    // this.eventEmitterService.onFirstComponentButtonClick();
  }

  async noClicked(e?: any) {
    this.closedAllPopup = true;
    this.isOpenConfirmPopUp.next(false);
    this.confirmApplicant.close();
    this.closeClicked.emit('close');
    this.closeModal();
    this.eventEmitterService.onFirstComponentButtonClick(
      this.currentTab == 1 ? true : false
    );
  }

  async noCClicked(e?: any) {
    this.closedAllPopup = true;
    this.isOpenContactPop.next(false);
    this.confirmContact.close();
    this.closeClicked.emit('close');
    this.closeModal();
    this.eventEmitterService.onFirstComponentButtonClick();
  }

  open(content: any) {
    this.confirmApplicant.open('sm');
    this.isOpenConfirmPopUp.next(true);
    // this.modalService
    //   .open(content, { ariaLabelledBy: 'modal-basic-title', size: 'sm' })
    //   .result.then(
    //     (result) => {
    //       //this.closeResult = `Closed with: ${result}`;
    //       console.log(result);
    //     },
    //     (reason) => {
    //       console.log(reason);
    //       if (reason == 'add_applicant') {
    //         console.log('hello');
    //         // this.addNewApplicant();
    //         this.openApplicantSearchModal();
    //       }
    //       if (reason == 'no') {
    //         this.closeModal();
    //       }
    //       // this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    //     }
    //   );
  }
  ngOnDestroy() {
    this.subscriptions.forEach((subscription: Subscription) => {
      if (subscription) subscription.unsubscribe();
    });
    localStorage.removeItem('contactForm');
    localStorage.removeItem('addresType');
    sessionStorage.removeItem('us_streetAddress1');
    sessionStorage.removeItem('nonus_streetAddress1');
    sessionStorage.removeItem('us_streetAddress2');
    sessionStorage.removeItem('nonus_streetAddress2');
    sessionStorage.removeItem('us_zipCode');
    sessionStorage.removeItem('nonus_zipCode');
    sessionStorage.removeItem('us-attention');
    sessionStorage.removeItem('nonus-attention');
    sessionStorage.removeItem('nonus_StateProvince');
    sessionStorage.removeItem('us_state');
    sessionStorage.removeItem('nonus_City');
    sessionStorage.removeItem('us_PostOffice');
    sessionStorage.removeItem('isFromScreen');
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }
  isOwnerChanged() {
    if (this.isOwner) {
      this.confirmPopup(this.ownerWarning, '20vh');
    }
  }

  warningOkClicked() {
    this.closedAllPopup = false;
    this.isOwner = false;
  }

  onWarningCancelClicked() {
    this.isOwner = true;
    this.formControls.owner.setValue(true);
  }

  onStateSelected($event: any) {
    this.stateOfIncorporationString = $event.target.value;
    this.clearVerifyBizNameWarningMessages();
    let selectedState = $event.target.value;
    if (selectedState == 'OT') {
      this.isOtherSelected = true;
    } else {
      this.isOtherSelected = false;
    }
    this.updateVerifiedAndUnverifiedValidations(
      this.areYouIncorporatedBoolean,
      this.stateOfIncorporationString
    );
  }

  onAreYouIncorporatedSelected($event: any) {
    let areYouIncorporatedString = $event.target.value;
    this.clearVerifyBizNameWarningMessages();
    if (areYouIncorporatedString == 'Y') {
      this.areYouIncorporatedBoolean = true;
      this.formControls.stateOfIncorporation.setValue('NY');
      this.updateVerifiedAndUnverifiedValidations(
        this.areYouIncorporatedBoolean,
        this.stateOfIncorporationString
      );
    } else if (
      areYouIncorporatedString == 'N' ||
      areYouIncorporatedString == ''
    ) {
      this.stateOfIncorporationString = 'NY';
      this.formControls.stateOfIncorporation.setValue('');
      this.formControls.territoryOrCountry.setValue('');
      this.formControls.taxpayerId.setValue('');
      this.areYouIncorporatedBoolean = false;
      this.clearVerifiedAndUnverifiedValues();
      this.isBusinessVerified = 'N';
      this.showVerifyListBox = false;
      this.updateVerifiedAndUnverifiedValidations(
        this.areYouIncorporatedBoolean,
        this.stateOfIncorporationString
      );
    }
  }

  publicId() {
    const applicantId = this.applicantDetails?.edbApplicantId;
    window.open(
      environment?.lrpUrl + applicantId
    );
  }

  verifyBusinessName() {
    this.originalBusinessName = this.formControls.businessName.value.trim();
    this.clearVerifiedAndUnverifiedValues();
    this.clearVerifyBizNameWarningMessages();
    if (this.originalBusinessName.length < 3) {
      this.showBizNameLessThanWarning = true;
    } else {
      this.utils.emitLoadingEmitter(true);
      this.applicantInfoService
        .verifyBusinessName(this.originalBusinessName)
        .subscribe(
          (response) => {
            let responseCode = response.status;
            if (responseCode == 200) {
              this.formControls.verified.setValue(true);
              this.isBusinessVerified = 'Y';
              this.updateVerifiedAndUnverifiedValidations(
                this.areYouIncorporatedBoolean,
                this.stateOfIncorporationString
              );
            } else if (responseCode == 204) {
              this.formControls.unverified.setValue(true);
              this.isBusinessVerified = 'N';
              this.showUpdateBusinessNameError = true;
              this.updateVerifiedAndUnverifiedValidations(
                this.areYouIncorporatedBoolean,
                this.stateOfIncorporationString
              );
            } else if (responseCode == 202) {
              this.showVerifyListBox = true;
              this.nextClicked = false;
              this.verifyBusinessList = response.body;
              this.updateVerifiedAndUnverifiedValidations(
                this.areYouIncorporatedBoolean,
                this.stateOfIncorporationString
              );
            } else if (responseCode == 206) {
              this.showTooManyResultsError = true;
            }
            this.utils.emitLoadingEmitter(false);
          },
          (error: any) => {
            this.showDOSDownError = true;
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          }
        );
    }
  }

  onBusinessNameChange(ev: any) {
    this.clearVerifiedAndUnverifiedValues();
    this.showVerifyListBox = false;
    this.clearVerifyBizNameWarningMessages();
    this.updateVerifiedAndUnverifiedValidations(
      this.areYouIncorporatedBoolean,
      this.stateOfIncorporationString
    );
  }

  onBusinessNameVerified(ev: any) {
    this.verifyBusinessSelected = ev.target.value;
    if (this.verifyBusinessSelected == 'NL') {
      this.formControls.businessName.setValue(this.originalBusinessName);
      this.formControls.unverified.setValue(true);
      this.formControls.verified.setValue(false);
    } else {
      this.formControls.businessName.setValue(this.verifyBusinessSelected);
      this.formControls.verified.setValue(true);
      this.formControls.unverified.setValue(false);
      this.verifiedBusinessName = this.verifyBusinessSelected;
    }
    this.updateVerifiedAndUnverifiedValidations(
      this.areYouIncorporatedBoolean,
      this.stateOfIncorporationString
    );
  }

  updateVerifiedAndUnverifiedValidations(
    isIncorporated: boolean,
    stateOfIncorporation: string
  ) {
    let verifiedControl = this.formControls['verified'];
    let unverifiedControl = this.formControls['unverified'];
    if (
      !verifiedControl.value &&
      !unverifiedControl.value &&
      isIncorporated &&
      stateOfIncorporation == 'NY' &&
      (this.selectedApplicantType === 'X' ||
        this.selectedApplicantType === 'T' ||
        this.selectedApplicantType === 'C')
    ) {
      verifiedControl.setErrors({ checkboxRequired: true });
      unverifiedControl.setErrors({ checkboxRequired: true });
    } else {
      verifiedControl.setErrors(null);
      unverifiedControl.setErrors(null);
      return;
    }
    return;
  }

  clearVerifyBizNameWarningMessages() {
    this.showBizLookUpRequiredError = false;
    this.showTooManyResultsError = false;
    this.showUpdateBusinessNameError = false;
    this.showBizNameLessThanWarning = false;
    this.showDOSDownError = false;
  }

  clearVerifiedAndUnverifiedValues() {
    this.formControls.verified.setValue(false);
    this.formControls.unverified.setValue(false);
  }

  showVerifiedHistory() {
    if (this.applicantDetailsHistory?.organization?.businessVerified === 'Y')
      return 'Verified';
    if (this.applicantDetailsHistory?.organization?.businessVerified === 'N')
      return 'Unverified';
  }

  isDetailsEmpty() {
    if (
      this.applicantDetails &&
      Object.keys(this.applicantDetails).length === 0
    )
      return true;
    return false;
  }
  emptyPublicId(temVal?: any) {
    if (this.mode !== 'validate') {
      return;
    }
    if (this.applicantsValidated) {
      return;
    }
    this.publicIdIfEmpty.setValue(temVal ? temVal : '');
    this.publicIdModalReference = this.modalService.open(
      this.emptyPublicIdModal,
      {
        ariaLabelledBy: 'modal-basic-title',
        size: '35vw',
        backdrop: 'static',
      }
    );

    this.publicIdModalReference.result.then(
      (result) => {
        // this.applicantInfoService.validatePublicId(result.value).subscribe((res) => {
        //           this.publicIdIfEmpty.setValue(result.value);
        // this.enteredPublicId = true;
        // }, (error) => {
        //   this.enteredPublicId = false;
        //   this.publicIdIfEmpty.markAsPending();
        //   this.publicIdIfEmpty.markAsDirty();
        //   this.publicIdIfEmpty.markAsTouched();
        // })
        // this.publicIdIfEmpty.setValue(result.value);
        // this.enteredPublicId = true;
      },
      (reason) => {
        this.publicIdIfEmpty.markAsPristine();
        this.publicIdIfEmpty.markAsUntouched();
        if (this.publicIdIfEmpty.value) {
          this.confirmPopup(this.confirmationWarningPopup, '41vh');
        }
      }
    );
  }

  validatePublicID(id: any) {
    this.applicantInfoService
      .validatePublicId(id, `${this.applicantId}`)
      .subscribe(
        (res) => {
          this.applicantDetails.edbApplicantId = id;
          this.publicIdIfEmpty.setValue(id);
          this.isValidPublicId = true;
          this.enteredPublicId = true;
          this.duplicatePublicId = false;
          this.publicIdModalReference.close();
        },
        (error) => {
          if (error.status === 409) {
            this.duplicatePublicId = true;
            this.isValidPublicId = true;
            this.enteredPublicId = true;
          } else {
            this.isValidPublicId = false;
            this.enteredPublicId = true;
            this.duplicatePublicId = false;
          }
          this.publicIdIfEmpty.markAsDirty();
        }
      );
  }

  confirmOkCliked() {
    this.closedAllPopup = false;
  }

  confirmCancelCliked() {
    this.emptyPublicId(this.publicIdIfEmpty.value);
  }
}

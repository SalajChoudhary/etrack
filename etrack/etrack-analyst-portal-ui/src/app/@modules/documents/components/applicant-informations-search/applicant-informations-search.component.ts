import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
import { takeUntil } from 'rxjs/operators';

import {
  AbstractControl,
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { NgbModal, NgbPopover } from '@ng-bootstrap/ng-bootstrap';
import { BehaviorSubject, fromEvent, Subject, Subscription } from 'rxjs';
import { atleastTwoCharReqd } from 'src/app/@shared/applicationInformation.validator';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { ApplicantInfoServiceService } from 'src/app/@shared/services/applicant-info-service.service';
import { CommonService } from 'src/app/@shared/services/commonService';
import { DocumentService } from 'src/app/@shared/services/documentService';
import { ModalConfig } from 'src/app/modal.config';
import { EventEmitterService } from 'src/app/@shared/services/event-emitter.service';
import { isEmpty } from 'lodash';
import { Utils } from 'src/app/@shared/services/utils';

@Component({
  selector: 'app-applicant-informations-search',
  templateUrl: './applicant-informations-search.component.html',
  styleUrls: ['./applicant-informations-search.component.scss'],
  encapsulation: ViewEncapsulation.Emulated,
})
export class ApplicantInformationsSearchComponent implements OnInit {
  @ViewChild('applicantInfoModal')
  private applicantInfoModal!: CustomModalPopupComponent;
  applicantType!: any;
  firstFormGroup!: UntypedFormGroup;
  secondFormGroup!: UntypedFormGroup;
  isEditable = false;
  searched = false;
  isFromSearch: string = 'search';
  subscriptions: Subscription[] = [];
  @Output() closeClicked = new EventEmitter();
  @Input() currentTab: any;
  isOpenPopupSearch = new BehaviorSubject<boolean>(false);
  errorMsgObj: any = {};
  addressDetails = 'main Project';
  modalConfig: ModalConfig = {
    title: 'New York State Department of Environmental Conservation',
    showHeader: true,
    onClose: () => {
      this.isOpenPopupSearch.next(false);
      return true;
    },
    onDismiss: () => {
      this.isOpenPopupSearch.next(false);
      return true;
    },
    shouldClose: () => {
      return true;
    },
    shouldDismiss: () => {
      return true;
    },
  };
  selectedApplicantType: string = '';
  closedAllPopup: boolean = false;
  tableCheckBox: any = '';
  popUpHeaderText: string = 'Affiliated Facilities';
  facilitiesPopUp: any = null;
  innerWidth: number = 0;
  searchedApplicantData: any = null;
  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.innerWidth = window.innerWidth;
  }
  showServerError = false;
  serverErrorMessage!: string;
  // files: any[] = [
  //   {
  //     id: 124234,
  //     title: 'some text',
  //   },
  //   {
  //     id: 124235,
  //     title: 'another text',
  //   },
  //   {
  //     id: '',
  //     title: 'None of the above',
  //   },
  // ];
  selectedRows: any = [];
  modalRef: any;
  headerText: string = '';
  isSubmitted: boolean = false;
  applicantsList: any = [];
  applicantsError: any = [];
  configObject: any;
  searchContent: string = '';
  facilityArray: any[] = [];
  searchParams: string = '';
  private unsubscriber: Subject<void> = new Subject<void>();

  constructor(
    private _formBuilder: UntypedFormBuilder,
    private commonService: CommonService,
    private docService: DocumentService,
    private modalService: NgbModal,
    private appService: ApplicantInfoServiceService,
    private el: ElementRef,
    private utils: Utils,
    private eventEmitterService: EventEmitterService
  ) {}
  selectRow(isChecked: boolean, item: any) {
    console.log(isChecked, item);
    this.selectedRows = [];
    this.selectedRows.push(item.applicantId);

    // if (isChecked) {
    //   this.selectedRows.push(item.applicantId);
    // } else {
    //   let i = this.selectedRows.findIndex((id: any) => id == item.applicantId);
    //   this.selectedRows.splice(i, 1);
    // }
  }
  getConfig() {
    this.commonService.getAllConfigurations().then((response) => {
      if (response) {
        this.configObject = response;
      }
    });
  }

  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val) => {
      if (val) this.errorMsgObj = this.commonService.getErrorMsgsObj();
    });
  }

  ngOnChanges() {}
  ngOnInit(): void {
    this.getConfig();
    this.innerWidth = window.innerWidth;
    this.getAllErrorMsgs();
    if (this.currentTab == 0) {
      this.headerText = 'Applicant';
    } else if (this.currentTab == 1) {
      this.headerText = 'Property Owner';
    } else if (this.currentTab == 2) {
      this.headerText = 'Contact/Agent';
    }
    this.subscriptions.push(
      this.commonService.closeApplicantModal.subscribe((val: boolean) => {
        if (val) {
          this.modalService.dismissAll();
        }
      })
    );
    this.firstFormGroup = this._formBuilder.group({
      firstCtrl: [''],
      applicantType: [''],
      ftype: ['S'],
      ltype: ['E'],
      firstName: [''],
      lastName: [''],
      businessName: [''],
      btype: ['C'],
    });
    this.secondFormGroup = this._formBuilder.group({
      secondCtrl: [''],
    });
    this.applicantsError = [];

    history.pushState(null, '');

    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }
  clearFirstSearch() {
    this.firstFormGroup.controls.firstName.setValue('');
    this.firstFormGroup.controls.lastName.setValue('');
    this.selectedRows = [];
    this.searched = false;
    this.isSubmitted = false;
    this.applicantsError = [];
  }
  clearSecondSearch() {
    this.firstFormGroup.controls.businessName.setValue('');
    this.searched = false;
    this.isSubmitted = false;
    this.applicantsError = [];
    this.selectedRows = [];
  }
  onLtypeChange(e: any) {
    if (
      this.selectedApplicantType == 'P' ||
      this.selectedApplicantType == 'I'
    ) {
      if (this.firstFormGroup?.get('ftype')?.value !== 'E') {
        this.updateFormControlValidation(
          this.firstFormGroup.controls.firstName,
          []
        );
      } else {
        this.updateFormControlValidation(
          this.firstFormGroup.controls.firstName,
          []
        );
      }
      console.log(this.firstFormGroup?.get('ltype')?.value, 'EAP ltype');
      if (this.firstFormGroup?.get('ltype')?.value !== 'E') {
        this.updateFormControlValidation(
          this.firstFormGroup.controls.lastName,
          [atleastTwoCharReqd, Validators.required]
        );
      } else {
        this.updateFormControlValidation(
          this.firstFormGroup.controls.lastName,
          [Validators.required]
        );
      }
    } else if (
      this.selectedApplicantType == 'X' ||
      this.selectedApplicantType == 'T' ||
      this.selectedApplicantType == 'C' ||
      this.selectedApplicantType == 'F' ||
      this.selectedApplicantType == 'S' ||
      this.selectedApplicantType == 'M'
    ) {
      this.updateFormControlValidation(
        this.firstFormGroup.controls.businessName,
        [atleastTwoCharReqd, Validators.required]
      );
      this.updateFormControlValidation(
        this.firstFormGroup.controls.businessName,
        [atleastTwoCharReqd, Validators.required]
      );
    }
  }
  onTypeChange(e: any) {
    let type = e.target.value;
    console.log(type, 'EAP type');
    this.selectedApplicantType = type;
    this.isSubmitted = false;
    this.searched = false;
    this.applicantsError = [];
    this.firstFormGroup.controls.btype.setValue('C');
    this.firstFormGroup.controls.firstName.setValue('');
    this.firstFormGroup.controls.lastName.setValue('');
    this.firstFormGroup.controls.businessName.setValue('');
    this.firstFormGroup.controls.firstName.clearValidators();
    this.firstFormGroup.controls.lastName.clearValidators();
    this.firstFormGroup.controls.businessName.clearValidators();
    this.firstFormGroup.controls.firstName.updateValueAndValidity();
    this.firstFormGroup.controls.lastName.updateValueAndValidity();
    this.firstFormGroup.controls.businessName.updateValueAndValidity();
    if (type == 'P' || type == 'I') {
      if (this.firstFormGroup?.get('ftype')?.value !== 'E') {
        this.updateFormControlValidation(
          this.firstFormGroup.controls.firstName,
          []
        );
      } else {
        this.updateFormControlValidation(
          this.firstFormGroup.controls.firstName,
          []
        );
      }
      console.log(this.firstFormGroup?.get('ltype')?.value, 'EAP ltype');
      if (this.firstFormGroup?.get('ltype')?.value !== 'E') {
        this.updateFormControlValidation(
          this.firstFormGroup.controls.lastName,
          [atleastTwoCharReqd, Validators.required]
        );
      } else {
        this.updateFormControlValidation(
          this.firstFormGroup.controls.lastName,
          [Validators.required]
        );
      }
    } else if (
      type == 'X' ||
      type == 'T' ||
      type == 'C' ||
      type == 'F' ||
      type == 'S' ||
      type == 'M'
    ) {
      this.updateFormControlValidation(
        this.firstFormGroup.controls.businessName,
        [atleastTwoCharReqd, Validators.required]
      );
      this.updateFormControlValidation(
        this.firstFormGroup.controls.businessName,
        [atleastTwoCharReqd, Validators.required]
      );
    }
  }

  private updateFormControlValidation(
    control: AbstractControl,
    validators: ValidatorFn | ValidatorFn[]
  ): void {
    control.setValidators(validators);
    control.updateValueAndValidity();
  }

  onValueChange(e: any) {
    this.applicantsError = [];
    this.isSubmitted = false;
    this.searched = false;
  }

  closePopUp() {
    this.closeClicked.emit();
    this.eventEmitterService.onFirstComponentButtonClick();
    this.searchedApplicantData = null;
  }

  onFormSubmit() {
    this.isSubmitted = true;
    if (this.firstFormGroup.valid) {
      let formData = this.firstFormGroup.value;
      const firstName =
        formData.firstName?.trim() || formData.businessName?.trim();
      const ftype = formData.businessName?.trim()
        ? formData.btype?.trim()
        : formData.ftype?.trim();
      this.searchContent = firstName + ' ' + formData.lastName.trim();
      this.searchParams = firstName + '$ ' + formData.lastName.trim();
      let dataToSave: any = {
        firstName: firstName,
        lastName: formData.lastName ? formData.lastName.trim() : '',
        applicantType: this.selectedApplicantType,
      };
      console.log(dataToSave);

      this.searchedApplicantData = { ...dataToSave };
      this.utils.emitLoadingEmitter(true);
      this.appService
        .searchByName(
          formData.applicantType,
          firstName,
          formData.lastName.trim(),
          ftype,
          formData.ltype
        )
        .subscribe(
          (response) => {
            this.utils.emitLoadingEmitter(false);
            this.applicantsError = [];
            this.applicantsList = [];
            this.searched = true;
            if (
              response &&
              response.applicants &&
              response.applicants.length > 0
            ) {
              console.log('we here');

              this.searchedApplicantData = null;
              response.applicants.push({
                applicantId: '0',
                displayName: 'None of the Above',
              });
              this.applicantsList = response.applicants;
              this.applicantsList.forEach((element: any) => {
                element.facilitieNames = element.facilities
                  .map((e: { facilityName: any }) => e.facilityName)
                  .join(',')
                  .toString();
              });
              //this.applicantsList=response.applicants.splice(0,10);
              setTimeout(() => {
                if (document.getElementById('radio0')) {
                  (
                    document.getElementById('radio0') as HTMLInputElement
                  ).checked = true;
                  this.selectedRows.push(
                    (document.getElementById('radio0') as HTMLInputElement)
                      .value
                  );
                }
              }, 100);
            } else {
              this.applicantsList = [];
            }
          },
          (err) => {
            this.utils.emitLoadingEmitter(false);
            this.applicantsList = [];
            this.applicantsError = err;

            console.log(this.applicantsError.error);
          }
        );
      //TODO API call
    }
    console.log(this.firstFormGroup.value);
  }
  onHover(e: any, item: any, pop?: NgbPopover) {
    this.facilitiesPopUp = pop;
    if (item && item?.facilities?.length > 0) {
      this.facilityArray = [...item?.facilities];
    } else {
      this.facilityArray = [...[]];
    }
    console.log(this.facilitiesPopUp);
    this.facilitiesPopUp.open();
    if(this.facilitiesPopUp._windowRef) {
      this.facilitiesPopUp._windowRef.location.nativeElement.addEventListener('mouseleave', 
      () => { this.facilitiesPopUp.close() });
      window.addEventListener('keydown', this.closePopoverOnTab);
    }
  }

  onPopoverHidden() {
    window.removeEventListener('keydown', this.closePopoverOnTab);
  }

  private closePopoverOnTab = (event: KeyboardEvent) => {
    if(event.key === 'Tab') {
      this.facilitiesPopUp.close();
    }
  }

  onSubmit() {
    console.log('submit is called selected rows', this.selectedRows);

    this.applicantsList = [];
    this.applicantsError = null;
    this.firstFormGroup = this._formBuilder.group({
      firstCtrl: [''],
      applicantType: [''],
      ftype: ['S'],
      ltype: ['E'],
      firstName: [''],
      lastName: [''],
      businessName: [''],
      btype: ['C'],
    });
    this.searched = false;
    console.log('J here', this.selectedRows);

    this.commonService.setApplicants(this.selectedRows);
    this.commonService.setSelectedApplicantype(this.selectedApplicantType);
    this.commonService.setFromScreen(this.isFromSearch);
    sessionStorage.setItem('isFromScreen', 'search'); // do not remove this line. This is identifier to set property relationship code
    this.isOpenPopupSearch.next(true);
    this.applicantInfoModal.open('xxl');
  }
  ngOnDestroy() {
    this.subscriptions.forEach((subscription: Subscription) => {
      if (subscription) subscription.unsubscribe();
    });
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }

  closeInfoModal(e: any) {
    console.log('search screen');
    this.closeClicked.emit(e);
    this.firstFormGroup.get('applicantType')?.setValue('');
    this.isOpenPopupSearch.next(false);
    this.applicantInfoModal.close();
  }

  onSearch() {}
}

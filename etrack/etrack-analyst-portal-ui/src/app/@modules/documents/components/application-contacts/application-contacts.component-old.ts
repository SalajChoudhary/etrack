import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, FormControl, UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { get, isEqual, groupBy, isEmpty } from 'lodash';
import contactResponse from 'src/assets/data/permits.json';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { ModalConfig } from 'src/app/modal.config';
import { formatDate } from '@angular/common';

@Component({
  selector: 'app-application-contacts',
  templateUrl: './application-contacts.component.html',
  styleUrls: ['./application-contacts.component.scss'],
})
export class ApplicationContactsComponent implements OnInit {
  @ViewChild('warningModal', { static: true }) warningModal!: any;
  @ViewChild('modExtRequestModal')
  modExtRequestModal!: CustomModalPopupComponent;
  @ViewChild('pendingChangesPopup', { static: true })
  pendingChangesPopup!: PendingChangesPopupComponent;
  modalReference: any;
  applicantContacts: any = [];
  @Input() selectedPermitTypes: any = [];
  @Input() selectedIndex: any = 0;
  @Input() avoidFormInitiate: boolean = false;
  @Output() stepCompleted = new EventEmitter();
  @Output() onCloseClick = new EventEmitter();
  @ViewChild('pendingPopup', { static: true })
  pendingPopup!: PendingChangesPopupComponent;
  selectedJAFPermits: any[] = [];
  selectedNonJAFPermits: any[] = [];
  fields: any = [];
  applicationContactsForm = new UntypedFormGroup({});
  allEtrackPermits: any[] = [];
  formDetails: any;
  allDartPermits: any = [];
  dartFields: any[] = [];
  allContacts: any = [];
  leftOutContacts: any[] = [];
  @Input() isForwardClick: boolean = false;
  isArrowClicked: boolean = false;
  applicationContactsValidated: boolean = false;
  mode: any = localStorage.getItem('mode');
  EAValidated: boolean = false;
  errorMsgObj: any = {};
  extPermits: any[] = [];
  modPermits: any[] = [];
  transPermits: any[] = [];
  extFields: any[] = [];
  modFields: any[] = [];
  transFields: any[] = [];
  groupedBatchExtPermits: any = {};
  groupedBatchModPermits: any = {};
  groupedBatchTransPermits: any = {};
  supplementalForms: any = [];
  projectId!: any;
  reasonForModExt: string = '';
  estCompletionDate!: any;
  isModRequest: boolean = false;
  isExtRequest: boolean = false;
  showEstComplDateRequiredError: boolean = false;
  showEstComplDateInvalidError: boolean = false;
  showEstComplDatePermitTermLimitError: boolean = false;
  showModDescRequiredError: boolean = false;
  showExtReasonRequiredError: boolean = false;
  modExtPopupIsSubmitted: boolean = false;
  modList: any[] = [];
  extList: any[] = [];
  permitDescription: string = '';
  currentPermit: any = {};

  modalConfig: ModalConfig = {
    title: '',
    showHeader: false,
    onClose: () => {
      return true;
    },
    onDismiss: () => {
      return true;
    },
    shouldClose: () => {
      return true;
    },
    shouldDismiss: () => {
      return true;
    },
  };
  @Output() apiCalled = new EventEmitter();

  constructor(
    private modalService: NgbModal,
    private projectService: ProjectService,
    private fb: UntypedFormBuilder,
    private cdr: ChangeDetectorRef,
    private router: Router,
    public commonService: CommonService
  ) {
    this.initiateForm();
  }

  isCompletionDateValid(date: string) {
    if (!date) return false;
    let now = formatDate(new Date(), 'MM-dd-yyyy', 'en_US');
    date = formatDate(date, 'MM-dd-yyyy', 'en_US');
    if (Date.parse(date) <= Date.parse(now)) {
      return true;
    } else return false;
  }

  openModExtModal(indicator: string, field: any) {
    let batchId = Object.keys(field.controls)[0];
    this.projectService.getExtModRequestData(batchId).then((data) => {
      if (data) {
        console.log('JAY HERE', data);
        let date = data[0].estCompletionDate;
        if (date) {
          date = formatDate(date, 'yyyy-MM-dd', 'en-US');
          this.estCompletionDate = date;
        }

        if (data[0].modExtReason) {
          this.reasonForModExt = data[0].modExtReason;
        }
      }
    });
    if (indicator === 'MOD') {
      this.isModRequest = true;
      this.modList = this.groupedBatchModPermits[batchId];
      this.modList.sort((a: any, b: any) => {
        if (a.programIdFormatted > b.programIdFormatted) return 1;
        if (b.programIdFormatted > a.programIdFormatted) return -1;
        return 0;
      });
      this.currentPermit = this.modList[0];
      this.permitDescription = this.modList[0].projectDesc;
    }
    if (indicator === 'EXT') {
      this.isExtRequest = true;
      this.extList = this.groupedBatchExtPermits[batchId];
      this.extList.sort((a: any, b: any) => {
        if (a.programIdFormatted > b.programIdFormatted) return 1;
        if (b.programIdFormatted > a.programIdFormatted) return -1;
        return 0;
      });
      this.currentPermit = this.extList[0];
      this.permitDescription = this.extList[0].projectDesc;
    }
    this.projectId = localStorage.getItem('projectId');
    this.modExtRequestModal.open('permit-validate');
  }

  modExtModalSubmit() {
    this.clearErrorMessages();
    this.modExtPopupIsSubmitted = true;
    if (this.isCompletionDateValid(this.estCompletionDate)) {
      this.showEstComplDateInvalidError = true;
      return;
    }
    if (isEmpty(this.reasonForModExt)) {
      if (this.isModRequest) {
        this.showModDescRequiredError = true;
      }
      if (this.isExtRequest) {
        this.showExtReasonRequiredError = true;
      }
      return;
    }
    if (!this.estCompletionDate) {
      this.showEstComplDateRequiredError = true;
      return;
    }

    //TO-DO validations for permit max term
    // showEstComplDatePermitTermLimitError
    // if(this.estCompletionDate > permitTermLimit){
    //   this.showEstComplDatePermitTermLimitError = true;
    //   return;
    // }

    let payload = {};

    if (this.isExtRequest) {
      payload = this.extList.map((permit: any) => {
        return {
          applicationId: get(permit, 'applicationId', null),
          edbApplnId: get(permit, 'edbApplicationId', null),
          permitTypeCode: get(permit, 'permitTypeCode', null),
          batchId: get(permit, 'batchId', null),
          transType: get(permit, 'transType', null),
          programId: get(permit, 'programId', ''),
          modExtReason: this.reasonForModExt,
          estCompletionDate: formatDate(
            this.estCompletionDate,
            'MM/dd/yyyy',
            'en_US'
          ),
        };
      });
    }
    if (this.isModRequest) {
      payload = this.modList.map((permit: any) => {
        return {
          applicationId: get(permit, 'applicationId', null),
          edbApplnId: get(permit, 'edbApplicationId', null),
          permitTypeCode: get(permit, 'permitTypeCode', null),
          batchId: get(permit, 'batchId', null),
          transType: get(permit, 'transType', null),
          programId: get(permit, 'programId', ''),
          modExtReason: this.reasonForModExt,
          estCompletionDate: formatDate(
            this.estCompletionDate,
            'MM/dd/yyyy',
            'en_US'
          ),
        };
      });
    }

    this.projectService
      .updateExistingPermitAmendRequest(payload)
      .then((response) => {
        this.currentPermit.formSubmittedInd = 'Y';
        this.modExtRequestModal.close();
        this.permitDescription = '';
        this.estCompletionDate = '';
        this.reasonForModExt = '';
        this.isExtRequest = false;
        this.isModRequest = false;
      })
      .catch((error) => {
        console.log(error);
      });
  }

  isComplete(permit: any, ind: string) {
    let isComplete: boolean = false;
    let batchId = Object.keys(permit.controls)[0];
    if (ind === 'MOD') {
      let list = this.groupedBatchModPermits[batchId];
      if (list[0].formSubmittedInd === 'Y') {
        isComplete = true;
      }
    } else if (ind === 'EXT') {
      let list = this.groupedBatchExtPermits[batchId];
      if (list[0].formSubmittedInd === 'Y') {
        isComplete = true;
      }
    }
    return isComplete;
  }

  clearErrorMessages() {
    this.showEstComplDateRequiredError = false;
    this.showEstComplDateInvalidError = false;
    this.showEstComplDatePermitTermLimitError = false;
    this.showModDescRequiredError = false;
    this.showExtReasonRequiredError = false;
  }

  onCancelClicked() {
    if (isEmpty(this.estCompletionDate) && isEmpty(this.reasonForModExt)) {
      this.pendingChangesCancelClicked();
    } else {
      this.pendingChangesPopup.open();
    }
  }

  onPendingChangesOkclick() {
    this.pendingChangesPopup.close();
  }

  pendingChangesCancelClicked() {
    this.isModRequest = false;
    this.isExtRequest = false;
    this.modExtPopupIsSubmitted = false;
    this.reasonForModExt = '';
    this.estCompletionDate = '';
    this.currentPermit = {};
    this.modExtRequestModal.close();
  }

  async openConfirmModal() {
    this.modalReference = await this.pendingPopup.open();
  }
  navigateToMainPage() {
    // localStorage.setItem('mode', '');
    this.router.navigate(['/apply-for-permit-details']);
  }

  okClicked() {
    this.navigateToMainPage();
  }
  initiateForm() {
    this.applicationContactsForm = this.fb.group({
      nonJaf: this.fb.array([]),
      ext: this.fb.array([]),
      mod: this.fb.array([]),
      trans: this.fb.array([]),
      jaf: '',
    });
  }
  get nonJAFArray() {
    return this.applicationContactsForm.get('nonJaf') as UntypedFormArray;
  }

  get extArray() {
    return this.applicationContactsForm.get('ext') as UntypedFormArray;
  }
  get modArray() {
    return this.applicationContactsForm.get('mod') as UntypedFormArray;
  }
  get transArray() {
    return this.applicationContactsForm.get('trans') as UntypedFormArray;
  }

  get isReadOnly() {
    return this.mode == 'read' || this.applicationContactsValidated;
  }
  get isValidate() {
    return this.mode == 'validate';
  }
  ngOnInit(): void {
    this.getFormDetails();
    this.getAllErrorMsgs();
  }

  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val)=>{
      if(val)this.errorMsgObj=this.commonService.getErrorMsgsObj();
    })
  }

  setFormData() {
    console.log(this.allContacts, 'allContacts    0');
    if (this.allContacts?.length > 1) {
      if (this.EAValidated) {
        this.allEtrackPermits.forEach((permit: any, i: number) => {
          let selectedContact = permit.contacts.filter(
            (contact: any) => contact.permitAssignedInd == 'Y'
          );
          if (selectedContact.length > 0) {
            this.nonJAFArray
              .at(i)
              .get(permit.permitTypeCode)
              ?.setValue(selectedContact[0].roleId);
          }
        });
      } else {
        this.allEtrackPermits.forEach((permit: any, i: number) => {
          let selectedContact = permit.contacts.filter(
            (contact: any) => contact.permitAssignedInd == 'Y'
          );
          if (selectedContact.length > 0) {
            this.nonJAFArray
              .at(i)
              .get(permit.permitTypeCode)
              ?.setValue(selectedContact[0].roleId);
          }
        });
        // if (this.selectedJAFPermits.length > 0) {
        //   let selectedContact = this.selectedJAFPermits[0].contacts.filter(
        //     (permit: any) => permit.permitAssignedInd == 'Y'
        //   );
        //   if (selectedContact.length > 0)
        //     this.applicationContactsForm
        //       .get('jaf')
        //       ?.setValue(selectedContact[0].roleId);
        //   this.cdr.detectChanges();
        //   this.applicationContactsForm.get('jaf')?.updateValueAndValidity();
        // }
      }
    } else if (this.allContacts?.length === 1) {
      if (this.EAValidated) {
        this.allEtrackPermits.forEach((permit: any, i: number) => {
          this.nonJAFArray
            .at(i)
            .get(permit.permitTypeCode)
            ?.setValue(this.allContacts[0].roleId);
        });
      } else {
        this.allEtrackPermits.forEach((permit: any, i: number) => {
          this.nonJAFArray
            .at(i)
            .get(permit.permitTypeCode)
            ?.setValue(this.allContacts[0].roleId);
        });
        // if (this.selectedJAFPermits?.length > 0) {
        //   this.applicationContactsForm
        //     .get('jaf')
        //     ?.setValue(this.allContacts[0].roleId);
        //   this.cdr.detectChanges();
        //   this.applicationContactsForm.get('jaf')?.updateValueAndValidity();
        // }
        let i = 0;
        for (let batchId in this.groupedBatchExtPermits) {
          console.log(this.extArray.at(i), 'ext arrayt');
          this.extArray
            .at(i)
            ?.setValue({ [batchId]: this.allContacts[0].roleId });
          i++;
        }

        let j = 0;
        for (let batchId in this.groupedBatchModPermits) {
          this.modArray
            .at(j)
            ?.setValue({ [batchId]: this.allContacts[0].roleId });
          j++;
        }
        let k = 0;
        for (let batchId in this.groupedBatchTransPermits) {
          this.transArray
            .at(k)
            ?.setValue({ [batchId]: this.allContacts[0].roleId });
          k++;
        }
      }
    }
    console.log(this.applicationContactsForm.value, 'formValue1');
  }
  getFormDetails() {
    this.projectService.getAppContactDetails().then((response: any) => {
      console.log('RESPONSE', response);

      this.applicationContactsValidated = isEqual(
        get(contactResponse, 'validateInd', 'N'),
        'Y'
      );
      this.EAValidated = isEqual(
        get(contactResponse, 'emergencyInd', 'N'),
        'Y'
      );
      console.log(this.EAValidated, 'EAVALK');
      if (response) {
        this.allEtrackPermits = [];
        //this.allDartPermits=[];
        //this.formDetails = contactResponse['permit-assign'];
        this.formDetails = response['permit-assign'];
        this.supplementalForms = response['supplemental-forms'];
        console.log(this.formDetails, 'formdet');
        let groupedBycategory = groupBy(
          this.formDetails,
          (val) => val.transType
        );
        if (this.EAValidated) {
          this.allEtrackPermits = this.formDetails;
          if (this.allEtrackPermits.length > 0) {
            this.allContacts = this.allEtrackPermits[0].contacts.sort(
              (a: any, b: any) => {
                if (a.displayName > b.displayName) return -1;
                return 1;
              }
            );
          }
        } else {
          this.allEtrackPermits = groupedBycategory.NEW
            ? groupedBycategory.NEW
            : [];
          this.extPermits = groupedBycategory.EXT ? groupedBycategory.EXT : [];
          this.modPermits = groupedBycategory.MOD ? groupedBycategory.MOD : [];
          this.transPermits = groupedBycategory.TRANS
            ? groupedBycategory.TRANS
            : [];
          this.groupedBatchExtPermits = groupBy(this.extPermits, 'batchId');
          this.groupedBatchModPermits = groupBy(this.modPermits, 'batchId');
          this.groupedBatchTransPermits = groupBy(this.transPermits, 'batchId');
          this.modList = Object.values(this.groupedBatchModPermits);
          this.extList = Object.values(this.groupedBatchExtPermits);
          this.apiCalled.emit({
            modPermits: this.modPermits,
            extPermits: this.extPermits,
          });
          console.log('MOD LIST', this.modList);
          console.log('EXT LIST', this.extList);

          if (this.allEtrackPermits.length > 0) {
            this.allContacts = this.allEtrackPermits[0].contacts?.sort(
              (a: any, b: any) => {
                if (a.displayName > b.displayName) return -1;
                return 1;
              }
            );
          }
          if (this.extPermits.length > 0) {
            this.allContacts = this.extPermits[0].contacts?.sort(
              (a: any, b: any) => {
                if (a.displayName > b.displayName) return -1;
                return 1;
              }
            );
          }
          if (this.modPermits.length > 0) {
            this.allContacts = this.modPermits[0].contacts?.sort(
              (a: any, b: any) => {
                if (a.displayName > b.displayName) return -1;
                return 1;
              }
            );
          }
        }

        console.log(groupedBycategory, 'groupBy');
        // for(let prop in this.formDetails['etrack-permits']){
        //   this.formDetails['etrack-permits'][prop].forEach((permit:any)=>{
        //     this.allEtrackPermits.push(permit)
        //   })
        // }
        // for(let prop in this.formDetails['dart-permits']){
        //   this.formDetails['dart-permits'][prop].forEach((permit:any)=>{
        //     this.allDartPermits.push(permit)
        //   })
        // }

        if (this.allEtrackPermits?.length > 0) this.stepCompleted.next(true);
        this.aggregateJAFPermits();
        this.addFormControl();

        // if(this.allDartPermits.length>0){
        //   this.allContacts=this.allDartPermits[0].contacts.sort((a:any,b:any)=>{
        //     if(b.displayName>a.displayName)return -1;
        //     return 1;
        //   });
        //   return;
        // }
      }
    });
  }
  ngOnChanges(changes: any) {
    if (changes.selectedIndex) {
      if (changes.selectedIndex.currentValue == 2 && !this.avoidFormInitiate) {
        this.initiateForm();
        this.getFormDetails();
      }
    }

    if (changes.isForwardClick) {
      if (changes.isForwardClick.currentValue) {
        this.isArrowClicked = true;
        this.openWarningModal();
      }
    }
  }
  aggregateJAFPermits() {
    if (!this.EAValidated) {
      // this.selectedJAFPermits = this.allEtrackPermits.filter((permit: any) => {
      //   return this.projectService.JAFPermits.includes(permit?.permitTypeCode);
      // });
      // this.allEtrackPermits = this.allEtrackPermits.filter(
      //   (permit: any) => {
      //     return !this.projectService.JAFPermits.includes(
      //       permit?.permitTypeCode
      //     );
      //   }
      // );
    }
  }
  close() {
    // this.router.navigate(['/apply-for-permit-details']);
    if (this.applicationContactsForm.dirty) this.openConfirmModal();
    else this.commonService.navigateToMainPage();
  }
  addFormControl() {
    this.fields = [];
    this.extFields = [];
    this.modFields = [];
    this.transFields = [];
    if (this.EAValidated) {
      this.allEtrackPermits.forEach((permit: any) => {
        this.fields.push({
          id: permit?.permitTypeCode,
          label: permit?.permitTypeDesc,
          value: '',
          contacts: permit?.contacts,
          applicationId: permit?.applicationId,
        });
      });
      this.fields.forEach((permit: any) => {
        const nonJAFFormgroup = this.fb.group({
          [permit['id']]: '',
        });
        this.nonJAFArray.push(nonJAFFormgroup);
      });
    } else {
      this.allEtrackPermits.forEach((permit: any) => {
        this.fields.push({
          id: permit?.permitTypeCode,
          label: permit?.permitTypeDesc,
          value: '',
          contacts: permit?.contacts,
          applicationId: permit?.applicationId,
        });
      });
      for (let batchId in this.groupedBatchTransPermits) {
        console.log(batchId, 'batchId');
        this.transFields.push({
          id: batchId,
          label: '',
          value: '',
        });
      }

      for (let batchId in this.groupedBatchExtPermits) {
        console.log(batchId, 'batchId');
        this.extFields.push({
          id: batchId,
          label: '',
          value: '',
        });
      }

      for (let batchId in this.groupedBatchModPermits) {
        console.log(batchId, 'batchId');
        this.modFields.push({
          id: batchId,
          label: '',
          value: '',
        });
      }

      this.fields.forEach((permit: any) => {
        const nonJAFFormgroup = this.fb.group({
          [permit['id']]: '',
        });
        this.nonJAFArray.push(nonJAFFormgroup);
      });
      this.extFields.forEach((permit: any) => {
        const extFormGroup = this.fb.group({
          [permit['id']]: '',
        });
        this.extArray.push(extFormGroup);
      });
      this.modFields.forEach((permit: any) => {
        const modFormGroup = this.fb.group({
          [permit['id']]: '',
        });
        this.modArray.push(modFormGroup);
      });
      this.transFields.forEach((permit: any) => {
        const transArray = this.fb.group({
          [permit['id']]: '',
        });
        this.transArray.push(transArray);
      });
    }
    console.log(
      this.nonJAFArray,
      this.fields,
      this.applicationContactsForm.controls,
      'formcontrolsss'
    );
    this.setFormData();
    this.applicationContactsForm.updateValueAndValidity();
    this.cdr.detectChanges();
  }
  getContactSummary() {
    const category = 'C';
    const associatedInd = '1';
    this.projectService
      .getAssociateDetails(associatedInd, category)
      .then((res) => {
        this.applicantContacts = res.applicants;
      })
      .catch((err) => {});
  }
  openWarningModal() {
    let apiData = this.getFormData();
    let allContacts: any[] = [...(this.allContacts ? this.allContacts : [])];
    console.log(allContacts,'allcontacts',apiData)
    apiData.forEach((permit: any) => {
      let i = allContacts.findIndex(
        (contact: any) => permit.roleId == contact.roleId
      );
      if (i >= 0) allContacts.splice(i, 1);
    });
    this.leftOutContacts = [...allContacts];
    if (this.leftOutContacts.length > 0) {
      this.modalReference = this.modalService.open(this.warningModal, {
        ariaLabelledBy: 'modal-basic-title',
        size: 'md',
        backdrop: 'static',
      });
    } else {
      this.onSubmit();
    }
  }
  onRemove() {
    let contacts = '';
    this.leftOutContacts.forEach((contact: any, i) => {
      if (i != 0) contacts += ',' + contact.applicantId;
      else contacts += contact.applicantId;
    });

    this.projectService.removeContacts(contacts).then((response) => {
      this.onSubmit();
      this.modalService.dismissAll();
    });
  }
  getFormData() {
    let apiData: any = [];
    let formObject: any = {};
    if (this.EAValidated) {
      this.fields.forEach((field: any) => {
        formObject = {
          applicationId: '',
          permitTypeCode: '',
          roleId: '',
          edbApplnId: '',
          transType: '',
          batchId: '',
          programId: '',
        };
        this.nonJAFArray.value.forEach((obj: any) => {
          if (Object.keys(obj)[0] == field.id) {
            formObject.roleId = Object.entries(obj)[0][1];
          }
        });

        formObject.permitTypeCode = field.id;
        let i = this.allEtrackPermits.findIndex(
          (x: any) => x.permitTypeCode == field.id
        );
        if (i >= 0) {
          formObject.applicationId = this.allEtrackPermits[i].applicationId;
          formObject.edbApplnId = this.allEtrackPermits[i].edbApplicationId;
          formObject.transType = this.allEtrackPermits[i].transType;
          formObject.batchId = this.allEtrackPermits[i].batchId;
          formObject.programId = this.allEtrackPermits[i].programId;
          let contactselected = this.allEtrackPermits[i].contacts.filter(
            (obj: any) => obj.roleId == formObject.roleId
          );
          if (contactselected.length > 0) {
            formObject.edbPublicId = contactselected[0].edbPublicId;
          }
          apiData.push(formObject);
        }
      });
    } else {
      this.fields.forEach((field: any) => {
        formObject = {
          applicationId: '',
          permitTypeCode: '',
          roleId: '',
          edbApplnId: '',
          transType: '',
          batchId: '',
          programId: '',
        };
        this.nonJAFArray.value.forEach((obj: any) => {
          if (Object.keys(obj)[0] == field.id) {
            formObject.roleId = Object.entries(obj)[0][1];
          }
        });

        formObject.permitTypeCode = field.id;
        let i = this.allEtrackPermits.findIndex(
          (x: any) => x.permitTypeCode == field.id
        );
        if (i >= 0) {
          formObject.applicationId = this.allEtrackPermits[i].applicationId;
          formObject.edbApplnId = this.allEtrackPermits[i].edbApplicationId;
          formObject.transType = this.allEtrackPermits[i].transType;
          formObject.batchId = this.allEtrackPermits[i].batchId;
          formObject.programId = this.allEtrackPermits[i].programId;
          apiData.push(formObject);
        }
      });
      
      this.extPermits.forEach((permit: any) => {
        formObject = {
          applicationId: permit.applicationId,
          permitTypeCode: permit.permitTypeCode,
          roleId: '',
          edbApplnId: permit.edbApplicationId,
          transType: permit.transType,
          batchId: permit.batchId,
          programId: permit.batchId,
        };
        this.extArray.value.forEach((obj: any) => {
          if (Object.keys(obj)[0] == permit.batchId) {
            formObject.roleId = Object.entries(obj)[0][1];
          }
        });
        apiData.push(formObject);
      });
      this.modPermits.forEach((permit: any) => {
        formObject = {
          applicationId: permit.applicationId,
          permitTypeCode: permit.permitTypeCode,
          roleId: '',
          edbApplnId: permit.edbApplicationId,
          transType: permit.transType,
          batchId: permit.batchId,
          programId: permit.batchId,
        };
        this.modArray.value.forEach((obj: any) => {
          if (Object.keys(obj)[0] == permit.batchId) {
            formObject.roleId = Object.entries(obj)[0][1];
          }
        });
        apiData.push(formObject);
      });
      this.supplementalForms?.forEach((permit: any) => {
        formObject = {
          applicationId: permit.applicationId,
          permitTypeCode: permit.permitTypeCode,
          roleId: this.applicationContactsForm.get('jaf')?.value,
          edbApplnId: permit.edbApplicationId,
          transType: permit.transType,
          batchId: permit.batchId,
          programId: permit.batchId,
        };
        
        apiData.push(formObject);
      });
    }
    console.log(apiData, 'apiDtaaa');
    return apiData;
  }
  onSubmit() {
    console.log(
      this.applicationContactsForm.valid,
      this.applicationContactsForm.dirty
    );
    if (
      this.applicationContactsForm.valid &&
      this.applicationContactsForm.dirty
    ) {
      console.log('hey');
      let apiData = this.getFormData();
      this.projectService
        .submitAppContactsForm(apiData)
        .subscribe((response: any) => {
          if (this.isArrowClicked) {
            this.isArrowClicked = false;
            this.navigateToMainPage();
          } else {
            this.navigateToMainPage();
          }
        });
    } else if (
      this.applicationContactsForm.valid &&
      !this.applicationContactsForm.dirty
    ) {
      console.log('2');
      if (this.isArrowClicked) {
        this.isArrowClicked = false;
        this.navigateToMainPage();
      } else {
        this.navigateToMainPage();
      }
    }
  }
}

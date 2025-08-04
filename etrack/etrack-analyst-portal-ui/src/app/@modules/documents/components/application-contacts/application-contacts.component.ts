import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import {
  UntypedFormArray,
  UntypedFormBuilder,
  FormControl,
  UntypedFormGroup,
  FormArray,
} from '@angular/forms';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import {
  get,
  isEqual,
  groupBy,
  isEmpty,
  values,
  cloneDeep,
  toString,
  uniq,
  trim,
} from 'lodash';
import contactResponse from 'src/assets/data/permits.json';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { ModalConfig } from 'src/app/modal.config';
import { formatDate } from '@angular/common';
import { Utils } from 'src/app/@shared/services/utils';
import { ErrorService } from 'src/app/@shared/services/errorService';

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
  jafForm: any;
  modExtForm: any = {};
  nonJafRoleIds: any;

  nonJafContacts: any[] = [];

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
  serverErrorMessage: any='';
  showServerError: boolean=false;

  constructor(
    private modalService: NgbModal,
    private projectService: ProjectService,
    private fb: UntypedFormBuilder,
    private cdr: ChangeDetectorRef,
    private router: Router,
    public commonService: CommonService,
    private utils: Utils,
    private errorService: ErrorService
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
      nonJafRoleIds: [''],
      jafRoleIds: [''],
      nonJaf: new FormArray([]),
      ext: this.fb.array([]),
      mod: this.fb.array([]),
      trans: this.fb.array([]),
      jaf: '',
      type: this.fb.array([]),
    });
  }

  getName(i: number) {
    if (this.getControls()[i].value.formName) {
      return this.getControls()[i].value.formName;
    }
    return '';
  }

  getContacts(i: number) {
    if (this.getControls()[i].value.contacts) {
      return this.getControls()[i].value.contacts;
    }
    return [];
  }

  getControls() {
    return (<FormArray>this.applicationContactsForm.get('nonJaf')).controls;
  }
  get nonJAFArray() {
    return this.applicationContactsForm.get('nonJaf') as FormArray;
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
        let i = 0;
        for (let batchId in this.groupedBatchExtPermits) {
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
  }

  selectChange(obj: any, event: any) {
  }

  getFormDetails() {
    this.nonJafContacts = [];
    this.projectService.getAppContactDetails().then((response: any) => {
      if (response) {
        this.formDetails = response['permit-assign'];
        for (let key in this.formDetails) {
          if (key === 'JAF') {
            this.jafForm = this.formDetails[key];
            if (this.jafForm) {
              if (this.jafForm.contacts) {
                this.jafForm.contacts.forEach((contact: any) => {
                  if (contact.permitAssignedInd == 'Y') {
                    this.applicationContactsForm.patchValue({
                      jafRoleIds: contact.roleId,
                    });
                  }
                });
                if (this.jafForm.contacts.length == 1) {
                  this.applicationContactsForm.patchValue({
                    jafRoleIds: this.jafForm.contacts[0].roleId,
                  });
                  this.applicationContactsForm.controls.jafRoleIds.markAsDirty();
                }
              }
            }
          } else {
            let formObject: any = {};
            let selectedRoleId = '';

            formObject.formName = this.formDetails[key].formName;
            formObject.contacts = this.formDetails[key].contacts;
            if (formObject.contacts) {
              formObject.contacts.forEach((contact: any) => {
                if (contact.permitAssignedInd == 'Y') {
                  selectedRoleId = '' + contact.roleId + '';
                }
              });
            }

            if (formObject.contacts.length == 1) {
              selectedRoleId = '' + formObject.contacts[0].roleId + '';
              this.applicationContactsForm.controls.nonJaf.markAsDirty();
            }

            formObject.applicationIds = this.formDetails[key].applicationIds;
            formObject.selected = selectedRoleId;
            formObject.permitFormId = this.formDetails[key].permitFormId;
            this.nonJafContacts.push(formObject);
          }
        }

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
        this.close();
      }
    }
  }
  close() {
    if (this.applicationContactsForm.dirty) {
      this.openConfirmModal();
    } else {
      this.commonService.navigateToMainPage();
    }
  }
  openWarningModal() {
    this.leftOutContacts = [];

    let contactsList: any[] = [];
    let selectedContactList: any[] = [];
    // get non selected Contact role Ids
    let jafSelectedRoleId =
      this.applicationContactsForm.getRawValue().jafRoleIds;
    selectedContactList.push(toString(jafSelectedRoleId));
    if (this.jafForm) {
      if (this.jafForm.contacts) {
        this.jafForm.contacts.forEach((contact: any) => {
          contactsList.push({
            applicantId: contact.applicantId,
            displayName: contact.displayName,
            selected: jafSelectedRoleId,
            permitAssignedInd: contact.permitAssignedInd,
            roleId: contact.roleId,
          });
        });
      }
    }

    if (this.nonJafContacts && this.nonJafContacts.length > 0) {
      for (let i = 0; i <= this.nonJafContacts.length; i++) {
        if (this.nonJafContacts[i] && this.nonJafContacts[i] != undefined) {
          let selectedRoleId = this.nonJafContacts[i].selected;
          selectedContactList.push(toString(selectedRoleId));
          this.nonJafContacts[i].contacts.forEach((con: any) => {
            contactsList.push({
              applicantId: con.applicantId,
              displayName: con.displayName,
              selected: selectedRoleId,
              permitAssignedInd: con.permitAssignedInd,
              roleId: con.roleId,
            });
          });
        }
      }
    }

    // selectedContactList.forEach((data:any)=>{
    //   let res = contactsList.find(ob=>ob['roleId'] === data)
    // })
    const selectedContactListUniq = uniq(selectedContactList);
    const contactsListUniq = contactsList.filter(
      (item, i, arr) =>
        arr.findIndex((t) => t.applicantId === item.applicantId) === i
    );
    this.leftOutContacts = contactsListUniq.filter((contact) => {
      return selectedContactListUniq.indexOf(toString(contact.roleId)) == -1;
    });
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
      this.applicationContactsForm.controls.nonJaf.markAsDirty();
      this.applicationContactsForm.controls.jafRoleIds.markAsDirty();
      this.onSubmit();
      this.modalService.dismissAll();
    });
  }
  getFormData() {
    let apiData: any = [];

    if (this.jafForm?.applicationIds) {
      for (
        let index = 0;
        index <= this.jafForm.applicationIds.length;
        index++
      ) {
        if (
          this.jafForm.applicationIds[index] &&
          this.jafForm.applicationIds[index] != undefined
        ) {
          apiData.push({
            applicationId: this.jafForm.applicationIds[index],
            roleId: this.applicationContactsForm.getRawValue().jafRoleIds,
            permitFormId: this.jafForm.permitFormId,
          });
        }
      }
    }
    if (this.nonJafContacts && this.nonJafContacts.length > 0) {
      for (let i = 0; i <= this.nonJafContacts.length; i++) {
        if (this.nonJafContacts[i] && this.nonJafContacts[i] != undefined) {
          if (
            this.nonJafContacts[i].applicationIds &&
            this.nonJafContacts[i].applicationIds != undefined &&
            this.nonJafContacts[i].selected
          ) {
            this.nonJafContacts[i].applicationIds.forEach((id: any) => {
              apiData.push({
                applicationId: id,
                roleId: this.nonJafContacts[i].selected,
                permitFormId: this.nonJafContacts[i].permitFormId,
              });
            });
          }
        }
      }
    }    
    const apiDataFiltered = apiData.filter((data:any) => !isEmpty(trim(data.roleId)));
    return  uniq(apiDataFiltered);
  }

  makeFormDirt(event: any) {
    if (event.target.value) {
      this.applicationContactsForm.controls.nonJaf.markAsDirty();
    }
  }
  onSubmit() {
    if (
      this.applicationContactsForm.valid &&
      this.applicationContactsForm.dirty
    ) {
      let apiData = this.getFormData();
      if(!apiData.length){
        this.navigateToMainPage();
        return;
      }
      this.utils.emitLoadingEmitter(true);
        this.projectService
          .submitAppContactsForm(apiData)
          .subscribe((response: any) => {
            this.utils.emitLoadingEmitter(false);
            if (this.isArrowClicked) {
              this.isArrowClicked = false;
              this.navigateToMainPage();
            } else {
              this.navigateToMainPage();
            }
          },
          (error: any) => {
            
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          },
          );
    } else if (
      this.applicationContactsForm.valid &&
      !this.applicationContactsForm.dirty
    ) {
      if (this.isArrowClicked) {
        this.isArrowClicked = false;
        this.navigateToMainPage();
      } else {
        this.navigateToMainPage();
      }
    }
  }
}

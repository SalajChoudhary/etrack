import { Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormGroup,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { Utils } from '../../../../@shared/services/utils';
import { DocumentService } from 'src/app/@shared/services/documentService';
import { formatDate } from '@angular/common';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { Router } from '@angular/router';
import { DashboardService } from 'src/app/@shared/services/dashboard.service';
import moment from 'moment';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DimsrConfirmationPopupComponent } from '../../../../@shared/components/dimsr-confirmation-popup/dimsr-confirmation-popup.component';
import { ErrorService } from '../../../../@shared/services/errorService';
import { CommonService } from 'src/app/@shared/services/commonService';
import { WarningPopUpComponent } from 'src/app/@shared/components/warning-pop-up/warning-pop-up.component';

@Component({
  selector: 'app-dimsr',
  templateUrl: './dimsr.component.html',
  styleUrls: ['./dimsr.component.scss'],
})
export class DimsrComponent implements OnInit {

  searchForm!: UntypedFormGroup;
  dimcrForm!: UntypedFormGroup;
  transTypeForm!: UntypedFormGroup;
  submitted: boolean = false;
  dimcrsubmitted: boolean = false;
  errorMessages: any;
  sucessful: boolean = false;
  error: boolean = false;
  uiMessage: string = '';
  panelOpenState: boolean = false;
  ngModelType: boolean = false;
  response: any;
  existingPermits: any;
  pendingPermits: any;
  modalReference: any;
  documents: any = [];
  effecDate: string = '';
  facilityname: string = '';
  @ViewChild('pendingPopup', { static: true })
  pendingPopup!: PendingChangesPopupComponent;
  @ViewChild('warningPopup', { static: true })
  warningPopup!: WarningPopUpComponent;

  @ViewChild('dimsrconfirmationpopup', { static: true })
  dimsrconfirmationpopup!: DimsrConfirmationPopupComponent;
  PopUpMessage: string = 'Message';
  disablepropety: boolean = false;
  currentDate: Date = new Date();
  showexistingDIMSRerror: boolean = false;
  showDocuments: boolean = false;
  showSerchResult: boolean = false;
  showServerError = false;
  serverErrorMessage!: string;
  isOnlyPendingPermits: boolean = false;
  existingPermitsCopy: any;
  pendingPermitWarning: boolean = false;
  constructor(
    private formBuilder: UntypedFormBuilder,
    private commonService: CommonService,
    public utils: Utils,
    public ngbmodelservice: NgbModal,
    public docService: DocumentService,
    private dashboardService: DashboardService,
    private router: Router,
    private errorService: ErrorService,
    private modalService:NgbModal
  ) {
    this.searchForm = this.formBuilder.group({
      searchItemText: new UntypedFormControl('', [
        Validators.required,
        Validators.minLength(12),
        this.utils.searchTextValidatorDecId,
      ]),
    });
    this.dimcrForm = this.formBuilder.group({
      ProjectManager: new UntypedFormControl('', [Validators.required]),
      MailingDate: new UntypedFormControl('', [Validators.required]),
      EffectiveDate: new UntypedFormControl('', [Validators.required]),
      Notes: new UntypedFormControl('', [Validators.required]),
    });
    this.transTypeForm = this.formBuilder.group({
      transType: new UntypedFormControl(null, [Validators.required]),
    });
  }
  get MailingDateValid() {
    const value = this.dimcrForm.get('MailingDate')?.value;
    if (!value) {
      return true;
    }
    let cuurentDate = new Date();
    return moment(value, 'YYYY-MM-DD').isSameOrBefore(moment(cuurentDate));
  }
  get EffectiveDateValid() {
    const value = this.dimcrForm.value.EffectiveDate;
    let startDate = new Date(this.dimcrForm.value.MailingDate);
    startDate.setDate(startDate.getDate() + 15);
    if (!value) {
      return true;
    }
    return moment(value, 'YYYY-MM-DD').isSameOrAfter(moment(startDate));
  }
  SetEffective() {
    let startDate = new Date(this.dimcrForm.value.MailingDate);
    startDate.setDate(startDate.getDate() + 16);
    const eedate = formatDate(startDate, 'yyyy-MM-dd', 'en-US');
    this.effecDate = moment(eedate).format('L');
    this.dimcrForm.controls['EffectiveDate'].setValue(eedate);
  }

  onDropdownBlur(){
    this.isInputClicked=false;
    this.dimcrForm.get('ProjectManager')?.setValue('');
  }
  ngOnInit(): void {
    this.commonService.emitErrorMessages.subscribe((val) => {
      console.log(val, 'dimsr val');
      if (val) this.errorMessages = this.commonService.getErrorMsgsObj();

      console.log('j here', this.errorMessages);
    });
    this.getAnalystsList();
    this.Warnings1 = false;
    this.Warnings2 = false;
    this.showServerError = false;
  }

  clearSearchForm() {
    this.showSerchResult = false;
    this.facilityname = '';
    this.clearMessages();
    this.searchForm.controls.searchItemText.setValue('');
    this.submitted = false;
    this.dimcrsubmitted = false;
    this.showFacilityExists = true;
  }
  transTypeRequired: boolean = false;
  reponsenullMessage: string = '';
  reponsenNullshow: boolean = false;
  showFacilityExists: boolean = true;
  onSearch() {
    this.clearMessages();
    this.submitted = true;
    if (
      this.searchForm.controls.searchItemText.errors?.required ||
      this.searchForm.controls.searchItemText.value.length != 12 ||
      this.searchForm.controls.searchItemText.errors?.invalidDecSearch
    )
      return;
    this.utils.emitLoadingEmitter(true);
    const ID = this.searchForm.controls.searchItemText.value.replaceAll(
      '-',
      ''
    );
    this.docService
      .getDimsrDetails(ID)
      .then((res) => {
        if (res != null) {
          this.response = res;
          this.existingPermits = res.existingPermits;
          this.existingPermitsCopy = JSON.parse(
            JSON.stringify(res.existingPermits )
          );
          this.pendingPermits = res.pendingPermits;
          this.facilityname = res.facility.facilityname;
          this.showSerchResult = true;
          this.pendingPermitWarning = Object.keys(this.pendingPermits).length > 0;
          console.log("Pending permits warning, ",this.pendingPermitWarning );
          this.isOnlyPendingPermits =
            Object.keys(this.existingPermits).length == 0 &&
            Object.keys(this.pendingPermits).length > 0;
          this.showFacilityExists =
            Object.keys(this.existingPermits).length > 0 ||
            Object.keys(this.pendingPermits).length > 0
              ? true
              : false;
        } else {
          this.reponsenullMessage =
            'No data found for DEC ID: ' +
            this.searchForm.controls.searchItemText.value;
          this.reponsenNullshow = true;
        }
        this.utils.emitLoadingEmitter(false);
      })
      .catch((ex) => {
        this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = this.errorService.getServerMessage(ex);
        this.showServerError = true;
        throw ex;
      });

    this.dimcrForm.valueChanges.subscribe((data) => {
      this.disablepropety = true;
    });

    this.searchForm.valueChanges.subscribe((data) => {
      this.reponsenNullshow = false;
    });
  }
  selectedReviewer: any;
  isInputClicked: boolean = false;


  searchTextChange(e: any) {
    if (
      this.searchForm.controls.searchItemText.errors?.required ||
      this.searchForm.controls.searchItemText.value.length != 12 ||
      this.searchForm.controls.searchItemText.errors?.invalidDecSearch
    )
      this.showSerchResult = false;
  }

  //TODO: wipe of commented code once functionality approved by DEC
  isShowWarning(propName: string) {
    if (!this.existingPermitsCopy || !this.existingPermits) return null;
    const oldTransType = this.existingPermitsCopy[propName][0].transType;
    const newTransType = this.existingPermits[propName][0].transType;
    // if (
    //   (oldTransType == 'REN' ||
    //     oldTransType == 'RTN' ||
    //     oldTransType == 'DIM') &&
    //   (newTransType == 'DIM' ||
    //     newTransType == 'DTN' ||
    //     newTransType == 'DIS' ||
    //     newTransType == 'DIR')
    // ) {
    //   return  this.errorMessages.PEND_REN_DIMSR_REQ_WARN;
    // }
    // if (
    //   (oldTransType == 'MOD' ||
    //     oldTransType == 'AA' ||
    //     oldTransType == 'MTN' ||
    //     oldTransType == 'MNM') &&
    //   (newTransType == 'DIM' || newTransType == 'DTN')
    // ) {
    //   return this.errorMessages.PEND_MOD_DIMSR_REQ_WARN;
    // }
    if(this.pendingPermitWarning){
      return  this.errorMessages.PEND_REN_DIMSR_REQ_WARN;
    }
   return null;
  }
  IDs: string[] = [];
  DisableProperty(existPermit: any, permit: any, transType: string) {
    for (let index = 0; index < existPermit.value.length; index++) {
      existPermit.value[index]['transType'] = transType;
    }
    if (this.IDs.filter((e) => e.includes(existPermit.key)).length == 0)
      this.IDs.push(existPermit.key);

    this.disablepropety = true;
  }
  projectManagers: any = [];
  projectManagersList: any = [];

  
  getAnalystsList() {
    this.dashboardService.getAnalystsByRegion(0).subscribe(
      (response) => {
        response?.sort((a: any, b: any) => {
          if (b.managerName > a.managerName) return -1;
          return 1;
        });
        this.projectManagers = response;
        this.projectManagersList = response;
      },
      (err) => {
        this.projectManagers = [];
      }
    );
  }
  details: any = [];

  Validation(){

    console.log("Pending permit ? ", Object.keys(this.pendingPermits).length > 0)
    if( Object.keys(this.pendingPermits).length > 0){
      this.Warnings1 = true;
      this.Warnings2 = true;
      
    }
  }
//TODO: Once Above validation Functionality sufficient, remove this
  // Validation() {
  //   this.Warnings1 = false;
  //   this.Warnings2 = false;
  //   this.showexistingDIMSRerror = false;
  //   const Keys = Object.keys(this.existingPermitsCopy);
  //   for (let index = 0; index < Keys.length; index++) {
  //     const element = this.existingPermitsCopy[Keys[index]];
  //     console.log(element, 'element');
  //     for (let index1 = 0; index1 < element.length; index1++) {
  //       const transType = element[index1].transType;
  //       console.log(transType, 'transtype');
  //       switch (transType) {
  //         case 'DIM':
  //         case 'DTN':
  //         case 'DIR':
  //         case 'DIS':
  //           this.showexistingDIMSRerror = true;
  //           break;
  //         case 'REN':
  //         case 'RTN':
  //         case 'REI':
  //           this.Warnings1 = true;
  //           break;
  //         case 'MOD':
  //         case 'MNM ':
  //         case 'AA':
  //         case 'MTN':
  //           if (
  //             this.transTypeForm.controls.transType.value === 'DTN' ||
  //             this.transTypeForm.controls.transType.value === 'DIM'
  //           ) {
  //             this.Warnings2 = true;
  //             break;
  //           }
  //       }
  //       if (this.showexistingDIMSRerror || this.Warnings1 || this.Warnings2) {
  //         break;
  //       }
  //     }
  //     if (this.showexistingDIMSRerror || this.Warnings1 || this.Warnings2) {
  //       break;
  //     }
  //   }
  //   console.log(
  //     this.Warnings1,
  //     this.Warnings2,
  //     this.showexistingDIMSRerror,
  //     'indicator'
  //   );
  // }
  Warnings1: boolean = false;
  Warnings2: boolean = false;
  onFormSubmit() {
    this.details = [];
    this.transTypeRequired = true;
    this.dimcrsubmitted = true;
    if (
      this.dimcrForm.controls.ProjectManager.errors?.required ||
      this.dimcrForm.controls.MailingDate.errors?.required ||
      this.dimcrForm.controls.EffectiveDate.errors?.required ||
      this.dimcrForm.controls.Notes.errors?.required ||
      this.transTypeForm.controls.transType.errors?.required
    )
      return;

    this.Validation();
    if (this.showexistingDIMSRerror) {
      return;
    } else if (this.Warnings1) {

      this.PopUpMessage = this.errorMessages.PEND_REN_DIMSR_REQ_WARN;
      this.OpenWarning();
    } else if (this.Warnings2) {
      this.PopUpMessage = this.errorMessages.PEND_MOD_DIMSR_REQ_WARN;
      this.OpenWarning();
    } else if (
      !this.showexistingDIMSRerror &&
      !this.Warnings1 &&
      !this.Warnings2
    ) {
      this.SaveDimsrDetails(true);
    }
  }
  getProjectManagerName(id:string){
    const manager= this.projectManagersList.find((x:any)=>x.userId===id);
    if(manager)return manager.managerName;
    return '';
  }

  getProjectAssignedRoleId(id:string){
    const assignedAnalystRoleId= this.projectManagersList.find((x:any)=>x.userId===id);
    if(assignedAnalystRoleId)return assignedAnalystRoleId.analystRoleId;
    return '';
  }
  SaveDimsrDetails(Valid: boolean) {
    if (Valid) {
      this.utils.emitLoadingEmitter(true);
      const res = {
        facilityName: this.facilityname,
        decId: this.response.facility.decId,
        edbDistrictId: this.response.facility.edbDistrictId,
       
        projectDesc: this.dimcrForm.controls.Notes.value,
        intentMailingDate: moment(
          this.dimcrForm.controls.MailingDate.value
        ).format('L'),
        proposedEffDate: moment(
          this.dimcrForm.controls.EffectiveDate.value
        ).format('L'),
        assignedAnalystName: this.getProjectManagerName(this.dimcrForm.controls.ProjectManager.value),
        analystAssignedId:this.dimcrForm.controls.ProjectManager.value, 
        analystRoleId: this.getProjectAssignedRoleId(this.dimcrForm.controls.ProjectManager.value),
        permits: [{}],
      };
      res.permits = [];
      this.IDs.forEach((element: any) => {
        const d = this.existingPermits[element];
        for (let index = 0; index < d.length; index++) {
          this.details.push(d[index]);
        }
      });
      this.details.forEach((element: any) => {
        let data = {
          permitTypeCode: element.permitTypeCode,
          edbApplnId: element.edbApplicationId,
          batchId: element.batchId,
          transType: element.transType,
          programId: element.programId,
          edbTrackingInd: element.edbTrackingInd
        };
        res.permits.push(data);
      });
      this.docService
        .submitDimsrDetails(res)
        .then(async (res) => {
          this.utils.emitLoadingEmitter(false);
          this.modalReference = await this.dimsrconfirmationpopup.open(res);
        })
        .catch((ex) => {
          this.utils.emitLoadingEmitter(false);
          this.serverErrorMessage = this.errorService.getServerMessage(ex);
          this.showServerError = true;
          throw ex;
        });
    }
  }
  async close() {
    this.modalReference = await this.pendingPopup.open();
  }

  async OpenWarning() {
    this.modalReference = this.modalService.open(this.warningPopup, {
      size: 'warning-pop',
      backdrop: 'static',
    });
    this.modalReference.result.then(
      (result:any) => {},
      (reason:any) => {}
    );
  }
  goBack() {
    window.location.reload();
  }
  goBack1() {
    this.SaveDimsrDetails(true);
  }
  clearMessages() {
    this.reponsenNullshow = false;
    this.reponsenullMessage = '';
    this.response = [];
    this.sucessful = false;
    this.error = false;
    this.uiMessage = '';
    this.showServerError = false;
  }
  onInputChange(event: string) {
    this.dimcrForm.patchValue({ briefDesc: event });
    this.dimcrForm.updateValueAndValidity();
  }
}

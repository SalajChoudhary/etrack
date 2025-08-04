import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import {
  NgbActiveModal,
  NgbModal,
  NgbModalOptions,
} from '@ng-bootstrap/ng-bootstrap';
import {
  isEmpty,
  groupBy,
  cloneDeep,
  values,
  get,
  isEqual,
  flatten,
  intersection,
  isArray,
} from 'lodash';
import { Subject, fromEvent } from 'rxjs';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { PermitSelectionSummaryKeys } from '../permit-selection-summary-keys';
import { PermitSelectionSummaryModifyPopupComponentComponent } from '../permit-selection-summary-modify-popup-component/permit-selection-summary-modify-popup-component.component';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { formatDate } from '@angular/common';
import moment from 'moment';
import { ActivatedRoute, Router } from '@angular/router';
import { Utils } from 'src/app/@shared/services/utils';

@Component({
  selector: 'app-permit-selection-summary-common-popup-component',
  templateUrl:
    './permit-selection-summary-common-popup-component.component.html',
  styleUrls: [
    './permit-selection-summary-common-popup-component.component.scss',
  ],
})
export class PermitSelectionSummaryCommonPopupComponentComponent
  implements OnInit, OnChanges
{
  permitSelectionSummaryKeys: any[] = [];
  permitSelectionSummaryResponse: any = {};
  transTypes: any = [];
  errorMsgObj!: any;

  @ViewChild('pendingPopup', { static: true })
  pendingPopup!: PendingChangesPopupComponent;

  summaryConfig = {
    title: 'New York State Department of Environmental Conservation',
    showHeader: false,
    showClose: false,
  };
  @ViewChild('uploadConfirmDeleteModal', { static: true })
  uploadConfirmDeleteModal!: any;
  @ViewChild('trackedRecordDeleteWarningModal', { static: true })
  trackedRecordDeleteWarningModal!: any;
  //@ViewChild('confirmBatchDeleteModal', { static: true })
  //confirmBatchDeleteModal!: any;

  currentTab = 0;
  emergencyInd: any = '';
  applicantsCollection: any = {};
  projectId: any = localStorage.getItem('projectId');
  newPermits: any = [];
  modifyPermits: any = [];
  transferPermits: any = [];
  showPermitSelectionModalContent: boolean = false;
  mode: any = localStorage.getItem('mode');
  confirmDeleteBodyText: string =
    'Are you sure you want to permanently delete the selected item?';
  private unsubscriber: Subject<void> = new Subject<void>();
  aquaticPestacideCode: string = 'CC';
  @Output() onNextClick = new EventEmitter();
  @Output() hasModifyPermits = new EventEmitter();
  deleteIsClicked: Subject<boolean> = new Subject();
  permitSelectedToDelete: any = {};
  projectSelectionValidated: boolean = false;
  showPesticidesError: boolean = false;
  etrackPermitsHasZeroTrackingInd:boolean = false;
  nonEtrackPermitHasZeroTrackingInd: boolean = false;
  nonEtrackPermitHasMoreThanOneTrackingInd: boolean = false;
  etrackPermitOneBatchOneTrackingInd: boolean = false;
  etrackPermitGPOneBatchInd:boolean = false;
  etrackPermitGPWithinOneBatchSelectedInd: boolean = false
  batchPermitsSelectedToDelete: any = [];
  pageFrom!: string;
  uploadToDartFormIsDirty: boolean = false;
  permitSelectionSummaryResponseUnmodified: any = {};
  showServerError = false;
  serverErrorMessage!: string;
  receivedDate: any;
  @ViewChild('successPopup', { static: true })
  successPopup!: any;

  successMsg = 'Successfully Uploaded to DART';

  get isReadOnly() {
    return this.mode == 'read' || this.projectSelectionValidated;
  }
  get isValidate() {
    return this.mode == 'validate';
  }

  permitGroups: any;
  permitGroupKeys: any;

  batchGroupOptions: any[] = ['A', 'B', 'C', 'D', 'E'];
  get isReceivedDateValid() {
    if (!this.receivedDate) {
      return true;
    }
    return moment(this.receivedDate, 'YYYY-MM-DD').isBefore(moment());
  }
  constructor(
    public activeModal: NgbActiveModal,
    public projectService: ProjectService,
    private modalService: NgbModal,
    private errorService: ErrorService,
    public router: Router,
    public activatedRoute: ActivatedRoute,
    public utils: Utils //private docService: DocumentService,
  ) //public commonService: CommonService,
  {}
  ngOnChanges(changes: SimpleChanges): void {}

  ngOnInit(): void {
    this.getTransTypes();
    this.getPermits();
  }
  getTransTypes() {
    this.projectService.getTransTypes().then((res: any) => {
      this.transTypes = res;
    });
  }

  editPermits(permit: any) {}

  onSuccesPopupOkClicked() {
    this.activeModal.close();
    this.backToHomePage();
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }

  clearErrorMessagesUpload() {
    this.showPesticidesError = false;
    this.etrackPermitsHasZeroTrackingInd = false;
    this.nonEtrackPermitHasZeroTrackingInd = false;
    this.nonEtrackPermitHasMoreThanOneTrackingInd = false;
    this.etrackPermitOneBatchOneTrackingInd = false;
    this.etrackPermitGPOneBatchInd = false; 
    this.etrackPermitGPWithinOneBatchSelectedInd = false; 
  }
  async closeIsClicked() {
    if (this.uploadToDartFormIsDirty) {
      const modalReference = await this.pendingPopup.open();
      return;
    }

    this.activeModal.close();
  }
  onCloseConfirmation() {
    this.activeModal.close();
  }

  okIsClicked() {
    this.clearErrorMessagesUpload(); 
    if (!this.nonEtrackPermitHasZeroTrackingIndFn()) {
      this.nonEtrackPermitHasZeroTrackingInd = true;
      return;
    }
    if (!this.nonEtrackPermitHasMoreThanOneTrackingIndFn()) {
        this.nonEtrackPermitHasMoreThanOneTrackingInd = true;
      return;
    }
    if(!this.etrackPermitsHasZeroTrackingIndFn()){
      this.etrackPermitsHasZeroTrackingInd = true;
      return;
    }
    if (!this.etrackPermitOneBatchOneTrackingIndFn()) {
      this.etrackPermitOneBatchOneTrackingInd = true;
      return;
    }
    
    if(this.etrackPermitGPIndOneBatchFn()){   
      const etrackPermits = get(
        this.permitSelectionSummaryResponse,
        'etrack-permits',
        []
      );
      if(!isEmpty(etrackPermits)) {
        this.etrackPermitGPOneBatchInd = true; 
        return;
      }
    }

    if(this.etrackPermitGPWithinOneBatchFn()){
      const etrackPermits = get(
        this.permitSelectionSummaryResponse,
        'etrack-permits',
        []
      );
      if(!isEmpty(etrackPermits)) {
      this.etrackPermitGPWithinOneBatchSelectedInd = true;
      return;
      }
    }

    


    let payload: any = {};
    for (let key in this.permitSelectionSummaryResponse) {
      payload[key] = {};
      const flattenedResponse = flatten(
        this.permitSelectionSummaryResponse[key]
      );
      payload[key] = flattenedResponse.map((item: any) => {
        if (key != 'etrack-permits') {
          return {
            applicationId: item?.applicationId,
            permitTypeCode: item?.permitTypeCode,
            edbApplnId: item?.edbApplicationId,
            batchId: item?.batchId,
            programId: item?.programId,
            transType: item?.transType,
            modifiedTransType: item?.validatedSelTransType,
            trackingInd: item.trackingInd ? 1 : 0,
          };
        } else {
          return {
            applicationId: item?.applicationId,
            permitTypeCode: item?.permitTypeCode,
            transType: item?.transType || 'NEW',
            batchGroup: item.batchGroup || null,
            trackingInd: item.trackingInd ? 1 : 0,
          };
        }
      });
    }

    let submitPayload: any = {};
    submitPayload['receivedDate'] = formatDate(
      this.receivedDate,
      'MM/dd/yyyy',
      'en-US'
    );
    submitPayload['reviewedPermits'] = payload;
  
    this.utils.emitLoadingEmitter(true);

   
    this.projectService.reviewedPermits(submitPayload).then(
      (res) => {
        // Proceed to next step;
        this.clearErrorMessagesUpload();
        this.utils.emitLoadingEmitter(false);
        this.successPopup.open();
      },
      (error: any) => {
        this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  backToHomePage() {
    this.router.navigate(['/dashboard']);
  }

  nonEtrackPermitHasZeroTrackingIndFn() {
    let allNonEtrackPermitsHasAtleastOneTrackingInd = true;
    for (let key in this.permitSelectionSummaryResponse) {
      if (key != 'etrack-permits') {
        const flattenedResponse = flatten(
          this.permitSelectionSummaryResponse[key]
        );
        if (!isEmpty(flattenedResponse)) {
          const flattenedResponseGroup = groupBy(flattenedResponse, 'batchId');
          for (let groupKey in flattenedResponseGroup) {
            const permitWithTrackingInd = flattenedResponseGroup[
              groupKey
            ].filter((item: any) => item.trackingInd);
            if (!permitWithTrackingInd.length) {
              allNonEtrackPermitsHasAtleastOneTrackingInd = false;
            }
          }
        }
      }
    }
    return allNonEtrackPermitsHasAtleastOneTrackingInd;
  }
  nonEtrackPermitHasMoreThanOneTrackingIndFn() {
    let allNonEtrackPermitsHasExactlyOneTrackingInd = true;
    for (let key in this.permitSelectionSummaryResponse) {
      if (key != 'etrack-permits') {
        const flattenedResponse = flatten(
          this.permitSelectionSummaryResponse[key]
        );
        if (!isEmpty(flattenedResponse)) {
          const flattenedResponseGroup = groupBy(flattenedResponse, 'batchId');
          for (let groupKey in flattenedResponseGroup) {
            const permitWithTrackingInd = flattenedResponseGroup[
              groupKey
            ].filter((item: any) => item.trackingInd);
            if (permitWithTrackingInd.length > 1) {
              allNonEtrackPermitsHasExactlyOneTrackingInd = false;
            }
          }
        }
      }
    }
    return allNonEtrackPermitsHasExactlyOneTrackingInd;
  }

  etrackPermitsHasZeroTrackingIndFn(){
    
    const etrackPermits = get(
      this.permitSelectionSummaryResponse,
      'etrack-permits',
      []
    );
    if (isEmpty(etrackPermits)) {
      return true;
    }
    const etrackPermitsFlattened = flatten(etrackPermits);
    const etrackPermitsFlattenedGroupBy = groupBy(
      etrackPermitsFlattened,
      'batchGroup'
    );
    let trackingIndSelected = 0;
    for (let key in etrackPermitsFlattenedGroupBy) {
      const trackingIndItems = etrackPermitsFlattenedGroupBy[key].filter(
        (item: any) => item.trackingInd
      );
      trackingIndSelected += trackingIndSelected+trackingIndItems.length 
    }
    return trackingIndSelected;
  }
  etrackPermitOneBatchOneTrackingIndFn() {
    const etrackPermits = get(
      this.permitSelectionSummaryResponse,
      'etrack-permits',
      []
    );
    if (isEmpty(etrackPermits)) {
      return true;
    }
    const etrackPermitsFlattened = flatten(etrackPermits);
    const etrackPermitsFlattenedGroupBy = groupBy(
      etrackPermitsFlattened,
      'batchGroup'
    );
    let onlyOneTrackingIndPerBatchSelected = true;
    for (let key in etrackPermitsFlattenedGroupBy) {
      const trackingIndItems = etrackPermitsFlattenedGroupBy[key].filter(
        (item: any) => item.trackingInd
      );
      if (trackingIndItems.length != 1) {
        onlyOneTrackingIndPerBatchSelected = false;
      }
    }
    return onlyOneTrackingIndPerBatchSelected;
  }

  etrackPermitGPIndOneBatchFn() {
    const etrackPermits = get(
      this.permitSelectionSummaryResponse,
      'etrack-permits',
      []
    );
    if (isEmpty(etrackPermits)) {
      return true;
    }
    const etrackGPPermitsFlattened = flatten(etrackPermits);
    let nonGeneralPermitGroup : any = [];
    let generalPermitGroup : any = [];
    let onlyOneBatchForGPPermits = false;
    nonGeneralPermitGroup = etrackGPPermitsFlattened.filter((item: any) => item.permitTypeCode.indexOf('GP') === -1);
    generalPermitGroup = etrackGPPermitsFlattened.filter((item: any) => item.permitTypeCode.indexOf('GP') != -1);     
    
    const generalPermitGroupBy = groupBy(generalPermitGroup,'batchGroup');
    for (let key in generalPermitGroupBy) {
      generalPermitGroupBy[key].forEach((data:any) =>{
        const nonPermitGroupList = nonGeneralPermitGroup.filter((vt:any) => vt.batchGroup === data.batchGroup);
        if(nonPermitGroupList.length > 0){
          onlyOneBatchForGPPermits = true;
        }
      })
    }
    return onlyOneBatchForGPPermits  ;
  }

  etrackPermitGPWithinOneBatchFn() {
    const etrackPermits = get(
      this.permitSelectionSummaryResponse,
      'etrack-permits',
      []
    );
    if (isEmpty(etrackPermits)) {
      return true;
    }
    const etrackGPPermitsFlattened = flatten(etrackPermits);
    let nonGeneralPermitGroup : any = [];
    let generalPermitGroup : any = [];
    let onlyOneBatchWithinGPPermits = false;
    nonGeneralPermitGroup = etrackGPPermitsFlattened.filter((item: any) => item.permitTypeCode.indexOf('GP') === -1);
    generalPermitGroup = etrackGPPermitsFlattened.filter((item: any) => item.permitTypeCode.indexOf('GP') != -1);     
 
    const generalPermitGroupBy = groupBy(generalPermitGroup,'batchGroup');

    if(Object.keys(generalPermitGroupBy).length != generalPermitGroup.length){
      onlyOneBatchWithinGPPermits = true;
    }
    
    return onlyOneBatchWithinGPPermits  ;
  }

  uploadConfirmDelete(permit: any) {
    if (permit.trackingInd) {
      const trackedRecordDeleteWarningModal = this.modalService.open(
        this.trackedRecordDeleteWarningModal,
        {
          size: '20vh',
        }
      );
      trackedRecordDeleteWarningModal.result.then(() => {
        return;
      });
      return;
    }

    if (
      permit.permitTypeCode !== this.aquaticPestacideCode &&
      this.newPermits.length === 2
    ) {
      let permitCodes: string[] = [];
      this.newPermits.forEach((permit: any) => {
        permitCodes.push(permit.permitTypeCode);
      });
      if (permitCodes.includes(this.aquaticPestacideCode)) {
        this.showPesticidesError = true;
        return;
      }
    }
    // this.deleteIsClicked.next(true);
    this.permitSelectedToDelete = permit;
    const modelSize = '20vh';
    const modalReference = this.modalService.open(
      this.uploadConfirmDeleteModal,
      {
        ariaLabelledBy: 'modal-basic-title',
        size: modelSize,
      }
    );
    modalReference.result.then(
      (result) => {
        if (isEmpty(this.permitSelectedToDelete)) {
          return;
        }
        const permit = cloneDeep(this.permitSelectedToDelete);
        this.permitSelectedToDelete = {};
        this.projectService
          .deleteProjectSelectionSummary(
            permit.applicationId,
            permit.permitTypeCode
          )
          .then((result) => {
            this.showPesticidesError = false;
            this.getPermits();
          });
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  getPermits() {
    this.newPermits = [];
    this.modifyPermits = [];
    this.transferPermits = [];
    this.projectService.getPermitForSummaryScreen().then(
      (res) => {
        if (res.receivedDate) {
          this.receivedDate = formatDate(
            res.receivedDate,
            'yyyy-MM-dd',
            'en-US'
          );
        }
        // add batchNo, trackingInd and transtype property
        for (let key in res) {
          if (
            key != 'validateInd' &&
            key != 'emergencyInd' &&
            key != 'constrnType' &&
            key != 'receivedDate'
          ) {
            res[key].forEach((item: any) => {
              item.validatedSelTransType =
                item.validatedSelTransType || item.transType || null;
              item.batchGroup = item.batchGroup || 'A';
              item.trackingInd = item.trackingInd || 0;
            });
          }
        }
        let prepared: any = {};
        for (let key in res) {
          const groupKey =
            key !== 'etrack-permits' ? 'batchId' : 'applicationId';
          if (isArray(res[key]) && !isEmpty(res[key])) {
            res[key].forEach((item: any) => {
              if (!isEmpty(item['availableTransTypes'])) {
                item['availableTransTypes'].forEach((transType: any) => {
                  transType.selected = false;
                  if (transType.code == 'MOD' && isEqual(item.modReqInd, 'Y')) {
                    transType.selected = true;
                  }
                  if (
                    transType.code == 'REN' &&
                    isEqual(item.renewReqInd, 'Y')
                  ) {
                    transType.selected = true;
                  }
                  if (
                    transType.code == 'EXT' &&
                    isEqual(item.extnReqInd, 'Y')
                  ) {
                    transType.selected = true;
                  }
                  if (
                    transType.code == 'XFER' &&
                    isEqual(item.transferReqInd, 'Y')
                  ) {
                    transType.selected = true;
                  }
                });
              }
            });
          }
          const grouped = groupBy(res[key], groupKey);
          prepared[key] = cloneDeep(values(grouped));
        }
        this.permitSelectionSummaryResponse = prepared;
        delete this.permitSelectionSummaryResponse['receivedDate']; // remove receivedDate
        delete this.permitSelectionSummaryResponse['validateInd']; // remove validateInd
        delete this.permitSelectionSummaryResponse['emergencyInd']; //remove emergencyInd
        delete this.permitSelectionSummaryResponse['constrnType']; //remove construction Type from response
        this.permitSelectionSummaryResponseUnmodified = res;

        this.emergencyInd =
          Number(get(res, 'emergencyInd', 0)) > 0 ? true : false;
        let hasPermits = false;
        this.permitSelectionSummaryKeys.forEach((item) => {
          if (!isEmpty(this.permitSelectionSummaryResponse[item.ref])) {
            hasPermits = true;
          }
        });
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  onUploadDeleteConfirmPopupClosed(modalReference: any) {
    modalReference.result.then(
      (result: any) => {
        if (isEmpty(this.permitSelectedToDelete)) {
          return;
        }

        const permit = cloneDeep(this.permitSelectedToDelete);
        this.uploadCleanDeleteConfirmation();
        this.projectService
          .deleteProjectSelectionSummary(
            permit.applicationId,
            permit.permitTypeCode
          )
          .then(
            (result) => {
              this.ngOnInit();
            },
            (error: any) => {
              this.serverErrorMessage =
                this.errorService.getServerMessage(error);
              this.showServerError = true;
              throw error;
            }
          );
      },
      (reason: any) => {
        this.uploadCleanDeleteConfirmation();
      }
    );
  }
  uploadCleanDeleteConfirmation() {
    this.permitSelectedToDelete = {};
    this.deleteIsClicked.next(false);
  }
}

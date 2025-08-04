import {
  Component,
  OnInit,
  ViewChild,
  EventEmitter,
  Output,
  ElementRef,
} from '@angular/core';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { ProjectService } from 'src/app/@shared/services/projectService';
import {
  get,
  isEmpty,
  values,
  flatten,
  filter,
  isEqual,
  cloneDeep,
  groupBy,
  intersection,
  isArray,
  trim,
  find,
  remove,
  clone,
} from 'lodash';
import { CommonService } from 'src/app/@shared/services/commonService';
import { DocumentService } from 'src/app/@shared/services/documentService';
import { PermitSelectionSummaryKeys } from './permit-selection-summary-keys';

import {
  ModalDismissReasons,
  NgbModal,
  NgbModalOptions,
  NgbModalRef,
} from '@ng-bootstrap/ng-bootstrap';
import { fromEvent, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import _ from 'lodash';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { PermitSelectionSummaryModifyPopupComponentComponent } from './permit-selection-summary-modify-popup-component/permit-selection-summary-modify-popup-component.component';
import { PermitSelectionSummaryCommonPopupComponentComponent } from './permit-selection-summary-common-popup-component/permit-selection-summary-common-popup-component.component';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { ApplnPermitDescModalComponent } from './appln-permit-desc-modal/appln-permit-desc-modal.component';
import { Utils } from 'src/app/@shared/services/utils';

@Component({
  selector: 'app-permit-selection-summary',
  templateUrl: './permit-selection-summary.component.html',
  styleUrls: ['./permit-selection-summary.component.scss'],
})
export class PermitSelectionSummaryComponent implements OnInit {
  summaryConfig = {
    title: 'New York State Department of Environmental Conservation',
    showHeader: false,
    showClose: false,
  };
  @ViewChild('confirmDeleteModal', { static: true }) confirmDeleteModal!: any;
  @ViewChild('confirmBatchDeleteModal', { static: true })
  confirmBatchDeleteModal!: any;
  @ViewChild('etrackPermitDeleteWarningModal', { static: true })
  etrackPermitDeleteWarningModal!: any;
  @ViewChild('dartPendingTxsEdbTransTypesExistsInWatchListModal', {
    static: true,
  })
  dartPendingTxsEdbTransTypesExistsInWatchListModal!: any;

  @ViewChild('permitSelectionSummaryForm', { static: false })
  permitSelectionSummaryForm!: ElementRef;

  currentTab = 0;
  emergencyInd: any = '';
  applicantsCollection: any = {};
  projectId: any = '';
  newPermits: any = [];
  modifyPermits: any = [];
  transferPermits: any = [];

  renewalPermits: any = [];
  transPermits: any = [];
  extPermits: any = [];
  modalReference: any;
  @ViewChild('pendingPopup', { static: true })
  warningModal!: PendingChangesPopupComponent;
  showPermitSelectionModalContent: boolean = false;
  mode: any = localStorage.getItem('mode');
  confirmDeleteBodyText: string =
    'Are you sure you want to permanently delete the selected item?';
  private unsubscriber: Subject<void> = new Subject<void>();
  errorMsgObj!: any;
  aquaticPestacideCode: string = 'CC';

  receivedDate: any;
  @Output() onNextClick = new EventEmitter();
  @Output() hasModifyPermits = new EventEmitter();
  @Output() emitHasPermits = new EventEmitter();
  @ViewChild('modal') modal!: CustomModalPopupComponent;

  deleteIsClicked: Subject<boolean> = new Subject();
  permitSelectedToDelete: any = {};
  projectSelectionValidated: boolean = false;
  showPesticidesError: boolean = false;
  mustHaveAtleastOneTrackedRecord: boolean = false;
  batchPermitsSelectedToDelete: any = [];
  pageFrom!: string;
  permitSelectionSummaryKeys: any[] = PermitSelectionSummaryKeys;
  permitSelectionSummaryResponse: any = {};
  permitSelectionSummaryResponseUnmodified: any = {};
  showServerError = false;
  serverErrorMessage!: string;
  modifiedPermits: any[] = [];
  permits: any = [];
  numberOfBatchIds: number = 0;
  conModAnswerMap = new Map<string, string>([]);
  //scenario 2 extend to: checkbox
  conExtensionMap = new Map<string, string>([]);
  showYesNoRequiredError: boolean = false;
  //scenario 3
  operatingPermitAnswerMap = new Map<string, string>([]);
  get hasPermits() {
    // return (
    //   this.modifyPermits?.length ||
    //   this.transferPermits?.length ||
    //   this.newPermits?.length
    // );
    let hasPermits = false;
    this.permitSelectionSummaryKeys.forEach((item) => {
      if (!isEmpty(this.permitSelectionSummaryResponse[item.ref])) {
        hasPermits = true;
      }
    });
    this.emitHasPermits.emit(hasPermits);
    return hasPermits;
  }

  get isReadOnly() {
    return this.mode == 'read' || this.projectSelectionValidated;
  }
  get isValidate() {
    return this.mode == 'validate';
  }

  permitGroups: any;
  permitGroupKeys: any;
  transTypes: any = [];
  batchGroupOptions: any[] = ['A', 'B', 'C', 'D', 'E'];
  constructor(
    public projectService: ProjectService,
    private modalService: NgbModal,
    public commonService: CommonService,
    private docService: DocumentService,
    private errorService: ErrorService,
    private utils:Utils
  ) {}

  ngOnInit(): void {
    this.getTransTypes();
    this.getPermits();
    this.projectId = localStorage.getItem('projectId');
    this.getAllErrorMsgs();
    this.commonService.stepThreePageFrom.subscribe((data: string) => {
      this.pageFrom = data;
      // console.log('this.pageFrom', this.pageFrom);
    });
    this.verifyPendingTxrForValidateMode();
    //diables browswers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }

  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val) => {
      if (val) this.errorMsgObj = this.commonService.getErrorMsgsObj();
    });
  }

  getPermits() {
    this.utils.emitLoadingEmitter(true);
    this.newPermits = [];
    this.modifyPermits = [];
    this.transferPermits = [];
    this.projectService.getPermitForSummaryScreen().then(
      (res) => {
      //  console.log('Get Permits');
        this.receivedDate = res.receivedDate;
        this.projectSelectionValidated = isEqual(
          get(res, 'validateInd', 'N'),
          'Y'
        );
       // console.log(this.receivedDate, ' ', res.receivedDate);
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
                item.validatedSelTransType || item.edbTransType || null;
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
              //  console.log('item', item);
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
        delete this.permitSelectionSummaryResponse['receivedDate']; // remove received Date from response
        delete this.permitSelectionSummaryResponse['validateInd']; // remove validateInd
        delete this.permitSelectionSummaryResponse['emergencyInd']; //remove emergencyInd
        delete this.permitSelectionSummaryResponse['constrnType']; //remove construction Type from response
        this.permitSelectionSummaryResponseUnmodified = res;

        this.emergencyInd =
          Number(get(res, 'emergencyInd', 0)) > 0 ? true : false;
        // console.log(
        //   'Emergency',
        //   get(res, 'emergencyInd') + '=' + this.emergencyInd
        // );
        let hasPermits = false;
        this.permitSelectionSummaryKeys.forEach((item) => {
          if (!isEmpty(this.permitSelectionSummaryResponse[item.ref])) {
            hasPermits = true;
          }
        });
        if (!hasPermits && this.pageFrom !== 'selection') {
          this.openPermitSelectionModal();
        }
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    ).finally(()=>{
    this.utils.emitLoadingEmitter(false);

    });
  }

  getTransTypes() {
    this.projectService.getTransTypes().then((res: any) => {
      const omitTransTypes = ['DIM', 'DIR', 'DIS', 'DTN'];
      const allowedTransTypes = res.filter((item:any) => omitTransTypes.indexOf(item.transTypeCode) == -1); 
      this.transTypes = allowedTransTypes;
    });
  }

  closeSearchModal(event: any) {}

  openPermitSelectionModal() {
    this.clearErrorMessages();
    this.showPermitSelectionModalContent = true;
    this.modal.open('xl');
  }
  closeSummaryModal(event: any) {
    this.modal.close();
  }

  onPermitSelectionClose(event: any) {
    this.modal.close();
    this.showPermitSelectionModalContent = false;
    this.ngOnInit();
  }

  editPermits(permit: any) {}

  canDeleteEtrackPermit(permit: any) {
    if (!permit.trackingInd) {
      // if tracking Ind is false it can delete
      return true;
    }
    const etrackPermits = get(
      this.permitSelectionSummaryResponseUnmodified,
      'etrack-permits',
      []
    );
    // Group etrackPermits by batchGroup
    const groupedByBatchGroup = groupBy(etrackPermits, 'batchGroup');
    const permitsByBatchGroup = get(groupedByBatchGroup, permit.batchGroup, []);
    if (permitsByBatchGroup.length == 1) {
      // if only one permit is available it can delete
      return true;
    }
    // filter by trackingInd
    const permitsByBatchGroupWithTrackingInd = permitsByBatchGroup.filter(
      (item) => item.trackingInd
    );
    if (permitsByBatchGroupWithTrackingInd.length != 1) {
      // If there are more than one trackingInd, it not an orphan. so  it can delete
      return true;
    }
    if (!this.isValidate) {
      // should be able to delete in data entry mode (i.e not validate mode)
      return true;
    }
    const options = { ariaLabelledBy: 'modal-basic-title', size: '25vw' };
    this.modalService.open(this.etrackPermitDeleteWarningModal, options);

    return false;
  }
  checkForAquaticPestacideCode(permit: any) {
    const etrackPermits: any =
      this.permitSelectionSummaryResponseUnmodified['etrack-permits'];
    if (etrackPermits?.length != 2) {
      return true;
    }
    const orphanedPermit = etrackPermits.find(
      (item: any) => !isEqual(item.permitTypeCode, permit.permitTypeCode)
    );
    if (!orphanedPermit) {
      return true;
    }
    if (!isEqual(orphanedPermit.permitTypeCode, this.aquaticPestacideCode)) {
      return true;
    }
    return false;
  }
  confirmBatchDelete(key: any, permits: any) {
    // console.log('Batch Delete call');

    this.showPesticidesError = false;
    if (key == 'etrack-permits') {
      if (!this.canDeleteEtrackPermit(permits)) {
        return;
      }
      if (!this.checkForAquaticPestacideCode(permits)) {
        this.showPesticidesError = true;
        return;
      }
      this.batchPermitsSelectedToDelete = {
        key: key,
        permits: [permits],
      };
      // console.log('this.', this.batchPermitsSelectedToDelete);
    } else {
      this.batchPermitsSelectedToDelete = {
        key: key,
        permits: flatten(permits),
      };
    }

    const modelSize = 'lag';
    const modalReference = this.modalService.open(
      this.confirmBatchDeleteModal,
      {
        ariaLabelledBy: 'modal-basic-title',
        size: modelSize,
      }
    );
    modalReference.result.then(
      (result) => {
        // console.log('result', result);
      },
      (reason) => {
        // console.log('reason', reason);
      }
    );
  }

  //WORK HERE
  confirmDelete(permit: any) {
    // console.log('Delete');
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
    const modalReference = this.modalService.open(this.confirmDeleteModal, {
      ariaLabelledBy: 'modal-basic-title',
      size: modelSize,
    });
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
            this.ngOnInit();
          });
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }
  onDeleteConfirmPopupClosed(modalReference: any) {
    modalReference.result.then(
      (result: any) => {
        if (isEmpty(this.permitSelectedToDelete)) {
          return;
        }

        const permit = cloneDeep(this.permitSelectedToDelete);
        this.cleanDeleteConfirmation();
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
        this.cleanDeleteConfirmation();
        // console.log('reason - from permit selection summary component', reason);
      }
    );
  }
  cleanDeleteConfirmation() {
    this.permitSelectedToDelete = {};
    this.deleteIsClicked.next(false);
  }

  onClose(e?: any) {
    if (!this.isValidate) {
      this.commonService.navigateToMainPage();
      return;
    }
    if (e) {
      this.openConfirmModal();
      return;
    }
    if (
      this.permitSelectionSummaryForm &&
      this.permitSelectionSummaryForm.nativeElement.classList.contains(
        'ng-dirty'
      )
    ) {
      this.openConfirmModal();
    } else {
      this.commonService.navigateToMainPage();
    }
  }

  async openConfirmModal() {
    this.modalReference = await this.warningModal.open();
  }

  onPermitTypeSubmitRevised() {
    this.clearErrorMessages();
    if (this.conModAnswerMap.size < this.numberOfBatchIds) {
      this.showYesNoRequiredError = true;
      return;
    }

    this.permits.forEach((permit: any) => {
      let yesNoValue = 'N';
      if (permit.modReqInd && permit.modReqInd == 'Y') {
        if (permit.batchId && permit.batchId != undefined) {
          let yesno = this.conModAnswerMap.get(permit.batchId.toString());
          yesNoValue = yesno == undefined ? 'N' : yesno;
        }
      }
      permit.modQuestionAnswer = yesNoValue;
    });

    // this.existingPermitsSelected.forEach((permit: any) => {
    //   let yesNoValue = 'N';
    //   if (permit.modReqInd && permit.modReqInd == 'Y') {
    //     if (permit.batchId && permit.batchId != undefined) {
    //       let yesno = this.conModAnswerMap.get(permit.batchId.toString());
    //       yesNoValue = yesno == undefined ? 'N' : yesno;
    //     }
    //   }
    //   permit.modQuestionAnswer = yesNoValue;
    // });

    //TODO: Temp code to remove all null objects
    const filteredPermits = this.permits.filter((permit: any) => {
      let allValuesNull = true;
      for (let key in permit) {
        allValuesNull = isEmpty(trim(permit[key])) && allValuesNull;
      }
      return !allValuesNull;
    });
    filteredPermits.forEach((permit: any) => {
      // payload cleanup
      delete permit.generalPermitInd;
    });
    const params = {
      etrackPermits: filteredPermits,
      dartPermits: this.modifiedPermits,
      emergencyInd: 'N',
    };

    // this.projectService.submitPermitTypes(params).subscribe(
    //   (response) => {
    //     this.onSubmit.emit(this.selectedPermitTypes);
    //     this.isSubmitted = false;
    //     this.onClose();
    //     this.onNext(2);
    //     // this.openPop();
    //     // this.onNext();
    //   },
    //   (error: any) => {
    //     this.serverErrorMessage = this.errorService.getServerMessage(error);
    //     this.showServerError = true;
    //     throw error;
    //   }
    // );
  }

  async confirmationForModifiedPermits() {
    return new Promise((resolve: any, reject: any) => {
      const options: NgbModalOptions = {
        centered: true,
        size: 'permit-selection-summary-modify-popup',
      };
      const modalRef = this.modalService.open(
        PermitSelectionSummaryModifyPopupComponentComponent,
        options
      );
      modalRef.componentInstance.modifiedPermitsFromSummary =
        this.modifiedPermits;
      modalRef.componentInstance.isModifiedPermitFromSummary = true;
      modalRef.result.then(
        (result) => {
          if (!result) {
            this.ngOnInit();
            resolve(false);
            return;
          }
          resolve(true);
          // console.log('ok clicked', result);
        },
        (reason) => {
          // console.log('cancel clicked', reason);
          resolve(false);
        }
      );
    });
  }
  applyChangesToOtherPermits(permits: any) {
    return new Promise((resolve: any, reject: any) => {
      const prepared = permits.map((permit: any) => {
        return {
          applicationId: permit?.applicationId,
          permitTypeCode: permit?.permitTypeCode,
          roleId: '',
          edbApplnId: permit?.edbApplicationId,
          edbAuthId: permit?.edbAuthId,
          edbTransType: permit?.edbTransType,
          batchId: permit?.batchId,
          newReqInd: permit?.newReqInd,
          modReqInd: permit?.modReqInd,
          extnReqInd: permit?.extnReqInd,
          transferReqInd: permit?.transferReqInd,
          renewReqInd: permit?.renewReqInd,
          pendingAppTransferReqInd: permit?.pendingAppTransferReqInd,
          programId: permit?.programId,
          edbPermitEffectiveDate: permit?.effectiveStartDate,
          edbPermitExpiryDate: permit?.effectiveEndDate,
          calculatedBatchIdForProcess: permit?.calculatedBatchIdForProcess,
          modQuestionAnswer: permit?.modQuestionAnswer,
        };
      });
      const payload = {
        emergencyInd: this.emergencyInd,
        constrnType: null,
        etrackPermits: [],
        dartPermits: prepared,
      };
      return this.projectService
        .submitPermitTypesValues(payload, false)
        .subscribe((res) => {
          resolve(true);
        });
    });
  }
  async nextButton() {
    // console.log('Next BUTTON CLICK EVENT');
    
    if (this.isValidate) {
      if (
        this.permitSelectionSummaryForm.nativeElement.classList.contains(
          'ng-dirty'
        )
      ) {
        const permitsConcated: any = this.renewalPermits
          .concat(this.extPermits)
          .concat(this.transPermits)
          .concat(this.modifiedPermits);
        /**======= Code fix to remove duplicate entry on ext and transfer */
        let permits: any = [];

        const permitsConcatedGrouped: any = groupBy(
          permitsConcated,
          'applicationId'
        );
        for (let key in permitsConcatedGrouped) {
          if (permitsConcatedGrouped[key].length == 1) {
            permits.push(permitsConcatedGrouped[key][0]);
          } else {
            // Assuming there will be only one duplicate becasuse of extend and transfer. other cases need to test
            const clonePermit = cloneDeep(permitsConcatedGrouped[key][0]);
            const extnReqInds = permitsConcatedGrouped[key].map(
              (item: any) => item.extnReqInd
            );
            const transferReqInds = permitsConcatedGrouped[key].map(
              (item: any) => item.transferReqInd
            );
            if (extnReqInds.indexOf('Y') !== -1) {
              clonePermit.extnReqInd = 'Y';
            }
            if (transferReqInds.indexOf('Y') !== -1) {
              clonePermit.transferReqInd = 'Y';
            }
            permits.push(clonePermit);
          }
        }
        /**======= Code fix to remove duplicate entry on ext and transfer */
        // console.clear();
        // console.log('permits', permits);

        let modifiedPermitsCleared: any = true;        
        if (this.modifiedPermits?.length) {
          let modifiedSelectedCount = 0;
          this.modifiedPermits.forEach((item) => {
            // console.log(item);
            if (item.availableTransTypes?.length > 0) {
              item.availableTransTypes.forEach((types: any) => {
                if (types.selected && types.code == 'MOD') {
                  modifiedSelectedCount = modifiedSelectedCount + 1;
                }
              });
            }
          });
          // console.log('Modified Count ' + modifiedSelectedCount);
          if (modifiedSelectedCount > 0) {
            modifiedPermitsCleared =
              await this.confirmationForModifiedPermits();
          }
        }

        // console.log('Clear', modifiedPermitsCleared);
        if (!modifiedPermitsCleared) {
          this.modifiedPermits = [];
          // user clicked cancel, so dont process other permits too
          return;
        }
        let otherPermitsCleared: any = true;
        if (permits?.length) {
          otherPermitsCleared = await this.applyChangesToOtherPermits(permits);
        }
      }
      this.permitSelectionSummaryForm.nativeElement.reset();
      //this.uploadToDart(event);
      this.ngOnInit();
      this.onNextClick.emit(null);
      return;
    }

    if (!this.isValidate) {
      this.clearErrorMessages();
      this.onNextClick.emit(null);
      return;
    } else {
      this.clearErrorMessages();
    }
    if (
      !this.permitSelectionSummaryForm.nativeElement.classList.contains(
        'ng-dirty'
      )
    ) {
      this.clearErrorMessages();
      this.onNextClick.emit(null);
      return;
    }
    this.onSave();
  }

  hasAtleastOneTrackRecord() {
    let permitHasTrackRecord = true;
    for (let key in this.permitSelectionSummaryResponse) {
      const flattenedResponse = flatten(
        this.permitSelectionSummaryResponse[key]
      );
      if (!isEmpty(flattenedResponse)) {
        const permitWithTrackingInd = flattenedResponse.find(
          (item: any) => item.trackingInd
        );
        if (isEmpty(permitWithTrackingInd)) {
          // console.error(
          //   'Permit with  key ' + key + ' has no tracking indicator'
          // );
          permitHasTrackRecord = false;
        }
      }
    }
    return permitHasTrackRecord;
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
    this.commonService.stepThreePageFrom.next('summary');
  }

  deleteBatch(batchData: any) {
    // console.log('batchData', batchData);
    let payLoad: any = [];
    batchData?.permits.forEach((e: any) => {
      payLoad.push(e.applicationId);
      // if(batchData.key == 'etrack-permits'){
      //   payLoad.push({applicationId: e.applicationId});
      // }else{
      //   payLoad.push({applicationId: e.applicationId});
      // }
    });
    this.projectService.deleteBatch(payLoad).subscribe(
      (res) => {
        this.ngOnInit();
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );

    // deleteBatch(applId: any, permitTypeCode: any){
  }

  clearErrorMessages() {
    this.showPesticidesError = false;
    this.mustHaveAtleastOneTrackedRecord = false;
  }

  onModifyClickedRevised(type: any, permit: any, permitArr: any) {
    // console.log(type);

    const permitClone = clone(permit);
    const permitArrClone = cloneDeep(permitArr);
    if (type.code == 'MOD') {
      const permitExists = find(this.modifiedPermits, (item: any) =>
        isEqual(item.batchId, permitClone.batchId)
      );

      if (isEmpty(permitExists)) {
        const permitArrCloneMapped = permitArrClone.map((item: any) => {
          item.modReqInd = type.selected ? 'Y' : 'N';
          return item;
        });
        this.modifiedPermits =
          this.modifiedPermits.concat(permitArrCloneMapped);
        return;
      }
      remove(this.modifiedPermits, (permit: any) => {
        return isEqual(permit.batchId, permitClone.batchId);
      });
    } else if (type.code == 'REN') {
      const permitExists = find(this.renewalPermits, (item: any) =>
        isEqual(item.batchId, permitClone.batchId)
      );

      if (isEmpty(permitExists)) {
        const permitArrCloneMapped = permitArrClone.map((item: any) => {
          item.renewReqInd = type.selected ? 'Y' : 'N';
          return item;
        });
        this.renewalPermits = this.renewalPermits.concat(permitArrCloneMapped);
        return;
      }
      remove(this.renewalPermits, (permit: any) => {
        return isEqual(permit.batchId, permitClone.batchId);
      });
    } else if (type.code == 'XFER') {
      const permitExists = find(this.transPermits, (item: any) =>
        isEqual(item.batchId, permitClone.batchId)
      );

      if (isEmpty(permitExists)) {
        const permitArrCloneMapped = permitArrClone.map((item: any) => {
          item.transferReqInd = type.selected ? 'Y' : 'N';
          return item;
        });
        this.transPermits = this.transPermits.concat(permitArrCloneMapped);
        return;
      }
      remove(this.transPermits, (permit: any) => {
        return isEqual(permit.batchId, permitClone.batchId);
      });
    } else if (type.code == 'EXT') {
      const permitExists = find(this.extPermits, (item: any) =>
        isEqual(item.batchId, permitClone.batchId)
      );

      if (isEmpty(permitExists)) {
        const permitArrCloneMapped = permitArrClone.map((item: any) => {
          item.extnReqInd = type.selected ? 'Y' : 'N';
          return item;
        });
        this.extPermits = this.extPermits.concat(permitArrCloneMapped);
        return;
      }
      remove(this.extPermits, (permit: any) => {
        return isEqual(permit.batchId, permitClone.batchId);
      });
    }
  }

  onSave() {
    //prepare payload
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
            transType: item?.edbTransType,
            modifiedTransType: item?.validatedSelTransType,
            // "batchGroup": item.batchGroup?parseInt(item.batchGroup):null,
            trackingInd: item.trackingInd ? 1 : 0,
          };
        } else {
          return {
            applicationId: item?.applicationId,
            permitTypeCode: item?.permitTypeCode,
            transType: item?.edbTransType || 'NEW',
            batchGroup: item.batchGroup || null,
            trackingInd: item.trackingInd ? 1 : 0,
          };
        }
      });
    }
    // payload.ppid = localStorage.getItem('ppid');
    this.projectService.reviewedPermits(payload).then((res) => {
      // Proceed to next step;
      this.clearErrorMessages();
      this.onNextClick.emit(null);
      return;
    });
  }

  onModifyClick(event: any, ref: any) {
    event.preventDefault();
    event.stopPropagation();
    const options: NgbModalOptions = {
      centered: true,
      size: 'permit-selection-summary-modify-popup',
    };
    const modalRef = this.modalService.open(
      PermitSelectionSummaryModifyPopupComponentComponent,
      options
    );
    modalRef.componentInstance.key = ref;
    modalRef.result.then(
      (result) => {},
      (reason) => {}
    );
  }

  uploadToDart(event: any) {
    event.preventDefault();
    event.stopPropagation();
    const options: NgbModalOptions = {
      centered: true,
      size: 'permit-selection-summary-common-popup',
    };
    const modalRef = this.modalService.open(
      PermitSelectionSummaryCommonPopupComponentComponent,
      options
    );
    //modalRef.componentInstance.key=ref;
    modalRef.componentInstance.permitSelectionSummaryKeys =
      this.permitSelectionSummaryKeys;
    modalRef.componentInstance.permitSelectionSummaryResponse =
      this.permitSelectionSummaryResponse;
    modalRef.componentInstance.transTypes = this.transTypes;
    modalRef.componentInstance.receivedInputDate = this.receivedDate;
    modalRef.componentInstance.errorMsgObj = this.errorMsgObj;
    modalRef.result.then(
      (result) => {},
      (reason) => {}
    );
  }

  verifyPendingTxrForValidateMode() {
    if (!this.isValidate) {
      return;
    }
    this.projectService.getPermitSummary().then((res: any) => {
      const dartPendingTxs = get(res, 'dart-pending-txr', {});
      if (isEmpty(dartPendingTxs)) {
        return;
      }
      const dartPendingTxsValues = values(dartPendingTxs);
      const dartPendingTxsFlattened = flatten(dartPendingTxsValues);
      const edbTransTypes = dartPendingTxsFlattened
        .map((item) => item.edbTransType)
        .filter((v) => v);
      const watchList = ['DIS', 'DIM', 'DTN', 'DIR'];
      const existsFromWatchList = intersection(edbTransTypes, watchList);
      if (!existsFromWatchList?.length) {
        return;
      }
      const options = { ariaLabelledBy: 'modal-basic-title', size: '25vw' };
      this.modalService.open(
        this.dartPendingTxsEdbTransTypesExistsInWatchListModal,
        options
      );
    });
  }
  canShowAdditionalBubble(permits: any) {
    const hasPermitsWithWWPAndWWN = flatten(permits).filter((item: any) => {
      return (
        ['WWP', 'WWN'].indexOf(item.permitTypeCode) != -1 &&
        isEqual(item.transferReqInd, 'Y')
      );
    });
    return !isEmpty(hasPermitsWithWWPAndWWN);
  }

  showApplnPermitDesc(permit: any) {
    const options: NgbModalOptions = {
      centered: true,
      // size: 'permit-appln-permit-desc-modal',
      size: '20vh',
    };
    const modalRef = this.modalService.open(
      ApplnPermitDescModalComponent,
      options
    );
    modalRef.componentInstance.applnPermitDesc = permit?.applnPermitDesc;
    modalRef.result.then((result) => {});
  }
}

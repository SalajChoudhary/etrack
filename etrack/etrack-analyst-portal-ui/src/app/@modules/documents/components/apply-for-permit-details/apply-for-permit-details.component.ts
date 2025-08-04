import {
  Component,
  EventEmitter,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import {
  ModalDismissReasons,
  NgbModal,
  NgbModalOptions,
  NgbModalRef,
} from '@ng-bootstrap/ng-bootstrap';
import { BehaviorSubject, fromEvent, Subject } from 'rxjs';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { ModalConfig } from 'src/app/modal.config';
import {
  cloneDeep,
  get,
  groupBy,
  isArray,
  isEmpty,
  isEqual,
  values,
} from 'lodash';
import { takeUntil } from 'rxjs/operators';
import { PermitSelectionSummaryCommonPopupComponentComponent } from '../permit-selection-summary/permit-selection-summary-common-popup-component/permit-selection-summary-common-popup-component.component';
import { PermitSelectionSummaryKeys } from '../permit-selection-summary/permit-selection-summary-keys';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { SuccessPopupComponent } from 'src/app/@shared/components/success-popup/success-popup.component';
import { Utils } from 'src/app/@shared/services/utils';

@Component({
  selector: 'app-apply-for-permit-details',
  templateUrl: './apply-for-permit-details.component.html',
  styleUrls: ['./apply-for-permit-details.component.scss'],
})
export class ApplyForPermitDetailsComponent implements OnInit {
  userRoles: any[] = [];
  configs = {
    activityTaskStatus: [],
    applicantTypes: [],
    developmentTypes: [],
  };
  projectStatus: any[] = [];
  isOpenPopupApplicants = new BehaviorSubject<boolean>(false);
  modalReference: any;
  @ViewChild('applyForPermit', { static: true })
  applyForPermitModal!: any;
  closeResult!: string;
  @Output() closeBtnClicked = new EventEmitter();
  isValidateMode = false;
  isReadMode = false;
  isValidatedMode = false;
  restartStepper: boolean = false;
  restartStepperSub: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(
    false
  );
  projectId: any = '';
  newPermits: any = [];
  modifyPermits: any = [];
  transferPermits: any = [];

  renewalPermits: any = [];
  transPermits: any = [];
  extPermits: any = [];
  receivedDate: any;

  @ViewChild('warningModal')
  warningModal!: CustomModalPopupComponent;
  @ViewChild('confirmModal')
  confirmModal!: CustomModalPopupComponent;
  @ViewChild('rejectProjectModal')
  rejectProjectModal!: CustomModalPopupComponent;
  warningConfig!: { title: string; showHeader: boolean };
  rejectProject!:{title: string, showHeader: boolean};
  mode: string = '';
  private unsubscriber: Subject<void> = new Subject<void>();
  showUploadToDartButton: boolean = false;
  showViewDocument: boolean = false;
  showSubmitButton: boolean = false;
  showServerError = false;
  serverErrorMessage!: string;
  permitSelectionSummaryKeys: any[] = PermitSelectionSummaryKeys;
  transTypes: any = [];
  permitSelectionSummaryResponse: any = {};
  permitSelectionSummaryResponseUnmodified: any = {};
  modifiedPermits: any[] = [];
  permits: any = [];
  numberOfBatchIds: number = 0;
  conModAnswerMap = new Map<string, string>([]);
  @ViewChild('successPopup', { static: true })
  successPopup!: SuccessPopupComponent;
  isUploadToDart: boolean=false;
  errorMsgObj!:any;
  rejectProjectDetails: any = [];
  get navigateQueryParams() {
    if (!this.mode) {
      return {};
    }
    return { mode: this.mode };
  }

  constructor(
    public commonService: CommonService,
    public projectService: ProjectService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private modalService: NgbModal,
    private utils: Utils,
    private errorService: ErrorService
  ) {}

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe((params) => {
    });
    this.commonService.activeMode.subscribe((res) => {
      this.mode = res;
    });
    this.userRoles = this.commonService.roles;
    this.projectService
      .getProjectStatus(localStorage.getItem('projectId'))
      .then((res) => {
        this.projectStatus = res;
      });

    this.projectService.getProjectConfigs().then((res) => {
      this.configs = res;
    });

    this.warningConfig = {
      title: '',
      showHeader: false,
    };

    this.rejectProject = {
      title: '',
      showHeader: false,
    }

    if (localStorage.getItem('mode') === 'validate') this.isValidateMode = true;

    if (localStorage.getItem('mode') === 'read') this.isReadMode = true;
    //diables browswers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
      this.getAllErrorMsgs()
  }

  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val) => {
      if (val){
        this.errorMsgObj = this.commonService.getErrorMsgsObj();
      } 
    });
  }

  getStatusOfStep(activityCode: string) {
    //Disable all steps but step 1 for current validateMode scop

    if (this.isReadMode) {
      return true;
    }
    if (this.restartStepper === true) {
      this.projectStatus = ['1'];
    }
    if (
      !this.configs.activityTaskStatus?.length ||
      !this.projectStatus?.length
    ) {
      if (localStorage.getItem('mode') === 'validate') {
        this.showViewDocument = true;
      }
      return;
    }

    //@ts-ignore
    const config: any = this.configs.activityTaskStatus.filter(
      (v: any) => v.activityCode == activityCode
    );
    //@ts-ignore
    let count = 0;
    const status: any[] = this.projectStatus.filter(
      (v) => v.activityStatusId === config[0].activityStatusId
    );
    const statuss = this.projectStatus.filter((v: any) => {
      config.filter((config: any) => {
        if (v.activityStatusId === config.activityStatusId) {
          status.push(v);
        }
      });

      v.activityStatusId == config[0].activityStatusId;
    });
    if (activityCode == 'SEL_PROJ_LOC') {
    }

    if (this.isValidateMode) {
      if (this.projectStatus[4] && this.projectStatus[4].completed === 'Y') {
        this.showUploadToDartButton = true;
        this.isValidatedMode = true;
        this.showViewDocument = false;
      } else {
        this.showViewDocument = true;
      }
    }

    if (
      this.projectStatus[3] &&
      this.projectStatus[3].completed === 'Y' &&
      this.isValidateMode == false
    ) {
      this.showSubmitButton = true;
    }

    return !status.length || (status[0] && status[0].completed == 'N')
      ? false
      : true;
  }

  get isAllStepsCompleted() {
    return (
      this.projectStatus &&
      this.projectStatus[0] &&
      this.projectStatus[0].completed == 'Y' &&
      this.projectStatus[1] &&
      this.projectStatus[1].completed == 'Y' &&
      this.projectStatus[2] &&
      this.projectStatus[2].completed == 'Y' &&
      this.projectStatus[3] &&
      this.projectStatus[3].completed == 'Y' &&
      this.projectStatus[4] &&
      this.projectStatus[4].completed == 'Y'
    );
  }

  isStepDisabled(currentStep: any, prevStep: string) {
    if (this.isReadMode) {
      return false;
    }
    if (this.showUploadToDartButton) {
      return true;
    }
    if (!this.isValidateMode) {
      // data entry mode
      if (!prevStep && currentStep && this.getStatusOfStep(currentStep)) {
        return false;
      }
      if (
        prevStep &&
        currentStep &&
        this.getStatusOfStep(prevStep) &&
        this.getStatusOfStep(currentStep)
      ) {
        return false;
      }
    }

    if (this.isValidateMode) {
      // validate mode
      if (!prevStep && currentStep && this.getStatusOfStep(currentStep)) {
        return true;
      }
      if (
        prevStep &&
        currentStep &&
        this.getStatusOfStep(prevStep) &&
        this.getStatusOfStep(currentStep)
      ) {
        return true;
      }
    }
    if (prevStep && !this.getStatusOfStep(prevStep)) {
      return true;
    }
    if (currentStep && this.getStatusOfStep(currentStep)) {
      return true;
    }
    return false;
  }

  openApplyPermitModal(content: any, windowClass = '') {
    this.warningModal.close();
    this.modalService
      .open(content, { ariaLabelledBy: 'modal-basic-title', windowClass })
      .result.then(
        (result) => {
          this.closeResult = `Closed with: ${result}`;
        },
        (reason) => {
          this.closeResult = `Dismissed`;
        }
      );
  }

  open() {
    this.warningModal.open('md');
  }

  openApplyForPermitModal() {
    localStorage.setItem('projectId', '');
    this.commonService.restartStepper();
    this.router.navigate(['/dashboard']);
    this.openApplyPermitModal(this.applyForPermitModal);
    localStorage.removeItem('mode');
  }
  okClicked(){
    if(this.isUploadToDart){
      this.closeModal(true)
      this.uploadToDart();
    }else{
      this.closeModal(true)
      this.onSubmitProject();
    }
  }
  closeModal(e: any) {
    this.modalService.dismissAll();
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }

  onUploadToDartClicked() {
    this.isUploadToDart=true;
    this.confirmModal.open('sm');
    // this.uploadToDart();
  }

  uploadToDart() {
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
    // modalRef.componentInstance.receivedInputDate = this.error;
    modalRef.result.then(
      (result) => {},
      (reason) => {}
    );
  }

  getPermits() {
    this.newPermits = [];
    this.modifyPermits = [];
    this.transferPermits = [];
    this.projectService.getPermitForSummaryScreen().then(
      (res) => {
        this.receivedDate = res.receivedDate;
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
                console.log('item', item);
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
        delete this.permitSelectionSummaryResponse['receivedDate'];
        delete this.permitSelectionSummaryResponse['validateInd']; // remove validateInd
        delete this.permitSelectionSummaryResponse['emergencyInd']; //remove emergencyInd
        delete this.permitSelectionSummaryResponse['constrnType']; //remove construction Type from response
        this.permitSelectionSummaryResponseUnmodified = res;
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }
  getTransTypes() {
    this.projectService.getTransTypes().then((res: any) => {
      const omitTransTypes = ['DIM', 'DIR', 'DIS', 'DTN'];
      const allowedTransTypes = res.filter((item:any) => omitTransTypes.indexOf(item.transTypeCode) == -1); 
      this.transTypes = allowedTransTypes;
    });
  }

  returnToDash() {
    localStorage.setItem('mode', '');
    this.router.navigateByUrl('/dashboard');
  }

  onViewDocmentsClicked() {
    let projectId = localStorage?.getItem('projectId');
    localStorage.setItem('projectId', projectId!);

    // this.commonService.projectIdChanged.next(true);
    // this.commonService.activeMode.next('');
    //Will this be validate mode???
    // localStorage.setItem('mode', 'validate');
    // localStorage.setItem('emergencyAuth', '');
    window.open('/supporting-documentation/view');
    // this.router.navigate(['/supporting-documentation']);
  }

  onSubmitClicked(){
    //this.isUploadToDart=false;
    this.projectService.getRejectedProjectDetail().then(res=>{
      this.rejectProjectDetails = res;
      console.log('Rejected', !this.rejectProjectDetails?.projectId);
      if(!this.rejectProjectDetails?.projectId){
        this.confirmModal.open('sm');
       
        }
        else{
          this.rejectProjectModal.open('sm');
        }
    })
    
    
  }
  onSubmitProject() {
    this.utils.emitLoadingEmitter(true);
    this.projectService.submitFinalProject().subscribe(
      async (response: any) => {
        this.utils.emitLoadingEmitter(false);
        this.modalReference = await this.successPopup.open();
      },
      (error: any) => {
        console.log(error)
        this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
    // }
  }
  onSuccesPopupOkClicked() {
    this.router.navigateByUrl('/dashboard');
  }
}

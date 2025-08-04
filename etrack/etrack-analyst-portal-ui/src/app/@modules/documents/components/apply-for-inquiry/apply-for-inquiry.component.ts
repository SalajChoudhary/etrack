import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { BehaviorSubject, fromEvent, Subject } from 'rxjs';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { InquiryService } from 'src/app/@shared/services/inquiryService';
import { takeUntil } from 'rxjs/operators';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { Utils } from 'src/app/@shared/services/utils';
import { SuccessPopupComponent } from 'src/app/@shared/components/success-popup/success-popup.component';

@Component({
  selector: 'app-apply-for-inquiry',
  templateUrl: './apply-for-inquiry.component.html',
  styleUrls: ['./apply-for-inquiry.component.scss'],
})
export class ApplyForInquiryComponent implements OnInit {

  userRoles: any[] = [];
  configs = {
    activityTaskStatus: [],
    applicantTypes: [],
    developmentTypes: [],
  };
  inquiryStatus: number = 0;
  isOpenPopupApplicants = new BehaviorSubject<boolean>(false);
  modalReference!: any;
  @ViewChild('applyForInquiry', { static: true }) applyForInquiryModal!: any;
  @ViewChild('confirmModal') confirmModal!: CustomModalPopupComponent;
  @ViewChild('successPopup', { static: true }) successPopup!: SuccessPopupComponent;
  closeResult!: string;
  @Output() closeBtnClicked = new EventEmitter();
  restartStepper: boolean = false;
  showSubmitButton: boolean = false;
  showServerError = false;
  serverErrorMessage!: string;
  restartStepperSub: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  @ViewChild('warningModal') warningModal!: CustomModalPopupComponent;
  warningConfig!: { title: string; showHeader: boolean };
  private unsubscriber: Subject<void> = new Subject<void>();
  showUploadToDartButton: boolean = false;


  constructor(
    public commonService: CommonService,
    private router: Router,
    private modalService: NgbModal,
    private inquiryService:InquiryService,
    private errorService: ErrorService,
    private utils: Utils,
  ) { }
  ngOnInit() {
    this.userRoles = this.commonService.roles;
    let inquiryId=localStorage.getItem('inquiryId');
    if(inquiryId !== undefined &&
    inquiryId !== null &&
    inquiryId !== '' &&
    inquiryId !== '0'){
    this.inquiryService
      .getInquiryStatus(inquiryId)
      .then((res) => {
        this.inquiryStatus = res.status;
        console.log('inquiry status: ', this.inquiryStatus);
      });
    }
    this.warningConfig = { title: '', showHeader: false, };

    //diables browswers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }

  isStepDisabled(stepNumber:number){
    return this.inquiryStatus<stepNumber;
  }

  getStatusOfStep(stepNumber:number){
    return this.inquiryStatus>stepNumber;
  }

  openApplyInquiryModal(content: any, windowClass = '') {
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

  openApplyForInquiryModal() {
    this.resetLocalStorage();
    this.router.navigate(['/dashboard']);
    this.openApplyInquiryModal(this.applyForInquiryModal);
  }

  resetLocalStorage(){
    localStorage.setItem('inquiryId', '');
    localStorage.setItem('inquiryCategoryCode', "");
  }

  returnToDash() {
    this.router.navigateByUrl('/dashboard');
  }

  get isAllStepsCompleted() {
   return this.inquiryStatus===2;
  }

  closeModal(e: any) {
    this.modalService.dismissAll();
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }

  onSubmitClicked(){
    this.confirmModal.open('sm');
  }

  onSubmitInquiry() {
    this.utils.emitLoadingEmitter(true);
    this.inquiryService.submitFinalInquiry().toPromise().then(() => {
      this.utils.emitLoadingEmitter(false);
      this.modalReference = this.successPopup.open();
    }).catch((error) => {
      this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      this.utils.emitLoadingEmitter(false);
      throw error;
    });

  }

  okClicked(){
    this.closeModal(true)
    this.onSubmitInquiry();
  }
  onSuccesPopupOkClicked() {
    this.resetLocalStorage();
    this.router.navigateByUrl('/dashboard');
  }
}

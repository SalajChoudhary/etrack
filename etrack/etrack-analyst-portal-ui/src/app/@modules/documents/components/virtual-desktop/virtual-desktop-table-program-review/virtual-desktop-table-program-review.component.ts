import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { ModalConfig } from 'src/app/modal.config';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { VirtualDesktopService } from 'src/app/@shared/services/virtual-desktop.service';
import { BehaviorSubject } from 'rxjs';
import { EmailComposeComponent } from 'src/app/@shared/components/email-compose/email-compose.component';
import { emailSend } from 'src/app/@store/models/emailSend';
import { reviewComplete } from 'src/app/@store/models/reviewComplete';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ProgramReviewCorrespondenceComponent } from 'src/app/@shared/components/program-review-correspondence/program-review-correspondence.component';
import { isEmpty } from 'lodash';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { Utils } from 'src/app/@shared/services/utils';

@Component({
  selector: 'app-virtual-desktop-table-program-review',
  templateUrl: './virtual-desktop-table-program-review.component.html',
  styleUrls: ['./virtual-desktop-table-program-review.component.scss'],
})
export class VirtualDesktopTableProgramReviewComponent implements OnInit {
  emailData!: emailSend;
  showServerError = false;
  serverErrorMessage!: string;
  confirmationMessage: string = '';
  infoData: emailSend = {
    toEmailId: [],
    ccEmailId: [],
    fromEmailId: '',
    subject: '',
    template: '',
    emailBody: '',
    emailType: '',
    existingContents: [''],
    emailCorrespondenceId: 0,
    projectId: 0,
    topicId: 0,
    emailPurpose: 'Program Review'
  };
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

  @ViewChild('emailModal', { static: false })
  emailModal!: EmailComposeComponent;

  @ViewChild('confirmationPopup', { static: true })
  confirmationPopup!: CustomModalPopupComponent;
  @Input() programReviewers!: any[];
  @Input() projectId:any;
  @Input() inquiryId: any;
  currentReviewer!: any;
  currentReviewerObj!: any;
  $clickEvent!: any;
  currentDate!: Date;
  isReviewCompletedChecked!: boolean;
  nodalConfig: { title: string; showHeader: boolean };
  isAddModalOpen = new BehaviorSubject<boolean>(false);
  @ViewChild('addModal')
  addModal!: CustomModalPopupComponent;
  @Input() isReadOnly: boolean = false;
  @Input() isGi: boolean = false;
  @Input() inquiryCompletedInd: any;
  @Output() reloadData: any = new EventEmitter();

  constructor(
    private vdService: VirtualDesktopService,
    public ngbmodelservice: NgbModal,
    private errorService: ErrorService,
    private utils: Utils
  ) {
    this.nodalConfig = {
      title: 'New York State Department of Environmental Conservation',
      showHeader: true,
      // showClose:true,
    };
  }

  openAddModal() {
    this.isAddModalOpen.next(true);
    this.addModal.open('lg');
  }

  onClose(e: any) {
    console.log(e);
    this.addModal.close();
    this.isAddModalOpen.next(false);
    if (!this.isGi && e.isSaved == true) {
   
      this.emailData.ccEmailId = e.response.ccEmailId;
      this.emailData.toEmailId = e.response.toEmailId;
      this.emailData.fromEmailId = e.response.fromEmailId;
      this.emailData.subject = e.response.subject;
      this.emailData.emailBody = e.response.emailBody;
      this.emailData.existingContents = e.response.existingContents;
      this.emailData.emailCorrespondenceId = e.response.emailCorrespondenceId;
      this.emailModal.open(
        this.emailData,
        false,
        e.selectedReviewer[0].emailAddress
      );
    }
    else if(this.isGi && e.isSaved) {
      this.reloadData.emit(true);
    }
  }
  onEmailClose() {
    this.showServerError = false;
    this.vdService
      .getVirtualDesktopData(this.projectId)
      .toPromise()
      .then(
        (data: any) => {
          console.log(data);
          this.programReviewers = data.body.reviewDocuments;
        },
        (error: any) => {
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      );
  }
  ngOnInit(): void {
    this.currentDate = new Date();
    if(!this.isGi) {

      this.vdService.getReviewer().subscribe((reviewer) => {
        if (reviewer === 'assigned') {
          this.vdService
            .getVirtualDesktopData(this.projectId)
            .toPromise()
            .then((data: any) => {
              console.log(data);
              this.programReviewers = data.body.reviewDocuments;
            });
        }
      });
      this.emailData = this.infoData;
    }
  }

  reviewCompleteClicked(reviewer: any, ev: any) {
    this.$clickEvent = ev;
    this.currentReviewer = reviewer.docReviewerName;
    this.currentReviewerObj = reviewer;

    if(this.$clickEvent.target.checked){
      if(!this.isGi) {

        this.confirmationMessage = "Mark this review Complete and save  correspondence to DMS. For additional coordination with "+ reviewer.docReviewerName +" request a new review."
      }
      else {
        this.confirmationMessage = "Mark this review Complete. For additional coordination with "+ reviewer.docReviewerName +" request a new review."
      }
    }
    else{
      this.confirmOkClicked(false);
      return;
    }
    this.confirmationPopup.open('vd-reviewer');
  }

  confirmCloseClicked() {
    this.$clickEvent.target.checked = false;
    this.confirmationPopup.close();
  }

  confirmOkClicked(markComplete?: any) {
    this.showServerError = false;
    console.log(this.currentReviewerObj);
    console.log(markComplete);
    this.utils.emitLoadingEmitter(true);
    if(!this.isGi) {
      let payload: reviewComplete = new reviewComplete();
      payload.reviewerId = this.currentReviewerObj.docReviewerId;
      payload.documentId = this.currentReviewerObj.documentId;
      payload.documentReviewId = this.currentReviewerObj.documentReviewId;
      payload.docReviewerName = this.currentReviewerObj.docReviewerName;
      payload.correspondenceId = this.currentReviewerObj.correspondenceId;
      this.vdService.markReviewAsComplete(payload, this.projectId).then(
        (response) => {
          this.utils.emitLoadingEmitter(false);
          this.currentReviewerObj.docReviewedInd = 'Y';
          this.confirmationPopup.close();
          this.reloadData.emit(true);
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      );
    }
    else {
      let payload = {
        docReviewedInd: markComplete ? "Y" : "N",
        docReviewerName: this.currentReviewerObj.docReviewerName,
        documentId: this.currentReviewerObj.documentId,
        documentReviewId: this.currentReviewerObj.documentReviewId,
        reviewerId: this.currentReviewerObj.docReviewerId
      };
      console.log("Payload: ");
      console.log(payload);
      console.log(this.currentReviewerObj);
      this.vdService.markGiReviewAsComplete(payload, this.inquiryId).then(
        (response) => {
          this.utils.emitLoadingEmitter(false);
          this.currentReviewerObj.docReviewedInd = payload.docReviewedInd;
          this.confirmationPopup.close();
          this.reloadData.emit(true);
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

  compareDates(dueDate: any) {
    let currentDate = new Date();
    return new Date(currentDate) > new Date(dueDate); // true if currentDate is later
  }

  isReviewCompleteChecked(reviewer: any, checkbox: HTMLInputElement) {
    if (reviewer.docReviewedInd === 'Y') {
      //checkbox.disabled = true;
      return true;
    }
    return false;
  }

  reviewerClicked(reviewer: any) {
    
    const payload = {
      reviewerId: reviewer.docReviewerId,
      documentId: reviewer.documentId,
    };
    if(payload.documentId == null){
      payload.documentId =''
    }
    this.vdService
      .getProgramReviewerCorrespondence(payload, this.projectId)
      .subscribe((res) => {
        const modelref = this.ngbmodelservice.open(
          ProgramReviewCorrespondenceComponent
        );
        modelref.componentInstance.data = res;
      });
  }

  isEmpty(programReviewers: any) {
    return isEmpty(programReviewers);
  }
}

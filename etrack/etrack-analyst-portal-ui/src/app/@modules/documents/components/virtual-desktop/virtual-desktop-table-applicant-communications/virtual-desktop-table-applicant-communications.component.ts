import { Component, OnInit, Input, ViewChild, Output, EventEmitter } from '@angular/core';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { BehaviorSubject } from 'rxjs';
import { EmailComposeComponent } from 'src/app/@shared/components/email-compose/email-compose.component';
import { emailSend } from 'src/app/@store/models/emailSend';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ProgramReviewCorrespondenceComponent } from 'src/app/@shared/components/program-review-correspondence/program-review-correspondence.component';
import { ModalConfig } from 'src/app/modal.config';
import { reviewComplete } from 'src/app/@store/models/reviewComplete';
import { VirtualDesktopService } from 'src/app/@shared/services/virtual-desktop.service';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { CommonService } from 'src/app/@shared/services/commonService';

@Component({
  selector: 'app-virtual-desktop-table-applicant-communications',
  templateUrl: './virtual-desktop-table-applicant-communications.component.html',
  styleUrls: ['./virtual-desktop-table-applicant-communications.component.scss']
})
export class VirtualDesktopTableApplicantCommunicationsComponent implements OnInit {

  @ViewChild('addModal')
  addModal!: CustomModalPopupComponent;
  @Input() isReadOnly: boolean = false;
  isAddModalOpen = new BehaviorSubject<boolean>(false);
  modalConfig: { title: string; showHeader: boolean };
  @Input() communicationsHistory: any;
  @Input() documents: any;
  @Input() existingRequestedDocs: any;
  @Input() projectId: any;
  @Input() onlineSubmitter: any;
  @Output() reloadData: any = new EventEmitter();

  @ViewChild('emailModal')
  emailModal!: EmailComposeComponent;
  emailData!: emailSend;
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
    emailPurpose: ''
  };

  $clickEvent!: any;
  currentCorrespondenceObj!: any;
  showServerError = false;
  serverErrorMessage!: string;

  @ViewChild('confirmationPopup', {static: true})
  confirmationPopup!: CustomModalPopupComponent;

  confirmModalConfig: ModalConfig = {
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

  requestedDocs: any;

  constructor(
    private ngbmodelservice: NgbModal,
    private vdService: VirtualDesktopService,
    private errorService: ErrorService) { 
    this.modalConfig = {
      title: 'New York State Department of Environmental Conservation',
      showHeader: true,
      // showClose:true,
    };
  }

  ngOnInit(): void {
    this.emailData = this.infoData;
  }

  openAddModal() {
    this.isAddModalOpen.next(true);
    this.addModal.open('appl-info');
  }

  closedClicked(communications: any, ev: any) {
    this.$clickEvent = ev;
    this.currentCorrespondenceObj = communications;
    this.confirmationPopup.open('vd-reviewer');
  }

  confirmCloseClicked() {
    this.$clickEvent.target.checked = false;
    this.confirmationPopup.close();
  }

  confirmOkClicked() {
    this.showServerError = false;

    this.vdService.markCommunicationsAsClosed(this.currentCorrespondenceObj, this.projectId).then(
      (response) => {
        this.currentCorrespondenceObj.aplctCorrespCompltdInd = 1;
        this.confirmationPopup.close();
        this.reloadData.emit(true);
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  onClose(e: any) {
    this.addModal.close();
    this.isAddModalOpen.next(false);
    if(e.isSubmitted == true) {
      this.emailData.fromEmailId = e.emailInfo.fromEmail;
      this.emailData.subject = e.emailInfo.subject;
      this.emailData.emailBody = e.emailInfo.bodyMessage;
      this.emailData.existingContents = e.emailInfo.bodyMessage;
      this.emailData.emailPurpose = e.emailInfo.emailPurpose;
      this.emailData.toEmailId = [this.onlineSubmitter.emailAddress];
      this.requestedDocs = e.emailInfo.requestedDocs;
      this.emailModal.open(
        this.emailData,
        false,
        ''
      );
    }
  }

  onEmailClose() {

  }

  getDocumentsList(correspondenceId: number) {
    let documentsList = "";
    this.existingRequestedDocs.forEach((requestedDoc: any) => {
      if(requestedDoc.correspondenceId === correspondenceId) {
        if(!documentsList) {
          documentsList = requestedDoc.documentName;
        }
        else {
          documentsList += ", " + requestedDoc.documentName;
        }
      }
    })
    return documentsList;
  }

  subjectClicked(communications: any) {
    const modelref = this.ngbmodelservice.open(
      ProgramReviewCorrespondenceComponent
    );
    modelref.componentInstance.data = [[communications.emailContent]];
  }

}

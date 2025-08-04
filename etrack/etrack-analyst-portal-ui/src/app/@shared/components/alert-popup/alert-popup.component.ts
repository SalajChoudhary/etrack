import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef, NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { fromEvent, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonService } from '../../services/commonService';
import { ProjectService } from '../../services/projectService';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { get, isEmpty } from 'lodash';
import { ErrorService } from '../../services/errorService';
import { InquiryService } from '../../services/inquiryService';

@Component({
  selector: 'app-alert-popup',
  templateUrl: './alert-popup.component.html',
  styleUrls: ['./alert-popup.component.scss'],
})
export class AlertPopupComponent implements OnInit, OnChanges {
  @ViewChild('alertModal', { static: true })
  alertModal!: CustomModalPopupComponent;
  private unsubscriber: Subject<void> = new Subject<void>();
  modalRef!: NgbModalRef;
  @Input() openModal!: Subject<boolean>;
  @Input() alertData!: any[];
  @Input() pageFrom!: string;
  @Output() closePopup = new EventEmitter();
  projectNote!: string;
  hasAlerts: boolean = false;
  showServerError = false;
  serverErrorMessage!: string;
  alertsToDelete: string[] = [];
  inquiry: any;

  constructor(
    public commonService: CommonService,
    public modalService: NgbModal,
    public router: Router,
    public projectService: ProjectService,
    private inquiryService: InquiryService,
    private errorService: ErrorService
  ) {}

  ngOnChanges() {
    
  }

  ngOnInit(): void {
    this.openModal?.subscribe((v) => {
      if (!v) {
        return;
      }
      this.open();
    });



    //diables browswers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }

  async open() {
    setTimeout(() => {
      if (isEmpty(this.alertData)) {
        this.hasAlerts = false;
      } else this.hasAlerts = true;
      console.log('j here', this.alertData);

      const modalReference = this.modalService.open(this.alertModal, {
        ariaLabelledBy: 'A modal that shows project alerts.',
        size: 'md',
      });
    }, 75);
  }

  showNote(alertId: any) {
    let alert = this.alertData.find((alertObj) => alertObj.alertId == alertId);
    if (alert) {
      this.projectNote = alert.assignmentNote;

      return alert.assignmentNote;
    } else {
      return '';
    }
  }

  close() {
    this.modalService.dismissAll();
    this.closePopup.emit();
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }

  deleteAlerts() {
    this.alertData = this.alertData.filter((alert) => alert.msgRead === 'N');
    if (isEmpty(this.alertData)) {
      this.hasAlerts = false;
    }
  }

  onViewClicked(alert: any, alertId: string) {

    
    alert.isShow = true;
    let viewedAlert = this.alertData.filter(
      (alert) => alert.alertId == alertId
    );
    if (viewedAlert[0].msgRead == 'N') {
      if(!isEmpty(alert?.inquiryId)){
        this.inquiry=this.inquiryService.decodeInquiryId(alert?.inquiryId)
      }
      
      this.projectService.deleteProjectAlert(alertId, alert?.projectId, this.inquiry).then(
        (res) => {
          alert.msgRead = 'Y';
        },
        (error: any) => {
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      );
    }
  }
}

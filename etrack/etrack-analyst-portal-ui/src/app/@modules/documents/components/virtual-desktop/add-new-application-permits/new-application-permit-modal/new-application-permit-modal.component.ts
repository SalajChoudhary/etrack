import {
  Component,
  OnInit,
  Output,
  EventEmitter,
  ViewChild,
  OnDestroy,
  Input,
} from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { VirtualDesktopService } from '../../../../../../@shared/services/virtual-desktop.service';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { Utils } from 'src/app/@shared/services/utils';

@Component({
  selector: 'app-new-application-permit-modal',
  templateUrl: './new-application-permit-modal.component.html',
  styleUrls: ['./new-application-permit-modal.component.scss'],
})
export class NewApplicationPermitModalComponent implements OnInit, OnDestroy {
  serverErrorMessage: any = '';
  successMsg:any='Tesing success message'
  @Input() errorMsgObj:any = {};
  @Output() onCancel = new EventEmitter();
  @Output() onSuccess = new EventEmitter();
  @ViewChild('pendingPopup', { static: true })
  congirmationModal!: PendingChangesPopupComponent;
  @ViewChild('warningModal')
  warningModal!: CustomModalPopupComponent;
  @ViewChild('confirmModal')
  confirmModal!: CustomModalPopupComponent;
  warningConfig!: { title: string; showHeader: boolean };

  form = this.fb.group({
    permit: ['', [Validators.required]],
    batchId: ['', [Validators.required]],
    transType: ['', [Validators.required]],
  });
  isSubmitted:boolean = false;
  availablePermits: any = [];
  availableBatchId: any = [];
  availableTransTypes: any = [];
  activeAuthorizations : any = [];
  subscriptions: Subscription[] = [];
  showServerError = false;
  disableTransType = false;
  projectId: any = '';

  constructor(
    private fb: FormBuilder,
    private virtualDesktopService: VirtualDesktopService,
    private projectService: ProjectService,
    private errorService: ErrorService,
    private modalService: NgbModal,
    private route: ActivatedRoute,
    private utils: Utils,

  ) {}

  ngOnInit(): void {

    // this.projectId = localStorage.getItem('projectId');
    this.route.params.subscribe((params:any)=>{
      this.projectId = params.projectId;
      this.loadAvailablePermits();
      this.loadTransTypes();
    })
  }

  loadTransTypes() {
    this.virtualDesktopService.getVWTransTypes(this.projectId).then((res: any) => {
      const omitTransTypes = ['DIM', 'DIR', 'DIS', 'DTN'];
      const allowedTransTypes = res.filter((item:any) => omitTransTypes.indexOf(item.transTypeCode) == -1); 
      this.availableTransTypes = allowedTransTypes;
    });
  }

  loadAvailablePermits() {
    this.subscriptions.push(
      this.virtualDesktopService.getAvailablePermits(this.projectId).subscribe({
        next: (res: any) => {
          this.availablePermits = res.permitTypes;
          this.availableBatchId = res.batches;
          this.activeAuthorizations = res.activeAuthorizations;
        },
        error: (err: any) => {},
      })
    );
  }


  onSubmitClicked($event:any){    
    this.confirmModal.open('sm');
  }

  okClicked(event:any){   
      this.closeModal(true)
      this.onSaveClick(event);    
  }

  closeModal(e: any) {
    this.modalService.dismissAll();
  }

  onSaveClick(event: any) {
    event.preventDefault();
    event.stopPropagation();
    this.isSubmitted = true;
    if (this.form.valid) {
      let formValue = this.form.getRawValue();
      const payload = {
        //emergencyInd: this.emergencyInd,
        constrnType: null,
        activeAuthorizations : this.activeAuthorizations,
        etrackPermits: [
          {
            applicationId: '',
            permitTypeCode: formValue.permit,
            roleId: '',
            edbApplnId: '',
            edbAuthId: '',
            transType: formValue.transType,
            batchId: formValue.batchId,
            newReqInd: '',
            modReqInd: '',
            extnReqInd: '',
            transferReqInd: '',
            renewReqInd: '',
            pendingAppTransferReqInd: '',
            programId: '',
            edbPermitEffectiveDate: '',
            edbPermitExpiryDate: '',
            calculatedBatchIdForProcess: '',
            modQuestionAnswer: '',
          },
        ],
        dartPermits: [],
      };
    this.utils.emitLoadingEmitter(true);

      
      this.virtualDesktopService.submitPermitTypesValues(payload, this.projectId).then((res: any)=> {
        console.log(res);
          let responseCode = res.status;
          this.utils.emitLoadingEmitter(false);
          if (responseCode == 200) {
            this.onSuccess.emit(true);
          } else if (responseCode == 226) {
            this.showServerError = true;
            this.serverErrorMessage=this.errorMsgObj?.BUILT_PEMIT_ERR;
          }
      }
      ).catch((error:any)=>{
     this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          // throw error;
      }).finally(()=>{
    this.utils.emitLoadingEmitter(false);

      });

      //   next: (res: any) => {},
      //   error: (error: any) => {
      //     this.serverErrorMessage = this.errorService.getServerMessage(error);
      //     this.showServerError = true;
      //     throw error;
      //   },
      // });
    }
  }

  onCancelClicked(event: any) {
    if (this.form.dirty) {
      this.congirmationModal.open();
      return;
    }
    this.onCancel.emit(null);
  }

  onConfirmExit(event: any) {
    this.onCancel.emit(null);
  }
  setTransTypeValue() {
    this.form.get('transType')?.disable()
    this.form.patchValue({transType:""});
    let batchId = this.form.getRawValue().batchId;
    let transList = this.availableBatchId.filter(
      (x: any) => x.batchId === batchId
    );
    if (transList && transList.length > 0) {
      let transType = transList[0].transType;
      this.form.patchValue({ transType: transType });
      this.form.get('transType')?.enable();
    }
    
  }
  ngAfterViewInit(): void {
    this.form.get('transType')?.disable()
    
  }
  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => {
      subscription.unsubscribe();
    });
  }
}

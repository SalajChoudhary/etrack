import { DatePipe } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { validateGreaterThanEqRecvdDate } from 'src/app/@shared/applicationInformation.validator';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { InquiryService } from 'src/app/@shared/services/inquiryService';
import { Utils } from 'src/app/@shared/services/utils';
import { VirtualDesktopService } from 'src/app/@shared/services/virtual-desktop.service';

@Component({
  selector: 'app-gi-response',
  templateUrl: './gi-response.component.html',
  styleUrls: ['./gi-response.component.scss']
})
export class GiResponseComponent implements OnInit {

  @ViewChild('confirmationPopup', { static: true })
  confirmationPopup!: CustomModalPopupComponent;

  @ViewChild('reviewNotCompletePopup', { static: true })
  reviewNotCompletePopup!: CustomModalPopupComponent;

  giFormGroup!: UntypedFormGroup;
  submitted: boolean = false;
  @Input() errorMsgObj: any;
  @Input() inquiryId: any;
  @Input() inquiryType: any;
  geographicalInquiryResponse: any;
  @Input() virtualDesktopData: any;
  @Input() maxNote: any;
  @Input() disableSubmit: any = null;
  @Input() reviewsAreComplete: any;
  receivedDate: any;
  @Output() reloadData: any = new EventEmitter();


  constructor(
    private fb: UntypedFormBuilder,
    private datePipe: DatePipe,
    private utils: Utils,
    private virtualDesktopService: VirtualDesktopService,
    private inquiryService: InquiryService) 
    { }

  ngOnInit(): void {
    this.geographicalInquiryResponse = this.virtualDesktopData?.geographicalInquiryResponse;
    this.receivedDate = this.virtualDesktopData?.receivedDate ? 
      this.virtualDesktopData.receivedDate :
      this.datePipe.transform(new Date(2020, 0, 1), 'yyyy-MM-dd');
    
    this.initForm();

  }

  initForm() {
    const formattedReceivedDate: string | null = this.datePipe.transform(this.receivedDate, 'yyyy-MM-dd') ? 
      this.datePipe.transform(this.receivedDate, 'yyyy-MM-dd') :
      '';
      console.log(formattedReceivedDate);
    this.giFormGroup = this.fb.group({
      sendResponseInd: ['N', Validators.required],
      completeInd: [''],
      responseDate: [this.datePipe.transform(new Date(), 'yyyy-MM-dd'), 
        [Validators.required, 
        validateGreaterThanEqRecvdDate(formattedReceivedDate!)]],
      response: ['', Validators.required]
    });

    this.geographicalInquiryResponse?.responseSentDate
    ? this.giFormGroup.controls.responseDate
      .setValue(this.datePipe.transform(this.geographicalInquiryResponse.responseSentDate, 'yyyy-MM-dd'))
    : null;

    this.geographicalInquiryResponse?.response
      ? this.giFormGroup.controls.response
        .setValue(this.geographicalInquiryResponse.response)
      : null;

    // if(this.geographicalInquiryResponse?.responseSentInd) {
    //   this.giFormGroup.controls.sendResponseInd.setValue
    //     (this.geographicalInquiryResponse.responseSentInd);
    //   this.onSendResponseChange();
    // }

    if(this.geographicalInquiryResponse?.inquiryCompletedInd) {
      this.geographicalInquiryResponse.inquiryCompletedInd === "Y" 
        ? this.giFormGroup.controls.completeInd.setValue(true)
        : this.giFormGroup.controls.completeInd.setValue(false);
      if(this.geographicalInquiryResponse.inquiryCompletedInd === "Y") {
        this.giFormGroup.controls.completeInd.disable();
      }
      this.checkDisableInputs();
    }

    this.giFormGroup.controls.sendResponseInd.disable(); //Disable for pi3, since there are no emails
    this.removeSendResponseValidators(); //Remove these validators since send response defaults to No
    this.giFormGroup.controls.responseDate.updateValueAndValidity();
    this.giFormGroup.controls.response.updateValueAndValidity();

  }

  onFormSubmit() {
    if(!this.reviewsAreComplete && this.giFormGroup.controls.completeInd.value) {
      this.reviewNotCompletePopup.open('vd-reviewer');
      return;
    }
    this.submitted = true;
    this.giFormGroup.controls.responseDate.updateValueAndValidity();
    this.giFormGroup.updateValueAndValidity();
    console.log(this.giFormGroup);
    if(this.giFormGroup.valid) {
      let formValues = this.giFormGroup.value;
      const responseId = this.geographicalInquiryResponse?.inqResponseId 
        ? this.geographicalInquiryResponse.inqResponseId : null;
      const responseText = formValues.response ? formValues.response : 
        this.giFormGroup.controls.response.value;
      const responseDate = formValues.responseDate ? formValues.responseDate : 
        this.giFormGroup.controls.responseDate.value;
      const responseSentInd = formValues.sendResponseInd ? formValues.sendResponseInd :
        this.giFormGroup.controls.sendResponseInd.value;

      let responseData = {
        inqResponseId: responseId,
        responseSentInd: responseSentInd,
        inquiryCompletedInd: formValues.completeInd ? "Y" : "N",
        response: responseText,
        responseSentDate: this.datePipe.transform(responseDate, 'MM/dd/yyyy')
      };
      this.utils.emitLoadingEmitter(true);
      this.virtualDesktopService.submitInquiryResponse(responseData, this.inquiryId)
        .subscribe((res: any) => {
          this.utils.emitLoadingEmitter(false);
          // this.confirmationPopup.open('vd-reviewer');
          this.reloadData.emit(true);
          if(responseData.inquiryCompletedInd === "Y") {
            this.giFormGroup.controls.completeInd.disable();
          }
        }, (err: any) => {
          console.log(err);
          this.utils.emitLoadingEmitter(false);
        });
    }
  }

  onSendResponseChange() {
    this.giFormGroup.controls.sendResponseInd.value === "Y" ? 
    this.setSendResponseValidators() :
    this.removeSendResponseValidators();
    this.giFormGroup.controls.responseDate.updateValueAndValidity();
    this.giFormGroup.controls.response.updateValueAndValidity();
  }

  setSendResponseValidators() {
    const formattedReceivedDate: string | null = this.datePipe.transform(this.receivedDate, 'yyyy-MM-dd') ? 
      this.datePipe.transform(this.receivedDate, 'yyyy-MM-dd') :
      '';
    this.giFormGroup.controls.responseDate.setValidators([Validators.required, 
      validateGreaterThanEqRecvdDate(formattedReceivedDate!)]);
    this.giFormGroup.controls.response.setValidators(Validators.required);
  }

  removeSendResponseValidators() {
    this.giFormGroup.controls.responseDate.removeValidators(Validators.required);
    this.giFormGroup.controls.response.removeValidators(Validators.required);
  }

  checkDisableInputs() {
    this.giFormGroup.controls.completeInd.value ? this.disableInputs() : 
                                                  this.enableInputs();
  }

  onCompleteChange() {
    console.log(this.reviewsAreComplete);
    if(!this.reviewsAreComplete && this.giFormGroup.controls.completeInd.value) {
      this.giFormGroup.controls.completeInd.setValue(false);
      this.giFormGroup.controls.completeInd.updateValueAndValidity();
      this.reviewNotCompletePopup.open('vd-reviewer');
      return;
    }
    this.checkDisableInputs();
  }

  onDateChange() {
    this.checkDisableInputs();
    this.giFormGroup.controls.responseDate.updateValueAndValidity();
  }

  disableInputs() {
    this.giFormGroup.controls.sendResponseInd.disable();
    this.giFormGroup.controls.response.disable();
    this.giFormGroup.controls.responseDate.disable();
    
  }

  enableInputs() {
    // this.giFormGroup.controls.sendResponseInd.enable();
    if(this.giFormGroup.controls.responseDate.status === "DISABLED") {
      this.giFormGroup.controls.responseDate.enable();
    }
    if(this.giFormGroup.controls.response.status === "DISABLED") {
      this.giFormGroup.controls.response.enable();
    }
  }

  formatInquiryId(inqId: any) {
    return this.inquiryService.formatInquiryId(inqId);
  }

  confirmOkClicked() {
    this.confirmationPopup.close();
    this.reloadData.emit(true);
  }

  confirmReviewOkClicked() {
    this.reviewNotCompletePopup.close();
  }

}

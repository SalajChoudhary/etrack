import {
  Component,
  OnInit,
  ViewChild,
  ElementRef,
  Output,
  EventEmitter,
  Input,
} from '@angular/core';
import { ErrorService } from 'src/app/@shared/services/errorService';

import { Router, ActivatedRoute } from '@angular/router';
import { ProjectInvoiceService } from 'src/app/@shared/services/project-invoice.service';
import { environment } from 'src/environments/environment';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  UntypedFormControl,
  Validators,
} from '@angular/forms';
import moment from 'moment';
import { ProjectService } from '../../../../@shared/services/projectService';
import { get, isEmpty, isEqual, set } from 'lodash';
import { takeUntil } from 'rxjs/operators';
import { fromEvent, Subject } from 'rxjs';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { AuthService } from 'src/app/core/auth/auth.service';
import { CommonService } from 'src/app/@shared/services/commonService';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { Utils } from 'src/app/@shared/services/utils';
@Component({
  selector: 'app-project-invoice',
  templateUrl: './project-invoice.component.html',
  styleUrls: ['./project-invoice.component.scss'],
})
export class ProjectInvoiceComponent implements OnInit {
  //projectId = localStorage.getItem('projectId');
  
  private unsubscriber: Subject<void> = new Subject<void>();

  @ViewChild('pendingPopup', { static: true })
  congirmationModal!: PendingChangesPopupComponent;
  @Input() userFullName: any;
  @Input() invoiceId = '';
  @Input() errorMsgObj: any = {};
  @Input() feesAndInvoiceOptions: any = {
    FW: [],
    LG: [],
    TW: [],
  };
  projectInvoiceList = this.feesAndInvoiceOptions.TW;
  freshWaterprojectInvoiceList = this.feesAndInvoiceOptions.FW;
  lngfprojectInvoiceList = this.feesAndInvoiceOptions.LG;
  serverErrorMessage!: string;
  showServerError = false;
  showSameDateCancelError=false;
  dueAmount: any;

  get selectedObject() {
    return {
      tidal: this.projectInvoiceList.find(
        (item: any) =>
          item.invoiceFeeType == this.selectionForm.get('tidal')?.value
      ),
      freshwater: this.freshWaterprojectInvoiceList.find(
        (item: any) =>
          item.invoiceFeeType == this.selectionForm.get('freshwater')?.value
      ),
      lngf: this.lngfprojectInvoiceList.find(
        (item: any) =>
          item.invoiceFeeType == this.selectionForm.get('lngf')?.value
      ),
    };
  }

  get amountDue() {
    const calculatedDueAmount = this.invoiceId !== ''||null? this.dueAmount :(
      (this.selectedObject.tidal?.invoiceFee || 0) +
      (this.selectedObject.freshwater?.invoiceFee || 0) +
      (this.selectedObject?.lngf?.invoiceFee || 0)) ;
    return calculatedDueAmount;
  }

  get canShowCancellationForm() {
    const mode = localStorage.getItem('mode');
    return mode == 'admin';
  }

  // get isSelectionFormCheckDateIsValid() {
  //   const value = this.selectionForm.get('date')?.value;
  //   if (!value) {
  //     return true;
  //   }
  //   return moment(value, 'YYYY-MM-DD').isBefore(moment());
  // }

  selectionForm = this.fb.group({
    lngf: ['', Validators.compose([])],
    freshwater: ['', Validators.compose([])],
    tidal: ['', Validators.compose([])],
    paymentReceived: [false],
    checkNo: ['', Validators.compose([])],
    date: ['', Validators.compose([])],
    amount: ['', Validators.compose([])],
    reconciled: [false],
    reconciledDate: [''],
    reconciledAmount: [''],
    reconciledRef: [''],
    notes: [''],
    cancellationReason: [''],
  });

  cancellationForm: UntypedFormGroup = new UntypedFormGroup({
    reason: new UntypedFormControl(''),
  });

  @ViewChild('printContent') printContent!: ElementRef;
  @Output() onCancel = new EventEmitter();
  invoice: any;
  isInvoiceSaved = false;
  projectId: any;
  generateInvoice: boolean = false;
  paymentUrl: string = '';
  pageFrom: any = '';
  formSubmitted = false;
  InvoiceStatus: string = '';
  userRoles: any = [];
  UserRole = UserRole;
  cancelInvoiceClicked: boolean=false;
  enableSave:boolean=false;
  constructor(
    private fb: UntypedFormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private projectInvoiceService: ProjectInvoiceService,
    private projectSrv: ProjectService,
    private authService: AuthService,
    private commonService: CommonService,
    public utils: Utils,
    private errorService : ErrorService
  ) {
    this.pageFrom = this.route.snapshot.queryParamMap.get('page');
  }

  ngOnInit(): void {
    this.route.params.subscribe((params:any)=>{
      this.projectId = params.projectId;
    })
    this.getCurrentUserRole();
    this.selectionForm.get('paymentReceived')?.valueChanges.subscribe(() => {
      this.onPaymentReceivedChange();
    });
    setTimeout(() => {
      this.onPaymentReceivedChange();
    });
    if (this.invoiceId) {
      this.getInvoiceDetails();
    }
    console.log("this.feesAndInvoiceOptions", this.feesAndInvoiceOptions)
    //diables browswers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
      this.cancelInvoiceClicked=false;
      this.enableSave =false;
  }

  ngOnChanges() {
    this.projectInvoiceList = this.feesAndInvoiceOptions.TW;
    this.freshWaterprojectInvoiceList = this.feesAndInvoiceOptions.FW;
    this.lngfprojectInvoiceList = this.feesAndInvoiceOptions.LG;
    this.updateSelectionFormValidators();
  }
  getCurrentUserRole() {
    let userInfo = this.authService.getUserInfo();
    this.commonService
      .getUsersRoleAndPermissions(userInfo.ppid)
      .then((response) => {
        this.userRoles = response.roles;
        //this.userRoles =[UserRole.DEC_Program_Staff]
      }, 
      (error: any) =>{
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;  
      });
  }
  updateSelectionFormValidators() {
    this.selectionForm?.get('freshwater')?.clearValidators();
    this.selectionForm?.get('lngf')?.clearValidators();
    this.selectionForm?.get('tidal')?.clearValidators();
    if (this.freshWaterprojectInvoiceList?.length) {
      this.selectionForm?.get('freshwater')?.setValidators([Validators.required]);
    }
    if (this.lngfprojectInvoiceList?.length) {
      this.selectionForm?.get('lngf')?.setValidators([Validators.required]);
    }
    if (this.projectInvoiceList?.length) {
      this.selectionForm?.get('tidal')?.setValidators([Validators.required]);
    }

    this.selectionForm?.get('freshwater')?.updateValueAndValidity();
    this.selectionForm?.get('lngf')?.updateValueAndValidity();
    this.selectionForm?.get('tidal')?.updateValueAndValidity();
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }

  onPaymentReceivedChange() {
    const paymentReceived = this.selectionForm.get('paymentReceived')?.value;
    this.selectionForm.get('checkNo')?.setValue('');
    this.selectionForm.get('checkNo')?.clearValidators();
    this.selectionForm.get('amount')?.setValue('');
    this.selectionForm.get('amount')?.clearValidators();
    this.selectionForm.get('date')?.setValue('');
    this.selectionForm.get('date')?.clearValidators();
    this.selectionForm.get('checkNo')?.disable();
    this.selectionForm.get('amount')?.disable();
    this.selectionForm.get('date')?.disable();

    if (paymentReceived) {
      this.selectionForm
        .get('checkNo')
        ?.setValidators(
          Validators.compose([
            Validators.required,
            Validators.maxLength(10),
            Validators.pattern('[A-Za-z0-9]*'),
          ])
        );
      this.selectionForm
        .get('amount')
        ?.setValidators(
          Validators.compose([
            Validators.required,
            Validators.pattern('[0-9.]*'),
          ])
        );
      this.selectionForm
        .get('date')
        ?.setValidators(Validators.compose([Validators.required]));
      this.selectionForm.get('checkNo')?.enable();
      this.selectionForm.get('amount')?.enable();
      this.selectionForm.get('date')?.enable();
    }
    this.selectionForm.get('checkNo')?.updateValueAndValidity();
    this.selectionForm.get('amount')?.updateValueAndValidity();
    this.selectionForm.get('date')?.updateValueAndValidity();
  }

  // onGenerateInvoice(){
  //   this.generateInvoice = true;
  // }
  onCancelInvoice() {
    this.cancelInvoiceClicked=true;
    if (this.selectionForm.controls.cancellationReason.value &&
      this.selectionForm.controls.cancellationReason.value.length > 0 
      && !this.showSameDateCancelError) {
    const requestPayload = {
      notes: this.selectionForm.get('notes')?.value,
      reason: this.selectionForm.get('cancellationReason')?.value,
      cancelledUserName: this.userFullName,
      invoiceNum: this.invoiceId
    };
    this.projectInvoiceService.onCancelInvoice(this.projectId, requestPayload).subscribe((response) => {
      this.onCancel.emit(true);
    }, 
    (error: any) =>{
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;  
    });
  }}

  getInvoiceDetails() {
     this.utils.emitLoadingEmitter(true);
    this.projectInvoiceService
      .getInvoiceDetails(this.projectId, this.invoiceId)
      .subscribe((res) => {
        this.dueAmount=res.dueAmount;
        const formValues = {
          lngf: '',
          freshwater: '',
          tidal: '',
          paymentReceived: res.checkNumber?true:false,
          checkNo: get(res, 'checkNumber', ''),
          date: get(res, 'checkRcvdDate', ''),
          amount: get(res, 'checkAmt', ''),
          // dueAmount: get(res, this.amountDue, ''),
          reconciled: true,
          reconciledDate: get(res, 'invoiceDate', ''),
          reconciledAmount: get(res, 'checkAmt', ''),
          reconciledRef: get(res, 'payReference', ''),
          notes: get(res, 'notes', ''),
          cancellationReason: get(res, 'reason', '')
        };
        this.InvoiceStatus = get(res, 'status', '');
        if(this.InvoiceStatus==='Cancelled') {
          this.selectionForm.controls.cancellationReason.disable();
        }
        res.types.forEach((type: any) => {
          if (
            this.projectInvoiceList.find((item: any) =>
              isEqual(item.invoiceFeeType, type.type)
            )
          ) {
            formValues.tidal = type.type;
          }
          if (
            this.freshWaterprojectInvoiceList.find((item: any) =>
              isEqual(item.invoiceFeeType, type.type)
            )
          ) {
            formValues.freshwater = type.type;
          }
          if (
            this.lngfprojectInvoiceList.find((item: any) =>
              isEqual(item.invoiceFeeType, type.type)
            )
          ) {
            formValues.lngf = type.type;
          }
        });
        if (formValues.date) {
          formValues.date = moment(formValues.date, 'MM-DD-YYYY').format(
            'YYYY-MM-DD'
          );
        }
        if (formValues.reconciledDate) {
          formValues.reconciledDate = moment(
            formValues.reconciledDate,
            'MM-DD-YYYY'
          ).format('YYYY-MM-DD');
        }
        console.clear();
        this.showSameDateCancelError = (new Date(get(res, 'invoiceDate', ''))).toDateString() === (new Date()).toDateString();
        console.log(res);
        console.log(formValues);
        this.selectionForm.patchValue(formValues);
         this.utils.emitLoadingEmitter(false);
      }, 
      (error: any) =>{
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;  
      });
  }
  verifyPaymentReceivedCheckbox() {
    const paymentReceived = this.selectionForm?.get('paymentReceived')?.value;
    console.log(paymentReceived)
  }
  onGenerateInvoice() {
    this.formSubmitted = true;
    // if(!this.isSelectionFormCheckDateIsValid){
    //   return true;
    // }
    if (this.selectionForm.invalid) {
      return true;
    }

    const payloadTypes = [
      {
        type: this.selectedObject.tidal?.invoiceFeeType,
        fee: this.selectedObject.tidal?.invoiceFee,
      },
      {
        type: this.selectedObject.freshwater?.invoiceFeeType,
        fee: this.selectedObject.freshwater?.invoiceFee,
      },
      {
        type: this.selectedObject.lngf?.invoiceFeeType,
        fee: this.selectedObject.lngf?.invoiceFee,
      },
    ]
    const requestPayload = {
      types: payloadTypes.filter((item: any) => !isEmpty(item?.type)),
      totalCharge: this.amountDue,
      checkNumber: this.selectionForm.get('checkNo')?.value,
      checkRcvdDate: this.selectionForm.get('date')?.value.replace(/-/gi, '/'),
      checkAmt: Number(this.selectionForm.get('amount')?.value),
      decId: '',
      notes: this.selectionForm.get('notes')?.value,
    };
    if (requestPayload?.checkRcvdDate) {
      requestPayload.checkRcvdDate = moment(requestPayload?.checkRcvdDate, 'YYYY/MM/DD').format('MM/DD/YYYY');
    }
    console.clear();
    console.log("requestPayload", requestPayload);

    this.utils.emitLoadingEmitter(true);
    this.projectSrv.getFacilityDetails(this.projectId).then((res) => {
      // set(requestPayload, 'decId', get(res, 'decId', ''));
      var response = this.projectInvoiceService
        .submitProjectInfo(this.projectId, requestPayload)
        .subscribe(
          (response) => {
            this.utils.emitLoadingEmitter(false);
            this.isInvoiceSaved = true;
            this.invoice = response;
            this.onCancel.emit(true);
          }, 
          (error: any) =>{
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;  
          }
        );
    }, 
    (error: any) =>{
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;  
    });


  }

  makePayment() {
    this.projectInvoiceService
      .getTransactionId(this.projectId, this.invoice.invoiceNum)
      .subscribe(
        (response) => {
          window.open(`${environment.paymentUrl}` + response, '_blank');
        }, 
        (error: any) =>{
        this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;  
        }
      );
  }

  onCancelClicked(event: any) {
    if (this.selectionForm?.dirty) {
      this.congirmationModal.open();
      return;
    }
    this.onConfirmExit(event);
  }

  onConfirmExit(event: any) {
    this.onCancel.emit(null);
    if (this.pageFrom == 'virtual-workspace') {
      this.router.navigate(['/virtual-workspace']);
    }
  }
  onSaveClick() {

    const requestPayload = {
      notes: this.selectionForm.get('notes')?.value,
      invoiceNum: this.invoiceId
    };
    this.projectInvoiceService.onSaveNotes(this.projectId, requestPayload).subscribe((response) => {
      this.onCancel.emit(true);
    }, 
    (error: any) =>{
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;  
    });
  }
  noteschange(){
    this.enableSave =true;
  }
  openinvoiceDocument(id:string){
    this.projectSrv.generateInvoiceDocument(this.projectId, id).then((response)=>{
      var link = document.createElement('a');
      link.href = window.URL.createObjectURL(response);
      window.open(link.href);
    }, 
    (error: any) =>{
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;  
    })
  }

  // onPrint()
  // {
  //   let myWindow:any =window.open('','', 'width=400px,height:400px');
  //   myWindow.document.write(this.printContent.nativeElement.innerHTML);
  //   myWindow.document.close(); //missing code
  //   myWindow.focus();
  //   myWindow.print();
  // }    
}

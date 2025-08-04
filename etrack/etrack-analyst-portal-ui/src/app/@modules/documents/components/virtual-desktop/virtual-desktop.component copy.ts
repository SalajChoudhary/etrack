import { DatePipe } from '@angular/common';
import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnInit,
  ViewChild,
} from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
  FormControl,
} from '@angular/forms';
import { NgbModal, NgbModalRef, NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import { BehaviorSubject, fromEvent, Subject, Subscription } from 'rxjs';
import { whiteSpaceValidator } from 'src/app/@shared/applicationInformation.validator';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { VirtualDesktopService } from 'src/app/@shared/services/virtual-desktop.service';
import { AuthService } from 'src/app/core/auth/auth.service';
import { ProjectNotesComponent } from '../project-notes/project-notes.component';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { takeUntil, timeout } from 'rxjs/operators';
import { get, set, isEmpty, cloneDeep, isEqual } from 'lodash';
import moment from 'moment';
import { Utils } from 'src/app/@shared/services/utils';
import { PendingChangesPopupComponent } from '../../../../@shared/components/pending-changes-popup/pending-changes-popup.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { VirtualDesktopLitigationHoldConfirmModalComponent } from 'src/app/@modules/documents/components/virtual-desktop/sideblock/litigation-hold/virtual-desktop-litigation-hold-confirm-modal/virtual-desktop-litigation-hold-confirm-modal.component';


@Component({
  selector: 'app-virtual-desktop',
  templateUrl: './virtual-desktop.component.html',
  styleUrls: ['./virtual-desktop.component.scss'],
})
export class VirtualDesktopComponent implements OnInit {
  // @ViewChild('addNotePop', { static: true }) addNotePop!: ElementRef;
  @ViewChild('litPendingPopup', { static: true })
  litPendingPopup!: PendingChangesPopupComponent;
  @ViewChild('pendingPopup', { static: true })
  pendingPopup!: PendingChangesPopupComponent;
  @ViewChild(ProjectNotesComponent)
  projectNoteComponent!: ProjectNotesComponent;
  @ViewChild('notesModal')
  notesModal!: CustomModalPopupComponent;
  @ViewChild('systemGenNotesModal')
  systemGenNotesModal!: CustomModalPopupComponent;
  modalReference!: NgbModalRef;
  configObject: any;
  UserRole = UserRole;
  userRoles: any = [];
  submitted: boolean = false;
  errorMsgObj: any = {};
  notesConfig: { title: string; showHeader: boolean; showClose?: boolean };
  note: string = '';
  comments: string = '';
  currentDate: any = '';
  notesFormGroup!: UntypedFormGroup;
  isOpenNotePopUp = new BehaviorSubject<boolean>(false);
  isOpenSystemGenNotePopUp = new BehaviorSubject<boolean>(false);
  isEdit: boolean = false;
  noteDetails: any = {};
  maxNote: any = 500;
  maxComment: any = 300;
  projectId: any = '';
  dimsr: boolean = false;
  facilityName!: string;
  virtualDesktopData: any = {
    application: [],
  };
  private unsubscriber: Subject<void> = new Subject<void>();
  currentNoteId: any;
  // isReadOnly: boolean = true;
  feesAndInvoiceOptions: any = undefined;
  showServerError = false;
  serverErrorMessage!: string;
  isFromDisposed = false;
  isFromSuspended = false;
  projectNotes!: any;
  showSaveWasSuccessful = false;
  isFoil = false;

  foilRequestForm = this.fb.group({
    foilRequest: ['', Validators.compose([Validators.requiredTrue])],
    foilRequestNumber: ['', Validators.compose([]), []],
  });
  litigationForm = this.fb.group({
    litigationHoldCheckBox: [''],
    startDate: ['', Validators.compose([Validators.required])],
    endDate: ['', Validators.compose([])],
  });
  foilRequestRowsUntouched: any[] = [];
  foilRequestRowsModified: any[] = [];
  saveClicked: boolean = false;
  subs = new Subscription();
  isFoilChecked: any = false;
  isLitigationChecked: any = false;
  get userIsProgramReviewer() {
    return this.userRoles.includes(UserRole.DEC_Program_Staff);
  }

  get isReadOnly() {
    return (
      this.isFromDisposed || this.isFromSuspended || this.userIsProgramReviewer
    );
  }
  get canShowLitigationBanner() {
    const litigationRequestHoldInd = get(this.virtualDesktopData, 'litigationRequest.holdInd', 'N');
    if(litigationRequestHoldInd != 'Y'){
      return false;
    }
    // const checkbox = this.litigationForm?.get('litigationHoldCheckBox')?.value;
    // if (!checkbox) {
    //   return false;
    // }
    // const startDate = this.litigationForm?.get('startDate')?.value;
    // if (!startDate) {
    //   return false;
    // }
    // const startDateMomented = moment(startDate, 'YYYY-MM-DD').startOf('day');
    // const endDate = this.litigationForm?.get('endDate')?.value;
    // const endDateMomented = endDate
    //   ? moment(endDate, 'YYYY-MM-DD').startOf('day')
    //   : null;
    // const today = moment().startOf('day');
    
    // if (startDateMomented.isSameOrAfter(today)) {
    //   return false;
    // }
    // if (endDateMomented && endDateMomented.isBefore(today)) {
    //   return false;
    // }

    return true;
  }
  constructor(
    private commonService: CommonService,
    private virtualDesktopService: VirtualDesktopService,
    private formBuilder: UntypedFormBuilder,
    private datePipe: DatePipe,
    private authService: AuthService,
    private utils: Utils,
    private cdr: ChangeDetectorRef,
    private fb: UntypedFormBuilder,
    private router: Router,
    private errorService: ErrorService,
    private route: ActivatedRoute,
    private modalService: NgbModal,

  ) {
    this.notesConfig = {
      title: 'New York State Department of Environmental Conservation',
      showHeader: true,
      // showClose:true,
    };
  }

  ngOnInit(): void {
    this.commonService.removeGreenBackground();
    this.commonService.emitLitCheckChanged.subscribe((val) => {
      this.saveClicked = val;
    });
    // let param = this.route.snapshot.paramMap.get('from');
    // if (!isEmpty(param)) {
    //   this.isFromDisposed = true;
    // }
    this.route.queryParams.subscribe((val: any) => {
      const from = get(val, 'from', null);
      this.isFromDisposed = isEqual(from, 'disposed');
      this.isFromSuspended = isEqual(from, 'suspended');
    });
    this.route.params.subscribe((params:any)=>{
      this.projectId = params.projectId;
    })
    // this.projectId = localStorage.getItem('projectId');TODO: delete
    this.getConfig();
    this.getAllErrorMsgs();
    this.getCurrentUserRole();
    this.currentDate = this.datePipe.transform(new Date(), 'yyyy-MM-dd');
    this.getVirtualDesktopData();
    this.virtualDesktopService.getShowReviewerSub().subscribe((data: any) => {
      if ((data = 'show')) {
        this.ngOnInit();
      }
    });
    //diables browswers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
    this.dimsr =
      sessionStorage.getItem('navigatedFrom') === 'dimsr' ? true : false;
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
    this.subs.unsubscribe();
  }
  onFoilChange(e: any) {
    this.isLitigationChecked = true;
  }
  onLitigationChange(e: any) {
    // console.log(e)
    this.isLitigationChecked = true;
  }
  getVirtualDesktopData() {
    this.utils.emitLoadingEmitter(true);
    this.virtualDesktopService
      .getVirtualDesktopData(this.projectId)
      .pipe(timeout(10000))
      .subscribe(
        (res: any) => {
          const processed = this.prepareFeesAndInvoiceData(res);
          this.virtualDesktopData = processed;
          this.facilityName = res.facility.facilityName;
          this.projectNotes = res.notes;
          this.isFoilChecked = isEqual(
            get(this.virtualDesktopData, 'foilReqInd', 'N'),
            'Y'
          );
          this.presetFoilRequestRows();
          this.presetLitigationFormData();
          set(this.virtualDesktopData, 'dataFetchedFromBackend', true); // custom identifier used to confirm the data is filled from the backend
          //this.isReadOnly = get(res, 'readOnly', false);
          // this.isReadOnly = this.isFromDisposed;
          this.utils.emitLoadingEmitter(false);
          this.virtualDesktopService.vdsData = res; 

        },
        (error: any) => {
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          this.utils.emitLoadingEmitter(false);
          throw error;
        }
      );
  }
  reloadData(event: any) {
    if (event) {
      this.getVirtualDesktopData();
    }
  }
  prepareFeesAndInvoiceData(res: any) {
    res.invoice?.forEach((item: any) => {
      let valueOfInvoiceDate: any = null;
      if (!isEmpty(item.invoiceDate)) {
        valueOfInvoiceDate = moment(item?.invoiceDate, 'MM/DD/YYYY').valueOf();
      }
      set(item, 'valueOfInvoiceDate', valueOfInvoiceDate);
    });
    res.invoice.sort((a: any, b: any) => {
      return a.valueOfInvoiceDate > b.valueOfInvoiceDate ? -1 : 1;
    });
    return res;
  }
  getCurrentUserRole() {
    this.subs.add(
      this.authService.emitAuthInfo.subscribe((authInfo: any) => {
        if (authInfo && !authInfo.isError) {
          this.userRoles = authInfo.roles;
           //this.userRoles = ['DEC Program Staff']; // only for testing purpose. To be commented on production
        } else if (authInfo && authInfo.isError) {
          this.serverErrorMessage = this.errorService.getServerMessage(
            authInfo.error
          );
          this.showServerError = true;
          throw authInfo.error;
        }
      })
    );
  }
  onFeesAndInvoiceLoad(feesAndInvoiceData: any) {
    this.feesAndInvoiceOptions = feesAndInvoiceData;
  }

  get canShowAddNewButton() {
    if (this.feesAndInvoiceOptions === undefined) return true;
    return (
      !isEmpty(this.feesAndInvoiceOptions?.TW) ||
      !isEmpty(this.feesAndInvoiceOptions?.LG) ||
      !isEmpty(this.feesAndInvoiceOptions?.FW) ||
      this.virtualDesktopData.invoiceReq == 'Y'
    );
  }

  onCommentInputChange(event: string) {
    this.comments = event;
    this.notesFormGroup.patchValue({ comments: event });
    this.notesFormGroup.updateValueAndValidity();
  }
  onInputChange(event: string) {
    this.note = event;
    this.notesFormGroup.patchValue({ actionNote: event });
    this.notesFormGroup.updateValueAndValidity();
  }
  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val) => {
      if (val) this.errorMsgObj = this.commonService.getErrorMsgsObj();
    });
  }
  initiateForm() {
    this.notesFormGroup = this.formBuilder.group({
      actionDate: [this.currentDate, Validators.required],
      actionType: ['', [Validators.required, whiteSpaceValidator]],
      actionNote: ['', Validators.required],
      comments: [''],
    });
  }
  getDisplayType(id: any) {
    let i = this.configObject?.actionTypes?.findIndex(
      (x: any) => x?.actionTypeCode == id
    );
    if (i >= 0) {
      return this.configObject?.actionTypes[i]?.actionTypeDesc;
    } else {
      return '';
    }
  }
  onFormSubmit() {
    this.submitted = true;
    if (this.notesFormGroup.valid) {
      let formData = this.notesFormGroup.value;
      let apiData: any = this.noteDetails;
      apiData.actionDate = this.datePipe.transform(
        formData.actionDate,
        'MM/dd/yyyy'
      );
      let currentTime = this.datePipe.transform(new Date(), 'shortTime');
      apiData.actionNote = formData.actionNote?.trim();
      apiData.comments = formData.comments ? formData.comments?.trim() : '';
      if (this.isEdit) {
        apiData.projectNoteId = this.noteDetails?.projectNoteId;
        let pastActionDate = this.datePipe.transform(
          this.noteDetails?.actionDate,
          'MM/dd/yyyy'
        );
        if (formData?.actionDate === pastActionDate) {
          apiData.actionDate = this.noteDetails.actionDate;
        } else {
          apiData.actionDate += ' ' + currentTime;
        }
        this.virtualDesktopService.updateNote(apiData, this.projectId).subscribe(
          (response) => {
            this.closeModal('program');
            this.noteDetails = {};
            this.getVirtualDesktopData();
          },
          (error: any) => {
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          }
        );
      } else {
        formData.actionDate = this.datePipe.transform(
          formData.actionDate,
          'MM/dd/yyyy'
        );

        this.virtualDesktopService.addNote(formData, this.projectId).subscribe(
          (response) => {
            this.closeModal('program');
            this.getVirtualDesktopData();
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

  setFormValues(formValues: any) {
    this.notesFormGroup.controls.actionDate.setValue(
      this.datePipe.transform(formValues?.actionDate, 'yyyy-MM-dd')
    );
    this.notesFormGroup.controls.actionType.setValue(formValues.actionType);
    this.notesFormGroup.controls.actionNote.setValue(
      formValues.actionNote ? formValues.actionNote : ''
    );
    this.notesFormGroup.controls.comments.setValue(
      formValues.comments ? formValues.comments : ''
    );
    this.notesFormGroup.updateValueAndValidity();
    this.maxComment = 301;
    this.maxNote = 501;
    this.cdr.detectChanges();
    this.maxNote = 500;
    this.maxComment = 300;
  }
  getNoteDetails(noteId: number) {
    this.virtualDesktopService.getNoteDetailsById(noteId, this.projectId).then(
      (response) => {
        this.noteDetails = response;
        this.setFormValues(response);
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }
  onEditClicked(e: any) {
    this.openAddNotePop(e);
  }
  openAddNotePop(item?: any) {
    this.initiateForm();
    if (item?.projectNoteId) {
      this.currentNoteId = item.projectNoteId;
      item.systemGenerated !== 'Y'
        ? this.getNoteDetails(item.projectNoteId)
        : null;
      this.isEdit = true;
    } else {
      this.isEdit = false;
    }
    this.note = '';
    this.comments = '';
    if (item?.systemGenerated === 'Y') {
      this.systemGenNotesModal.open('lg');
      this.isOpenSystemGenNotePopUp.next(true);
    } else {
      this.notesModal.open('lg');
      this.isOpenNotePopUp.next(true);
    }
  }

  onCloseClicked() {
    if ((this.litigationForm.dirty || this.foilRequestForm.dirty) && !this.showSaveWasSuccessful) {
      this.litPendingPopup.open();
    } else {
      this.router.navigateByUrl('/dashboard');
    }
  }

  goToDash() {
    this.router.navigateByUrl('/dashboard');
  }
  closeModal(flag?: string) {
    if (flag === 'manual' && this.notesFormGroup.dirty) {
      this.pendingPopup.open();
    } else {
      this.notesModal.close();
      this.isOpenNotePopUp.next(false);
      this.submitted = false;
      this.isEdit = false;
    }
  }

  onClose(e: any) {
    this.systemGenNotesModal.close();
    this.isOpenSystemGenNotePopUp.next(false);
    if (e) {
      this.getVirtualDesktopData();
    }
  }

  getConfig() {
    this.commonService.getAllConfigurations().then(
      (response) => {
        if (response) {
          this.configObject = response;
        }
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  presetFoilRequestRows() {
    this.foilRequestRowsUntouched =
      this.virtualDesktopData?.foilRequestNumber.map((item: any) => {
        return {
          foil: item,
        };
      }) || [];
    this.foilRequestRowsModified = cloneDeep(this.foilRequestRowsUntouched);
    if (this.userIsProgramReviewer) {
      this.foilRequestForm.get('foilRequestNumber')?.disable();
      this.foilRequestForm.get('foilRequest')?.setValue(true);
      this.foilRequestForm.get('foilRequest')?.disable();
    }
  }

  presetLitigationFormData() {
    // const holdInd = get( // commented out infavor of litigation hold banner used for holdInd
    //   this.virtualDesktopData,
    //   'litigationRequest.holdInd',
    //   'N'
    // );
    // const holdIndAsBoolean = isEqual(holdInd, 'Y');
    // this.isLitigationChecked=holdIndAsBoolean
    const litigationStartDate = get(this.virtualDesktopData, 'litigationRequest.litigationStartDate', null);

    setTimeout(() => {
      this.litigationForm
        .get('litigationHoldCheckBox')
        ?.setValue(!isEmpty(litigationStartDate), { emitEvent: false });
    });
    if (this.userIsProgramReviewer) {
      this.litigationForm.get('litigationHoldCheckBox')?.disable();
      this.litigationForm.get('startDate')?.disable();
      this.litigationForm.get('endDate')?.disable();
    }
    const startDate = get(
      this.virtualDesktopData,
      'litigationRequest.litigationStartDate',
      null
    );
    const endDate = get(
      this.virtualDesktopData,
      'litigationRequest.litigationEndDate',
      null
    );

    if (startDate) {
      this.litigationForm
        .get('startDate')
        ?.setValue(moment(startDate, 'MM/DD/YYYY').format('YYYY-MM-DD'));
    }
    if (endDate) {
      this.litigationForm
        .get('endDate')
        ?.setValue(moment(endDate, 'MM/DD/YYYY').format('YYYY-MM-DD'));
    }
  }
  onFoilRequestModified(modifiedRows: any) {
    this.foilRequestRowsModified = modifiedRows;
    this.saveFoil();
  }
  onSaveFoilAndLitigation() {
    
    this.showSaveWasSuccessful = false;
    this.saveClicked = true;
    if(this.litigationForm.invalid){
      console.error("Litigation form is invalid");
      return;
    }
    const litigationStartDate = get(this.virtualDesktopData, 'litigationRequest.litigationStartDate', null);
    // const prevHoldInd = get(this.virtualDesktopData, 'litigationRequest.holdInd', 'Y');
    const holdInd = this.litigationForm.get('litigationHoldCheckBox')?.value;

    if(litigationStartDate && !holdInd){
      this.openLigitationHoldConfirmModal();
      return;
      }
   
   this.onLitigationConfirmProceed();
  }

  saveLitigation(){
    let litigationRequest = this.litigationRequestPayload();
    this.virtualDesktopService.saveLitigation(litigationRequest, this.projectId).subscribe({
      next: (res: any) => {
        //alert("saved successfully")
        this.showSaveWasSuccessful = true;
        if(litigationRequest.holdInd != 'Y'){
          this.litigationForm.get('startDate')?.setValue(null);
          this.litigationForm.get('endDate')?.setValue(null);
        }
        
        this.getVirtualDesktopData();
      },
      error: (err: any) => {},
    });
  }

  saveFoil(){
    const payload = {
      foilRequest: {
        foilReqInd: 'Y',
        modified: this.isFoilRequestModified() ? 'Y' : 'N',
        foilRequestNumber: this.foilRequestRowsModified.map(
          (item) => item.foil
        ),
      }
    }; 
    this.virtualDesktopService.saveFoil(payload.foilRequest, this.projectId).subscribe({
      next: (res: any) => {
        //alert("saved successfully")
        this.showSaveWasSuccessful = true;       
        this.getVirtualDesktopData();
      },
      error: (err: any) => {},
    });

  }

  onLitigationConfirmProceed(){
    
    const payload = {
      foilRequest: {
        foilReqInd: 'Y',
        modified: this.isFoilRequestModified() ? 'Y' : 'N',
        foilRequestNumber: this.foilRequestRowsModified.map(
          (item) => item.foil
        ),
      },
      litigationRequest: this.litigationRequestPayload(),
    };
    this.virtualDesktopService.saveFoilAndLitigation(payload, this.projectId).subscribe({
      next: (res: any) => {
        //alert("saved successfully")
        this.showSaveWasSuccessful = true;
        if(payload.litigationRequest.holdInd != 'Y'){
          this.litigationForm.get('startDate')?.setValue(null);
          this.litigationForm.get('endDate')?.setValue(null);
        }
        
        this.getVirtualDesktopData();
      },
      error: (err: any) => {},
    });
  }
  
  openLigitationHoldConfirmModal() { // moved to virtual-desktop.component.ts
    const options: NgbModalOptions = {
      centered: true,
      size: 'litigation-hold-confirm',
    };
    const modalRef = this.modalService.open(
      VirtualDesktopLitigationHoldConfirmModalComponent,
      options
    );
    modalRef.result.then(
      (result) => { // on ok click
        this.onLitigationConfirmProceed();
      },
      (reason) => { // on cancel click
          this.litigationForm.get('litigationHoldCheckBox')?.setValue(true);
       
      }
    );
}
  litigationRequestPayload() {
    const litigationFormValue = this.litigationForm?.value;
    const holdInd = get(litigationFormValue, 'litigationHoldCheckBox', false);
    let payload = {
      litigationStartDate: null,
      litigationEndDate: null,
      holdInd: holdInd ? 'Y' : 'N',
      modified: 'N',
    };
    if (!holdInd) {
      payload.modified = this.isLitigationRequestModified(payload) ? 'Y' : 'N';
      return payload;
    }
    const litigationStartDate = litigationFormValue.startDate
      ? moment(litigationFormValue.startDate, 'YYYY-MM-DD').format('MM/DD/YYYY')
      : null;
    const litigationEndDate = litigationFormValue.endDate
      ? moment(litigationFormValue.endDate, 'YYYY-MM-DD').format('MM/DD/YYYY')
      : null;
    set(payload, 'litigationStartDate', litigationStartDate);
    set(payload, 'litigationEndDate', litigationEndDate);
    payload.modified = this.isLitigationRequestModified(payload) ? 'Y' : 'N';
    return payload;
  }
  isLitigationRequestModified(payload: any) {
    const originalHoldIndicator = get(
      this.virtualDesktopData,
      'litigationRequest.holdInd',
      'N'
    );
    if (payload.holdInd != originalHoldIndicator) {
      return true;
    }

    const startDate = get(
      this.virtualDesktopData,
      'litigationRequest.litigationStartDate',
      null
    );
    const endDate = get(
      this.virtualDesktopData,
      'litigationRequest.litigationEndDate',
      null
    );

    if (
      payload.litigationStartDate != startDate ||
      payload.litigationEndDate != endDate
    ) {
      return true;
    }
    return false;
  }
  isFoilRequestModified() {
    let modified = false;
    if (
      this.foilRequestRowsModified.length !=
      this.foilRequestRowsUntouched.length
    ) {
      return true;
    }
    this.foilRequestRowsUntouched.forEach((item: any, index: any) => {
      if (item.foil != this.foilRequestRowsModified[index].foil) {
        modified = true;
      }
    });
    return modified;
  }
}

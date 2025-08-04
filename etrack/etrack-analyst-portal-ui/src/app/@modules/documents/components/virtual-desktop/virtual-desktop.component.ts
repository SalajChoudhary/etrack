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
import { get, set, isEmpty, cloneDeep, isEqual, concat } from 'lodash';
import moment from 'moment';
import { Utils } from 'src/app/@shared/services/utils';
import { PendingChangesPopupComponent } from '../../../../@shared/components/pending-changes-popup/pending-changes-popup.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { VirtualDesktopLitigationHoldConfirmModalComponent } from 'src/app/@modules/documents/components/virtual-desktop/sideblock/litigation-hold/virtual-desktop-litigation-hold-confirm-modal/virtual-desktop-litigation-hold-confirm-modal.component';
import { ModalConfig } from 'src/app/modal.config';
import { LitigationHoldComponent } from './sideblock/litigation-hold/virtual-desktop-litigation-hold/litigation-hold.component';
import { KeywordMaintainService } from 'src/app/@shared/services/keyword-maintain.service';
import { InquiryService } from 'src/app/@shared/services/inquiryService';


@Component({
  selector: 'app-virtual-desktop',
  templateUrl: './virtual-desktop.component.html',
  styleUrls: ['./virtual-desktop.component.scss'],
})
export class VirtualDesktopComponent implements OnInit {
  // @ViewChild('addNotePop', { static: true }) addNotePop!: ElementRef;
  @ViewChild('confirmationPopup', { static: true })
  confirmationPopup!: CustomModalPopupComponent;
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
  giNoteTypes: any;
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
  isGi: boolean = false;
  noteDetails: any = {};
  maxNote: any = 500;
  maxComment: any = 300;
  projectId: any = '';
  inquiryId: any = '';
  dimsr: boolean = false;
  facilityName!: string;
  virtualDesktopData: any = {
    application: [],
  };
  private unsubscriber: Subject<void> = new Subject<void>();
  currentNoteId: any;
  feesAndInvoiceOptions: any = undefined;
  showServerError = false;
  serverErrorMessage!: string;
  isFromDisposed = false;
  isFromSuspended = false;
  projectNotes!: any;
  showSaveWasSuccessful = false;
  isFoil = false;
  canShowLitigationBanner:boolean=false;
  requestIdentifier: any;
  reviewsAreComplete: boolean = true;

  foilRequestForm = this.fb.group({
    foilRequestNumber: ['', Validators.compose([]), []],
  });
  litigationForm = this.fb.group({
    startDate: ['', Validators.compose([Validators.required])], // TODO: Validatiors.required is removed. confirme
    endDate: ['', Validators.compose([])],
  });
  foilRequestRowsUntouched: any[] = [];
  foilRequestRowsModified: any[] = [];
  saveClicked: boolean = false;
  subs = new Subscription();
  isFoilChecked: any = false;
  isLitigationChecked: any = false;
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
  isNoContent: boolean = false;



  get userIsProgramReviewer() {
    return this.userRoles.includes(UserRole.DEC_Program_Staff);
  }

  get isReadOnly() {
    return (
      this.isFromDisposed || this.isFromSuspended || this.userIsProgramReviewer
    );
  }
  get isDocumentReadOnly() {
    return (
      this.isFromDisposed || this.isFromSuspended || this.userIsProgramReviewer
    );
  }
  // get canShowLitigationBanner() {
  //   const litigationRequestHoldInd = get(this.virtualDesktopData, 'litigationRequest.holdInd', 'N');
  //   if(litigationRequestHoldInd != 'Y'){
  //     return false;
  //   }
  //   return true;
  // }
  categoryList: any[] = [];
  otherKeywordList: any[] = [];
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
    private keywordMaintainanceService: KeywordMaintainService,
    private inquiryService: InquiryService

  ) {
    this.notesConfig = {
      title: 'New York State Department of Environmental Conservation',
      showHeader: true,
      // showClose:true,
    };
  }

  ngOnInit(): void {
    console.log('in it','Query');
    console.log("Q PID", this.projectId )

    
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
      this.inquiryId = params.inquiryId;
      this.isGi = this.inquiryId ? true : false;
      if(!this.isGi) {
        this.loadKeywordData();
      }
    })
    //localStorage.setItem('projectId',this.projectId);
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

  loadKeywordData() {
    console.log("PID", this.projectId)
    this.virtualDesktopService
    .permitKeyWordData(this.projectId)
    .then((res: any) => {
      // res= this.keywordItems;
      console.log("Keyword Ites", res)
      let categories: any[] = [];
      Object.keys(res.permitKeyword).forEach((e: any) => {
        res.permitKeyword[e][0].categoryText = e;
        categories = categories.concat(res.permitKeyword[e]);
      });

      categories.sort((a: any, b: any) => {
        if(a.keywordCategory.toUpperCase() > b.keywordCategory.toUpperCase()) {
          return 1;
        }
        else if((a.keywordCategory === b.keywordCategory) && 
          a.keywordText.toUpperCase() > b.keywordText.toUpperCase()) {
            return 1;
        }
        return -1;
      });
      
      let systemList: any[] = [];
          Object.keys(res.systemDetectedKeyword).forEach((e: any) => {
            res.systemDetectedKeyword[e][0].categoryText = e;
            systemList = systemList.concat(res.systemDetectedKeyword[e]);
          });

      let otherList: any[] = [];
      Object.keys(res.candidateKeyword).forEach((e: any) => {
        res.candidateKeyword[e][0].categoryText = e;
        otherList = otherList.concat(res.candidateKeyword[e]);
      });
      let concatCatList= concat(categories, systemList)
      this.categoryList = concatCatList;
      this.otherKeywordList = otherList;

    })
  }

  updatedCategoryList(data: any) {
    console.log("Emit Data", data);
    let apiData = JSON.parse(JSON.stringify(data));
    delete(apiData.categoryText)
    this.keywordMaintainanceService.updateKeyword(apiData, data.projectSelected, this.projectId).then((response) => {
    },
    (error: any) => {
      this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;
    })
  }

  updatedOtherKeywordList(data: any) {
    console.log("Emit Data", data);
    let apiData = JSON.parse(JSON.stringify(data));
    delete(apiData.categoryText)
    this.keywordMaintainanceService.updateKeyword(apiData, data.projectSelected, this.projectId).then((response) => {
    },
    (error: any) => {
      this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;
    })
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
   
    if(!this.isGi) {
      this.utils.emitLoadingEmitter(true);
      this.virtualDesktopService
        .getVirtualDesktopData(this.projectId)
        .pipe(timeout(10000))
        .subscribe(
          (res: any) => {
            this.utils.emitLoadingEmitter(false);
            if (res.status == 204) {
              //this.utils.emitLoadingEmitter(false);
              this.isNoContent=true;
              this.virtualDesktopService.isNoContent.next(true);
            } else {
            this.isNoContent=false;
            const processed = this.prepareFeesAndInvoiceData(res.body);
            this.virtualDesktopData = processed;
            this.facilityName = res.body.facility.facilityName;
            this.projectNotes = res.body.notes;
            this.isFoilChecked = isEqual(
              get(this.virtualDesktopData, 'foilReqInd', 'N'),
              'Y'
            );
            this.presetFoilRequestRows();
            this.presetLitigationFormData();
            this.checkLitigationStartDate();
            set(this.virtualDesktopData, 'dataFetchedFromBackend', true); // custom identifier used to confirm the data is filled from the backend
            
            this.virtualDesktopService.vdsData = res.body; 
            this.canShowLitigationBanner =this.virtualDesktopData.litigationRequest?.holdInd === 'Y'? true : false;
            const assignedAnalyst = this.virtualDesktopData.assignedAnalystName ? this.virtualDesktopData.assignedAnalystName : '';
            this.virtualDesktopService.assignedAnalyst.next(assignedAnalyst);
            }
             },
          (error: any) => {
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            this.utils.emitLoadingEmitter(false);
            throw error;
          }
        );
    }
    else {
      this.utils.emitLoadingEmitter(true);
      this.virtualDesktopService.getGiVirtualDesktopData(this.inquiryId)
        .subscribe( (res: any) => {
          this.utils.emitLoadingEmitter(false);
          console.log(res);
          this.virtualDesktopData = res;
          const assignedAnalyst = this.virtualDesktopData.assignedAnalystName ? 
            this.virtualDesktopData.assignedAnalystName : '';
          this.virtualDesktopService.assignedAnalyst.next(assignedAnalyst);
          this.projectNotes = res.geographicalInquiryNotes;
          set(this.virtualDesktopData, 'dataFetchedFromBackend', true);
          this.reviewsAreComplete = true;
          this.virtualDesktopData.reviewDocuments.forEach((doc: any) => {
            if("N" === doc.docReviewedInd) {
              this.reviewsAreComplete = false;
            }
          });
          console.log(this.reviewsAreComplete);
          this.utils.emitLoadingEmitter(false);
        })
    }
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
           // this.userRoles = ['DEC Program Staff']; // only for testing purpose. To be commented on production
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
    if(this.isGi) {
      this.notesFormGroup.controls.actionType.clearValidators();
      this.notesFormGroup.controls.actionType.setValue("3");
      this.notesFormGroup.controls.actionType.updateValueAndValidity();
    }
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
  formatInquiryId(inqId: any) {
    return this.inquiryService.formatInquiryId(inqId);
  }
  onFormSubmit() {
    this.submitted = true;
    console.log(this.notesFormGroup.value);
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
        if(this.isGi) {
          apiData.actionTypeCode = this.giNoteTypes[0].actionTypeCode;
          apiData.inquiryId = this.inquiryId;
          apiData.inquiryNoteId = this.currentNoteId;
          delete apiData.updatedBy;
          delete apiData.updatedDate;
          delete apiData.actionTypeDesc;
          delete apiData.createDate;
          delete apiData.systemGenerated;
          console.log(this.currentNoteId);
          this.utils.emitLoadingEmitter(true);
          this.virtualDesktopService.updateGiNote(apiData, this.inquiryId).subscribe(
            (response) => {
              this.utils.emitLoadingEmitter(false);
              this.closeModal('program');
              this.noteDetails = {};
              this.getVirtualDesktopData();
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
          this.utils.emitLoadingEmitter(true);
          this.virtualDesktopService.updateNote(apiData, this.projectId).subscribe(
            (response) => {
              this.utils.emitLoadingEmitter(false);
              this.closeModal('program');
              this.noteDetails = {};
              this.getVirtualDesktopData();
            },
            (error: any) => {
              this.utils.emitLoadingEmitter(false);
              this.serverErrorMessage = this.errorService.getServerMessage(error);
              this.showServerError = true;
              throw error;
            }
          );
        }
      } else {
        formData.actionDate = this.datePipe.transform(
          formData.actionDate,
          'MM/dd/yyyy'
        );
        if(this.isGi) {
          
          apiData.actionTypeCode = this.giNoteTypes[0].actionTypeCode;
          apiData.inquiryId = this.inquiryId;
          this.utils.emitLoadingEmitter(true);
          this.virtualDesktopService.updateGiNote(apiData, this.inquiryId).subscribe(
            (response) => {
              this.utils.emitLoadingEmitter(false);
              this.closeModal('program');
              this.getVirtualDesktopData();
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
          this.utils.emitLoadingEmitter(true); 
          this.virtualDesktopService.addNote(formData, this.projectId).subscribe(
            (response) => {
              this.utils.emitLoadingEmitter(false);
              this.closeModal('program');
              this.getVirtualDesktopData();
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
    if(!this.isGi) {
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
    else {
      this.virtualDesktopService.getGiNoteDetailsById(noteId, this.inquiryId).then(
        (response) => {
          console.log(response);
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
  }
  onEditClicked(e: any) {
    this.openAddNotePop(e);
  }
  openAddNotePop(item?: any) {
    this.initiateForm();
    console.log(item);
    if (item?.projectNoteId || item?.inquiryNoteId) {
      this.currentNoteId = item.projectNoteId ? item.projectNoteId : item.inquiryNoteId;
      item.systemGenerated !== 'Y'
        ? this.getNoteDetails(this.currentNoteId)
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
    console.log('yeah');
    
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

    this.virtualDesktopService.getInquiryNoteConfig().then(
      (response) => {
        if (response) {
          this.giNoteTypes = response;
          console.log('In gi config');
          console.log(response);
        }
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    )
    
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
      // this.foilRequestForm.get('foilRequestNumber')?.disable();
      // this.foilRequestForm.get('foilRequest')?.setValue(true);
      // this.foilRequestForm.get('foilRequest')?.disable();
    }
  }

  presetLitigationFormData() {

    if (this.userIsProgramReviewer) {
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
  saveLitigation() {
    console.log('J here', this.litigationForm);
    this.litigationForm.get('startDate')?.enable();
    
    this.showSaveWasSuccessful = false;
    this.saveClicked = true;
    if(this.litigationForm.invalid){
      console.log('in VW, here');
      
      console.error("Litigation form is invalid");
      console.log(this.litigationForm.get('startDate')?.errors)
      console.log(this.litigationForm.get('endDate')?.errors)
      return;
    }
    // const litigationStartDate = this.litigationForm.get('startDate')?.value;
    const litigationStartDate = get(this.virtualDesktopData, 'litigationRequest.litigationStartDate', null);
    const prevHoldInd = get(this.virtualDesktopData, 'litigationRequest.holdInd', 'N');
    // console.log(litigationStartDate);
    // console.log(prevHoldInd)
    // if(!litigationStartDate && prevHoldInd == 'Y'){
    //   this.openLigitationHoldConfirmModal(); // TODO: remove function
    //   return;
    //   }
  
   this.saveLitigationConfirmed();
  }

  confirmOkClicked() {
    this.confirmationPopup.close();
    location.reload();
  }

  saveLitigationConfirmed(){
    let litigationRequest = this.litigationRequestPayload();
    this.utils.emitLoadingEmitter(true);
    this.virtualDesktopService.saveLitigation(litigationRequest, this.projectId).subscribe({
      next: (res: any) => {
        this.utils.emitLoadingEmitter(false);
        //alert("saved successfully")
        this.confirmationPopup.open('vd-reviewer');
       // this.showSaveWasSuccessful = true;
        if(litigationRequest.holdInd != 'Y'){
          this.litigationForm.get('startDate')?.setValue(null);
          this.litigationForm.get('endDate')?.setValue(null);
        }
        this.virtualDesktopData.litigationRequest= res.litigationRequest;
        this.virtualDesktopData.litigationRequestHistory = res.litigationRequestHistory;
        this.canShowLitigationBanner = this.virtualDesktopData.litigationRequest?.holdInd === 'Y' ? true: false;     
        this.litigationForm.markAsPristine();   
        // this.getVirtualDesktopData();
      },
      error: (error: any) => {
        this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        this.utils.emitLoadingEmitter(false);
        throw error;
      },
    });
  }

  saveFoil(){
    const payload = {
      foilRequest: {
        foilReqInd: '',
        modified: this.isFoilRequestModified() ? 'Y' : 'N',
        foilRequestNumber: this.foilRequestRowsModified.map(
          (item) => item.foil
        ),
      }
    }; 
    this.utils.emitLoadingEmitter(true);
    this.virtualDesktopService.saveFoil(payload.foilRequest, this.projectId).subscribe({
      next: (res: any) => {
        this.utils.emitLoadingEmitter(false);
        //alert("saved successfully")
        this.showSaveWasSuccessful = true;       
       // this.getVirtualDesktopData();
       this.virtualDesktopData.foilRequestNumber=res;
      },

      error: (error: any) => {
        this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        this.utils.emitLoadingEmitter(false);
        throw error;
      },
    });

  }

  // onLitigationConfirmProceed(){
    
  //   const payload = {
  //     foilRequest: {
  //       foilReqInd: 'Y',
  //       modified: this.isFoilRequestModified() ? 'Y' : 'N',
  //       foilRequestNumber: this.foilRequestRowsModified.map(
  //         (item) => item.foil
  //       ),
  //     },
  //     litigationRequest: this.litigationRequestPayload(),
  //   };
  //   this.virtualDesktopService.saveFoilAndLitigation(payload, this.projectId).subscribe({
  //     next: (res: any) => {
  //       //alert("saved successfully")
  //       this.showSaveWasSuccessful = true;
  //       if(payload.litigationRequest.holdInd != 'Y'){
  //         this.litigationForm.get('startDate')?.setValue(null);
  //         this.litigationForm.get('endDate')?.setValue(null);
  //       }
        
  //       this.getVirtualDesktopData();
  //     },
  //     error: (err: any) => {},
  //   });
  // }
  
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
        this.saveLitigationConfirmed(); // TODO: remove onLitigationConfirmProceed
      },
      (reason) => { // on cancel click
       
      }
    );
}
  litigationRequestPayload() {
    const litigationFormValue = this.litigationForm?.value;
    const holdInd = get(this.virtualDesktopData, 'litigationRequest.holdInd', 'N');
    let payload = {
      litigationStartDate: null,
      litigationEndDate: null,
      holdInd: '',
      modified: 'Y',
    };
    const litigationStartDate = litigationFormValue.startDate
      ? moment(litigationFormValue.startDate, 'YYYY-MM-DD').format('MM/DD/YYYY')
      : null;
    const litigationEndDate = litigationFormValue.endDate
      ? moment(litigationFormValue.endDate, 'YYYY-MM-DD').format('MM/DD/YYYY')
      : null;
    set(payload, 'litigationStartDate', litigationStartDate);
    set(payload, 'litigationEndDate', litigationEndDate);
    set(payload, 'holdInd', get(payload, 'litigationStartDate')?'Y':'N');
    return payload;
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

  checkLitigationStartDate() {
    if(this.litigationForm.get('startDate') && 
       this.litigationForm.get('startDate')?.value) {
      this.isLitigationChecked = true;
    }
  }

  onCloseEmptyProject() {
    this.ngOnDestroy();
    window.close();
  }
}

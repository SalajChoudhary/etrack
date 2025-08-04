import {
  Component,
  Input,
  OnInit,
  Output,
  EventEmitter,
  ViewChild,
  ElementRef,
  HostListener,
} from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { BehaviorSubject, fromEvent, Subject, Subscription } from 'rxjs';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { ApplicantInfoServiceService } from 'src/app/@shared/services/applicant-info-service.service';
import { CommonService } from 'src/app/@shared/services/commonService';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { ModalConfig } from 'src/app/modal.config';
import { takeUntil } from 'rxjs/operators';
import { Utils } from 'src/app/@shared/services/utils';
import { ErrorService } from 'src/app/@shared/services/errorService';

@Component({
  selector: 'app-applicant-selection',
  templateUrl: './applicant-selection.component.html',
  styleUrls: ['./applicant-selection.component.scss'],
})
export class ApplicantSelectionComponent implements OnInit {
  modalRef!: NgbModalRef;
  firstFormGroup: any;
  isEditable = false;
  selectedApplicants: any = [];
  applicantsList: any[] = [];
  addressDetails: string = '';
  notListedvalue: String = '';
  @Input() popupData: any;
  @Input() currentTab: any;
  @Output() closeBtnClicked = new EventEmitter();
  isFromSelection: string = 'selection';
  subscriptions: Subscription[] = [];
  appSearchModalRef!: NgbModalRef;
  isSubmitted: boolean = false;
  errorMsgObj: any = {};
  configObject: any;
  searchConfig: { title: string; showHeader: boolean; showClose: boolean };

  isOpenPopupSelectionSearch = new BehaviorSubject<boolean>(false);
  isOpenPopupSelectionInfo = new BehaviorSubject<boolean>(false);
  modalConfig: ModalConfig = {
    title: 'New York State Department of Environmental Conservation',
    showHeader: true,
    onClose: () => {
      this.isOpenPopupSelectionSearch.next(false);
      return true;
    },
    onDismiss: () => {
      this.isOpenPopupSelectionSearch.next(false);
      return true;
    },
    shouldClose: () => {
      return true;
    },
    shouldDismiss: () => {
      return true;
    },
  };
  @ViewChild('notListedCheckbox') notListedCheckbox!: ElementRef;
  @ViewChild('modal') private modal!: CustomModalPopupComponent;
  @ViewChild('applicantInfoModal')
  private applicantInfoModal!: CustomModalPopupComponent;
  noteText: string = '';
  headerText: string = '';
  selectedApplicantArray: any = [];
  //reload = () => window.location.reload();
  category: string = '';
  associatedInd: string = '';
  isFromSearch: string = 'selection';
  private unsubscriber: Subject<void> = new Subject<void>();
  serverErrorMessage: string='';
  showServerError: boolean=false;

  constructor(
    private _formBuilder: UntypedFormBuilder,
    private router: Router,
    private commonService: CommonService,
    private applicantService: ApplicantInfoServiceService,
    private modalService: NgbModal,
    private utils: Utils,
    private errorService: ErrorService,
    public projectService: ProjectService
  ) {
    this.searchConfig = {
      title: 'New York State Department of Environmental Conservation',
      showHeader: true,
      showClose: true,
    };
  }

  async openModal() {
    this.isOpenPopupSelectionSearch.next(true);
    this.modal.open('responsive-modal1');
    setTimeout(() => {
      this.notListedCheckbox.nativeElement.checked = false;
    });
  }

  ngOnChanges() {
    if (this.popupData && this.popupData?.title == 'Contact/Agent') {
      this.getExistingAplicants();
    }
  }

  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val) => {
      if (val) this.errorMsgObj = this.commonService.getErrorMsgsObj();
    });
  }
  getConfig() {
    this.commonService.getAllConfigurations().then((response) => {
      if (response) {
        this.configObject = response;
      }
    });
  }
  ngOnInit(): void {
    this.getAllErrorMsgs();
    this.getConfig();
    this.associatedInd = '0';
    if (this.currentTab && this.currentTab == 2) {
      this.noteText = 'contacts';
      this.headerText = 'Contact / Agent';
      this.notListedvalue = 'Contact/Agent';
      //this.popupData=this.headerText;
      this.category = 'C';
    } else if (this.currentTab && this.currentTab == 1) {
      this.noteText = 'property owners at this location.';
      this.headerText = 'Property Owner';
      this.notListedvalue = 'Property Owner';
      //this.popupData=this.headerText;
      this.category = 'O';
    } else {
      this.noteText = 'applicants for previous projects at this location.';
      this.headerText = 'Applicant(s) FOR SELECTED LOCATION';
      this.notListedvalue = 'Applicant';
      //this.popupData='Applicants'
      this.category = 'P';
      //sessionStorage.removeItem('applicantsList');
    }

    this.getExistingAplicants();
    this.firstFormGroup = this._formBuilder.group({
      firstCtrl: [''],
    });
    this.addressDetails = 'main Project';
    //This disables browsers back button
    history.pushState(null, '');

    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }
  ngAfterViewInit() {}

  getExistingAplicants() {
    let projectId = localStorage.getItem('projectId');
    sessionStorage.setItem('isFromScreen', 'selection');

    console.log("Ass Ind, Categ",this.associatedInd, this.category);
    //!!TODO Remove project id from localstorage
    if (projectId && this.associatedInd && this.category ) {
      this.utils.emitLoadingEmitter(true);
      this.subscriptions.push(
        this.applicantService
          .getAllExistingApplicants(''+this.associatedInd, ''+this.category)
          .subscribe(
            (response) => {
              this.utils.emitLoadingEmitter(false);
              this.applicantsList = response.applicants;
              if (
                (this.applicantsList == null &&
                  sessionStorage.getItem('addApplicants') == 'true') ||
                this.applicantsList?.length == 0
              ) {
                this.selectedApplicants.push('0');
                sessionStorage.removeItem('addApplicants');
                this.navigateToAppInfo();
              }
            },
            (error: any) => {
              
              this.utils.emitLoadingEmitter(false);
              this.applicantsList = [];
              this.serverErrorMessage = this.errorService.getServerMessage(error);
              this.showServerError = true;
              throw error;
            },
          )
      );
    }
  }

  getApplicantDetailsArray() {
    let applicantsSelected: any = [];
    this.applicantsList.forEach((obj: any) => {
      if (this.selectedApplicants.includes(`${obj?.applicantId}`)) {
        applicantsSelected.push(obj);
      }
    });
    this.selectedApplicantArray = [...applicantsSelected];
  }
  onApplicantChange(event: any) {
    let applicantId = event.target.value;
    this.isSubmitted = false;
    if (event.target.checked) {
      this.selectedApplicants.push(applicantId);
    } else {
      let index = this.selectedApplicants.findIndex(
        (person: any) => person === applicantId
      );
      this.selectedApplicants.splice(index, 1);
    }
    this.getApplicantDetailsArray();
  }
  openApplicantInfoModal() {
    this.commonService.setFromScreen(this.isFromSearch);
    this.isOpenPopupSelectionInfo.next(true);
    this.applicantInfoModal.open('xxl');
  }
  sortApplicants() {
    this.selectedApplicantArray.sort((a: any, b: any) => {
      if (a.displayName < b.displayName) return -1;
      if (a.display > b.displayName) return 1;
      return 0;
    });
    let applicantIds = [...this.selectedApplicants];
    this.selectedApplicants = [];
    this.selectedApplicantArray.forEach((obj: any) => {
      this.selectedApplicants.push(obj.applicantId);
    });
    if (applicantIds.includes('0')) this.selectedApplicants.push('0');
  }
  navigateToAppInfo() {
    this.isSubmitted = true;
    if (this.selectedApplicants?.length > 0) {
      if (
        this.selectedApplicants?.length == 1 &&
        this.selectedApplicants?.includes('0')
      ) {
        this.commonService.closeApplicantSelectionModal.next(true);
        this.openModal();
        return;
      }
      this.sortApplicants();
      this.commonService.setApplicants(this.selectedApplicants);

      let tab = this.currentTab;
      this.currentTab = '';

      this.openApplicantInfoModal();
      this.currentTab = tab;
    }
  }

  ngOnDestroy() {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }

  closeSearchModal(e: any) {
    this.reset();
    this.isOpenPopupSelectionSearch.next(false);
    this.closeBtnClicked.emit(e);
    this.modal.close();
  }

  @HostListener('window:keydown.esc', ['$event'])
  handleKeyDown(event: KeyboardEvent) {
    this.closeSearchModal(event);
  }
  reset() {
    this.notListedCheckbox.nativeElement.checked = false;
    this.selectedApplicants = [];
  }

  closeInfoModal(e: any) {
    //this.reset();
    this.isOpenPopupSelectionInfo.next(false);
    this.applicantInfoModal.close();
    this.closeBtnClicked.emit(e);
    //this.closeBtnClicked.emit(e);
    this.commonService.closeApplicantSelectionModal.next(true);
    if (e == 'open-search') {
      this.openModal();
    }
  }
}

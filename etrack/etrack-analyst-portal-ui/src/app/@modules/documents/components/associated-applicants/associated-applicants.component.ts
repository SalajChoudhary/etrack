import {
  Component,
  EventEmitter,
  Input,
  NgZone,
  OnInit,
  Output,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { takeUntil } from 'rxjs/operators';
import {
  ModalDismissReasons,
  NgbModal,
  NgbModalRef,
} from '@ng-bootstrap/ng-bootstrap';
import { MatStepper } from '@angular/material/stepper';
import { BehaviorSubject, fromEvent, Subject, Subscription } from 'rxjs';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { ApplicantInfoServiceService } from 'src/app/@shared/services/applicant-info-service.service';
import { CommonService } from 'src/app/@shared/services/commonService';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { ModalConfig } from 'src/app/modal.config';
import { DocumentService } from 'src/app/@shared/services/documentService';
import { EventEmitterService } from 'src/app/@shared/services/event-emitter.service';
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { isEmpty, get, isEqual, trim } from 'lodash';
import { ErrorService } from '../../../../@shared/services/errorService';
import { Utils } from 'src/app/@shared/services/utils';

@Component({
  selector: 'app-associated-applicants',
  templateUrl: './associated-applicants.component.html',
  styleUrls: ['./associated-applicants.component.scss'],
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { displayDefaultIndicatorType: false },
    },
  ],
  // encapsulation:ViewEncapsulation.ShadowDom,
})
export class AssociatedApplicantsComponent implements OnInit {
  @ViewChild('applicantInfoModal')
  private applicantInfoModal!: CustomModalPopupComponent;
  @ViewChild('deleteAgentModal', { static: true }) modaldeleteConfirm!: any;
  @Output() closeBtnClicked = new EventEmitter();
  selectedIndex: number = 0;
  firstFormGroup: any;
  isEditable = true;
  selectedApplicants: any = [];
  isChecked: boolean = false;
  routerLinkVal: String = '';
  applicantsCollection: any;
  propertyOwnerCollection: any;
  errorMsgObj: any = {};
  contractAgentCollection: any;
  addressDetails: any;
  popUpTitles = ['Applicant', 'Property Owner', 'Contact/Agent'];
  emptyMessageTitles = ['An Applicant', 'A Property Owner', 'Contact/Agent'];
  //searchConfig: { title: string; showHeader: boolean; showClose: boolean };
  isFromAssociate: string = 'associate';
  propertyOwnerPopupMessage = [
    'Would you like to add an additional property owner for this location?',
  ];

  //'Contact/Agents are optional for application(s) for this new project ',
  agentPopupMessage = [
    'Would you like to add a contact or agent for the permit applications?',
  ];
  confirmationMessage: any;
  applicantsList: any;
  popupData = {
    title: this.popUpTitles[0],
    details: '',
  };

  emptyMessageData = {
    title: this.emptyMessageTitles[0],
    details: '',
  };

  isOpenPopupApplicantsCnf = new BehaviorSubject<boolean>(false);
  isOpenPopupApplicants = new BehaviorSubject<boolean>(false);
  isOpenPopupApplicantsInfo = new BehaviorSubject<boolean>(false);
  isOpenPopUpSearch = new BehaviorSubject<boolean>(false);
  modalConfig: ModalConfig = {
    title: 'New York State Department of Environmental Conservation',
    showHeader: true,
    onClose: () => {
      this.isOpenPopupApplicants.next(false);
      this.isOpenPopupApplicantsInfo.next(false);
      return true;
    },
    onDismiss: () => {
      this.isOpenPopupApplicants.next(false);
      this.isOpenPopupApplicantsInfo.next(false);
      return true;
    },
    shouldClose: () => {
      return true;
    },
    shouldDismiss: () => {
      return true;
    },
  };
  selectionConfig: ModalConfig = {
    title: 'New York State Department of Environmental Conservation',
    showHeader: false,
    onClose: () => {
      this.isOpenPopupApplicants.next(false);
      this.isOpenPopupApplicantsInfo.next(false);
      return true;
    },
    onDismiss: () => {
      this.isOpenPopupApplicants.next(false);
      this.isOpenPopupApplicantsInfo.next(false);
      return true;
    },
    shouldClose: () => {
      return true;
    },
    shouldDismiss: () => {
      return true;
    },
  };

  searchConfig: ModalConfig = {
    title: 'New York State Department of Environmental Conservation',
    showHeader: true,
    showClose: true,
    onClose: () => {
      this.isOpenPopUpSearch.next(false);
      this.searchModal.dismiss();
      return true;
    },
    onDismiss: () => {
      this.isOpenPopUpSearch.next(false);
      return true;
    },
    shouldClose: () => {
      return true;
    },
    shouldDismiss: () => {
      return true;
    },
  };
  @ViewChild('modal') private modal!: CustomModalPopupComponent;

  confirmationModalConfig: ModalConfig = {
    title: '',
    showHeader: false,
    onClose: () => {
      this.isOpenPopupApplicantsCnf.next(false);
      this.isOpenPopupApplicants.next(false);
      return true;
    },
    onDismiss: () => {
      this.isOpenPopupApplicants.next(false);
      this.isOpenPopupApplicantsCnf.next(false);
      return true;
    },
    shouldClose: () => {
      return true;
    },
    shouldDismiss: () => {
      return true;
    },
  };
  @ViewChild('confirmationModal')
  private confirmationModal!: CustomModalPopupComponent;
  @ViewChild('searchModal')
  private searchModal!: CustomModalPopupComponent;
  @ViewChild('stepper') private myStepper: MatStepper | undefined;
  mode: string = '';
  totalStepCount = 0;
  subscriptions: Subscription[] = [];
  closeResult!: string;
  applicantContacts: any;
  modalReference!: NgbModalRef;
  deleteId: any;
  headerText: string = '';
  edbpublicId: any;
  isStayPoScreen: boolean = false;
  noClickCount: number = 0;
  category: string = 'P';
  associatedInd: string = '';
  propertyOwnerExistingList: any;
  contactAgentExistingList: any;
  applicantsExistingList: any;
  isFromSearch: string = 'applicants';
  closedAllPopup: boolean = false;
  stepOneCompleted: boolean = false;
  stepTwoCompleted: boolean = false;
  stepThreeCompleted: boolean = false;
  deleteIsClicked: Subject<boolean> = new Subject();
  deleteApplicant: Subject<boolean> = new Subject();
  deletePopupText: string =
    'Are you sure you want to permanently delete the selected item?';
  private unsubscriber: Subject<void> = new Subject<void>();

  applicantsValidated: boolean = false;
  propertyOwnersValidated: boolean = false;
  contactsAgentsValidated: boolean = false;
  applicantId: string = '';
  reloadPageBoolean: boolean = true;
  showServerError = false;
  serverErrorMessage!: string;
  isAlsoApplicant: boolean=false;

  get isReadonly() {
    return this.mode === 'read';
  }

  get isValidate() {
    return this.mode === 'validate';
  }

  deleteItem: any = '';

  constructor(
    public router: Router,
    public projectService: ProjectService,
    private modalService: NgbModal,
    private docService: DocumentService,
    private applicantService: ApplicantInfoServiceService,
    public commonService: CommonService,
    private applicationservice: ApplicantInfoServiceService,
    private eventEmitterService: EventEmitterService,
    private activatedRoute: ActivatedRoute,
    private errorService: ErrorService,
    private ngZone: NgZone,
    private utils: Utils
  ) {}

  validateChange() {}
  goForward() {
    this.myStepper?.next();
  }

  goBack() {
    this.myStepper?.previous();
  }

  async openModal() {
    this.isOpenPopupApplicants.next(true);
    return await this.modal.open('responsive-modal');
  }

  closeSearchModal(e: any) {
    this.closedAllPopup = true;
    this.isOpenPopUpSearch.next(false);
    this.searchModal.close();
    setTimeout(() => {
      if (e === 'reset') {
        this.openSelection();
      }
    });
  }

  async openSearchModal() {
    this.isOpenPopUpSearch.next(true);
    return await this.searchModal.open('responsive-modal1');
  }

  openConfirmationModal() {
    this.isOpenPopupApplicantsCnf.next(true);
    this.ngZone.run(() => {
      this.confirmationModal.open('sm');
    });
  }
  getHeaderText() {
    if (this.selectedIndex == 0) {
      this.headerText = 'Applicant';
    } else if (this.selectedIndex == 1) {
      this.headerText = 'Property Owner';
    } else if (this.selectedIndex == 2) {
      this.headerText = 'Contact/Agent';
    }
  }
  async ngOnInit() {
    console.log('NgONit call', Date);
    this.activatedRoute.queryParams.subscribe((params) => {
      // this.mode = get(params, 'mode', '')
    });
    this.commonService.activeMode.subscribe((res) => {
      console.log('assoccc service', res);
      this.mode = res ? res : `${localStorage.getItem('mode')}`;
      sessionStorage.setItem('mode', this.mode);
    });
    this.eventEmitterService.invokeFirstComponentFunction.subscribe(
      (name: string) => {
        this.isOpenPopupApplicants.next(false);
        sessionStorage.setItem('addApplicants', 'false');
        this.modal.close().then((val) => {
          if (name) {
            this.isStayPoScreen = true;
            this.popupData.title = this.popUpTitles[2];
            this.confirmationMessage = this.agentPopupMessage;
            this.openConfirmationModal();
          }
        });
      }
    );
    this.commonService.emitSubmitStatus.subscribe((status: boolean) => {
      if (status) {

        console.log("Status after close", status)
        this.getApplicantSummary();
        this.getPropertySummary();
        this.getContactSummary();
        // if (this.selectedIndex == 0 || this.selectedIndex == 1|| this.selectedIndex == 2 ) {
        //   this.getApplicantSummary();
        //   this.getPropertySummary();
        //   this.getContactSummary();
        // }
      }
    });

    this.getApplicantSummary();
    this.getPropertySummary();
    this.getContactSummary();
    //await this.getPropertySummary();
    // this.projectService.getProjectAddress(applicationId).then((res) => {
    //   this.addressDetails = res;
    // }),
    //   (error: any) => {
    //     this.serverErrorMessage = this.errorService.getServerMessage(error);
    //     this.showServerError = true;
    //     throw error;
    //   };

    this.deleteApplicant.subscribe((value) => {
      this.deleteApplicants();
    });

    this.projectService.getDestroyAssociatedSub().subscribe((data) => {
      if (data && this.reloadPageBoolean) {
        this.reloadPage();
        this.reloadPageBoolean = false;
      }
    });

    history.pushState(null, '');
    //diables browswers back button
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }

  // * @param category - Applicant Category . P- Publics, O- Owners, C- Contacts/Agents
  // * @param associatedInd - Project associated indicator .
  // * 0- Applicants associated with Facility and yet to be associated. 1- Associated

  getContactSummary() {
    const category = 'C';
    const associatedInd = '1';
    this.utils.emitLoadingEmitter(true);
    this.projectService.getAssociateDetails(associatedInd, category).then(
      (res) => {
        this.utils.emitLoadingEmitter(false);
        this.applicantContacts = res;
        this.contactsAgentsValidated =
          isEqual(get(this.applicantContacts, 'validatedInd', 'N'), 'Y') &&
          this.applicantContacts?.applicants?.length;
        // sessionStorage.setItem(
        //   'applicationTypeCode',
        //   get(this.applicantContacts, 'applicantTypeCode', '')
        // );
        // sessionStorage.setItem('isFirstApplicant',this.applicantContacts.applicants? 'false':'true');
        if (this.applicantContacts.applicants?.length > 0) {
          this.stepThreeCompleted = true;
        } else {
          this.stepThreeCompleted = false;
        }
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
    this.subscriptions.push(
      this.applicantService.getAllExistingApplicants('0', category).subscribe(
        (response) => {
          this.contactAgentExistingList = response.applicants;
        },
        (error: any) => {
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      )
    );
  }
  ngAfterViewInit() {
    let elements = Array.from(
      document.getElementsByClassName(
        'mat-horizontal-stepper-content'
      ) as HTMLCollection
    );
    elements.forEach((element: any) => {
      element.removeAttribute('aria-expanded');
    });
  }
  getApplicantSummary() {
    const category = 'P';
    const associatedInd = '1';
    this.projectService.getAssociateDetails(associatedInd, category).then(
      (res) => {
        this.applicantsCollection = res;
        sessionStorage.setItem(
          'applicationTypeCode',
          get(this.applicantsCollection, 'applicantTypeCode', '')
        );
        sessionStorage.setItem(
          'isFirstApplicant',
          this.applicantsCollection.applicants ? 'false' : 'true'
        );
        sessionStorage.setItem(
          'mailInInd',
          get(this.applicantsCollection, 'mailInInd', '')
        );

        this.applicantsValidated = isEqual(
          get(this.applicantsCollection, 'validatedInd', 'N'),
          'Y'
        );

        // if(this.applicantsCollection.applicants?.length>0){
        //   this.stepOneCompleted = true;
        // }
        // else{
        //   this.stepOneCompleted=false;
        // }
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
    //!!TODO Remove project id from localstorage
    this.subscriptions.push(
      this.applicantService.getAllExistingApplicants('0', category).subscribe(
        (response) => {
          this.applicantsExistingList = response.applicants;
        },
        (error: any) => {
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      )
    );
  }

  async getPropertySummary() {
    const category = 'O';
    const associatedInd = '1';
    this.projectService.getAssociateDetails(associatedInd, category).then(
      (res) => {
        this.propertyOwnerCollection = res;
        this.propertyOwnersValidated = isEqual(
          get(this.propertyOwnerCollection, 'validatedInd', 'N'),
          'Y'
        );
        // sessionStorage.setItem(
        //   'applicationTypeCode',
        //   get(this.propertyOwnerCollection, 'applicantTypeCode', '')
        // );
        // sessionStorage.setItem('isFirstApplicant',this.propertyOwnerCollection.applicants? 'false':'true');
        // // if(this.propertyOwnerCollection?.applicants?.length>0){
        //   this.stepTwoCompleted = true;
        // }
        // else{
        //   this.stepTwoCompleted=false;
        // }
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
    this.subscriptions.push(
      this.applicantService.getAllExistingApplicants('0', category).subscribe(
        (response) => {
          this.propertyOwnerExistingList = response.applicants;
        },

        (error: any) => {
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      )
    );
  }
  closeInfoModal(e: any) {
    //this.reset();
    this.isOpenPopupApplicantsInfo.next(false);
    this.applicantInfoModal.close();
    this.closeBtnClicked.emit(e);
    if (e === 'reset') {
      this.closeSearchModal(e);
    }
  }
  get getApplicantList() {
    return sessionStorage.getItem('applicantsList')
      ? sessionStorage.getItem('applicantsList')
      : '';
  }
  onStepChange(e: any) {
    this.selectedIndex = e.selectedIndex;

    this.applicantsList = sessionStorage.getItem('applicantsList');
    this.mode == sessionStorage.getItem('mode');
    console.log('assoccc onStepChange', this.mode);
    switch (this.selectedIndex) {
      case 0:
        this.popupData.title = this.popUpTitles[0];
        this.emptyMessageData.title = this.emptyMessageTitles[0];
        this.isStayPoScreen = false;
        this.category = 'P';
        this.getApplicantSummary();
        break;
      case 1:
        this.category = 'O';
        this.popupData.title = this.popUpTitles[1];
        this.emptyMessageData.title = this.emptyMessageTitles[1];
        this.getPropertySummary();
        if (e.previouslySelectedIndex < e.selectedIndex) {
          if (this.isStayPoScreen) {
            this.popupData.title = this.popUpTitles[2];
            this.confirmationMessage = this.agentPopupMessage;
            this.openConfirmationModal();
          } else {
            if (this.applicantsList && this.mode != 'validate') {
              this.confirmationMessage = this.propertyOwnerPopupMessage;
              this.openConfirmationModal();
            } else {
              //this.goForward();
            }
          }
        }
        break;
      case 2:
        this.category = 'C';
        this.popupData.title = this.popUpTitles[2];
        this.emptyMessageData.title = this.emptyMessageTitles[2];
        this.getContactSummary();
        if (!e.previouslySelectedIndex) {
          this.goForward();
          setTimeout(() => {
            this.goForward();
          });
        }
        break;
    }

    //this.stepperClickEvent(e, this.selectedIndex);

    // if(this.selectedIndex == 2 && this.applicantContacts?.applicants == null){
    //   this.isStayPoScreen = true;
    //   this.popupData.title = this.popUpTitles[2];
    //       this.confirmationMessage = this.agentPopupMessage;
    //       this.openConfirmationModal();
    // }
    // else if (!(this.selectedIndex == 2 && this.isStayPoScreen)) {
    //   this.stepperClickEvent(e, this.selectedIndex);
    // }

    this.getHeaderText();
  }

  stepperClickEvent(e: any, tabIndex: any) {
    this.applicantsList = sessionStorage.getItem('applicantsList');
    if (sessionStorage.getItem('mode')) {
      this.mode = sessionStorage.getItem('mode') as string;
    } else {
      this.mode = '';
    }
    console.log('assoccc onStepChange', this.mode);
    switch (tabIndex) {
      case 0:
        this.isStayPoScreen = false;
        this.popupData.title = this.popUpTitles[0];
        this.emptyMessageData.title = this.emptyMessageTitles[0];
        this.confirmationMessage = [];
        this.goForward();
        break;
      case 1:
        this.popupData.title = this.popUpTitles[1];
        this.emptyMessageData.title = this.emptyMessageTitles[1];
        // this.goForward();
        if (this.isStayPoScreen) {
          this.popupData.title = this.popUpTitles[2];
          this.confirmationMessage = this.agentPopupMessage;
          this.openConfirmationModal();
        } else {
          if (this.applicantsList && this.mode != 'validate') {
            this.confirmationMessage = this.propertyOwnerPopupMessage;
            this.openConfirmationModal();
          }
          this.goForward();
        }
        break;
      case 2:
        if (this.applicantContacts?.applicants === null) {
          this.isStayPoScreen = true;
          if (this.applicantsList && this.mode != 'validate') {
            this.popupData.title = this.popUpTitles[1];
            this.emptyMessageData.title = this.emptyMessageTitles[1];
            this.confirmationMessage = this.agentPopupMessage;
            this.openConfirmationModal();
          } else {
            if (this.applicantsList && this.mode != 'validate') {
              this.popupData.title = this.popUpTitles[2];
              this.emptyMessageData.title = this.emptyMessageTitles[2];
              this.confirmationMessage = this.propertyOwnerPopupMessage;
            } else {
              this.goForward();
            }
          }
        } else {
          console.log('here');

          this.popupData.title = this.popUpTitles[2];
          this.emptyMessageData.title = this.emptyMessageTitles[2];
          this.goForward();
        }
        break;
      case 3:
        //debugger;
        this.popupData.title = this.popUpTitles[2];
        this.emptyMessageData.title = this.emptyMessageTitles[2];
        this.navigateAccountInfo();
        //this.router.navigateByUrl('/project-informations')
        break;
    }
  }

  navigateAccountInfo() {
    this.modalService.dismissAll();
    this.commonService.navigateToMainPage();
  }

  closeModal(e: any) {
    this.isOpenPopupApplicants.next(false);
    sessionStorage.setItem('addApplicants', 'false');
    //this.modalService.dismissAll();
    //this.reload();
    this.modal.close();
    setTimeout(() => {
      if (e == 'reset') {
        this.openSelection();
      }
    });
  }

  //* @param category - Applicant Category . P- Publics, O- Owners, C- Contacts/Agents
  //* @param associatedInd - Project associated indicator .
  //* 0- Applicants associated with Facility and yet to be associated. 1- Associated/

  async openSelection() {
    //  alert("openSelectioncalled")
    this.isOpenPopupApplicants.next(false);
    sessionStorage.setItem('addApplicants', 'true');
    if (this.selectedIndex && this.selectedIndex === 2) {
      this.category = 'C';
      if (this.contactAgentExistingList === null) {
        this.openSearchModal();
      } else {
        this.openModal();
      }
    } else if (this.selectedIndex && this.selectedIndex === 1) {
      this.category = 'O';
      sessionStorage.removeItem('applicantsList');
      if (this.propertyOwnerExistingList === null) {
        this.openSearchModal();
      } else {
        this.openModal();
      }
    } else {
      this.category = 'P';
      // sessionStorage.removeItem('applicantsList');

      if (isEmpty(this.applicantsExistingList)) {
        this.openSearchModal();
      } else {
        this.openModal();
      }
    }
    // } else {
    //   //this.searchModal.close();
    //   this.openSearchModal();
    // }

    //sessionStorage.removeItem("addApplicants")
  }
  AddMoreApplicants() {
    this.openSearchModal();
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  // open(content: any, windowClass = '') {
  //   this.modelref=this.modalService
  //     .open(content, { ariaLabelledBy: 'modal-basic-title', size: 'xl', windowClass });
  //     //this.modelref.componentInstance.currentTab=this.selectedIndex;
  //     this.modelref.result.then(
  //       (result) => {
  //         this.closeResult = `Closed with: ${result}`;
  //       },
  //       (reason) => {
  //         this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
  //       }
  //     );
  // }
  open(content: any, modelSize = '40vw') {
    this.modalReference = this.modalService.open(content, {
      ariaLabelledBy: 'modal-basic-title',
      size: modelSize,
    });
    this.modalReference.result.then(
      (result) => {
        this.closeResult = `Closed with: ${result}`;
      },
      (reason) => {
        this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
      }
    );
  }
  async editApplicants(obj: any) {
    this.applicantId = obj?.applicantId;
    //this.applicantService.setApplicantId(this.applicantId);
    sessionStorage.removeItem('isFromScreen')
    await this.commonService.setApplicants([obj?.applicantId]);
    this.commonService.setFromScreen(this.isFromSearch);
    this.isOpenPopupApplicants.next(false);
    this.isOpenPopupApplicantsInfo.next(true);
    this.applicantInfoModal.open('appl-info');
  }
  confirmDelete(obj: any, isPropertyOwner = false) {
    this.deleteItem = obj?.displayName;
    this.deleteId = obj?.applicantId;
    this.edbpublicId = obj?.edbPublicId;
    this.isAlsoApplicant = false;
    if (isPropertyOwner) {
      this.isAlsoApplicant = this.checkIfAlsoApplicant(obj); // this.open(this.modaldeleteConfirm, '20vh');
    }

    this.deleteIsClicked.next(true);
  }
  checkIfAlsoApplicant(obj:any) {
    let isFound = this.applicantsCollection.applicants.find(
      (applicant:any) =>
        obj.edbPublicId == applicant.edbPublicId &&
        obj.applicantId == applicant.applicantId
    );
    return !!isFound;
  }
  deleteApplicants() {
    //TODO:
    this.applicationservice
      .deleteAgentById(this.deleteId, this.edbpublicId, this.category)
      .subscribe(
        (res) => {
          this.getApplicantSummary();
          this.getPropertySummary();
          this.getContactSummary();
          // if (this.selectedIndex == 0) {
          //   console.log('calling getApplicantSummary()');
          //   this.getApplicantSummary();
          // } else if (this.selectedIndex == 1) {
          //   console.log('calling getPropertySummary()');
          //   this.getPropertySummary();
          // } else if (this.selectedIndex == 2) {
          //   console.log('calling getContactSummary()');
          //   this.getContactSummary();
          // }
        },
        (error: any) => {
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      );
    // sessionStorage.removeItem('addApplicants');
    let stringArray: any = sessionStorage.getItem('applicantsList');
    if (isEmpty(stringArray)) {
      return;
    }
    const stringArraySplitted = stringArray
      .split(',')
      .map((item: any) => trim(item))
      .filter((v: any) => v);
    // stringArray = sessionStorage.getItem('applicantsList')?.split(',');
    // stringArray = stringArray?.filter((e: any) => this.deleteItem !== e);
    if (isEmpty(stringArraySplitted)) {
      return;
    }
    let index = stringArraySplitted.indexOf(trim(this.deleteItem));
    if (index > -1) {
      // only splice array when item is found
      stringArraySplitted.splice(index, 1); // 2nd parameter means remove one item only
      const stringArrayJoined = stringArraySplitted.join(', ');
      sessionStorage.setItem('applicantsList', stringArrayJoined);
    }
  }

  reloadPage() {
    this.ngOnInit();
  }

  ngOnDestroy() {
    sessionStorage.removeItem('applicantsList');
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }

  navigateEvent(e: any, action: string) {
    e.stopPropagation();
    this.modalService.dismissAll();
    if (action == 'yes') {
      this.isOpenPopupApplicants.next(false);
      this.isOpenPopupApplicantsInfo.next(false);
      // alert("selectedIndex"+this.selectedIndex)
      // alert("isStayPoScreen"+this.isStayPoScreen)
      if (this.selectedIndex == 1 && this.isStayPoScreen) {
        this.selectedIndex = 2;
        this.popupData.title = this.popUpTitles[2];
        this.confirmationMessage = this.agentPopupMessage;
        setTimeout(() => {
          this.openSelection();
        });
      } else {
        if (this.noClickCount === 1) {
          this.goForward();
        }
        setTimeout(() => {
          this.openSelection();
        });
      }
      this.noClickCount = 0;
    } else if (action == 'no') {
      sessionStorage.removeItem('applicantsList');
      this.noClickCount += 1;
      // alert(this.noClickCount)
      if (this.applicantContacts?.applicants == null && !this.isStayPoScreen) {
        this.selectedIndex = 1;
        this.isStayPoScreen = true;
        this.stepperClickEvent(e, this.selectedIndex);
      } else if (
        this.selectedIndex == 2 ||
        (this.selectedIndex == 1 && this.isStayPoScreen) ||
        this.noClickCount == 2
      ) {
        this.noClickCount = 0;
        this.navigateAccountInfo();
      } else {
        //this.selectedIndex = 1;
        //this.stepperClickEvent(e,this.selectedIndex);
        this.popupData.title = '';
        this.confirmationMessage = this.agentPopupMessage;
        this.openConfirmationModal();
        //this.selectedIndex = 2;
      }
    }
    sessionStorage.removeItem('applicantsList');
  }

  backToMain() {
    this.commonService.activeMode.next('');
    localStorage.setItem('mode', '');
    this.router.navigate(['/apply-for-permit-details']);
  }
  back() {
    this.router.navigate(['/project-location']);
  }
  forward() {
    this.router.navigate(['/project-informations']);
  }
}

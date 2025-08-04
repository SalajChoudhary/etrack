import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { fromEvent, Subject } from 'rxjs';
import { CommonService } from 'src/app/@shared/services/commonService';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { takeUntil } from 'rxjs/operators';
import moment from 'moment';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { AuthService } from 'src/app/core/auth/auth.service';

@Component({
  selector: 'app-apply-for-permit',
  templateUrl: './apply-for-permit.component.html',
  styleUrls: ['./apply-for-permit.component.scss'],
})
export class ApplyForPermitComponent implements OnInit {
  frm!: UntypedFormGroup;
  @Output() closeBtnClicked: any = new EventEmitter();
  @Input() userRoles: any[] = [];
  applicationReceipts: any[] = ['Online'];
  configObject: any;
  errorMsgObj: any;
  @Input() restartStepper!: boolean;
  isSubmitted: boolean = false;
  today = moment().format('YYYY-MM-DD');
  systemParameters:any;
  private unsubscriber: Subject<void> = new Subject<void>();

  get isFormDateValid() {
    const value = this.frm.get('date')?.value;
    if (!value) {
      return true;
    }
    return moment(value, 'YYYY-MM-DD').isBefore(moment());
  }

  get userIsAnalyst() {
    return this.userRoles.includes(UserRole.Analyst);
  }
  get userIsOnlineSubmitter() {
    return this.userRoles.includes(UserRole.Online_Submitter);
  }

  get userIsOverrideAdmin() {
    return this.userRoles.includes(UserRole.Override_Admin);
  }

  get userIsSystemAdmin() {
    return this.userRoles.includes(UserRole.System_Admin);
  }

  constructor(
    private commonService: CommonService,
    private formBuilder: UntypedFormBuilder,
    public projectService: ProjectService,
    public router: Router,
    private modalService: NgbModal,
    private authService: AuthService
  ) {}
  getConfig() {
    this.commonService.getAllConfigurations().then((response) => {
      if (response) {
        this.configObject = response;
      }
    });
  }

  getApplicantTypes() {
    return this.configObject.applicantTypes.filter( (applicantType: any) => 
      applicantType.applicantTypeDesc !== 'Contact / Agent');
  }

  getAllErrorMessages() {
    this.commonService.emitErrorMessages.subscribe((val) => {
      if (val) this.errorMsgObj = this.commonService.getErrorMsgsObj();
    });
  }
  ngOnInit(): void {
    this.commonService.getSystemParameters().subscribe(data=>{
      this.systemParameters=data;
    });
    if (this.userRoles && this.userRoles.includes('Online Submitter')) {
      this.applicationReceipts = ['Online'];
    } else {
      this.applicationReceipts = ['Email', 'Mail'];
    }
    this.frm = this.formBuilder.group({
      documentName: new UntypedFormControl('', []),
      date: new UntypedFormControl(this.today, [Validators.required]),
      documentReleasableCode: ['NODET'],
      description: [''],
      owner: ['0'],
      receivedBy: ['0'],
      filledBy: [1],
      classifiedUnderSeqr: ['3'],
      ownerOfProperty: ['2'],
    });
    //Disables browswers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
    this.getConfig();
  }
  nextBtnClicked() {
    // this.projectService.getFacilityDetails()
    // .then((res:any)=>{
    //   //localStorage.setItem('projectId',`${res.projectId}`);
    //  // localStorage.setItem('projectId','901');
    //   localStorage.setItem('projectId',`${res.projectId}`);
    //   //localStorage.setItem('projectId','842');
    //   this.closeModal("close");
    //   this.commonService.navigateToMainPage();
    // })

    // .catch(err=>{
    //   alert("Something went wrong while creating project");
    // })
    this.isSubmitted = true;

    if (this.frm.invalid || !this.isFormDateValid) {
      return false;
    }
    const data = this.frm.value;
    data.date = moment(data.date, 'YYYY-MM-DD').format('MM/DD/YYYY');

    const localStorageData = {
      mailInInd:
        this.userIsAnalyst || this.userIsOverrideAdmin || this.userIsSystemAdmin
          ? data.receivedBy
          : null,
      applicantTypeCode:
        this.userIsAnalyst || this.userIsOverrideAdmin || this.userIsSystemAdmin
          ? data.filledBy
          : null,
      receivedDate:
        this.userIsAnalyst || this.userIsOverrideAdmin || this.userIsSystemAdmin
          ? data.date
          : null,
      classifiedUnderSeqr: data.classifiedUnderSeqr,
      ownerOfProperty: this.userIsOnlineSubmitter ? data.ownerOfProperty : null,
    };
    localStorage.setItem(
      'applyForPermitData',
      JSON.stringify(localStorageData)
    );
    localStorage.setItem('mode', '');
    sessionStorage.setItem('applicationTypeCode', data.filledBy);
    this.closeModal('close');

    this.commonService.navigateToMainPage();
  }
  closeModal(e: any) {
    this.modalService.dismissAll();
    this.closeBtnClicked.emit(e);
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }
}

import {
  Component,
  ChangeDetectorRef,
  ViewChild,
  OnInit,
  Input,
  Output,
  EventEmitter,
} from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable, Subscription, timer } from 'rxjs';
import { map, shareReplay, timeout } from 'rxjs/operators';
import { AuthService } from 'src/app/core/auth/auth.service';
import { Router } from '@angular/router';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { CommonService } from '../../services/commonService';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';

@Component({
  selector: 'app-analyst-sub-header',
  templateUrl: './analyst-sub-header.component.html',
  styleUrls: ['./analyst-sub-header.component.scss'],
})
export class AnalystSubHeaderComponent implements OnInit {
  @ViewChild('pendingPopup', { static: true })
  pendingPopup!: PendingChangesPopupComponent;
  isHandset$: Observable<boolean> = this.breakpointObserver
    .observe(Breakpoints.Handset)
    .pipe(
      map((result) => result.matches),
      shareReplay()
    );
  screenerName: string = '';
  @ViewChild('applyForPermit', { static: true })
  applyForPermitModal!: any;
  userRoles: any[] = [];
  UserRole = UserRole;
  closeResult!: string;
  headerCollection: any;

  @Input() showButton: boolean = false;
  @Input() isPending: boolean = false;
  @Input() showAddress: boolean = false;
  @Input() isPopUp: boolean = false;
  @Output() openWarningModalEmitter = new EventEmitter();
  @Input() isValidateMode!: boolean;
  noBorderComponents: any = ['/dashboard'];
  addressSubscription!: Subscription;
  @ViewChild('validateModeLeaveConfirm', { static: true })
  validateModeLeaveConfirm!: NgbModal;
  subs = new Subscription();
  pendingUrl: string = '';

  constructor(
    private breakpointObserver: BreakpointObserver,
    public router: Router,
    private modalService: NgbModal,
    public commonService: CommonService,
    public projectService: ProjectService,
    private authService: AuthService
  ) {}

  goBack() {
    if (this.pendingUrl === 'pop-up') {
      if (!this.isValidateMode) {
        this.openModal();
        return;
      }
      const options = { ariaLabelledBy: 'modal-basic-title', size: '25vw' };
      const modalReference = this.modalService.open(
        this.validateModeLeaveConfirm,
        options
      );
      modalReference.result.then(
        (result) => {
          this.openModal();
        },
        (reason) => {
          console.log(reason);
        }
      );
      return;
    }
    if (this.pendingUrl == '/search') {
      this.router.navigateByUrl('/',{skipLocationChange:true}).then(() => {
        console.log('closed search')
        timer(500).subscribe(() => this.router.navigate([this.pendingUrl]));
      }); 
      return;
    }
    this.router.navigate([this.pendingUrl]);
  }

  navigateTo(url: string) {
    if (!this.isValidateMode) {
      if (this.isPending && url != '/dimsr') {
        this.pendingUrl = url;
        this.pendingPopup.open();
        return;
      }
      if(url==='/apply-for-inquiry'){
        localStorage.setItem('inquiryId',"");
        localStorage.setItem('inquiryCategoryCode', "");
      }
      
      this.router.navigate([url]);
      return;
    }
    const options = { ariaLabelledBy: 'modal-basic-title', size: '25vw' };
    const modalReference = this.modalService.open(
      this.validateModeLeaveConfirm,
      options
    );
 
    modalReference.result.then(
      (result) => {
        this.router.navigate([url]);
      },
      (reason) => {
        console.log(reason);
      }
    );
  }
  getFacilityAddress() {
    if (!this.showAddress) return;
    this.projectService
      .getFacilityDetails()

      .then((res) => {
        this.headerCollection = res;
      })

      .catch((err) => {
        console.log(err);
      });
  }
  ngOnInit() {
    this.addressSubscription = this.commonService.projectIdChanged.subscribe(
      (isChanged: boolean) => {
        if (isChanged) this.getFacilityAddress();
      }
    );
    this.getFacilityAddress();
    this.getCurrentUserRole();
  }

  getCurrentUserRole() {
    // let userInfo = this.authService.getUserInfo();
    // this.commonService
    //   .getUsersRoleAndPermissions(userInfo.ppid)
    //   .then((response) => {
    //   this.userRoles = response.roles;
    //   //this.userRoles = ['DEC Program Staff'];
    //   // this.userRoles = [UserRole.Online_Submitter];
    //   });
    this.subs.add(
      this.authService.emitAuthInfo.subscribe((authInfo: any) => {
        if (authInfo && !authInfo.isError) this.userRoles = authInfo.roles;
      })
    );
  }

  closeModal(e: any) {
    this.modalService.dismissAll();
  }

  onOkClick() {
    this.router.navigate(['/dashboard']);
    localStorage.removeItem('mode');
  }

  beforeOpenModal() {
    if (!this.isValidateMode) {
      if (this.isPending) {
        this.pendingUrl = 'pop-up';
        this.pendingPopup.open();
        return;
      }
      this.openModal();
      return;
    }
    const options = { ariaLabelledBy: 'modal-basic-title', size: '25vw' };
    const modalReference = this.modalService.open(
      this.validateModeLeaveConfirm,
      options
    );
    modalReference.result.then(
      (result) => {
        this.openModal();
      },
      (reason) => {
        console.log(reason);
      }
    );
  }

  openModal() {
    if (this.router.url !== '/apply-for-permit-details') {
      console.log('in first');
      localStorage.setItem('projectId', '');
      this.open(this.applyForPermitModal, 'apply-for-permit');
    } else if (this.router.url === '/apply-for-permit-details') {
      if (!this.isValidateMode) {
        this.openWarningModalEmitter.emit('open');
        return;
      }

      localStorage.setItem('projectId', '');
      this.open(this.applyForPermitModal, 'apply-for-permit');
    }
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

  open(content: any, windowClass = '') {
    this.modalService
      .open(content, { ariaLabelledBy: 'modal-basic-title', windowClass })
      .result.then(
        (result) => {
          this.closeResult = `Closed with: ${result}`;
        },
        (reason) => {
          this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
        }
      );
  }
  ngOnDestroy() {
    if (this.addressSubscription) this.addressSubscription.unsubscribe();
    this.subs.unsubscribe();
  }
}

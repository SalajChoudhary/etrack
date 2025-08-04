import {
  Component,
  OnInit,
  Input,
  Output,
  EventEmitter,
  ViewChild,
} from '@angular/core';
import { isEmpty, get, cloneDeep, uniq, find, isEqual } from 'lodash';
import { ProjectService } from '../../../../../@shared/services/projectService';
import { ApplicantInfoServiceService } from '../../../../../@shared/services/applicant-info-service.service';
import { forkJoin, Subscription } from 'rxjs';
import { CommonService } from 'src/app/@shared/services/commonService';
import { AuthService } from 'src/app/core/auth/auth.service';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { FormControl, UntypedFormGroup } from '@angular/forms';
import { UntypedFormBuilder, FormGroup, Validators } from '@angular/forms';
import { VirtualDesktopService } from 'src/app/@shared/services/virtual-desktop.service';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { InquiryService } from 'src/app/@shared/services/inquiryService';

@Component({
  selector: 'app-sideblock',
  templateUrl: './sideblock.component.html',
  styleUrls: ['./sideblock.component.scss'],
})
export class SideblockComponent implements OnInit {
  @ViewChild('pendingPopup', { static: true })
  pendingPopup!: PendingChangesPopupComponent;
  @Input() projectId: string = '';
  @Input() isFromDisposed: any = false;
  @Input() virtualDesktopData: any = {};
  @Input() errorMsgObj: any = {};
  facilityDetails: any = {};
  UserRole = UserRole;
  userRoles: any = [];
  associatedDetails: any = {
    C: [],
    P: [],
    O: [],
  };
  giIdForm!: UntypedFormGroup;
  activeTab: string | null;
  panelOpenState: boolean = false;
  @Input() foilRequestForm: any;
  @Input() litigationForm: any;
  @Output() foilRequestRowsModified = new EventEmitter();
  @Output() onFoilChange = new EventEmitter();
  @Output() onLitigationChange = new EventEmitter();
  @Input() foilRequestRowsUntouched: any = [];
  @Input() saveClicked: boolean = false;
  @Input() isGi: boolean = false;
  subs = new Subscription();
  rows: any = [];
  addClicked: boolean=false;  
  isDuplicateEntry: boolean = false;
  showServerError: boolean = false;
  serverErrorMessage: any;
  minLengthError: boolean = false

  // get batchGroups(){
  //   const batches = get(this.virtualDesktopData, 'milestone.batchDetails', []);
  //   if(isEmpty(batches)){
  //     return [];
  //   }

  //   const grouped = groupBy(batches, (batch:any) => batch.batchNumber)
  //   return values(grouped);
  // }



  get giIdNumberCtrl() {
    return this.giIdForm?.get('giId');
  }

  get giIdPrefix() {
    return this.giIdNumberCtrl?.value ? 'GID-' : '';
  }

  get edbDistrictIdUrl() {
    const edbDistrictId = get(
      this.virtualDesktopData,
      'facility.edbDistrictId',
      ''
    );
    if (edbDistrictId && !isEmpty(environment?.facilityNameUrl)) {
      return environment?.facilityNameUrl + edbDistrictId;
    }
    return '#';
  }

  get userIsProgramReviewer() {
    return this.userRoles.includes(UserRole.DEC_Program_Staff);
  }

  get userCanAddNewApplicationPermits() {
    let hasAccess = [
      UserRole.System_Admin,
      UserRole.Analyst,
      UserRole.Override_Admin,
    ];
    return this.userRoles.every((val: any) => hasAccess.includes(val));
  }

  constructor(
    private projectSrv: ProjectService,
    private applicantInfoSrv: ApplicantInfoServiceService,
    private commonService: CommonService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private errorService: ErrorService,
    private fb: UntypedFormBuilder,
    private inquiryService: InquiryService,
    private vds: VirtualDesktopService
  ) {
    this.activeTab = localStorage.getItem('vdActiveTab');
  }
  onCloseClick() {
    if (this.activeTab) {
      window.close();
      return;
    }
    this.router.navigate(['/dashboard']);
  }
  ngOnInit(): void {
    this.route.params.subscribe((params: any) => {
      this.projectId = params.projectId;
      this.getFacilityDetails();
    });
    this.getCurrentUserRole();
    console.log("VD Data",this.virtualDesktopData);
    if(!this.isGi) {

      this.rows = this.virtualDesktopData.inquiries.map((e: any) => {
        return {
          giId: this.inquiryService.formatInquiryId(e)
        }
      })
    }
    // if(this.virtualDesktopData?.milestone.batchDetails?.length){ // TODDO: Only for testing purpse. rmeove this code on production
    //   this.virtualDesktopData?.milestone.batchDetails.push(cloneDeep(this.virtualDesktopData?.milestone.batchDetails[0]))
    // }
    this.giIdForm = new FormGroup({
      giId: new FormControl('', [Validators.required]),
    });
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }
  getCurrentUserRole() {
    this.subs.add(
      this.authService.emitAuthInfo.subscribe((authInfo: any) => {
        if (authInfo && !authInfo.isError) {
          this.userRoles = authInfo.roles;
          //this.userRoles=['System Admin']
        }
      })
    );
  }

  getFacilityDetails() {
    this.projectSrv.getFacilityDetails(this.projectId).then((res) => {
      this.facilityDetails = res;
    });
  }

 

  getLrpUrl(detail: any) {
    return `${environment?.lrpUrl}${detail?.edbPublicId}`;
  }

  onViewMapClick() {
    if (this.virtualDesktopData.dimsrInd === 'Y') {
      const decId = this.virtualDesktopData?.facility?.edbDistrictId;

      window.open(
        '/project-map/?projectId=' + this.projectId + '&dimsrDecId=' + decId,
        '_blank'
      );
    } else {
      window.open('/project-map/?projectId=' + this.projectId, '_blank');
    }
  }
  
  onFoilRowsModified(modifiedRows: any) {
    this.foilRequestRowsModified.emit(modifiedRows);
  }

  onAdd(event: any) {
    event.preventDefault();
    event.stopPropagation();
    this.addClicked = true;
   
    if (this.giIdForm.invalid ) {
      return;
    }
    let val = this.giIdNumberCtrl?.value;
    if (val) {
      val = val.replace('GID-', '').trim();
      if (val.length < 6) {
        this.minLengthError=true;
      }
      else{
        this.minLengthError=false
      }
    }
    if (this.minLengthError) return;
    const value = this.giIdNumberCtrl?.value;
    let index = this.rows.findIndex((e: any) => e.giId == value);
    if (index != -1) {
      this.isDuplicateEntry = true;
    } else {
      this.isDuplicateEntry = false;
    }
    if (this.isDuplicateEntry) return;
    // this.isDuplicateEntry = !isEmpty(
    //   find(this.rows, (item) => isEqual(item.giId, value))
    // );
    // if (this.isDuplicateEntry) {
    //   return false;
    // }
    this.updategiId();
  }

  onClick(ev: any){
    window.open('gi-virtual-workspace/'+this.inquiryService.decodeInquiryId(ev))
  }

  updategiId(){
    let apiData=this.giIdNumberCtrl?.value;
    let giId=apiData.replace('GID-','')
    console.log("GID",this.projectId, giId)
    this.vds.updateGiId(this.projectId, giId).subscribe((res: any) => {
    this.rows = [...this.rows, ...[{ giId: this.giIdNumberCtrl?.value }]];
    this.giIdNumberCtrl?.setValue('');
    this.addClicked = false;
    },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  showFormattedPhoneNumber(number: string){
    if(number){
      let areaCode = number.substring(0,3);
      let SecondSub = number.substring(3,6);
      let thirdSub = number.substring(6);
   
      return "(" + areaCode + ") " + SecondSub + '-' + thirdSub;
    }
  return '';
  }
}

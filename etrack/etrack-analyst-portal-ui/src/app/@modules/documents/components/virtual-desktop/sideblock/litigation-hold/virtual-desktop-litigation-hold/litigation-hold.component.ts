import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { UntypedFormBuilder, FormControl, Validators } from '@angular/forms';
import { NgbModal, NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import { get, isEqual } from 'lodash';
import moment from 'moment';
import { LitigationService } from 'src/app/@shared/services/litigation.service';
import { VirtualDesktopLitigationHoldConfirmModalComponent } from '../virtual-desktop-litigation-hold-confirm-modal/virtual-desktop-litigation-hold-confirm-modal.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { AuthService } from 'src/app/core/auth/auth.service';
import { VirtualDesktopService } from 'src/app/@shared/services/virtual-desktop.service';

@Component({
  selector: 'app-litigation-hold',
  templateUrl: './litigation-hold.component.html',
  styleUrls: ['./litigation-hold.component.scss'],
})
export class LitigationHoldComponent implements OnInit {
  @Input() errorMsgObj: any = {};
  @Input() form: any;
  userRoles: any = [];
  @Input() virtualDesktopData:any;
  @Input() saveClicked: boolean = false;
  @Output() onLitigationChange = new EventEmitter();
  temp:any = {startDate:null, endDate:null};
  get litigationError() {
    if (this.userIsProgramReviewer) {
      return null;
    }
    
    const startDate = this.form?.get('startDate')?.getRawValue();    
    const endDate = this.form?.get('endDate')?.value;
    if (!startDate) {
     // console.log('were here in error', startDate);
      
      return (
        this.errorMsgObj?.LITIGATN_START_DATE_REQD ||
        'Error: Start Date is Required when a Litigation Hold is being placed on a project'
      );
    }
    if (
      endDate &&
      moment(startDate, 'YYYY-MM-DD').isAfter(moment(endDate, 'YYYY-MM-DD'))
    ) {
      this.form.get('endDate').setErrors({invalid:true});
      return (
        this.errorMsgObj?.END_DATE_GT_START_DATE ||
        'End Date must be greater than Start Date.'
      );
    }
    if(this.form.get('endDate')?.errors?.invalid){
      this.form.get('endDate').setErrors(null);
    }
    return null;
  }

  get userIsProgramReviewer() {
    return this.userRoles.includes(UserRole.DEC_Program_Staff);
  }

  constructor(
    private modalService: NgbModal,
    private fb: UntypedFormBuilder,
    private litigationSrv: LitigationService,
    private commonSrv: CommonService,
    private authService: AuthService,
    private vds:VirtualDesktopService
  ) {}

  ngOnInit(): void {
    this.authService.emitAuthInfo.subscribe((val: any) => {
      if (val?.isError) {
        return;
      }
      this.userRoles = this.commonSrv.roles;
    });
    this.watchLitigationForm();
  }

  watchLitigationForm() {
    console.log('watching');
    
    if (this.userIsProgramReviewer){
      this.form.get('startDate')?.disable();
    } 
    else{
      this.form.get('startDate')?.enable();
    }   


    this.form.get('endDate')?.disable();
    const litigationStartDate = get(this.vds.vdsData, 'litigationRequest.litigationStartDate', null);
  if (litigationStartDate) { 
    console.log('enable end date, disable start date');
    
      this.form.get('endDate')?.enable();
      this.form.get('startDate').disable();
    }
    this.form
      .get('startDate')
      ?.valueChanges.subscribe((val: any) => {
        if (val) {
          if (!this.userIsProgramReviewer) {
            this.form.get('endDate')?.enable();
          }
          // this.form.get('endDate').setValue(this.temp.endDate);
          return;
        }
        this.form.get('endDate')?.disable();
        this.temp.startDate = this.form.get('startDate')?.getRawValue();
        console.log(this.temp.startDate);
        
        this.temp.endDate = this.form.get('endDate')?.value;
          this.form.get('endDate').setValue(null);
      });
  }


  getFormattedDate(dateString: any) {
    if (!dateString) {
      return;
    }    
    return moment(dateString, 'YYYY-MM-DD').format('MM/DD/YYYY');
  }
}

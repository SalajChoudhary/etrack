import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { UntypedFormBuilder, FormGroup, Validators } from '@angular/forms';
import { FoilService } from '../../../../../../../@shared/services/foil.service';
import { CommonService } from 'src/app/@shared/services/commonService';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { find, isEmpty, isEqual } from 'lodash';
import { AuthService } from 'src/app/core/auth/auth.service';

@Component({
  selector: 'app-virtual-desktop-foil',
  templateUrl: './virtual-desktop-foil.component.html',
  styleUrls: ['./virtual-desktop-foil.component.scss'],
})
export class VirtualDesktopFoilComponent implements OnInit {
  @Input() form: any;

  @Input() rows: any = [];
  userRoles: any = [];
  @Input() errorMsgObj: any = {};
  isDuplicateEntry: Boolean = false;
  @Output() onFoilChange = new EventEmitter();
  @Output() modifiedRows: any = new EventEmitter();
  isFoilChecked: boolean = false;
  addClicked: boolean = false;
  showBlankInput = false;

  get foilRequestNumberCtrl() {
    return this.form?.get('foilRequestNumber');
  }

  get foilRequestPrefix() {
    return this.foilRequestNumberCtrl?.value ? 'W' : '';
  }

  get userIsProgramReviewer() {
    return this.userRoles.includes(UserRole.DEC_Program_Staff);
  }

  constructor(
    private fb: UntypedFormBuilder,
    private foilSrv: FoilService,
    private commonSrv: CommonService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.userRoles = this.commonSrv.roles;
    this.authService.emitAuthInfo.subscribe((val: any) => {
      if (val?.isError) {
        return;
      }
      this.userRoles = this.commonSrv.roles;

      setTimeout(() => {
        if (this.userIsProgramReviewer) {
          // this.form.get('foilRequest').disable({ emitEvent: false });
        }
      });
    });
    // this.userRoles =[UserRole.DEC_Program_Staff]

    // this.watchFoilRequest(); // TODO: remove Foil request
    this.watchFoilRequestNumber();
    // this.foilRequestNumberCtrl?.disable();
  }

  watchFoilRequest() {
    this.form?.get('foilRequest')?.valueChanges.subscribe((value: any) => {
      this.form?.get('foilRequestNumber').setValue('');
      this.showBlankInput = !this.showBlankInput;
      this.foilRequestNumberCtrl?.disable();
      this.foilRequestNumberCtrl?.clearValidators();
      this.isFoilChecked = false;
      this.addClicked = false;
      if (value) {
        this.foilRequestNumberCtrl?.enable();
        this.isFoilChecked = true;
        if (!this.userIsProgramReviewer) {
          this.foilRequestNumberCtrl?.setValidators(
            Validators.compose([
              Validators.required,
              Validators.minLength(14),
              Validators.maxLength(14),
            ])
          );
        }
      }

      this.foilRequestNumberCtrl?.updateValueAndValidity();
    });
  }
  watchFoilRequestNumber() {
    this.form.get('foilRequestNumber')?.valueChanges.subscribe((value: any) => {
      this.isDuplicateEntry = false;
    });
  }
  onAdd(event: any) {
    event.preventDefault();
    event.stopPropagation();
    this.addClicked = true;
    const isValid = this.validateRequestNumber(
      this.form.controls.foilRequestNumber.value
    );
    if (this.form.invalid || !isValid) {
      return;
    }
    const value = this.foilRequestNumberCtrl?.value;
    this.isDuplicateEntry = !isEmpty(
      find(this.rows, (item) => isEqual(item.foil, value))
    );
    if (this.isDuplicateEntry) {
      return false;
    }

    this.rows = [...this.rows, ...[{ foil: value }]];
    this.modifiedRows.emit(this.rows);
    this.foilRequestNumberCtrl?.setValue('');
    this.addClicked = false;
  }
  onRemove(event: any) {
    this.rows = this.rows.filter((row: any) => {
      return row.foil != event.foil;
    });
    this.modifiedRows.emit(this.rows);
  }
  // onSave(){
  //   const foilRequestNumber = this.rows.map((row:any)=>{
  //     return row.foil;
  //   })
  //   const payload = {
  //     foilReqInd: "Y",
  //     foilRequestNumber:foilRequestNumber
  //     }
  //   this.foilSrv.save(payload).subscribe({
  //     next: (res:any)=>{
  //       console.log(res)
  //     },
  //     error: (err:any)=>{
  //       console.error(err)
  //     }
  //   })
  // }

  validateRequestNumber(value: string) {
    //var reg = new RegExp('[Ww](\d{6})-(\d{0})/g', '');
    // if (!value.length || (value && !value.match(reg)) || value.length === 14) {
    //   return false;
    // }
    return true;
    if (value && value.length === 14) {
      const reg = /[Ww](\d{6})-(\d{6})/g;
      const isValid = reg.test(value);
      return isValid;
    }
    return false;
  }
}

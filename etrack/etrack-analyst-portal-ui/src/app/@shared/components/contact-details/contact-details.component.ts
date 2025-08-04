import {
  ChangeDetectorRef,
  Component,
  HostListener,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormGroup, NgForm, Validators } from '@angular/forms';

@Component({
  selector: 'app-contact-details',
  templateUrl: './contact-details.component.html',
  styleUrls: ['./contact-details.component.scss'],
})
export class ContactDetailsComponent implements OnInit {
  @Input() applicantsValidated:any;
  @Input() selectedApplicantType: any;
  @Input() contactDetailsFormGroup!: any;
  @Input() ngFormInstance!: any;
  @Input() errorMessages!: any;
  @Input() applicantDetails!: any;
  @Input() applicantDetailsHistory!: any;
  @Input() isClosevalidationCheck: boolean = false; 
  @Input() addressType!: any;
  @ViewChild('#cph', { static: true }) cph: any;
  isPaperMail:any;
  yieldSignPath = "assets/icons/yieldsign.svg";
  editSignPath = "assets/icons/revert_symbol.svg";
 @HostListener('window:keydown.tab', ['$event'])
  onTabClick(e: any) {
    let atId = e.target.getAttribute('id');
    if (atId == 'attentionId') {
      setTimeout(()=>{
        document.getElementById('cph')?.focus();
      })

    }
  }
  mode:any = localStorage.getItem('mode');
  get isModeValidate(){
    return this.mode == 'validate';
  }
  

  constructor(private cdr:ChangeDetectorRef) {}

  ngOnInit(): void {
    console.log(this.cph, 'cph');
   this.isPaperMail =sessionStorage.getItem('mailInInd') ;
    if(this.isPaperMail ==="1"){
      this.formControls.emailAddress.clearValidators();
    }
    // this.setInputFilter(this.cph, function (value) {
    //   return /^\d*\.?\d*$/.test(value); // Allow digits and '.' only, using a RegExp
    // });    
  }
  // ngAfterViewInit() {
  //   this.formControls.extension.disable();
  // }

  ngAfterContentInit(){
    if (
      !!this.applicantDetails &&
      this.applicantDetails.contact &&
      this.applicantDetails.contact.workPhoneNumber
    ) {
      this.formControls.extension.enable();
    }else{
      this.formControls.extension.disable();

    }

  }
  ngOnChanges(changes: any) {
    if(this.isPaperMail === "1"){
      this.formControls.emailAddress.clearValidators();
    }
    if (
      !!this.applicantDetails &&
      this.applicantDetails.contact &&
      this.applicantDetails.contact.workPhoneNumber
    ) {
      this.formControls.extension.enable();
    }
    if (this.addressType && this.addressType == 'us') {
      this.formControls.workPhNumber.setValidators([
        Validators.minLength(10),
        Validators.maxLength(10),
      ]);
      this.formControls.cellPhNumber.setValidators([
        Validators.minLength(10),
        Validators.maxLength(10),
      ]);
      this.formControls.homePhNumber.setValidators([
        Validators.minLength(10),
        Validators.maxLength(10),
      ]);
    }
    if (this.addressType && this.addressType == 'nonus') {
      this.formControls.workPhNumber.setValidators([]);
      this.formControls.cellPhNumber.setValidators([]);
      this.formControls.homePhNumber.setValidators([]);
    }
  }

  phoneNumberKeypress(ev: any){
    if(ev.keyCode == 8 || ev.keyCode == 46) return;
    else if(this.addressType == 'us' && ev.keyCode >= 48 && ev.keyCode <= 57 && (ev.shiftKey == false)){
      let text= ev.target.value;
      text=text.replace(/[^[0-9.]/g, '').replace(/(\..*)\./g, '$1');
      if(text.length>0) text=text.replace(/.{0}/,'$&(')
      if(text.length>3) text=text.replace(/.{4}/,'$&)')
      if(text.length>5) text=text.replace(/.{5}/,'$& ')
      if(text.length>6) text=text.replace(/.{9}/,'$&-')
      ev.target.value=text;
    }else{
      ev.preventDefault();
    }
  }

  get formControls() {
    return this.contactDetailsFormGroup.controls;
  }
  validateExt() {
    console.log(this.formControls.workPhNumber.value);
    if (this.formControls.workPhNumber?.value?.length > 0) {
      this.formControls.extension.enable();
    } else {
      this.formControls.extension.disable();
      this.formControls.extension.setValue('');
    }
    this.formControls.extension.updateValueAndValidity();
    this.cdr.detectChanges();
  }
  onKeyDown(e: any, flag?: string) {
    console.log(e, e.keyCode);
    if (
      (e.keyCode > 95 && e.keyCode < 106) ||
      e.keyCode == 8 ||
      e.keyCode == 37 ||
      e.keyCode == 39 ||
      e.keyCode == 46
    ) {
    } else {
      e.preventDefault();
    }
  }

  toggleValue(formControlName:any, value:any){
    if(this.applicantsValidated){
      return;
    }
    this.contactDetailsFormGroup.get(formControlName).setValue(value);
  }

  showFormattedPhoneNumber(number: string){
    if(number){
      let areaCode = number.substring(0,3);
      let SecondSub = number.substring(3,6);
      let thirdSub = number.substring(6);
   
      return "(" + areaCode + ") " + SecondSub + '-' + thirdSub;
    }
  return 
  }

}

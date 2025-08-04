import { Component, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';
import { UntypedFormGroup, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { ApplicantInfoServiceService } from '../../services/applicant-info-service.service';
import { CommonService } from '../../services/commonService';
import { ErrorService } from 'src/app/@shared/services/errorService';

@Component({
  selector: 'app-address',
  templateUrl: './address.component.html',
  styleUrls: ['./address.component.scss'],
})
export class AddressComponent implements OnInit {
  @Input() basicDetailsFormGroup!: UntypedFormGroup;
  addressType: string = 'us';
  @Input() applicantsValidated:boolean = false;
  @Input() ngFormInstance!: any;
  @Input() errorMessages!: any;
  @Input() applicantDetails!: any;
  @Output() addressChange = new EventEmitter<any>();
  @Input() applicantDetailsHistory!: any;
  @Input() selectedApplicantType: any = '';
  @Input() addressLocation: any = '';
  @Input() currentTab!: any;
  @Input() isClosevalidationCheck: boolean = false;
  streetAddress1Value : string = "";
  zipCodeValue : string  = "";
  timer : any = 0;
  showNoMatchFoundError : boolean = false;
  showAddressLookupDownError : boolean = false;
  yieldSignPath = 'assets/icons/yieldsign.svg';
  editSignPath = "assets/icons/revert_symbol.svg";
  configObject: any;
  showServerError = false;
  serverErrorMessage! : string;

  mode:any = localStorage.getItem('mode');
  streetAddress2Value: any;

  get isModeValidate(){
    return this.mode == 'validate';
  }

  constructor(private commonService: CommonService, private applicantInfoService: ApplicantInfoServiceService, private errorService: ErrorService) {}

  getConfig() {
    this.commonService.getAllConfigurations().then((response) => {
      if (response) {
        this.configObject = response;
      }
    }, 
    (error: any) =>{
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;  
    }
);
  }

  ngOnChanges(changes:any){
console.log(changes,'changes',this.currentTab,'currenttab')
  }
  ngOnInit(): void {    
    this.getConfig();
    this.addressChange.emit({addressType:this.addressType,isFirst:true});
    this.addressLocation = this.applicantDetails?.address?.adrType;
    if (this.addressLocation == '0') {
      this.addressType = 'us';
      this.clearNonUSValidators();
    } else if (this.addressLocation == '1') {
      this.addressType = 'nonus';
      this.clearUSValidators();
    }
    else {
      this.addressType = localStorage.getItem('addressType')!;
      if(this.addressType === 'us') {
        this.clearNonUSValidators();
      }
      else {
        this.clearUSValidators();
      }
    }
    this.basicDetailsFormGroup.controls.addressType.setValue(this.addressType);
    this.clearSessionStorage();
    this.storeInitialFormValues();
    
  }

  toggleValue(formControlName:any, value:any){
    console.clear();
    console.log(this.applicantsValidated)
    if(this.applicantsValidated){
      return;
    }
    this.basicDetailsFormGroup?.get(formControlName)?.setValue(value);
    
    if(formControlName == 'zipCode'){
      this.zipKeyup();
    }
    this.storeInitialFormValues();
  }

  storeInitialFormValues(){

    
    if(this.addressType == 'us'){
      if(this.formControls.streetAddress1.value){
        sessionStorage.setItem('us_streetAddress1', this.formControls.streetAddress1.value);
      }
      if(this.formControls.streetAddress2.value){
        sessionStorage.setItem('us_streetAddress2', this.formControls.streetAddress2.value);
      }else{
        sessionStorage.setItem('us_streetAddress2', "");
      }
      if(this.formControls.zipCode.value){
        sessionStorage.setItem('us_zipCode', this.formControls.zipCode.value);
      }
      if(this.formControls.state.value){
        sessionStorage.setItem('us_state', this.formControls.state.value);
      }
      if(this.formControls.postOffice.value){
        sessionStorage.setItem('us_PostOffice', this.formControls.postOffice.value);
      }
    }
    if(this.addressType == 'nonus'){
      if(this.formControls.streetAddress1.value){
        sessionStorage.setItem('nonus_streetAddress1', this.formControls.streetAddress1.value);
      }
      if(this.formControls.streetAddress2.value){
        sessionStorage.setItem('nonus_streetAddress2', this.formControls.streetAddress2.value);
      }else{
        sessionStorage.setItem('nonus_streetAddress2', "");
      }
      if(this.formControls.postalCode.value){
        sessionStorage.setItem('nonus_zipCode', this.formControls.postalCode.value);
      }
      if(this.formControls.stateProvince.value){
        sessionStorage.setItem('nonus_StateProvince', this.formControls.stateProvince.value);
      }
      if(this.formControls.city.value){
        sessionStorage.setItem('nonus_City', this.formControls.city.value);
      }
      if(this.formControls.country.value){        
        sessionStorage.setItem('nonus_Country', this.formControls.country.value);
      }
    }
  }

  clearUSValidators() {
    this.basicDetailsFormGroup.get('zipCode')?.clearValidators();
    this.basicDetailsFormGroup.get('postOffice')?.clearValidators();
    this.basicDetailsFormGroup.get('state')?.clearValidators();
    this.basicDetailsFormGroup.get('zipCode')?.updateValueAndValidity();
    this.basicDetailsFormGroup.get('postOffice')?.updateValueAndValidity();
    this.basicDetailsFormGroup.get('state')?.updateValueAndValidity();

  }
  resetFields() {    
    if(this.basicDetailsFormGroup.controls.addressType.value == 'us'){
      console.log('its us');
      
      if(sessionStorage.getItem('us_streetAddress1')){
        this.basicDetailsFormGroup.controls.streetAddress1.setValue(sessionStorage.getItem('us_streetAddress1'));
      }else{
        this.basicDetailsFormGroup.controls.streetAddress1.setValue('');
      }
      if(sessionStorage.getItem('us_streetAddress2')){
        this.basicDetailsFormGroup.controls.streetAddress2.setValue(sessionStorage.getItem('us_streetAddress2'));
      }else{
        this.basicDetailsFormGroup.controls.streetAddress2.setValue('');
      }

      this.basicDetailsFormGroup.controls.zipCode.setValue(sessionStorage.getItem('us_zipCode'));
      this.basicDetailsFormGroup.controls.state.setValue(sessionStorage.getItem('us_state'));
      this.basicDetailsFormGroup.controls.postOffice.setValue(sessionStorage.getItem('us_PostOffice'));
    } else {
      console.log('its non-us');
      
      if(sessionStorage.getItem('nonus_streetAddress1')){
        this.basicDetailsFormGroup.controls.streetAddress1.setValue(sessionStorage.getItem('nonus_streetAddress1'));
      }else{
        this.basicDetailsFormGroup.controls.streetAddress1.setValue('');
      }
      if(sessionStorage.getItem('nonus_streetAddress2')){
        this.basicDetailsFormGroup.controls.streetAddress2.setValue(sessionStorage.getItem('nonus_streetAddress2'));
      }else{
        this.basicDetailsFormGroup.controls.streetAddress2.setValue('');
      }

             
       this.basicDetailsFormGroup.controls.country.setValue(sessionStorage.getItem('nonus_Country'));
    
      this.basicDetailsFormGroup.controls.city.setValue(sessionStorage.getItem('nonus_City'));
      this.basicDetailsFormGroup.controls.stateProvince.setValue((sessionStorage.getItem('nonus_StateProvince')));
      this.basicDetailsFormGroup.controls.postalCode.setValue(sessionStorage.getItem('nonus_zipCode'));
    }

  }
  clearNonUSValidators() {
    this.basicDetailsFormGroup.get('city')?.clearValidators();
    this.basicDetailsFormGroup.get('stateProvince')?.clearValidators();
    this.basicDetailsFormGroup.get('postalCode')?.clearValidators();
    this.basicDetailsFormGroup.get('country')?.clearValidators();
    this.basicDetailsFormGroup.get('city')?.updateValueAndValidity();
    this.basicDetailsFormGroup.get('stateProvince')?.updateValueAndValidity();
    this.basicDetailsFormGroup.get('postalCode')?.updateValueAndValidity();
    //this.basicDetailsFormGroup.get('country')?.setValue('');
    this.basicDetailsFormGroup.get('country')?.updateValueAndValidity();
  }
  validateNonUsFields() {
    this.basicDetailsFormGroup.get('city')?.setValidators(Validators.required);
    this.basicDetailsFormGroup
      .get('stateProvince')
      ?.setValidators(Validators.required);
    this.basicDetailsFormGroup
      .get('postalCode')
      ?.setValidators(Validators.required);
    this.basicDetailsFormGroup
      .get('country')
      ?.setValidators(Validators.required);
    this.basicDetailsFormGroup.get('city')?.updateValueAndValidity();
    this.basicDetailsFormGroup.get('stateProvince')?.updateValueAndValidity();
    this.basicDetailsFormGroup.get('postalCode')?.updateValueAndValidity();
    this.basicDetailsFormGroup.get('country')?.updateValueAndValidity();
  }
  validateUsFields() {
    this.basicDetailsFormGroup
      .get('zipCode')
      ?.setValidators([
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(5),
      ]);
    this.basicDetailsFormGroup
      .get('postOffice')
      ?.setValidators(Validators.required);
      this.basicDetailsFormGroup.get('state')?.setValidators(Validators.required);
    this.basicDetailsFormGroup.get('zipCode')?.updateValueAndValidity();
    this.basicDetailsFormGroup.get('postOffice')?.updateValueAndValidity();
    this.basicDetailsFormGroup.get('state')?.updateValueAndValidity();

  }

  toggleAddress(adType: string) {
    this.addressType = adType;
    this.addressChange.emit({addressType: this.addressType, isFirst:false});
    this.resetFields();
    if (this.addressType === 'nonus') {
      this.clearUSValidators();
      this.validateNonUsFields();
    } else {      
      this.clearNonUSValidators();
      this.validateUsFields();
      if (this.basicDetailsFormGroup.get('zipCode')?.value) {
        let val = this.basicDetailsFormGroup
          .get('zipCode')
          ?.value.replace(/[^[0-9.]/g, '')
          .replace(/(\..*)\./g, '$1');
        this.basicDetailsFormGroup.get('zipCode')?.setValue(val);
      } else {
        this.basicDetailsFormGroup.get('zipCode')?.setValue('');
      }
    }
  }
  
  noUsChange(e: any) {
    this.addressType = e.target.value;  
    console.log('address type changed,' ,this.addressType);  
    this.addressChange.emit({addressType:this.addressType,isFirst:false});    
    this.resetFields();
    if (this.addressType === 'nonus') {
      this.clearUSValidators();
      this.validateNonUsFields();
    } else {      
      this.clearNonUSValidators();
      this.validateUsFields();
      if (this.basicDetailsFormGroup.get('zipCode')?.value) {
        let val = this.basicDetailsFormGroup
          .get('zipCode')
          ?.value.replace(/[^[0-9.]/g, '')
          .replace(/(\..*)\./g, '$1');
        this.basicDetailsFormGroup.get('zipCode')?.setValue(val);
      } else {
        this.basicDetailsFormGroup.get('zipCode')?.setValue('');
      }
    }

      //this.clearSessionStorage();
    // if (this.applicantDetails && this.applicantDetails.address.adrType == '0') {
    //   if (this.addressType == 'us') {
    //    //this.setAddressData();
    //   }else{
    //     this.resetFields();
    //   }
    // } else if (
    //   this.applicantDetails &&
    //   this.applicantDetails.address.adrType == '1'
    // ) {
    //   if (this.addressType == 'nonus') {
    //     //this.setAddressData();
    //    }else{
    //      this.resetFields();
    //    }
    // }
  }

  onZipInput(el: HTMLInputElement) {
    el.value = el.value.replace(/[^[0-9.]/g, '').replace(/(\..*)\./g, '$1');
    this.zipCodeValue = this.formControls.zipCode.value;
  }


  get formControls() {
    return this.basicDetailsFormGroup.controls;
  }
  onInput(e: any, el: HTMLInputElement) {
    if (!el.value.match(/[0-9]/)) {
      e.target.value = el.value.replace(/[^0-9]/g, '');
    }
  }
  onKeyPress(e: any) {
    var charCode = e.which || e.keyCode;
   var charStr = String.fromCharCode(charCode);
   if (!(/[a-z0-9]/i.test(charStr))) {
    e.preventDefault();
   }else{}
    }

    onPostalCodeKeyUp(ev: any){
      //this.clearNecessaryStorage();
      sessionStorage.setItem('nonus_zipCode', ev.target.value);
    }

  addressKeyUp(ev: any){
    //this.clearNecessaryStorage();
    this.clearAddressLookupErrors();
    console.log('setting addy', this.streetAddress1Value);
    this.streetAddress1Value = ev.target.value;
    
    this.basicDetailsFormGroup.controls.addressType.value == "us"? sessionStorage.setItem('us_streetAddress1', this.streetAddress1Value) : sessionStorage.setItem('nonus_streetAddress1', this.formControls.streetAddress1.value);
    // this.basicDetailsFormGroup.controls.state.setValue('');
    // this.basicDetailsFormGroup.controls.postOffice.setValue('');
    if(this.addressType ==='us'){
      this.streetAddress1Value = this.formControls.streetAddress1.value;  
      this.zipCodeValue = this.formControls.zipCode.value;
    }else{ 
      this.streetAddress1Value = '';
      this.zipCodeValue = ''; 
    }
    clearTimeout(this.timer);
    this.timer = setTimeout(()=>{
    this.callAddressZipLookUp();
    }, 1500); //half a second 1 second == 1000
  }

  address2KeyUp(){
    //this.clearNecessaryStorage();
     this.streetAddress2Value = this.formControls.streetAddress2.value;
    this.basicDetailsFormGroup.controls.addressType.value == "us"? sessionStorage.setItem('us_streetAddress2', this.streetAddress2Value) : sessionStorage.setItem('nonus_streetAddress2', this.streetAddress2Value);
  }

  zipKeyup(){
   // this.clearNecessaryStorage();    
   this.clearAddressLookupErrors();
    this.basicDetailsFormGroup.controls.addressType.value == "us"? sessionStorage.setItem('us_zipCode', this.zipCodeValue) : sessionStorage.setItem('nonus_zipCode', ' ');
    this.basicDetailsFormGroup.controls.state.setValue('');
    this.basicDetailsFormGroup.controls.postOffice.setValue('');
    this.updateCityAndStateStorage();
    if(this.addressType ==='us'){
      this.zipCodeValue = this.formControls.zipCode.value;
      this.streetAddress1Value = this.formControls.streetAddress1.value;
    }else{
      this.zipCodeValue = "";
      this.streetAddress1Value = '';
    }
    clearTimeout(this.timer);
      this.timer = setTimeout(()=>{
        this.callAddressZipLookUp();
      }, 1500); //half a second 1 second == 1000

  }

  stateKeyUp() {
    sessionStorage.setItem('us_state', this.formControls.state.value);
  }

  cityKeyUp() {
    sessionStorage.setItem('us_PostOffice', this.formControls.postOffice.value);
  }
  
  nonUsCityInput(event: any){
    //this.clearNecessaryStorage();
     sessionStorage.setItem('nonus_City', event.target.value);
  }

  
  onCountryChange(event: any){
    //this.clearNecessaryStorage();
    sessionStorage.setItem('nonus_Country', event.target.value);
    console.log('session', sessionStorage.getItem('nonus_Country'));
    
  }
 

  updateCityAndStateStorage(){
    sessionStorage.setItem('us_state', this.formControls.state.value);
    sessionStorage.setItem('us_PostOffice', this.formControls.postOffice.value);
  }

  nonUsStateProvinceInput(event: any){
    //this.clearNecessaryStorage();
    sessionStorage.setItem('nonus_StateProvince', event.target.value);
  }


  clearSessionStorage(){
    sessionStorage.removeItem('us_streetAddress1');
    sessionStorage.removeItem('nonus_streetAddress1');
    sessionStorage.removeItem('us_streetAddress2');
    sessionStorage.removeItem('nonus_streetAddress2');
    sessionStorage.removeItem('us_zipCode');
    sessionStorage.removeItem('nonus_zipCode');
    sessionStorage.removeItem('us-attention');
    sessionStorage.removeItem('nonus-attention');
    sessionStorage.removeItem('nonus_StateProvince');
    sessionStorage.removeItem('us_state');
    sessionStorage.removeItem('nonus_City');
    sessionStorage.removeItem('us_PostOffice');
    sessionStorage.removeItem('nonus_Country');
  }

  clearNecessaryStorage(){
    if(this.addressType === 'us'){
      this.clearNonUsSessionStorage();
    }else if(this.addressType === 'nonus'){
      this.clearUsSessionStorage();
      
    }
  }

  clearUsSessionStorage(){
    sessionStorage.removeItem('us_streetAddress1');
    sessionStorage.removeItem('us_streetAddress2');
    sessionStorage.removeItem('us_zipCode');
    sessionStorage.removeItem('us-attention');
    sessionStorage.removeItem('us_state');
    sessionStorage.removeItem('us_PostOffice');
  }

  clearNonUsSessionStorage(){
    sessionStorage.removeItem('nonus_streetAddress1');
    sessionStorage.removeItem('nonus_streetAddress2');
    sessionStorage.removeItem('nonus_zipCode');
    sessionStorage.removeItem('nonus_StateProvince');
    sessionStorage.removeItem('nonus_City');
    sessionStorage.removeItem('nonus_Country');
    sessionStorage.removeItem('nonus-attention');
  }
  clearAddressLookupErrors(){
    this.showAddressLookupDownError = false;
    this.showNoMatchFoundError = false;
  }

  callAddressZipLookUp(){
    this.showServerError = false;
      if(this.zipCodeValue && this.streetAddress1Value){
    if(this.zipCodeValue.length == 5 && this.streetAddress1Value != ""){
      this.applicantInfoService.getCityAndState(this.streetAddress1Value, this.streetAddress2Value, this.zipCodeValue).subscribe((response) =>{
          if(response){
            let responseCode = response.status;
            let responseBod = response.body;
            if(responseCode == 204){
                this.showNoMatchFoundError = true;
            }
            if(responseBod){              
            this.formControls.state.setValue(responseBod.state);
            this.formControls.postOffice.setValue(responseBod.city.toUpperCase());
            (<HTMLInputElement>document.getElementById('state')).value = responseBod.state;
            (<HTMLInputElement>document.getElementById('city')).value = responseBod.city.toUpperCase();
            sessionStorage.setItem('us_state', responseBod.state);
            sessionStorage.setItem('us_PostOffice', responseBod.city.toUpperCase());
          }
          }
      }, 
      (error: any) =>{
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;  
      }
  
      );
    }else if((this.zipCodeValue.length < 5 && this.streetAddress1Value != "") && this.zipCodeValue.length > 0){           
      this.showNoMatchFoundError = true;

  };
}
  }


  showAddressTypeHistory(){
    if(this.applicantDetailsHistory?.address?.adrType === '0') return 'US Address';
    return 'Non-US Adress';
  }

  showCountryHistory(){
    let countryCode = this.applicantDetailsHistory?.address?.country;
    let countriesArray : any[] = this.configObject.countries; 
    let countryName = "";
    countriesArray.forEach((country)=>{
      if(country.countryCode === countryCode){
         countryName = country.countryName;  
      }
    })
    return countryName;
    
  }

  ngOnDestroy(){
    this.clearSessionStorage();
  }


}

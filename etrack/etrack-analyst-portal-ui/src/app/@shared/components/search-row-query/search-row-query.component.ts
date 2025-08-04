import { Component, ElementRef, EventEmitter, Input, OnInit, Output, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, UntypedFormArray, UntypedFormGroup, Validators } from '@angular/forms';
import { isEmpty, isNull } from 'lodash';
import { merge } from 'rxjs';
import { CommonService } from '../../services/commonService';
import moment from 'moment';
@Component({
  selector: 'app-search-row-query',
  templateUrl: './search-row-query.component.html',
  styleUrls: ['./search-row-query.component.scss']
})
export class SearchRowQueryComponent implements OnInit {
  @Output() onAddClicked = new EventEmitter();
  @Output() onDeleteClicked = new EventEmitter();
  @Input() fieldName! : string;
  @Input() rowIndex! : number;
  @Input() mainRowIndex! : number;
  @Input() sectionFieldlist! : string[];
  @Input() sectionOperators! : string [];
  @Input() searchForm!: FormGroup;
  @Input() trackingArray! : any;
  @Input() isSubmitted! : boolean;
  @Input() firstSection!: any;
  currentFieldVal!: any;
  isBetween = false;
  checkboxValueArray : any[] = [];
  todaysDate: any = this.getTodaysDate();
  constructor() {}

  ngOnInit(): void {    
    // this.currentFieldVal = this.sectionFieldlist[0]; 
    this.currentFieldVal = {
      isCheckbox: "N",
      isMultiSelect:"N",
      checkboxesValue: []
    }; 
    let fVal = JSON.parse(JSON.stringify(this.searchForm.value));
    if (this.searchForm.get('field')?.getRawValue()) {
      this.onFieldChanged({target:{value: this.searchForm.get('field')?.getRawValue()}});
      console.log(fVal);
      if (fVal.dataType == 3 && fVal.operator == 'BETWEEN') {
        // fVal.inputField = new Date(fVal.inputField);
        this.onOperatorChanged({target: {value: 'BETWEEN'}});
        // fVal.betweenInput1 = new Date(fVal.betweenInput1);
        // fVal.betweenInput2 = new Date(fVal.betweenInput2);
      } else if (fVal.dataType == 3) {
        // fVal.inputField = new Date(fVal.inputField);
      }
      this.searchForm.patchValue(fVal);
    }     
}

getTodaysDate() {
  let dt = new Date();
  return moment(dt).format('YYYY-MM-DD');
}
get formControls(){
  return this.searchForm.controls;
}

getFilterList(filterList: any[]) {
  if (filterList && filterList.length > 0) {
    return filterList.filter((obj: any) => obj.label && obj.value);
  }
  return [];
}




  onFieldChanged(ev: any){
    console.log("OnFieldChange", ev)
    this.searchForm.patchValue({
      operator: '',
      multipleCheckboxes: '',
      inputField: '',
      betweenInput1: '',
      betweenInput2: '',
      checkbox:''
    });
    this.sectionFieldlist.forEach((obj :any) =>{
      if(obj.field == ev.target.value){
          this.currentFieldVal = obj;
          this.sectionOperators = (obj.operators && obj.operators.length > 0) ? obj.operators : [];
      }
    });
    console.log(this.currentFieldVal);
    if(this.currentFieldVal.isCheckbox == 'Y'){      
        this.searchForm.get('checkbox')?.setValue('0')
        this.searchForm.get('operator')?.setValue(null)
        this.searchForm.get('operator')?.clearValidators();
        this.searchForm.get('operator')?.updateValueAndValidity();
    }else if(this.currentFieldVal.isMultiCheckbox == 'Y'){      
      this.searchForm.get('multicheckbox')?.setValue('0')
      this.searchForm.get('operator')?.setValue(null)
      this.searchForm.get('operator')?.clearValidators();
      this.searchForm.get('operator')?.updateValueAndValidity();
  }
    else{
      this.searchForm.get('checkbox')?.setValue('')
      this.searchForm.get('operator')?.setValue(null)
      this.searchForm.get('operator')?.setValidators(this.sectionOperators.length>0? [Validators.required]: []);
      this.searchForm.get('operator')?.updateValueAndValidity();
    }


    let checkboxArray = this.currentFieldVal.checkboxesValue;
    // if(!isEmpty(checkboxArray)){
    //   for(let i = 0; i < checkboxArray.length; i++){
    //     this.multipleCheckboxesFormArry.push(new FormControl(null));
    //   }
    //   console.log(this.isCheckboxArrayEmpty);
    switch (this.currentFieldVal.attributeDataType) {
      case 1:
        this.searchForm.get('inputField')?.setValidators([Validators.required]);
        this.searchForm.get('inputField')?.updateValueAndValidity();
        this.searchForm.get('multipleCheckboxes')?.setValidators([]);
        this.searchForm.get('multipleCheckboxes')?.updateValueAndValidity();
        break;
      case 2:
        this.searchForm.get('multipleCheckboxes')?.setValidators([Validators.required]);
        this.searchForm.get('multipleCheckboxes')?.updateValueAndValidity();
        this.searchForm.get('inputField')?.clearValidators();
        this.searchForm.get('inputField')?.updateValueAndValidity();
        this.searchForm.updateValueAndValidity();
        break;
      case 3:
        if (this.isBetween) {
          this.searchForm.get('betweenInput1')?.setValidators([Validators.required]);
          this.searchForm.get('betweenInput1')?.updateValueAndValidity();
          this.searchForm.get('betweenInput2')?.setValidators([Validators.required]);
          this.searchForm.get('betweenInput2')?.updateValueAndValidity();
          // this.searchForm.get('inputField')?.setValidators([]);
          // this.searchForm.get('inputField')?.updateValueAndValidity();
        } 
        // else {          
        //   this.searchForm.get('inputField')?.setValidators([Validators.required]);
        //   this.searchForm.get('inputField')?.updateValueAndValidity();
        // }
        this.searchForm.get('multipleCheckboxes')?.setValidators([]);
        this.searchForm.get('multipleCheckboxes')?.updateValueAndValidity();
        break;
      case 4:
        this.searchForm.get('checkbox')?.setValidators([Validators.required]);
        this.searchForm.get('checkbox')?.updateValueAndValidity();
        this.searchForm.get('multipleCheckboxes')?.setValidators([]);
        this.searchForm.get('multipleCheckboxes')?.updateValueAndValidity();
        this.searchForm.get('inputField')?.setValidators([]);
        this.searchForm.get('inputField')?.updateValueAndValidity();
        this.searchForm.get('betweenInput1')?.setValidators([]);
        this.searchForm.get('betweenInput1')?.updateValueAndValidity();
        this.searchForm.get('betweenInput2')?.setValidators([]);
        this.searchForm.get('betweenInput2')?.updateValueAndValidity();
        break;
      case 5:
        this.searchForm.get('inputField')?.setValidators([Validators.required]);
        this.searchForm.get('inputField')?.updateValueAndValidity();
        this.searchForm.get('checkbox')?.setValidators([]);
        this.searchForm.get('checkbox')?.updateValueAndValidity();
        this.searchForm.get('multipleCheckboxes')?.setValidators([]);
        this.searchForm.get('multipleCheckboxes')?.updateValueAndValidity();
        this.searchForm.get('betweenInput1')?.setValidators([]);
        this.searchForm.get('betweenInput1')?.updateValueAndValidity();
        this.searchForm.get('betweenInput2')?.setValidators([]);
        this.searchForm.get('betweenInput2')?.updateValueAndValidity();
        break;
      case 6:
        this.searchForm.get('inputField')?.setValidators([Validators.required]);
        this.searchForm.get('inputField')?.updateValueAndValidity();
        this.searchForm.get('checkbox')?.setValidators([]);
        this.searchForm.get('checkbox')?.updateValueAndValidity();
        this.searchForm.get('multipleCheckboxes')?.setValidators([]);
        this.searchForm.get('multipleCheckboxes')?.updateValueAndValidity();
        this.searchForm.get('betweenInput1')?.setValidators([]);
        this.searchForm.get('betweenInput1')?.updateValueAndValidity();
        this.searchForm.get('betweenInput2')?.setValidators([]);
        this.searchForm.get('betweenInput2')?.updateValueAndValidity();
        break;
    }

    if (this.sectionOperators.length > 0 && this.currentFieldVal.isCheckbox == 'N') this.searchForm.get('operator')?.setValue(this.sectionOperators[0]);
    console.log(this.searchForm);
    
    
  }  

  formatDecId(val: string) {
    val = val.replace(/\D/g,'');
    const length = val.length;
    if (length > 0) {
      if (length > 1 && length < 5) {
        val = val.substring(0,1) + '-'+val.substring(1,5);
      } else if (length > 5 && length < 11) {
        val = val.substring(0,1) + '-'+val.substring(1,5)+'-'+val.substring(5,10);
       } else if (length > 10) {
        let rtns = length/10;
        rtns = rtns %1 !== 0 ? rtns + 1 : rtns;
        rtns = (''+rtns).includes('.') ? +(''+rtns).split('.')[0] : rtns;
        let substr: any[] = [];
        for (let index = 0; index < rtns; index++) {
          substr.push(val.substring(index*10, (index*10) + 10));
        }
        for (let index = 0; index < substr.length; index++) {
          substr[index] = this.formatDecId(substr[index]);
        }
        val = substr.join(',');
       }
      return val;
    }
    return val;
  }
  
  checkTypeAndFormat(data: any) {
     console.log(data.target.value, this.currentFieldVal);
     if (this.currentFieldVal.attributeDataType === 5) {
       console.log(this.formatDecId(data.target.value));
       let decId = this.formatDecId(data.target.value);
       this.searchForm.controls['maskedInput'].setValue(decId);

       this.searchForm.controls['inputField'].setValue(decId.replace(/-/g,''));
     }
  }

  isCheckboxArrayEmpty(control: AbstractControl){
    console.log(control.getRawValue());
      let checkboxesEmpty = control.getRawValue().every(isNull);
      if(checkboxesEmpty){
        return {isCheckboxArrayEmpty: true};
      }
     
    return null;
   
   }


  createRow(){
    console.log('add clicked!!, hild', this.searchForm);
    this.onAddClicked.emit(this.fieldName);
  
  }

  deleteRow(){
    this.onDeleteClicked.emit(this.fieldName);
  }

  onOperatorChanged(event: any){
   
    let val = event.target.value;
    console.log(val)
    if(val === "BETWEEN"){
      this.isBetween = true;
      this.searchForm.get('betweenInput1')?.setValidators([Validators.required]);
      this.searchForm.get('betweenInput1')?.updateValueAndValidity();
      this.searchForm.get('betweenInput2')?.setValidators([Validators.required]);
      this.searchForm.get('betweenInput2')?.updateValueAndValidity();
      if (this.currentFieldVal.attributeDataType != 2) {
        this.searchForm.get('inputField')?.setValidators([Validators.required]);
        this.searchForm.get('inputField')?.updateValueAndValidity();
      }
    }else{
     this.searchForm.get('betweenInput1')?.setValue(null);
     this.searchForm.get('betweenInput2')?.setValue(null);
     this.searchForm.get('betweenInput1')?.setValidators([]);
      this.searchForm.get('betweenInput1')?.updateValueAndValidity();
      this.searchForm.get('betweenInput2')?.setValidators([]);
      this.searchForm.get('betweenInput2')?.updateValueAndValidity();
      // if (this.currentFieldVal.attributeDataType != 2) {
      //   this.searchForm.get('inputField')?.setValidators([Validators.required]);
      //   this.searchForm.get('inputField')?.updateValueAndValidity();
      // }
  
     this.isBetween = false;
    }
  
  }

}

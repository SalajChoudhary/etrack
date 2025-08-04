import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';


@Component({
  selector: 'app-textarea-child',
  templateUrl: './textarea-child.component.html',
  styleUrls: ['./textarea-child.component.scss']
})
export class TextareaChildComponent implements OnInit {
brief:string='';
@Input() controlName:string='';
@Input() maxLength:string='';
@Input() noOfRows:string='';
@Input() fieldWidth:string='';
@Input() formGroup!:UntypedFormGroup;
@Input() showPlaceholder!: boolean;
@Input() placeholderVal! : string;
@Input() counterWidth : string = '';
@Output() onInputChange =new EventEmitter();
  constructor() { }
  ngOnChanges(changes:any){
    this.brief=this.formGroup?.value[this.controlName];
    // this.formGroup?.controls[this.controlName].setValue(this.formGroup?.controls[this.controlName].value);
  }
  ngOnInit(): void {
  }
  onChange(e:any){
    this.brief=e.target.value;
   // console.log(e,e.target,e.target.value)
    this.onInputChange.emit(this.brief);
  }
}

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { isEqual } from 'lodash';
@Component({
  selector: 'app-stepper-buttons',
  templateUrl: './stepper-buttons.component.html',
  styleUrls: ['./stepper-buttons.component.scss']
})
export class StepperButtonsComponent implements OnInit {

  @Input() startNumber: number = 0;
  @Input() isChecked: boolean =false;
  @Input() endNumber: number = 0;
  @Input() startName: string = '';
  @Input() endName: string = '';
  @Input() selectedIndex: number = 1;
  @Input() lastIndex: number = 3;
  @Input() className:string = '';

  @Input() mainNavigation:string =  '';
  @Input() backwardName:string = '';
  @Input() forwardName:string = '';
  @Input() backward:string = '';
  @Input() forward:string = '';
  @Input() showBackArrow: boolean = true;
  @Input() showForwardArrow: boolean = true;
  @Output() forwardClicked=new EventEmitter();

  get isValidateMode(){
    const mode = localStorage.getItem('mode');
    return isEqual(mode, 'validate');
  }

  constructor(public router:Router) { }

  ngOnInit(): void {
  }

  navigate(route: string) {
    if(this.selectedIndex===2 && this.endName==='Supporting Documents' && this.startName==='Project Information'){
      this.forwardClicked.emit(true);
    }else{
      this.router.navigate([route]);
    }
   
    
  }
}

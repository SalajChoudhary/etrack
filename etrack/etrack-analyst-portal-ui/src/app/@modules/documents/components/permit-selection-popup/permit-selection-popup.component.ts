import { Component, OnInit } from '@angular/core';
import { CustomValues } from './custom-values';
import { takeUntil } from 'rxjs/operators';
import { fromEvent, Subject } from 'rxjs';

@Component({
  selector: 'app-permit-selection-popup',
  templateUrl: './permit-selection-popup.component.html',
  styleUrls: ['./permit-selection-popup.component.scss']
})
export class PermitSelectionPopupComponent implements OnInit {

  existingPermits:any = CustomValues?.existingPermits;
private unsubscriber : Subject<void> = new Subject<void>();
   
  constructor() { }

  ngOnInit(): void {
      //diables browswers back button
   history.pushState(null, '');
   fromEvent(window, 'popstate').pipe(
     takeUntil(this.unsubscriber)
   ).subscribe((_) => {
     history.pushState(null, '');
   });
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }
}

import { Component, ElementRef, Input, OnInit, Output, EventEmitter, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { fromEvent, Subject } from 'rxjs';
import { CustomModalPopupComponent } from '../custom-modal-popup/custom-modal-popup.component';
import { takeUntil } from 'rxjs/operators';


@Component({
  selector: 'app-view-document-popup',
  templateUrl: './view-document-popup.component.html',
  styleUrls: ['./view-document-popup.component.scss']
})
export class ViewDocumentPopupComponent implements OnInit {
  @ViewChild("viewDocumentModal") viewDocumentPopup! : CustomModalPopupComponent;
  @ViewChild('closeButton',{static:false}) closeButton!: ElementRef;
  @Input() openModal!: Subject<string>;
  @Input() fileList!: any[];
  @Input() fileNameWidth!: number;
  @Input() fileDateWidth!: number;
  @Input() modalSize!: string;
  @Input() fullData!: any;
  radio_val!: string;
  @Output() fileClicked = new EventEmitter;
private unsubscriber : Subject<void> = new Subject<void>();
   
   

  documentName!: string;
   
  constructor(private modalService: NgbModal) { }

  ngOnInit(): void {
    this.openModal.subscribe((data)=>{
      this.documentName = data;
        this.openViewDoc();
    });
    this.radio_val = this.fullData[0]?.displayName;
       //diables browswers back button
   history.pushState(null, '');
   fromEvent(window, 'popstate').pipe(
     takeUntil(this.unsubscriber)
   ).subscribe((_) => {
     history.pushState(null, '');
   });
 
}

ngOnChanges(){
  this.fullData.sort((a: any,b : any) =>{
    if(a && b){
      a.displayName.localeCompare(b.displayName);
    }
  });
  this.radio_val = this.fullData[0]?.displayName;

}

  focusButton(){
  setTimeout(()=>{
    let button=document.getElementById('closeButton') as HTMLElement;
    if(button){
      button.focus();
    }
   
  },10)
}

ngAfterViewInit(){
 this.focusButton();
}

openViewDoc(){    
    const modelSize = '20vh';
    const modalReference = this.modalService.open(this.viewDocumentPopup, {
      ariaLabelledBy: 'A table showing files related to a document',
      size: this.modalSize,
    });
    this.focusButton();
  }

  onCloseClicked(){
    this.modalService.dismissAll();
  }

  onFileClick(file:any){
    this.fileClicked.emit(file)
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }
  
  

}

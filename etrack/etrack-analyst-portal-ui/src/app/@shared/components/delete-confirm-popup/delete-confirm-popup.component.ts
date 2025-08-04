import { Component, Input, OnInit, Output, EventEmitter, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { isEmpty, stubFalse } from 'lodash';
import { fromEvent, Subject } from 'rxjs';
import { CustomModalPopupComponent } from '../custom-modal-popup/custom-modal-popup.component';
import { takeUntil } from 'rxjs/operators';   

@Component({
  selector: 'app-delete-confirm-popup',
  templateUrl: './delete-confirm-popup.component.html',
  styleUrls: ['./delete-confirm-popup.component.scss']
})
export class DeleteConfirmPopupComponent implements OnInit {
  @ViewChild('confirmDeleteModal', { static: true }) confirmDeleteModal!: any;
  @Input() openModal!: Subject<boolean>;
  @Input() deleteApplicant : Subject<any[]> = new Subject();
  @Output() result = new EventEmitter;
  @Output() onDeleteConfirmed = new EventEmitter;
  @Input() bodyText!: string;
  @Input() secondLineBodyText!: string;
  @Input() closeClicked : Subject<string> = new Subject();
  @Input() isMultiple!: boolean;
  @Input() filesList!: any[];
  @Input() deletedDocName! : string;
  @Input() fromSupporting! :boolean;
  @Input() isDashboard! : boolean;
  @Input() isAlsoApplicant : boolean=false;
  deletedFilesForm! : UntypedFormGroup;
  @Input() formGroup!:UntypedFormGroup;
  filesToDelete : any[] = [];
  @Input() isValidate!: boolean;
  reason: any ='';
  @Input() rejectReason:string='';
  reasonError: boolean =false;
  private unsubscriber : Subject<void> = new Subject<void>();


  constructor(private modalService: NgbModal, private fb: UntypedFormBuilder) { }

  ngOnInit(): void {
    this.openModal.subscribe((v)=>{
      if(!v){
        return;
      }
      this.confirmDelete();
    });

       //diables browswers back button
   history.pushState(null, '');
   fromEvent(window, 'popstate').pipe(
     takeUntil(this.unsubscriber)
   ).subscribe((_) => {
     history.pushState(null, '');
   });
    
  }

  confirmDelete(){
    const modelSize = '20vh';
    const modalReference = this.modalService.open(this.confirmDeleteModal, {
      ariaLabelledBy: 'modal-basic-title',
      size: modelSize,
    });
    this.result.emit(modalReference); 
  }

  onChange(e:any){
    this.reason=e.target.value;
  }

  deleteClicked(){ 
    if(!this.isValidate) {
    this.deleteApplicant.next(this.filesToDelete);
    this.filesToDelete = [];
    this.onDeleteConfirmed.emit(true);
    }

    if(isEmpty(this.reason)){
      this.reasonError = true;
      return;
    }
     
    if(this.isValidate){
      this.onDeleteConfirmed.emit(this.reason);
    }
    else{
      this.onDeleteConfirmed.emit(true);  
    }
    this.modalService.dismissAll('deleted');
  }

  onCloseClicked(){
    this.closeClicked.next('close');
  }

  onCheckBoxChange(ev: any){
    let docName = ev.srcElement.id;
    console.log(ev);
    if(ev.target.checked){
      this.filesToDelete.push(docName);
    }else{
      this.filesToDelete = this.filesToDelete.filter(documentName => documentName != docName);
    }
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }

}

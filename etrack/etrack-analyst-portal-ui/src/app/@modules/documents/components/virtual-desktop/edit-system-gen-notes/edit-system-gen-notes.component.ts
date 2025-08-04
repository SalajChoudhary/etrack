import { DatePipe } from '@angular/common';
import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { VirtualDesktopService } from 'src/app/@shared/services/virtual-desktop.service';
import { PendingChangesPopupComponent } from '../../../../../@shared/components/pending-changes-popup/pending-changes-popup.component';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { ActivatedRoute } from '@angular/router';
import { isEmpty } from 'lodash';
@Component({
  selector: 'app-edit-system-gen-notes',
  templateUrl: './edit-system-gen-notes.component.html',
  styleUrls: ['./edit-system-gen-notes.component.scss'],
})
export class EditSystemGenNotesComponent implements OnInit {
  @ViewChild('pendingPopup', { static: true })
  pendingPopup!: PendingChangesPopupComponent;
  @Output() onClose = new EventEmitter();
  @Input() noteId!: number;
  @Input() isReadOnly:boolean = false;
  inquiryId: any;
  isGi: boolean = false;
  notesFormGroup!: UntypedFormGroup;
  submitted: boolean = false;
  maxNote: number = 300;
  noteDetails: any = {};
  showServerError = false;
  serverErrorMessage! : string;
  projectId: any = '';
  litHoldStartString! :string;
  litHoldEndString! : string;
  litHoldHasEndDate = true;
  constructor(
    private formBuilder: UntypedFormBuilder,
    private virtualDesktopService: VirtualDesktopService,
    private datePipe: DatePipe,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
    private errorService: ErrorService
  ) {}

  ngOnInit(): void {
    this.initiateForm();
    if (this.noteId) {      
      this.getNoteDetails(this.noteId);
    }
    this.route.params.subscribe((params:any)=>{
      this.projectId = params.projectId;
    })

  }

  getNoteDetails(noteId: number) {
    this.route.params.subscribe((params:any)=>{
      this.projectId = params.projectId;
      this.inquiryId = params.inquiryId;
      this.isGi = this.inquiryId ? true : false;
    })
    if(this.isGi) {
      this.virtualDesktopService.getGiNoteDetailsById(noteId, this.inquiryId).then(
        (response) => {
          this.noteDetails = response;
          this.setFormValues(response);
        },
        (error: any) => {
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      );
      return;
    }
    this.virtualDesktopService
      .getNoteDetailsById(noteId, this.projectId)
      .then((response) => {
        this.noteDetails = response;
        console.log(this.noteDetails);
        
        this.setFormValues(response);
        if(this.noteDetails.actionType =="19"){          
      let splitArray =    this.noteDetails.actionNote.toString().split("|");
      this.litHoldStartString = splitArray[0];
      this.litHoldEndString = splitArray[1];
      let hasEndDate = this.litHoldEndString.split('=');  

      console.log(hasEndDate[1].trim().length);
        
      if(hasEndDate[1].trim().length < 1){
        console.log('here');
        
        this.litHoldHasEndDate = false;
      }}
      }, 
      (error: any) =>{
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;  
      }
  )

  }

  setFormValues(formValues: any) {
    this.notesFormGroup.controls.comments.setValue(
      formValues.comments ? formValues.comments : ''
    );
    this.notesFormGroup.updateValueAndValidity();

    this.maxNote = 301;
    this.cdr.detectChanges();
    this.maxNote = 300;
  }

  initiateForm() {
    this.notesFormGroup = this.formBuilder.group({
      comments: [''],
    });
    if(this.isReadOnly){
      this.notesFormGroup?.get('comments')?.disable()
    }
  }

  onInputChange(event: string) {
    this.notesFormGroup.patchValue({ comments: event });
    this.notesFormGroup.updateValueAndValidity();
  }

  // onFormSubmit() {
  //   this.submitted = true;
  //   if (this.notesFormGroup.valid) {
  //     const payload = {
  //       comments: this.notesFormGroup.get('comments')?.value,
  //       projectNoteId: '',
  //     };
  //   }
  // }
  onFormSubmit() {
    this.submitted = true;
    console.log(this.noteDetails,'payload0')
    if (this.notesFormGroup.valid) {
      let formData = this.notesFormGroup.value;

      this.noteDetails.comments = formData.comments
        ? formData.comments?.trim()
        : '';
      console.log(this.noteDetails,'payload');
      
      if(this.isGi) {
        delete this.noteDetails.updatedBy;
        delete this.noteDetails.updatedDate;
        delete this.noteDetails.actionTypeDesc;
        delete this.noteDetails.createDate;
        delete this.noteDetails.systemGenerated;
        this.virtualDesktopService.updateGiNote(this.noteDetails, this.inquiryId).subscribe((response) => {
          console.log('success')
          this.closeModal('program');
          this.noteDetails = {};
        }, 
        (error: any) =>{
        this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;  
        }
        );
      }
      else {      
        this.virtualDesktopService.updateNote(this.noteDetails, this.projectId).subscribe((response) => {
          console.log('success')
          this.closeModal('program');
          this.noteDetails = {};
        }, 
        (error: any) =>{
        this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;  
        }
    );
      }
    }
  }

  closeModal(flag?:string) {
    if(flag==='manual' && this.notesFormGroup.dirty){
      this.pendingPopup.open();
    }else{
      this.onClose.emit(true);
    }
   
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
}

import { Component, ElementRef, EventEmitter, HostListener, Input, OnInit, Output, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { DocumentService } from 'src/app/@shared/services/documentService';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { RequiredDocsService } from 'src/app/@shared/services/required-docs.service';
import { AuthService } from 'src/app/core/auth/auth.service';

@Component({
  selector: 'app-add-applicant-communications',
  templateUrl: './add-applicant-communications.component.html',
  styleUrls: ['./add-applicant-communications.component.scss']
})
export class AddApplicantCommunicationsComponent implements OnInit {

  noticeSubTypeId: any;
  noticeDocType: any;
  noticeSubDocTypes: any;
  analystEmail!: string;
  documentTypes: any;
  documentSubTypes: any;
  showOtherDirections: boolean = false;
  requestedDocs: any = [];
  @Input() documents: any;
  @Input() existingRequestedDocs: any;
  @Input() projectId: any;
  SEQRdocSubTypes : string [] = [];
  SHPAdocSubTypes : string [] =[];
  addButtonDisabled: boolean = true;
  @Output() onClose =  new EventEmitter<{status: boolean, isSubmitted: boolean, emailInfo: any}>();

  form!: UntypedFormGroup;
  submitted: boolean = false;
  errorMsgObj: any = {};
  @ViewChild('pendingPopup', { static: true })
  pendingPopup!: PendingChangesPopupComponent;

  showServerError: boolean = false;
  serverErrorMessage!: string;
  showDuplicateDocNameError: boolean = false;
  showDocNameRequiredError: boolean = false;
  showTypeRequiredError: boolean = false;
  showOtherRequiredError: boolean = false;
  showIncompleteEntriesError: boolean = false;
  showDocInputErrors: boolean = false;
  showAddDocError: boolean = false;
  showDocRequiredError: boolean = false;
  showRequestDocsSection: boolean = false;
  addButtonClicked: boolean = false;

  sortByColumn: string = '';
  sortDescending: boolean = true;
  columnWithBorder: string = '';
  @ViewChild('header')
  header!: ElementRef;
  deleteIsClicked: Subject<boolean> = new Subject();
  selectedIndexForDelete: any;
  confirmDeleteBodyText: any;

  correspondenceType = 'Correspondence';
  requestType = 'Request for Additional Information';
  noticeType = 'Application Notice';

  constructor(private fb: UntypedFormBuilder, 
    private docService: DocumentService,
    private requiredDocsService: RequiredDocsService,
    private commonService: CommonService,
    private errorService: ErrorService,
    private authService: AuthService
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.analystEmail = this.authService.getUserInfo().upn;
    this.getAllErrorMsgs();
    this.getDocumentTypes();
  }

  getDocumentTypes() {
    this.docService.getDocumentTypes().then((response) => {
      let types = response['en-US'];
      const sortedKeys = Object.keys(types).sort((a, b) => {
        return types[a].docTypeDesc < types[b].docTypeDesc ? -1 : 1;
      });

      this.documentTypes = [];
      sortedKeys.forEach((value) => {
        this.documentTypes.push(types[value]);
      });
      this.documentTypes.forEach((docType: any) => {
        console.log(docType);
        if(docType.docTypeId == 3) {
          this.noticeDocType = docType;
        }
      });
      console.log(this.noticeDocType);
      this.noticeSubDocTypes = [];
      let noticeSubTypeIds = [96, 244, 51, 52, 282, 281]
      this.noticeSubDocTypes = this.noticeDocType.docSubTypes.filter((docSubType: any) => 
        noticeSubTypeIds.includes(docSubType.subTypeId));
      console.log(this.noticeSubDocTypes);
      console.log(this.documentTypes);

    });
  }

  async getAllErrorMsgs() {
    try {
      this.commonService.emitErrorMessages.subscribe((val)=>{
        if(val)this.errorMsgObj=this.commonService.getErrorMsgsObj();
      })
    } catch (error: any) {
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;  

    }
  }

  initForm() {
    this.form = this.fb.group({
      subject: ['', Validators.required],
      correspondenceType: ['', Validators.required],
        documentName: [''],
        docCategory: [''],
        docSubCategory: [''],
        otherDocSubCategory: ['']
    }
    )
    this.form.controls.docSubCategory.disable();
    this.form.controls.otherDocSubCategory.disable();
    this.documentSubTypes = [];
  }

  onCorrespondenceTypeChange(ev: any) {
    switch (this.form.controls.correspondenceType.value) {
      case this.correspondenceType:
        this.setCorrespondenceValidators();
        break;
      case this.requestType:
        this.setRequestValidators();
        break;
    }
  }

  setDocValidators() {
    this.form.controls.documentName.setValidators(Validators.required);
    this.form.controls.docCategory.setValidators(Validators.required);
  }

  clearDocValidators() {
    this.form.controls.documentName.clearValidators();
    this.form.controls.docCategory.clearValidators();
    this.form.controls.otherDocSubCategory.clearValidators();
  }

  setCorrespondenceValidators() {
    this.clearDocValidators();
    this.showRequestDocsSection = false;
    this.showDocInputErrors = false;
    this.showDocRequiredError = false;
  }

  setRequestValidators() {
    this.onDocTypeChange();
    this.showDocInputErrors = true;
    this.showRequestDocsSection = true;
  }

  onDocTypeChange() {
    console.log('doc changed', this.documentTypes);
    
    let typeValue = this.form.controls['docCategory'].value;
    if(typeValue) {
      this.setDocValidators();
    }
    if(typeValue === 10){
      this.showOtherDirections = true;
      this.form.controls.otherDocSubCategory.setValidators(Validators.required);
    }else{
      this.form.controls.otherDocSubCategory.clearValidators();
      this.showOtherDirections = false;
    }    
    console.log('doc types', this.documentTypes);
    
    this.documentSubTypes = this.documentTypes.find(
      (docType: { docTypeId: any; }) => docType.docTypeId === this.form.controls.docCategory.value
    )?.docSubTypes
      ? this.documentTypes.find(
          (docType: { docTypeId: any; }) =>
            docType.docTypeId === this.form.controls.docCategory.value
        ).docSubTypes
      : [];
      console.log(this.documentSubTypes);
      
    this.form.controls.otherDocSubCategory[
      this.form.controls.docCategory.value == 10 ? 'enable' : 'disable'
    ]();
    this.form.controls.docSubCategory[
      this.form.controls.docCategory.value == 10 ||
      this.documentSubTypes.length == 0
        ? 'disable'
        : 'enable'
    ]();

    if (this.form.controls.docCategory.value != 10) {
      this.form.controls.otherDocSubCategory.setValue('');
    }
    this.form.controls.docSubCategory.setValue('0');
    this.checkInputs();
  }

  checkInputs() {
    if(this.form.controls.documentName.value || this.form.controls.docCategory.value) {
      this.setDocValidators();
    }
    else {
      if(!this.showAddDocError) {
        this.clearDocValidators();
      }
    }
    
  }

  docInputsEmpty() {
    return !this.form.controls.documentName.value &&
           !this.form.controls.docCategory.value;
  }

  addDocument() {
    if(!this.addButtonClicked) {
      this.addButtonClicked = true;
      return;
    }
    this.showAddDocError = true;
    this.setDocValidators();
    const docName = this.form.controls.documentName.value;
    if(!docName) {
      this.form.controls.documentName.setErrors({'required':true});
    }
    if(!this.form.controls.docCategory.value) {
      this.form.controls.docCategory.setErrors({'required':true});
    }
    if(!docName || !this.form.controls.docCategory.value || this.form.controls.otherDocSubCategory.errors?.required) {
      return;
    }
    if(this.documents.documentNames.includes(docName) || 
      this.requestedDocs.find((doc: any) => doc.name === docName) ||
      this.existingRequestedDocs.find((doc: any) => doc.documentName === docName)) {
      console.log('Duplicate doc name ');
      this.showDuplicateDocNameError = true;
      return;
    }
    this.showDuplicateDocNameError = false;
    let docTypeDesc = this.documentTypes.find(
      (docType: { docTypeId: any; }) => docType.docTypeId === this.form.controls.docCategory.value
    )?.docTypeDesc;
    let subTypeDesc = this.showOtherDirections ? this.form.controls.otherDocSubCategory.value
                      : this.documentSubTypes.find(
      (docSubType: any) => docSubType.subTypeId === this.form.controls.docSubCategory.value
    )?.subTypeDesc;

    subTypeDesc = subTypeDesc ? subTypeDesc : '';
    let docSubCategory = this.form.controls.docSubCategory.value;
    if(!docSubCategory || docSubCategory == "0") {
      docSubCategory = 1;
    }
    this.requestedDocs.push({
      name: docName,
      typeDesc: docTypeDesc,
      subTypeDesc: subTypeDesc,
      category: this.form.controls.docCategory.value,
      subCategory: docSubCategory,
      otherSubCategory: this.form.controls.otherDocSubCategory.value
    });
    this.requestedDocs.sort((a: any, b: any) => (a.name > b.name ? 1 : -1));
    this.showDocRequiredError = false;
    this.clearDocInputs();
  }

  onDeleteClicked(index: number) {
    this.selectedIndexForDelete = index;
    this.confirmDeleteBodyText = 'Are you sure you want to delete the '.concat(this.requestedDocs[index].name).concat(' file?');
    this.deleteIsClicked.next(true);
  }

  deleteDocument() {
    this.requestedDocs.splice(this.selectedIndexForDelete, 1);
  }

  sortColumn(column: string) {
    this.columnWithBorder = column;
    if(column !== this.sortByColumn) {
      this.sortByColumn = column;
      this.sortDescending = true;
    }
    this.requestedDocs.sort((doc1: any, doc2: any) => {
      if(this.sortDescending) {
        return doc1[column] < doc2[column] ? -1 : 1;
      }
      else {
        return doc1[column] > doc2[column] ? -1 : 1;
      }
    });
    this.sortDescending = !this.sortDescending;
  }

  @HostListener('document:click', ['$event'])
  onClick(event: Event) {
    if(this.header && !this.header.nativeElement.contains(event.target)) {
      this.columnWithBorder = '';
    }
  }

  onFormSubmit() {
    this.submitted = true;
    if(this.form.controls.correspondenceType.value === this.requestType) {
      this.showAddDocError = true;
      this.showDocRequiredError = (this.requestedDocs.length == 0);
      if(!this.form.controls.documentName.value && !this.form.controls.docCategory.value) {
        this.clearDocValidators();
        this.form.controls.documentName.setErrors(null);
        this.form.controls.docCategory.setErrors(null);
      }
    }
    //TODO: go to email correspondence
    if(this.form.valid && 
      !this.showDocRequiredError) {
      const emailInfo = {
        subject: this.form.controls.subject.value,
        requestedDocs: this.requestedDocs,
        fromEmail: this.analystEmail,
        bodyMessage: this.composeExistingBodyMessage(),
        emailPurpose: this.form.controls.correspondenceType.value
      }
      this.onClose.emit({status: true, isSubmitted: true, emailInfo: emailInfo});
    }
  }

  private composeExistingBodyMessage() {
    if(this.form.controls.correspondenceType.value===this.correspondenceType) {
      return [''];
    }
    // else if (this.form.controls.correspondenceType.value === this.noticeType) {
    //   return [`In regards to your submitted application for project ${this.projectId}, please see attached`];
    // }
    let bodyMessage = ['The assigned DEC project manager needs more information to process your application(s).'];
    this.requestedDocs.forEach((doc: any) => {
      bodyMessage.push(doc.name);
    });
    bodyMessage.push('Please log into the eTrack applicant portal to view.');
    return bodyMessage;
  }

  closeModal(value? : boolean) {
    if(this.form.dirty) {
      this.openPendingPopup();
    }
    else {
      this.onClose.emit({status: value ? true : false, isSubmitted: false, emailInfo: ''});
    }
  }

  openPendingPopup() {
    this.pendingPopup.open();
  }

  goBack() {
    this.onClose.emit({status: false, isSubmitted: false, emailInfo: ''});
  }

  private clearDocInputs() {
    this.form.controls.documentName.setValue('');
    this.form.controls.docCategory.setValue('');
    this.form.controls.docSubCategory.setValue('');
    this.form.controls.otherDocSubCategory.setValue('');
    this.form.controls.docSubCategory.disable();   
    this.form.controls.otherDocSubCategory.disable(); 
    this.documentSubTypes = [];
    this.showAddDocError = false;
    this.clearDocValidators();
  }

}

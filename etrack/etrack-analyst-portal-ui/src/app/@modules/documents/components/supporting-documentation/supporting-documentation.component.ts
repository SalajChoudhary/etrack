import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  HostListener,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import {
  UntypedFormArray,
  UntypedFormBuilder,
  FormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { isEqual, get, last, isEmpty } from 'lodash';
import { fromEvent, Subject } from 'rxjs';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { RequiredDocsService } from 'src/app/@shared/services/required-docs.service';
import { Utils } from 'src/app/@shared/services/utils';
import { WindowRef } from 'src/app/@shared/services/windowRef';
import { DocumentService } from 'src/app/@shared/services/documentService';
import { takeUntil } from 'rxjs/operators';
import { ModalConfig } from 'src/app/modal.config';
import { ErrorService } from 'src/app/@shared/services/errorService';

@Component({
  selector: 'app-supporting-documentation',
  templateUrl: './supporting-documentation.component.html',
  styleUrls: ['./supporting-documentation.component.scss'],
})
export class SupportingDocumentationComponent implements OnInit {
  supportingDocumentsFormGroup!: UntypedFormGroup;
  relatedDocumentsFormArray!: UntypedFormArray;
  requiredDocumentsFormArray!: UntypedFormArray;
  secondFormGroup!: UntypedFormGroup;
  isEditable = false;
  permits: any[] = [];
  selectedIndex: any = 0;
  requiredDocs!: any[];
  relatedDocs!: any[];
  documentTitleId: string = '';
  documentDescription: string = '';
  docCategory: string = '';
  docSubCategory: string = '';
  documentId: any = '';
  @ViewChild('informationModal') informationModal!: CustomModalPopupComponent;
  @ViewChild('missingDocsPopup', { static: true })
  missingDocsPopup!: CustomModalPopupComponent;
  modalConfig!: any;
  deleteIsClicked: Subject<boolean> = new Subject();
  multipleDeleteIsClicked: Subject<boolean> = new Subject();
  viewButtonIsClicked: Subject<string> = new Subject();
  uploaded = 'uploaded';
  notUploaded = 'not uploaded';
  selectedPermitTypes!: string;
  permitsObjList: any[] = [];
  fullData: any = [];
  deleteSingleFiles: Subject<boolean> = new Subject();
  deleteMultipleFiles: Subject<any[]> = new Subject();
  currentDoc: any = {};
  listIndicator: string = '';
  existingRefDocumentId: any;
  singleDeletePopupText: string =
    'Are you sure you want to delete the following file?';
  multipleDeletePopupText: string =
    'Please select the files you would like to delete from the list below:';
  deletePopupCloseClicked: Subject<string> = new Subject();
  supportingDocumentationValidated: boolean = false;
  mode: any = localStorage.getItem('mode');
  currentDocumentFileList: any[] = [];
  missingRequiredDocuments: any[] = [];
  private unsubscriber: Subject<void> = new Subject<void>();
  //Tie this indicator to back-end Indicator
  isEA: boolean = false;
  SEQRDocs: any[] = [];
  SHPADocs: any[] = [];
  //Tie this to back-end indicator
  //SEQR Values: Y, N and NS - 1, 2 and 3 respectively
  isSEQR: boolean = false;
  showRelatedRequiredInfo = false;
  showSEQRInfo: boolean = false;
  showSHPAInfo: boolean = false;
  deleteListInd!: string;
  documentTitle: any;
  areDocumentsPresent = false;
  hideAlreadyUploadedButton = false;
  showServerError = false;
  serverErrorMessage! : string;
  showRequiredDocuments : boolean = true;
  signList: any;
  projectNotes : any;
  deletedDocName! : string;
  isViewDocument = false;
  projectId!: string;
  noneReceived = "None Received";
  systemParameters:any;

  get isValidate() {
    return this.mode == 'validate';
  }
  get isReadonly() {
    return this.mode == 'read' || this.supportingDocumentationValidated;
  }
notesFormGroup!:UntypedFormGroup;
  constructor(
    public commonService: CommonService,
    private projectService: ProjectService,
    private router: Router,
    private modalService: NgbModal,
    private requiredDocsService: RequiredDocsService,
    private winRef: WindowRef,
    public utils: Utils,
    private fb:UntypedFormBuilder,
    private errorService: ErrorService
  ) {
    if(this.isValidate){
      this.notesFormGroup = this.fb.group({
        comments: [''],
      });
    }
  }

  //SEQR Values: Y, N and NS - 1, 2 and 3 respectively

  ngOnInit(): void {
    this.commonService.getSystemParameters().subscribe(data=>{
      this.systemParameters=data;
    });
   let param  = this.router.url.split('/').pop();
   if(param == 'view'){
    this.isViewDocument = true;
   }
   this.projectId = localStorage.getItem("projectId")!;
   
    this.projectService.getRequiredAndRelatedDocs().then((response) => {
      let permitsAbbrev: any[] = [];
      if(this.isViewDocument){
        this.relatedDocs = response.relatedDoc?.filter((doc:any) => doc.uploadInd =="Y");
        this.requiredDocs = response.requiredDoc?.filter((doc:any) => doc.uploadInd =="Y");
        this.SHPADocs = response?.shpaDoc?.filter((doc:any) => doc.uploadInd =="Y");
        this.SEQRDocs = response?.seqrDoc?.filter((doc:any) => doc.uploadInd =="Y");

      }else{
        this.relatedDocs = response.relatedDoc;      
        this.requiredDocs = response.requiredDoc;      
        this.SEQRDocs = get(response, 'seqrDoc', []);
        this.SHPADocs = get(response, 'shpaDoc', []);
        let docsObj = {
         related : this.relatedDocs,
         required :  this.requiredDocs,
         seqr : this.SEQRDocs,
         shpa : this.SHPADocs
        }
        this.requiredDocsService.setStepFourDocs(docsObj);
        let otherObj = { 
          documentTitle: 'Other, not listed',
          description: null,
          uploadInd: 'N',
          documentId: null
        }
         this.relatedDocs.push(otherObj);
         this.SEQRDocs?.push(otherObj);
         this.SHPADocs?.push(otherObj);
      }     
            
      if (response.eaInd === 1) {
        this.isEA = true;
      }
      if (response.seqrInd == '2' || response.seqrInd == '3') {        
        this.isSEQR = true;
      }
      //SEQR Values: Y, N and NS - 1, 2 and 3 respectively

      this.supportingDocumentationValidated = isEqual(
        get(response, 'validatedInd', 'N'),
        'Y'
      );  

      let keys = Object.keys(response.permitTypes);
      keys.forEach((key) => {
        this.permitsObjList.push(response.permitTypes[key]);
        permitsAbbrev.push(response.permitTypes[key].permitTypeDesc);
      });
      this.permitsObjList.sort((a: any, b: any) => {
        var textA = a.permitTypeDesc.toUpperCase();
        var textB = b.permitTypeDesc.toUpperCase();
        return textA < textB ? -1 : textA > textB ? 1 : 0;
      });
      this.selectedPermitTypes = permitsAbbrev.join(', ');
    }, 
    (error: any) =>{
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;  
    }
  
);
this.signList=[];
this.getSignList();

    this.modalConfig = {
      title: '',
      showHeader: false,
    };
    this.deleteSingleFiles.subscribe((data: any) => {
      this.deleteReference();
    });

    this.deleteMultipleFiles.subscribe((filesList: any[]) => {
      this.deleteMultipleFile(filesList);
    });
    this.deletePopupCloseClicked.subscribe((data) => {
      this.onDeleteCancelClicked();
    });

    this.requiredDocsService.getDeleteListInd().subscribe((ind) => {
      this.deleteListInd = ind; 
    });

    this.projectService.getAlreadyUploadedData().then(data =>{
      if(isEmpty(data)){
        this.hideAlreadyUploadedButton = true;
      }else{
        this.areDocumentsPresent = true;
      } 
    }, 
    (error: any) =>{
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;  
    }
);

    //WE need to get the SEQR indicator here
    //jthis.isSEQR = this.getIsSEQR;

    //diables browswers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });      
  }

  onFormSubmit() {
    if (this.requiredDocs) {
      this.requiredDocs.forEach((doc) => {
        if (doc.uploadInd === 'N') this.missingRequiredDocuments.push(doc);
      });
      if (this.missingRequiredDocuments.length > 0) {
        this.missingDocsPopup.open('missing');
      } else {
        //Make api call to get checkmark for step 4
        this.projectService.supportingDocNextCall().then((data: any) => {
          this.commonService.navigateToMainPage();
        });
      }
    } else {
      this.projectService.supportingDocNextCall().then((data: any) => {
        this.commonService.navigateToMainPage();
      });
    }
  }

  onSkipClicked() {
    this.missingDocsPopup.close();
    this.missingRequiredDocuments = [];
    this.projectService.supportingDocNextCall().then((data: any) => {
      this.commonService.navigateToMainPage();
    });
  }

  onViewButtonClicked(doc: any) {    
    this.requiredDocsService
      .getSupportDocumentFiles(doc.documentId)
      .then((files: any[]) => {
        this.fullData = files;
        let docName = doc.documentTitleId;
        this.viewButtonIsClicked.next(docName);
      }, 
      (error: any) =>{
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;  
      }
  );
  }

  async uploadClicked(doc: any, listInd: string, buttonInd: string) {
    this.showServerError = false;
    let filesCount = 0;
    //refactor two if statements to remove duplicates
    if (buttonInd === 'upload') {      
      doc.DisplayName = doc.documentTitle;
      this.documentTitleId = doc.documentTitleId;
      this.documentDescription = doc.description;
      this.documentId = doc.documentId;
      this.requiredDocsService.setListInd(listInd);
      this.requiredDocsService.setDocument(doc);
      this.router.navigate(['/document-upload'], {
        queryParams: { page: 'required', documentTitleId: doc.documentTitleId },
      });
    } 
    
    else if (buttonInd === 'additional') {
      await this.requiredDocsService
        .getSupportDocumentFiles(doc.documentId)
        .then((files: any[]) => {
          this.requiredDocsService.setExistingFilesArray(files);
          let docFromRes = files[0];
          if(docFromRes){
            this.docCategory = docFromRes.docCategory;
            this.docSubCategory = docFromRes.docSubCategory;
          }
          
          let fileNames: any[] = [];
          files.forEach((a) => {
            fileNames.push(a.displayName);
          });
          this.requiredDocsService.setExistingDocumentNames(fileNames);
          let sortedArray = files.sort((a: any,b : any) => a.displayName.localeCompare(b.displayName));
          let highestName = sortedArray[sortedArray.length-1].displayName;
          let lastChar : any = highestName[highestName.length -1];
          var isNumeric = ! isNaN( parseInt(lastChar));
          if(isNumeric){
            lastChar++;
            filesCount = lastChar;
          }else{
            filesCount = 1;
          }
        }, 
        (error: any) =>{
        this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;  
        }
    );
      doc.docCategory = this.docCategory;
      doc.docSubCategory = this.docSubCategory;
      doc.DisplayName = doc.documentTitle;
      this.documentTitle = doc.documentTitle.concat('-' + filesCount);
      doc.documentTitleId = this.documentTitleId?this.documentTitleId:doc.documentTitleId;
      doc.documentTitle = this.documentTitle;
      

      this.documentDescription = doc.description;
      this.documentId = doc.documentId;
      this.requiredDocsService.setListInd(listInd);
      this.requiredDocsService.setDocument(doc);

      if (listInd === 'related' && buttonInd === 'additional') {
        this.router.navigate(['/document-upload'], {
          queryParams: { page: 'required-other-Add' , documentTitleId:doc.documentTitleId },
        });
      } else {
        this.router.navigate(['/document-upload'], {
          queryParams: { page: 'required-Add', documentTitleId:doc.documentTitleId },
        });
      }
    }
  }

  setDocumentName(doc: any) {
    this.documentTitleId = doc.documentTitleId;
    this.documentTitle = doc.documentTitle;
    this.documentDescription = doc.description;
    this.documentId = doc.documentId;
  }
  getSignList() {
    this.projectService
      .getSignsData()
      .then((response) => {
        this.signList = get(response, 'reqdsigneddoc', []) //response?.length > 0 ? response : [];
        this.signList.map((x:any)=>x.isAlreadySaved=x.acknowledgeInd==='Y')
      })
      .catch((err) => {
        this.signList = [];
      this.serverErrorMessage = this.errorService.getServerMessage(err);
        this.showServerError = true;
        throw err;  
      });
  }


  yesClicked(){
    let reqddocs:any=[];
    this.missingRequiredDocuments.forEach((doc:any)=>reqddocs.push(doc.documentTitleId))
    let payload={documentTitleIds:reqddocs,reason:this.notesFormGroup.get('comments')?.value}
    this.projectService.submitMissingNote(payload).subscribe((resp)=>{
      this.onSkipClicked();
    }, 
    (error: any) =>{
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;  
    }
)
  }
  onInputChange(e: any) {
    this.notesFormGroup.get('comments')?.setValue(e);
  }

  alreadyUploadedClicked(doc: any) {
    this.setDocumentName(doc);
    this.router.navigate(['/already-uploaded'], {
      queryParams: {
        displayName: this.documentTitle,
        docId: doc.documentId,
        documentTitleId: this.documentTitleId,
        page: 'step4',
      },
    });
  }

  otherButtonClicked() {
    this.router.navigate(['/document-upload'], {
      queryParams: { page: 'other' },
    });
  }

  onDeleteClicked(doc: any, ind: string) {
    console.log('doc', doc);
    this.deletedDocName = doc.documentTitle;
    
    if (ind === 'rel') {
      this.requiredDocsService.setDeleteListInd('rel');
    }
    this.requiredDocsService.setDocument(doc);
    this.currentDoc = doc;
    this.requiredDocsService
      .getSupportDocumentFiles(doc.documentId)
      .then((files: any[]) => {
        this.currentDocumentFileList = files;
        if (this.currentDocumentFileList.length > 1) {
          this.currentDocumentFileList.sort((a: any, b: any) =>
            a.displayName.localeCompare(b.displayName)
          );
          this.multipleDeleteIsClicked.next(true);
        } else {
          this.deleteIsClicked.next(true);
        }
      }, 
      (error: any) =>{
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;  
      }
  );
  }

  async deleteReference() { 
    let docId: string[] = [];
    this.requiredDocsService.getDocument().subscribe((doc) => {
      docId = doc.documentId;
      console.log('doc', doc);
      
    });
    this.currentDoc.description = '';
    await this.requiredDocsService
      .deleteSupportDocumentFile(docId)
      .then((data) => {
        if (this.deleteListInd === 'rel') {
          let result: any[] = [];
          this.relatedDocs.forEach((doc) => {
            if (doc.documentId !== this.currentDoc.documentId) {
              result.push(doc);
            }
          });
          this.relatedDocs = result;
        }
      }, 
      (error: any) =>{
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;  
      }
  );
    this.currentDoc.uploadInd = 'N';
    this.currentDoc.refDocumentDesc = "";
    this.ngOnInit();
  }
  async deleteMultipleFile(docIdList: any[]) {
    await this.requiredDocsService.deleteSupportDocumentFile(docIdList);
     this.ngOnInit();
  }

  onDeleteCancelClicked() {
    this.requiredDocsService.setDeleteListInd('');
    this.requiredDocsService.setDocument({});
  }

  onCloseClicked() {
    this.commonService.navigateToMainPage();
  }

  isChecked(doc: any) {
   if(this.requiredDocs){
    this.showRequiredDocuments = this.requiredDocs.filter(e=>e.uploadInd === "N").length === 0 ? false : true;
   }
    if (doc.uploadInd === 'Y') {
      return true;
    }
    return false;
  }

  showButtons(index: any, docList: any[], indicator: string) {
    if(this.isViewDocument){
      return false;
    }
    if (docList) {
      if (docList[index]?.documentTitle === 'Other, not listed') {
        return false;
      }
      if (indicator === this.uploaded) {
        //Delete, View, add additional
        if (docList[index]?.uploadInd === 'N') return false;
        return true;
      } else if (indicator === this.notUploaded) {
        //Upload and Reference Location
        if (docList[index]?.uploadInd === 'N') return true;
        return false;
      }
    }
  }

  onOtherClicked(listInd: string) {
    if(listInd == 'SEQR' || listInd == 'SHPA'){
      this.requiredDocsService.setListInd(listInd=='SEQR'? 'seqr':'shpa');
      this.requiredDocsService.setDocName(listInd);
      this.router.navigate(['/document-upload'], {
        queryParams: { page: 'other', isSEQR : 'true' },
      });
    }else{
      this.requiredDocsService.setListInd(listInd);
      this.router.navigate(['/document-upload'], {
        queryParams: { page: 'other' },
      });
   }


  //  this.router.navigate(['/document-upload'], {
  //   queryParams: { page: 'other' },modalConfig
  // });
  }

  onInfoClicked(indicator: string) {
    if (indicator === '1') {
      this.showRelatedRequiredInfo = true;
      this.informationModal.open('info');
    } else if (indicator === '2') {
      this.showSEQRInfo = true;
      this.informationModal.open('info');
    } else {
      this.showSHPAInfo = true;
      this.informationModal.open('info');
    }
  }

  onInfoCloseClicked() {
    this.clearInfoConditions();
    this.modalService.dismissAll();
  }

  onViewDocumentPopupFileClicked(event: any) {
    console.clear();
    let win = this.winRef.nativeWindow;
    let downloadable = [
      'accdb',
      'docx',
      'xlsx',
      'vsd',
      'vsdx',
      'rtf',
      'xls',
      'mdb',
      'doc',
      'eml',
      'mbox',
      'msg',
      'ppt',
      'pptx',
      'rtf',
      'shp',
      'tif',
      'zip',
    ];
    const file = event.fileName;
    console.log('file', file);
    let newTab = !downloadable.includes(file.split('.')[file.split('.').length - 1]) ? 
      win.open() : null;
    if (!downloadable.includes(file.split('.')[file.split('.').length - 1]))
    newTab.document.write(`<html>
      <head><title>${file}</title></head>
      <body style="margin: 0; padding: 0"> <span id="sm1">Retrieving file content...</span>`);
      // newTab.document.write('Retrieving file content...');
    this.utils.emitLoadingEmitter(true);
    this.requiredDocsService
      .retrieveFileContent(event.fileName, event.documentId, this.projectId)
      .then((res: any) => {
        this.utils.emitLoadingEmitter(false);
        console.log('here', res);
        if (!res) {
          
          //this.modalReference.close('no_data');
          return;
        }

        if (!downloadable.includes(file.split('.')[file.split('.').length - 1])){
          //newTab.location.href = win.URL.createObjectURL(res);
          let docUrl = win.URL.createObjectURL(res);
          newTab.document.write(`
          <iframe src="${docUrl}" style="width: 100%; height: 100%; margin: 0; padding: 0; border: none;">
          </iframe></body>
          <script>
            document.getElementById("sm1").innerHTML = "";
          </script>
          </html>`);
        }
        else this.saveFiles(file, res);
      }, 
      (error: any) =>{
        this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;  
      }
  );
  }
  private async saveFiles(fileName: string, blob: Blob) {
    var link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.title=fileName;
    link.download = fileName;
    link.click();
  }

  returnToListClicked() {
    this.missingDocsPopup.close();
    this.missingRequiredDocuments = [];
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }

  clearInfoConditions() {
    this.showRelatedRequiredInfo = false;
    this.showSEQRInfo = false;
    this.showSHPAInfo = false;
  }
  isEmpty(array: any[]){
    if (array === undefined || array === null || array?.length == 0) {
      return true;
  }
  return false;
  }
}

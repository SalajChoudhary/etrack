import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { isEqual, get,isEmpty } from 'lodash';
import { fromEvent, Subject } from 'rxjs';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { Utils } from 'src/app/@shared/services/utils';
import { WindowRef } from 'src/app/@shared/services/windowRef';
import { takeUntil } from 'rxjs/operators';
import { InquiryService } from 'src/app/@shared/services/inquiryService';
import { FormArray, FormBuilder, FormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { GisService } from 'src/app/@shared/services/gisService';
import { SIProject } from 'src/app/@store/models/siProject';

@Component({
  selector: 'app-inquiry-documentation',
  templateUrl: './inquiry-documentation.component.html',
  styleUrls: ['./inquiry-documentation.component.scss'],
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { displayDefaultIndicatorType: false },
    },
  ],
})
export class InquiryDocumentationComponent implements OnInit {
  inquiryDocumentsFormGroup!: FormGroup;
  relatedDocumentsFormArray!: FormArray;
  requiredDocumentsFormArray!: FormArray;
  secondFormGroup!: FormGroup;
  isEditable = false;
  selectedIndex: any = 0;
  requiredDocs!: any[];
  relatedDocs!: any[];
  documentTitle: string = '';
  documentTitleId: string = '';
  documentDescription: string = '';
  docCategory: string = '';
  docSubCategory: string = '';
  documentId: any = '';
  @ViewChild('informationModal') informationModal!: CustomModalPopupComponent;
  @ViewChild('missingDocsPopup', { static: true }) missingDocsPopup!: CustomModalPopupComponent;
  modalConfig!: any;
  deleteIsClicked: Subject<boolean> = new Subject();
  multipleDeleteIsClicked: Subject<boolean> = new Subject();
  viewButtonIsClicked: Subject<string> = new Subject();
  uploaded = 'uploaded';
  notUploaded = 'not uploaded';
  fullData: any = [];
  deleteSingleFiles: Subject<boolean> = new Subject();
  deleteMultipleFiles: Subject<any[]> = new Subject();
  currentDoc: any = {};
  listIndicator: string = '';
  supportDocRefId: any;
  singleDeletePopupText: string ='Are you sure you want to permanently delete the selected item?';
  multipleDeletePopupText: string ='Please select the files you would like to delete from the list below:';
  deletePopupCloseClicked: Subject<string> = new Subject();
  inquiryDocumentationValidated: boolean = false;
  mode: any = localStorage.getItem('mode');
  currentDocumentFileList: any[] = [];
  missingRequiredDocuments: any[] = [];
  private unSubscriber: Subject<void> = new Subject<void>();
  isEA: boolean = false;
  isSEQR:boolean =false;
  SEQRDocs: any[] = [];
  SHPADocs: any[] = [];
  showRelatedRequiredInfo = false;
  showSEQRInfo: boolean = false;
  showSHPAInfo: boolean = false;
  hideAlreadyUploadedButton:boolean=false;
  areDocumentsPresent:boolean=false;
  showServerError = false;
  serverErrorMessage! : string;
  showRequiredDocuments : boolean = true;
  deleteListInd!: string;
  deletedDocName! : string;
  serviceError: boolean = false;
  serviceErrorMessage: string = '';
  siProject!: SIProject;
  analystResponse!: FormGroup;
  systemParameters:any;
  noneReceived = "None Received";
  isViewDocument = false;

  constructor(
    public commonService: CommonService,
    private inquiryService: InquiryService,
    private router: Router,
    private modalService: NgbModal,
    private winRef: WindowRef,
    public utils: Utils,
    private fb:UntypedFormBuilder,
    private errorService: ErrorService,
    private gisService: GisService,
    private _formBuilder: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.commonService.getSystemParameters().subscribe(data=>{
      this.systemParameters=data;
    });
    let param  = this.router.url.split('/').pop();
    if(param == 'view'){
     this.isViewDocument = true;
    }
    this.initializeform();
    let inquiryId = localStorage.getItem('inquiryId');
    if (
      inquiryId !== undefined &&
      inquiryId !== null &&
      inquiryId !== '' &&
      inquiryId !== '0'
    ) {
      this.utils.emitLoadingEmitter(true);
      this.gisService.getInquiryProject(inquiryId).subscribe(
        (data: any) => {
          this.siProject = data as SIProject;
          localStorage.setItem('inquiryCategoryCode', data.reason);
          this.analystResponse.controls.response_sent.setValue(this.siProject.response);
          this.analystResponse.controls.response_date.setValue(this.siProject.responseDate);
          console.log(this.analystResponse)
          this.utils.emitLoadingEmitter(false);
        },
        (error: any) => {
          this.handleError(error, 'Unable to retrieve inquiry project');
        }
      );
    }
    this.inquiryService.getRequiredAndRelatedDocs().then((response) => {
      if(this.isViewDocument){
        this.relatedDocs = response.relatedDoc?.filter((doc:any) => doc.uploadInd =="Y");
        this.requiredDocs = response.requiredDoc?.filter((doc:any) => doc.uploadInd =="Y");
        this.SHPADocs = response?.shpaDoc?.filter((doc:any) => doc.uploadInd =="Y");
        this.SEQRDocs = response?.seqrDoc?.filter((doc:any) => doc.uploadInd =="Y");
      }else{
        this.relatedDocs = response.relatedDoc !==null ? response.relatedDoc:[];
        this.requiredDocs = response.requiredDoc;
        this.SEQRDocs = get(response, 'seqrDoc', []);
        this.SHPADocs = get(response, 'shpaDoc', []);
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
    },
    (error: any) =>{
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;
    });

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

    this.inquiryService.getDeleteListInd().subscribe((ind) => {
      this.deleteListInd = ind;
    });
    this.inquiryService.getAlreadyUploadedData().then(data =>{
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
    });

    //disables browsers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unSubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }


  private initializeform(){
    this.analystResponse = this._formBuilder.group({
      complete:[''],
      response_sent:[''],
      response_date:['']
    });
  }

  private handleError(error: any, message?: string) {
    this.utils.emitLoadingEmitter(false);
    this.serviceError = true;
    if (message !== undefined) {
      this.serviceErrorMessage = 'Failed to save or Update Inquiry Project';
    } else {
      this.serviceErrorMessage = error.error;
    }
  }

  onFormSubmit() {
    if (this.requiredDocs) {
      this.requiredDocs.forEach((doc) => {
        if (doc.uploadInd === 'N') this.missingRequiredDocuments.push(doc);
      });
      if (this.missingRequiredDocuments.length > 0) {
        this.missingDocsPopup.open('missing');
      } else {
        this.router.navigate(['/apply-for-inquiry']);
      }
    } else {
      this.router.navigate(['/apply-for-inquiry']);
    }
  }

  async onSkipClicked() {
    this.missingDocsPopup.close();
    this.missingRequiredDocuments = [];
    this.utils.emitLoadingEmitter(true);
    await this.inquiryService.inquiryDocSkipCall().then((data: any) => {
      this.utils.emitLoadingEmitter(false);
      this.router.navigate(['/apply-for-inquiry']);
    }, (error: any) =>{
        this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      });

  }

  onViewButtonClicked(doc: any) {
    console.log(doc);
    this.inquiryService
      .getInquiryDocumentFiles(doc.documentId)
      .then((files: any[]) => {
        this.fullData = files;
        let docName = doc.documentTitle;
        this.viewButtonIsClicked.next(docName);
      });
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
      this.inquiryService.setListInd(listInd);
      this.inquiryService.setDocument(doc);
      this.router.navigate(['/document-upload'], {
        queryParams: { page: 'required', documentTitleId: doc.documentTitleId, otherPageFrom:'inquiry'},
      });
    }

    else if (buttonInd === 'additional') {
      await this.inquiryService
        .getInquiryDocumentFiles(doc.documentId)
        .then((files: any[]) => {
          this.inquiryService.setExistingFilesArray(files);
          let docFromRes = files[0];
          if(docFromRes){
            this.docCategory = docFromRes.docCategory;
            this.docSubCategory = docFromRes.docSubCategory;
          }
          let fileNames: any[] = [];
          files.forEach((a) => {
            fileNames.push(a.displayName);
          });
          this.inquiryService.setExistingDocumentNames(fileNames);
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
      this.inquiryService.setListInd(listInd);
      this.inquiryService.setDocument(doc);

      if (listInd === 'related' && buttonInd === 'additional') {
        this.router.navigate(['/document-upload'], {
          queryParams: { page: 'required-other-Add',otherPageFrom:'inquiry' },
        });
      } else {
        this.router.navigate(['/document-upload'], {
          queryParams: { page: 'required-Add', documentTitleId:doc.documentTitleId ,otherPageFrom:'inquiry' },
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

  alreadyUploadedClicked(doc: any) {
    this.setDocumentName(doc);
    this.router.navigate(['/already-uploaded'], {
      queryParams: {
        displayName: this.documentTitle,
        documentTitleId:doc.documentTitleId,
        docId: doc.documentId,
        page: 'inquiry',
      },
    });
  }

  otherButtonClicked() {
    this.router.navigate(['/document-upload'], {
      queryParams: { page: 'other',otherPageFrom:'inquiry' },
    });
  }

  onDeleteClicked(doc: any, ind: string) {
    console.log('doc', doc);
    this.deletedDocName = doc.documentTitle;

    if (ind === 'rel') {
      this.inquiryService.setDeleteListInd('rel');
    }
    this.inquiryService.setDocument(doc);
    this.currentDoc = doc;
    this.inquiryService
      .getInquiryDocumentFiles(doc.documentId)
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
    this.inquiryService.getDocument().subscribe((doc) => {
      docId = doc.documentId;
      console.log('doc', doc);

    });
    this.currentDoc.description = '';
    await this.inquiryService
      .deleteInquiryDocumentFile(docId)
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
    await this.inquiryService.deleteInquiryDocumentFile(docIdList);
    this.ngOnInit();
  }

  onDeleteCancelClicked() {
    this.inquiryService.setDeleteListInd('');
    this.inquiryService.setDocument({});
  }

  onCloseClicked() {
    this.router.navigate(['/apply-for-inquiry']);
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
    if (docList) {
      if (docList[index].documentTitle === 'Other, not listed') {
        return false;
      }
      if (indicator === this.uploaded) {
        //Delete, View, add additional
        if (docList[index].uploadInd === 'N') return false;
        return true;
      } else if (indicator === this.notUploaded) {
        //Upload and Reference Location
        if (docList[index].uploadInd === 'N') return true;
        return false;
      }
    }
  }

  onOtherClicked(listInd: string) {
    if(listInd == 'SEQR' || listInd == 'SHPA'){
      this.inquiryService.setListInd(listInd=='SEQR'? 'seqr':'shpa');
      this.inquiryService.setDocName(listInd);
      this.router.navigate(['/document-upload'], {
        queryParams: { page: 'other', otherPageFrom:'inquiry', isSEQR : 'true' },
      });
    }else{
      this.inquiryService.setListInd(listInd);
      this.router.navigate(['/document-upload'], {
        queryParams: { page: 'other', otherPageFrom:'inquiry' },
      });
   }
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
    this.inquiryService
      .retrieveFileContent(event.fileName, event.documentId)
      .then((res: any) => {
        console.log('here', res);
        if (!res) {
          this.utils.emitLoadingEmitter(false);
          return;
        }

        let win = this.winRef.nativeWindow;
        let downloadable = [
          'accdb','docx','xlsx','vsd','vsdx','rtf','xls','mdb','doc','eml','mbox','msg','ppt','pptx','rtf','shp','tif','zip',
        ];
        const file = event.fileName;
        console.log('file', file);
        if (!downloadable.includes(file.split('.')[file.split('.').length - 1]))
          this.winRef.nativeWindow.open(win.URL.createObjectURL(res), '_blank');
        else this.saveFiles(file, res);
      },
      (error: any) =>{
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      });
  }
  private async saveFiles(fileName: string, blob: Blob) {
    let link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = fileName;
    link.click();
  }

  returnToListClicked() {
    this.missingDocsPopup.close();
    this.missingRequiredDocuments = [];
  }

  ngOnDestroy(): void {
    this.unSubscriber.next();
    this.unSubscriber.complete();
  }

  clearInfoConditions() {
    this.showRelatedRequiredInfo = false;
    this.showSEQRInfo = false;
    this.showSHPAInfo = false;
  }

  public selectionChange(event: any): void {
    this.selectedIndex = event.selectedIndex;
    switch (event.selectedIndex) {
      case 0:
        break;
    }
  }
}

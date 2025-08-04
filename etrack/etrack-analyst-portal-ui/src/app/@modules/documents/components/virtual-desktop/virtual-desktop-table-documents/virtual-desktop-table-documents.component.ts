import { Component, OnInit, Input } from '@angular/core';
import { Subject } from 'rxjs';
import { WindowRef } from 'src/app/@shared/services/windowRef';
import { VirtualDesktopService } from 'src/app/@shared/services/virtual-desktop.service';
import { Utils } from 'src/app/@shared/services/utils';
import { RequiredDocsService } from 'src/app/@shared/services/required-docs.service';
import { isEmpty } from 'lodash';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { Router } from '@angular/router';
import { InquiryService } from 'src/app/@shared/services/inquiryService';

@Component({
  selector: 'app-virtual-desktop-table-documents',
  templateUrl: './virtual-desktop-table-documents.component.html',
  styleUrls: ['./virtual-desktop-table-documents.component.scss']
})
export class VirtualDesktopTableDocumentsComponent implements OnInit {

  @Input() documents:any = {}
  @Input() isReadOnly:boolean = false;
  @Input() isFromDisposed:boolean = false;
  @Input() virtualDesktopData:any = {};
  @Input() projectId:any;
  @Input() isGi: boolean = false;
  @Input() inquiryId: any;
  popUpDocumentHeader: string = '';
  showServerError = false;
  serverErrorMessage! : string;
  docContent!:string;
  supportDocuments = {
    show: false,
    documentName: new Subject,
    fileList: [],
    documentData: []
  }
  popOverDocument: string = ''; 
  showtitle :boolean =false;
  constructor(private errorService: ErrorService, private srv:VirtualDesktopService, 
    private router: Router, private winRef: WindowRef, 
    public utils: Utils, private requiredDocService: RequiredDocsService,
    private inquiryService: InquiryService) { }

  ngOnInit(): void {    
    
  }
  
  onDocumentHover() {
    if(this.virtualDesktopData.facility.decId){
      this.showtitle = true;
      this.popUpDocumentHeader = "";
      this.popOverDocument='See all Project ID ' + this.projectId + ' documents';
    }else{
      this.showtitle = false;
    this.popUpDocumentHeader = "Documents"
    this.popOverDocument = this.documents.documentNames;
    }
  }
  NavigateDM(){
    const decId = this.virtualDesktopData.facility.decId;
    if(decId){
      sessionStorage.setItem(
        'documentdecID',
        decId
      );      
   sessionStorage.setItem('documentProjectID',this.projectId);
      window.open('/documents','_blank')
    }
    //this.router.navigate(['/documents'],'_blank');
  }
  onDocumentTitleClick(document:any){
    if(this.isGi) {
      this.inquiryService.getInquiryDocumentFiles(document.documentId, this.inquiryId).then(res => {
        this.supportDocuments.show = true;
        this.supportDocuments.documentData = res;
        setTimeout(()=>{
          this.supportDocuments.fileList = res;
          this.supportDocuments.documentName.next(document.documentTitle);
        });
      })
      
      return;
    }
    this.srv.getSuppportDocumentById(document.documentId, this.projectId).subscribe((res) => {
      this.supportDocuments.show = true;
      this.supportDocuments.documentData = res;
      setTimeout(()=>{
        this.supportDocuments.fileList = res;
        this.supportDocuments.documentName.next(document.documentTitle);
      });
      }, 
      (error: any) =>{
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;  
      }
    );
  }

  onViewDocumentPopupFileClicked(event:any){
    this.showServerError = false;
    this.utils.emitLoadingEmitter(true);
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
    let newTab = !downloadable.includes(file.split('.')[file.split('.').length - 1]) ? 
      win.open('',file ) : null;
    if (!downloadable.includes(file.split('.')[file.split('.').length - 1]))
      newTab.document.write(`<html>
      <head><title>${file}</title></head>
      <body style="margin: 0; padding: 0"> <span id="sm1">Retrieving file content...</span>`);
      
    if(this.isGi) {
      this.inquiryService.retrieveFileContent(event.fileName, event.documentId, this.inquiryId).then( (res: any)=> {
        this.utils.emitLoadingEmitter(false);
        if (!res) {
          //this.modalReference.close('no_data');
          return;
        }
      
      if (!downloadable.includes(file.split('.')[file.split('.').length - 1])){
        let docUrl = win.URL.createObjectURL(res);
        newTab.document.h1Title=file;
        newTab.document.download=file;
        console.log("URL title",docUrl,newTab.document.title);
        newTab.document.write(`
        <iframe title=${file} h1Title=${file} src="${docUrl}" style="width: 100%; height: 100%; margin: 0; padding: 0; border: none;">
        <head><title>${file}</title></head>
        </iframe>
        </body>
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
      });
      return;
    }
    
    this.requiredDocService.retrieveFileContent(event.fileName,event.documentId ,this.projectId).then((res:any) =>{ 
      this.utils.emitLoadingEmitter(false);
      console.log("Rertived",res)
      if (!res) {
              //this.modalReference.close('no_data');
              return;
            }
        if (!downloadable.includes(file.split('.')[file.split('.').length - 1])){
          // newTab.location.href = win.URL.createObjectURL(res);
          let docUrl = win.URL.createObjectURL(res);
          newTab.document.write(`
          <iframe  src="${docUrl}" style="width: 100%; height: 100%; margin: 0; padding: 0; border: none;">
          <head><title>${file}</title></head>
          </iframe>
          </body>
          <script>
            document.getElementById("sm1").innerHTML = "";
          </script>
          </html>`);
        }else this.saveFiles(file, res);
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
    this.showServerError = false;
    var link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.title=fileName;
    link.download = fileName;
    link.click();
  }

  isEmpty(documents: any){
    return isEmpty(documents);
  }

}

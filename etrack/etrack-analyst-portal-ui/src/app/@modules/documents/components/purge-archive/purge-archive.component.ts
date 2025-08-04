import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { PTableHeader } from 'src/app/@shared/components/dashboard-table/table.model';
import { FormControl, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { BehaviorSubject } from 'rxjs';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { environment } from 'src/environments/environment';
import { VirtualDesktopService } from 'src/app/@shared/services/virtual-desktop.service';
import { WindowRef } from 'src/app/@shared/services/windowRef';
import { Utils } from 'src/app/@shared/services/utils';
import { Router } from '@angular/router';
import { purgeArchiveHeaders, archiveAdminHeaders, purgeArchiveAdminHeaders, purgeArchiveAdminReadOnlyHeaders,
  purgeArchiveDownloadHeaders} from './purgeArchiveHeaders';
import { DocumentService } from 'src/app/@shared/services/documentService';
import { DatePipe } from '@angular/common';
import { get } from 'lodash';
import { DashboardTableComponent } from 'src/app/@shared/components/dashboard-table/dashboard-table.component';
import { PurgeArchiveService } from 'src/app/@shared/services/purge-archive.service';
import { ValidatorService } from 'src/app/@shared/services/validator.service';
import { DashboardService } from 'src/app/@shared/services/dashboard.service';

@Component({
  selector: 'app-purge-archive',
  templateUrl: './purge-archive.component.html',
  styleUrls: ['./purge-archive.component.scss']
})
export class PurgeArchiveComponent implements OnInit {

  documentTypes: any = [];
  regionList: any = [];
  queriesList: any = [];
  reviewDocuments: any = [];
  apiReviewDocuments: any = [];
  filteredDocumentList: any = [];
  serverErrorMessage: any = '';
  isArchive: boolean = true;
  showServerError: boolean = false;
  @Input() userIsSystemAdmin: boolean = false;
  @Input() activeTab: any;
  @ViewChild('processPopUp') processPopUp!: CustomModalPopupComponent;
  @ViewChild('processCompletePopUp') processCompletePopUp!: CustomModalPopupComponent;
  @ViewChild('dmsStatusPopUp') dmsStatusPopUp!: CustomModalPopupComponent;
  @ViewChild('informAnalystPopUp') informAnalystPopUp!: CustomModalPopupComponent;
  @ViewChild('reviewCompletePopUp') reviewCompletePopUp!: CustomModalPopupComponent;
  @ViewChild('noRecordsPopUp') noRecordsPopUp!: CustomModalPopupComponent;
  @ViewChild('docModal') docModal!: CustomModalPopupComponent;
  @ViewChild('downloadModal') downloadModal!: CustomModalPopupComponent;
  @ViewChild('downloadFilesTable') downloadFilesTable!: DashboardTableComponent;
  @ViewChild('documentsTable') documentsTable!: DashboardTableComponent;
  region: any = '';
  query: any = '';
  resultSets: any = [];
  showGrid: boolean = false;
  analystIsReviewing: boolean = false;
  showProcessOrReviewButton: boolean = false;
  isReadOnly: boolean = false;
  gridChanged: boolean = false;
  resultSelected: boolean = true;
  queryForm!: UntypedFormGroup;
  purgeArchiveHeaders: PTableHeader[] = [];
  purgeArchiveDownloadHeaders: PTableHeader[] = [];
  selectedRegion: string = '0';
  selectedQuery: string = '';
  currentQueryCode: number = 0;
  canClickReview: boolean = false;
  completeCheckbox: any;
  processPopupOpen: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  processCompletePopUpOpen: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  dmsStatusPopUpOpen: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  informAnalystPopUpOpen: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  reviewCompletePopUpOpen: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  noRecordsPopUpOpen: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  docModalOpen: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  downloadModalOpen: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  openinformAnalystPopUp: boolean = false;
  numDocsToRemove: number = 0;
  analystRegion: any;
  analystQuery: any;
  modalConfig: any = {
    title: '',
    showHeader: false,
    showClose: true
  };
  currentModalRow: any;
  changedDocs: any = [];
  fileList: any = [];
  filteredFileList: any = [];
  currentResultSet: string = '';
  currentResultCode: number = 0;
  docsHaveBeenChanged: boolean = false;
  downloadFilesList: any = [];
  numberFilesDownloaded: number = 0;
  //download files errors
  downloadErrors: any[] = [];
  downloadSubmited: boolean = false;


  constructor(
    private winRef: WindowRef,
    private purgeArchiveService: PurgeArchiveService,
    private docService: DocumentService,
    private projectService: ProjectService,
    private formBuilder: UntypedFormBuilder,
    private virtualDesktopService: VirtualDesktopService,
    private dashboardService: DashboardService,
    private utils: Utils,
    private router: Router,
    private datePipe: DatePipe,
    private errorService: ErrorService,
    private util: ValidatorService) { 
      
    }
  
  createForm() {
    this.queryForm = this.formBuilder.group({
      region: new FormControl('', [Validators.required]),
      queryName: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit(): void {
    
    this.getQueries();
    this.createForm();
    this.getRegions();
    this.getDocTypes();    
    this.getResultSets(true);
    this.purgeArchiveHeaders = this.userIsSystemAdmin ? purgeArchiveAdminHeaders :
      purgeArchiveHeaders;
    this.purgeArchiveDownloadHeaders = purgeArchiveDownloadHeaders;
  }

  getDocTypes() {
    this.docService.getDocumentTypes().then((response) => {
      console.log('docs response here', response);
      
      let types = response['en-US'];
      const sortedKeys = Object.keys(types).sort((a, b) => {
        return types[a].docTypeDesc < types[b].docTypeDesc ? -1 : 1;
      });

      this.documentTypes = [];
      sortedKeys.forEach((value) => {
        this.documentTypes.push(types[value]);
      });
      console.log(this.documentTypes);
    }, 
    (error: any) =>{
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;  
    });
  }

  getResultSets(resetGrid: boolean) {
    this.utils.emitLoadingEmitter(true);
    this.purgeArchiveService.getQueryResults().subscribe((res: any) => {
      this.resultSets = res;
      this.serverErrorMessage = '';
      this.showServerError = false;
      this.utils.emitLoadingEmitter(false);
      if(resetGrid)
        this.getResultSet();
    }, (err: any) => {
      this.utils.emitLoadingEmitter(false);
      this.serverErrorMessage = this.errorService.getServerMessage(err);
      this.showServerError = true;
      throw err;
    })
  }

  getSystemAdminHeaders(headers: PTableHeader[]) {
    headers = purgeArchiveAdminHeaders;

    return headers;
  }

  get popOverText() {
    let popovertext = '';
    switch(this.queryForm?.controls?.queryName?.value) {
      case 'Candidate Deletions for Construction Permits':
        popovertext = 'Records Retention Query for Projects with ONLY Construction Permits Candidate Deletions';
        break;
      case 'Candidate Deletions for Operating Permits':
        popovertext = 'Records Retention Query containing Operating Permits Candidate (can contain construction permits within project) Deletions';
        break;
      case 'Candidate Archives for Construction Permits':
        popovertext = 'Records Retention Query for ONLY Construction Permits Candidate for Archive';
        break;
      case 'Candidate Archives for Operating Permits':
        popovertext = 'Records Retention Query for Operating Permits (can contain construction permits within project) Candidate Archive';
        break;
      default:
        break;
    }
    return popovertext;
  }

  get canCompleteReview() {
    if(this.userIsSystemAdmin) {
      return true;
    }
    let allDocsMarked = true;
    this.reviewDocuments.forEach((doc: any) => {
      if(!doc.markForReview) {
        allDocsMarked = false;
      }
    });
    return allDocsMarked;
  }

  get fileSelectedForDownload() {
    let res = false;
    this.downloadFilesList.forEach((downloadDoc: any) => {
      if(downloadDoc.markForDownload) {
        res = true;
      }
    });
    return res;
  }

  get numFilesSelected() {
    let res = 0;
    this.downloadFilesList.forEach((downloadDoc: any) => {
      if(downloadDoc.markForDownload) {
        res++;
      }
    });
    return res;
  }

  get disableRemoveAll() {
    let res : boolean = true;
    if(!this.reviewDocuments.length) {
      res = false;
    }
    this.reviewDocuments.forEach((obj: any) => {
      if(!obj.removeDisabled) {
        res = false;
      }
    });
    return res;
  }

  getRegions() {
    this.projectService.getAllRegions().then(
      (response) => {
        this.regionList = response;
        if(!this.userIsSystemAdmin) {
          this.dashboardService.getAnalystRegion().subscribe((res: any) => {
            if(res != null) {
              this.queryForm.controls.region.setValue(res.toString());             
            }
            else {
              this.queryForm.controls.region.setValue('0');
            }
            this.queryForm.controls.region.updateValueAndValidity();
          });
        }
        else {
          this.queryForm.controls.region.setValue('0');
          this.queryForm.controls.region.updateValueAndValidity();
        }

      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  getQueries() {
    this.queriesList = [
      "Candidate Deletions for Construction Permits",
      "Candidate Deletions for Operating Permits",
      "Candidate Archives for Construction Permits",
      "Candidate Archives for Operating Permits"
    ]
  }

  onRunClicked() {
    if(this.queryForm.controls.queryName.value) {
      const payload = {
        "queryNameCode": this.currentQueryCode,
        "resultSetName": this.queryForm.controls.queryName.value + ' - R' 
          + this.queryForm.controls.region.value + ' ' + this.datePipe.transform(new Date(), 'MMddyyyy H:mm'),
        "regionId": Number(this.queryForm.controls.region.value)
      }
      this.utils.emitLoadingEmitter(true);
      this.purgeArchiveService.runQuery(payload).subscribe((res: any) => {
        this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = '';
        this.showServerError = false;
        if(res == null) {
          this.noRecordsPopUpOpen.next(true);
          this.noRecordsPopUp.open('sm');
        }
        else {
          this.currentResultSet = payload.resultSetName;
          this.openinformAnalystPopUp = true;
          this.getResultSets(true);
        }
 
      }, (err: any) => {
        this.utils.emitLoadingEmitter(false);    
        this.serverErrorMessage = this.errorService.getServerMessage(err);
        this.showServerError = true;
        throw err;  
      });
    }
  }

  onDocumentChecked(ev: any) {
    if(ev) {
      this.gridChanged = true;
    }
  }

  onRegionChange() {
    this.showGrid = false;
    this.gridChanged = false;
    this.serverErrorMessage = '';
    this.showServerError = false;
    this.currentResultSet = '';
    this.currentResultCode = 0;
    this.showProcessOrReviewButton = false;
    this.reviewDocuments = [];
    this.queryForm.controls.queryName.setValue('');
    this.queryForm.controls.queryName.updateValueAndValidity();
  }

  getResultSet() {
    this.showGrid = false;
    this.gridChanged = false;
    if(this.queryForm.controls.queryName.value && this.queryForm.controls.region.value) {
      this.serverErrorMessage = '';
      this.showServerError = false;
      let queryFound = false;
      if(this.resultSets[this.queryForm.controls.region.value]) {
        this.resultSets[this.queryForm.controls.region.value].queryResults.forEach((res: any) => {
          if(res.queryResult.queryDesc === this.queryForm.controls.queryName.value) {
            queryFound = true;
            this.currentQueryCode = res.queryResult.queryCode;
            if(res.queryResult.resultCode) {
              this.currentResultSet = res.queryResult.resultDesc;
              this.currentResultCode = res.queryResult.resultCode;
              if(this.userIsSystemAdmin) {
                this.showProcessOrReviewButton = res.queryResult.resultReviewedInd ? true : false;
                if(!this.showProcessOrReviewButton) {
                  this.getDocumentsList();
                }
                else {
                  this.reviewDocuments = [];
                }
              }
              else {
                this.showProcessOrReviewButton = true;
                this.isReadOnly = res.queryResult.resultReviewedInd ? true : false;
              }
            }
            else {
              this.currentResultSet = '';
              this.currentResultCode = 0;
              this.showProcessOrReviewButton = false;
              this.reviewDocuments = [];
            }
          }
        });
      }
      if(!queryFound) {
        this.currentQueryCode = this.queriesList.indexOf(this.queryForm.controls.queryName.value) + 1;
        this.currentResultSet = '';
        this.currentResultCode = 0;
        this.showProcessOrReviewButton = false;
        this.reviewDocuments = [];
        console.log(this.currentQueryCode);
      }
    }
  }


  removePurgeArchiveDoc(doc: any) {
    this.numDocsToRemove = 1;
    this.utils.emitLoadingEmitter(true);
    this.removeDocFromList(doc, "R");
  }

  renderDocuments(data: any) {
    
    let documentsArray = new Set<any>();
    let docTypeArray = new Set<any>();
    let docSubTypeArray = new Set<any>();
    let projectIdArray = new Set<any>();
    let decIdArray = new Set<any>();
    let facilityName = new Set<any>();
    let municipality = new Set<any>();

    data?.forEach((obj: any) => {
      obj.docType = obj.docTypeDesc;
      if(obj.otherDocSubCategory) {
        obj.docSubType = obj.otherDocSubCategory;
      }
      else {
        obj.docSubType = obj.docSubTypeDesc;
      }
    });

    data?.sort((a: any, b: any) => {
      if (a.projectId < b.projectId) {
        return -1;
      }
      else if(a.projectId === b.projectId) {
        if(a.docType < b.docType) {
          return -1;
        }
        else if(a.docType === b.docType) {
          if(!b.docSubType && a.docSubType) return 1;
          if(!a.docSubType && b.docSubType) return -1;
          if(a.docSubType && b.docSubType && a.docSubType < b.docSubType) {
            return -1;
          }
          else if((!a.docSubType && !b.docSubType) || a.docSubType === b.docSubType) {
            return a.documentName.toUpperCase() < b.documentName.toUpperCase() ? -1 : 1;
          }
          return 1;
        }
        return 1;
      }

      return 1;
    });
    data.forEach((obj: any) => {
      if(this.userIsSystemAdmin) {
        obj.markedForReview = true;
      }
      else {
        obj.markForReview = (obj.markForReview && obj.markForReview !== "0") ? true : false;
      }
      if(obj.decIdFormatted) {
        obj.decId = obj.decIdFormatted;
      }
      documentsArray.add(obj.documentName ? obj.documentName : '');
      docTypeArray.add(obj.docType ? obj.docType : '');
      docSubTypeArray.add(obj.docSubType ? obj.docSubType : '');
      projectIdArray.add(obj.projectId ? obj.projectId : '');
      decIdArray.add(obj.decId ? obj.decId : '');
      facilityName.add(obj.facilityName ? obj.facilityName : '');
      municipality.add(obj.municipalityName ? obj.municipalityName : '');
      this.purgeArchiveHeaders.forEach((header: any) => {
        if (header.columnTitle === 'decId' && obj.edbDistrictId) {
          obj[header.columnTitle + 'linkToNavigate'] =
            environment.facilityNameUrl + obj.edbDistrictId.toString();
        }
        if(header.columnTitle === 'projectId' && obj.projectId) {
          obj[header.columnTitle + 'linkToNavigate'] = '/documents';
        }
      });
    });
    
    this.purgeArchiveHeaders.forEach((header: any) => {
      switch(header.columnTitle) {
        case 'documentName':
          header.filtersList = Array.from(documentsArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'docType':
            header.filtersList = Array.from(docTypeArray)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
          break;
        case 'docSubType':
              header.filtersList = Array.from(docSubTypeArray)
                .sort((a: any, b: any) => {
                  if (a < b) return -1;
                  return 1;
                })
                .map((str: string) => {
                  return { label: str, value: str };
                });
          break;
        case 'projectId':
            header.filtersList = Array.from(projectIdArray)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
          break;
        case 'decId':
            header.filtersList = Array.from(decIdArray)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
          break;
        case 'facilityName':
            header.filtersList = Array.from(facilityName)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
          break;
        case 'municipalityName':
            header.filtersList = Array.from(municipality)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
          break;
        default:
            header.filtersList = [];
          break;
      }
    });
    if(!this.userIsSystemAdmin) {
      this.completeCheckbox = this.isReadOnly;
    }
    this.reviewDocuments = [...data];
    this.showGrid = true;
  }

  openDocModal(ev: any) {
    console.log(ev);
    this.currentModalRow = ev;
    if(ev.projectId) {
      this.virtualDesktopService.getSuppportDocumentById(ev.documentId, ev.projectId.toString()).subscribe((res) => {
      setTimeout(()=>{
        console.log(res);
        this.fileList = res[0].files;
        this.docModalOpen.next(true);
        this.docModal.open('dms');
      });
      }, 
      (error: any) =>{
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;  
      }
    );
    }
    else if(ev.edbDistrictId) {
      this.docService.getGridFromFacility(ev.edbDistrictId).then((res: any) => {
        res.documents.forEach((doc: any) => {
          if(doc.documentId === ev.documentId) {
            this.fileList = doc.files;
            this.docModalOpen.next(true);
            this.docModal.open('dms');
          }
        })
      });
    }
    
  }

  closeDocModal() {
    this.currentModalRow = {};
    this.docModalOpen.next(false);
    this.docModal.close();
  }

  openFileContent(currentFile: any) {
    let docClassName = this.documentTypes.find(
      (docType: any) => docType.docTypeId === this.currentModalRow.docCategory
    ).docClassName;
    const file = currentFile.fileName;
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
          'txt',
          'msg',
          'ppt',
          'pptx',
          'rtf',
          'shp',
          'tif',
          'zip',
    ];
    let newTab = !downloadable.includes(file.split('.')[file.split('.').length - 1]) ? 
      win.open() : null;
    if (!downloadable.includes(file.split('.')[file.split('.').length - 1]))
    newTab.document.write(`<html>
      <head><title>${file}</title></head>
      <body style="margin: 0; padding: 0"> <span id="sm1">Retrieving file content...</span>`);
      //newTab.document.write('Retrieving file content...');
    this.utils.emitLoadingEmitter(true);
    this.docService.getFileContent(this.currentModalRow.documentId, currentFile.fileName, docClassName, this.currentModalRow.projectId ? this.currentModalRow.projectId : '').then((res:any) =>{ 
      
      this.utils.emitLoadingEmitter(false);
      if (!res) {
          //this.modalReference.close('no_data');
          
          return;
      }
        
        if (!downloadable.includes(file.split('.')[file.split('.').length - 1])){
          let docUrl = win.URL.createObjectURL(res);
          newTab.document.write(`
          <iframe src="${docUrl}" style="width: 100%; height: 100%; margin: 0; padding: 0; border: none;">
          </iframe></body>
          <script>
            document.getElementById("sm1").innerHTML = "";
          </script>
          </html>`);
        }
         // newTab.location.href = win.URL.createObjectURL(res);
        else this.saveFiles(file, res);
        
    }, 
    (error: any) =>{
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;  
    }
  );
  }

  /**
   * downloading selected files and create error list for failed files and scuccssful list for successful files
   * to track how many files download and how many files failed to download
   *  this call getFileContent sequentially and download file.
   *
   */
  downloadFilesFromSelected() {
    let fileMarkedForDownload = false;
    this.downloadErrors = [];
    this.numberFilesDownloaded = 0;
    console.clear();
    console.log(this.downloadFilesList)
    this.downloadFilesList.forEach((file: any) => {
      if(file.markForDownload) {
        fileMarkedForDownload = true;
        let docClassName = this.documentTypes.find(
          (docType: any) => docType.docTypeId === file.docTypeId
        ).docClassName;
        this.docService
          .getFileContent(file.documentId, file.fileName, docClassName, get(file, 'projectId', ''))
          .then((res) => {
            this.numberFilesDownloaded += 1;
            this.saveFiles(file.documentName + '_' + file.fileName, res);
            if(this.numberFilesDownloaded === this.numFilesSelected) {
              this.resetDownloadFilesList(false);
              this.downloadFilesTable.headerCheckbox = false;
            }
          })
          .catch((error: any) => {
          this.downloadErrors.push(file.documentName + '_' + file.fileName);
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;  
          });
      }
    });
    this.downloadSubmited = true;
  }

  private async saveFiles(fileName: string, blob: Blob) {
    this.showServerError = false;
    var link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = fileName;
    link.click();
  }

  buildDownloadFilesList() {

    if(!(this.reviewDocuments.length)) {
      return;
    }

    let facilityIds = new Set<any>();
    let docIds = new Set<any>();
    let filesList: any[] = [];

    this.reviewDocuments.forEach((doc: any) => {
      if(doc.edbDistrictId) {
        facilityIds.add(doc.edbDistrictId);
      }
      if(doc.documentId) {
        docIds.add(doc.documentId);
      }
    });

    console.log(docIds);
    
    this.utils.emitLoadingEmitter(true);
    this.getApiFilesList(filesList, Array.from(facilityIds), docIds, 0);
  }

  getApiFilesList(filesList: any[], facilityIds: any[], docIds: Set<any>, index: number) {
    if(index < facilityIds.length) {
      this.docService.getGridFromFacility(facilityIds[index]).then((res: any) => {
        res.documents.forEach((downloadDoc: any) => {
          if(docIds.has(downloadDoc.documentId)) {

            const downloadDocTypeObj = this.documentTypes.find((docType: any) => 
              docType.docTypeId === downloadDoc.docCategory
            );
            let downloadDocSubtypeDesc = '';
            if(downloadDoc.otherDocSubCategory) {
              downloadDocSubtypeDesc = downloadDoc.otherDocSubCategory;
            }
            else if(downloadDocTypeObj.docSubTypes?.length) {
              const downloadDocSubTypeObj = downloadDocTypeObj.docSubTypes.find((docSubType: any) => 
                  docSubType.subTypeId === downloadDoc.docSubCategory
                )!;
              downloadDocSubtypeDesc = downloadDocSubTypeObj.subTypeDesc;
            }

            const downloadDocTypeDesc = downloadDocTypeObj.docTypeDesc ? downloadDocTypeObj.docTypeDesc : '';
            const accessLevel = downloadDoc.accessByDepOnly == 0 ? 'DEC only' : 'DEC and Applicant';

            downloadDoc.files.forEach((file: any) => {
              filesList.push({
                fileDate: file.fileDate ? this.datePipe.transform(
                  file.fileDate,
                  'MM/dd/yyyy'
                ) : '',
                fileName: file.fileName ? file.fileName : '',
                documentId: downloadDoc.documentId,
                projectId: downloadDoc.projectId ? downloadDoc.projectId : '',
                documentName: downloadDoc.documentName ? downloadDoc.documentName : '',
                docType: downloadDocTypeDesc,
                docTypeId: downloadDoc.docCategory,
                docSubType: downloadDocSubtypeDesc,
                depRelDet: downloadDoc.documentReleasableDesc ? downloadDoc.documentReleasableDesc : '',
                accessLevel: accessLevel ? accessLevel : '',
                appId: downloadDoc.trackedApplicationId ? downloadDoc.trackedApplicationId : '',
                markForDownload: false
              });
            })
          }
        })
        this.getApiFilesList(filesList, facilityIds, docIds, index + 1);
      }).catch((err: any) => {
        console.log(err);
        this.getApiFilesList(filesList, facilityIds, docIds, index + 1);
      })
    }
    else {
      this.utils.emitLoadingEmitter(false);
      this.renderDownloadFilesGrid(filesList);
    }
  }

  renderDownloadFilesGrid(filesList: any[]) {
    let fileNameArray = new Set<any>();
    let fileDateArray = new Set<any>();
    let docNames = new Set<any>();
    let docTypeArray = new Set<any>();
    let docSubTypeArray = new Set<any>();
    let appIds = new Set<any>();
    let accessArray = new Set<any>();
    let releasableDet = new Set<any>();

    filesList.sort((a: any, b: any) => {
      if (a.documentName && b.documentName) {
        if (a.documentName.toUpperCase() < b.documentName.toUpperCase()) return -1;
        else if(a.documentName === b.documentName) {
          if(a.fileName.toUpperCase() < b.fileName.toUpperCase()) {
            return -1;
          }
          return 1;
        }
      }
      return 1;
    });

    filesList.forEach((fileRow: any) => {
      fileNameArray.add(fileRow.fileName ? fileRow.fileName : '');
      fileDateArray.add(fileRow.fileDate ? this.datePipe.transform(
        fileRow.fileDate,
        'MM/dd/yyyy'
      ) : '');
      docNames.add(fileRow.documentName ? fileRow.documentName : '');
      releasableDet.add(fileRow.documentReleasableDesc ? fileRow.documentReleasableDesc : '');
      accessArray.add(fileRow.accessLevel ? fileRow.accessLevel : '');
      appIds.add(fileRow.trackedApplicationId ? fileRow.trackedApplicationId : '');
      docTypeArray.add(fileRow.docType ? fileRow.docType : '');
      docSubTypeArray.add(fileRow.docSubType ? fileRow.docSubType : '');
    });

    this.purgeArchiveDownloadHeaders.forEach((header: any) => {
      switch(header.columnTitle) {
        case 'documentName':
          header.filtersList = Array.from(docNames)
          .sort((a: any, b: any) => {
            if (a < b) return -1;
            return 1;
          })
          .map((str: string) => {
            return { label: str, value: str };
          });
          break;
        case 'docType':
          header.filtersList = Array.from(docTypeArray)
          .sort((a: any, b: any) => {
            if (a < b) return -1;
            return 1;
          })
          .map((str: string) => {
            return { label: str, value: str };
          });
          break;
        case 'docSubType':
          header.filtersList = Array.from(docSubTypeArray)
          .sort((a: any, b: any) => {
            if (a < b) return -1;
            return 1;
          })
          .map((str: string) => {
            return { label: str, value: str };
          });
          break;
        case 'appId':
          header.filtersList = Array.from(appIds)
          .sort((a: any, b: any) => {
            if (a < b) return -1;
            return 1;
          })
          .map((str: string) => {
            return { label: str, value: str };
          });
          break;
        case 'fileName':
          header.filtersList = Array.from(fileNameArray)
          .sort((a: any, b: any) => {
            if (a < b) return -1;
            return 1;
          })
          .map((str: string) => {
            return { label: str, value: str };
          });
          break;
        case 'fileDate':
          header.filtersList = Array.from(fileDateArray)
          .sort((a: any, b: any) => {
            if (a < b) return -1;
            return 1;
          })
          .map((str: string) => {
            return { label: str, value: str };
          });
          break;
        case 'accessLevel':
          header.filtersList = Array.from(accessArray)
          .sort((a: any, b: any) => {
            if (a < b) return -1;
            return 1;
          })
          .map((str: string) => {
            return { label: str, value: str };
          });
          break;
        case 'depRelDet':
          header.filtersList = Array.from(releasableDet)
          .sort((a: any, b: any) => {
            if (a < b) return -1;
            return 1;
          })
          .map((str: string) => {
            return { label: str, value: str };
          });
          break;
        default:
          header.filtersList = [];
          break;
      }
    });

    this.downloadFilesList = [...filesList];
  }

  onQueryFormSubmit() {
    console.log(this.queryForm);
    if(this.userIsSystemAdmin) {
      this.processPopUp.open('sm');
      this.processPopupOpen.next(true);
    }
    else {
      this.getDocumentsList();
    }
    
  }

  getDocumentsList() {
    if(this.queryForm.valid) {
      this.selectedRegion = this.queryForm.controls.region.value;
      this.selectedQuery = this.queryForm.controls.queryName.value;
      this.isArchive = this.queryForm.controls.queryName.value === this.queriesList[2] ||
        this.queryForm.controls.queryName.value === this.queriesList[3];
      if(this.userIsSystemAdmin) {
        if(this.showProcessOrReviewButton) {
          this.purgeArchiveHeaders = purgeArchiveAdminHeaders;
          this.getSystemAdminHeaders(this.purgeArchiveHeaders);
        }
        else {
          this.purgeArchiveHeaders = purgeArchiveAdminReadOnlyHeaders;
        }
      }
      console.log(this.currentResultSet);
      console.log(this.currentResultCode);
      this.utils.emitLoadingEmitter(true);
      this.purgeArchiveService.getResultDocuments(this.currentResultCode).subscribe((res: any) => {
        this.serverErrorMessage = '';
        this.showServerError = false;
        if(!this.userIsSystemAdmin) {
          res.documents.forEach((obj: any) => {
            if(obj.markForReview && obj.markForReview === '1') {
              obj.removeDisabled = true;
            }
          });
        }
        this.renderDocuments(this.util.replaceNullWithEmptyString(res.documents));
        this.utils.emitLoadingEmitter(false);
        if(this.openinformAnalystPopUp) {
          this.openinformAnalystPopUp = false;
          this.informAnalystPopUpOpen.next(true);
          this.informAnalystPopUp.open('sm');
        }
      }, (err: any) => {
        this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = this.errorService.getServerMessage(err);
        this.showServerError = true;
        throw err; 
      });
      
    }
  }

  checkAllReviewDocuments(ev: any) {
    this.gridChanged = true;
      this.reviewDocuments.forEach((doc: any) => {
        if(this.filteredDocumentList.length && (
          (this.filteredDocumentList.length == this.reviewDocuments.length) || 
          this.rowIsFiltered(doc.documentId, this.filteredDocumentList))) {

            doc.markForReview = 'Y' === ev
          }
      })
  }

  checkAllDownloadDocuments(ev: any) {
    //Mark only filtered files for download
    if(('Y' === ev) && this.filteredFileList.length && 
      !(this.filteredFileList.length == this.downloadFilesList.length)) {
      this.downloadFilesList.forEach((downloadFile: any)=> {
        if(this.rowIsFiltered(downloadFile.documentId, this.filteredFileList, downloadFile.fileName)) {

          downloadFile.markForDownload = 'Y' === ev
        }
      })
    }
    //no filters selected
    else {
      this.downloadFilesList.forEach((downloadFile: any)=> {
        downloadFile.markForDownload = 'Y' === ev
      })
    }
  }
  //Check if a row is in the filtered list of rows
  rowIsFiltered(rowDocId: any, filteredRows: any, rowFileName?:any) {
    let res = false;
    filteredRows.forEach((filteredRow: any) => {
      if(rowDocId == filteredRow.documentId) {
        if( !rowFileName || (rowFileName && (rowFileName === filteredRow.fileName))) {
          res = true;
        }
      }
    })

    return res;
  }

  onDownloadRowsFiltered(currentFilteredRows: any[]) {
    this.filteredFileList = [...currentFilteredRows];
  }

  onDocumentRowsFiltered(currentFilteredRows: any[]) {
    console.log(currentFilteredRows);
    this.filteredDocumentList = [...currentFilteredRows];
  }

  removeDoc(selectedDoc: any) {
    this.utils.emitLoadingEmitter(true);
    this.purgeArchiveService.removeDocument(this.currentResultCode, selectedDoc.documentId).subscribe((res: any) => {
      let filteredDocs = this.reviewDocuments.filter((doc: any) => selectedDoc.documentId !== doc.documentId);
      this.renderDocuments(filteredDocs);
      this.serverErrorMessage = '';
      this.showServerError = false;
      setTimeout(() => {
        if(!this.filteredDocumentList.length && this.reviewDocuments.length) {
          this.documentsTable.resetFilters();
        }
        this.utils.emitLoadingEmitter(false);
      }, 200);
      
    }, (err: any) => {
      this.utils.emitLoadingEmitter(false);
      this.serverErrorMessage = this.errorService.getServerMessage(err);
      this.showServerError = true;
      throw err;  
    });
  }

  removeAllDocuments() {
    if(!this.reviewDocuments.length || !this.filteredDocumentList.length) {
      return;
    }
    let documentIds = new Set<any>();
    this.reviewDocuments.forEach((doc: any) => {
      if(this.rowIsFiltered(doc.documentId, this.filteredDocumentList) && !doc.removeDisabled) {
        documentIds.add(doc.documentId);
      }
    });
    if(!documentIds.size) {
      return;
    }

    const payload = Array.from(documentIds);

    this.utils.emitLoadingEmitter(true);
    this.purgeArchiveService.removeDocuments(this.currentResultCode, payload).subscribe((res: any) => {
      let filteredDocs = this.reviewDocuments.filter((doc: any) => !documentIds.has(doc.documentId));
      this.renderDocuments(filteredDocs);
      this.serverErrorMessage = '';
      this.showServerError = false;    
      setTimeout(() => {
        if(!this.filteredDocumentList.length && this.reviewDocuments.length) {
          this.documentsTable.resetFilters();
        }
        this.utils.emitLoadingEmitter(false);
      }, 200);
    }, 
    (err: any) => {
      this.utils.emitLoadingEmitter(false);
      this.serverErrorMessage = this.errorService.getServerMessage(err);
      this.showServerError = true;
      throw err;  
    });


  }

  deleteAllDocuments() {
    if(!this.reviewDocuments.length || !this.filteredDocumentList.length) {
      return;
    }

    let docAndProjectIds = new Set<any>();

    this.reviewDocuments.forEach((doc: any) => {
      if(this.rowIsFiltered(doc.documentId, this.filteredDocumentList)) {
        docAndProjectIds.add(`${doc.documentId.toString()},${doc.projectId.toString()}`);
      }
    })
    const payload = Array.from(docAndProjectIds);
    this.utils.emitLoadingEmitter(true);
    this.purgeArchiveService.purgeDocuments(this.currentResultCode, payload).subscribe((res: any) => {
      this.removeAllDocuments();
    }, 
    (err: any) => {
      this.utils.emitLoadingEmitter(false);
      this.serverErrorMessage = this.errorService.getServerMessage(err);
      this.showServerError = true;
      throw err;  
    });
  }

  deleteDocClicked(ev: any) {
    const payload = [`${ev.documentId},${ev.projectId}`];
    this.utils.emitLoadingEmitter(true);
    this.purgeArchiveService.purgeDocuments(this.currentResultCode, payload).subscribe((res: any) => {
      this.removeDoc(ev);
    }, 
    (err: any) => {
      this.utils.emitLoadingEmitter(false);
      this.serverErrorMessage = this.errorService.getServerMessage(err);
      this.showServerError = true;
      throw err;  
    });
  }

  archiveAllDocuments() {
    // if(!this.reviewDocuments.length || !this.filteredDocumentList.length) {
    //   return;
    // }
    // let documentIds = new Set<any>();
    // this.reviewDocuments.forEach((doc: any) => {
    //   if(this.rowIsFiltered(doc.documentId, this.filteredDocumentList)) {
    //     documentIds.add(doc.documentId.toString());
    //   }
    // })
    // const payload = {
    //   resultId: this.currentResultCode,
    //   documentIds: Array.from(documentIds),
    //   archiveDocInd: "Y"
    // }
    // this.utils.emitLoadingEmitter(true);
    // this.purgeArchiveService.archiveAllDocuments(payload).subscribe((res: any) => {
    //   this.utils.emitLoadingEmitter(false);
    //   this.serverErrorMessage = '';
    //   this.showServerError = false;
    //   this.removeAllRows(true, "A");
    // }, (err: any) => {
    //   this.utils.emitLoadingEmitter(false);
    //   this.serverErrorMessage = this.errorService.getServerMessage(err);
    //   this.showServerError = true;
    //   throw err;
    // });
  }

  archiveDocClicked(ev: any) {
    
    // this.utils.emitLoadingEmitter(true);
    // this.purgeArchiveService.archiveDocument(this.currentResultCode, ev.documentId).subscribe((res: any) => {
    //   this.numDocsToRemove = 1;
    //   this.serverErrorMessage = '';
    //   this.showServerError = false;
    //   this.removeDocFromList(ev, "A");
    // }, (err: any) => {
    //   this.utils.emitLoadingEmitter(false);
    //   this.serverErrorMessage = this.errorService.getServerMessage(err);
    //   this.showServerError = true;
    //   throw err;  
    // });
  }

  processPopupOkClicked() {
    
    this.processPopUp.close();
    this.processPopupOpen.next(false);
    this.getDocumentsList();
  }

  processPopupCancelClicked() {
    this.processPopUp.close();
    this.processPopupOpen.next(false);
  }

  removeDocFromList(selectedDoc: any, actionType: string) {
    let filteredDocs = this.reviewDocuments.filter((doc: any) => {
      return (doc.documentName !== selectedDoc.documentName) || (doc.projectId !== selectedDoc.projectId)
    });
    this.renderDocuments(filteredDocs);

    if(actionType === "D") {
      if(selectedDoc.projectId) {
        let docId: string[] = [];
        docId = selectedDoc.documentId;
        this.docService.deleteDMSDocumentFile(docId, selectedDoc.projectId).then((res: any) => {
          this.purgeArchiveService.removeDocument(this.currentResultCode, selectedDoc.documentId).subscribe((res: any) => {
            this.numDocsToRemove--;
            if(this.numDocsToRemove == 0)
              this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = '';
            this.showServerError = false;
          }, (err: any) => {
            this.numDocsToRemove--;
            if(this.numDocsToRemove == 0)
              this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(err);
            this.showServerError = true;
            throw err;  
          });
        })
        .catch((error: any) => {
          this.numDocsToRemove--;
          if(this.numDocsToRemove == 0)
            this.utils.emitLoadingEmitter(false);
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;  
        });
      }
      else {
        let docClassName = this.documentTypes.find(
          (docType: any) => docType.docTypeId === selectedDoc.docTypeId
        ).docClassName;
        this.docService.deleteDocument(selectedDoc.documentId, docClassName).then((res: any) => {
          this.purgeArchiveService.removeDocument(this.currentResultCode, selectedDoc.documentId).subscribe((res: any) => {
            this.numDocsToRemove--;
            if(this.numDocsToRemove == 0)
              this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = '';
            this.showServerError = false;
          }, (err: any) => {
            this.numDocsToRemove--;
            if(this.numDocsToRemove == 0)
              this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(err);
            this.showServerError = true;
            throw err;  
          });
        })
        .catch((error: any) => {
          this.numDocsToRemove--;
          if(this.numDocsToRemove == 0)
            this.utils.emitLoadingEmitter(false);
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;  
        });
      }
    }
    else {
      this.purgeArchiveService.removeDocument(this.currentResultCode, selectedDoc.documentId).subscribe((res: any) => {
        this.numDocsToRemove--;
        if(this.numDocsToRemove == 0)
          this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = '';
        this.showServerError = false;
      }, (err: any) => {
        this.numDocsToRemove--;
        if(this.numDocsToRemove == 0)
          this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = this.errorService.getServerMessage(err);
        this.showServerError = true;
        throw err;  
      });
    }
    setTimeout(() => {
      if(!this.filteredDocumentList.length && this.reviewDocuments.length) {
        this.documentsTable.resetFilters();
      }
    }, 200);
  }

  removeAllRows(ev: any, actionType: string) {
    if(ev && this.filteredDocumentList.length) {
      this.numDocsToRemove = 0;
      this.reviewDocuments.forEach((doc: any) => {
        if(this.rowIsFiltered(doc.documentId, this.filteredDocumentList) && 
        !(actionType === "D" && doc.litigationHoldInd)) {
          this.numDocsToRemove++;
        }
      });
      if(this.numDocsToRemove)
        this.utils.emitLoadingEmitter(true);
      this.reviewDocuments.forEach((doc: any) => {
        if(this.rowIsFiltered(doc.documentId, this.filteredDocumentList) && 
        !(actionType === "D" && doc.litigationHoldInd)) {
          this.removeDocFromList(doc, actionType);
        }
      });
    }
  }

  onCompleteClicked() {
    if(this.completeCheckbox && !this.userIsSystemAdmin) {
      this.reviewCompletePopUpOpen.next(true);
      this.reviewCompletePopUp.open('sm');
    }
    if(this.completeCheckbox && this.userIsSystemAdmin) {
      this.processCompletePopUpOpen.next(true);
      this.processCompletePopUp.open('sm');
    }
  }

  saveClicked() {
      this.updateResult();
  }

  updateResult() {
    if(!this.reviewDocuments.length) {
      return;
    }
    let docIds : any[] = [];
    this.reviewDocuments.forEach((reviewDoc: any) => {
      docIds.push( {
        docId: reviewDoc.documentId.toString(),
        markForReview: reviewDoc.markForReview
      });
    });
    const payload = {
      resultId: this.currentResultCode,
      documents: docIds,
      archiveType: this.currentQueryCode > 2 ? "Y" : "N"
    };
      
    this.utils.emitLoadingEmitter(true);
    this.purgeArchiveService.updateResult(payload).subscribe((res: any) => {
      this.utils.emitLoadingEmitter(false);
      this.serverErrorMessage = '';
      this.showServerError = false;
      this.getDocumentsList();
      if(res && res.length) {
        this.changedDocs = [];
        res.forEach((deletedDoc: any) => {
          this.reviewDocuments.forEach((reviewDoc: any) => {
            if(reviewDoc.documentId === deletedDoc.documentId && reviewDoc.markForReview) {
              this.changedDocs.push(deletedDoc);
            }
          });
          
        });
        if(this.changedDocs.length) {
          this.dmsStatusPopUpOpen.next(true);
          this.dmsStatusPopUp.open('md');
        }
      }
    }, (err: any) => {
      this.utils.emitLoadingEmitter(false);
      this.serverErrorMessage = this.errorService.getServerMessage(err);
      this.showServerError = true;
      throw err;  
    });
  }

  markReviewComplete() {
    this.reviewCompletePopUpOpen.next(false);
    this.reviewCompletePopUp.close();
    this.utils.emitLoadingEmitter(true);
    this.purgeArchiveService.markReviewComplete(this.currentResultCode).subscribe((res: any) => {
      this.utils.emitLoadingEmitter(false);
      this.isReadOnly = true;
      this.serverErrorMessage = '';
      this.showServerError = false;
      this.getResultSets(false);     
    }, (err: any) => {
      this.utils.emitLoadingEmitter(false);
      this.serverErrorMessage = this.errorService.getServerMessage(err);
      this.showServerError = true;
      throw err;  
    });
  }

  processCompleteDeleteClicked() {
    this.processCompletePopUpOpen.next(false);
    this.processCompletePopUp.close();
    this.utils.emitLoadingEmitter(true);
    this.purgeArchiveService.deleteResult(this.currentResultCode).subscribe((res: any) => {
      this.utils.emitLoadingEmitter(false);
      this.showGrid = false;
      this.showProcessOrReviewButton = false;
      this.completeCheckbox = false;
      this.currentResultCode = 0;
      this.currentResultSet = '';
      this.serverErrorMessage = '';
      this.showServerError = false;
      this.getResultSets(true);
    }, 
    (err: any) => {
      this.utils.emitLoadingEmitter(false);
      this.completeCheckbox = false;
      this.serverErrorMessage = this.errorService.getServerMessage(err);
      this.showServerError = true;
      throw err;  
    });

    
  }

  processCompleteCancelClicked() {
    this.processCompletePopUpOpen.next(false);
    this.processCompletePopUp.close();
    this.completeCheckbox = false;
  }

  dmsStatusOkClicked() {
    let changedDocsIds = new Set<any>();
    this.changedDocs.forEach((changedDoc: any) => {
      changedDocsIds.add(changedDoc.documentId);
    });
    this.numDocsToRemove = changedDocsIds.size;
    if(this.numDocsToRemove)
      this.utils.emitLoadingEmitter(true);
    this.reviewDocuments.forEach((reviewDoc: any) => {
      if(changedDocsIds.has(reviewDoc.documentId)) {
        this.removeDocFromList(reviewDoc, "R");
      }
    });
    this.dmsStatusPopUpOpen.next(false);
    this.dmsStatusPopUp.close();
  }

  dmsStatusCancelClicked() {
    this.dmsStatusPopUpOpen.next(false);
    this.dmsStatusPopUp.close();
  }

  reviewCompleteOkClicked() {
    this.reviewCompletePopUpOpen.next(false);
    this.reviewCompletePopUp.close();
    this.utils.emitLoadingEmitter(true);
    this.purgeArchiveService.markReviewComplete(this.currentResultCode).subscribe((res: any) => {
      this.utils.emitLoadingEmitter(false);
      this.isReadOnly = true;
      this.serverErrorMessage = '';
      this.showServerError = false;
      this.getResultSets(false);     
    }, (err: any) => {
      this.utils.emitLoadingEmitter(false);
      this.serverErrorMessage = this.errorService.getServerMessage(err);
      this.showServerError = true;
      throw err;  
    });
  }

  reviewCompleteCancelClicked() {
    this.reviewCompletePopUpOpen.next(false);
    this.reviewCompletePopUp.close();
    this.completeCheckbox = false;
  }
  
  informAnalystOkClicked() {
    this.informAnalystPopUpOpen.next(false);
    this.informAnalystPopUp.close();
  }

  noRecordsOkClicked() {
    this.noRecordsPopUpOpen.next(false);
    this.noRecordsPopUp.close();
  }

  openDownloadFilesDialog() {
    this.buildDownloadFilesList();
    this.downloadModalOpen.next(true);
    this.downloadModal.open('xl');
  }

  closeDownloadFilesDialog() {

    this.downloadModalOpen.next(false);
    this.downloadModal.close();
    this.resetDownloadFilesList(true);
    this.downloadFilesTable.resetFilters();
    this.filteredFileList = [];
    this.downloadFilesTable.headerCheckbox = false;
    this.downloadSubmited = false;
  }

  resetDownloadFilesList(sortGrid: boolean) {
    this.downloadFilesList.forEach((downloadFile: any) => {
      downloadFile.markForDownload = false;
    });
    
    if(!sortGrid) return;
    this.downloadFilesList.sort((a: any, b: any) => {
      if (a.documentName && b.documentName) {
        if (a.documentName < b.documentName) return -1;
        else if(a.documentName === b.documentName) {
          if(a.fileName < b.fileName) {
            return -1;
          }
          return 1;
        }
      }
      return 1;
    });
  }

  closeClicked(){
    this.router.navigate(['/dashboard']);
  }
  
}

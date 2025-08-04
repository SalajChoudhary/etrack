import { Component, OnInit, ElementRef, ViewChild, Input } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Utils } from '../../../../@shared/services/utils';
import {
  NgbModal,
  ModalDismissReasons,
  NgbModalRef,
} from '@ng-bootstrap/ng-bootstrap';
import { NGXLogger } from 'ngx-logger';
import { Table } from 'primeng/table';


import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
  UntypedFormControl,
  UntypedFormArray,
} from '@angular/forms';
import {
  NgxFileDropEntry,
  FileSystemFileEntry,
  FileSystemDirectoryEntry,
} from 'ngx-file-drop';
import { WindowRef } from 'src/app/@shared/services/windowRef';
import { takeUntil } from 'rxjs/operators';

import { DocumentService } from 'src/app/@shared/services/documentService';
import { AuthService } from 'src/app/core/auth/auth.service';
import moment from 'moment';
import { CommonService } from 'src/app/@shared/services/commonService';
import { fromEvent, Subject } from 'rxjs';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { Headers } from './headers';
import { get } from 'lodash';
import { ErrorService } from '../../../../@shared/services/errorService';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';

@Component({
  selector: 'app-documents',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.scss'],
})
export class DocumentsComponent implements OnInit {
  documents: any[] = [];
  formattedDocuments: any[] = [];
  litigationHoldProjects:any = [];
  downloadFilesList: any[] = [];
  numberFilesDownloaded: number = 0;
  selectedFiles: any[] = [];
  selectedDoc: any = {};
  searchItemText: any = '';
  closeResult = '';
  showExtension: boolean = false;
  @ViewChild('docFormElem') docFormElem!: any;
  @ViewChild('content', { static: true }) modalContent!: CustomModalPopupComponent;
  @ViewChild('duplicateFileName', { static: true })
  modalDuplicateFileName!: any;
  @ViewChild('deleteDocumentConfirm', { static: true })
  modaldeleteDocumentConfirm!: any;
  //receivedDate: Date = new Date();
  modalConfig: any= {};

  ReleasableCode!: any;
  accessSelect!: any;
  fileDateSelect!: any;
  trackedApplicationId!: any;
  docSubTypeSelect!: any;
  doctypeSelect!: any;
  fileName!: any;
  docName!: any;
  UserRole:any = UserRole;
  showServerError = false;
  serverErrorMessage! :string;
  //download files
  @ViewChild('downloadFiles', { static: true })
  modalDownloadFiles!: any;
  //DocumentDetailsSection
  @ViewChild('DocumentDetailsSection', { static: false })
  documentDetailsSection!: ElementRef;

  @ViewChild('documentFormTopElem', { static: false })
  documentFormTopElem!: ElementRef;

  @ViewChild('documentFormReasonsElem', { static: false })
  documentFormReasonsElem!: ElementRef;

  modalDocumentNameBody: string = '';
  modalDocumentNameTitle: string = '';
  modalDocumentNameBodyMessage: string =
    'Select Yes to Continue REPLACE. Click No to Rename.';
  tempformData: any;

  sucessful: boolean = false;
  error: boolean = false;
  uiMessage: string = '';

  //download files errors
  downloadErrors: any[] = [];
  downloadSubmited: boolean = false;

  //hold unique values for given download file filters
  documentTypeOptionsSet = new Set();
  documentSubTypesOptionsSet = new Set();
  accessOptionsSet = new Set();
  depReleaseOptionsSet = new Set();
  fileDateOptionsSet = new Set();

  public files: NgxFileDropEntry[] = [];
  public rawFiles: any[] = [];
  showUpload: boolean = false;
  documentTypes: any[] = [];
  documentSubTypes: any[] = [];
  docForm!: UntypedFormGroup;
  searchTypeDEC: boolean = true;
  showOptions: boolean = false;
  isEdit: any = true;
  errorMessages: any;
  searchForm: UntypedFormGroup;
  submitted: boolean = false;
  docFormsubmitted: boolean = false;
  invalidExtensiontext: string = '';
  reasons: any;
  disableOthers: boolean = true;
  selectedDocId: any;
  districtId: any;
  @Input() facilityNameDropdown: any = '0';
  facilityOptions: any = [];
  screenerName: string | null = '';
  selectedFacility: any;
  disableFacilityDropdown: any = true;
  fileErrorType: any[] = [];
  SearchResponseReceived: boolean = false;
  fileTypesArr: any[] = [];
  selectedIdForDelete: any;
  tooManyFacilities: boolean = false;
  modalReference!: NgbModalRef;
  isvalid: boolean = true;
  fileNameChar: any = [];
  isSpecialChar: boolean = false;
  deleteIsClicked: Subject<boolean> = new Subject();
  confirmDeleteBodyText!: string;
private unsubscriber : Subject<void> = new Subject<void>();
userRole!: string;
headers: any = Headers;
docAppId : string = '';
filterValues: string[] = [];
filtersList = [];
docNameSelect : any = '';
doctypeLabel : any = '';
doctypeLabelFilteringList : any [] = [];
docSubTypeFilteringList : any [] = [];
docNameFilteringList : any [] = [];
trackedApplcationIdFilteringList : any [] = [];
fileNameFilteringList : any = [];
  displayDuplicateErrorMsg: boolean=false;
  removeFacilitySelect: boolean = false;
  projectId: any ='';
  editProjectId: any;
projectIdsSet = new Set();
  constructor(
    private winRef: WindowRef,
    public elem: ElementRef,
    public http: HttpClient,
    public utils: Utils,
    private modalService: NgbModal,
    private logger: NGXLogger,
    private formBuilder: UntypedFormBuilder,
    public docService: DocumentService,
    public authService: AuthService,
    public commonService: CommonService,
    private errorService: ErrorService
  ) {
    this.initiateDocForm();
    this.docService.getReason().then((data) => {
      this.reasons = data;
    });

    this.searchForm = this.formBuilder.group({
      decId: ['option1', Validators.required],
      searchMatchType: [{ value: 'E', disabled: this.searchTypeDEC }],
      facilityNameDropdown: [{ value: '0' }],
      searchItemText: new UntypedFormControl('', [
        Validators.required,
        Validators.minLength(12),
        this.utils.searchTextValidatorDecId,
        this.utils.searchTextValidatorfacility,
      ]),
    });

    this.modalConfig = {
      title: '',
      showHeader: false,
      showClose: true
    };
  }

  closeModal(){
    this.modalContent.close();
  }

  ngAfterViewInit(){
    console.log('after view init');

  }

  ngAfterContentInit(){
    console.log('after content init');

  }

  getDownloadFilterData(){
    this.downloadFilesList.forEach((v, i) =>{
      if(v.documentName && this.docNameFilteringList.findIndex(i => i.label == v.documentName) == -1)
      this.docNameFilteringList.push({label: v.documentName, value: v.documentName});
    });
  }

  ngOnChanges(changes: any){
    console.log('changes');

    this.downloadFilesList.forEach((v, i) =>{
      console.log('j here', v,i);
      if(v.documentName && this.docNameFilteringList.findIndex(i => i.label == v.documentName) == -1)
      console.log('we here', this.docNameFilteringList);

      this.docNameFilteringList.push({label: v.documentName, value: v.documentName});
    });
  }


  scrollToTop(constant: number = 12) {
    let scrollToTop = window.setInterval(() => {
      let pos = window.pageYOffset;
      if (pos > 0) {
        window.scrollTo(0, pos - constant); // how far to scroll on each step
      } else {
        window.clearInterval(scrollToTop);
      }
    }, 16);
  }

  /**
   * On Add document button click event.
   * @constructor
   */
  addDocClicked() {
    console.log('CLICKED');

    this.clearMessages();
    this.initiateDocForm();
    this.revertDocFormState();
    this.showUpload = true;
    setTimeout(() => {
      this.documentDetailsSection.nativeElement.scrollIntoView();
      this.documentDetailsSection.nativeElement.focus();
    }, 100);
  }

  /**
   * Resets the document upload and update form and hides it.
   * @constructor
   */
  hideDocForm() {
    this.initiateDocForm();
    this.clearMessages();
    this.showUpload = false;
    this.isEdit = false;
    this.isSpecialChar = false;
    this.fileNameChar = [];
  }
  /**
   * Formats DEC ID in specific format with dashes(-).
   * @constructor
   * @param {string} decId - The decId.
   */
  formattedDECId(decId: string) {
    let formatted = '';
    if (decId) {
      for (var i = 0; i < decId.length; i++) {
        formatted = formatted + (i == 1 || i == 5 ? '-' + decId[i] : decId[i]);
      }
    }
    return formatted;
  }
  depDeterminationChange() {
    this.showOptions = false;
    const formArray: UntypedFormArray = this.docForm.get('reasons') as UntypedFormArray;
    while (formArray.length !== 0) {
      formArray.removeAt(0);
    }
  }
  /**
   * Initiates or resets the upload document form.
   * @constructor
   */
  initiateDocForm() {
    this.docForm = this.formBuilder.group({
      documentName: new UntypedFormControl('', [
        Validators.required,
        Validators.maxLength(100),
        this.utils.documentNameValidator,
      ]),
      documentReleasableCode: ['NODET'],
      description: [''],
      accessByDepOnly: ['0'],
      docCategory: ['', Validators.required],
      docSubCategory: ['0', Validators.required],
      //  recordDate:new FormControl(new Date()),
      trackedApplicationId: new UntypedFormControl('', [
        Validators.required,
        this.utils.trackedAppIdValidator,
      ]),
      otherDocSubCategory: new UntypedFormControl(
        {
          value: '',
          disabled: this.disableOthers,
        },
        [
          Validators.required,
          Validators.minLength(12),
          this.utils.searchTextValidatorfacility,
        ]
      ),
      reasons: new UntypedFormArray([]),
      projectId : new UntypedFormControl('')
    });

    // Disable the subtype dropdown by default

    this.docForm.controls.docSubCategory.disable();

    if(this.projectIdsSet.size == 0) {
      this.docForm.controls.projectId.disable();
    }

    //  this.docForm.controls.recordDate.setValue(new Date());

    // Reset Doc subcategory dropdown options
    this.documentSubTypes = [];
  }

  /**
   * Handles radio chnage event for DEC ID or Facility name.
   * @constructor
   * @param {any} e - The radio change event.
   */
  accessRadioChanged(e: any) {
    this.submitted = false;
    this.searchForm.controls.searchMatchType[!e ? 'enable' : 'disable']();
    e
      ? this.searchForm.controls.searchMatchType.setValue('E')
      : this.searchForm.controls.searchMatchType.setValue('C');
    this.clearSearchForm();
  }

  /**
   * Handles change event of reasons checkbox.
   * @constructor
   * @param {any} event - The checked event.
   */

  onCheckChange(event: any) {
    const formArray: UntypedFormArray = this.docForm.get('reasons') as UntypedFormArray;
    if (event.target.checked) {
      // Add a new control in the arrayForm
      formArray.push(new UntypedFormControl(event.target.value));
    } else {
      // find the unselected element
      let i: number = 0;
      formArray.controls.forEach((ctrl: any) => {
        if (ctrl.value == event.target.value) {
          formArray.removeAt(i);
          return;
        }
        i++;
      });
    }
  }

  searchTextChange() {
    this.submitted = false;
  }

  /**
   * Triggers on changes event of docCategory dropdown.enabling
   * and disabling others input and doc sub type dropdowns on different
   * scenario
   * @constructor
   */

  onDocumentChange(fromEdit: boolean = false) {
    this.documentSubTypes = this.documentTypes.find(
      (docType) => docType.docTypeId === this.docForm.controls.docCategory.value
    ).docSubTypes
      ? this.documentTypes.find(
          (docType) =>
            docType.docTypeId === this.docForm.controls.docCategory.value
        ).docSubTypes
      : [];
    this.docForm.controls.otherDocSubCategory[
      this.docForm.controls.docCategory.value == 10 ? 'enable' : 'disable'
    ]();
    this.docForm.controls.docSubCategory[
      this.docForm.controls.docCategory.value == 10 ||
      this.documentSubTypes.length == 0
        ? 'disable'
        : 'enable'
    ]();

    if (this.docForm.controls.docCategory.value != 10) {
      this.docForm.controls.otherDocSubCategory.setValue('');
    }
    if (!fromEdit) {
      this.docForm.controls.docSubCategory.setValue('0');
    }
  }

  clearMessages() {
    this.sucessful = false;
    this.error = false;
    this.uiMessage = '';
    this.tooManyFacilities = false;
    this.docFormsubmitted = false;
    this.showServerError = false;
    this.displayDuplicateErrorMsg=false;
  }

  /**
   * Handles submit event of document update.
   * @constructor
   */
  editFormSubmit() {
    let document = this.documents.filter(
      (v) => this.selectedDocId == v.documentId
    )[0];
    let docClassName = this.documentTypes.find(
      (docType) => docType.docTypeId === this.docForm.controls.docCategory.value
    ).docClassName;
    let req = {
      attachmentFilesCount:
        document.files != undefined ? document.files.length : 0,
      metadataProperties: {
        docCategory: this.docForm.value.docCategory,
        docSubCategory: this.docForm.value.docSubCategory
          ? this.docForm.value.docSubCategory.toString()
          : '0',
        docCreationType: 'Text',
        DocumentTitle: this.docForm.value.documentName?.trim(),
        Description: this.docForm.value.description,
        source: 'ETRACK',
        eTrackDocumentID: this.selectedDocId,
        historic: '1',
        projectID: this.editProjectId,
        applicationID: document.trackedApplicationId,
        // @ts-ignore
        docLastModifier: this.screenerName.replace('SVC', '').substring(1),
        //docLastModifier: 'loadrunner',
        foilStatus: this.docForm.value.documentReleasableCode,
        deleteFlag: 'F',
        renewalNumber: '00',
        modificationNumber: '00',
        trackedAppId: this.docForm.value.trackedApplicationId,
        access: this.docForm.value.accessByDepOnly,
        otherSubCatText: this.docForm.value.otherDocSubCategory
          ? this.docForm.value.otherDocSubCategory
          : '',
        indexDate: moment(new Date()).format('YYYYMMDDhhmmss'),
        nonRelReasonCodes:
          this.docForm.value.documentReleasableCode == 'NOREL'
            ? this.docForm.value.reasons.join(',')
            : '',
      },
    };
    this.utils.emitLoadingEmitter(true);
    this.docService
      .updateDocument(req, this.districtId, docClassName, this.editProjectId)
      .then((updateResponse) => {
        this.sucessful = true;
        this.error = false;
        this.uiMessage = 'Successfully Updated Document';
        this.showUpload = false;
        this.isEdit = false;
        this.isSpecialChar = false;
        this.fileNameChar = [];
        this.onFacilityDropdownChange(null);
        this.docForm.reset();
        this.scrollToTop(12);
      })
      .catch((error: any) => {
        console.log(error);
        this.sucessful = false;
        this.error = true;
        let resultCode = error.error.resultCode;
        if (!resultCode) {
          this.uiMessage = error.error;
          this.scrollToTop(12);
        } else {
          this.uiMessage = this.errorMessages[resultCode];
          this.scrollToTop(12);
        }

      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;


      })
      .finally(() => this.utils.emitLoadingEmitter(false));
  }

  /**
   * Calls get documents api on change of the facility dropdown.
   * @constructor
   * @param {any} e - The select event, defaults to null if want to trigger the change event manually.
   */
  onFacilityDropdownChange(e: any = null) {
    this.isEdit = false;
    this.showUpload = false;
    let districtId: any;
    if (e) {
      districtId = e.target.value;
      this.districtId = districtId;
      this.selectedFacility = this.facilityOptions.filter(
        (v: any) => v.districtId == districtId
      )[0];
      this.sucessful = this.error = false;
    } else {
      districtId = this.districtId;
    }

    this.docService.getGridFromFacility(districtId).then((data) => {
      if (data) {
        data.documents.forEach((v: any, i: number) => {
          let docTypeCategory = this.documentTypes.find(
            (docType) => docType.docTypeId === v.docCategory
          );

          if (docTypeCategory) {
            data.documents[i]['doctypeLabel'] =
              docTypeCategory.docTypeDesc.trim();

            if (docTypeCategory.docSubTypes) {
              var subcat = docTypeCategory.docSubTypes.filter(
                (value: any) => v.docSubCategory == value.subTypeId
              );
              data.documents[i]['docSubTypeLabel'] = subcat.length
                ? subcat[0].subTypeDesc
                : '';
            }
          }
        });
        this.documents = data.documents;
        this.documents.sort((a:any,b: any) =>{
          return  new Date(b .uploadDateTime).getTime() - new Date(a.uploadDateTime).getTime();
        });
        this.litigationHoldProjects = get(data, 'litigationHoldProjects', []);
        this.documents.forEach((v) => {
          v.uploadDateTime = moment(new Date(v.uploadDateTime)).format(
            'MM/DD/yyyy HH:mm:ss ZZZZ'
          );
         if(this.litigationHoldProjects){
            v.deleteDisabled = this.litigationHoldProjects.indexOf(v.projectId) !== -1;
         }
        });
        this.projectIdsSet = new Set();
        this.documents.forEach((doc: any) => {
          if(doc.projectId) {
            this.projectIdsSet.add(doc.projectId);
          }
        });
        const sortedProjectIds = Array.from(this.projectIdsSet).sort((a: any, b: any) => a - b);
        this.projectIdsSet = new Set(sortedProjectIds);
        this.buildDownloadFilesList();
        this.formattedDocuments = JSON.parse(JSON.stringify(this.documents));
        this.formattedDocuments.forEach((doc: any) => {
          if(doc.trackedApplicationId) {
            doc.trackedApplicationId = this.formattedDECId(this.selectedFacility.decId)
              + '/' + doc.trackedApplicationId;
          }
        });
      }
    },
    (error: any) =>{
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;
    });
  }

  private buildDownloadFilesList(): void {
    let result: any[] = [];
    this.documents?.forEach((document) => {
      document.files?.forEach((f: any) => {
        result.push({
          documentId: document.documentId,
          projectId: document.projectId,
          documentName: document.documentName,
          docCategory: document.docCategory,
          doctypeLabel: document.doctypeLabel,
          docSubTypeLabel:
            document.docCategory == '10'
              ? document.otherDocSubCategory
              : document.docSubTypeLabel,
          trackedApplicationId: document.trackedApplicationId,
          fileName: f.fileName,
          fileDate: f.fileDate,
          fileDateString: moment(new Date(f.fileDate)).format('MM/DD/yyyy'),
          accessByDepOnly:
            document.accessByDepOnly == 0 ? 'DEC only' : 'DEC and Applicant',
          documentReleasableCode: document.documentReleasableCode,
          documentReleasableDesc: document.documentReleasableDesc,
        });
      });
    });
    console.log('were setting downloadFile list data setting>>', result);

    this.downloadFilesList = result;
    this.documentTypeOptionsSet.clear();
    this.documentSubTypesOptionsSet.clear();
    this.depReleaseOptionsSet.clear();
    this.accessOptionsSet.clear();
    this.fileDateOptionsSet.clear();
    let fileDateStringArray: Date[] = [];
    this.downloadFilesList.forEach((element) => {
      if (element.doctypeLabel) {
        this.documentTypeOptionsSet.add(element.doctypeLabel);
      }
      if (element.docSubTypeLabel) {
        this.documentSubTypesOptionsSet.add(element.docSubTypeLabel);
      }
      if (element.accessByDepOnly) {
        this.accessOptionsSet.add(element.accessByDepOnly);
      }
      // if (element.documentReleasableCode) {
      //   this.depReleaseOptionsSet.add(element.documentReleasableCode);
      // }
      if (element.documentReleasableDesc) {
        this.depReleaseOptionsSet.add(element.documentReleasableDesc);
      }
      if (element.fileDate) {
        fileDateStringArray.push(element.fileDate);
      }
    });
    fileDateStringArray
      .sort()
      .reverse()
      .forEach((date) =>
        this.fileDateOptionsSet.add(moment(new Date(date)).format('MM/DD/yyyy'))
      );
      console.log(this.downloadFilesList,'downloadfileslist')
  }

  clearFilters(downloadTable: Table) {
    this.ReleasableCode = '';
    this.accessSelect = '';
    this.fileDateSelect = '';
    this.trackedApplicationId = '';
    this.docSubTypeSelect = '';
    this.doctypeSelect = '';
    this.fileName = '';
    this.docName = '';
    this.selectedFiles = [];
    downloadTable.filter('', 'documentName', 'contains');
    downloadTable.filter('', 'doctypeLabel', 'equals');
    downloadTable.filter('', 'docSubTypeLabel', 'equals');
    downloadTable.filter('', 'trackedApplicationId', 'contains');
    downloadTable.filter('', 'fileName', 'contains');
    downloadTable.filter('', 'fileDateString', 'contains');
    downloadTable.filter('', 'accessByDepOnly', 'equals');
    downloadTable.filter('', 'documentReleasableDesc', 'equals');
  }

  /**
   * clearing selected files from
   *
   */
  clearSelected(downloadTable: Table) {
    this.selectedFiles = [];
    this.ReleasableCode = '';
    this.accessSelect = '';
    this.fileDateSelect = '';
    this.trackedApplicationId = '';
    this.docSubTypeSelect = '';
    this.doctypeSelect = '';
    this.fileName = '';
    this.docName = '';
    this.selectedFiles = [];
    downloadTable.filter('', 'documentName', 'contains');
    downloadTable.filter('', 'doctypeLabel', 'equals');
    downloadTable.filter('', 'docSubTypeLabel', 'equals');
    downloadTable.filter('', 'trackedApplicationId', 'contains');
    downloadTable.filter('', 'fileName', 'contains');
    downloadTable.filter('', 'fileDateString', 'contains');
    downloadTable.filter('', 'accessByDepOnly', 'equals');
    downloadTable.filter('', 'documentReleasableCode', 'equals');
  }

  /**
   * downloading selected files and create error list for failed files and scuccssful list for successful files
   * to track how many files download and how many files failed to download
   *  this call getFileContent sequentially and download file.
   *
   */
  downloadFilesFromSelected() {
    this.downloadErrors = [];
    this.numberFilesDownloaded = 0;
    console.clear();
    console.log(this.selectedFiles)
    console.log(this.downloadFilesList)
    this.selectedFiles.forEach((file: any) => {
      let docClassName = this.documentTypes.find(
        (docType) => docType.docTypeId === file.docCategory
      ).docClassName;
      this.docService
        .getFileContent(file.documentId, file.fileName, docClassName, get(file, 'projectId', ''))
        .then((res) => {
          this.numberFilesDownloaded += 1;
          this.saveFiles(file.documentName + '_' + file.fileName, res);
        })
        .catch((error: any) => {
        this.downloadErrors.push(file.documentName + '_' + file.fileName);
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
        });
    });
    this.selectedFiles = [];
    this.downloadSubmited = true;
  }
  /**
   * create a link and click action on link to download files as browser click download
   *
   */
  private async saveFiles(fileName: string, blob: Blob) {
    var link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = fileName;
    link.click();
  }

  /**
   * open download file dialog with model size xl
   *
   */
  openDownloadFilesDialog() {
    this.getDownloadFilterData();
    this.clearMessages();
    this.downloadSubmited = false;
    this.open(this.modalDownloadFiles, 'xl');
  }

  /**
   * Resets the search form on click of the clear button next to search button.
   * @constructor
   */

  clearSearchForm() {
    this.clearMessages();
    this.searchForm.controls.searchItemText.setValue('');
    this.facilityNameDropdown = '0';
    this.submitted = false;
    this.facilityOptions = [];
    this.isEdit = true;
    this.documents = [];
    this.formattedDocuments = [];
    this.showUpload = this.SearchResponseReceived = false;
    this.disableFacilityDropdown = true;
    this.commonService.emitClearTableFilters.next(true);
    this.litigationHoldProjects = [];
  }
  /**
   *  Validates reasons
   * @constructor
   */
  isInvalidReasons() {
    return this.docForm.controls.reasons.value.length ==
      this.docForm.controls.reasons.value.filter((v: any) => !v).length
      ? true
      : false;
  }

  /**
   *  Handles submit event of both upload document and update document.
   * @constructor
   */
  onDocFormSubmit() {
    this.clearMessages();
    this.docFormsubmitted = true;
    let formValid: any = null;
    let errorCount = 0;

    Object.keys(this.docForm.controls).forEach((v, i) => {
      if (formValid == null) {
        if (
          (this.docForm.controls[v].errors &&
            v != 'trackedApplicationId' &&
            v != 'docSubCategory' &&
            v != 'otherDocSubCategory') ||
          (v == 'trackedApplicationId' &&
            this.docForm.controls.trackedApplicationId.errors
              ?.maxFiveAllowed) ||
          (v == 'otherDocSubCategory' &&
            this.docForm.controls.docCategory.value == '10' &&
            !this.docForm.controls.otherDocSubCategory.value.length) ||
          !this.districtId ||
          (v == 'documentReleasableCode' &&
            this.docForm.controls.documentReleasableCode.value == 'NOREL' &&
            (!this.docForm.value.reasons.length ||
              this.docForm.value.reasons.length ==
                this.docForm.value.reasons.filter((v: any) => !v).length))
        ) {
          formValid = false;
          errorCount++;
        }
      }
    });
    this.fileTypesArr = [];
    this.fileTypesArr = this.rawFiles.filter(
      (v) =>
        v.name.split('.')[v.name.split('.').length - 1] !=
        this.rawFiles[0].name.split('.')[
          this.rawFiles[0].name.split('.').length - 1
        ]
    );
    if (formValid != null) {
      setTimeout(() => {
        if (
          errorCount == 1 &&
          this.docForm.controls.documentReleasableCode.value == 'NOREL' &&
          (!this.docForm.value.reasons.length ||
            this.docForm.value.reasons.length ==
              this.docForm.value.reasons.filter((v: any) => !v).length)
        ) {
          this.documentFormReasonsElem.nativeElement.scrollIntoView();
          this.documentFormReasonsElem.nativeElement.focus();
        } else {
          this.documentFormTopElem.nativeElement.scrollIntoView();
          this.documentFormTopElem.nativeElement.focus();
        }
      }, 100);
      formValid = null;
      return;
    }
    if (
      (!this.isEdit && this.fileTypesArr.length) ||
      (!this.isEdit &&
        (!this.rawFiles.length ||
          this.invalidExtensiontext.length ||
          this.fileErrorType.length))
    ) {
      formValid = false;
    }
    if (formValid != null) {
      formValid = null;
      return;
    }
    if (this.isEdit) {
      this.editFormSubmit();
      return;
    }
    const {
      description,
      documentName,
      docCategory,
      reasons,
      docSubCategory,
      documentReleasableCode,
      projectId,
      accessByDepOnly,
      otherDocSubCategory,
      trackedApplicationId,
      //recordDate,
    } = this.docForm.value;

    let formData = new FormData();
    let fileDates: any = {};
    this.rawFiles.forEach((v: any, index: number) => {
      formData.append('uploadFiles', new Blob([v], {}), v.name);
      fileDates[index] = moment(new Date(v.lastModified)).format(
        'YYYYMMDDhhmmss'
      );
    });

    let docCreationType = ['jpg', 'jpeg', 'png', 'jpeg', 'gif', 'shp', 'vsd'];

    if(projectId) {
      let request: any = {
        attachmentFilesCount: this.rawFiles.length.toString(),
        fileDates,
        metadataProperties: {
          Description: description,
          docCategory: docCategory.toString(),
          docSubCategory: docSubCategory ? docSubCategory.toString() : '0',
          docCreationType: docCreationType.includes(
            this.rawFiles[0].name.split('.')[
              this.rawFiles[0].name.split('.').length - 1
            ]
          )
            ? 'IMAGE'
            : 'TEXT',
          DocumentTitle: documentName?.trim(),
          historic: '0',
          // @ts-ignore
          docCreator: this.screenerName.replace('SVC', '').substring(1),
          //docCreator: "loadrunner",
          indexDate: moment(new Date()).format('YYYYMMDDhhmmss'),
          // @ts-ignore
          docLastModifier: this.screenerName.replace('SVC', '').substring(1),
          //docLastModifier: "loadrunner",
          source: 'ETRACK',
          applicationID: '0',
          foilStatus: documentReleasableCode,
          deleteFlag: 'F',
          renewalNumber: '0',
          modificationNumber: '0',
          trackedAppId: trackedApplicationId,
          access: accessByDepOnly,
          nonRelReasonCodes: reasons.join(','),
          otherSubCatText: otherDocSubCategory,
          documentTitleId: "",
          DisplayName: null,
          receivedDate: moment(new Date()).format('YYYYMMDDhhmmss'),
          //recordDate,
          permitType: '',
        },
      };
      // console.log(request);
      // return false;
      formData.append(
        'ingestionMetaData',
        new Blob([JSON.stringify(request)], {
          type: 'application/json',
        }),
        'ingestionMetaData.json'
      );
      this.utils.emitLoadingEmitter(true);
      let docClassName = this.documentTypes.find(
        (docType) => docType.docTypeId === this.docForm.controls.docCategory.value
      ).docClassName;

      this.docService
      .uploadButtonDocument(formData, '', projectId.toString())
      .then((response) => {
        this.sucessful = true;
        this.error = false;
        this.isSpecialChar = false;
        this.fileNameChar = [];
        this.uiMessage = 'Successfully Uploaded Document';
        this.revertDocFormState();
        this.displayDuplicateErrorMsg=false;
        this.scrollToTop(12);
        console.log(response);
      })
      .catch((error: any) => {
        let resultCode = error.error.resultCode;
        console.log(resultCode);
        if (!resultCode) {
          this.sucessful = false;
          this.error = true;
          this.uiMessage = error.error;
          this.scrollToTop(12);
          this.displayDuplicateErrorMsg=false;
        } else if (resultCode == 'DOC_NAME_DUP_REPLACE_MSG') {
          // let errorMessage = this.errorMessages[resultCode].split(':');
          // this.modalDocumentNameTitle = errorMessage[0];
          // this.modalDocumentNameBody = errorMessage[1];
          // // this.openDuplicateDocumentModel();
          this.displayDuplicateErrorMsg=true;
          this.tempformData = formData;
        } else {
          this.sucessful = false;
          this.error = true;
          this.uiMessage = this.errorMessages[resultCode];
          this.scrollToTop(12);
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          this.displayDuplicateErrorMsg=false;
          throw error;
        }
      })
      .finally(() => {
        this.utils.emitLoadingEmitter(false);
        console.log('Done loading');
      });
    }
    else {

      let request: any = {
        attachmentFilesCount: this.rawFiles.length.toString(),
        fileDates,
        metadataProperties: {
          Description: description,
          docCategory: docCategory.toString(),
          docSubCategory: docSubCategory ? docSubCategory.toString() : '0',
          docCreationType: docCreationType.includes(
            this.rawFiles[0].name.split('.')[
              this.rawFiles[0].name.split('.').length - 1
            ]
          )
            ? 'IMAGE'
            : 'TEXT',
          DocumentTitle: documentName?.trim(),
          historic: '1',
          // @ts-ignore
          docCreator: this.screenerName.replace('SVC', '').substring(1),
          //docCreator: "loadrunner",
          indexDate: moment(new Date()).format('YYYYMMDDhhmmss'),
          // @ts-ignore
          docLastModifier: this.screenerName.replace('SVC', '').substring(1),
          //docLastModifier: "loadrunner",
          source: 'ETRACK',
          projectID: '0',
          applicationID: '0',
          foilStatus: documentReleasableCode,
          deleteFlag: 'F',
          renewalNumber: '0',
          modificationNumber: '0',
          trackedAppId: trackedApplicationId,
          access: accessByDepOnly,
          nonRelReasonCodes: reasons.join(','),
          otherSubCatText: otherDocSubCategory,
          receivedDate: moment(new Date()).format('YYYYMMDDhhmmss'),
          //recordDate,
          permitType: '',
        },
      };
      // console.log(request);
      // return false;
      formData.append(
        'ingestionMetaData',
        new Blob([JSON.stringify(request)], {
          type: 'application/json',
        }),
        'ingestionMetaData.json'
      );
      this.utils.emitLoadingEmitter(true);
      let docClassName = this.documentTypes.find(
        (docType) => docType.docTypeId === this.docForm.controls.docCategory.value
      ).docClassName;

    this.docService
      .uploadDocument(formData, this.districtId, docClassName)
      .then((response) => {
        this.sucessful = true;
        this.error = false;
        this.isSpecialChar = false;
        this.fileNameChar = [];
        this.uiMessage = 'Successfully Uploaded Document';
        this.revertDocFormState();
        this.displayDuplicateErrorMsg=false;
        this.scrollToTop(12);
        console.log(response);
      })
      .catch((error: any) => {
        let resultCode = error.error.resultCode;
        if (!resultCode) {
          this.sucessful = false;
          this.error = true;
          this.uiMessage = error.error;
          this.scrollToTop(12);
          this.displayDuplicateErrorMsg=false;
        } else if (resultCode == 'DOC_NAME_DUP_REPLACE_MSG') {
          // let errorMessage = this.errorMessages[resultCode].split(':');
          // this.modalDocumentNameTitle = errorMessage[0];
          // this.modalDocumentNameBody = errorMessage[1];
          // // this.openDuplicateDocumentModel();
          this.displayDuplicateErrorMsg=true;
          this.tempformData = formData;
        } else {
          this.sucessful = false;
          this.error = true;
          this.uiMessage = this.errorMessages[resultCode];
          this.scrollToTop(12);
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          this.displayDuplicateErrorMsg=false;
          throw error;
        }
      })
      .finally(() => {
        this.utils.emitLoadingEmitter(false);
      });
    }
  }

  revertDocFormState() {
    this.showUpload = false;
    this.onFacilityDropdownChange(null);
    this.docFormsubmitted = false;
    this.rawFiles = [];
    this.files = [];
  }

  /**
   * Replaces an existing documents if the user wants to submit a document with exiting document name
   * and agrees to replace it.
   * @constructor
   */
  replaceDocument() {
    if (this.tempformData) {
      let docClassName = this.documentTypes.find(
        (docType) =>
          docType.docTypeId === this.docForm.controls.docCategory.value
      ).docClassName;
      this.utils.emitLoadingEmitter(true);
      this.docService
        .replaceDocument(this.tempformData, this.districtId, docClassName)
        .then((response) => {
          this.sucessful = true;
          this.error = false;
          this.uiMessage = 'Successfully Replaced Document';
          this.tempformData = null;
          this.showUpload = false;
          this.revertDocFormState();
          this.scrollToTop(12);
        })
        .catch((error: any) => {
          this.sucessful = false;
          this.error = true;
          this.uiMessage = this.errorMessages[error.error.resultCode];
          this.scrollToTop(12);
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;

        })
        .finally(() => this.utils.emitLoadingEmitter(false));
    }
  }
  onSubmit() {}
  ngOnInit(): void {

    let userInfo = this.authService.getUserInfo();
    this.commonService
    .getUsersRoleAndPermissions(userInfo.ppid)
    .then((response) => {
      this.userRole = response.roles[0];
    })
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
      console.log('docs after response: ', this.documentTypes);
    },
    (error: any) =>{
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;
    });
    this.commonService.emitErrorMessages.subscribe((val)=>{
      if(val)this.errorMessages=this.commonService.getErrorMsgsObj();
    })
    this.screenerName = localStorage.getItem('loggedUserName');

     //diables browswers back button
   history.pushState(null, '');
   fromEvent(window, 'popstate').pipe(
     takeUntil(this.unsubscriber)
   ).subscribe((_) => {
     history.pushState(null, '');
   });
   const decId = sessionStorage.getItem('documentdecID');
   if(decId != null){
    this.removeFacilitySelect = true;
    this.searchTypeDEC = true;
    this.accessRadioChanged(true)
    this.searchForm.controls.searchItemText.setValue(decId);
    sessionStorage.removeItem('documentdecID');
    this.onSearch()
   }
  }

  /**
   * On click of a file name , opens a browser window and renders the files.
   * @constructor
   * @param {any} file - The seleted file.
   */
  openFile(file: any) {
    this.clearMessages();
    let document = this.documents.filter(
      (v) => this.selectedDocId == v.documentId
    )[0];
    let docClassName = this.documentTypes.find(
      (docType) => docType.docTypeId === document.docCategory
    ).docClassName;

    console.log("Doc Class",this.documentTypes.find(
      (docType) => docType.docTypeId === document.docCategory
    ))
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
    this.docService
      .getFileContent(this.selectedDocId, file, docClassName, get(this.selectedDoc, 'projectId', ''))
      .then((res) => {
        this.utils.emitLoadingEmitter(false);
        console.log(res, 'response');
        if (!res) {

          this.modalReference.close('no_data');
          return;
        }

        if (!downloadable.includes(file.split('.')[file.split('.').length - 1]))
        {
          let docUrl = win.URL.createObjectURL(res);
          newTab.document.write(`
          <iframe src="${docUrl}" style="width: 100%; height: 100%; margin: 0; padding: 0; border: none;">
          </iframe></body>
          <script>
            document.getElementById("sm1").innerHTML = "";
          </script>
          </html>`);
          newTab.document.title=file;
        }
          //newTab.location.href = win.URL.createObjectURL(res);
        else this.saveFiles(file, res);
      },
      (error: any) =>{
        this.utils.emitLoadingEmitter(false);
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      });
  }

  /**
   * On search of a facility name, if the response has only one result, automatically selects it and trigger
   * the document search api based on the selection .
   * @constructor
   * @param {any} facilities - List of facilities.
   */
  preSelectSingleFacility(facilities: any) {
    this.facilityNameDropdown =
      facilities.length > 1 || facilities.length == 1 ? facilities[0].districtId : 0;
    this.districtId = facilities.length > 1 || facilities.length == 1 ? facilities[0].districtId : 0;
    this.selectedFacility =  facilities.length > 1 || facilities.length == 1 ? facilities[0] : null;
    if (facilities.length > 1 || facilities.length == 1) this.onFacilityDropdownChange(null);
  }
  onSearch(): any {
    // if(!this.commonService.activateFeature(['PERMISSION1']))
    // {
    //    alert("You are not allowed to use this feature");
    //    return false;
    // }
    this.clearMessages();
    this.facilityOptions = [];
    this.isEdit = true;
    this.documents = [];
    this.formattedDocuments = [];
    this.projectIdsSet = new Set();
    this.showUpload = false;
    this.submitted = true;
    this.SearchResponseReceived = false;
    this.disableFacilityDropdown = true;
    if (
      this.searchForm.controls.searchItemText.errors?.required ||
      (this.searchForm.controls.decId.value == 'option1' &&
        this.searchForm.controls.searchItemText.value.length != 12) ||
      (this.searchForm.controls.decId.value == 'option1' &&
        this.searchForm.controls.searchItemText.errors?.invalidDecSearch) ||
      (this.searchForm.controls.decId.value == 'option2' &&
        !this.searchForm.controls.searchItemText.value.trim().length) ||
      (this.searchForm.controls.decId.value == 'option2' &&
        this.searchForm.controls.searchMatchType.value != 'E' &&
        this.searchForm.controls.searchItemText.errors
          ?.invalidFacilitySearch) ||
      (this.searchForm.controls.decId.value == 'option2' &&
        this.searchForm.controls.searchItemText.errors
          ?.invalidFacilityCharSearch)
    )
      return;
    this.documents = [];
    this.formattedDocuments = [];
    this.utils.emitLoadingEmitter(true);
    if (this.searchTypeDEC) {
      this.docService
        .getFacilityOptions(
          this.searchForm.controls.searchItemText.value.replace(/-/g, ''),
          'DECID'
        )
        .then((data) => {
          if (data) {
            data.forEach((v: any) => {
              v['label'] = `${this.formattedDECId(v.decId)} - ${
                v.facilityName
              } - ${v.municipalityName}`;
            });
            this.facilityOptions = data;
            this.disableFacilityDropdown = false;
            this.preSelectSingleFacility(this.facilityOptions);
            console.log( this.preSelectSingleFacility,'option',this.facilityOptions )
          } else {
            this.facilityOptions = [];
            this.disableFacilityDropdown = true;
          }
          this.SearchResponseReceived = true;
        },
        (error: any) =>{
        this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        })
        .finally(() => {
          this.utils.emitLoadingEmitter(false);
        });
    } else {
      this.docService
        .getFacilityOptions(
          this.searchForm.controls.searchItemText.value.trim(),
          'FACILITY',
          this.searchForm.controls.searchMatchType.value
        )
        .then((data) => {
          if (data) {
            data.forEach((v: any) => {
              v['label'] = `${this.formattedDECId(v.decId)} - ${
                v.facilityName
              } - ${v.municipalityName}`;
            });
            this.facilityOptions = data;
            this.disableFacilityDropdown = false;
            this.preSelectSingleFacility(this.facilityOptions);
          } else {
            this.facilityOptions = [];
            this.disableFacilityDropdown = true;
          }
          this.SearchResponseReceived = true;
        })
        .catch((err) => {
          this.tooManyFacilities =
            err.error.resultCode == 'TOO_MANY_FACILITIES';

      this.serverErrorMessage = this.errorService.getServerMessage(err);
        this.showServerError = true;
        throw err;

        })
        .finally(() => {
          this.utils.emitLoadingEmitter(false);
        });
    }
  }

  deleteClicked(doc: any) {
    this.selectedIdForDelete = doc.documentId;
    this.projectId = doc.projectId;
    console.log("DMS Doc", doc)
    this.confirmDeleteBodyText = 'Are you sure you want to delete the '.concat(doc.documentName).concat(' file?');
    this.clearMessages();
   // this.open(this.modaldeleteDocumentConfirm);
    this.deleteIsClicked.next(true);
  }



  /**
   * Deletes a document logically on click of a times icon in the documents grid.
   * The grid refreshes onces the document is sucessfully deleted
   * @constructor
   * @param {any} id - The document id.
   */

  deleteDocument() {
    console.log("Delete Document with ProjectId")
    console.log("DOc P ID", this.projectId)
    let id = this.selectedIdForDelete;
    let document = this.documents.filter((v) => id == v.documentId)[0];
    let docClassName = this.documentTypes.find(
      (docType) => docType.docTypeId === document.docCategory
    ).docClassName;
    if(!this.projectId){
    this.docService
      .deleteDocument(id, docClassName)
      .then((response) => {
        this.onFacilityDropdownChange(null);
        this.sucessful = true;
        this.uiMessage = 'Successfully Deleted Document';
      })
      .catch((error: any) => {
        let resultCode = error.error.resultCode;
        if (!resultCode) {
          this.sucessful = false;
          this.error = true;
          this.uiMessage = error.error.error;
        } else {
          this.sucessful = false;
          this.error = true;
          this.uiMessage = this.errorMessages[resultCode];
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;

        }
      });
    }
    else{
      this.docService.deleteDMSDocumentFile(id, this.projectId)
      .then((response) => {
        this.onFacilityDropdownChange(null);
        this.sucessful = true;
        this.uiMessage = 'Successfully Deleted Document';
      })
      .catch((error: any) => {
        let resultCode = error.error.resultCode;
        if (!resultCode) {
          this.sucessful = false;
          this.error = true;
          this.uiMessage = error.error.error;
        } else {
          this.sucessful = false;
          this.error = true;
          this.uiMessage = this.errorMessages[resultCode];
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;

        }
      });
    }
  }
  editClickedFun(id: any) {
    this.clearMessages();
    this.isEdit = true;
    this.showUpload = true;
    this.selectedDocId = id;
    this.editProjectId = '';
    let document = this.documents.filter((v) => v.documentId == id)[0];
    Object.keys(document).forEach((key) => {
      if (
        key == 'docNonRelReasonCodes' &&
        document?.docNonRelReasonCodes?.length
      ) {
        this.showOptions = true;
        this.docForm.controls.reasons.reset();
        const formArray: UntypedFormArray = this.docForm.get('reasons') as UntypedFormArray;
        document.docNonRelReasonCodes.forEach((value: any) => {
          formArray.push(new UntypedFormControl(value));
        });
      }
      else {
        this.docForm.controls[key]?.setValue(document[key]);
      }

      if (key === 'projectId') {
        this.editProjectId = document[key];
      }
    });
    setTimeout(() => {
      this.documentDetailsSection.nativeElement.scrollIntoView();
      this.documentDetailsSection.nativeElement.focus();
    }, 100);
    this.onDocumentChange(this.isEdit);
    this.docForm.controls['projectId'].disable();
  }

  open(content: any, modelSize = '40vw') {
    this.modalReference = this.modalService.open(content, {
      ariaLabelledBy: 'modal-basic-title',
      size: modelSize,
    });
    this.modalReference.result.then(
      (result) => {
        console.log(result);
        if (result === 'no_data') {
          this.error = true;
          this.uiMessage = this.errorMessages.NO_CONTENT_FOUND_MSG;
        }
        this.closeResult = `Closed with: ${result}`;
      },
      (reason) => {
        console.log(reason);
        this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
      }
    );
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  /**
   * Deletes a files from the list of files on click of times icon in files grid.
   * @constructor
   * @param {number} i - The curent index of the clicked file.
   */
  public deleteFile(i: number) {
    console.log('delete is clicked');

    this.utils.decrementTotalFileSize(this.rawFiles[i].size);
    this.rawFiles.splice(i, 1);
    this.files.splice(i, 1);
    this.fileErrorType = [];
    this.fileTypesArr = [];
    this.fileTypesArr = this.rawFiles.filter(
      (v) =>
        v.name.split('.')[v.name.split('.').length - 1] !=
        this.rawFiles[0].name.split('.')[
          this.rawFiles[0].name.split('.').length - 1
        ]
    );
    this.validateUploadedFiles();
    this.rawFiles = [...this.rawFiles];
  }

  /**
   * Opens up a modal with list of files in a document.
   * @constructor
   * @param {any} id - The document id.
   */
  public openModal(id: any) {
    this.selectedDoc = this.documents.filter((v) => v.documentId == id)[0];
    this.selectedDocId = id;
    //this.open(this.modalContent);
    this.modalContent.open('dms');
  }

  public openDuplicateDocumentModel() {
    this.open(this.modalDuplicateFileName);
  }

  /**
   * Validates all the uploaded file types and sizes.
   * @constructor
   * @return {string} errorText - A string with a combined error message
   */
  validateUploadedFiles(): string {
    let {
      isInvalid,
      invalidFiles,
      sameFileNameErr,
      invalidSizeFiles,
      totalSizeError,
      invalidFilesError,
      sizeExceededError,
    } = this.utils.fileValidate(this.rawFiles);
    var alertText = '';
    this.fileErrorType = [];
    if (isInvalid) {
      if (totalSizeError) {
        this.fileErrorType.push('totalSizeError');
        alertText += 'Total size cannot exceed 50 MB \r\n';
      }
      if (sizeExceededError) {
        this.fileErrorType.push('sizeExceededError');
        alertText += `The files ${invalidSizeFiles.join(
          ','
        )} is/are exceeding file size \r\n`;
      }
      if (invalidFilesError) {
        this.fileErrorType.push('invalidFilesError');
        alertText += `The files ${invalidFiles.join(
          ','
        )} is/are of invalid file type`;
      }
      if (sameFileNameErr) {
        this.fileErrorType.push('sameFileNameErr');
        alertText += `The files cannot be of same name`;
      }
    }
    this.invalidExtensiontext = alertText;
    return alertText;
  }
  validateFile(files: any) {
    this.fileNameChar = [];
    this.isSpecialChar = false;
    if (!this.fileNameChar.length)
      this.fileNameChar = files.filter((file: any) => {
        return file?.fileEntry?.name?.length > 100;
      });
    files.forEach((v: any) => {
      let indexDot = v?.fileEntry?.name.lastIndexOf('.');
      let name = v?.fileEntry?.name.substring(0, indexDot);

      if (/[~`!@#$%^&={}[\]:;,<>+\/?]/.test(name)) this.isSpecialChar = true;
    });

    return this.fileNameChar.length > 0 || this.isSpecialChar;
  }

  public dropped(files: NgxFileDropEntry[]): boolean {
    this.files = files;

    this.isvalid = this.validateFile(files);
    for (const droppedFile of files) {
      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {
          // Here you can access the real file
          if (!this.isvalid) {
            this.rawFiles = [...this.rawFiles, file];
            this.validateUploadedFiles();
          }
        });
      }
    }
    return true;
  }


	ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }
}

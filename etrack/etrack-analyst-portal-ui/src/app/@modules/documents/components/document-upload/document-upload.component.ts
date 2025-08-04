import { Component, OnInit, ViewChild } from '@angular/core';
import {
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment';
import { NgxFileDropEntry, FileSystemFileEntry } from 'ngx-file-drop';
import { CommonService } from 'src/app/@shared/services/commonService';
import { GisService } from 'src/app/@shared/services/gisService';
import { DocumentService } from 'src/app/@shared/services/documentService';
import { RequiredDocsService } from 'src/app/@shared/services/required-docs.service';
import { Utils } from 'src/app/@shared/services/utils';
import { AuthService } from 'src/app/core/auth/auth.service';
import { PendingChangesPopupComponent } from '../../../../@shared/components/pending-changes-popup/pending-changes-popup.component';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { fromEvent, Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ErrorService } from '../../../../@shared/services/errorService';
import {take} from 'rxjs/operators';
import { InquiryService } from 'src/app/@shared/services/inquiryService';

@Component({
  selector: 'app-document-upload',
  templateUrl: './document-upload.component.html',
  styleUrls: ['./document-upload.component.scss'],
})
export class DocumentUploadComponent implements OnInit {
  modalDocumentNameBodyMessage: string =
    'Select Yes to Continue REPLACE. Click No to Rename.';
  docFormsubmitted: boolean = false;
  reasons: any[] = [];
  rawFiles: any[] = [];
  isvalid: any;
  files: NgxFileDropEntry[] = [];
  isSpecialChar: boolean = false;
  fileNameChar: any;
  invalidExtensiontext: string = '';
  fileErrorType: any[] = [];
  fileTypesArr: any[] = [];
  docForm!: UntypedFormGroup;
  errorMessages: any;
  userRoles: any;
  userRole = UserRole;
  pageFrom: string | null = null;
  pageFrom2: string | null = null;
  otherPageFrom: string | null = null;
  documentTypes: any[] = [];
  error: boolean = false;
  sucessful: boolean = false;
  uiMessage: string = '';
  projectId: string | null = null;
  inquiryId:string |null=null;
  documentSubTypes: any[] = [];
  disableOthers: boolean = true;
  screenerName: string | null = '';
  closeResult: string = '';
  modalReference: any;
  modalDocumentNameTitle: any;
  modalDocumentNameBody: any;
  tempformData!: FormData;
  // @ViewChild('duplicateFileName', { static: true })
  // modalDuplicateFileName!: any;
  displayName: string | null = null;
  documentTitleId: string | null = null;
  initialDocumentTitle: string = '';
  @ViewChild('pendingChangesPopup', { static: true })
  pendingChangesModal!: PendingChangesPopupComponent;
  showOptions: boolean = false;
  decId: any;
  showInvalidZipError: boolean = false;
  showCantProcessError: boolean = false;
  documentCategory: string = '';
  documentSubCategory : string = '';
  documentId : string = '';
  existingDisplayNames: string[] = [];
  showDuplicateDocNameError : boolean = false;
  isConfidential: boolean = false;
  private unsubscriber: Subject<void> = new Subject<void>();
  existingFilesArray : any []= [];
  showOtherDirections: boolean = false;
  showServerError = false;
  serverErrorMessage!: string;
  subscription=new Subscription();
  isSEQR: string | null = null;
  SEQRdocSubTypes : string [] = [];
  SHPAdocSubTypes : string [] =[];
  stepFourDocs! : any;
  listInd: string='';
  otherLabel: string = 'Other:';

  constructor(
    private docService: DocumentService,
    private inquiryService: InquiryService,
    public utils: Utils,
    private commonService: CommonService,
    private formBuilder: UntypedFormBuilder,
    private route: ActivatedRoute,
    private modalService: NgbModal,
    private authService: AuthService,
    private requiredDocsService: RequiredDocsService,
    private router: Router,
    public gisService: GisService,
    private errorService: ErrorService
  ) {
    this.pageFrom = this.route.snapshot.queryParamMap.get('page');
    this.otherPageFrom = this.route.snapshot.queryParamMap.get('otherPageFrom');
    this.isSEQR = this.route.snapshot.queryParamMap.get('isSEQR');

    this.documentTitleId = this.route.snapshot.queryParamMap.get('documentTitleId') || '';
    this.pageFrom2 = this.route.snapshot.queryParamMap.get('page2');
    if (localStorage.getItem('projectId'))
      this.projectId = localStorage.getItem('projectId');
      const queryprojectId = this.route.snapshot.queryParamMap.get('projectId');
    if(queryprojectId){
      this.projectId = queryprojectId;
    }
      
    if (localStorage.getItem('inquiryId'))
      this.inquiryId = localStorage.getItem('inquiryId');
    let queryInquiryId = this.route.snapshot.queryParamMap.get('inquiryId');
    if(queryInquiryId){
      this.inquiryId = queryInquiryId;
    }
    this.screenerName = localStorage.getItem('loggedUserName');
    this.commonService.removeGreenBackground();
    this.initiateDocForm();
  }
  getUploadDetails() {
    this.docService.getUploadDocDetails().then((response) => {});
  }
  getFacilityDetails() {
    const projectId = this.projectId || localStorage.getItem('projectId');
    if (projectId) {
      this.gisService.getProjectLocation(projectId).subscribe((response) => {
        this.decId = response.decId;
      });
    }
  }

  getDocClassificationId(listInd:string):string{
    let docClassificationId='';
    switch(listInd){
      case 'related':
        docClassificationId= '2';
      break;
      case 'required':
        docClassificationId= '1';
      break;
      case 'seqr':
        docClassificationId= '3';
      break;
      case 'shpa':
        docClassificationId= '4';
      break;
    }
    return docClassificationId;
  }
  ngOnInit(): void {
    this.docForm.controls.isConfidential.valueChanges.subscribe((value) => {
      this.isConfidential = value;
    });
    this.requiredDocsService.getListInd().subscribe((listInd:string)=>{
      console.log("listInd", listInd)
      if(listInd){
        this.listInd=this.getDocClassificationId(listInd)
      }
    })
    this.getCurrentUserRole();
    //this.getUploadDetails();
    this.getFacilityDetails();
    this.docService.getReason().then((data) => {
      this.reasons = data;
    });
    this.commonService.emitErrorMessages.subscribe((val)=>{
      if(val)this.errorMessages=this.commonService.getErrorMsgsObj();
    })
    if (this.otherPageFrom === 'inquiry') {
      this.inquiryService.getListInd().subscribe((listInd:string)=>{
        console.log("listInd", listInd)
        if(listInd){
          this.listInd=this.getDocClassificationId(listInd)
        }
      });
      this.inquiryService.getDocumentTypes().then((response) => {
        let types = response['en-US'];
        const sortedKeys = Object.keys(types).sort((a, b) => {
          return types[a].docTypeDesc < types[b].docTypeDesc ? -1 : 1;
        });
        this.documentTypes = [];
        sortedKeys.forEach((value) => {
          this.documentTypes.push(types[value]);
        });

        if(this.isSEQR !== null){
          let uploadType = this.inquiryService.getDocName();
          if(uploadType == 'SEQR'){
            this.docForm.controls['docCategory'].setValue(21);
            this.documentTypes.forEach(doc =>{
              if(doc.docTypeId == 21){
                this.SEQRdocSubTypes = doc.docSubTypes;
                return;
              }
            });
            this.documentSubTypes = this.SEQRdocSubTypes;
          }else if(uploadType == 'SHPA'){
            this.docForm.controls['docCategory'].setValue(23);
            this.documentTypes.forEach(doc =>{
              if(doc.docTypeId == 23){
                this.SHPAdocSubTypes = doc.docSubTypes;
                return;
              }
            });
            this.documentSubTypes = this.SHPAdocSubTypes;
          }
        }
      });

      this.inquiryService.getDocument().subscribe((doc) => {
        if (
          this.pageFrom === 'required' ||
          this.pageFrom === 'other' ||
          this.pageFrom === 'required-Add' ||
          this.pageFrom === 'required-other-Add'
        ) {
          this.documentCategory = doc.docCategory;
          this.documentSubCategory = doc.docSubCategory;
          this.documentId = doc.documentId;
          if (this.pageFrom !== 'other') {
            this.docForm.controls['documentName'].setValue(doc.documentTitle);
          }
          if(this.isSEQR !== null){
            let uploadType = this.inquiryService.getDocName();
            if(uploadType == 'SEQR'){
              this.docForm.controls['docCategory'].setValue(21);
            }else if(uploadType == 'SHPA'){
              this.docForm.controls['docCategory'].setValue(23);
            }
          }
          this.initialDocumentTitle = doc.DisplayName;
          if(this.documentId !==undefined && this.documentId !== null && this.documentId !== ''){
            this.inquiryService.getInquiryDocumentFiles(doc.documentId).then((files: any[]) => {
              files.forEach(file => {
                this.existingDisplayNames.push(file.displayName);
              })
            });
          }
        }
      });
    }else{
    this.docService.getDocumentTypes().then((response) => {
      let types = response['en-US'];
      const sortedKeys = Object.keys(types).sort((a, b) => {
        return types[a].docTypeDesc < types[b].docTypeDesc ? -1 : 1;
      });

      this.documentTypes = [];
      sortedKeys.forEach((value) => {
        this.documentTypes.push(types[value]);
      });
      if(this.isSEQR !== null){
        let uploadType = this.requiredDocsService.getDocName();
        if(uploadType == 'SEQR'){
          this.documentTypes.forEach(doc =>{
            if(doc.docTypeId == 21){
              this.SEQRdocSubTypes = doc.docSubTypes;
              return;
            }
          });
          this.documentSubTypes = this.SEQRdocSubTypes;
        }else if(uploadType == 'SHPA'){
          this.documentTypes.forEach(doc =>{
            if(doc.docTypeId == 23){
              this.SHPAdocSubTypes = doc.docSubTypes;
              return;
            }
          });
          this.documentSubTypes = this.SHPAdocSubTypes;
        }
      }
    });

    this.requiredDocsService.getDocument().subscribe((doc) => {
      if (
        this.pageFrom === 'required' ||
        this.pageFrom === 'other' ||
        this.pageFrom === 'required-Add' ||
        this.pageFrom === 'required-other-Add'
      ) {
        this.documentCategory = doc.docCategory;
        this.documentSubCategory = doc.docSubCategory;
        this.documentId = doc.documentId;
        if( this.pageFrom !== 'other'){
          this.docForm.controls['documentName'].setValue(doc.documentTitle);
        }
        this.initialDocumentTitle = doc.DisplayName;
        if(doc.documentId){
          this.requiredDocsService.getSupportDocumentFiles(doc.documentId).then((files: any[]) =>{
            files.forEach(file =>{
              this.existingDisplayNames.push(file.displayName);
            })

          });
        }

      } else if (this.pageFrom === 'step5') {
        this.docForm.controls['documentName'].setValue(doc.displayName);
      }
    });
  }
    setTimeout(() => {
      window.scroll({ top: 0, left: 0, behavior: 'smooth' });
    }, 0);

    //diables browswers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
      if (this.otherPageFrom === 'inquiry') {
        this.inquiryService.existingDocumentNames.subscribe((names) => {
          this.existingDisplayNames = names;
        });
      } else {
        this.stepFourDocs = this.requiredDocsService.step4Docs;
      }

    // this.requiredDocsService.existingDocumentNames.subscribe((names) => {
    //   this.existingDisplayNames = names;
    //   console.log('j HERE', this.existingDisplayNames);
    // });
  }




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
  public deleteFile(i: number) {
    //this.showInvalidZipError = false;
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
  initiateDocForm() {
    let controlsGroup: any = {};
    if (this.pageFrom === 'other' || this.pageFrom === 'virtual-workspace' || this.pageFrom ==='gi-virtual-workspace') {
      controlsGroup = {
        documentName: new UntypedFormControl('', [
          Validators.required,
          Validators.maxLength(100),
          this.utils.documentNameValidator,
        ]),
        isConfidential: new UntypedFormControl(false),
        documentReleasableCode: ['NODET'],
        description: [''],
        reasons: new UntypedFormArray([]),
        accessByDepOnly: ['0'],
        trackedApplicationId: new UntypedFormControl('', [
          Validators.required,
          this.utils.trackedAppIdValidator,
        ]),
        docCategory: ['', Validators.required],
        docSubCategory: ['0', Validators.required],
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
      };
    } else {
      controlsGroup = {
        documentName: new UntypedFormControl('', [
          Validators.required,
          Validators.maxLength(100),
          this.utils.documentNameValidator,
        ]),
        isConfidential: new UntypedFormControl(false),
        description: [''],
        reasons: new UntypedFormArray([]),
        accessByDepOnly: ['0'],
        trackedApplicationId: new UntypedFormControl('', [
          Validators.required,
          this.utils.trackedAppIdValidator,
        ]),
        docCategory: [''],
        docSubCategory: ['0'],
        otherDocSubCategory: new UntypedFormControl(''),
      };
    }
    this.docForm = this.formBuilder.group(controlsGroup);
    this.docForm.controls.docSubCategory.disable();
    this.documentSubTypes = [];

    this.displayName = this.route.snapshot.queryParamMap.get('displayName');
    this.docForm.controls['documentName'].setValue(this.displayName);
    if(this.isSEQR !== null){
      let uploadType = this.requiredDocsService.getDocName();
      if(uploadType == 'SEQR'){
        this.docForm.controls['docCategory'].setValue(21);
      }else if(uploadType == 'SHPA'){
        this.docForm.controls['docCategory'].setValue(23);
      }
       this.docForm.controls['docCategory'].disable();
      this.docForm.controls['docSubCategory'].enable();
      console.log('doc types', this.documentTypes);

   //   this.onDocumentChange(false);

    }

  }

  depDeterminationChange() {
    this.showOptions = false;
    const formArray: UntypedFormArray = this.docForm.get('reasons') as UntypedFormArray;
    while (formArray.length !== 0) {
      formArray.removeAt(0);
    }
  }
  getCurrentUserRole() {
   this.subscription.add( this.authService.emitAuthInfo.pipe(take(1)).subscribe((authInfo:any)=>{
      if(authInfo && !authInfo.isError){
        this.userRoles=authInfo.roles;
        if (
          this.userRoles &&
          this.userRoles.includes(UserRole.Online_Submitter)
        ) {
          this.docForm.controls.accessByDepOnly.setValue('1');
        }
      }else if(authInfo && authInfo.isError){
        this.serverErrorMessage = this.errorService.getServerMessage(authInfo.error);
        this.showServerError = true;
        throw authInfo.error;
      }
    })
   )
  }
  formattedDECId(decId: string) {
    let formatted = '';
    if (decId) {
      for (var i = 0; i < decId.length; i++) {
        formatted = formatted + (i == 1 || i == 5 ? '-' + decId[i] : decId[i]);
      }
    }

    return formatted;
  }
    onDocFormSubmit(replaceDoc? : boolean) {
    this.docFormsubmitted = true;
    let formValid: any = null;
    let errorCount = 0;
    let docName = this.docForm.controls.documentName.value;
    console.log('j here', this.existingDisplayNames);

    if((this.existingDisplayNames.includes(docName) && !this.userRoles?.includes('Analyst')) && !replaceDoc ){
      this.showDuplicateDocNameError = true;
      return;
    } else{
      this.showDuplicateDocNameError = false;
    }

     // else if( (this.existingDisplayNames.includes(docName) && this.userRoles?.includes('Analyst')) && !replaceDoc ){
    //   console.log('existing');
    //   this.openDuplicateDocumentModel();
    //   return;
    // }

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
      formValid = null;
      return;
    }
    if (
      this.fileTypesArr.length ||
      !this.rawFiles.length ||
      this.invalidExtensiontext.length ||
      this.fileErrorType.length
    ) {
      formValid = false;
    }
    if (formValid != null) {
      formValid = null;
      return;
    }
    const {
      description,
      documentName,
      docCategory,
      reasons,
      docSubCategory,
      documentReleasableCode,
      accessByDepOnly,
      otherDocSubCategory,
      trackedApplicationId,
      isConfidential,
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
    let request: any = {
      attachmentFilesCount: this.rawFiles.length.toString(),
      fileDates,
      metadataProperties: {
        Description: description,

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
        deleteFlag: 'F',
        renewalNumber: '0',
        modificationNumber: '0',
        trackedAppId: trackedApplicationId,
        access: accessByDepOnly,
        nonRelReasonCodes: reasons.join(','),
        documentTitleId: this.documentTitleId,
        DisplayName: this.displayName,
        receivedDate: moment(new Date()).format('YYYYMMDDhhmmss'),
        permitType: '',
      },
    };

    if (this.existingDisplayNames.includes(docName)) {
      this.showDuplicateDocNameError = true;
    }

    // if (
    //   this.pageFrom === 'required-Add' ||
    //   this.pageFrom === 'required-other-Add'
    // ) {
    //   request.metadataProperties.existingDocumentId = this.documentId;
    // }

    if (this.pageFrom === 'required' || this.pageFrom === 'required-Add') {
      if (
        this.docForm.controls['documentName'].value !==
        this.initialDocumentTitle
      ) {
        request.metadataProperties.DisplayName = this.initialDocumentTitle;
      } else {
        request.metadataProperties.DisplayName =
          this.docForm.controls['documentName'].value;
      }
      if (!request.metadataProperties.docCategory) {
        request.metadataProperties.DocumentTitle = documentName;
      }
      request.metadataProperties.foilStatus = isConfidential
        ? 'NOREL'
        : 'NODET';
      request.metadataProperties.confidential = isConfidential ? 'Y' : 'N';
    } else if (
      this.pageFrom === 'other' ||
      this.pageFrom === 'required-other-Add'
    ) {
      console.log('were in this if!');

      request.metadataProperties.confidential = isConfidential ? 'Y' : 'N';
      request.metadataProperties.foilStatus = documentReleasableCode;

       if(this.isSEQR){
        request.metadataProperties.docCategory = this.docForm.controls['docCategory'].value;
       }else{
        request.metadataProperties.docCategory = docCategory.toString();
       }

      request.metadataProperties.docCreationType = docCreationType.includes(
        this.rawFiles[0].name.split('.')[
          this.rawFiles[0].name.split('.').length - 1
        ]
      )
        ? 'IMAGE'
        : 'TEXT';
      request.metadataProperties.docSubCategory = docSubCategory
        ? docSubCategory.toString()
        : '0';
      request.metadataProperties.otherSubCatText = otherDocSubCategory;
      request.metadataProperties.DocumentTitle = documentName;
    } else {
      console.log('were in this if!');

      //request.metadataProperties.documentName=documentName;
      request.metadataProperties.DocumentTitle = documentName;
      request.metadataProperties.docCategory = docCategory.toString();
      request.metadataProperties.foilStatus = documentReleasableCode;
      request.metadataProperties.docCreationType = docCreationType.includes(
        this.rawFiles[0].name.split('.')[
          this.rawFiles[0].name.split('.').length - 1
        ]
      )
        ? 'IMAGE'
        : 'TEXT';
      request.metadataProperties.docSubCategory = docSubCategory
        ? docSubCategory.toString()
        : '0';
      request.metadataProperties.otherSubCatText = otherDocSubCategory;
    }

    if (this.pageFrom === 'required-other-Add') {
      request.metadataProperties.docCategory = this.documentCategory;
      request.metadataProperties.docSubCategory = this.documentSubCategory;
      request.metadataProperties.foilStatus = isConfidential
        ? 'NOREL'
        : 'NODET';
      //for other, add additional for demo hard code foilStatus: 'NODET'
    }

    console.log(request);
    console.log(JSON.stringify(request));

    formData.append(
      'ingestionMetaData',
      new Blob([JSON.stringify(request)], {
        type: 'application/json',
      }),
      'ingestionMetaData.json'
    );
    this.utils.emitLoadingEmitter(true);
    console.log('about to make api call');
    let docClassName = this.documentTypes.find(
      (docType) => docType.docTypeId === this.docForm.controls.docCategory.value
    )?.docClassName;
    console.log(formData, 'formdata');
    console.log(replaceDoc,"replaceDoc");
    if(replaceDoc){
      if (this.otherPageFrom === 'inquiry') {
        this.inquiryService.replaceExistingDocument(formData, this.listInd).then(response => {
          this.router.navigate(['/inquiry-documentation']);
        }).catch(error=>{
          let resultCode = error.error.resultCode;
          if (!resultCode) {
            this.sucessful = false;
            this.error = true;
            this.uiMessage = error.error;
          }
          else if (resultCode === 'INVALID_ZIP_FILE') {
            this.sucessful = false;
            this.error = true;
            this.showInvalidZipError = true;
          } else if(resultCode === 'DUP_DOC_NAME_NOT_ALLOWED'){
            this.showDuplicateDocNameError = true;
            this.sucessful = false;
            this.error = true;

          }
          else if (resultCode = 'UNABLE_TO_PROCESS_NOW') {
            this.sucessful = false;
            this.error = true;
            // this.uiMessage = this.errorMessages[resultCode];
            this.showCantProcessError = true;
          } else {
            this.sucessful = false;
            this.error = true;
            this.uiMessage = this.errorMessages[resultCode];
          }
          return;
        }) .finally(() => {
          this.utils.emitLoadingEmitter(false);
        });
      } else {
    this.requiredDocsService.replaceExistingDocument(formData, this.listInd).then(response =>{
          // this.utils.emitLoadingEmitter(false);

            this.router.navigate([
              this.pageFrom === 'step5'
                ? '/sign-submit'
                : this.pageFrom === 'virtual-workspace'
                ? 'virtual-workspace'
                : '/supporting-documentation',
            ]);

      }).catch(error=>{
        let resultCode = error.error.resultCode;
        if (!resultCode) {
          this.sucessful = false;
          this.error = true;
          this.uiMessage = error.error;
        }
        // else if (resultCode == 'DOC_NAME_DUP_REPLACE_MSG') {
        //   let errorMessage = this.errorMessages[resultCode].split(':');
        //   this.modalDocumentNameTitle = errorMessage[0];
        //   this.modalDocumentNameBody = errorMessage[1];
        //   this.openDuplicateDocumentModel();
        //   this.tempformData = formData;
        // }
        else if (resultCode === 'INVALID_ZIP_FILE') {
          this.sucessful = false;
          this.error = true;
          this.showInvalidZipError = true;
        } else if(resultCode === 'DUP_DOC_NAME_NOT_ALLOWED'){
          this.showDuplicateDocNameError = true;
          this.sucessful = false;
          this.error = true;

        }
        else if (resultCode = 'UNABLE_TO_PROCESS_NOW') {
          this.sucessful = false;
          console.log('WE HERE');

          this.error = true;
          // this.uiMessage = this.errorMessages[resultCode];
          this.showCantProcessError = true;
        } else {
          this.sucessful = false;
          this.error = true;
          this.uiMessage = this.errorMessages[resultCode];
        }
        return;
      }) .finally(() => {
        this.utils.emitLoadingEmitter(false);
      });
    }
    }
    if (
      // this.pageFrom !== 'required-Add' &&
      this.pageFrom !== 'required-other-Add' && !replaceDoc
    ) {
      if (this.otherPageFrom === 'inquiry') {
        this.inquiryService
          .uploadButtonDocument(formData, this.listInd, this.inquiryId)
          .then((response) => {
            console.log(response);
            this.sucessful = true;
            this.error = false;
            this.isSpecialChar = false;
            this.fileNameChar = [];
            if (this.pageFrom === 'required') {
              this.inquiryService.getDocument().subscribe((doc) => {
                this.inquiryService.uploadWasSuccessful(doc);
                this.router.navigate(['/inquiry-documentation']);
              });
            }
            if (this.pageFrom === 'other') {
              let doc = {
                description: null,
                documentId: null,
                documentTitle: documentName,
                releasableCode: null,
                supportDocRefId: null,
                uploadDate: null,
                uploadInd: 'Y',
              };
              this.inquiryService.otherUpLoadWasSuccessful(doc);
              this.router.navigate(['/inquiry-documentation']);
            } else {
              this.router.navigate(['/inquiry-documentation']);
            }
          })
          .catch((error: any) => {
            let resultCode = error.error.resultCode;
            if (!resultCode) {
              this.sucessful = false;
              this.error = true;
              this.uiMessage = error.error;
            } else if (resultCode === 'INVALID_ZIP_FILE') {
              this.sucessful = false;
              this.error = true;
              this.showInvalidZipError = true;
            } else if (resultCode === 'DUP_DOC_NAME_NOT_ALLOWED' || 
              resultCode === 'DOC_NAME_DUP_REPLACE_MSG') {
              this.showDuplicateDocNameError = true;
              this.sucessful = false;
              this.error = true;
            } else if ((resultCode = 'UNABLE_TO_PROCESS_NOW')) {
              this.sucessful = false;
              this.error = true;
              this.showCantProcessError = true;
            } else {
              this.sucessful = false;
              this.error = true;
              this.uiMessage = this.errorMessages[resultCode];
              this.serverErrorMessage = this.errorService.getServerMessage(error);
              this.showServerError = true;
              throw error;
            }
          })
          .finally(() => {
            this.utils.emitLoadingEmitter(false);
          });

      } 
      else if(this.pageFrom ==='gi-virtual-workspace') {
        this.inquiryService.uploadButtonDocument(formData, this.listInd, this.inquiryId)
          .then((response) => {
            this.sucessful = true;
            this.error = false;
            this.isSpecialChar = false;
            this.fileNameChar = [];
            this.router.navigate(['/gi-virtual-workspace/' + this.inquiryId]);
          })
          .catch((error: any) => {
            console.log(error);
            let resultCode = error.error.resultCode;
            if (!resultCode) {
              this.sucessful = false;
              this.error = true;
              this.uiMessage = error.error;
            }
            else if(resultCode === 'DUP_DOC_NAME_NOT_ALLOWED' || 
              resultCode === 'DOC_NAME_DUP_REPLACE_MSG'){
              this.showDuplicateDocNameError = true;
              this.sucessful = false;
              this.error = true;
    
            } else if (resultCode === 'INVALID_ZIP_FILE') {
              this.sucessful = false;
              this.error = true;
              this.showInvalidZipError = true;
            } else if (resultCode = 'UNABLE_TO_PROCESS_NOW') {
              console.log('WE HEE',resultCode);
    
              this.sucessful = false;
              this.error = true;
              this.showCantProcessError = true;
            } else {
              this.sucessful = false;
              this.error = true;
              this.uiMessage = this.errorMessages[resultCode];
            }
          })
          .finally(() => {
            this.utils.emitLoadingEmitter(false);
          });
      }
      
      else {
      this.docService
        .uploadButtonDocument(formData, this.listInd, this.projectId)
        .then((response) => {
          console.log(response);
          this.sucessful = true;
          this.error = false;
          this.isSpecialChar = false;
          this.fileNameChar = [];
          if (this.pageFrom === 'required') {
            this.requiredDocsService.getDocument().subscribe((doc) => {
              this.requiredDocsService.uploadWasSuccessful(doc);
              this.router.navigate(['/supporting-documentation']);
            });
          }
          if (this.pageFrom === 'other') {
            let doc = {
              description: null,
              documentId: null,
              documentTitle: documentName,
              releasableCode: null,
              supportDocRefId: null,
              uploadDate: null,
              uploadInd: 'Y',
            };
            this.requiredDocsService.otherUpLoadWasSuccessful(doc);
            console.log(this.pageFrom2, 'pageform2');
            this.router.navigate([
              this.pageFrom2 === 'step5'
                ? '/sign-submit'
                : '/supporting-documentation',
            ]);
          } else {
            this.router.navigate([
              this.pageFrom === 'step5'
                ? '/sign-submit'
                : this.pageFrom === 'virtual-workspace'
                ? 'virtual-workspace/'+this.projectId
                : '/supporting-documentation',
            ]);
          }
        })
        .catch((error: any) => {
          let resultCode = error.error.resultCode;
          if (!resultCode) {
            this.sucessful = false;
            this.error = true;
            this.uiMessage = error.error;
          }
           else if (resultCode === 'INVALID_ZIP_FILE') {
            this.sucessful = false;
            this.error = true;
            this.showInvalidZipError = true;
          } else if(resultCode === 'DUP_DOC_NAME_NOT_ALLOWED'){
            this.showDuplicateDocNameError = true;
            this.sucessful = false;
            this.error = true;

          }
           else if (resultCode = 'UNABLE_TO_PROCESS_NOW') {
            this.sucessful = false;
            this.error = true;
            // this.uiMessage = this.errorMessages[resultCode];
            this.showCantProcessError = true;
          } else {
            this.sucessful = false;
            this.error = true;
            this.uiMessage = this.errorMessages[resultCode];
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          }
        })
        .finally(() => {
          this.utils.emitLoadingEmitter(false);
        });
      }
    }
    else if(!replaceDoc) {
      if (this.otherPageFrom === 'inquiry') {
        this.inquiryService.addAdditionalDocument(formData, this.listInd)
      .then((response) => {
        this.sucessful = true;
        this.error = false;
        this.isSpecialChar = false;
        this.fileNameChar = [];
        if(this.pageFrom==='virtual-workspace')this.router.navigate(['/virtual-workspace']);
        else this.router.navigate(['/inquiry-documentation']);
      })
      .catch((error: any) => {
        let resultCode = error.error.resultCode;
        if (!resultCode) {
          this.sucessful = false;
          this.error = true;
          this.uiMessage = error.error;
        }
        else if(resultCode === 'DUP_DOC_NAME_NOT_ALLOWED'){
          this.showDuplicateDocNameError = true;
          this.sucessful = false;
          this.error = true;

        } else if (resultCode === 'INVALID_ZIP_FILE') {
          this.sucessful = false;
          this.error = true;
          this.showInvalidZipError = true;
        } else if (resultCode = 'UNABLE_TO_PROCESS_NOW') {
          console.log('WE HEE',resultCode);

          this.sucessful = false;
          this.error = true;
          this.showCantProcessError = true;
        } else {
          this.sucessful = false;
          this.error = true;
          this.uiMessage = this.errorMessages[resultCode];
        }
      })
      .finally(() => {
        this.utils.emitLoadingEmitter(false);
      });
  }
  
  else{
      this.docService
        .addAdditionalDocument(formData, this.listInd)
        .then((response) => {
          this.sucessful = true;
          this.error = false;
          this.isSpecialChar = false;
          this.fileNameChar = [];
          if(this.pageFrom==='virtual-workspace')this.router.navigate(['/virtual-workspace']);
          else this.router.navigate(['/supporting-documentation']);
        })
        .catch((error: any) => {
          let resultCode = error.error.resultCode;
          if (!resultCode) {
            this.sucessful = false;
            this.error = true;
            this.uiMessage = error.error;
          }
          // else if (resultCode == 'DOC_NAME_DUP_REPLACE_MSG') {
          //   let errorMessage = this.errorMessages[resultCode].split(':');
          //   this.modalDocumentNameTitle = errorMessage[0];
          //   this.modalDocumentNameBody = errorMessage[1];
          //   this.openDuplicateDocumentModel();
          //   this.tempformData = formData;
          // }
          else if(resultCode === 'DUP_DOC_NAME_NOT_ALLOWED'){
            this.showDuplicateDocNameError = true;
            this.sucessful = false;
            this.error = true;

          } else if (resultCode === 'INVALID_ZIP_FILE') {
            this.sucessful = false;
            this.error = true;
            this.showInvalidZipError = true;
          } else if (resultCode = 'UNABLE_TO_PROCESS_NOW') {
            console.log('WE HEE',resultCode);

            this.sucessful = false;
            this.error = true;
            // this.uiMessage = this.errorMessages[resultCode];
            this.showCantProcessError = true;
          } else {
            this.sucessful = false;
            this.error = true;
            this.uiMessage = this.errorMessages[resultCode];
          }
        })
        .finally(() => {
          this.utils.emitLoadingEmitter(false);
        });
    }
  }
  }

  open(content: any, modelSize = '40vw') {
    this.modalReference = this.modalService.open(content, {
      ariaLabelledBy: 'modal-basic-title',
      size: modelSize,
    });
    this.modalReference.result.then(
      (result: any) => {
        if (result === 'no_data') {
          this.error = true;
          this.uiMessage = this.errorMessages.NO_CONTENT_FOUND_MSG;
        }
        this.closeResult = `Closed with: ${result}`;
      },
      (reason: any) => {
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
  // public openDuplicateDocumentModel() {
  //   this.open(this.modalDuplicateFileName, '20vh');
  // }

  //Other = 10
  onDocumentChange(fromEdit: boolean = false) {
    console.log('doc changed', this.documentTypes);

    let typeValue = this.docForm.controls['docCategory'].value;
    if(typeValue === 10){
      this.showOtherDirections = true;
    }else{
      this.showOtherDirections = false;
    }
    console.log('doc types', this.documentTypes);

    this.documentSubTypes = this.documentTypes.find(
      (docType) => docType.docTypeId === this.docForm.controls.docCategory.value
    )?.docSubTypes
      ? this.documentTypes.find(
          (docType) =>
            docType.docTypeId === this.docForm.controls.docCategory.value
        ).docSubTypes
      : [];
      console.log(this.documentSubTypes);

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
      this.otherLabel = 'Other:';
    }
    else {
      this.otherLabel = 'Other:*';
    }
    if (!fromEdit) {
      this.docForm.controls.docSubCategory.setValue('0');
    }
  }
  ngOnDestroy() {
    this.commonService.addGreenBackground();
    this.unsubscriber.next();
    this.subscription.unsubscribe();
    this.unsubscriber.complete();
  }

  onCloseClicked() {
    if (
      ((this.pageFrom === 'other' ||
        this.pageFrom === 'required' ||
        this.pageFrom === 'required-Add' ||
        this.pageFrom === 'virtual-workspace' ||
        this.pageFrom === 'gi-virtual-workspace') &&
        this.docForm.dirty) ||
      ((this.pageFrom === 'other' ||
        this.pageFrom === 'required' ||
        this.pageFrom === 'required-Add' ||
        this.pageFrom === 'virtual-workspace' ||
        this.pageFrom === 'gi-virtual-workspace') &&
        this.files.length > 0)
    ) {
      this.pendingChangesModal.open();
    } else {
      this.requiredDocsService.setDocument({});
      if (this.pageFrom === 'virtual-workspace') {
        this.requiredDocsService.setDocument({});
        this.router.navigate(['/virtual-workspace/'+this.projectId]);
        return;
      }
      if(this.pageFrom === 'gi-virtual-workspace') {
        this.router.navigate(['/gi-virtual-workspace/'+this.inquiryId]);
        return;
      }
      if (this.otherPageFrom === 'inquiry') {
        this.router.navigate(['/inquiry-documentation']);
        return;
      }
      this.router.navigate([
        this.pageFrom2 === 'step5' ? 'sign-submit' : 'supporting-documentation',
      ]);
    }
  }

  onPendingChangesOkclick(ev: any) {
    this.requiredDocsService.setDocument({});
    if (
      this.pageFrom === 'other' ||
      this.pageFrom === 'required' ||
      this.pageFrom == 'required-Add'
    ) {
      if (this.pageFrom2 === 'step5') {
        this.router.navigate(['/sign-submit']);
        return;
      }
      this.router.navigate(['/supporting-documentation']);
    }
    if (this.pageFrom === 'virtual-workspace') {
      this.router.navigate(['/virtual-workspace/' + this.projectId]);
      return;
    }
    if(this.pageFrom === 'gi-virtual-workspace') {
      this.router.navigate(['/gi-virtual-workspace/' + this.inquiryId]);
      return;
    }
    if (this.otherPageFrom === 'inquiry') {
      this.router.navigate(['/inquiry-documentation']);
      return;
    }
  }

  deleteAndReplaceDoc(){
    this.onDocFormSubmit(true);
  }
}

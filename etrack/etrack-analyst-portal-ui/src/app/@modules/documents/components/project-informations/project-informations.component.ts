import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { DatePipe } from '@angular/common';
import { takeUntil } from 'rxjs/operators';
import { ErrorService } from 'src/app/@shared/services/errorService';

import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  NgZone,
  OnInit,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';

import {
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';

import { MatStepper } from '@angular/material/stepper';
import { Router } from '@angular/router';
import { NgbModal, NgbPopover } from '@ng-bootstrap/ng-bootstrap';
import _, { get, isEmpty, isEqual, flatten, isArray, uniqBy } from 'lodash';
import {
  validateConstrnType,
  validateGreatThanStartDate,
  whiteSpaceValidator,
} from 'src/app/@shared/applicationInformation.validator';
import { KeywordMaintainService } from 'src/app/@shared/services/keyword-maintain.service'
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { StepperWarningModalComponent } from 'src/app/@shared/components/stepper-warning-modal/stepper-warning-modal.component';
import { fromEvent, Subject } from 'rxjs';
import { ApplicantInfoServiceService } from 'src/app/@shared/services/applicant-info-service.service';
import { ApplicationContactsComponent } from '../application-contacts/application-contacts.component';
import { KeywordOtherGridComponent } from 'src/app/@shared/components/keyword-other-grid/keyword-other-grid.component';
import { Utils } from 'src/app/@shared/services/utils';


interface binNumbers {
  bin: string;
  edbBin: string | null;
}

@Component({
  selector: 'app-project-informations',
  templateUrl: './project-informations.component.html',
  styleUrls: ['./project-informations.component.scss'],
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { displayDefaultIndicatorType: false },
    },
  ],
})

export class ProjectInformationsComponent implements OnInit {
  @ViewChildren('structureCheckboxes')
  structureCheckboxes!: QueryList<ElementRef>;
  @ViewChildren('swCheckboxes')
  swCheckboxes!: QueryList<ElementRef>;
  @ViewChildren('swSubCheckboxes')
  swSubCheckboxes!: QueryList<ElementRef>;
  @ViewChild('pendingPopup', { static: true })
  warningModal!: PendingChangesPopupComponent;
  @ViewChildren('developmentCheckBoxes')
  developmentCheckBoxes!: QueryList<ElementRef>;
  @ViewChild('applicationContacts', { static: true })
  applicationContacts!: ApplicationContactsComponent;
  @ViewChild('stepper', { static: false }) stepper!: MatStepper;
  stepOneCompleted: boolean = false;
  selectedIndex: number = 0;
  firstFormGroup: any;
  isEditable = false;
  isChecked: boolean = false;
  routerLinkVal: String = '';
  applicantsCollection: any;
  addressDetails: any;
  isRefreshed: boolean = true;
  submitted: boolean = false;
  infoMsg: string = 'Names separated by commas';
  wetlandInfoMsg: string = 'Numbers separated by commas';
  popUpTitles = [
    'Applicant(s) for selected location',
    'Property Owner',
    'Contact/Agent',
  ];
  items: any = [];
  xtraIdItems : any = [];
  programIdItems : any = [];
  specialAttnCodeItems :any = [];
  popupData = {
    title: this.popUpTitles[0],
    details: '',
  };
  devTypes: any = [];
  private unsubscriber: Subject<void> = new Subject<void>();
  structureTypes: any = [];
  brief = '';
  projectDescForm!: UntypedFormGroup;
  sicArray: any = [];
  xtraIdArray : any  = [];
  programIdArray : any = [];
  specialAttnCodeArray : any = [];
  naicsArray: any = [];
  isBinShow: boolean = false;
  proposedUseCodes: any = [];
  errorMsgObj: any = {};
  projectDetails: any = undefined;
  addClicked: boolean = false;
  addxtraIdClicked : boolean = false;
  addprogramIdClicked : boolean = false;
  addsplAttnCode : boolean = false;
  isError: boolean = false;
  isXtraIdExistsError : boolean = false;
  isProgramIdExistsError: boolean = false;
  isSpecialAttnExistsError: boolean = false;
  isContactEmpty: boolean = false;
  selectedNextStep: number = 0;
  selectedPermitTypes: any = [];
  modalReference: any;
  isAddedtoGrid: boolean = false;
  isExtraIdAddedtoGrid : boolean = false;
  isprogramIdAddedtoGrid : boolean = false;
  isspecialAttnCodeAddedtoGrid : boolean = false; 
  isChecBoxChanges: boolean = false;
  bridgeIds: any[] = [];
  filtersList: any;
  addedBridgeIds: any[] = [];
  bridgeIdsFormattedList: any[] = [];
  showDuplicateBridgeIdError: boolean = false;
  showBridgeIdRequiredError: boolean = false;
  showBridgeIdMinLengthError: boolean = false;
  binNumber: string = '';
  stepTwoCompleted: boolean = false;
  stepThreeCompleted: boolean = false;
  maxLength: any = 300;
  constructionTypePermits: any[] = [
    'CC',
    'CA',
    'DA',
    'DO',
    'EF',
    'ETF',
    'FW',
    'RZ2',
    'TW',
    'SD',
    'WQ',
    'WR',
  ];
  solidWastePermits: string[] = ['SW1'];

  damTypePermits: string[] = ['DA'];
  damTypeRadios: any[] = [
    { label: "Class 'A'", value: 'A', isChecked: false },
    { label: "Class 'B'", value: 'B', isChecked: false },
    { label: "Class 'C'", value: 'C', isChecked: false },
  ];
  isConstructionDisplay: boolean = false;
  isStreamDisplayed: boolean = false;
  isForwardClick: boolean = false;
  projectStatus: any;
  mode: any = localStorage.getItem('mode');
  isFromStepperGoBack: boolean = false;
  projectDescriptionValidated: boolean = false;
  isSolidWasteFieldsDisplay: boolean = false;
  isDamTypeTobeDisplayed: boolean = false;
  hasZeroContactAgents: boolean = false;
  binHistories: string[] = [];
  yieldSignPath = 'assets/icons/yieldsign.svg';
  hasOneContactAgents: boolean = false;
  avoidFormInitiate: boolean = false;
  solidWasteChecboxTypes: any[] = [];
  swSelectedState: any[] = [];
  extPermits: any = [];
  modPermits: any = [];
  serverErrorMessage!: string;
  showServerError = false;
  formDetails: any;
  contactList: any[] = [];
  singleContact: boolean = false;
  permitAssignHasEmergencyIndicator:boolean = false;
  allNaicsCodes : any[] = [];
  regExpressionError = '';
  checkResult = true;
  programIdRegex! : any;
  programIdPrefixValue : string = "";
  programIdMaskValue!: any;
  programIdHasErrorText = false;
  showProgramIdReqdError = false;
  showProgramIdValReqdError = false;
  currentSelectProgramIdObj : any = {};
  systemParameters: any;
  xtraIdTypeReqError: boolean = false;
  xtraIdValReqError: boolean = false;
  spclAttnCodeReqError: boolean = false;
  programIdMaxLength = '20'
  otherKeywordItems: any[] = [];
  @ViewChild('otherKeywordItemsTable',{ static: false }) otherKeywordItemsTable!:KeywordOtherGridComponent;
  otherSearchItem:any='';
  keywordItems: any[] = [];
  categoryList: any[] = [];
  otherDataList: any[] = [];
  initialData: any[]= [];
  keysToSearch:string[]=['keywordText'];
  systemList: any[] = [];
  otherKeywordButton: boolean= false;
  showOtherServerError: boolean = false;
  get isReadonly() {
    return this.mode == 'read' || this.projectDescriptionValidated;
  }
  get isValidate() {
    return this.mode == 'validate';
  }

  constructor(
    private projectService: ProjectService,
    private fb: UntypedFormBuilder,
    public commonService: CommonService,
    private datePipe: DatePipe,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private modalService: NgbModal,
    private keywordMaintenanceService: KeywordMaintainService,
    private applicantService: ApplicantInfoServiceService,
    private utils: Utils,
    private errorService: ErrorService
  ) {
    this.initiateForm();
    this.getFormContacts();
    this.projectService.getBridgeIds();
  }
  backToMain() {
    this.commonService.activeMode.next('');
    localStorage.setItem('mode', '');
    localStorage.setItem('emergencyAuth', '');
    this.router.navigate(['/apply-for-permit-details']);
  }
  onInputChange(event: string) {
    this.projectDescForm.patchValue({ briefDesc: event });
    this.projectDescForm.updateValueAndValidity();
  }

  back() {
    this.router.navigate(['/associated-applicants']);
  }
  forward() {
    this.router.navigate(['/supporting-documentation']);
  }
  getProjectDetails() {
    this.projectService
      .getProjectData()
      .then((response) => {        
        if (response && response?.briefDesc) {          
          this.projectDetails = response;
          this.getBridgeIds();
          this.binHistories = this.projectDetails?.binNumbersHistory
            ? this.projectDetails?.binNumbersHistory
            : [];
          this.initiateForm();
          this.setFormData();
          this.stepTwoCompleted = true;
        } else {  
          this.setProgramIds(response?.programIds);
          this.stepTwoCompleted = false;
        }
        this.projectDescriptionValidated = isEqual(
          get(response, 'validatedInd', 'N'),
          'Y'
        );
      })
      .catch((err) => {
        this.stepTwoCompleted = false;
        this.serverErrorMessage = this.errorService.getServerMessage(err);
        this.showServerError = true;
        throw err;
      });
  }

  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val)=>{
      if(val)this.errorMsgObj=this.commonService.getErrorMsgsObj();
    })
  }

  deleteRow(i: number) {
    if (this.isReadonly) {
      return;
    }
    let dupitems = JSON.parse(JSON.stringify(this.items));

    dupitems.splice(i, 1);
    this.items = [...dupitems];
    this.isAddedtoGrid = true;
    //window.scrollTo(0, document.body.scrollHeight);
  }

  deletextraIdRow(i: number){
    if (this.isReadonly) {
      return;
    }
    let dupxtraIdItems = JSON.parse(JSON.stringify(this.xtraIdItems));

    dupxtraIdItems.splice(i, 1);
    this.xtraIdItems = [...dupxtraIdItems];
    this.isExtraIdAddedtoGrid = true;
    this.projectDescForm.markAsDirty();
  }

  deleteSpecialAttnCodeRow(i: number){
    if (this.isReadonly) {
      return;
    }
    let dupsplAttnCodeIdItems = JSON.parse(JSON.stringify(this.specialAttnCodeItems));
    dupsplAttnCodeIdItems.splice(i, 1);
    this.specialAttnCodeItems = [...dupsplAttnCodeIdItems];
    this.isspecialAttnCodeAddedtoGrid = true;
    this.projectDescForm.markAsDirty();
    //window.scrollTo(0, document.body.scrollHeight);
  }

  keywordTableData(){
      this.keywordMaintenanceService
        .permitKeyWordData()
        .then((res: any) => {
          console.log("Keyword Ites", res)
          let categories: any[] = [];
          Object.keys(res.permitKeyword).forEach((e: any) => {
            res.permitKeyword[e][0].categoryText = e;
            categories = categories.concat(res.permitKeyword[e]);
          });

          categories.sort((a: any, b: any) => {
            if(a.keywordCategory.toUpperCase() > b.keywordCategory.toUpperCase()) {
              return 1;
            }
            else if((a.keywordCategory === b.keywordCategory) && 
              a.keywordText.toUpperCase() > b.keywordText.toUpperCase()) {
                return 1;
            }
            return -1;
          });
          
          this.categoryList = categories;
          let filtersListValue = Array.from(categories)
          .sort((a: any, b: any) => {
            if (a.keywordCategory < b.keywordCategory) return -1;
            return 1;
          })
          .map((str: any) => {
            return { label: str.keywordCategory, value: str.keywordCategory };
          });
          this.filtersList = uniqBy(
            filtersListValue,
            (value: any) => value.value
          );

          let systemList: any[] = [];
          Object.keys(res.systemDetectedKeyword).forEach((e: any) => {
            res.systemDetectedKeyword[e][0].categoryText = e;
            systemList = systemList.concat(res.systemDetectedKeyword[e]);
          });
          let candidateKeywordList: any[] = [];
          Object.keys(res.candidateKeyword).forEach((e: any) => {
            res.candidateKeyword[e][0].categoryText = e;
            candidateKeywordList = candidateKeywordList.concat(res.candidateKeyword[e]);
          });
          this.systemList = systemList;
          this.otherKeywordItems = candidateKeywordList;
          this.initialData = JSON.parse(JSON.stringify(this.otherKeywordItems));
        })
    
  }

  emittedData(data: any) {
    console.log("Emit Data", data);
    let apiData = JSON.parse(JSON.stringify(data));
    delete(apiData.categoryText)
    this.keywordMaintenanceService.updateKeyword(apiData, data.projectSelected).then((response) => {
    },
    (error: any) => {
      this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;
    })

  }

  otherKeywordEmittedData(data: any){
    console.log("Other Keyword", data)
    if(data.categoryText) {
      delete(data.categoryText);
    }
    this.keywordMaintenanceService.updateKeyword(data, data.projectSelected).then((response) => {
    },
    (error: any) => {
      this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;
    })
  }

  search(){
    if(this.otherSearchItem){
    this.otherKeywordItems= this.initialData.reduce((result: any,obj: any)=>{
      for(const key of this.keysToSearch){
        if(Object.prototype.hasOwnProperty.call(obj,key)){
          const lowerCaseValue= String(obj[key]).toLowerCase();
          const lowercaseinput=this.otherSearchItem.toLowerCase();
          if(lowerCaseValue.includes(lowercaseinput)){
            result.push(obj);
            break;
          }
        }
      }
      return result;
    },[]);}
    else{
      this.otherKeywordItems=JSON.parse(JSON.stringify(this.initialData));
    }
  }

  addOtherkeyWord(){
    if(this.otherSearchItem ==0){
      this.otherKeywordButton = true;
      return;
    }else{
      this.otherKeywordButton = false;
      this.keywordMaintenanceService.addOtherKeyword({
        "keywordCategory": null,
        "keywordCategoryId": null,
        "keywordId": null,
        "keywordText": this.otherSearchItem,
        "projectSelected": null,
        "systemDetected": null
      }).then((res: any) => {
        this.otherSearchItem = '';
        let candidateKeywordList: any[] = [];
        Object.keys(res.candidateKeyword).forEach((e: any) => {
          res.candidateKeyword[e][0].categoryText = e;
          candidateKeywordList = candidateKeywordList.concat(res.candidateKeyword[e]);
        });
        this.otherKeywordItems = candidateKeywordList;
        // this.keywordTableData();
      },
      (error: any) => {
      this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showOtherServerError = true;
      throw error;
    })
    }
  }

  selectCategory(ev: any){

  }

  selectOtherKeywords(ev: any){

  }

  
  deleteBinRow(i: number) {
    if (this.isReadonly) {
      return;
    }
    let binNumber = this.bridgeIds[i];
    const index: number = this.addedBridgeIds.indexOf(binNumber);
    this.bridgeIds.splice(i, 1);
    if (index !== -1) {
      this.addedBridgeIds.splice(index, 1);
      let deleted = this.bridgeIdsFormattedList.splice(index, 1);
    }
    let array = [...this.bridgeIds];
    this.bridgeIds = [...[]];
    this.isAddedtoGrid = true;
    setTimeout(() => {
      this.bridgeIds = [...array];
      this.cdr.detectChanges();
    }, 10);
  }

  deleteProgramIdRow(i: number, programIdItem : any){
    if (this.isReadonly || programIdItem.isFromEfind) {
      return;
    }
    let dupprogramIdItems = JSON.parse(JSON.stringify(this.programIdItems));
    dupprogramIdItems.splice(i, 1);
    this.programIdItems = [...dupprogramIdItems];
    this.isprogramIdAddedtoGrid = true;
    this.projectDescForm.markAsDirty();
    window.scrollTo(0, document.body.scrollHeight);
  }

  getNaicsList(sicCode: number) {
    this.projectService
      .getNaicsCodes(sicCode)
      .then((response: any) => {
        this.naicsArray  =  Object.keys(response).map(key => ({naicsCode: key, naicsDesc: key + ' - ' + response[key]}));
        this.allNaicsCodes =  this.allNaicsCodes.concat(this.naicsArray);
      })
      .catch((err: any) => {
        this.naicsArray = [];
        this.serverErrorMessage = this.errorService.getServerMessage(err);
        this.showServerError = true;
        throw err;
      });
     
  }
  initiateForm() {
    this.projectDescForm = this.fb.group(
      {
        briefDesc: ['', [Validators.required, whiteSpaceValidator]],
        proposedUse: ['1', [Validators.required]],
        developmentType: this.fb.array([], Validators.required),
        structureType: this.fb.array([], Validators.required),
        binNumbers: [[]],
        sicCode: [null],
        naicsCode: [null],
        wetlandIds: [''],
        solidWasteTypes: this.fb.array([]),
        swSubTypes: this.fb.array([]),
        damType: [null, [Validators.required]],
        strWaterbodyName: [''],
        estmtdCompletionDate: [''],
        proposedStartDate: [''],
        constrnType: [null],
        classifiedUnderSeqr: [null],
        xtraIdType:["sel"],
        xtraIdValue:[{value: null, disabled: true}],
        programIds:["sel"],
        programIdValue:[{value: null, disabled: true}],
        splAttnCodes: ["sel"],
      },
      {
        validators: [
          validateGreatThanStartDate(
            'proposedStartDate',
            'estmtdCompletionDate',
            'constrnType',
            false
          ),
          validateConstrnType(
            'constrnType',
            this.isConstructionDisplay
          )
        ],
      }
    );

    if (!this.isDamTypeTobeDisplayed) {
      this.projectDescForm.removeControl('damType');
    }
  }
  onContructionChange(e: any) {
    // if(e.target.checked){
    //   this.projectDescForm.controls.proposedStartDate.setValidators(Validators.required);
    //   this.projectDescForm.controls.estmtdCompletionDate.setValidators(Validators.required);
    // }else{
    //   this.projectDescForm.controls.proposedStartDate.clearValidators();
    //   this.projectDescForm.controls.estmtdCompletionDate.clearValidators();
    // }
  }
  onclassifiedUnderSeqrChange(e: any) {}
  onOtherChange(e: any) {
    // if(e.target.checked){
    //   this.projectDescForm.controls.proposedStartDate.clearValidators();
    //   this.projectDescForm.controls.estmtdCompletionDate.clearValidators();
    // }
  }
  onOtherTextChange() {
    this.showOtherServerError = false;
  }
  resetCheckBoxes() {
    this.structureCheckboxes.map((item: ElementRef) => {
      item.nativeElement.checked = false;
    });
    this.developmentCheckBoxes.map((item: ElementRef) => {
      item.nativeElement.checked = false;
    });
  }
  setCheckBoxes() {
    const developmentType = this.projectDescForm.controls
      .developmentType as UntypedFormArray;
    const structureType = this.projectDescForm.controls
      .structureType as UntypedFormArray;
    const swType = this.projectDescForm.controls
      .solidWasteTypes as UntypedFormArray;
    const swSubType = this.projectDescForm.controls
      .swSubTypes as UntypedFormArray;
    this.unCheckAll(this.developmentCheckBoxes, developmentType, 'dev');
    this.unCheckAll(this.structureCheckboxes, structureType, 'struc');
    this.unCheckAll(this.swCheckboxes, swType, 'sw');
    this.unCheckAll(this.swSubCheckboxes, swSubType, 'swSub');
    this.uncheckNA(this.developmentCheckBoxes, developmentType, 'dev');
    this.uncheckNA(this.structureCheckboxes, structureType, 'struc');
    if (this.solidWasteChecboxTypes.length > 0 && this.projectDetails) {
      this.swCheckboxes.map((item: ElementRef) => {
        this.swSelectedState.forEach((obj: any) => {
          if (parseInt(item.nativeElement.value) == obj.swFacilityType) {
            item.nativeElement.checked = true;
            swType.push(new UntypedFormControl(item.nativeElement.value));
          }
        });
      });
      this.swSubCheckboxes.map((item: ElementRef) => {
        this.swSelectedState.forEach((obj: any) => {
          if (obj.swFacilitySubTypes) {
            obj.swFacilitySubTypes.forEach((subItem: any) => {
              if (
                subItem.swfacilitySubType == parseInt(item.nativeElement.value)
              ) {
                item.nativeElement.checked = true;
                swSubType.push(
                  new UntypedFormControl(item.nativeElement.value)
                );
              }
            });
          }
        });
      });
    }
    if (this.structureTypes?.length > 1 && this.projectDetails) {
      this.structureCheckboxes.map((item: ElementRef) => {
        if (
          this.projectDetails?.structureType?.includes(
            parseInt(item.nativeElement.value)
          )
        ) {
          item.nativeElement.checked = true;
          structureType.push(new UntypedFormControl(item.nativeElement.value));
        }
      });
    }
    if (this.devTypes?.length > 1 && this.projectDetails) {
      this.developmentCheckBoxes.map((item: ElementRef) => {
        if (
          this.projectDetails?.developmentType?.includes(
            parseInt(item.nativeElement.value)
          )
        ) {
          item.nativeElement.checked = true;
          developmentType.push(
            new UntypedFormControl(item.nativeElement.value)
          );
        }
      });
    }
  }
  setFormData() {
    
    this.projectDescForm.controls.briefDesc.setValue(
      this.projectDetails?.briefDesc
    );
    this.projectDescForm.controls.proposedUse.setValue(
      this.projectDetails?.proposedUse
    );
    this.projectDescForm.controls.binNumbers.setValue(
      this.projectDetails?.binNumbers
    );

    this.projectDescForm.controls.constrnType.setValue(
      `${this.projectDetails?.constrnType}`
    );
    this.projectDescForm.controls.classifiedUnderSeqr.setValue(
      `${this.projectDetails?.classifiedUnderSeqr}`
    );

    if (this.projectDetails?.constrnType == 1) {
      this.onContructionChange({ target: { checked: true } });
    }
    this.projectDescForm.controls.proposedStartDate.setValue(
      this.projectDetails?.proposedStartDate
        ? this.datePipe.transform(
            new Date(this.projectDetails?.proposedStartDate),
            'yyyy-MM-dd'
          )
        : ''
    );
    this.projectDescForm.controls.estmtdCompletionDate.setValue(
      this.projectDetails?.estmtdCompletionDate
        ? this.datePipe.transform(
            new Date(this.projectDetails?.estmtdCompletionDate),
            'yyyy-MM-dd'
          )
        : ''
    );
    
    this.projectDescForm.controls.strWaterbodyName.setValue(
      this.projectDetails?.strWaterbodyName
    );

    this.projectDescForm.controls.wetlandIds.setValue(
      this.projectDetails?.wetlandIds
    );
    this.projectDescForm.controls?.damType?.setValue(
      this.projectDetails?.damType ? this.projectDetails.damType : ''
    );
    this.items = [];
    if (this.projectDetails?.sicCodeNaicsCode?.length > 0) {
      this.projectDetails?.sicCodeNaicsCode?.forEach((gridObj: any) => {
        let sicCodesArray = Object.keys(gridObj);
        sicCodesArray?.forEach((code: any) => {
          this.getNaicsList(code);
          this.items.push({
            sicCode: code,
            sicDesc: '',
            naicsCode: gridObj[code],
          });
        });
      });
      
    }
    
    this.xtraIdItems = [];
    if(this.projectDetails?.xtraIds?.length > 0){
        let xtraIdArray =  this.projectDetails?.xtraIds;
        xtraIdArray?.forEach((code: any) => {
          this.xtraIdItems.push({
            programApplicationTypeCode: code.programApplicationCode,
            xtraIdValue: code.programApplicationIdentifier,
            xtraId: '',
          });
        });
    }
    this.setProgramIds(this.projectDetails?.programIds);
    this.specialAttnCodeItems = [];
    if(this.projectDetails?.splAttnCodes?.length > 0){
        let specialAttnCodeArray = this.projectDetails?.splAttnCodes;
        specialAttnCodeArray?.forEach((code:any)=>{
          this.specialAttnCodeItems.push({
            specialAttnTypeCode: code,
            specialAttnCd: '',
          })
        })
      
    }

    this.swSelectedState = this.projectDetails.swFacilityTypes;
    setTimeout(() => {
      this.setCheckBoxes();
      this.setGrid();
      this.setXtraIdGrid();
      this.setprogramIdGrid();
      this.setspecialAttentionCodeGrid();
      this.maxLength = 302;
      this.cdr.detectChanges();
      this.maxLength = 300;
    }, 500);

  }


  setProgramIds(programIdArray: any[]){    
    this.programIdItems = [];                
        programIdArray?.forEach((code: any) => {                    
          this.programIdItems.push({
            progDistrictTypeCode: code.programDistrictCode,
            programIdValue: code.programDistrictIdentifier,
            programId: code.programDistrictCode,
            isFromEfind : (code.edbProgramDistrictIdentifier == 'null') ? false : (code.edbProgramDistrictIdentifier != 'null') ? true : ""
          });
        });
  }

  forwardClick() {
    this.isForwardClick = true;
    setTimeout(() => {
      this.isForwardClick = false;
    }, 1000);
  }


  setGrid() {
    if (this.sicArray?.length > 0 && this.items.length > 0) {
      this.items.forEach((item: any) => {
     let naicsIndex = this.allNaicsCodes.findIndex((o : any) => o.naicsCode == item?.naicsCode);     
     if(naicsIndex >= 0){
      item.naicsCode = this.allNaicsCodes[naicsIndex]?.naicsDesc;
     } 
    let i = this.sicArray.findIndex(
          (x: any) => x?.sicCode == item?.sicCode
        );
        if (i >= 0) {
          item.sicDesc = this.sicArray[i]?.sicDesc;
        }
      });
    }
  }

  setXtraIdGrid() {
    if (this.xtraIdArray?.length > 0 && this.xtraIdItems.length > 0) {
      this.xtraIdItems.forEach((xtraIdItem: any) => {
    let i = this.xtraIdArray.findIndex(
          (xtra: any) => xtra?.programApplicationTypeCode == xtraIdItem?.programApplicationTypeCode
        );
        if (i >= 0) {

          xtraIdItem.xtraId = this.xtraIdArray[i]?.xtraId;          
        }
      });
    }
  }

  setprogramIdGrid(){
    if (this.programIdArray?.length > 0 && this.programIdItems.length > 0) {
      this.programIdItems.forEach((programIdItem: any) => {
    let i = this.programIdArray.findIndex(
          (x: any) => x?.progDistrictTypeCode == programIdItem?.progDistrictTypeCode
        );
        if (i >= 0) {
          programIdItem.programId = this.programIdArray[i]?.programId;
        }
      });
    }

  }

  setspecialAttentionCodeGrid(){
    if (this.specialAttnCodeArray?.length > 0 && this.specialAttnCodeItems.length > 0) {
      this.specialAttnCodeItems.forEach((specialAttnCodeItem: any) => {
      let i = this.specialAttnCodeArray.findIndex(
          (x: any) => x?.specialAttnTypeCode == specialAttnCodeItem?.specialAttnTypeCode
        );
        if (i >= 0) {
          specialAttnCodeItem.specialAttnCd = this.specialAttnCodeArray[i]?.specialAttnCd;
        }
      });
    }

  }

  goBack(e?: any) {
    if (e) {
      this.openConfirmModal();
      return;
    }
    if (
      this.projectDescForm.dirty ||
      this.isChecBoxChanges ||
      this.isAddedtoGrid
    ) {
      this.openConfirmModal();
    } else {
      this.commonService.navigateToMainPage();
    }
  }

  stepperGoback() {
    if (
      !this.projectDescForm.dirty &&
      !(this.addClicked  || this.isAddedtoGrid || this.isChecBoxChanges)
    ) {
      return;
    }

    const options = {
      size: 'stepper-warning-modal',
      backdrop: <any>'static',
    };
    const modalRef = this.modalService.open(
      StepperWarningModalComponent,
      options
    );
    modalRef.result.then(
      (value) => {
        //on ok
        console.log('On Ok Warning click')
        this.devTypes = [];
        this.getConfigs();
        this.getSelectedTypes();
      },
      (reason) => {
        // on cancel
        this.isFromStepperGoBack = true;
        this.stepper.selectedIndex = 1;
        setTimeout(() => {
          this.isFromStepperGoBack = false;
        });
      }
    );
  }

  onXtraIdChanged(ev: any){
      this.xtraIdTypeReqError = false;
      this.xtraIdValReqError = false;
      this.isXtraIdExistsError = false;
      this.projectDescForm.get('xtraIdValue')?.setValue('');
      this.projectDescForm.get('xtraIdValue')?.enable();

  }


  addXTRAIDToGrid(){
      this.xtraIdTypeReqError = false;
      this.xtraIdValReqError = false;
      this.isXtraIdExistsError = false;
      this.addxtraIdClicked = true;
      let xtra = this.projectDescForm.get('xtraIdType')?.value;
      let xtraIdValue = this.projectDescForm.get('xtraIdValue')?.value;
      if(xtra =="sel"){
        this.xtraIdTypeReqError = true;
        if(!xtraIdValue){
          this.xtraIdValReqError = true;
        }
        return;
      }
      if(!xtraIdValue){        
        this.xtraIdValReqError = true;
        return;
      }
      let existsInGrid = this.xtraIdItems.filter((data:any)=>  data.xtraId === xtra);
      if(existsInGrid && existsInGrid.length > 0){
        this.isXtraIdExistsError = true;
        return;
      }
        let i = this.xtraIdArray.findIndex((x: any) => x.xtraId == xtra);
        let xtraIdObj: any = {
          programApplicationTypeDesc:this.xtraIdArray[i]['programApplicationTypeDesc'] ,
          programApplicationTypeCode: this.xtraIdArray[i]['programApplicationTypeCode'],
          xtraId: xtra,
          xtraIdValue:xtraIdValue
        };
        let xtrArray = this.xtraIdItems;        
        xtrArray.push(xtraIdObj);    
        this.xtraIdItems = [...[]];
        this.cdr.detectChanges();
        setTimeout(() => {
          this.xtraIdItems = [...xtrArray];
          this.cdr.detectChanges();
       //   window.scrollTo(0, document.body.scrollHeight);
        }, 10);
        this.isExtraIdAddedtoGrid = true;
        this.projectDescForm.get('xtraIdType')?.setValue("sel");   
        this.projectDescForm.get('xtraIdValue')?.setValue(null);     
        this.projectDescForm.get('xtraIdValue')?.disable();     

    
  }

  validateProgramId(val: string){
    let lastValue = "";

    if(this.programIdRegex){
    if (!val.match(this.programIdRegex))
      val = lastValue;
    else
      lastValue = val;
    }
  }

  

  onProgramIdChanged(){
    this.programIdMaskValue ="";
    this.programIdPrefixValue="";
    this.checkResult = true;
    this.isProgramIdExistsError = false;
    this.showProgramIdReqdError = false;
    this.showProgramIdValReqdError = false;
    this.projectDescForm.get('programIdValue')?.enable();
    this.projectDescForm.get('programIdValue')?.setValue("");
    let currentVal= this.projectDescForm.get('programIds')?.value;

    this.currentSelectProgramIdObj =  this.programIdArray.find((x: any)=> x.programId === currentVal);
    console.log(this.currentSelectProgramIdObj);
    let formatMask =this.currentSelectProgramIdObj?.formatMask;
    if(formatMask?.trim()){
      this.programIdMaxLength = formatMask.length;
    }else{
      this.programIdMaxLength = "20";
    }
  
    if( this.currentSelectProgramIdObj.errorText){
      this.programIdHasErrorText = true;
    }else{
      this.programIdHasErrorText = false;
    }
  
  if( this.currentSelectProgramIdObj.progDistrictTypeCode == "BIN"){
    this.programIdMaskValue ="0000000";
    this.programIdPrefixValue = "BIN ";
  }else if( this.currentSelectProgramIdObj.progDistrictTypeCode == "VCP"){
    this.programIdMaskValue ="000000"
    this.programIdPrefixValue = "A";
  }else if( this.currentSelectProgramIdObj.progDistrictTypeCode == "SPDES"){
    this.programIdPrefixValue = "NY"
    this.programIdMaskValue = "AAAAAAA"
  } else if( this.currentSelectProgramIdObj.progDistrictTypeCode == "DEC"){
    this.programIdMaskValue = "0-0000-00000";
  }
  }

  onInput(val : any){
    this.projectDescForm.get('programIdValue')?.setValue(val);
    this.isProgramIdExistsError = false;
  }

  addProgramIDToGrid(){
    this.regExpressionError = '';
    this.checkResult = true;
    this.addprogramIdClicked = true;
    this.showProgramIdValReqdError = false;
    let program = this.projectDescForm.get('programIds')?.value; 
    let programIdValue = this.projectDescForm.get('programIdValue')?.value;     
    if(program == "sel"){
      this.showProgramIdReqdError = true;
      if(!programIdValue){
        this.showProgramIdValReqdError = true;
      }
      return;
    }else if(!programIdValue){      
     this.showProgramIdValReqdError = true;
      return;
    }
    else{
      let regIndex = this.programIdArray.findIndex((x: any) => x.programId == program);
      const exp = this.programIdArray[regIndex]['editMask'];
      const regErr = this.programIdArray[regIndex]['errorText'];
      
          if(exp){
        const regExp = new RegExp(exp);  
        this.checkResult = regExp.test(programIdValue);
        if(!this.checkResult){
          this.regExpressionError = regErr;
          return;
        }
      }
  
      let existsInGrid = this.programIdItems.filter((data:any)=> data.programId === program && data.programIdValue === programIdValue );
        if(existsInGrid && existsInGrid.length > 0){
          this.isProgramIdExistsError = true;
          return;
        }
        let i = this.programIdArray.findIndex((x: any) => x.programId == program);
        let programIdObj: any = {
          progDistrictTypeCode: this.programIdArray[i]['progDistrictTypeCode'],
          progDistrictTypeDesc: this.programIdArray[i]['progDistrictTypeDesc'],
          programId: program,
          programIdValue: programIdValue
        };     
        let programArray = this.programIdItems;  
        programArray.push(programIdObj);     
        this.programIdItems = [...[]];
        this.cdr.detectChanges();
        setTimeout(() => {
          this.programIdItems = [...programArray];
          this.cdr.detectChanges();
          // window.scrollTo(0, document.body.scrollHeight);
        }, 10);
        this.isprogramIdAddedtoGrid = true;
        this.projectDescForm.get('programIds')?.setValue("sel");
        this.projectDescForm.get('programIdValue')?.setValue(null);
        this.projectDescForm.get('programIdValue')?.disable();

    }
    
  }

  addSplAttCodeToGrid(){
    this.addsplAttnCode = true;
    this.spclAttnCodeReqError = false;
    let specialAttn = this.projectDescForm.get('splAttnCodes')?.value;
    if(specialAttn == 'sel'){
      this.spclAttnCodeReqError = true;
      return;
    }
    let existsInGrid = this.specialAttnCodeItems.filter((data:any)=> data.specialAttnCd === specialAttn);
      if(existsInGrid && existsInGrid.length > 0){
        this.isSpecialAttnExistsError = true;
        return;
      }
      let i = this.specialAttnCodeArray.findIndex((x: any) => x.specialAttnCd == specialAttn);
      let specialAttnCodeObj: any = {
        specialAttnTypeCode: this.specialAttnCodeArray[i]['specialAttnTypeCode'],
        specialAttnTypeDesc: this.specialAttnCodeArray[i]['specialAttnTypeDesc'],
        specialAttnCd: specialAttn,
      };      
      let specialAttnArray = this.specialAttnCodeItems;        
      specialAttnArray.push(specialAttnCodeObj);     
      this.specialAttnCodeItems = [...[]];
      this.cdr.detectChanges();
      setTimeout(() => {
        this.specialAttnCodeItems = [...specialAttnArray];
        this.cdr.detectChanges();
      }, 10);
      this.isspecialAttnCodeAddedtoGrid = true;
      this.projectDescForm.get('splAttnCodes')?.setValue("sel");
  }
  
  addToGrid() {
    this.addClicked = true;
    let sic = this.projectDescForm.get('sicCode')?.value;
    let naics =
      this.projectDescForm.get('naicsCode')?.value &&
      this.projectDescForm.get('naicsCode')?.value != 'null'
        ? this.projectDescForm.get('naicsCode')?.value
        : '';
    if (sic) {
      let sicIndexes = this.items.reduce((obj: any, e: any, i: number) => {
        if (this.items[i].sicCode == sic) obj.push(i);
        return obj;
      }, []);
      let count = 0;
      sicIndexes.forEach((index: number) => {
        if (
          this.items[index].naicsCode == naics ||
          (!this.items[index]?.naicsCode && !naics)
        ) {
          count++;
        }
      });

      if (count > 0) {
        this.isError = true;
        return;
      } else {
        this.isError = false;
      }
      const naicsObj = this.naicsArray.find((x: any) => x.naicsCode === naics);
      naics = naicsObj?.naicsDesc;
      let i = this.sicArray.findIndex((x: any) => x.sicCode == sic);
      let obj: any = {
        sicDesc: this.sicArray[i]['sicDesc'],
        sicCode: sic,
        naicsCode: naics && naics != 'null' ? naics : '',
      };      
      let array = this.items;
      array.push(obj);       
      this.items = [...[]];
      this.cdr.detectChanges();
      setTimeout(() => {
        this.items = [...array];
        this.cdr.detectChanges();
      }, 10);
      this.isAddedtoGrid = true;
      this.projectDescForm.get('sicCode')?.setValue(null);
      this.projectDescForm.get('naicsCode')?.setValue(null);
      this.addClicked = false;
    }
  }

  onStepperChange(event: any) {
    this.selectedIndex = event.selectedIndex;
    if (this.selectedIndex == 0) {
      this.commonService.removeGreenBackground();
      if (event.previouslySelectedIndex == 1) {
        this.stepperGoback();
      }
    } else if (this.selectedIndex == 1) {
      if (!this.isFromStepperGoBack) {
        this.getSelectedTypes();
      }
    } else {
      if(this.hasOneContactAgents){
        this.submitApiIfHasOneContactAgent();
        
      setTimeout(() => {
        this.completeProjectInformationStep();
      }, 20);
      if (this.hasZeroContactAgents || this.isContactEmpty || this.permitAssignHasEmergencyIndicator) {
        this.stepThreeCompleted = true;
        this.commonService.navigateToMainPage();
      }
      return;        
      }
      if (event.previouslySelectedIndex == 1) {
        this.stepperGoback();
      }
    }
  }

  async openConfirmModal() {
    this.modalReference = await this.warningModal.open();
  }

  facilityTypeChange(e: any, item: any) {
    this.isChecBoxChanges = true;
    const swWasteTypes: UntypedFormArray = this.projectDescForm.get(
      'solidWasteTypes'
    ) as UntypedFormArray;

    if (e.target.checked) {
      this.swSelectedState.push({
        swFacilityType: item.swFacilityTypeId,
        swFacilitySubTypes: [],
      });
      swWasteTypes.push(new UntypedFormControl(e.target.value));
    } else {
      let i: number = 0;
      let toBeUnchecked = this.swSelectedState.findIndex(
        (obj: any) => obj.swFacilityType === item.swFacilityTypeId
      );
      if (toBeUnchecked >= 0) this.swSelectedState.splice(toBeUnchecked, 1);
      swWasteTypes.controls.forEach((item: any) => {
        if (item.value == e.target.value) {
          swWasteTypes.removeAt(i);
          return;
        }
        i++;
      });
    }
  }

  subTypeChange(e: any, item: any, subItem: any) {
    this.isChecBoxChanges = true;
    const swWasteTypes: UntypedFormArray = this.projectDescForm.get(
      'swSubTypes'
    ) as UntypedFormArray;
    let index = this.swSelectedState.findIndex(
      (obj) => obj.swFacilityType === item.swFacilityTypeId
    );
    if (e.target.checked) {
      if (index >= 0) {
        this.swSelectedState[index].swFacilitySubTypes.push({
          swfacilitySubType: subItem.swFacilitySubTypeId,
        });
      } else {
        this.swSelectedState.push({
          swFacilityType: item.swFacilityTypeId,
          swFacilitySubTypes: [
            { swfacilitySubType: subItem.swFacilitySubTypeId },
          ],
        });
        swWasteTypes.push(new UntypedFormControl(e.target.value));
      }
    } else {
      let i: number = 0;
      let toBeUnchecked = this.swSelectedState[
        index
      ].swFacilitySubTypes.findIndex(
        (obj: any) => obj.swFacilitySubType === subItem.swFacilitySubTypeId
      );
      if (toBeUnchecked)
        this.swSelectedState[index].swFacilitySubTypes.splice(toBeUnchecked, 1);
      if (this.swSelectedState[index].swFacilitySubTypes.length === 0)
        this.swSelectedState.splice(index, 1);
      swWasteTypes.controls.forEach((item: any) => {
        if (item.value == e.target.value) {
          swWasteTypes.removeAt(i);
          return;
        }
        i++;
      });
    }
  }

  onDevTypeCheckboxChange(e: any) {
    this.isChecBoxChanges = true;
    const developmentType: UntypedFormArray = this.projectDescForm.get(
      'developmentType'
    ) as UntypedFormArray;

    if (e.target.checked) {
      if (e.target.value == 10) {
        this.unCheckAll(this.developmentCheckBoxes, developmentType, 'dev');
      } else {
        this.uncheckNA(this.developmentCheckBoxes, developmentType, 'dev');
      }
      developmentType.push(new UntypedFormControl(e.target.value));
    } else {
      let i: number = 0;
      developmentType.controls.forEach((item: any) => {
        if (item.value == e.target.value) {
          developmentType.removeAt(i);
          return;
        }
        i++;
      });
    }
  }

  uncheckNA(
    checkBoxes: QueryList<ElementRef>,
    formArray: UntypedFormArray,
    flag: string
  ) {
    let naId = 0;
    if (flag == 'dev') {
      naId = 10;
    } else {
      naId = 9;
    }
    formArray?.controls.forEach((element, i) => {
      if (element.value == naId) {
        formArray.removeAt(i);
      }
    });
    checkBoxes?.map((item: ElementRef) => {
      if (item.nativeElement.value == naId) {
        item.nativeElement.checked = false;
      }
    });
  }
  unCheckAll(
    checkBoxes: QueryList<ElementRef>,
    formArray: UntypedFormArray,
    flag: string
  ) {
    formArray?.clear();
    let id = 0;
    if (flag === 'dev') {
      id = 10;
    } else if (flag === 'struc') {
      id = 9;
    }
    checkBoxes?.map((item: ElementRef) => {
      if (item.nativeElement.value != id) {
        item.nativeElement.checked = false;
      }
    });
  }
  onStructureTypeCheckboxChange(e: any) {
    this.isChecBoxChanges = true;
    const structureType: UntypedFormArray = this.projectDescForm.get(
      'structureType'
    ) as UntypedFormArray;
    if (e.target.checked) {
      if (e.target.value == 9) {
        this.unCheckAll(this.structureCheckboxes, structureType, 'struc');
      } else {
        this.uncheckNA(this.structureCheckboxes, structureType, 'struc');
      }
      structureType.push(new UntypedFormControl(e.target.value));
    } else {
      let i: number = 0;
      structureType.controls.forEach((item: any) => {
        if (item.value == e.target.value) {
          structureType.removeAt(i);
          return;
        }
        i++;
      });
    }
  }

  ngOnInit(): void {
    this.commonService.getSystemParameters().subscribe(data=>{
      this.systemParameters=data;
    });
    this.getConfigs();
    this.getSicCodes();    
    this.getSummaryData();
    this.getAllErrorMsgs();
    this.getSelectedTypes();
    this.getProjectStatus();
    this.getXtraIds();
    this.keywordTableData();
    this.applicantService
      .getAllExistingApplicants('1', 'C')
      .subscribe((data) => {
        let applicants = data.applicants;
        if (!applicants) {
          this.hasZeroContactAgents = true;
        } else if (applicants?.length === 1) {
          this.hasOneContactAgents = true;
        }
      });
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }

  onSicChange(selectedObj:any){
    this.getNaicsList(selectedObj.sicCode);
    this.formControls['naicsCode'].setValue('');
  }

  onXtraIdChange(object:any){
    console.log(object);
  }

  onSpclAttnCodeChange(){
    this.spclAttnCodeReqError = false;
    this.isSpecialAttnExistsError = false;
  }

  getProjectStatus() {
    this.projectService
      .getProjectStatus(localStorage.getItem('projectId'))
      .then(
        (res) => {
          let i = res.findIndex((x: any) => x.activityStatusId === 3);
          if (i >= 0)
            res[i].completed === 'Y'
              ? (this.stepThreeCompleted = true)
              : (this.stepThreeCompleted = false);
          else this.stepThreeCompleted = false;
        },
        (error: any) => {
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      );
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }
  setPermitType(permitTypes: any) {
    if (permitTypes && permitTypes?.length > 0) {
      this.stepOneCompleted = true;
      this.selectedPermitTypes = [...permitTypes];
      this.checkIfStreamToBeDisplayed();
      this.checkIfDamnTobeDisplayed();
      this.checkSolidWasteFieldsTobeDisplayed();
    }
    
  }

  getSolidWasteTypes() {
    this.projectService
      .getSolidWasteArray()
      .then((response: any) => {
        this.solidWasteChecboxTypes = response;
      })
      .catch((err) => {
        this.solidWasteChecboxTypes = [];
        this.serverErrorMessage = this.errorService.getServerMessage(err);
        this.showServerError = true;
        throw err;
      });
  }

  checkSolidWasteFieldsTobeDisplayed() {
    let count = 0;
    this.selectedPermitTypes?.forEach((permitCode: string) => {
      if (this.solidWastePermits.includes(permitCode)) {
        count++;
      }
    });
    if (count > 0) {
      this.isSolidWasteFieldsDisplay = true;
      this.getSolidWasteTypes();
    } else {
      this.isSolidWasteFieldsDisplay = false;
      this.projectDescForm.removeControl('solidWasteTypes');
      this.projectDescForm.removeControl('swSubTypes');
    }
  }

  checkIfDamnTobeDisplayed() {
    let count = 0;
    this.selectedPermitTypes?.forEach((permitCode: string) => {
      if (this.damTypePermits.includes(permitCode)) {
        count++;
      }
    });
    if (count > 0) this.isDamTypeTobeDisplayed = true;
    else {
      this.isDamTypeTobeDisplayed = false;
      this.projectDescForm.removeControl('damType');
    }
    this.initiateForm();
    if (!!this.projectDetails) this.setFormData();
  }

  checkIfStreamToBeDisplayed() {
    this.isConstructionDisplay = false;
    this.isStreamDisplayed = false;
    if (
      this.selectedPermitTypes.includes('P1S') ||
      this.selectedPermitTypes.includes('P2S') ||
      this.selectedPermitTypes.includes('P3S')
    )
      this.isStreamDisplayed = true;
    this.selectedPermitTypes?.forEach((permitCode: string) => {
      if (this.constructionTypePermits.includes(permitCode)) {
        this.isConstructionDisplay = true;
        this.isStreamDisplayed = true;
      }
    });
    this.initiateForm();
    if (!!this.projectDetails) this.setFormData();
  }
  
  getSelectedTypes() {
    this.projectService.getPermitForSummaryScreen().then(
      (res) => {
        const etrackPermits = get(res, 'etrack-permits', {});
        const dartPermits = get(res, 'dart-permits', {});

        if (!isEmpty(etrackPermits) || !isEmpty(dartPermits)) {
          this.stepOneCompleted = true;
        }
        else {
          this.stepOneCompleted = false;
        }
        this.checkIfStreamToBeDisplayed();
        this.checkIfDamnTobeDisplayed();
        this.checkSolidWasteFieldsTobeDisplayed();
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
    this.projectService
      .getExistingPermitTypes()
      .then((response) => {
        this.selectedPermitTypes = response ? response : [];

        if (this.selectedPermitTypes?.length > 0) {
          this.stepOneCompleted = true;
        } else {
          this.stepOneCompleted = false;
        }
        this.checkIfStreamToBeDisplayed();
        this.checkSolidWasteFieldsTobeDisplayed();
        this.checkIfDamnTobeDisplayed();
        this.getProjectDetails();
        this.keywordTableData();
      })
      .catch((err) => {
        this.stepOneCompleted = false;
        this.selectedPermitTypes = [];
        this.serverErrorMessage = this.errorService.getServerMessage(err);
        this.showServerError = true;
        throw err;
      });
  }

  onHasPermits(ev: any) {
    this.stepOneCompleted = ev;
  }

  getSummaryData() {
    const category = 'P';
    const associatedInd = '1';
    this.projectService.getAssociateDetails(associatedInd, category).then(
      (res) => {
        this.applicantsCollection = res;
        this.applicantsCollection?.applicants?.forEach((obj: any) => {
          if (
            obj.applicantType == 'S' ||
            obj.applicantType == 'F' ||
            obj.applicantType == 'M'
          ) {
            this.isBinShow = true;
          }
        });
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  getSicCodes() {
    this.projectService
      .getSicCodes()
      .then((response) => {

        this.sicArray = response.map((obj:any)=> { 
          return {...obj,sicDesc:`${obj.sicCode} - ${obj.sicDesc}`}
        });
        this.setGrid();
      })
      .catch((err: any) => {
        this.sicArray = [];
        this.serverErrorMessage = this.errorService.getServerMessage(err);
        this.showServerError = true;
        throw err;
      });
  }

  getXtraIds(){
    this.projectService
      .getXtraIds()
      .then((response: any) => {
        this.xtraIdArray = response['XTRA_ID'].map((obj:any)=> { 
          return {...obj,xtraId:`${obj.programApplicationTypeCode} - ${obj.programApplicationTypeDesc}`}
        });
        this.programIdArray = response['PROG_ID'].map((obj:any)=> { 
          if (obj.progDistrictTypeCode == 'SW' && obj.formatMask.length == 7) obj.formatMask = obj.formatMask+ '@';
          return {...obj,programId:`${obj.progDistrictTypeCode} - ${obj.progDistrictTypeDesc}`}
        });
        this.specialAttnCodeArray = response['SPL_ATTN_CODE'].map((obj:any)=> { 
          return {...obj,specialAttnCd:`${obj.specialAttnTypeCode} - ${obj.specialAttnTypeDesc}`}
        });
        this.setXtraIdGrid();
        this.setprogramIdGrid();
        this.setspecialAttentionCodeGrid();
      })
      .catch((err: any) => {
        this.sicArray = [];
        this.serverErrorMessage = this.errorService.getServerMessage(err);
        this.showServerError = true;
        throw err;
      });
  }

get formControls() {
    return this.projectDescForm.controls;
  }

  getConfigs() {
    this.projectService.getProjectConfigs().then(
      (response) => {
        response.developmentTypes.forEach(
          (obj: any) => (obj.isChecked = false)
        );
        response.residentialDevelopType.forEach(
          (obj: any) => (obj.isChecked = false)
        );
        this.proposedUseCodes = response.proposedUseCodes;
        this.devTypes = response.developmentTypes;
        this.structureTypes = response.residentialDevelopType;
        setTimeout(() => {
          this.setCheckBoxes();
        });
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  onStepChange(e: any) {
    this.selectedIndex = e.selectedIndex;
    this.stepperClickEvent(this.selectedIndex);
  }

  getApiData(formData: any) {
    let modData = formData;
    modData.briefDesc = modData.briefDesc.replace(/\s\s+/g, ' ').trim();
    modData.proposedStartDate = modData?.proposedStartDate
      ? this.datePipe.transform(modData?.proposedStartDate, 'MM/dd/yyyy')
      : '';
    modData.estmtdCompletionDate = modData?.estmtdCompletionDate
      ? this.datePipe.transform(modData?.estmtdCompletionDate, 'MM/dd/yyyy')
      : '';
    let codeArray: any = [];
    this.items.forEach((element: any) => {
      let sic = element['sicCode'];
      let naics = element.naicsCode;
      codeArray.push({ [sic]: naics });
    });
    delete modData.sicCode;
    delete modData.naicsCode;
    modData.sicCodeNaicsCode = codeArray;

    let i = modData.developmentType.findIndex((x: any) => x == '0');
    let j = modData.structureType.findIndex((x: any) => x == '0');
    if (i >= 0) modData.developmentType.splice(i, 1);
    if (j >= 0) modData.structureType.splice(j, 1);
    if (
      !modData?.developmentType?.includes('5') &&
      !modData?.developmentType?.includes('1')
    ) {
      delete modData.sicCodeNaicsCode;
    }
    modData.swFacilityTypes = this.swSelectedState;
    delete modData.solidWasteTypes;
    delete modData.swSubTypes;
    return modData;
  }

  completeProjectInformationStep(isSubmit?: boolean) {
    this.stepTwoCompleted = true;
    this.cdr.detectChanges();
    this.submitted = false;
    this.projectDescForm.reset();
    const developmentType = this.projectDescForm.controls
      .developmentType as UntypedFormArray;
    const structureType = this.projectDescForm.controls
      .structureType as UntypedFormArray;
    const swtype = this.projectDescForm.controls
      .solidWasteTypes as UntypedFormArray;
    const swSubType = this.projectDescForm.controls
      .swSubTypes as UntypedFormArray;
    this.unCheckAll(this.developmentCheckBoxes, developmentType, 'dev');
    this.unCheckAll(this.structureCheckboxes, structureType, 'struc');
    this.unCheckAll(this.swCheckboxes, structureType, 'sw');
    this.unCheckAll(this.swSubCheckboxes, structureType, 'swSub');
    this.uncheckNA(this.developmentCheckBoxes, developmentType, 'dev');
    this.uncheckNA(this.structureCheckboxes, structureType, 'struc');
    this.items = [];
    this.addClicked = false;
    if (isSubmit) {
      this.initiateForm();
      this.checkIfStreamToBeDisplayed();
      this.checkIfDamnTobeDisplayed();
      this.checkSolidWasteFieldsTobeDisplayed();
      this.getSicCodes();
      this.getXtraIds();
    }
    this.ngZone.run(() => {
      this.stepper.next();
      this.cdr.detectChanges();
    });
  }

  onFormSubmit(stepper: MatStepper) {
    if (this.isBinShow) {
      this.formControls['binNumbers'].setValue(this.bridgeIdsFormattedList);
    }
    this.submitted = true;

    if (
      this.projectDetails &&
      !this.projectDescForm.dirty &&
      this.projectDescForm.valid &&
      (this.swSelectedState.length > 0 || !this.isSolidWasteFieldsDisplay) &&
      !(this.addClicked || this.isAddedtoGrid || this.isChecBoxChanges)
    ) {
      if (this.hasOneContactAgents) {

        this.submitApiIfHasOneContactAgent();
      }
      setTimeout(() => {
        this.completeProjectInformationStep();
      }, 20);
      if (this.hasZeroContactAgents || this.isContactEmpty || this.permitAssignHasEmergencyIndicator) {
        this.stepThreeCompleted = true;
        this.commonService.navigateToMainPage();
      }
      return;
    }
    this.addClicked = false;
    this.isAddedtoGrid = false;
    this.isChecBoxChanges = false;
    if (
      this.projectDescForm.valid &&
      (this.swSelectedState.length > 0 || !this.isSolidWasteFieldsDisplay)
    ) {
      let naicsCodes : any[] = [];
      let apiData = this.getApiData(this.projectDescForm.value);
      delete apiData['xtraIdType'];
      delete apiData['xtraIdValue'];
      delete apiData['programIdValue'];
      naicsCodes  = apiData.sicCodeNaicsCode;
      let mappedCodes: { [x: string]: string; }[] = [];
      if(naicsCodes) {
        mappedCodes = naicsCodes.map(code =>{
          let key = Object.keys(code).toString();        
          let val = Object.values(code).toString();
          let x = val.split('-')[0].trim();
          let p  = {[key] : x};
          return p;
        });
      }
      let xtraidsList : any = {};
      if(this.xtraIdItems){
        this.xtraIdItems.forEach((e:any)=>{
            xtraidsList[e.programApplicationTypeCode]=e.xtraIdValue;
        })
      }      
      apiData.xtraIds = xtraidsList;
     interface programidsObj {[key: string] : string[]};
     let programidsList : programidsObj = {};
      let programIdMap : Map<string, string[]> = new Map();
     this.programIdItems = this.programIdItems.filter((id: any) => id.isFromEfind == false || id.isFromEfind == null);
      if(this.programIdItems){
        this.programIdItems.forEach((id : any) =>{
          let keys = Object.keys(programidsList);
          let key = id.progDistrictTypeCode;
          if(keys.includes(key)){                      
            programidsList[key].push(id.programIdValue);
          }else{
            programidsList[key] = [id.programIdValue];
          }
          
        });        
      }
      apiData.programIds = programidsList;
      let splAttnCodeList : any = [];
      if(this.specialAttnCodeItems){
        this.specialAttnCodeItems.forEach((e:any)=>{
          splAttnCodeList.push(e.specialAttnTypeCode);
        })
      }
      apiData.splAttnCodes = splAttnCodeList;
      apiData.sicCodeNaicsCode = mappedCodes;
      this.clearBridgeIdErrors();
      this.utils.emitLoadingEmitter(true);
      this.projectService.submitProjectInfo(apiData).subscribe(
        (response) => {
          this.utils.emitLoadingEmitter(false);
          if (
            this.hasZeroContactAgents &&
            !(this.extPermits?.length || this.modPermits?.length)
          ) {
            this.stepThreeCompleted = true;
            this.commonService.navigateToMainPage();
          }
          if (
            this.hasOneContactAgents &&
            !(this.extPermits?.length || this.modPermits?.length)
          ) {
            this.avoidFormInitiate = true;
            setTimeout(() => {
              this.applicationContacts.applicationContactsForm.markAsDirty();
              this.applicationContacts.openWarningModal();
              this.avoidFormInitiate = false;
            }, 10);
          }
          setTimeout(() => {
            this.completeProjectInformationStep(true);
          }, 20);
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.stepTwoCompleted = false;
          this.showServerError = true;
          throw error;
        }
      );
    }
  }

  modifyNaicsPayload(apiData: any){

  }

  stepperClickEvent(tabIndex: any) {
    switch (tabIndex) {
      case 0:
        this.popupData.title = this.popUpTitles[0];
        break;
      case 1:
        this.popupData.title = this.popUpTitles[1];
        break;
      case 2:
        this.popupData.title = this.popUpTitles[2];
        break;
    }
  }

  addToBridgeIds(bin: any) {
    this.isAddedtoGrid = true;
    if (this.bridgeIds.indexOf(bin?.edbBin) != -1) {
      return;
    }
    this.bridgeIds.push(bin?.edbBin);
    let binObj = {
      bin: bin?.edbBin,
      edbBin: null,
    };
    this.addedBridgeIds.push(binObj);
    this.bridgeIdsFormattedList.push(binObj);
  }

  onAddBridgeId() {
    if (!this.binNumber || this.binNumber.length == 4) {
      this.showBridgeIdRequiredError = true;
      return;
    } else if (this.binNumber.length < 11 && this.binNumber.length > 4) {
      this.showBridgeIdMinLengthError = true;
      return;
    } else if (this.bridgeIds.includes(this.binNumber)) {
      this.showDuplicateBridgeIdError = true;
      return;
    } else {
      let binObj = {
        bin: this.binNumber,
        edbBin: null,
      };
      this.bridgeIds.push(this.binNumber);
      this.addedBridgeIds.push(binObj);
      this.bridgeIdsFormattedList.push(binObj);
      this.isAddedtoGrid = true;
      (<HTMLInputElement>document.getElementById('binNumbers')).value = '';
      this.binNumber = '';
    }
  }

  getBridgeIds() {
    if (get(this.projectDetails, 'binNumbers', [])) {
      this.bridgeIdsFormattedList = get(this.projectDetails, 'binNumbers', []);
    }
    this.bridgeIds = this.bridgeIdsFormattedList.map((entry: any) => {
      return entry.bin;
    });
  }

  clearBridgeIdErrors() {
    this.showBridgeIdRequiredError = false;
    this.showDuplicateBridgeIdError = false;
    this.showBridgeIdMinLengthError = false;
  }

  setExtModPermits(obj: any) {
    this.extPermits = obj?.extPermits;
    this.modPermits = obj?.modPermits;
  }

  // setBinObject() {
  //   let binList: { bin: any; edbBin: any }[] = [];
  //   let addedBins : any[] = [];
  //   console.log('added bins', addedBins);

  //   Object.keys(this.addedBridgeIds).forEach((key : any) => {
  //     console.log(key, this.addedBridgeIds[key].bin);
  //     addedBins.push(this.addedBridgeIds[key].bin);
  //     });

  //   this.bridgeIds.forEach((binNumber: any) => {
  //       let binObj = {
  //       bin: binNumber,
  //       edbBin: binNumber,
  //     };
  //     binList.push(binObj);
  //   });
  //   return binList;
  // }

  binKeyup(ev: any) {
    this.clearBridgeIdErrors();
    this.binNumber = ev.target.value;
  }

  checkReg(ev:any):void{
    const exp = new RegExp("^BIN [0-9]{7}$");
    exp.test(ev.target.value);
  }

  onStepOneComplete(event: any) {
    this.stepOneCompleted = true;
    this.cdr.detectChanges();
    this.stepper?.next();
  }

  onHasModifyPermits(event: any) {
    this.stepOneCompleted = event;
  }

  getFormContacts() {
    this.contactList = [];
    this.projectService.getAppContactDetails().then((response: any) => {
      if (response) {
        this.formDetails = response['permit-assign'];
        this.permitAssignHasEmergencyIndicator = isEqual(get(response, 'emergencyInd', ''), 'E');
        if (Object.keys(this.formDetails).length === 0) {
          this.isContactEmpty = true;
        } else {
          for (let key in this.formDetails) {
            let formObject: any = {};
            let selectedRoleId = '';
            formObject.formName = this.formDetails[key].formName;
            formObject.contacts = this.formDetails[key].contacts;
            if (formObject.contacts.length == 1) {
              this.singleContact = true;
              selectedRoleId = '' + formObject.contacts[0].roleId + '';
            }
            formObject.applicationIds = this.formDetails[key].applicationIds;
            formObject.selected = selectedRoleId;
            formObject.permitFormId = this.formDetails[key].permitFormId;
            this.contactList.push(formObject);
          }
        }
      }
    });
  }

  submitApiIfHasOneContactAgent(){
    let apiData: any[] = [];
        this.contactList.forEach((contact: any) => {
          contact.applicationIds.forEach((id: any) => {
            apiData.push({
              applicationId: id,
              roleId: contact.contacts[0].roleId,
              permitFormId: contact.permitFormId,
            });
          });
        });
        this.utils.emitLoadingEmitter(true);
        this.projectService
          .submitAppContactsForm(apiData)
          .subscribe((response: any) => {
            this.avoidFormInitiate = true;
            this.utils.emitLoadingEmitter(false);
            this.commonService.navigateToMainPage();
            setTimeout(() => {
              this.applicationContacts.applicationContactsForm.markAsDirty();
              this.avoidFormInitiate = false;
            }, 0);
          });
  }
}

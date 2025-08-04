import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  QueryList,
  SimpleChange,
  SimpleChanges,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { takeUntil } from 'rxjs/operators';
import { DocumentService } from 'src/app/@shared/services/documentService';
import { ProjectService } from 'src/app/@shared/services/projectService';
import _, {
  flatten,
  get,
  isEmpty,
  values,
  isEqual,
  cloneDeep,
  flattenDeep,
  concat,
  groupBy,
  valuesIn,
  trim,
  intersection,
} from 'lodash';
import { Utils } from 'src/app/@shared/services/utils';
import { BehaviorSubject, fromEvent, Subject } from 'rxjs';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { ModalConfig } from 'src/app/modal.config';
import moment from 'moment';
import { ErrorService } from '../../../../@shared/services/errorService';
@Component({
  selector: 'app-permit-selection',
  templateUrl: './permit-selection.component.html',
  styleUrls: ['./permit-selection.component.scss'],
})
export class PermitSelectionComponent implements OnInit {
  // constructionTypePermits: any[] = [
  //   'CC',
  //   'CA',
  //   'DA',
  //   'DO',
  //   'EF',
  //   'ETF',
  //   'FW',
  //   'RZ2',
  //   'TW',
  //   'SD',
  //   'WQ',
  //   'WR',
  // ];
  constrnType:any = '';
  permitCategories: any = {};
  permitCategoriesList: any[] = [];
  systemParameters: any;
  @ViewChild('confirmApplicant', { static: true })
  confirmApplicant!: CustomModalPopupComponent;
  @ViewChild('validationPopup', { static: true })
  validatePopup!: CustomModalPopupComponent;
  @ViewChildren('developmentCheckBoxes')
  developmentCheckBoxes!: QueryList<ElementRef>;
  wetlandsBoxes: any = [];
  wastewaterSPDESBoxes: any = [];
  mineralResourceBoxes: any = [];
  airFacilityBoxes: any = [];
  waterWithdrawalBoxes: any = [];
  waterWayBoxes: any = [];
  wasteManagementBoxes: any = [];
  otherBoxes: any = [];
  wetlandsBoxesng: any = [];
  wastewaterSPDESBoxesng: any = [];
  mineralResourceBoxesng: any = [];
  airFacilityBoxesng: any = [];
  waterWithdrawalBoxesng: any = [];
  wasteManagementBoxesng: any = [];
  showServerError = false;
  serverErrorMessage!: string;
  otherBoxesng: any = [];
  @Input() selectedPermitTypes: any = [];
  @Input() selectedPermitGroups: any = {};
  @Input() permitSummaryResponse: any = [];
  @Output() nextClicked = new EventEmitter();
  @Output() onCheck = new EventEmitter();
  @Output() closeClicked = new EventEmitter();
  @Output() onSubmit = new EventEmitter();
  @Input() errorMsgObj: any;
  confirmConfig: { title: string; showHeader: boolean };
  isOpenPopUp = new BehaviorSubject<boolean>(false);
  isSubmitted: boolean = false;
  isSPDESExceeded: boolean = false;
  isNewPermitsMatchesWithExistingPermitsSelected:boolean = false;
  isNewPermitsMatchesWithPermitSummaryValues:boolean = false;
  isModExtPermitsMatchesWithPermitSummaryValues:boolean = false;
  userRoles: any[] = [];
  currentRegion: any;
  floatingValidateRegions = [3, 4, 5, 6];
  tidalValidateRegions = [1, 2, 3];
  longIslandWellValidate = [1, 2];
  ischeckboxChanged: boolean = false;
  noEndDate: boolean =false;
  isOpenFbeModal = new BehaviorSubject<boolean>(false);
  private unsubscriber: Subject<void> = new Subject<void>();
  modalConfig: ModalConfig = {
    title: 'APPLICATION FORM ENTRY',
    showHeader: false,
    onClose: () => {
      this.isOpenFbeModal.next(false);
      return true;
    },
    onDismiss: () => {
      this.isOpenFbeModal.next(false);
      return true;
    },
    shouldClose: () => {
      return true;
    },
    shouldDismiss: () => {
      return true;
    },
  };

  @ViewChild('modal') private modal!: CustomModalPopupComponent;
  @Input() newPermits: any[] = [];
  existingPermits: any = [];
  existingPermitsSelected: any = [];
  pendingPermits: any = [];
  selectedPermits: any = [];
  isEmergencyAuth!: boolean;
  showAtLeastOneRequiredError: boolean = false;
  showAquaticPesticideError: boolean = false;
  showYesNoRequiredError: boolean = false;
  showNaturalResourceGPError: boolean = false;
  showSPDESGPError: boolean = false;
  Showonly1NaturalResourceGPSPDESGPError: boolean = false;
  showWaterwayCoastalAndWetlandsGeneralCountError:boolean = false;

  showEmergencyAuth: boolean = false;
  showConModPopup: boolean = false;
  showConExtPopup: boolean = false;
  showPermitPopup: boolean = false;

  constructionPermitCodes:  any[] = [];
    // 'WQ',
    // 'CC',
    // 'CE',
    // 'DA',
    // 'DO',
    // 'EF',
    // 'RZ2',
    // 'FW',
    // 'SD',
    // 'TW',
    // 'WSR',
    // 'LI2',
    // 'ETS',
  operatingPermitCodes: any[] = [];
  //  'P1S', 'P2S', 'P3S', 'ASF', 'ATV', 'HW'];
  spdesPermitCodes = ['P1G', 'P1S', 'P2G', 'P2S', 'P3S', 'P3G'];
  modifiedPermits: any[] = [];
  extendedPermits: any[] = [];
  transferPermits: any[] = [];
  extendedPermitsActive: any[] = [];
  extendedPermitsExpired: any[] = [];
  formattedPermitArray: any[] = [];
  permitGroupKeys: any;
  groupedPermits: any;
  permits: any = [];
  numberOfBatchIds: number = 0;

  //scenario 1
  conModAnswerMap = new Map<string, string>([]);
  //scenario 2 extend to: checkbox
  conExtensionMap = new Map<string, string>([]);
  //scenario 3
  operatingPermitAnswerMap = new Map<string, string>([]);

  scenario1GroupedPermits: any[] = [];
  scenario2GroupedPermits: any[] = [];
  scenario3GroupedPermits: any[] = [];
  existingPermitsBackUp: any[] = [];
  pageFrom!: string;
  operatingPermitAdditionalQuestion: string =
    'Do the proposed changes qualify as administrative amendments or minor modifications under 6 NYCRR 201-6.6 (b) or (c)?';
  atvQCheckValue: any;
  spdesQCheckValue: any;
  asfQCheckValue: any;
  hwQCheckValue: any;
  fetchedPermitSummary:boolean = false;
  get canShowPendingPermits() {
    return !isEmpty(this.pendingPermits);
  }

  get canShowExistingPermits() {
    return !isEmpty(this.existingPermits) && !this.isEmergencyAuth;
  }
  get permitSummaryResponseValues(){
    const permitSummaryResponse = cloneDeep(this.permitSummaryResponse); 
    delete(permitSummaryResponse.validateInd);
    delete(permitSummaryResponse.emergencyInd);
    delete(permitSummaryResponse.constrnType);
    delete(permitSummaryResponse.receivedDate)
    return flatten(values(permitSummaryResponse))
  }
  constructor(
    private projectService: ProjectService,
    private utils: Utils,
    private commonService: CommonService,
    private docService: DocumentService,
    private errorService: ErrorService
  ) {
    this.confirmConfig = {
      title: '',
      showHeader: false,
    };
  }

  get getEmergencyAuth() {
    return localStorage.getItem('emergencyAuth');
  }

  yesClicked() {
    this.confirmApplicant.close();
    this.isOpenPopUp.next(false);
    this.onNext(3);
    this.onClose();
  }
  noClicked() {
    this.confirmApplicant.close();
    this.isOpenPopUp.next(false);
    this.onClose();
    this.onNext(2);
  }
  openPop() {
    this.confirmApplicant.open('sm');
    this.isOpenPopUp.next(true);
  }

  open(isMoreInfo?: number) {
    let url = '';
    isMoreInfo == 1
      ? (url = this.systemParameters.MORE_ABOUT_PERMITS)
      : (url = this.systemParameters.QUALIFIED_PERMITS);
    window.open(url, '_blank');
  }
  // checkIfEligibleForDisplay(response: any) { // removed in favor of omitting all region based crosschecks
  //   console.log("Regions in STEP 3", this.currentRegion)
  //   if (!this.floatingValidateRegions.includes(this.currentRegion)) {
  //     let i = response['Waterways, Coastlines & Wetlands'][
  //       'non-general'
  //     ].findIndex((item: any) => item.permitTypeCode === 'RZ2');
  //     if (i >= 0)
  //       response['Waterways, Coastlines & Wetlands']['non-general'].splice(
  //         i,
  //         1
  //       );
  //   }
  //   if (!this.tidalValidateRegions.includes(this.currentRegion)) {
  //     let i = response['Waterways, Coastlines & Wetlands'][
  //       'non-general'
  //     ].findIndex((item: any) => item.permitTypeCode === 'TW');
  //     if (i >= 0)
  //       response['Waterways, Coastlines & Wetlands']['non-general'].splice(
  //         i,
  //         1
  //       );
  //   }
  //   if (!this.longIslandWellValidate.includes(this.currentRegion)) {
  //     let i = response['Water Withdrawal']['non-general'].findIndex(
  //       (item: any) => item.permitTypeCode === 'LI'
  //     );
  //     if (i >= 0) response['Water Withdrawal']['non-general'].splice(i, 1);
  //   }
  // }
  ngOnChanges(changes: any) {
    if (
      changes?.selectedPermitTypes &&
      changes?.selectedPermitTypes?.currentValue?.length > 0
    ) {
      setTimeout(() => {
        this.checkSelectedBoxes();
      }, 500);
    }
  }

  ngAfterViewInit(): void {
    //Called after ngAfterContentInit when the component's view has been initialized. Applies to components only.
    //Add 'implements AfterViewInit' to the class.
    this.checkSelectedBoxes();
  }

  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val)=>{
      if(val)this.errorMsgObj=this.commonService.getErrorMsgsObj();
    })
  }

  ngOnInit(): void {
    
    this.commonService.getSystemParameters().subscribe(data=>{
      this.systemParameters=data;
    });
    this.userRoles = this.commonService.roles;
    this.commonService.stepThreePageFrom.subscribe((data: string) => {
      this.pageFrom = data;
    });
    this.getAllErrorMsgs();
    // this.getAllRegions();
    this.getPermitSummary();
    this.permitConfigs();
    
    this.projectService.getPermitType().then(
      (response) => {
        
        // this.checkIfEligibleForDisplay(response); // removed: omit region based filter
        this.permitCategories = response;
        this.permitCategoriesList = Object.keys(response);
        this.permitCategoriesList.sort((a: any, b: any) => {
          return this.permitCategories[a]['non-general'][0].permitCategoryId > 
          this.permitCategories[b]['non-general'][0].permitCategoryId ? 1 : -1;
        });
        // this.wetlandsBoxes =
        //   response['Waterways, Coastlines & Wetlands']['general'];
        // this.wetlandsBoxesng =
        //   response['Waterways, Coastlines & Wetlands']['non-general'];
        // this.wastewaterSPDESBoxes = response['Wastewater (SPDES)']['general'];
        // this.wastewaterSPDESBoxesng =
        //   response['Wastewater (SPDES)']['non-general'];

        // this.mineralResourceBoxes =
        //   response['Mining of Mineral Resources']['general'];
        // this.mineralResourceBoxesng =
        //   response['Mining of Mineral Resources']['non-general'];

        // this.airFacilityBoxes = response['Air Facility']['general'];
        // this.airFacilityBoxesng = response['Air Facility']['non-general'];

        // this.waterWithdrawalBoxes = response['Water Withdrawal']['general'];
        // this.waterWithdrawalBoxesng =
        //   response['Water Withdrawal']['non-general'];

        // this.wasteManagementBoxes = response['Waste Management']['general'];
        // this.wasteManagementBoxesng =
        //   response['Waste Management']['non-general'];

        // this.otherBoxes = response['Others']['general'];
        // this.otherBoxesng = response['Others']['non-general'];
      
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }

  permitConfigs(){
    this.projectService.getPermitConfigs().then(
      (response) => {
        this.constructionPermitCodes=response.C;
        this.operatingPermitCodes=response.O;
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  onCheckBoxChange(e: any, permit: any) {
    this.clearErrorMessages();
    let isChecked = e.target.checked;
    let value = e.target.value;
    this.ischeckboxChanged = true;
    if (isChecked) {
      this.selectedPermitTypes.push(value);
      this.selectedPermits.push(permit);
    } else {
      this.selectedPermitTypes.splice(
        this.selectedPermitTypes.findIndex(
          (val: any) => val?.permitTypeCode === value
        ),
        1
      );
      this.selectedPermits.splice(
        this.selectedPermits.findIndex(
          (val: any) => val?.permitTypeCode === value
        ),
        1
      );
    }

    let count = this.validateSPDES();
    if (count > 1) {
      this.isSPDESExceeded = true;
    } else {
      this.isSPDESExceeded = false;
      this.showSPDESGPError = false;
    }

    // this.onCheck.emit(this.selectedPermitTypes);
  }
  onNext(index: number) {
    this.nextClicked.emit(index);
  }
  closeHandler() {
    this.onClose();
  }
  onClose() {
    this.commonService.stepThreePageFrom.next('selection');
    this.closeClicked.emit(this.ischeckboxChanged);
  }
  validateSPDES() {

    
    const wasteWaterSpdesCheckboxGeneralChecked = this.developmentCheckBoxes.filter((element:any)=>{
      return element.nativeElement.classList.contains('wasteWaterSpdesCheckbox') && element.nativeElement?.checked
    })
    return wasteWaterSpdesCheckboxGeneralChecked.length
      // below code to be deleted after 06/24/2023
    let count = 0;
    console.log(this.permits)
    this.permits.forEach((item: any) => { // compare against the new permits
      if (
        this.spdesPermitCodes.includes(item?.permitTypeCode) &&
        item.generalPermitInd && parseInt(item.generalPermitInd) && 
        item.transferReqInd == 'N' &&
        item.renewReqInd == 'N'
      ) {
        count++;
      }
    });
   
    // this is to crosscheck with summary response. getSummaryPermits omits etrack-permits
    // expected to working to existing permits, but not working.
    this.permitSummaryResponseValues.forEach((item: any) => { // compare against the summary permits
      if (
        this.spdesPermitCodes.includes(item?.permitTypeCode)
      ) {
       count++;
      }
    });
    return count;
  }

  validateNaturalResourcesPermitTypeCount(){
    let count = 0;
    this.permits.forEach((permit: any) => {
      if (
        permit.generalPermitInd === '1' &&
        this.constructionPermitCodes.includes(permit.permitTypeCode)
      ) {
        count++;
      }
    })
    return count;
  }

  waterwayCoastalAndWetlandsGeneralCount(){
    // let count = 0;
    // this.permits.forEach((permit: any) => {
    //   if (
    //     permit.generalPermitInd === '1' &&
    //     permit.permitCategoryId === '1'
    //   ) {
    //     count++;
    //   }
    // })
    const waterwayCoastalAndWetlandsGeneralChecked = this.developmentCheckBoxes.filter((element:any)=>{
      return element.nativeElement.classList.contains('wetlandsBoxesCheckbox') && element.nativeElement?.checked
    })
    return waterwayCoastalAndWetlandsGeneralChecked.length
    // return count;

  }
  isContructionType(permits: any[]) {
    return permits.find((x) =>
      this.constructionPermitCodes.includes(x.permitTypeCode)
    );
  }

  extendYesNoClicked(
    ev: any,
    radio: HTMLInputElement,
    indicator: string,
    batchedPermits: any,
    checkValue?: boolean,
    permit?: any
  ) {
    let answer = ev.target.value;
    permit.isYesChecked = checkValue;
    if (indicator === '1') {
      this.conModAnswerMap.set(radio.id, answer);
    } else if (indicator === '3') {
      this.operatingPermitAnswerMap.set(radio.id, answer);
    }
  }
  questionYesNoClicked(permitType: any, checkvalue: any) {
    if (permitType === 'ATV') {
      this.atvQCheckValue = checkvalue;
    } else if (permitType === 'ASF') {
      this.asfQCheckValue = checkvalue;
    } else if (permitType === 'SPDES') {
      this.spdesQCheckValue = checkvalue;
    } else if (permitType === 'HW') {
      this.hwQCheckValue = checkvalue;
    }
  }

  onScenarioTwoExtendClicked(ev: any, checkbox: HTMLInputElement) {
    let answer = ev.target.value;
    let checked = ev.target.checked;
    if (checked) {
      this.conExtensionMap.set(checkbox.id, answer);
    } else {
      this.conExtensionMap.delete(checkbox.id);
    }
  }

  closeIsClicked() {
    this.onClose();
    this.modal.close();
  }

  //scenario 1
  onShowConstructionOperatingPopup() {
    this.isSubmitted = true;
    this.clearErrorMessages();
    const selectedPermitTypeParams = this.getSelectedPermitTypeParams();
    const keyValue: any = {};
    this.selectedPermits.forEach(
      (x: { permitTypeCode: string | number; applicationId: any }) => {
        keyValue[x.permitTypeCode] = x.applicationId;
      }
    );
    selectedPermitTypeParams.forEach((x) => {
      if (!x.applicationId) {
        if (keyValue[x.permitTypeCode]) {
          x.applicationId = keyValue[x.permitTypeCode];
        } else {
          x.applicationId = '';
        }
      }
    });
    // filter out the preselected permits to remove from the payload
    const selectedPermitTypeParamsSelectedNow = selectedPermitTypeParams
                                      .filter(item => isEmpty(item.applicationId))
   // const existingPermitsParams = this.getExistingPermitsParamsNew();// Investigate: use of this function, line removed
     this.permits = concat(
      selectedPermitTypeParamsSelectedNow,
      // existingPermitsParams
      // pendingPermitsParams
     );

    /*Selected Permit from Permit Selection API*/
    
    this.selectedPermits = this.selectedPermits.filter((e: any) => e.applicationId);
    selectedPermitTypeParams.forEach((e: any) => {
      if (e.applicationId == '') this.selectedPermits.push(e);
    });
    // this.existing permits were mapped with existing values. now the same variable is reassigned
  // this.existingPermits= existingPermitsParams; // if no existing permits is selected, this will be empty, line removed
   if(!this.isEmergencyAuth){
    this.existingPermitsSelected = this.getExistingPermitsParamsNew();
    if (isEmpty(this.permits) && isEmpty(this.existingPermitsSelected)) {
      this.showAtLeastOneRequiredError = true;
      return;
    }
   }

    if (this.permits.length === 1) { // if only one permit is selected
      if(!this.permitSummaryResponse || !this.permitSummaryResponse['etrack-permits']?.length){
        // if there are no new permits in summary screen
        let permit: any = this.permits[0];
        if (permit.permitTypeCode === 'CC') {
          this.showAquaticPesticideError = true;
          return;
        }
      }
      
    }
    this.isSPDESExceeded = false;
    this.showNaturalResourceGPError = false;
    this.Showonly1NaturalResourceGPSPDESGPError = false;
    
    let SPDESGPCount = this.validateSPDES();
    let naturalResourceGPCount = this.validateNaturalResourcesPermitTypeCount();
    if (naturalResourceGPCount > 1) {
      this.showNaturalResourceGPError = true;
      return;
    } else if (SPDESGPCount > 1) {
      this.showSPDESGPError = true;
      return;
    } else if (SPDESGPCount > 1 && naturalResourceGPCount > 1) {
      this.Showonly1NaturalResourceGPSPDESGPError = true;
      return;
    }
    
    let waterwayCoastalAndWetlandsGeneralCount = this.waterwayCoastalAndWetlandsGeneralCount();
    this.showWaterwayCoastalAndWetlandsGeneralCountError = false;
    if(waterwayCoastalAndWetlandsGeneralCount > 1){
      this.showWaterwayCoastalAndWetlandsGeneralCountError = true;
      return;
    }

    this.isNewPermitsMatchesWithExistingPermitsSelected = false;
    this.isNewPermitsMatchesWithPermitSummaryValues = false;
    this.isModExtPermitsMatchesWithPermitSummaryValues = false;
    if(this.newPermitsMatchesWithExistingPermitsSelected()){
      this.isNewPermitsMatchesWithExistingPermitsSelected = true;
      return;
    }
    if(this.newPermitsMatchesWithPermitSummaryValues()){
      this.isNewPermitsMatchesWithPermitSummaryValues = true;
      return;
    }

    if(this.modExtPermitsMatchesWithPermitSummaryValues()){
      this.isModExtPermitsMatchesWithPermitSummaryValues = true;
     
      return;
    }
    
    let selectedModificationCodes: any[] = [];
    this.modifiedPermits.forEach((permit: any, i: number) => {
      selectedModificationCodes.push(permit.permitTypeCode);
    });
   
    let batchedPermits = groupBy(this.modifiedPermits, 'batchId');

    if (
      // selectedModificationCodes.some((code) =>
      //   this.operatingPermitCodes.includes(code)
      // ) ||
      // selectedModificationCodes.some((code) =>
      //   this.constructionPermitCodes.includes(code)
      // )
      selectedModificationCodes.length
    ) {
      this.groupedPermits = groupBy(this.modifiedPermits, 'batchId');
      let keys: any = [];
      Object.keys(this.groupedPermits).forEach((key: any) => {
        keys.push(key);
      });
      this.numberOfBatchIds = keys.length;
      this.scenario1GroupedPermits = this.groupedPermits;
      //This has all the existing permits groupded by batchId
      this.permitGroupKeys = keys;
      this.showConModPopup = true;
      this.validatePopup.open('permit-validate');
      this.extendedPermitsActive = [];
      this.extendedPermitsExpired = [];
      this.extendedPermitsExpired = this.extendedPermits.filter(
        (permit: any) => {
          const effectiveEndDate = moment(
            permit.effectiveEndDate,
            'MM/DD/YYYY'
          );
          const today = moment();
          return effectiveEndDate.isAfter(today);
        }
      );
      this.extendedPermitsActive = this.extendedPermits.filter(
        (permit: any) => {
          const effectiveEndDate = moment(
            permit.effectiveEndDate,
            'MM/DD/YYYY'
          );
          const today = moment();
          return effectiveEndDate.isBefore(today);
        }
      );
    } else {
      this.groupedPermits = groupBy(this.extendedPermits, 'batchId');
      let keys: any = [];
      Object.keys(this.groupedPermits).forEach((key: any) => {
        keys.push(key);
      });
      this.scenario2GroupedPermits = cloneDeep(this.groupedPermits);
      
      this.onPermitTypeSubmitRevised();
    }
  }
  // Scenario 2: If Construction EXT selected
  onShowConExtPopup() {
    //check condition to move on
    if (this.conModAnswerMap.size < this.numberOfBatchIds) {
      this.showYesNoRequiredError = true;
      return;
    }
    this.clearErrorMessages();
    this.showConModPopup = false;
    console.log("POP UP", this.showConModPopup )
    let selectedExtensionCodes: any[] = [];
    this.extendedPermits.forEach((permit: any) => {
      selectedExtensionCodes.push(permit.permitTypeCode);
    });
    if (
      selectedExtensionCodes.some((code) =>
        this.constructionPermitCodes.includes(code)
      )
    ) {
      this.groupedPermits = groupBy(this.extendedPermits, 'batchId');
      let keys: any = [];
      Object.keys(this.groupedPermits).forEach((key: any) => {
        keys.push(key);
      });
      this.scenario2GroupedPermits = cloneDeep(this.groupedPermits);

      this.permitGroupKeys = keys;
      this.showConModPopup = true;
      this.validatePopup.open('permit-validate');
      return;
    } else {
      this.scenario2GroupedPermits = [];
      this.onShowPermitPopup();
    }
  }
  //scenario 3
  onShowPermitPopup() {
    console.log("POP UP", this.showConModPopup )

    this.scenario3GroupedPermits = cloneDeep(this.groupedPermits);
    this.showConExtPopup = false;
    let operatingPermitCodes: any[] = [];
    this.modifiedPermits.forEach((permit: any) => {
      operatingPermitCodes.push(permit.permitTypeCode);
    });

    if (
      operatingPermitCodes.some((code) =>
        this.operatingPermitCodes.includes(code)
      )
    ) {
      this.groupedPermits = groupBy(this.modifiedPermits, 'batchId');

      let keys: any = [];
      Object.keys(this.groupedPermits).forEach((key: any) => {
        keys.push(key);
      });
      this.scenario3GroupedPermits = this.groupedPermits;

      this.numberOfBatchIds = keys.length;
      this.permitGroupKeys = keys;
      // this.showPermitPopup = true;
      // if (!isEmpty(this.groupedPermits)) {
      //   this.validatePopup.open('permit-validate');
      // } else {
      this.validatePopup.close();
      this.onPermitTypeSubmit();
      // }
      return;
    } else {
      this.scenario3GroupedPermits = [];
      this.validatePopup.close();
      this.onPermitTypeSubmit();
    }
  }

  onPermitTypeSubmitRevised() {
    this.clearErrorMessages();
    this.showConModPopup = true;

    console.log("POP UP", this.showConModPopup )

    if (this.conModAnswerMap.size < this.numberOfBatchIds) {
      this.showYesNoRequiredError = true;
      return;
    }

    this.permits.forEach((permit: any) => {
      let yesNoValue = 'N';
      if (permit.modReqInd && permit.modReqInd == 'Y') {
        if (permit.batchId && permit.batchId != undefined) {
          let yesno = this.conModAnswerMap.get(permit.batchId.toString());
          yesNoValue = yesno == undefined ? 'N' : yesno;
        }
      }
      permit.modQuestionAnswer = yesNoValue;
    });

    this.existingPermitsSelected.forEach((permit: any) => {
      let yesNoValue = 'N';
      if (permit.modReqInd && permit.modReqInd == 'Y') {
        if (permit.batchId && permit.batchId != undefined) {
          let yesno = this.conModAnswerMap.get(permit.batchId.toString());
          yesNoValue = yesno == undefined ? 'N' : yesno;
        }
      }
      permit.modQuestionAnswer = yesNoValue;
    });

    //TODO: investigate why to call this summary permits and push all the data to this.permits
    // commenting out temporarily
    // crosscheck for modify transfer and renew
    // this.getSummaryPermits().forEach((data: any) => {  
    //   this.permits.push(data);
    // });

    if (this.getEmergencyAuth === '') {
      localStorage.setItem('emergencyAuth', 'N');
    }
   /*
   // below code is commented in favor of removing previously selected items from the payload
    if (!isEmpty(this.selectedPermits)) {
      const selectedValues = flatten(values(this.selectedPermits));
      const selectedValuesFiltered = selectedValues.filter(
        (selectedValue: any) => {
          // remove duplicated new permit selection
          const existingInPermits = this.permits.find((permit: any) => {
            return (
              isEmpty(permit.applicationId) &&
              isEmpty(selectedValue.applicationId) && // the application id should be empty or null
              isEqual(permit.permitTypeCode, selectedValue.permitTypeCode)
            ); // the permitTypeCode should be equal
          });
          return isEmpty(existingInPermits);
        }
      );
      const tempSelectedValues = selectedValuesFiltered
        .map((item) => {
          return {
            applicationId: get(item, 'applicationId', null),
            permitTypeCode: get(item, 'permitTypeCode', null),
            roleId: get(item, 'roleId', ''),
            edbApplnId: get(item, 'edbApplicationId', null),
            newReqInd: 'Y',
            modReqInd: 'N',
            extnReqInd: 'N',
            renewReqInd: 'N',
            transferReqInd: 'N',
            pendingAppTransferReqInd: 'N',
            modQuestionAnswer: 'N',
            batchId: get(item, 'batchId', null),
            programId: get(item, 'programId', ''),
            edbPermitEffectiveDate: get(item, 'effectiveStartDate', null),
            edbPermitExpiryDate: get(item, 'effectiveEndDate', null),
            trackingInd: get(item, 'trackingInd', null),
            edbTransType: get(item, 'edbTransType', null),
            polEmissionInd: '',
            modExtReason: '',
            estCompletionDate: '',
          };
        })
        .filter((v) => v);
      this.permits = this.permits.concat(tempSelectedValues);
      //permits.push(tempSelectedValues);
    }
*/
    //TODO: Temp code to remove all null objects
    const filteredPermits = this.permits.filter((permit: any) => {
      let allValuesNull = true;
      for (let key in permit) {
        allValuesNull = isEmpty(trim(permit[key])) && allValuesNull;
      }
      return !allValuesNull;
    });
    filteredPermits.forEach((permit:any)=>{ // payload cleanup
      delete(permit.generalPermitInd)
      delete(permit.permitCategoryId)
    })
    const params = {
      constrnType: this.constrnType, //this.isContructionType(this.permits) ? 1 : null,
      // permits: filteredPermits,
      etrackPermits: filteredPermits,
      dartPermits: this.existingPermitsSelected,
      //dartPermits: concat(this.existingPermits, this.getSummaryPermits()),
      emergencyInd: this.getEmergencyAuth,
    };

    this.projectService.submitPermitTypes(params).subscribe(
      (response) => {
        this.onSubmit.emit(this.selectedPermitTypes);
        this.isSubmitted = false;
        this.onClose();
        this.onNext(2);
        // this.openPop();
        // this.onNext();
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  permitTypeSubmitPayloadSet(item: any, spread: any) {
    let answer: any = '';
    if (item.batchId) {
      answer = this.conModAnswerMap.get(item.batchId.toString());
    }
    let temp = {
      applicationId: get(item, 'applicationId', null),
      permitTypeCode: get(item, 'permitTypeCode', null),
      roleId: get(item, 'roleId', ''),
      edbApplnId: get(item, 'edbApplicationId', null),
      edbAuthId: get(item,'edbAuthId', null),
      newReqInd: 'N',
      modReqInd: 'N',
      extnReqInd: 'N',
      renewReqInd: 'N',
      transferReqInd: 'N',
      pendingAppTransferReqInd: 'N',
      modQuestionAnswer: 'N',
      batchId: get(item, 'batchId', null),
      programId: get(item, 'programId', ''),
      trackingInd: get(item, 'trackingInd', null),
      edbTransType: get(item, 'edbTransType', null),
      polEmissionInd: answer,
      modExtReason: '',
      estCompletionDate: '',
    };
    return { ...temp, ...spread };
  }

  onPermitTypeSubmit() {
    if (this.conModAnswerMap.size < this.numberOfBatchIds) { 
      this.showYesNoRequiredError = true;
      return;
    }
    if (
      this.operatingPermitAnswerMap.size < this.numberOfBatchIds &&
      this.showPermitPopup
    ) {
      this.showYesNoRequiredError = true;
      return;
    } else {
      this.validatePopup.close();
    }
    this.clearErrorMessages();

    let count = this.validateSPDES();
    if (count > 1) {
      this.isSPDESExceeded = true;
      return;
    }

    let naturalResourceGPCount: number = 0;
    let SPDESGPCount: number = 0;
    this.permits.forEach((permit: any) => {
      if (
        permit.generalPermitInd === '1' &&
        this.constructionPermitCodes.includes(permit.permitTypeCode)
      ) {
        naturalResourceGPCount++;
      }
      if (
        permit.generalPermitInd === '1' &&
        this.spdesPermitCodes.includes(permit.permitTypeCode)
      ) {
        SPDESGPCount++;
      }
    });

    if (naturalResourceGPCount > 1) {
      this.showNaturalResourceGPError = true;
      return;
    } else if (SPDESGPCount > 1) {
      this.showSPDESGPError = true;
      return;
    } else if (SPDESGPCount > 1 && naturalResourceGPCount > 1) {
      this.Showonly1NaturalResourceGPSPDESGPError;
      return;
    }

    //To do check if there is more than 1 natural resource permit
    //@ts-ignore
    if (this.getEmergencyAuth === '') {
      localStorage.setItem('emergencyAuth', 'N');
    }
    //scenario 1 payload
    let scenario1GroupedPermitsObjects: any = [];
    if (!isEmpty(this.scenario1GroupedPermits)) {
      const scenario1GroupedPermitsValues = Object.values(
        this.scenario1GroupedPermits
      );

      scenario1GroupedPermitsValues.forEach((scenario1GroupedPermitsValue) => {
        const temp = scenario1GroupedPermitsValue.map((item: any) => {
          let answer: string = '';
          if (item.batchId) {
            let batchId = item.batchId.toString();
            //@ts-ignore
            answer = this.conModAnswerMap.get(batchId);
          }
          return {
            applicationId: get(item, 'applicationId', null),
            permitTypeCode: get(item, 'permitTypeCode', null),
            roleId: get(item, 'roleId', ''),
            edbApplnId: get(item, 'edbApplicationId', null),
            edbAuthId: get(item, 'edbAuthId', null),
            newReqInd: 'N',
            modReqInd: 'Y',
            extnReqInd: 'N',
            renewReqInd: 'N',
            transferReqInd: 'N',
            pendingAppTransferReqInd: 'N',
            modQuestionAnswer: get(item,'modQuestionAnswer','N'),
            batchId: get(item, 'batchId', null),
            programId: get(item, 'programId', ''),
            trackingInd: get(item, 'trackingInd', null),
            edbTransType: get(item, 'edbTransType', null),
            polEmissionInd: answer,
            modExtReason: '',
            estCompletionDate: '',
          };
        });
        scenario1GroupedPermitsObjects =
          scenario1GroupedPermitsObjects.concat(temp);
      });
    }

    //scenario 2 payload
    let scenario2GroupedPermitsObjects: any = [];

    if (!isEmpty(this.scenario2GroupedPermits)) {
      const scenario2GroupedPermitsValues = Object.values(
        this.scenario2GroupedPermits
      );

      scenario2GroupedPermitsValues.forEach((scenario2GroupedPermitsValue) => {
        const temp = scenario2GroupedPermitsValue.map((item: any) => {
          let batchId = item.batchId.toString().trim();
          let transType = '';
          transType = this.conExtensionMap.get(batchId)!;
          return {
            applicationId: get(item, 'applicationId', null),
            permitTypeCode: get(item, 'permitTypeCode', null),
            roleId: get(item, 'roleId', ''),
            edbApplnId: get(item, 'edbApplicationId', null),
            edbAuthId: get(item, 'edbAuthId', null),
            newReqInd: 'Y',
            modReqInd: 'N',
            extnReqInd: 'N',
            renewReqInd: 'N',
            transferReqInd: 'N',
            pendingAppTransferReqInd: 'N',
            modQuestionAnswer: get(item,'modQuestionAnswer','N'),
            batchId: get(item, 'batchId', null),
            programId: get(item, 'programId', ''),
            trackingInd: get(item, 'trackingInd', null),
            edbTransType: get(item, 'edbTransType', null),
            polEmissionInd: '',
            modExtReason: '',
            estCompletionDate: '',
          };
        });
        scenario2GroupedPermitsObjects =
          scenario2GroupedPermitsObjects.concat(temp);
      });
    }
    let scenario3GroupedPermitsObjects: any = [];
    if (!isEmpty(this.scenario3GroupedPermits)) {
      const scenario3GroupedPermitsValues = Object.values(
        this.scenario3GroupedPermits
      );
      scenario3GroupedPermitsValues.forEach((scenario3GroupedPermitsValue) => {
        const temp = scenario3GroupedPermitsValue.map((item: any) => {
          let answer: string = '';
          if (item.batchId) {
            answer = this.conModAnswerMap.get(item.batchId)!;
          }
          return {
            applicationId: get(item, 'applicationId', null),
            permitTypeCode: get(item, 'permitTypeCode', null),
            roleId: get(item, 'roleId', ''),
            edbApplnId: get(item, 'edbApplicationId', null),
            edbAuthId: get(item, 'edbAuthId', null),
            newReqInd: 'Y',
            modReqInd: 'N',
            extnReqInd: 'N',
            renewReqInd: 'N',
            transferReqInd: 'N',
            pendingAppTransferReqInd: 'N',
            modQuestionAnswer: get(item,'modQuestionAnswer','N'),
            batchId: get(item, 'batchId', null),
            programId: get(item, 'programId', ''),
            trackingInd: get(item, 'trackingInd', null),
            edbTransType: get(item, 'edbTransType', null),
            polEmissionInd: answer,
            modExtReason: '',
            estCompletionDate: '',
          };
        });
        scenario3GroupedPermitsObjects =
          scenario3GroupedPermitsObjects.concat(temp);
      });
    }

    let permits: any[] = [];
    //let permits: any[] = this.permits;

    if (!isEmpty(scenario2GroupedPermitsObjects)) {
      permits = permits.concat(scenario2GroupedPermitsObjects);
    }
    if (!isEmpty(scenario1GroupedPermitsObjects)) {
      permits = permits.concat(scenario1GroupedPermitsObjects);
    }
    if (!isEmpty(scenario3GroupedPermitsObjects)) {
      permits = permits.concat(scenario3GroupedPermitsObjects);
    }

    if (!isEmpty(this.selectedPermits)) {
      const selectedValues = flatten(values(this.selectedPermits));
      const tempSelectedValues = selectedValues.map((item) => {
        return {
          applicationId: get(item, 'applicationId', null),
          permitTypeCode: get(item, 'permitTypeCode', null),
          roleId: get(item, 'roleId', ''),
          edbApplnId: get(item, 'edbApplicationId', null),
          edbAuthId: get(item, 'edbAuthId', null),
          newReqInd: 'Y',
          modReqInd: 'N',
          extnReqInd: 'N',
          renewReqInd: 'N',
          transferReqInd: 'N',
          pendingAppTransferReqInd: 'N',
          modQuestionAnswer: get(item,'modQuestionAnswer','N'),
          batchId: get(item, 'batchId', null),
          programId: get(item, 'programId', ''),
          trackingInd: get(item, 'trackingInd', null),
          edbTransType: get(item, 'edbTransType', null),
          polEmissionInd: '',
          modExtReason: '',
          estCompletionDate: '',
        };
      });
      permits = permits.concat(tempSelectedValues);
      //permits.push(tempSelectedValues);
    }

    // this.selectedPermits = this.selectedPermits.concat(this.getSummaryPermits());
    //TODO: Temp code to remove all null objects
    const filteredPermits = permits.filter((permit) => {
      let allValuesNull = true;
      for (let key in permit) {
        allValuesNull = isEmpty(trim(permit[key])) && allValuesNull;
      }
      return !allValuesNull;
    });

    const params = {
      constrnType: this.isContructionType(this.permits) ? 1 : null,
      permits: filteredPermits,
      emergencyInd: this.getEmergencyAuth,
    };
    // return;
    this.projectService.submitPermitTypes(params).subscribe(
      (response) => {
        this.onSubmit.emit(this.selectedPermitTypes);
        this.isSubmitted = false;
        this.onClose();
        this.onNext(2);
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  getPermitSummary() {
    this.utils.emitLoadingEmitter(true);
    this.existingPermits = [];
    this.projectService.getPermitSummary().then(
      (res: any) => {
        if (isEmpty(res)) {
          return;
        }
        this.constrnType = get(res, 'constrnType', '');
        const etrackPermits = get(res, 'etrack-permits', {});
        if (isEmpty(etrackPermits) && isEmpty(this.permitSummaryResponseValues)) {
          this.showEmergencyAuth = true;
          localStorage.setItem('emergencyAuth', '');
        }

        this.isEmergencyAuth = false;
        localStorage.setItem('emergencyAuth', '');

        if (res.emergencyInd === 'E') {
          this.isEmergencyAuth = true;
          localStorage.setItem('emergencyAuth', 'E');
        }
        if (res.emergencyInd === 1) {
          this.isEmergencyAuth = true;
          localStorage.setItem('emergencyAuth', 'E');
        }
        const summaryEtrackPermit = flatten(values(get(res, 'etrack-permits', {})));
        this.selectedPermits = summaryEtrackPermit;
        setTimeout(() => {
          this.checkSelectedBoxes();
        }, 500);
        this.existingPermits = get(res, 'dart-mod-extn', []);

        for (let v of Object.keys(this.existingPermits)) {
          this.existingPermits[v].forEach((item: any) => {
            if (
              item.availableTransTypes != undefined &&
              item.availableTransTypes != null
            ) {
              if(item.effectiveEndDate == null){
                this.noEndDate = true;
                }
              item.availableTransTypes.forEach((types: any) => {
                Object.assign(types, { selected: false });
              });
            }
          });
        }
        this.existingPermitsBackUp = JSON.parse(JSON.stringify(this.existingPermits));
        this.pendingPermits = get(res, 'dart-pending-txr', {});
        this.fetchedPermitSummary = true;
        this.utils.emitLoadingEmitter(false);
        return;
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }
  checkSelectedBoxes() {
    const seletedTypes: string[] = [];
    this.selectedPermits.forEach((data: any) => {
      seletedTypes.push(data.permitTypeCode.trim());
    });
    this.developmentCheckBoxes.forEach((item: ElementRef) => {
      if (seletedTypes.includes(item.nativeElement.value)) {
        item.nativeElement.checked = true;
        item.nativeElement.disabled = true;
      }
    });
  }

  isChecked(permit: any) {
    permit.transType = 'EXT';
    return true;
  }

  getSummaryPermits() {
    let summaryPermits: any = [];
    for (let obj of Object.keys(this.permitSummaryResponse)) {
      if (obj != 'etrack-permits') {
        let permitObjects = this.permitSummaryResponse[obj];
        if (permitObjects instanceof Array) {
          permitObjects.forEach((item: any) => {
            summaryPermits.push({
              applicationId: get(item, 'applicationId', null),
              permitTypeCode: get(item, 'permitTypeCode', null),
              roleId: get(item, 'roleId', ''),
              edbApplnId: get(item, 'edbApplicationId', null),
              edbAuthId: get(item, 'edbAuthId', null),
              newReqInd: get(item, 'newReqInd', null),
              modReqInd: get(item, 'modReqInd', null),
              extnReqInd: get(item, 'extnReqInd', null),
              renewReqInd: get(item, 'renewReqInd', null),
              transferReqInd: get(item, 'transferReqInd', null),
              pendingAppTransferReqInd: get(
                item,
                'pendingAppTransferReqInd',
                null
              ),
              modQuestionAnswer: get(item, 'modQuestionAnswer', null),
              batchId: get(item, 'batchId', null),
              programId: get(item, 'programId', ''),
              edbPermitEffectiveDate: get(item, 'effectiveStartDate', null),
              edbPermitExpiryDate: get(item, 'effectiveEndDate', null),
              trackingInd: get(item, 'trackingInd', null),
              edbTransType: get(item, 'edbTransType', null),
              polEmissionInd: '',
              modExtReason: '',
              estCompletionDate: '',
              calculatedBatchIdForProcess: get(item, 'calculatedBatchIdForProcess', null)
            });
          });
        }
      }
    }
    return summaryPermits;
  }

  getExistingPermitsParamsNew() {
    if(this.isEmergencyAuth){
      this.existingPermits=[];
      this.existingPermitsSelected=[];
      this.showConModPopup = false;
      return;
    }
    let selectedExistingPermits: any = [];
    for (let v of Object.keys(this.existingPermits)) {
      let isTransferSelected = false;
      let isExtensionSelected = false;
      let isRenewSelected = false;
      let isModifySelected = false;

      let existingPermitList = this.existingPermits[v];
      if (existingPermitList.length > 0) {
        let availableTransTypes = existingPermitList[0].availableTransTypes;
        if (availableTransTypes != null && availableTransTypes != undefined) {
          availableTransTypes.forEach((types: any) => {
            if (types.code == 'XFER') {
              isTransferSelected = types.selected;
            }
            if (types.code == 'EXT') {
              isExtensionSelected = types.selected;
            }

            if (types.code == 'REN') {
              isRenewSelected = types.selected;
            }

            if (types.code == 'MOD') {
              isModifySelected = types.selected;
            }
          });
        }
        if (
          isTransferSelected ||
          isExtensionSelected ||
          isRenewSelected ||
          isModifySelected
        ) {
          existingPermitList.forEach((item: any) => {
            selectedExistingPermits.push({
              applicationId: item?.applicationId,
              permitTypeCode: item?.permitTypeCode,
              roleId: '',
              edbApplnId: item?.edbApplicationId,
              edbAuthId: item?.edbAuthId,
              edbTransType: item?.edbTransType,
              batchId: item?.batchId,
              newReqInd: 'N',
              modReqInd: isModifySelected ? 'Y' : 'N',
              extnReqInd: isExtensionSelected ? 'Y' : 'N',
              transferReqInd: isTransferSelected ? 'Y' : 'N',
              renewReqInd: isRenewSelected ? 'Y' : 'N',
              pendingAppTransferReqInd: 'N',
              programId: item?.programId,
              edbPermitEffectiveDate: item?.effectiveStartDate,
              edbPermitExpiryDate: item?.effectiveEndDate,
              calculatedBatchIdForProcess:item?.calculatedBatchIdForProcess,
              modQuestionAnswer: null,
            });
          });
        }
      }
    }
    return selectedExistingPermits;
  }

  getSelectedPermitTypeParams() {
    const selectedPermitTypes = this.developmentCheckBoxes
      .map((item: ElementRef) => {
        //if (item.nativeElement.checked) {
        if (item.nativeElement.checked && !item.nativeElement.disabled) {
          // if disabled do not consider
          return {
            generalPermitInd:item.nativeElement.getAttribute('data-generalpermitind'),
            permitCategoryId:item.nativeElement.getAttribute('data-permitCategoryId'),
            value:item.nativeElement.value
          };
        }
        return null;
      })
      .filter((v: any) => v);
    const selectedPermitTypeParams = selectedPermitTypes
      .map((permitType: any) => {
        return {
          generalPermitInd:permitType.generalPermitInd,
          permitCategoryId:permitType.permitCategoryId,
          applicationId: '',
          permitTypeCode: permitType.value,
          roleId: '',
          edbApplnId: '',
          newReqInd: 'Y',
          modReqInd: 'N',
          extnReqInd: 'N',
          renewReqInd: 'N',
          transferReqInd: 'N',
          pendingAppTransferReqInd: 'N',
          modQuestionAnswer: 'N',
          batchId: '',
          programId: '',
          polEmissionInd: '',
          modExtReason: '',
          estCompletionDate: '',
        };
      })
      .filter((v: any) => v);
    return flattenDeep(selectedPermitTypeParams);
  }

  onEmergencyAuthChanged(ev: any) {
    
    if (ev.target.value === 'yes') {
      localStorage.setItem('emergencyAuth', 'E');
      this.isEmergencyAuth = true;
      this.existingPermits = JSON.parse(JSON.stringify(this.existingPermitsBackUp));
      this.showConModPopup = false;
      this.modifiedPermits = [];
      return;
    } else {
      this.isEmergencyAuth = false;
      localStorage.setItem('emergencyAuth', 'N');
    }
  }

  clearErrorMessages() {
    this.showAquaticPesticideError = false;
    this.showAtLeastOneRequiredError = false;
    this.isSPDESExceeded = false;
    this.Showonly1NaturalResourceGPSPDESGPError = false;
    this.showSPDESGPError = false;
    this.showNaturalResourceGPError = false;
    this.showYesNoRequiredError = false;
    this.showServerError = false;
    this.isNewPermitsMatchesWithExistingPermitsSelected= false;
    this.isNewPermitsMatchesWithPermitSummaryValues=false;
    this.isModExtPermitsMatchesWithPermitSummaryValues = false;
  }

  onModifyClickedRevised(type: any, permits: any) {
    if (type.code == 'MOD') {
      if (type.selected) {
        this.modifiedPermits = this.modifiedPermits.concat(permits);
        return;
      }
      const permitTypeDescs = permits.map((v: any) => v.permitTypeDesc);
      this.modifiedPermits = this.modifiedPermits.filter((v: any) => {
        return permitTypeDescs.indexOf(v.permitTypeDesc) == -1;
      });
      return;
    }
  }

  onConExtModifyNewClicked(ev: any, radioButton: HTMLInputElement) {
    let answer = ev.target.value;
    this.conExtensionMap.set(radioButton.id, answer);
  }
  newPermitsMatchesWithExistingPermitsSelected(){
    const existingPermits = flatten(values(this.existingPermitsSelected));
    const existingPermitsPermitTypeCodes = existingPermits.map((item) => item.permitTypeCode!).filter(v => v);
    const permitsPermitTypeCodes = this.permits.map((item:any) => item.permitTypeCode).filter((v:any) => v);
    const intersectedValues = intersection(existingPermitsPermitTypeCodes, permitsPermitTypeCodes)
    return !isEmpty(intersectedValues);
  }

  
  newPermitsMatchesWithPermitSummaryValues(){
   let permitSummaryResponseValuesPermitTypeCodes : any;
   let permitsPermitTypeCodes : any;
   let intersectedValues : any;
    if(this.permitSummaryResponseValues){
      permitSummaryResponseValuesPermitTypeCodes = this.permitSummaryResponseValues?.map((item) => item.permitTypeCode).filter(v => v);
      permitsPermitTypeCodes  = this.permits?.map((item:any) => item.permitTypeCode).filter((v:any) => v);
        intersectedValues  = intersection(permitSummaryResponseValuesPermitTypeCodes, permitsPermitTypeCodes);  
    }
    return !isEmpty(intersectedValues);
  }
  
  modExtPermitsMatchesWithPermitSummaryValues(){
    let permitSummaryResponseValuesPermitTypeCodes : any;
   let permitsPermitTypeCodes : any;
   let intersectedValues : any;
    if(this.permitSummaryResponseValues){
      permitSummaryResponseValuesPermitTypeCodes = this.permitSummaryResponseValues?.map((item) => item.permitTypeCode).filter(v => v);
      const modPermitsPermitTypeCodes  = this.modifiedPermits?.map((item:any) => item.permitTypeCode).filter((v:any) => v);
      const extPermitsPermitTypeCodes  = this.existingPermitsSelected?.map((item:any) => item.permitTypeCode).filter((v:any) => v);
      permitsPermitTypeCodes = concat(modPermitsPermitTypeCodes,extPermitsPermitTypeCodes);
      intersectedValues  = intersection(permitSummaryResponseValuesPermitTypeCodes, permitsPermitTypeCodes);  
    }
    return !isEmpty(intersectedValues);
  }

  ngOnDestroy() {
    this.unsubscriber.next();
    this.unsubscriber.complete();
    this.commonService.addGreenBackground();
  }
}

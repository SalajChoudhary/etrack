import {
  Component,
  ElementRef,
  Input,
  OnInit,
  QueryList,
  ViewChildren,
} from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { cloneDeep, concat, flatten, get, groupBy, isEmpty, values } from 'lodash';
import { ProjectService } from '../../../../../@shared/services/projectService';
import { ErrorService } from '../../../../../@shared/services/errorService';
@Component({
  selector: 'app-permit-selection-summary-modify-popup-component',
  templateUrl:
    './permit-selection-summary-modify-popup-component.component.html',
  styleUrls: [
    './permit-selection-summary-modify-popup-component.component.scss',
  ],
})
export class PermitSelectionSummaryModifyPopupComponentComponent
  implements OnInit
{
  modifiedPermitsFromSummary : any[] = [];
  isModifiedPermitFromSummary : boolean = false;
  key:any;  
  modifiedPermits: any[] = [];
  showEmergencyAuth: boolean = false;
  isEmergencyAuth: boolean = false;
  selectedPermits: any;
  pendingPermits: any;
  showServerError = false;
  serverErrorMessage!: string;
  permitGroupKeys: any;
  groupedPermits: any;
  permits: any = [];
  numberOfBatchIds: number = 0;
  conModAnswerMap = new Map<string, string>([]);
  //scenario 2 extend to: checkbox
  conExtensionMap = new Map<string, string>([]);
  //scenario 3
  operatingPermitAnswerMap = new Map<string, string>([]);
  emergencyInd:any = 0;
  @ViewChildren('developmentCheckBoxes')
  developmentCheckBoxes!: QueryList<ElementRef>;
  constructor(
    public activeModal: NgbActiveModal,
    private projectService: ProjectService,
    private errorService: ErrorService
  ) {}

  ngOnInit(): void {
    if(this.isModifiedPermitFromSummary){
      this.getPermitSummaryFromSummaryScreen();
      console.log('from popup',this.modifiedPermits)
    }else{
      this.getPermitSummary();
    }
    
  }

  getDefaultYes(groupId:string){
    return this.conModAnswerMap.get(groupId) == 'Y' ? true : false;
  }

  getDefaultNo(groupId:string){
    return this.conModAnswerMap.get(groupId) == 'Y' ? false : true;
  }

  getPermitSummaryFromSummaryScreen(){
    this.modifiedPermits = this.modifiedPermitsFromSummary;
    this.groupedPermits = groupBy(this.modifiedPermits, 'batchId');
    let keys: any = [];
    Object.keys(this.groupedPermits).forEach((key: any) => {
      keys.push(key);
      let value : any[] = this.groupedPermits[key];
      if(value && value.length > 0){
        // set default modQuestionAnswer to N
        value.forEach((val:any)=>{
          if(!val.modQuestionAnswer){
            val.modQuestionAnswer = 'N';
          }
        })
        this.conModAnswerMap.set(key,value[0].modQuestionAnswer);
      }else{
        this.conModAnswerMap.set(key,'N');
      }

    });

    this.numberOfBatchIds = keys.length;
    this.permitGroupKeys = keys;

  }
  getPermitSummary() {
    this.projectService.getPermitForSummaryScreen().then(
      (res) => {
        this.emergencyInd = get(res, 'emergencyInd', 0);
        console.log(res);
        if (!isEmpty(res)) {
          this.modifiedPermits = res[this.key];
          this.groupedPermits = groupBy(this.modifiedPermits, 'batchId');
          let keys: any = [];
          Object.keys(this.groupedPermits).forEach((key: any) => {
            keys.push(key);
            let value : any[] = this.groupedPermits[key];
            if(value && value.length > 0){
              this.conModAnswerMap.set(key,value[0].modQuestionAnswer);
            }else{
              this.conModAnswerMap.set(key,'N');
            }
            
          });

          console.log('Group conModAnswerMap', this.conModAnswerMap)
          this.numberOfBatchIds = keys.length;
          this.permitGroupKeys = keys;
        }
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
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
    permit.modQuestionAnswer = checkValue?'Y':'N';
    if (indicator === '1') {
      this.conModAnswerMap.set(radio.id, answer);
    } else if (indicator === '3') {
      this.operatingPermitAnswerMap.set(radio.id, answer);
    }
    console.log(permit)
    console.log(this.groupedPermits)
  }

  closeIsClicked() {    
    this.activeModal.close();
  }
  okIsClicked(){
    console.clear();
    console.log("this.groupedPermits", this.groupedPermits)
    let permits:any = [];
    this.permitGroupKeys.forEach((key:any) => {
      permits = concat(permits, this.groupedPermits[key])
    })
    console.log("permits", permits)
    const prepared = permits.map((permit:any) => {
      return {
        "applicationId": permit?.applicationId,
        "permitTypeCode": permit?.permitTypeCode,
        "roleId": "",
        "edbApplnId": permit?.edbApplicationId,
        "edbAuthId": permit?.edbAuthId,
        "edbTransType": permit?.edbTransType,
        "batchId": permit?.batchId,
        "newReqInd": permit?.newReqInd,
        "modReqInd": permit?.modReqInd,
        "extnReqInd": permit?.extnReqInd,
        "transferReqInd": permit?.transferReqInd,
        "renewReqInd": permit?.renewReqInd,
        "pendingAppTransferReqInd":permit?.pendingAppTransferReqInd,
        "programId": permit?.programId,
        "edbPermitEffectiveDate": permit?.effectiveStartDate,
        "edbPermitExpiryDate": permit?.effectiveEndDate,
        "calculatedBatchIdForProcess": permit?.calculatedBatchIdForProcess,
        "modQuestionAnswer": permit?.modQuestionAnswer,
      }
    });
    const payload = {
      emergencyInd: this.emergencyInd,
      "constrnType": null,
    	"etrackPermits": [],
	    "dartPermits": prepared      
    }
    this.projectService.submitPermitTypesValues(payload,true).subscribe((res) => {
      this.activeModal.close(true);
    },
    (error: any) => {
      this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;
    })
  }
}

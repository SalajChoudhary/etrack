import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { KeywordOtherGridComponent } from 'src/app/@shared/components/keyword-other-grid/keyword-other-grid.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { KeywordMaintainService } from 'src/app/@shared/services/keyword-maintain.service';
import { VirtualDesktopComponent } from '../virtual-desktop.component';

@Component({
  selector: 'app-keywords-text-grid',
  templateUrl: './keywords-text-grid.component.html',
  styleUrls: ['./keywords-text-grid.component.scss']
})
export class KeywordsTextGridComponent implements OnInit, OnChanges {

  @Input() keywordItems: any[] = [];
  @Input() categoryList: any[] = [];
  @Input() otherKeywordItems: any[] = [];
  @Output() categoryListUpdated: EventEmitter<any> = new EventEmitter();
  @Output() otherKeywordsUpdated: EventEmitter<any> = new EventEmitter();
  @ViewChild('otherKeywordItemsTable',{ static: false }) otherKeywordItemsTable!:KeywordOtherGridComponent;
  otherSearchItem: any ='';
  initialData: any[]= [];
  keysToSearch:string[]=['keywordText'];
  otherKeywordButton: boolean =false;
  errorMsgObj: any;
  serverErrorMessage: any;
  showOtherServerError: boolean=false;
  projectId: any='';


  constructor(
    private keywordMaintenanceService: KeywordMaintainService,
    private commonService: CommonService,
    private errorService: ErrorService,
    private route: ActivatedRoute,
    private virtualDesktopComponent: VirtualDesktopComponent
  ) { }
  ngOnChanges(changes: SimpleChanges): void {
   if (changes && changes.otherKeywordItems && changes.otherKeywordItems.currentValue != changes.otherKeywordItems.previousValue) {
    this.initialData = JSON.parse(JSON.stringify(changes.otherKeywordItems.currentValue));
   }
  }

  ngOnInit(): void {
    this.route.params.subscribe((params: any) => {
      this.projectId = params.projectId;
    });
    this.initialData = JSON.parse(JSON.stringify(this.otherKeywordItems));
    this.getAllErrorMsgs();
  }

  onOtherTextChange() {
    this.showOtherServerError = false;
  }

  keywordEmittedData(data: any){
    this.categoryListUpdated.emit(data);
  }

  otherKeywordEmittedData(otherKeywordData: any){
    this.otherKeywordsUpdated.emit(otherKeywordData);
  }

  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val)=>{
      if(val)this.errorMsgObj=this.commonService.getErrorMsgsObj();      
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
      }, this.projectId).then((res: any) => {
        this.otherSearchItem = '';
        let candidateKeywordList: any[] = [];
        Object.keys(res.candidateKeyword).forEach((e: any) => {
          res.candidateKeyword[e][0].categoryText = e;
          candidateKeywordList = candidateKeywordList.concat(res.candidateKeyword[e]);
        });
        this.otherKeywordItems = candidateKeywordList;
        //this.virtualDesktopComponent.loadKeywordData();
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showOtherServerError = true;
        throw error;
      })
    }
  }
}

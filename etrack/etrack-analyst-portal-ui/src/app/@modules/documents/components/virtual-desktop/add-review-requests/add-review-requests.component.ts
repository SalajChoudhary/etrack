import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import {
  AbstractControl,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { VirtualDesktopService } from 'src/app/@shared/services/virtual-desktop.service';
import { DocumentService } from 'src/app/@shared/services/documentService';
import { DatePipe } from '@angular/common';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { validateDueDate } from 'src/app/@shared/applicationInformation.validator';
import { isEmpty } from 'lodash';
import { EmailComposeComponent } from 'src/app/@shared/components/email-compose/email-compose.component';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { CommonService } from 'src/app/@shared/services/commonService';
import { ActivatedRoute } from '@angular/router';
import { InquiryService } from 'src/app/@shared/services/inquiryService';

@Component({
  selector: 'app-add-review-requests',
  templateUrl: './add-review-requests.component.html',
  styleUrls: ['./add-review-requests.component.scss'],
})
export class AddReviewRequestsComponent implements OnInit {
  
  @ViewChild('emailModal',{static: false}) emailModal!:EmailComposeComponent;
  @ViewChild('pendingPopup', { static: true })
  pendingPopup!: PendingChangesPopupComponent;
  @ViewChildren('g') checkboxes!:QueryList<any>;
  @Output() onClose = new EventEmitter<{status:boolean,isSaved:boolean,response:any,reviewers:any,selectedReviewer:any}>();
  reviewData: any[] = [];
  reviewersList: any[] = [];
  addReviewerForm!: UntypedFormGroup;
  submitted: boolean = false;
  errorMsgObj: any = {};
  reviewers:any = [];
  reviewer: AbstractControl = new UntypedFormControl('', Validators.required);
  selectedDocs: any[] = [];
  gridTouched: boolean = false;
  regionId: any;
  projectId: any;
  selectedReviewer: any;
  showOneDocRequiredError: boolean = false;
  reviewerPayload: {}= {};
  showServerError = false;
  serverErrorMessage! : string;
  

  emailData:any;
  @Input() inquiryId: any;
  @Input() isGi: boolean = false;
  
  constructor(
    private virtualDesktopService: VirtualDesktopService,
    private fb: UntypedFormBuilder,
    private docService: DocumentService,
    private datePipe: DatePipe,
    private errorService: ErrorService,
    private commonService:CommonService,
    private route:ActivatedRoute,
    private inquiryService: InquiryService
  ) {
    this.initiateForm();
  }
  
  async getAllErrorMsgs() {
    try {
      this.commonService.emitErrorMessages.subscribe((val)=>{
        if(val)this.errorMsgObj=this.commonService.getErrorMsgsObj();
      })
    } catch (error: any) {
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;  

    }
  }

  optionSelected(event: any) {
    // console.log(event.target.value);
  }

  displayWith(value: any) {
    if (value) return value.reviewerName;
  }

  initiateForm() {
    let cr=new Date();
    let dueDate=new Date();
    dueDate.setDate(cr.getDate() + 14);
    this.addReviewerForm = this.fb.group(
      {
        reviewer: this.reviewer,
        dueDate: [this.datePipe.transform(dueDate,'yyyy-MM-dd'), Validators.required],
        dateAssigned: [this.datePipe.transform(cr,'yyyy-MM-dd'), Validators.required],
      },
      { validators: [validateDueDate('dueDate', 'dateAssigned', 'reviewer')] }
    );
  }
  private _filter(value: any): string[] {
    const filterValue = this._normalizeValue(value);
    return this.reviewersList.filter((street) =>
      this._normalizeValue(street.managerName).includes(filterValue)
    );
  }

  private _normalizeValue(value: string): string {
    return value ? value.toLowerCase().replace(/\s/g, '') : '';
  }
  ngOnInit(): void {
    this.route.params.subscribe((params:any)=>{
      this.projectId = params.projectId;
    })
    

    this.getDocuments();
    this.getAllErrorMsgs();
    this.getRegionId();
  }

  ngOnDestroy(){
    // console.log('destroyed');
    
  }
  getRegionId() {
    this.virtualDesktopService
      .getUserRegionId()
      .then((response: any) => {
        this.regionId = response;
        if(this.regionId == null){
          this.regionId = '';
        }
          this.getReviewList();
      })
      .catch((error) => {
        this.reviewersList = [];
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;  

      });
  }
  isInputClicked:boolean=false;

  getReviewList() {

      this.virtualDesktopService
        .getReviewersList(this.regionId, this.projectId? this.projectId : null)
        .then((response: any[]) => {
          this.reviewersList = response;
          this.reviewers=response;
          //this.reviewers = of(this._filter(''));
        })
        .catch((error) => {
          this.reviewersList = [];
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
        });
  }
  checkUncheckAll(e: any) {
    if (e.target.checked) {
     this.checkboxes.forEach((checkbox:ElementRef)=>{
      checkbox.nativeElement.checked=true;
     });
     this.selectedDocs=[...this.reviewData]
    } else {
      this.checkboxes.forEach((checkbox:ElementRef)=>{
        checkbox.nativeElement.checked=false;
       });
       this.selectedDocs=[];
    }
  }
  selectRow(e: any, item: any) {
    this.gridTouched = true;
    if (e.target.checked) {
      this.selectedDocs.push(item);
    } else {
      let i = this.selectedDocs.findIndex(
        (x) => item.documentId === x.documentId
      );
      this.selectedDocs.splice(i, 1);
    }
  }

  getProjectManagerName(id:string){
    const manager= this.reviewers.find((x:any)=>x.userId===id);
    if(manager)return manager.managerName;
    return '';
  }
  getProjectManagerEmail(id:string){
    const manager= this.reviewers.find((x:any)=>x.userId===id);
    if(manager)return manager.emailAddress;
    return '';
  }

  getProjectReviewerRoleId(id:string){
    const manager= this.reviewers.find((x:any)=>x.userId===id);
    if(manager)return manager.analystRoleId;
    return '';
  }
  onFormSubmit() {
    this.submitted = true;
    if (this.addReviewerForm.valid) {
      if(!this.reviewer.value){
        this.reviewer.setValue('');
        return;
      }
      
     this.reviewerPayload = {
        reviewerId: this.reviewer.value,
        reviewerRoleId: this.getProjectReviewerRoleId(this.reviewer.value),
        reviewerName: this.getProjectManagerName(this.reviewer.value),
        documentIds: this.selectedDocs.map((obj: any) => obj.documentId),
        reviewerEmail:this.getProjectManagerEmail(this.reviewer.value),
        dueDate: this.datePipe.transform(
          this.addReviewerForm.value.dueDate,
          'MM/dd/yyyy'
        ),
        dateAssigned: this.datePipe.transform(
          this.addReviewerForm.value.dateAssigned,
          'MM/dd/yyyy'
        ),
      };

      console.log("Reviewer", this.reviewerPayload)
      this.virtualDesktopService.assignReviewer();      
      if(!this.isGi) {
        //Need to add these to seperate method:
        this.virtualDesktopService
          .updateReviewerData(this.reviewerPayload, this.projectId)
          .subscribe((response: any) => {          
            this.onClose.emit({status:true,isSaved:true,response:response,reviewers:this.reviewersList,selectedReviewer:this.reviewers});
          }, 
          (error: any) =>{
          this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;  
          }
      );
      }
      else {
        this.virtualDesktopService
          .updateGiReviewerData(this.reviewerPayload, this.inquiryId)
          .subscribe((response: any) => {          
            this.onClose.emit({status:true,isSaved:true,response:response,reviewers:this.reviewersList,selectedReviewer:this.reviewers});
          }, 
          (error: any) =>{
          this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;  
          }
      );
      }
    }
  }

  assignReviewer(){
    console.log("Reviewr", this.reviewerPayload)
    this.virtualDesktopService.assignReviewer();
    if(!this.isGi) {
      this.virtualDesktopService
        .updateReviewerData(this.reviewerPayload, this.projectId)
        .subscribe((response: any) => {
          this.onClose.emit({status:true,isSaved:true,response:response,reviewers:this.reviewersList,selectedReviewer:this.reviewers});
        }, 
        (error: any) =>{
        this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;  
        }
    );
    }
  }

  getDocuments() {
    if(!this.isGi) {
      this.virtualDesktopService
        .getReviewDocs(this.projectId)
        .then((response: any[]) => {
          this.reviewData = response;
        })
        .catch((error) => {
          this.reviewersList = [];
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
        });
    }
    else {
      this.inquiryService
        .getReviewDocs(this.inquiryId)
        .then((response) => {
          this.reviewData = [];
          // response.forEach((doc : any) => {
          //   this.reviewData.push( {
          //     documentName: doc.displayName,
          //     documentId: doc.documentId
          //   });
          // })
          this.reviewData = response;
        })
        .catch((error) => {
          this.reviewersList = [];
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        });
    }
  }

  closeModal(value?: boolean) {
    if (this.addReviewerForm.dirty || this.gridTouched) {
      this.openPendingPop();
    } else {
      this.onClose.emit({status:value ? true : false,isSaved:false,response:'',reviewers:'',selectedReviewer:''});
    }
  }
  openPendingPop() {
    this.pendingPopup.open();
  }
  goBack() {
    this.onClose.emit({status:false,isSaved:false,response:'',reviewers:'',selectedReviewer:''});
  }
}

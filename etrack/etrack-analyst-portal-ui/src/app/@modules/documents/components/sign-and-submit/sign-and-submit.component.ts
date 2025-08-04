import { Component, OnInit, ViewChild } from '@angular/core';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { CommonService } from 'src/app/@shared/services/commonService';
import { Router } from '@angular/router';
import { RequiredDocsService } from 'src/app/@shared/services/required-docs.service';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { SuccessPopupComponent } from 'src/app/@shared/components/success-popup/success-popup.component';
import { get, isEqual } from 'lodash';
import { fromEvent, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ErrorService } from 'src/app/@shared/services/errorService';



@Component({
  selector: 'app-sign-and-submit',
  templateUrl: './sign-and-submit.component.html',
  styleUrls: ['./sign-and-submit.component.scss'],
})
export class SignAndSubmitComponent implements OnInit {
  @ViewChild('pendingPopup', { static: true }) pendingPopup!: PendingChangesPopupComponent;
  @ViewChild('pendingChangesPopup', { static: true }) pendingChangesPopup!: PendingChangesPopupComponent;
  @ViewChild('successPopup', { static: true }) successPopup!: SuccessPopupComponent;

  signList: any = [];
  isAllCheckBoxesChecked: boolean=false;
  isTouched: boolean=false;
  modalReference: any;
  signSubmitValidated:boolean = false;
  mode:any = localStorage.getItem('mode');
private unsubscriber : Subject<void> = new Subject<void>();
showServerError = false;
serverErrorMessage! : string;
   
  get isValidate(){
    return this.mode == 'validate';
  }
  get isReadonly(){
    return (this.mode =='read') || this.signSubmitValidated;
  }

  constructor(
    private projectService: ProjectService,
    public commonService:CommonService,
    private router:Router,
    private requiredDocsService:RequiredDocsService,
    private errorService: ErrorService
    ) {}

  ngOnInit(): void {
    this.getSignList();
     //diables browswers back button
   history.pushState(null, '');
   fromEvent(window, 'popstate').pipe(
     takeUntil(this.unsubscriber)
   ).subscribe((_) => {
     history.pushState(null, '');
   });
  }
  checkIfAllCheckboxesChecked(){
    let isAllChecked=this.signList.find((x:any)=>x.acknowledgeInd==='N');
    console.log(isAllChecked,'checking')
    return !!!isAllChecked;
  }
  onSubmitProject(){
   
    // if(this.isAllCheckBoxesChecked){
      // this.modalReference = this.successPopup.open();
      this.projectService.submitFinalProject().subscribe(async(response:any)=>{
        this.modalReference=await this.successPopup.open();
      }, 
      (error: any) =>{
      this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;  
      });
    // }
  }
  onSuccesPopupOkClicked(){
    this.router.navigateByUrl('/dashboard');
    }
  getSignList() {
    this.projectService
      .getSignsData()
      .then((response) => {
        this.signList = get(response, 'reqdsigneddoc', []) //response?.length > 0 ? response : [];
        this.signList.map((x:any)=>x.isAlreadySaved=x.acknowledgeInd==='Y')
        this.isAllCheckBoxesChecked=this.checkIfAllCheckboxesChecked();
        this.signSubmitValidated = isEqual(get(response, 'validatedInd', 'N'), 'Y')
      })
      .catch((err) => {
        this.signList = [];
      this.serverErrorMessage = this.errorService.getServerMessage(err);
        this.showServerError = true;
        throw err;  
      });
  }

  	
	ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }
  
  onCheckBoxChange(e: any, item: any) {
   
    this.isTouched=true;
    if (e.target.checked) item.acknowledgeInd = 'Y';
    else item.acknowledgeInd = 'N';
    this.isAllCheckBoxesChecked=this.checkIfAllCheckboxesChecked();
    console.log(this.signList);
    
    console.log(this.isTouched,'change in check')
  }
  get selectedData(){
    return this.signList?.filter((x:any)=>x.acknowledgeInd==='Y')
  }
  getApiData(){
  let data:any=[];
  this.selectedData?.forEach((x:any)=>{
    data.push(x.applicantId);
  })
  return data;
  }
  onSaveClick() {
   // if(this.selectedData?.length>0) {
      let selectedData=this.getApiData();
      this.saveData(selectedData);
   // }
  }
  saveData(payload:any){
    this.projectService.saveApplicantDocs(payload).subscribe((response)=>{
      this.isTouched=false;
     // if(this.signList.length==payload.length){
        this.commonService.navigateToMainPage();
     //   return;
      //}
      //this.getSignList();
      // this.commonService.navigateToMainPage();
    }, 
    (error: any) =>{
    this.serverErrorMessage = this.errorService.getServerMessage(error);
      this.showServerError = true;
      throw error;  
    });
  }
  redirectToDocumentUpload(){
   let showPendingPopup = false;
   this.signList.forEach((doc: any) => {
    if(doc.acknowledgeInd === 'Y' && !doc.isAlreadySaved){
      showPendingPopup = true;
    }
   });
   if(showPendingPopup){
    this.pendingChangesPopup.open();
   }
   else{
    this.router.navigate(['/document-upload'],{queryParams:{page: 'other',page2:'step5'}});
   }
  }

  pendingOkClicked(){
    this.router.navigate(['/document-upload'],{queryParams:{page: 'other',page2:'step5'}});
  }
  

  docUpload(doc:any){
    console.log(doc);
    
    this.requiredDocsService.setDocument(doc);
    this.router.navigate(['/document-upload'],{queryParams:{page:'step5',displayName: doc.displayName}})
  }
  async openConfirmModal() {
    console.log(this.isTouched,'touched checkbox')
    if(this.isTouched){
      this.modalReference=await this.pendingPopup.open();
    }else{
      this.goBack();
    }
  }
  goBack(){
    this.commonService.navigateToMainPage();
  }
  alreadyUploadedClicked(doc:any) {
    console.log(doc,'sign doc here')
    this.router.navigate(['/already-uploaded'], {
      queryParams: { displayName: doc.displayName,page:'step5' },
    });
  }
}

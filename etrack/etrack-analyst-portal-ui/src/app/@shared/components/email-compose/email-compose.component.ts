import { Component, ElementRef, Input, OnInit, ViewChild, ChangeDetectorRef, HostListener, Output, EventEmitter } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, NgForm, Validators } from '@angular/forms';
import { filter, isFunction } from 'lodash';
import { emailSend } from 'src/app/@store/models/emailSend';
import { CommonService } from '../../services/commonService';
import { VirtualDesktopService } from '../../services/virtual-desktop.service';
import { PendingChangesPopupComponent } from '../pending-changes-popup/pending-changes-popup.component';
import { ErrorService } from '../../services/errorService';
import { ActivatedRoute } from '@angular/router';
import { Utils } from '../../services/utils';


@Component({
  selector: 'app-email-compose',
  templateUrl: './email-compose.component.html',
  styleUrls: ['./email-compose.component.scss']
})
export class EmailComposeComponent implements OnInit {

  @ViewChild('main', { static: false }) main!: ElementRef;
  @Output() popUpClosed= new EventEmitter();
  constructor(private commonService: CommonService,
    private virtualDesktopService: VirtualDesktopService,
    private changeDetectorRef: ChangeDetectorRef,
    private ModalRef: ElementRef,
    public utils: Utils,
    private errorService: ErrorService,
    private route: ActivatedRoute) { }

  emailForm = new UntypedFormGroup({
    fromEmail: new UntypedFormControl(''),
    toEmail: new UntypedFormControl(''),
    ccEmail: new UntypedFormControl(''),
    emailSubject: new UntypedFormControl(''),
    inputEmailBody: new UntypedFormControl(''),
    historyEmailBody: new UntypedFormControl(''),
    emailAttachments: new UntypedFormControl(''),
  });
  isInsideClick: boolean = false;
  showemailtemplate:boolean=true;
  files: File[]=[];
  title: any;
  sendMail: emailSend = new emailSend();
  isReply: boolean = false;
  sampleData: any;
  existingcctxt: any;
  existingtotxt: any;
  showhistory: boolean = false;
  showBody: boolean = false;
  showTitle: boolean = false;
  showReply: boolean = true;
  enableSubject: boolean = true;
  disableSubject: boolean = false;
  disableToEmail: boolean = false;
  removeCC: boolean = false;
  replyFromAdr:any;
  historyInfo: string[] = [];
  filename: string = '';
  @Input() subject!: any
  @Input() existingData!: emailSend
  @Input() requestedDocs: any;
  @ViewChild('emailModal', { static: false })
  modal!: ElementRef;
  @ViewChild('sel') toDropdown!: ElementRef
  @ViewChild('ccsel') ccDropdown!: ElementRef
  @ViewChild('emailContainer') emailContainer!: ElementRef;
  @ViewChild('ccOptions') ccOptions!: ElementRef;
  @ViewChild('toOptions') toOptions!: ElementRef;
  @ViewChild('pendingPopup' , { static: true }) pendingPopup! : PendingChangesPopupComponent;
  projectId: any = '';
  regionId: any;
  initalResponse:any;
  reviewersList: any[] = [];
  ccShowDropdown: boolean = false;
  toShowDropdown: boolean = false;
  ccSearchEmails: Array<any> = [];
  toSearchEmails: Array<any> = [];
  ccshowError: boolean = false;
  toshowError: boolean = false;
  isSubmitted : boolean = false;
  showServerError = false;
  serverErrorMessage! : string;
  headerText!: string;
  emailTextAreaHeight: string = "100px";
  emailTextAreaLines: number = 0;
  ngOnInit(): void {
    this.showhistory = false;
    this.showReply = true;
    this.route.params.subscribe((params:any)=>{
      this.projectId = params.projectId;
    })
  }


  @HostListener('document:click', ['$event.target'])
  clickout(targetElement: any) {
    const clickedInside = this.emailContainer.nativeElement.contains(targetElement);
    if (!clickedInside) {
    //  this.modal.nativeElement.style.display = "none";
      // this.isReply = false;
      this.ccShowDropdown = false;
    } else {
      if(!this.removeCC) {
        const ccClickedInside = this.ccOptions.nativeElement.contains(targetElement);
        if (!ccClickedInside) {
          this.ccOptions.nativeElement.style.display = "none";
        }
        if (this.ccShowDropdown) {
          this.ccValidate()
        }
      }

      const toClickedInside = this.toOptions.nativeElement.contains(targetElement);
      if (!toClickedInside) {
        this.toOptions.nativeElement.style.display = "none";
      }
      if (this.toShowDropdown) {
        this.toValidate()
      }
    }
  }

  getRegionId() {
    this.showServerError = false;
    this.virtualDesktopService
      .getUserRegionId()
      .then((response: any) => {
        this.regionId = response;
        //this.reviewers = of(this._filter(''));
        if (response === 0 || response) {
          this.getReviewList();
        }
      })
      .catch((error) => {
        this.reviewersList = [];
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;  
      });
  }
  isInputClicked: boolean = false;

  getEmailList() {
    this.showServerError = false;

    this.virtualDesktopService
      .getEmailList()
      .then((response: any[]) => {
        this.sampleData = response;
      })
      .catch((error) => {
        this.sampleData = [];
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      });
  }

  getReviewList() {
    this.showServerError = false;
    this.virtualDesktopService
      .getReviewersList(this.regionId, this.projectId)
      .then((response: any[]) => {
        this.reviewersList = response;
        console.log(this.reviewersList, 'reviewlist');
      })
      .catch((error) => {
        this.reviewersList = [];
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      });
  }
  onFileSelected(event: any) {
    alert('triggerd');
  }
  userChange(value: any) {
    console.log(value)
    if (value != null && value != "") {
      this.existingtotxt = this.emailForm.value.toEmail;
      this.emailForm.patchValue({
        toEmail: this.existingtotxt != "" ? this.existingtotxt + ';' + value : value
      });
    }
    this.toDropdown.nativeElement.value = "To"

  }
  ccChange(value: any) {
    console.log(value)
    if (value != null && value != "") {
      this.existingcctxt = this.emailForm.value.ccEmail;
      this.emailForm.patchValue({
        ccEmail: this.existingcctxt != "" ? this.existingcctxt + ';' + value + ";" : value
      });
    }
    this.ccDropdown.nativeElement.value = "Cc";
  }
  openFromNotifications(emailData: any, replybutton: boolean, reviewers: any) {

    this.getEmailList();
    this.getRegionId();
    this.historyInfo = [];
    this.commonService.getEnvelopDetails(emailData.correspondenceId, emailData.projectId).subscribe((response) => {
      console.log('envelope details:')
      console.log(response);
      console.log('correspondence id: ');
      console.log(emailData.correspondenceId);
      this.initalResponse=response;
      this.sendMail.topicId= emailData.topicId;
      this.projectId = emailData.projectId
      this.emailForm.patchValue({
        fromEmail: response.fromEmailAdr == null ? '' : response.fromEmailAdr + ';',
        toEmail: response.toEmailAdr == null ? '' : response.toEmailAdr + ';',
        ccEmail: response.ccEmailAdr == null ? '' : response.ccEmailAdr + ';',
        emailSubject: response.emailSubject,
        inputEmailBody: '',
        historyEmailBody: response.emailContent
      });
      this.title = response.emailSubject;
      this.historyInfo = response.emailContent;
      this.historyInfo = this.historyInfo.filter((f) => f != null)
      this.showReply = replybutton;
      this.showhistory = true;
      this.enableSubject = true;
      this.showTitle = false;
      this.showBody = false;
      this.modal.nativeElement.style.display = "block";
      this.ccOptions.nativeElement.style.display = "none";
      this.toOptions.nativeElement.style.display = "none";
      this.ccshowError = false;
      this.toshowError = false;
      this.replyFromAdr =response.replyFromAdr;
      this.showemailtemplate=false;
    },
      (error) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error; 
      });
  }

  autoGrowTextZone(e: any) {
    if(e.code === 'Enter') {
      this.emailTextAreaHeight = (e.target.scrollHeight + 20) + "px";
      this.emailTextAreaLines = e.target.value.split('\n').length;
    }
    else if(e.code === 'Backspace') {
      if (e.target.value.split('\n').length < this.emailTextAreaLines) {
        this.emailTextAreaHeight = (e.target.scrollHeight - (20 * (this.emailTextAreaLines - e.target.value.split('\n').length))) + "px";
        this.emailTextAreaLines = e.target.value.split('\n').length;
      }
    }
  }
  onEmailInput(e: any) {
     if(e.inputType==="insertFromPaste") {
        if(e.target.value.split('\n').length > this.emailTextAreaLines) {
          this.emailTextAreaHeight = (e.target.scrollHeight + 30) + "px";
          this.emailTextAreaLines = e.target.value.split('\n').length;
        }
     }

  }
  open(emailData: any, replybutton: boolean, selectedreviewer: any) {
   
    this.getRegionId();
    this.getEmailList();
    this.sendMail.emailCorrespondenceId = emailData.emailCorrespondenceId;
    this.sendMail.topicId= emailData.topicId;
    this.historyInfo = emailData.existingContents;
    this.headerText = emailData.emailPurpose;
    this.filename='';
    this.emailForm.patchValue({
      fromEmail: emailData.fromEmailId,
      ccEmail: '',
      toEmail: emailData.toEmailId + ";",
      emailSubject: emailData.subject,
      inputEmailBody: '',
      historyEmailBody: emailData.existingContents == null ? '' : emailData.existingContents,
      emailAttachments: ''
    });
    this.showReply = replybutton;
    this.showhistory = true;
    this.showBody = true;
    this.showTitle = true;
    this.enableSubject = false;
    this.modal.nativeElement.style.display = "block";
    this.toOptions.nativeElement.style.display = "none";
    this.isReply = true;
    this.ccshowError = false;
    this.toshowError = false;
    this.showemailtemplate=true;
    if(!(this.headerText === 'Program Review')) {
      this.disableSubject = true;
      this.disableToEmail = true;
    }
    else {
      this.ccOptions.nativeElement.style.display = "none";
    }
  }

  close() {    
    if(this.emailForm.dirty && !this.isSubmitted){  
      this.pendingPopup.open();
    }else{      
      this.isReply = false;
      this.emailForm.patchValue({
        toEmail: '',
        fromEmail: '',
        ccEmail: '',
        emailSubject: '',
        inputEmailBody: '',
        historyEmailBody: '',
        emailAttachments:'',
        
      })
      this.modal.nativeElement.style.display = "none";
      this.existingtotxt = '';
      this.ccShowDropdown = false;
    }  

  }
  reply() {
    if(!this.isReply){
    this.showemailtemplate=true;

    console.log(this.initalResponse);
    this.emailForm.patchValue({
      fromEmail: this.replyFromAdr,
      toEmail: this.emailForm.value.fromEmail.slice(0,-1) === this.replyFromAdr ? this.initalResponse.toEmailAdr:this.emailForm.value.fromEmail,
      ccEmail: this.emailForm.value.ccEmail,
      emailSubject: this.emailForm.value.emailSubject,
      inputEmailBody: '',
      historyEmailBody: this.emailForm.value.inputEmailBody
    })
    this.showhistory = true;
    this.showBody = true;
    this.showTitle = false;
    this.isReply = true;
    this.changeDetectorRef.detectChanges();}
  }
  Submit() {    
    this.isSubmitted = true;
    let toaddress: string[] = [];
    let ccaddress: string[] = [];
    this.ccValidate();
    this.toValidate();
    if (this.emailForm.value.toEmail !== null && this.emailForm.value.toEmail !== "") {
      let toaddressarray = this.emailForm.value.toEmail.split(';')
        .map((emailAddr: any) => emailAddr.trim());
      toaddress = toaddressarray;
    }
    if (this.emailForm.value.ccEmail !== null && this.emailForm.value.ccEmail !== "") {
      let ccaddressarray = this.emailForm.value.ccEmail.split(';')
        .map((emailAddr: any) => emailAddr.trim());
      ccaddress = ccaddressarray;
    }
    if (this.emailForm.valid == true) {
      
      this.sendMail.toEmailId = toaddress[toaddress.length-1] === "" ?  toaddress.slice(0, -1) : toaddress ;
      this.sendMail.ccEmailId = ccaddress[ccaddress.length-1] === "" ?  ccaddress.slice(0, -1) : ccaddress ;
      this.sendMail.subject = this.emailForm.value.emailSubject;
      this.sendMail.emailBody = this.emailForm.value.inputEmailBody;
      if(this.existingData) {
        this.existingData.existingContents?.forEach((line: any) => {
          if(!this.sendMail.emailBody) {
            this.sendMail.emailBody += line;
          }
          else {
            this.sendMail.emailBody += "\n" + line;
          }
        });
      }
      this.sendMail.emailBody=this.sendMail.emailBody?.replaceAll("<br>","\n")
      this.sendMail.fromEmailId = this.emailForm.value.fromEmail;
      if (!this.ccshowError && !this.toshowError) {
      this.commonService.SendEmail(this.sendMail, this.files, this.projectId).subscribe((response) => {
        if(this.headerText === 'Program Review') {
          this.virtualDesktopService.showReviewer();
        }
        else if(this.headerText === 'Correspondence' || this.headerText === 'Request for Additional Information') {
          let communicationsData: any = {};
          communicationsData.emailSubject = this.sendMail.subject;
          communicationsData.emailBody = this.sendMail.emailBody;
          communicationsData.fromEmailAddress = this.sendMail.fromEmailId;
          communicationsData.toEmailId = this.sendMail.toEmailId;
          communicationsData.ccEmailId = this.sendMail.ccEmailId;
          communicationsData.requestDocuments = this.requestedDocs?.map((requestDoc: any) => {
            return {documentName: requestDoc.name, 
              docCategory: requestDoc.category,
              docSubCategory: requestDoc.subCategory,
              otherDocSubCategory: requestDoc.otherSubCategory}
          });
          this.utils.emitLoadingEmitter(true);
          this.virtualDesktopService.saveApplicantCommunications(communicationsData, this.files, this.projectId)
          .subscribe((response) => {
            console.log(response);
            this.utils.emitLoadingEmitter(false);
          }, (error) => {
            this.utils.emitLoadingEmitter(false);
            this.isSubmitted = false;
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error; 
          });
          
        }
          this.close();
          console.log('making thar call');
        },
          (error) => {
            this.isSubmitted = false;
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error; 
          });
      }
    } else {
      alert("Please provide required fields")
    }

  }
  uploadfiles(event: any) {  
    const fileList: FileList =event.target.files;
    const newFiles=Array.from(fileList)
    this.files=this.files.concat(newFiles);
   
  
  }
 removeFile(index:number){
    this.files.splice(index,1)
  }
  toToggleDropdown(flag: boolean) {
    this.toOptions.nativeElement.style.display = flag ? "block" : "none";
  }
  toSearchOptionsFn(str: string) {
    let splitArray = str.split(';');
    let toSearchStr = splitArray[splitArray.length - 1].toLowerCase();
    splitArray.pop();
    let eleminateSearchStr = splitArray.join(';');
    let filteredArray = this.sampleData.filter((item: any) => {
      if (item.emailAddress) {
        return item.emailAddress.toLowerCase().includes(toSearchStr);
      }
    });

    this.existingtotxt = eleminateSearchStr;
    if (filteredArray.length > 0) {
      this.toShowDropdown = true
      this.toToggleDropdown(true);
      this.toSearchEmails = filteredArray
    } else {
      this.toShowDropdown = false;
      this.toToggleDropdown(false);
      this.toSearchEmails = [];
    }
  }
  toSelectOption(value: any) {
    if (value != null && value != "") {
      this.emailForm.patchValue({
        toEmail: this.existingtotxt != "" ?

          this.existingtotxt + ';' + value + ";" :
          value + ';'
      });
    }
    this.toToggleDropdown(false);
    this.toShowDropdown = false;
    this.toValidate();
  }
  toValidate() {
    let splitEmails = this.emailForm.value.toEmail === "" ? [] : this.emailForm.value.toEmail.split(";")
      .map((emailAddr: any) => emailAddr.trim());
    let validEmails: Array<string> = [];
    let storeAllEmails: Array<string> = [];
    if (splitEmails.length > 0) {
      splitEmails.map((email: string) => {
        if (email !== "") {
          storeAllEmails.push(email);
          this.sampleData.filter((item: any) => {
            if (item.emailAddress && item.emailAddress.toLowerCase() === email.toLowerCase()) {
              validEmails.push(email);
            }
          });
        }
      })
      if (storeAllEmails.length === validEmails.length) {
        this.toshowError = false;
      } else {
        this.toshowError = true;
      }
    } else {
      this.toshowError = false;
    }
  }
  toBlurFn() {
    if (!this.toShowDropdown) {
      this.toValidate()
    }
  }

  ccToggleDropdown(flag: boolean) {
    this.ccOptions.nativeElement.style.display = flag ? "block" : "none";
  }
  ccSearchOptionsFn(str: string) {
    let splitArray = str.split(';');
    let ccSearchStr = splitArray[splitArray.length - 1].toLowerCase();
    splitArray.pop();
    let eleminateSearchStr = splitArray.join(';');
    let filteredArray = this.sampleData.filter((item: any) => {
      if (item.emailAddress) {
        return item.emailAddress.toLowerCase().includes(ccSearchStr);
      }
    });

    this.existingcctxt = eleminateSearchStr;
    if (filteredArray.length > 0) {
      this.ccShowDropdown = true
      this.ccToggleDropdown(true);
      this.ccSearchEmails = filteredArray
    } else {
      this.ccShowDropdown = false;
      this.ccToggleDropdown(false);
      this.ccSearchEmails = [];
    }
  }
  ccSelectOption(value: any) {
    if (value != null && value != "") {
      this.emailForm.patchValue({
        ccEmail: this.existingcctxt != "" ?

          this.existingcctxt + ';' + value + ";" :
          value + ';'
      });
    }
    this.ccToggleDropdown(false);
    this.ccShowDropdown = false;
    this.ccValidate();
  }
  ccValidate() {
    let splitEmails = this.emailForm.value.ccEmail === "" ? [] : this.emailForm.value.ccEmail.split(";")
      .map((emailAddr: any) => emailAddr.trim());
    let validEmails: Array<string> = [];
    let storeAllEmails: Array<string> = [];
    if (splitEmails.length > 0) {
      splitEmails.map((email: string, index: number) => {
        if (email !== "") {
          storeAllEmails.push(email);
          this.sampleData.filter((item: any) => {
            if (item.emailAddress && item.emailAddress.toLowerCase() === email.toLowerCase()) {
              validEmails.push(email);
            }
          });
        }
      })
      if (storeAllEmails.length === validEmails.length) {
        this.ccshowError = false;
      } else {
        this.ccshowError = true;
      }
    } else {
      this.ccshowError = false;
    }
  }
  ccBlurFn() {
    if (!this.ccShowDropdown) {
      this.ccValidate()
    }
  }

  pendingOkClicked(){
    this.pendingPopup.close();
    this.modal.nativeElement.style.display = "none";
  }

  pendingCloseClicked(){
    console.log('YOOO');
    
  }
}

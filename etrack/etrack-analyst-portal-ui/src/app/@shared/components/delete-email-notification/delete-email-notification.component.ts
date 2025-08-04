import { Component, ElementRef, OnInit,ViewChild } from '@angular/core';
import { CommonService } from '../../services/commonService';

@Component({
  selector: 'app-delete-email-notification',
  templateUrl: './delete-email-notification.component.html',
  styleUrls: ['./delete-email-notification.component.scss']
})
export class DeleteEmailNotificationComponent implements OnInit {

  @ViewChild('deletemodal') deleteModal!:ElementRef
  correspondanceID!:number
  proejctID!:number
  constructor(private commonService:CommonService) { }

  ngOnInit(): void {
  }
  open(correspondanceId:number,proejctId:number){
    this.correspondanceID=correspondanceId
    this.proejctID=proejctId
    this.deleteModal.nativeElement.style.display="block";
  }

  Dismiss(){
    this.deleteModal.nativeElement.style.display="none";

  }
  Decline(){
    this.deleteModal.nativeElement.style.display="none";
    
  }
  Accept(){
 this.commonService.deleteNotification(this.correspondanceID,this.proejctID.toString())
 .subscribe((response)=>{
  console.log(response);
 })
 this.deleteModal.nativeElement.style.display="none";
  }
}

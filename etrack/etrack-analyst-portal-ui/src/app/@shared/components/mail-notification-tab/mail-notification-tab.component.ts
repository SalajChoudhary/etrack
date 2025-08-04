import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-mail-notification-tab',
  templateUrl: './mail-notification-tab.component.html',
  styleUrls: ['./mail-notification-tab.component.scss']
})
export class MailNotificationTabComponent implements OnInit {

  constructor() { }
  selectedItem:string='Received';
  sections =["Received","Sent"];
  showMessages:boolean=false;
  @Input () projectId!:string;
  @Input() data!: any
  ngOnInit(): void {

    //API for the received
  }

  expand(event: any) {
    if (event.classList.contains("active")) {
      event.classList.remove('active');
      event.classList.add('inactive');
    } else {
      event.classList.remove('inactive');
      event.classList.add('active');
    }
  }
  openSections(input:string){
    this.selectedItem= input;
  }
  getmail(input:any){
    this.showMessages=true;
  }
  
}

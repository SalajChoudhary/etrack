import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { CommonService } from '../../services/commonService';
import { EmailComposeComponent } from '../email-compose/email-compose.component';

@Component({
  selector: 'app-accordion-child',
  templateUrl: './accordion-child.component.html',
  styleUrls: ['./accordion-child.component.scss']
})
export class AccordionChildComponent implements OnInit {

  constructor(
    private commonService: CommonService,
  ) { }
  
  emailData!: any;  
  @ViewChild('emailModal', { static: false }) emailModal!: EmailComposeComponent;
  @Input() user!: any;
  @Input() index!: number;
  @Input() type!: string;
  @Input() projectId!:string;
  isItemOpen: boolean = false;
  emailSubjects: Array<any> = [];
  ngOnInit(): void {
  }

  handleItem(type: string, userItem: any): void {
    if (!this.isItemOpen) {
      this.isItemOpen = false
    } else {
      let user = localStorage.getItem('loggedUserName');
      const correspondenceType = type === 'received' ? 'R' : 'S';
      let emailSenderId = '', emailReceiverId = '';
      if (correspondenceType === 'R') {
        emailReceiverId = user ? user?.replace('SVC', '').substring(1) : '';
        emailSenderId = userItem.emailUserId;
      } else {
        emailReceiverId = userItem.emailUserId;
        emailSenderId = user ? user?.replace('SVC', '').substring(1) : '';
      }
      this.commonService.getEmailSubjectsVD(correspondenceType, emailSenderId, emailReceiverId,this.projectId)
        .then((response) => {
          console.log('Email subjects: ');
          console.log(response);
          console.log('User: ');
          console.log(this.user);
          this.emailSubjects = response;
          this.isItemOpen = true;
        })
    }


  }
  Openmail(notify: any) {
      console.log('notify:');
      console.log(notify);
      this.emailModal.openFromNotifications(notify, true, null);
      this.commonService.emailStatus(notify.topicId, notify.projectId).subscribe((response) => {
        console.log(response);
      });
    }  

}

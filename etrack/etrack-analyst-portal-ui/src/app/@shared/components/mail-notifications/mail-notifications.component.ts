import {
  Component,
  ElementRef,
  Input,
  OnInit,
  ViewChild,
  HostListener,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { emailSend } from 'src/app/@store/models/emailSend';
import { CommonService } from '../../services/commonService';
import { VirtualDesktopService } from '../../services/virtual-desktop.service';
import { DeleteEmailNotificationComponent } from '../delete-email-notification/delete-email-notification.component';
import { EmailComposeComponent } from '../email-compose/email-compose.component';

@Component({
  selector: 'app-mail-notifications',
  templateUrl: './mail-notifications.component.html',
  styleUrls: ['./mail-notifications.component.scss'],
})
export class MailNotificationsComponent implements OnInit {
  @HostListener('document:click')
  clickout() {
    if (!this.isInsideClick) {
      this.emailNotifications.nativeElement.style.display = 'none';
    }
    this.isInsideClick = false;
  }

  @HostListener('click')
  clickInside() {
    this.isInsideClick = true;
  }

  @ViewChild('notificationalert', { static: false })
  emailNotifications!: ElementRef;
  @ViewChild('emailModal', { static: false })
  emailModal!: EmailComposeComponent;
  @ViewChild('deleteModal', { static: false })
  deleteModal!: DeleteEmailNotificationComponent;

  isInsideClick: boolean = false;
  subjecttext: string = '';
  emailData!: any;
  envelopData: any;
  performOpenEmial: boolean = true;
  regularHeader: boolean = true;
  @Input() projectId: any;
  @Input() inquiryId: any;

  constructor(private service: CommonService, private route: ActivatedRoute, private virtualDesktop: VirtualDesktopService) {}

  ngOnInit(): void {
  }

  open(value: boolean) {
    if (value === true) {
      this.regularHeader = false;
      this.service.getVdEnvelops(this.projectId).then(
        (response) => {
          this.envelopData = response;
          this.emailNotifications.nativeElement.style.display = 'block';
        },
        (error) => {
          console.log(error);
        }
      );
    } else {
      this.regularHeader = true;
      this.service.getEnvelops().then(
        (response) => {
          console.log(response);
          this.envelopData = response;
          this.emailNotifications.nativeElement.style.display = 'block';
        },
        (error) => {
          console.log(error);
        }
      );
    }
  }

  close() {
    this.emailNotifications.nativeElement.style.display = 'none';
  }

  Openmail(notify: any) {
    if (this.performOpenEmial) {
      this.emailModal.openFromNotifications(notify, true, null);
      this.service
        .emailStatus(notify.topicId, notify.projectId)
        .subscribe((response) => {
          console.log(response);
        });
    }
    this.performOpenEmial = true;
  }
  deleteEmail(notify: any) {
    this.deleteModal.open(notify.correspondenceId, notify.projectId);
  }
  emailRead(notifyemail: any) {
    if (notifyemail.emailRead == 1) return true;
    else false;
  }
  navigateToVirtualDesktop(notifyemail: any) {
    this.performOpenEmial = false;
   // localStorage.setItem('projectId', notifyemail.projectId); // TODO: remove line in favor of sharing project id on address bar
    window.open('/virtual-workspace/'+notifyemail.projectId, '_blank');
  }
}

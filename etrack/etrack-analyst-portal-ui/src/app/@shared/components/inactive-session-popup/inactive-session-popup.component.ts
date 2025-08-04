import { Component, OnInit, ViewChild } from '@angular/core';
import { CustomModalPopupComponent } from '../custom-modal-popup/custom-modal-popup.component';
import { AuthService } from 'src/app/core/auth/auth.service';
import { Subscription } from 'rxjs';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-inactive-session-popup',
  templateUrl: './inactive-session-popup.component.html',
  styleUrls: ['./inactive-session-popup.component.scss']
})
export class InactiveSessionPopupComponent implements OnInit {

  inactiveSessionSubscription: Subscription = new Subscription();
  modalConfig! : any;
  @ViewChild('inactiveSessionPopup', {static: true})
  inactiveSessionPopup!: CustomModalPopupComponent;
  sessionTimeout: number;
  popupMessage: String;

  constructor(private authService: AuthService) { 
    this.sessionTimeout = environment.sessionTimeout;
    this.popupMessage = "You have been inactive for ";
    if(this.sessionTimeout < 60000) {
      this.popupMessage += `${this.sessionTimeout/1000} seconds`;
    }
    else {
      this.popupMessage += `${this.sessionTimeout/60000} minute`;
      if(this.sessionTimeout > 60000) {
        this.popupMessage += 's';
      }
    }
    this.popupMessage += '. You will now be logged out.';
  }

  ngOnInit(): void {
    this.modalConfig = {
      title: '',
      showHeader: false,
    };
    this.inactiveSessionSubscription = this.authService.emitInactiveSessionInfo.subscribe(flag => {
      if(flag) {
        this.inactiveSessionPopup.open('info');
      }
    });
  }

  okClicked() {
    this.authService.logout();
  }

  ngOnDestroy(): void {
    this.inactiveSessionSubscription.unsubscribe();
  }


}

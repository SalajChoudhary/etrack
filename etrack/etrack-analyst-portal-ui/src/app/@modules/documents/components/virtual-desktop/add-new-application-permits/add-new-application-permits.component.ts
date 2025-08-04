import {
  Component,
  OnInit,
  Input,
  ViewChild,
  Output,
  EventEmitter,
} from '@angular/core';
import { BehaviorSubject, Subject, Subscription } from 'rxjs';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { SuccessPopupComponent } from 'src/app/@shared/components/success-popup/success-popup.component';
import { ModalConfig } from 'src/app/modal.config';

@Component({
  selector: 'app-add-new-application-permits',
  templateUrl: './add-new-application-permits.component.html',
  styleUrls: ['./add-new-application-permits.component.scss']
})
export class AddNewApplicationPermitsComponent implements OnInit {
  @Input() isFromDisposed:any = false;

  @Input() errorMsgObj:any = {};
  @ViewChild('addNewModal')
  private addNewModal!: CustomModalPopupComponent;
  @ViewChild('successPopup', { static: true }) successPopup!: SuccessPopupComponent;

  openNewModal = new BehaviorSubject<boolean>(false);
  successMsg:string = 'Additional Application Submitted Successfully';
  modalConfig: ModalConfig = {
    title: '',
    showHeader: false,
    showClose: true,
    onClose: () => {
      this.openNewModal.next(false);
      this.addNewModal.dismiss();
      return true;
    },
    onDismiss: () => {
      this.openNewModal.next(false);
      return true;
    },
    shouldClose: () => {
      return true;
    },
    shouldDismiss: () => {
      return true;
    },
  };

  constructor() { }


  ngOnInit(): void {
  }


  addNewModalClosed(event: any) {}

  onCancel(event: any) {
    this.addNewModal.close();
  }
  onSuccess(event:any){
    this.addNewModal.close();
    setTimeout(()=>{
      this.successPopup.open();
    })

  }
  onSuccesPopupOkClicked(){
    window.location.reload();
  }

  onAddNew() {
    this.openNewModal.next(true);
    this.addNewModal.open('l');
    this.openNewModal.subscribe((val) => console.log(val));
  }

}

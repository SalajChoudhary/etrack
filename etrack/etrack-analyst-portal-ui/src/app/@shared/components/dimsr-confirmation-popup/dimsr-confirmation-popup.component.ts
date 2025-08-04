import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from '@angular/core';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CommonService } from '../../services/commonService';
@Component({
  selector: 'app-dimsr-confirmation-popup',
  templateUrl: './dimsr-confirmation-popup.component.html',
  styleUrls: ['./dimsr-confirmation-popup.component.scss']
})
export class DimsrConfirmationPopupComponent implements OnInit {
  modalRef!: NgbModalRef;
  @Output() onOkClick = new EventEmitter();
  @ViewChild('warningModal')
  public modalContent!: TemplateRef<DimsrConfirmationPopupComponent>;
  data: any;
  constructor(
    public commonService: CommonService,
    public modalService: NgbModal

  ) { }

  ngOnInit(): void { }
  okClicked() {
    this.onOkClick.emit('ok');
  }
  open(data: any): Promise<boolean> {
    this.data = data;
    return new Promise<boolean>((resolve) => {
      this.modalRef = this.modalService.open(this.modalContent, {
        size: 'small-popup',
        backdrop: 'static',
      });
      this.modalRef.result.then(
        (result) => { },
        (reason) => { }
      );
    });
  }
  navigateToVirtualDesktop() {
    if (this.data?.projectId != null) {
      localStorage.setItem('projectId', this.data.projectId);
      sessionStorage.setItem('navigatedFrom', 'dimsr');
      window.open(`virtual-workspace/${this.data.projectId}`);
      this.modalRef.close();
      window.location.reload();
    }
    else {
      this.modalRef.close();
    }
  }

}
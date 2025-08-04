import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-appln-permit-desc-modal',
  templateUrl: './appln-permit-desc-modal.component.html',
  styleUrls: ['./appln-permit-desc-modal.component.scss'],
})
export class ApplnPermitDescModalComponent implements OnInit {
  applnPermitDesc: any = '';
  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {}

  onClose() {
    this.activeModal.close();
  }
}

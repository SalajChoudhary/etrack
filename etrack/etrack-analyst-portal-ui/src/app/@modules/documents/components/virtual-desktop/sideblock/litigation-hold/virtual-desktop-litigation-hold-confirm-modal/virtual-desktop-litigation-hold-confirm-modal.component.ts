import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-virtual-desktop-litigation-hold-confirm-modal',
  templateUrl: './virtual-desktop-litigation-hold-confirm-modal.component.html',
  styleUrls: ['./virtual-desktop-litigation-hold-confirm-modal.component.scss']
})
export class VirtualDesktopLitigationHoldConfirmModalComponent implements OnInit {

  constructor(public activeModal:NgbActiveModal) { }

  ngOnInit(): void {
  }

}

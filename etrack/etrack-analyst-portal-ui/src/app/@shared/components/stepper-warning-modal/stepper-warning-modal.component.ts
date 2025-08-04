import { Component, OnInit } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
@Component({
  selector: 'app-stepper-warning-modal',
  templateUrl: './stepper-warning-modal.component.html',
  styleUrls: ['./stepper-warning-modal.component.scss']
})
export class StepperWarningModalComponent implements OnInit {

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}

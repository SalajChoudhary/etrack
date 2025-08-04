import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CommonService } from '../../services/commonService';


@Component({
  selector: 'app-success-popup',
  templateUrl: './success-popup.component.html',
  styleUrls: ['./success-popup.component.scss'],
})
export class SuccessPopupComponent implements OnInit {
  modalRef!: NgbModalRef;
  @Output() onOkClick = new EventEmitter();
  @Output() onRunClick = new EventEmitter();
  @Input() successMsg='';
  @Input() showRun!: boolean;
  @ViewChild('warningModal')
  public modalContent!: TemplateRef<SuccessPopupComponent>;

  constructor(
    public commonService: CommonService,
    public modalService: NgbModal

  ) {}

  ngOnInit(): void {}
  okClicked() {
    this.onOkClick.emit('ok');
  }
  open(): Promise<boolean> {
    return new Promise<boolean>((resolve) => {
      this.modalRef = this.modalService.open(this.modalContent, {
        size: 'small-popup',
        backdrop: 'static',
      });
      this.modalRef.result.then(
        (result) => {},
        (reason) => {}
      );
    });
  }
}

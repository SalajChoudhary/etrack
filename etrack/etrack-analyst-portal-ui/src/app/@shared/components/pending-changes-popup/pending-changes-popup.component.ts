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
import { takeUntil } from 'rxjs/operators';
import { fromEvent, Subject } from 'rxjs';
import { ProjectService } from '../../services/projectService';


@Component({
  selector: 'app-pending-changes-popup',
  templateUrl: './pending-changes-popup.component.html',
  styleUrls: ['./pending-changes-popup.component.scss'],
})
export class PendingChangesPopupComponent implements OnInit {
  modalRef!: NgbModalRef;
  @Output() onOkClick = new EventEmitter();
  @Output() onCancelClicked = new EventEmitter();
  @ViewChild('warningModal')
  public modalContent!: TemplateRef<PendingChangesPopupComponent>;
  private unsubscriber : Subject<void> = new Subject<void>();
  @Output() onCloseClicked = new EventEmitter();



  constructor(
    public commonService: CommonService,
    public modalService: NgbModal,
    public projectService: ProjectService
  ) {}

  ngOnInit(): void {
      //diables browswers back button
   history.pushState(null, '');
   fromEvent(window, 'popstate').pipe(
     takeUntil(this.unsubscriber)
   ).subscribe((_) => {
     history.pushState(null, '');
   });
  }
  okClicked() {
    console.log('we here');
    
    this.onOkClick.emit('ok');
  }
  open(): Promise<boolean> {
    return new Promise<boolean>((resolve) => {
      this.modalRef = this.modalService.open(this.modalContent, {
        size: 'pending-pop',
        backdrop: 'static',
      });
      this.modalRef.result.then(
        (result) => {},
        (reason) => {}
      );
    });
  }

  close(){
    console.log('Nexting closw');
    
    this.onCloseClicked.emit('closed');
    this.projectService.setDestroyAssociatedSub('Hey');
    this.modalService.dismissAll();
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }

  cancelClicked(){
    this.onCancelClicked.emit('cancel');
  // this.modalRef.close();
    
  }
  
}

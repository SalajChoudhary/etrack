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
  selector: 'app-warning-pop-up',
  templateUrl: './warning-pop-up.component.html',
  styleUrls: ['./warning-pop-up.component.scss']
})
export class WarningPopUpComponent implements OnInit {

  modalRef!: NgbModalRef;
  @Output() onOkClick = new EventEmitter();
  @Input() PopUpMessage!:any;
  @ViewChild('warningModal')
  public modalContent!: TemplateRef<WarningPopUpComponent>;
  private unsubscriber : Subject<void> = new Subject<void>();
  @Output() onCloseClicked = new EventEmitter();



  constructor(
    public commonService: CommonService,
    public modalService: NgbModal,
    public projectService: ProjectService
  ) {}

  ngOnInit(): void {
   history.pushState(null, '');
   fromEvent(window, 'popstate').pipe(
     takeUntil(this.unsubscriber)
   ).subscribe((_) => {
     history.pushState(null, '');
   });
  }
  okClicked() {
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
    console.log('we here!');    
  }
  
}

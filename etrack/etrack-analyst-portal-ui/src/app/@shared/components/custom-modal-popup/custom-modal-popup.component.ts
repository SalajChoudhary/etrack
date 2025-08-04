import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Injectable,
  Input,
  OnInit,
  Output,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import {
  ModalDismissReasons,
  NgbModal,
  NgbModalRef,
} from '@ng-bootstrap/ng-bootstrap';
import { fromEvent, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { ModalConfig } from 'src/app/modal.config';
import { EventEmitterService } from '../../services/event-emitter.service';

@Component({
  selector: 'app-custom-modal-popup',
  templateUrl: './custom-modal-popup.component.html',
  styleUrls: ['./custom-modal-popup.component.scss'],
})
@Injectable()
export class CustomModalPopupComponent implements AfterViewInit, OnInit {
  @Output() modalClosed = new EventEmitter();
  @Input() public modalConfig!: ModalConfig;
  @ViewChild('modal')
  public modalContent!: TemplateRef<CustomModalPopupComponent>;
  private modalRef!: NgbModalRef;
  closeResult!: string;
  private unsubscriber: Subject<void> = new Subject<void>();

  constructor(
    private modalService: NgbModal,
    private cdr: ChangeDetectorRef,
    private eventEmitterService: EventEmitterService
  ) {}

  ngOnInit(): void {
    //diables browswers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }

  ngAfterViewInit(): void {
    //Called after ngAfterContentInit when the component's view has been initialized. Applies to components only.
    //Add 'implements AfterViewInit' to the class.

    this.cdr.detectChanges();
  }
  ngOnChanges() {
    //console.log(this.modalConfig)
  }
  open(size?: string): Promise<boolean> {
    this.modalService.dismissAll();
    return new Promise<boolean>((resolve) => {
      this.modalRef = this.modalService.open(this.modalContent, {
        size: size ? size : 'lg',
        backdrop: 'static',
        windowClass:
          size == 'xxl' ? 'xtra-width' : size  ? size : '',
      });
      this.modalRef.result.then(
        (result) => {
          this.closeResult = `Closed with: ${result}`;
        },
        (reason) => {
          this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
        }
      );
    });
  }

  async close(): Promise<void> {
    this.modalService.dismissAll();
    
    if (this.modalClosed !== undefined) {
      console.log('modal is not undefined.');
      
      // this.modalClosed?.emit('closed');
      if (
        this.modalConfig?.shouldClose === undefined ||
        (await this.modalConfig?.shouldClose())
      ) {
        const result =
          this.modalConfig?.onClose === undefined ||
          (await this.modalConfig?.onClose());
        this.modalRef?.close(result);
      }
    }
   // this.modalConfig.onClose;
    this.modalService.dismissAll();
  }

  async dismiss(): Promise<void> {
    if (
      this.modalConfig.shouldDismiss === undefined ||
      (await this.modalConfig.shouldDismiss())
    ) {
      const result =
        this.modalConfig.onDismiss === undefined ||
        (await this.modalConfig.onDismiss());
      this.modalRef.close(result);
    }
    this.modalConfig.onDismiss;
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  async closeHandler(): Promise<void> {
    console.log('close handler ::::::');
    this.modalService.dismissAll();
    this.eventEmitterService.onFirstComponentButtonClick();
    if (this.modalClosed !== undefined) {
      this.modalClosed?.emit('closed');
      if (
        this.modalConfig?.shouldClose === undefined ||
        (await this.modalConfig?.shouldClose())
      ) {
        const result =
          this.modalConfig?.onClose === undefined ||
          (await this.modalConfig?.onClose());
        this.modalRef?.close(result);
      }
    }
    this.modalConfig.onClose;
    this.modalService.dismissAll();
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }
}

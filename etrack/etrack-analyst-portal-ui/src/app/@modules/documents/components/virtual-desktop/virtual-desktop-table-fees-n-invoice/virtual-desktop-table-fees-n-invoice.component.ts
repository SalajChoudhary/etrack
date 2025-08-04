import {
  Component,
  OnInit,
  Input,
  ViewChild,
  Output,
  EventEmitter,
} from '@angular/core';
import { ActivatedRoute} from '@angular/router';
import { BehaviorSubject, Subject, Subscription } from 'rxjs';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { VirtualDesktopService } from 'src/app/@shared/services/virtual-desktop.service';
import { ModalConfig } from 'src/app/modal.config';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { get, isEmpty, isEqual } from 'lodash';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { CommonService } from 'src/app/@shared/services/commonService';

@Component({
  selector: 'app-virtual-desktop-table-fees-n-invoice',
  templateUrl: './virtual-desktop-table-fees-n-invoice.component.html',
  styleUrls: ['./virtual-desktop-table-fees-n-invoice.component.scss'],
})
export class VirtualDesktopTableFeesNInvoiceComponent implements OnInit {
  showServerError = false;
  serverErrorMessage!: string;
  @Input() virtualDesktopData: any = {};
  invoice = this.virtualDesktopData?.invoice;
  @Input() errorMsgObj = {};
  @Input() isReadOnly: boolean = false;
  @Input() isFromDisposed: boolean = false;
  @Output() reloadData: any = new EventEmitter();
  @ViewChild('addNewModal')
  private addNewModal!: CustomModalPopupComponent;
  openNewModal = new BehaviorSubject<boolean>(false);
  selectedInvoiceId = '';
  projectId:any = '';
  systemParameters:any;
  searchConfig: ModalConfig = {
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
  feesAndInvoiceOptions = {
    FW: [],
    LG: [],
    TW: [],
  };
  @Output() onFeesAndInvoiceLoad = new EventEmitter<any>();

  get canShowAddNewButton() {
    return (
      !isEmpty(this.feesAndInvoiceOptions?.TW) ||
      !isEmpty(this.feesAndInvoiceOptions?.LG) ||
      !isEmpty(this.feesAndInvoiceOptions?.FW) ||
      this.virtualDesktopData.invoiceReq == 'Y'
    );
  }
  constructor(
    private virtualDesktopService: VirtualDesktopService,
    private projectSrv: ProjectService,
    private errorService: ErrorService,
    private commonService: CommonService,
    private activatedRoute:ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.commonService.getSystemParameters().subscribe(data=>{
      this.systemParameters=data;
    });
    this.activatedRoute.params.subscribe((params:any)=>{
        this.projectId = params.projectId;
      this.getFeesAndInvoicesOptions();

    })
  }

  ngOnChanges() {
    setTimeout(() => {
      this.invoice = this.virtualDesktopData?.invoice;
    });
  }

  getFeesAndInvoicesOptions() {
    this.showServerError = false;
    //if(this.virtualDesktopService.)
    this.projectSrv.getInvoicePermits(this.projectId).then(
      (res: any) => {
        // const etrack = get(res, 'etrack-permits', []);
        const invoiceFee = res;
        this.virtualDesktopService
          .getFeesAndInvoiceOptions(this.projectId)
          .subscribe((res: any) => {
            console.log("VW Invoice",res)
            this.feesAndInvoiceOptions = res;
            const invoiceFeeHasFW = invoiceFee.find((item: any) =>
              isEqual(item.permitTypeCode, 'FW')
            );
            const invoiceFeeHasLG = invoiceFee.find((item: any) =>
              isEqual(item.permitTypeCode, 'LG')
            );
            const invoiceFeeHasTW = invoiceFee.find((item: any) =>
              isEqual(item.permitTypeCode, 'TW')
            );
            this.feesAndInvoiceOptions = {
              FW: !isEmpty(invoiceFeeHasFW) ? get(res, 'FW', []) : [],
              LG: !isEmpty(invoiceFeeHasLG) ? get(res, 'LG', []) : [],
              TW: !isEmpty(invoiceFeeHasTW) ? get(res, 'TW', []) : [],
            };
            this.onFeesAndInvoiceLoad.emit(this.feesAndInvoiceOptions);
          });
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  onCancel(event: any) {
    this.addNewModal.close();
    this.reloadData.emit(event);
  }

  onAddNew() {
    this.showServerError = false;
    this.selectedInvoiceId = '';
    this.openNewModal.next(true);
    this.addNewModal.open('xl');
    this.openNewModal.subscribe((val) => console.log(val));
  }

  onPaymentClick(invoiceId: any) {
    this.selectedInvoiceId = invoiceId;
    this.openNewModal.next(true);
    this.addNewModal.open();
    this.openNewModal.subscribe((val) => {
      if (!val) {
        this.selectedInvoiceId = '';
      }
    });
  }

  addNewModalClosed(event: any) {}

  onReceived() {}
  openexistingNvoice(value: any) {
    this.selectedInvoiceId = value;
    this.openNewModal.next(true);
    this.addNewModal.open('xl');
    this.openNewModal.subscribe((val) => console.log(val));
  }
  openinvoiceDocument(id: string) {
    this.showServerError = false;
    this.projectSrv.generateInvoiceDocument(this.projectId, id).then(
      (response) => {
        var link = document.createElement('a');
        link.href = window.URL.createObjectURL(response);
        window.open(link.href);
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }
}

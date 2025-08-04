import { Component, OnInit, Input } from '@angular/core';
import { isEmpty } from 'lodash';
import {
  PendingApplicationsHeaders,
  ActiveAuthorizationsHeaders,
} from './table-headers';
import {
  PendingApplicationsRows,
  ActiveAuthorizationsRows,
} from './table-rows';
import { DatePipe } from '@angular/common';
import moment from 'moment';
@Component({
  selector: 'app-virtual-desktop-grid',
  templateUrl: './virtual-desktop-grid.component.html',
  styleUrls: ['./virtual-desktop-grid.component.scss'],
})
export class VirtualDesktopGridComponent implements OnInit {
  get isPendingApplications() {
    return this.title == 'Pending Applications';
  }

  get isActiveAuthorizations() {
    return this.title == 'Active Authorizations';
  }

  @Input() title: string = '';
  @Input() data: any = [];
  headers: any[] = [];
  rows: any[] = [];
  pipe = new DatePipe('en-US');

  constructor() {}

  ngOnInit(): void {

  }

  ngOnChanges(): void {
    this.setHeaders();
    this.setRows();
  }

  setHeaders() {
    if (this.isPendingApplications) {
      this.headers = PendingApplicationsHeaders;
    }
    if (this.isActiveAuthorizations) {
      this.headers = ActiveAuthorizationsHeaders;
    }
  }
  setRows() {
    if(isEmpty(this.data)){
      this.rows = [];
      return;
    }

    if (this.isPendingApplications) {
    //  console.log("pendingApplications", this.data)
      this.rows = this.data.map((item: any) => {
        item?.receivedDate ? item.receivedDate = this.pipe.transform(item?.receivedDate, 'MM/dd/yyyy') : '';
        return {
          projectID: item?.projectId,
          receivedAt: item?.rcvdDate ? new Date(item.rcvdDate):'',
          permitType: item?.permitType,
          appType: item?.appType,
          appStatus: item?.dartStatus,
          appId: item?.trackedIdFormatted,
          ren: item?.renOrderNum,
          mod: item?.modOrderNum,
        };
      });
    }
    if (this.isActiveAuthorizations) {
      this.rows = this.data.map((item: any) => {
        item?.sapaDate ? item.sapaDate = this.pipe.transform(item?.sapaDate, 'MM/dd/yyyy') : '';
        return {
          projectID: item?.project_id,
          "permitType": item?.permitType,
          "appType": item?.transType,
          "appId": item?.trackedIdFormatted,
          "ren": item?.renOrderNum,
          "mod": item?.modOrderNum,
          "sapaDeadline": item?.sapaDate,
          "sapaDeadlineValue":item?.sapaDate?moment(item?.sapaDate,'MM/DD/YYYY').valueOf():'',
          "sapaExtended": item?.sapaInd ==  1?'Y':'N'
        };
      });
    }
  }
}

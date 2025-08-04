import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-litigation-hold-table',
  templateUrl: './litigation-hold-table.component.html',
  styleUrls: ['./litigation-hold-table.component.scss']
})
export class LitigationHoldTableComponent implements OnInit {

  @Input() virtualDesktopData:any;

  get rows(){
    return this.virtualDesktopData?.litigationRequestHistory;
  }
  constructor() { }

  ngOnInit(): void {
  }

}

import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-virtual-desktop-foil-table',
  templateUrl: './virtual-desktop-foil-table.component.html',
  styleUrls: ['./virtual-desktop-foil-table.component.scss'],
})
export class VirtualDesktopFoilTableComponent implements OnInit {
  @Input() rows: any = [];
  @Input() userIsProgramReviewer: any;
  @Output() remove = new EventEmitter();
  constructor() {}

  ngOnInit(): void {}

  onDelete(row: any) {
    // const verify = confirm(`Are you sure you want to remove this foil number ${row.foil}`);
    // if(!verify){
    //   return false;
    // }
    this.remove.emit(row);
  }
}

import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-virtual-desktop-litigation-hold-banner',
  templateUrl: './virtual-desktop-litigation-hold-banner.component.html',
  styleUrls: ['./virtual-desktop-litigation-hold-banner.component.scss']
})
export class VirtualDesktopLitigationHoldBannerComponent implements OnInit {

  @Input() projectId:string = '';
  constructor() { }

  ngOnInit(): void {
  }

}

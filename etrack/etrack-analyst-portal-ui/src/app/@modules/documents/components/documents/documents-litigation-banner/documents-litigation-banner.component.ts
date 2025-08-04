import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-documents-litigation-banner',
  templateUrl: './documents-litigation-banner.component.html',
  styleUrls: ['./documents-litigation-banner.component.scss']
})
export class DocumentsLitigationBannerComponent implements OnInit {

  @Input() litigationHoldProjects:any[] = [];
  constructor() { }

  ngOnInit(): void {
  }

}

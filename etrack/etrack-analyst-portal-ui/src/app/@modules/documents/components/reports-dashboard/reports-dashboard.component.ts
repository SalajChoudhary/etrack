import { Component, OnInit } from '@angular/core';

@Component({
  templateUrl: './reports-dashboard.component.html',
  styleUrls: ['./reports-dashboard.component.scss']
})
export class ReportsDashboardComponent implements OnInit {

  activeTab: any = 'reports';
  constructor() { }

  ngOnInit(): void {
  }

  innerTabChange(input: any) {
    this.activeTab = input;
    switch (input) {
      case 'reports':
        break;

      case 'candidate':
        break;
      
      default:
        break;
    }
  }

}

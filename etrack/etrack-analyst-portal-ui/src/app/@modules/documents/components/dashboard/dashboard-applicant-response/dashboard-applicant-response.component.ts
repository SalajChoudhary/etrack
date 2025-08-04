import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { AuthService } from 'src/app/core/auth/auth.service';
import { DashboardService } from 'src/app/@shared/services/dashboard.service';

@Component({
  selector: 'app-dashboard-applicant-response',
  templateUrl: './dashboard-applicant-response.component.html',
  styleUrls: ['./dashboard-applicant-response.component.scss']
})
export class DashboardApplicantResponseComponent implements OnInit {

  @Input() rows:any = [];
  // @Input() totalRecords:any = 0;
 
  constructor(private authSrv:AuthService, private srv:DashboardService) { }

  ngOnInit(): void {
  
  }

  

}

import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonService } from 'src/app/@shared/services/commonService';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard-validate-data',
  templateUrl: './dashboard-validate-data.component.html',
  styleUrls: ['./dashboard-validate-data.component.scss']
})

export class DashboardValidateDataComponent implements OnInit {

  @Input() rows:any = [];
  @Input() totalRecords:any = 0;
 
  constructor(private router:Router, private commonService:CommonService) { }

  ngOnInit(): void {
  
  }
  truncate(text:string){
    return 'hello';
  }
  navigate(projectId:any){
    localStorage.setItem("projectId",projectId);
    localStorage.setItem("mode", 'validate');
    this.commonService.activeMode.next('validate');
    this.commonService.projectIdChanged.next(true);
    this.router.navigate(['/apply-for-permit-details']); // , { queryParams: { mode: 'validate'} }
  }
  
  navigateToVirtualDesktop(row:any){
    localStorage.setItem("projectId",row?.projectId);
    this.router.navigate(['/virtual-workspace']); // , { queryParams: { mode: 'validate'} }
  }

}

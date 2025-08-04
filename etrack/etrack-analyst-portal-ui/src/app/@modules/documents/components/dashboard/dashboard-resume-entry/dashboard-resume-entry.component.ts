import { Component, OnInit, Input, Output, EventEmitter,ViewChild } from '@angular/core';
import { CommonService } from 'src/app/@shared/services/commonService';
import { Router } from '@angular/router';
import { DashboardService } from 'src/app/@shared/services/dashboard.service';
import { isEqual, remove } from 'lodash';
import { BehaviorSubject, fromEvent, Subject, Subscription } from 'rxjs';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { ModalConfig } from 'src/app/modal.config';
import { DeleteConfirmPopupComponent } from '../../../../../@shared/components/delete-confirm-popup/delete-confirm-popup.component';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard-resume-entry',
  templateUrl: './dashboard-resume-entry.component.html',
  styleUrls: ['./dashboard-resume-entry.component.scss']
})
export class DashboardResumeEntryComponent implements OnInit {

  @Input() rows:any = [];
  @Input() totalRecords:any = 0;
  @Output() next = new EventEmitter();
  deleteProjectId:any = '';
  deleteIsClicked : Subject<boolean> = new Subject();
  deleteProject : Subject<boolean> = new Subject();
  private unsubscriber : Subject<void> = new Subject<void>();


  

  //   modalConfig: ModalConfig = {
  //   title: '',
  //   showHeader: false,
  //   showClose: true,
  //   onClose: () => {
  //     this.deleteProjectId = '';
  //     //this.confirmDeleteModal.dismiss();
  //     return true;
  //   },
  //   onDismiss: () => {
  //     this.deleteProjectId = '';
  //     return true;
  //   },
  //   shouldClose: () => {
  //     return true;
  //   },
  //   shouldDismiss: () => {
  //     return true;
  //   },
  // };

  constructor(private router:Router, 
    private dashboardSrv:DashboardService,
    private commonService:CommonService) { }

  ngOnInit(): void {
      //diables browswers back button
   history.pushState(null, '');
   fromEvent(window, 'popstate').pipe(
     takeUntil(this.unsubscriber)
   ).subscribe((_) => {
     history.pushState(null, '');
   });
    
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }
  

  navigate(projectId:any){
    localStorage.setItem("projectId",projectId);
    this.commonService.projectIdChanged.next(true);
    this.commonService.activeMode.next('');
    localStorage.setItem('mode', '');
    localStorage.setItem('emergencyAuth', '');
    this.router.navigate(['/apply-for-permit-details']);
  }
  onDeleteApplication(row:any){ 
    this.deleteProjectId = row.projectId;
    //this.confirmDeleteModal.open();
    this.deleteIsClicked.next(true);
    
  }
  deleteProjects(){
    this.dashboardSrv.deleteApplications(this.deleteProjectId).subscribe(()=>{
      remove(this.rows, (item:any) => isEqual(item.projectId, this.deleteProjectId))
    },
    ()=>{
      alert("Error, unable to delete application");
    });
    this.deleteProject.subscribe((value)=>{
      
      this.deleteProjects();
    })

  }


}

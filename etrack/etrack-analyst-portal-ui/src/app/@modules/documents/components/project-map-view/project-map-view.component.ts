import {
  Component,
  OnInit
} from '@angular/core';
import { TitleCasePipe } from '@angular/common';
import { CommonService } from 'src/app/@shared/services/commonService';
import { Utils } from 'src/app/@shared/services/utils';
import { takeUntil } from 'rxjs/operators';
import { fromEvent, Subject} from 'rxjs';
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/core/auth/auth.service';

@Component({
  selector: 'app-project-map-view',
  templateUrl: './project-map-view.component.html',
  styleUrls: ['./project-map-view.component.scss'],
  providers: [TitleCasePipe,{
    provide: STEPPER_GLOBAL_OPTIONS, useValue: { displayDefaultIndicatorType: false }
  }]
})
export class ProjectMapViewComponent implements OnInit {
  userRoles: any[] = [];
  errorMsgObj: any = {};
  mapStyleClass='parent2';

  private unsubscriber: Subject<void> = new Subject<void>();

  mapProperties = {
    basemap: 'streets',
    center: [-75.62757627797825, 42.98572311852962],
    zoom: 5,
  };

  projectId:any;
  decId:any;
  constructor(public utils: Utils, public commonService: CommonService, private route: ActivatedRoute, private router: Router,private authService: AuthService,) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params:any)=>{
      this.projectId = params.projectId;
      if(params.dimsrDecId !==undefined && params.dimsrDecId !==null){
        this.decId= params.dimsrDecId;
      }
      console.log("query dimsrDecId", this.decId);
      console.log("query Project id", this.projectId);
    })
    history.pushState(null, '');
    //disables browsers back button
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }

  getCurrentUserRole() {
    let userInfo = this.authService.getUserInfo();
    this.commonService
      .getUsersRoleAndPermissions(userInfo.ppid)
      .then((response) => {
        this.userRoles = response.roles;
      });
   }

  close():void{
    window.close();
  }
}

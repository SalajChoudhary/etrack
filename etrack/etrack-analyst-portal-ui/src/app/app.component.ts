import { Component } from '@angular/core';
import { AuthService } from './core/auth/auth.service';
import { Utils } from './@shared/services/utils';
import { CommonService } from './@shared/services/commonService';
import esriConfig from "@arcgis/core/config.js";
import { fromEvent, Subscription } from 'rxjs';
import { UserRole } from './@shared/constants/UserRole';
esriConfig.assetsPath = "./assets";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'DEC-Track-ng Application';
  loading: boolean = false;
  errorMsgObj: any;
  systemParameters:any;
  showAlertNotification!: boolean;
  isFixed:boolean=false;
  subs = new Subscription();
  rolename : string ='';
  constructor(private authService: AuthService, public utils: Utils, public commonService: CommonService) {
    this.authService.initAuth();
    this.commonService.isFixedFooter.subscribe((val:boolean)=> this.isFixed=val)
    this.utils.loadingEmitter
      .subscribe((_: any) => {
        setTimeout(()=>{
          this.loading = (_ == "true") ? true : false;
        })
      })
  }
  ngOnInit() {
    sessionStorage.removeItem('searchedApplicantData');
    //this.getAllErrorMsgs();
    this.commonService.showAlertNotification.subscribe(data => {
      this.showAlertNotification = data;
    });
    this.commonService.getSystemParameters().subscribe(data=>{
      this.systemParameters=data;
    });

    this.getCurrentUserRole();
    
  }

  getCurrentUserRole() {
    this.subs.add(
      this.authService.emitAuthInfo.subscribe((authInfo: any) => {
        if (authInfo === null) return;
        if (authInfo && !authInfo.isError) 
        if(authInfo.roles && authInfo.roles != undefined){
          this.rolename = authInfo.roles[0];
          // this.rolename = 'DEC Program Staff'; // only for testing purpose
          if(this.rolename ==UserRole.DEC_Program_Staff){
            this.rolename = 'Program Area Reviewer';
          }
        }        
      }) 
    );
  }


  ngOnDestroy() {
    this.authService.ngOnDestroy();
    this.subs.unsubscribe();
  }

  openHelpPdf() {
    window.open(
      '../../assets/data/eTrack Analyst Portal (PI 3) User Guide.pdf',
      '_blank'
    );
  }
}
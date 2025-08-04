import { Component, OnInit } from '@angular/core';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { Utils } from 'src/app/@shared/services/utils';
import { AuthService } from 'src/app/core/auth/auth.service';

@Component({
  templateUrl: './maintenance-dashboard.component.html',
  styleUrls: ['./maintenance-dashboard.component.scss']
})
export class MaintenanceDashboardComponent implements OnInit {
  activeTab: any = 'keyword';
  userRoles:any[]=[];
  UserRole = UserRole;

  constructor(
    public utils: Utils,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.getCurrentUserRole();
  }

  get userIsSystemAdmin() {
    return this.userRoles.includes(UserRole.System_Admin);
  }
  getCurrentUserRole() {
    this.authService.emitAuthInfo.subscribe((authInfo: any) => {
      if (authInfo === null) return;
      if (authInfo && !authInfo.isError) this.userRoles = authInfo.roles;
      if (this.userRoles.includes(UserRole.Analyst)) {
        this.activeTab = 'archive';
      }
    })
}

  innerTabChange(input: any) {
    this.activeTab = input;
    switch (input) {
      case 'keyword':
        break;
        
      case 'candidate':
          break;

      case 'documents':
        //this.utils.emitLoadingEmitter(true)
        break;

        case 'permitType':
        //this.utils.emitLoadingEmitter(true)
        break;

      case 'admins':
          //this.utils.emitLoadingEmitter(true)
        break;
      
      default:
        break;
    }
  }
}

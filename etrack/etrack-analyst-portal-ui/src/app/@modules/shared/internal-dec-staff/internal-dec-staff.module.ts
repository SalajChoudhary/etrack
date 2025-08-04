import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InternalDecStaffRoutingModule } from './internal-dec-staff-routing.module';
import { SharedModule } from "../../shared/shared.module";
import { LandingComponent } from './landing/landing.component';



@NgModule({
  declarations: [
    LandingComponent
  ],
  imports: [
    CommonModule,
    InternalDecStaffRoutingModule,
    SharedModule
  ]
})
export class InternalDecStaffModule { }

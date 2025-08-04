import { ErrorHandler, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClient, HTTP_INTERCEPTORS} from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DocumentsModule } from './@modules/documents/documents.module';
import { SharedModule, HttpLoaderFactory } from './@modules/shared/shared.module';
import { Utils } from './@shared/services/utils';
import { EventEmitterService } from './@shared/services/event-emitter.service';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { NgxLoggerLevel, LoggerModule } from 'ngx-logger';
import { AuthModule, OidcSecurityService } from 'angular-auth-oidc-client';
import { AuthService } from './core/auth/auth.service';
import { AuthGuard } from './core/auth/auth.guard';
import { AuthInterceptor } from './core/auth/auth.interceptor';
import { CoreModule } from 'src/app/core/core.module';
import {MultiSelectModule} from 'primeng/multiselect';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RequiredDocsService } from './@shared/services/required-docs.service';
import { SearchRowQueryComponent } from './@shared/components/search-row-query/search-row-query.component';
import { CalendarModule } from 'primeng/calendar';
import { FormsModule } from '@angular/forms';
@NgModule({
  declarations: [
    AppComponent,
    // BackwardForwardActionsComponent,
    // StepperButtonsComponent,
    //PendingChangesPopupComponent,
    //CustomModalPopupComponent,
    //AnalystSubHeaderComponent
    ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    DocumentsModule,
    CoreModule,
    MultiSelectModule,
    SharedModule,
    NgbModule,
    LoggerModule.forRoot({
      serverLoggingUrl: '/api/logs',
      level: NgxLoggerLevel.DEBUG,
      serverLogLevel: NgxLoggerLevel.ERROR
    }),

    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    FormsModule,
    CalendarModule
  ],
  providers: [Utils,
    OidcSecurityService,
    AuthService,
    AuthGuard,
    EventEmitterService,
    RequiredDocsService,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi:true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

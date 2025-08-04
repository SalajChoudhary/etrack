import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GridComponentComponent } from 'src/app/@shared/components/grid-component/grid-component.component';
import { HeaderComponent } from 'src/app/@shared/components/header/header.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TableModule } from 'primeng/table';
import { HttpClient } from '@angular/common/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { DocumentService } from 'src/app/@shared/services/documentService';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CalendarModule } from 'primeng/calendar';
import {
  
  NgxLoadingModule,
} from 'ngx-loading';
import { MultiSelectModule } from 'primeng/multiselect';
import { MatStepperModule } from '@angular/material/stepper';
import { AddressComponent } from 'src/app/@shared/components/address/address.component';
import { ContactDetailsComponent } from 'src/app/@shared/components/contact-details/contact-details.component';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { ApplyForPermitComponent } from '../documents/components/apply-for-permit/apply-for-permit.component';
import { AnalystSubHeaderComponent } from 'src/app/@shared/components/analyst-sub-header/analyst-sub-header.component';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { MatDividerModule } from '@angular/material/divider';
import { IConfig, NgxMaskModule } from 'ngx-mask';
import { SuccessPopupComponent } from 'src/app/@shared/components/success-popup/success-popup.component';
import { WarningPopUpComponent } from 'src/app/@shared/components/warning-pop-up/warning-pop-up.component';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { TextareaChildComponent } from 'src/app/@shared/components/textarea-child/textarea-child.component';
import { StepperButtonsComponent } from 'src/app/@shared/components/stepper-buttons/stepper-buttons.component';
import { BackwardForwardActionsComponent } from 'src/app/@shared/components/backward-forward-actions/backward-forward-actions.component';
import { DeleteConfirmPopupComponent } from '../../@shared/components/delete-confirm-popup/delete-confirm-popup.component';
import { ValidateBannerComponent } from '../../@shared/components/validate-banner/validate-banner.component';
import { ValidateCheckboxComponent } from '../../@shared/components/validate-checkbox/validate-checkbox.component';
import { ViewDocumentPopupComponent } from '../../@shared/components/view-document-popup/view-document-popup.component';
import { DashboardTableComponent } from '../../@shared/components/dashboard-table/dashboard-table.component';
import { StepperWarningModalComponent } from '../../@shared/components/stepper-warning-modal/stepper-warning-modal.component';
import { RouterModule } from '@angular/router';
import { ExportToExcelComponent } from '../../@shared/components/export-to-excel/export-to-excel.component';
import { AlertPopupComponent } from '../../@shared/components/alert-popup/alert-popup.component';
import { EmailComposeComponent } from 'src/app/@shared/components/email-compose/email-compose.component';
import { MailNotificationsComponent } from 'src/app/@shared/components/mail-notifications/mail-notifications.component';
import { DeleteEmailNotificationComponent } from 'src/app/@shared/components/delete-email-notification/delete-email-notification.component';
import { MailNotificationTabComponent } from 'src/app/@shared/components/mail-notification-tab/mail-notification-tab.component';
import { AccordionChildComponent } from 'src/app/@shared/components/accordion-child/accordion-child.component';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatTabsModule } from '@angular/material/tabs'; 
import { MatListModule } from '@angular/material/list';
import { ProgramReviewCorrespondenceComponent } from 'src/app/@shared/components/program-review-correspondence/program-review-correspondence.component';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { ServerErrorMessageComponent } from '../../@shared/components/server-error-message/server-error-message.component';
import { DimsrConfirmationPopupComponent } from '../../@shared/components/dimsr-confirmation-popup/dimsr-confirmation-popup.component';

import {MatBadgeModule} from '@angular/material/badge';
import { MatIconModule } from '@angular/material/icon';
import { SearchableDropdownComponent } from 'src/app/@shared/components/searchable-dropdown/searchable-dropdown.component';
import { InactiveSessionPopupComponent } from 'src/app/@shared/components/inactive-session-popup/inactive-session-popup.component';
import { SearchRowQueryComponent } from '../../@shared/components/search-row-query/search-row-query.component';
import { KeywordCategoryGridComponent } from '../../@shared/components/keyword-category-grid/keyword-category-grid.component';
import { KeywordOtherGridComponent } from '../../@shared/components/keyword-other-grid/keyword-other-grid.component';

// AoT requires an exported function for factories
export function HttpLoaderFactory(httpClient: HttpClient) {
  return new TranslateHttpLoader(httpClient);
}

const maskConfig: Partial<IConfig> = {
  validation: false,
};

@NgModule({
  declarations: [
    GridComponentComponent,
    ApplyForPermitComponent,
    TextareaChildComponent,
    ServerErrorMessageComponent,
    ContactDetailsComponent,
    HeaderComponent,
    CustomModalPopupComponent,
    AddressComponent,
    DashboardTableComponent,
    AnalystSubHeaderComponent,
    ProgramReviewCorrespondenceComponent,
    PendingChangesPopupComponent,
    WarningPopUpComponent,
    SuccessPopupComponent,
    StepperButtonsComponent,
    BackwardForwardActionsComponent,
    DeleteConfirmPopupComponent,
    ValidateBannerComponent,
    ValidateCheckboxComponent,
    ViewDocumentPopupComponent,
    StepperWarningModalComponent,
    ExportToExcelComponent,
    AlertPopupComponent,
    EmailComposeComponent,
    MailNotificationsComponent,
    DeleteEmailNotificationComponent,
    MailNotificationTabComponent,
    AccordionChildComponent,
    DimsrConfirmationPopupComponent,
    SearchableDropdownComponent,
    InactiveSessionPopupComponent,
    SearchRowQueryComponent,
    KeywordCategoryGridComponent,
    KeywordOtherGridComponent  
  ],
  imports: [
    CommonModule,
    RouterModule,
    TableModule,
    NgbModule,
    CalendarModule,
    FormsModule,
    ReactiveFormsModule,
    TranslateModule,
    MatStepperModule,
    MatDividerModule,
    FormsModule,
    MultiSelectModule,
    NgxLoadingModule.forRoot({}),
    NgxMaskModule.forRoot(maskConfig),
    MatExpansionModule,
    MatTabsModule,
    MatListModule,
    MatBadgeModule,
    MatIconModule
  ],
  providers: [DocumentService, ProjectService, ErrorService],
  exports: [
    GridComponentComponent,
    DashboardTableComponent,
    TextareaChildComponent,
    ApplyForPermitComponent,
    ProgramReviewCorrespondenceComponent,
    AddressComponent,
    ContactDetailsComponent,
    HeaderComponent,
    AnalystSubHeaderComponent,
    StepperButtonsComponent,
    BackwardForwardActionsComponent,
    DeleteConfirmPopupComponent,
    CustomModalPopupComponent,
    PendingChangesPopupComponent,
    WarningPopUpComponent,
    SuccessPopupComponent,
    DimsrConfirmationPopupComponent,
    MatStepperModule,
    MatDividerModule,
    NgxLoadingModule,
    CalendarModule,
    NgbModule,
    TableModule,
    TranslateModule,
    ValidateBannerComponent,
    ValidateCheckboxComponent,
    ViewDocumentPopupComponent,
    StepperWarningModalComponent,
    MailNotificationTabComponent,
    AccordionChildComponent,
    ExportToExcelComponent,
    EmailComposeComponent,
    DeleteEmailNotificationComponent,
    ServerErrorMessageComponent,
    SearchableDropdownComponent,
    InactiveSessionPopupComponent,
    SearchRowQueryComponent,
    KeywordCategoryGridComponent,
    KeywordOtherGridComponent
  ],
})
export class SharedModule {}

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { DocumentsComponent } from './components/documents/documents.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { NgxFileDropModule } from 'ngx-file-drop';
import { NgxMaskModule, IConfig } from 'ngx-mask';
import { MatExpansionModule } from '@angular/material/expansion';
import {MatRadioModule} from '@angular/material/radio';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { WindowRef } from 'src/app/@shared/services/windowRef';
import { ApplyForPermitComponent } from './components/apply-for-permit/apply-for-permit.component';
import { ApplyForPermitDetailsComponent } from './components/apply-for-permit-details/apply-for-permit-details.component';
import { ProjectLocationComponent } from './components/project-location/project-location.component';
import { ProjectLocationDetailsComponent } from './components/project-location-details/project-location-details.component';
import { ApplicantInformationsComponent } from './components/applicant-informations/applicant-informations.component';
import { SupportingDocumentationComponent } from './components/supporting-documentation/supporting-documentation.component';
import { SignAndSubmitComponent } from './components/sign-and-submit/sign-and-submit.component';
import { ApplicantInformationsSearchComponent } from './components/applicant-informations-search/applicant-informations-search.component';
import { ApplicantSelectionComponent } from './components/applicant-selection/applicant-selection.component';
import { GisMapComponent } from './components/gis-map/gis-map.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { AssociatedApplicantsComponent } from './components/associated-applicants/associated-applicants.component';
import { ProjectInformationsComponent } from './components/project-informations/project-informations.component';
import { GisMapViewComponent } from './components/gis-map-view/gis-map-view.component';
import { GisMapEditorComponent } from './components/gis-map-editor/gis-map-editor.component';
import { PermitSelectionComponent } from './components/permit-selection/permit-selection.component';
import { VirtualDesktopComponent } from './components/virtual-desktop/virtual-desktop.component';
import { ProjectNotesComponent } from './components/project-notes/project-notes.component';
import { MultiSelectModule } from 'primeng/multiselect';
import { MatIconModule } from '@angular/material/icon';
import { ProjectInvoiceComponent } from './components/project-invoice/project-invoice.component';
import { ApplicationContactsComponent } from './components/application-contacts/application-contacts.component';
import { TruncatePipe } from '../../@shared/pipes/truncate';
import { PermitSelectionSummaryComponent } from './components/permit-selection-summary/permit-selection-summary.component';
import { PermitSelectionPopupComponent } from './components/permit-selection-popup/permit-selection-popup.component';
import { DashboardApplicantResponseComponent } from './components/dashboard/dashboard-applicant-response/dashboard-applicant-response.component';
import { DashboardResumeEntryComponent } from './components/dashboard/dashboard-resume-entry/dashboard-resume-entry.component';
import { DashboardValidateDataComponent } from './components/dashboard/dashboard-validate-data/dashboard-validate-data.component';
import { DocumentUploadComponent } from './components/document-upload/document-upload.component';
import { AlreadyUploadedComponent } from './components/already-uploaded/already-uploaded.component';
import { SideblockComponent } from './components/virtual-desktop/sideblock/sideblock.component';
import { VirtualDesktopGridComponent } from './components/virtual-desktop/virtual-desktop-grid/virtual-desktop-grid.component';
import { VirtualDesktopTableDocumentsComponent } from './components/virtual-desktop/virtual-desktop-table-documents/virtual-desktop-table-documents.component';
import { VirtualDesktopTableProgramReviewComponent } from './components/virtual-desktop/virtual-desktop-table-program-review/virtual-desktop-table-program-review.component';
import { VirtualDesktopTableFeesNInvoiceComponent } from './components/virtual-desktop/virtual-desktop-table-fees-n-invoice/virtual-desktop-table-fees-n-invoice.component';
import { PhonePipe } from './pipes/phone.pipe';
import { RegionalProjectComponent } from './components/regional-project/regional-project.component';
import { EditSystemGenNotesComponent } from './components/virtual-desktop/edit-system-gen-notes/edit-system-gen-notes.component';
import { AddReviewRequestsComponent } from './components/virtual-desktop/add-review-requests/add-review-requests.component';
import { ProjectMapViewComponent } from './components/project-map-view/project-map-view.component';
import { VirtualDesktopFoilComponent } from './components/virtual-desktop/sideblock/foil/virtual-desktop-foil/virtual-desktop-foil.component';
import { VirtualDesktopFoilTableComponent } from './components/virtual-desktop/sideblock/foil/virtual-desktop-foil/virtual-desktop-foil-table/virtual-desktop-foil-table.component';
import { LitigationHoldComponent } from './components/virtual-desktop/sideblock/litigation-hold/virtual-desktop-litigation-hold/litigation-hold.component';
import { VirtualDesktopLitigationHoldConfirmModalComponent } from './components/virtual-desktop/sideblock/litigation-hold/virtual-desktop-litigation-hold-confirm-modal/virtual-desktop-litigation-hold-confirm-modal.component';
import { VirtualDesktopLitigationHoldBannerComponent } from './components/virtual-desktop/sideblock/litigation-hold/virtual-desktop-litigation-hold-banner/virtual-desktop-litigation-hold-banner.component';
import { DocumentsLitigationBannerComponent } from './components/documents/documents-litigation-banner/documents-litigation-banner.component';
import { DimsrComponent } from './components/dimsr/dimsr.component';
import { KeywordMaintenanceComponent } from './components/keyword-maintenance/keyword-maintenance.component';
import { PermitSelectionSummaryModifyPopupComponentComponent } from './components/permit-selection-summary/permit-selection-summary-modify-popup-component/permit-selection-summary-modify-popup-component.component';
import { PermitSelectionSummaryCommonPopupComponentComponent } from './components/permit-selection-summary/permit-selection-summary-common-popup-component/permit-selection-summary-common-popup-component.component';
import { ApplnPermitDescModalComponent } from './components/permit-selection-summary/appln-permit-desc-modal/appln-permit-desc-modal.component';
import { MilestonesComponent } from './components/virtual-desktop/sideblock/milestones/milestones.component';
import { AddNewApplicationPermitsComponent } from './components/virtual-desktop/add-new-application-permits/add-new-application-permits.component';
import { NewApplicationPermitModalComponent } from './components/virtual-desktop/add-new-application-permits/new-application-permit-modal/new-application-permit-modal.component';
import { LitigationHoldTableComponent } from './components/virtual-desktop/sideblock/litigation-hold/virtual-desktop-litigation-hold/litigation-hold-table/litigation-hold-table.component';
import { VirtualDesktopTableApplicantCommunicationsComponent } from './components/virtual-desktop/virtual-desktop-table-applicant-communications/virtual-desktop-table-applicant-communications.component';
import { AddApplicantCommunicationsComponent } from './components/virtual-desktop/add-applicant-communications/add-applicant-communications.component';
import { SearchComponent } from './components/search/search.component';
import { ApplyForInquiryComponent } from './components/apply-for-inquiry/apply-for-inquiry.component';
import { GeographicInquiryComponent } from './components/geographic-inquiry/geographic-inquiry.component';
import { InquiryDocumentationComponent } from './components/inquiry-documentation/inquiry-documentation.component';
import { GisMapSiComponent } from './components/gis-map-si/gis-map-si.component';
import { KeywordsTextGridComponent } from './components/virtual-desktop/keywords-text-grid/keywords-text-grid.component';
import { MaintenanceDashboardComponent } from './components/maintenance-dashboard/maintenance-dashboard.component';
import { SearchTableComponent } from './components/search/search-table/search-table.component';
import { ReportsDashboardComponent } from './components/reports-dashboard/reports-dashboard.component';
import { SubmittalReportComponent } from './components/submittal-report/submittal-report.component';
import { GiResponseComponent } from './components/virtual-desktop/gi-response/gi-response.component';
import { DocumentMaintenanceComponent } from './components/document-maintenance/document-maintenance.component';
import { CandidateKeywordsReportComponent } from './components/candidate-keywords-report/candidate-keywords-report.component';
import { CandidateKeyReplacementComponent } from './components/candidate-key-replacement/candidate-key-replacement.component';
import { PurgeArchiveComponent } from './components/purge-archive/purge-archive.component';
import { MaintenancePermitTypeComponent } from './components/maintenance-permit-type/maintenance-permit-type.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full',
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
  },
  {
    path: 'dimsr',
    component: DimsrComponent,
  },
  {
    path:'keyword-maintenance',
    component: KeywordMaintenanceComponent,
  },
  {
    path:'maintenance-dashboard',
    component: MaintenanceDashboardComponent,
  },
  {
    path:'reports-dashboard',
    component: ReportsDashboardComponent,
  },
  {
    path: 'documents',
    component: DocumentsComponent,
  },
  {
    path: 'apply-for-permit-details',
    component: ApplyForPermitDetailsComponent,
  },
  {
    path: 'project-location',
    component: ProjectLocationComponent,
  },
  {
    path: 'project-location-details',
    component: ProjectLocationDetailsComponent,
  },
  {
    path: 'applicant-informations',
    component: ApplicantInformationsComponent,
  },
  {
    path: 'applicant-informations-search',
    component: ApplicantInformationsSearchComponent,
  },
  {
    path: 'supporting-documentation',
    component: SupportingDocumentationComponent,
  },
  {
    path: 'applicant-selection',
    component: ApplicantSelectionComponent,
  },
  {
    path: 'associated-applicants',
    component: AssociatedApplicantsComponent,
  },
  {
    path: 'sign-and-submit',
    component: SignAndSubmitComponent,
  },
  {
    path: 'project-informations',
    component: ProjectInformationsComponent,
  },
  {
    path: 'virtual-workspace/:projectId',
    component: VirtualDesktopComponent,
  },
  {
    path: 'gi-virtual-workspace/:inquiryId',
    component: VirtualDesktopComponent,
  },
  {
    path: 'project-invoice',
    component: ProjectInvoiceComponent,
  },
  {
    path: 'permit-selection-summary',
    component: PermitSelectionSummaryComponent,
  },
  {
    path: 'supporing-documentation',
    component: SupportingDocumentationComponent,
  },
  {
    path: 'supporting-documentation/view',
    component: SupportingDocumentationComponent
},
  {
    path: 'sign-submit',
    component: SignAndSubmitComponent,
  },
  {
    path: 'document-upload',
    component: DocumentUploadComponent,
  },
  {
    path: 'already-uploaded',
    component: AlreadyUploadedComponent,
  },
  {
    path: 'regional-project',
    component: RegionalProjectComponent,
  },
  {
    path: 'project-map',
    component: ProjectMapViewComponent,
  },
  {
    path: 'search',
    component: SearchComponent,
  },
  {
    path: 'apply-for-inquiry',
    component: ApplyForInquiryComponent,
  },
  {
    path: 'geographic-inquiry',
    component: GeographicInquiryComponent,
  },
  {
    path: 'inquiry-documentation',
    component: InquiryDocumentationComponent,
  },
];

const maskConfig: Partial<IConfig> = {
  validation: false,
};
@NgModule({ 
  declarations: [
    DocumentsComponent,
    DashboardComponent,
    ApplyForPermitDetailsComponent,
    DimsrComponent,
    KeywordMaintenanceComponent,
    ProjectLocationComponent,
    ProjectLocationDetailsComponent,
    ApplicantInformationsComponent,
    SupportingDocumentationComponent,
    SignAndSubmitComponent,
    ApplicantInformationsSearchComponent,
    ApplicantSelectionComponent,
    GisMapComponent,
    PermitSelectionSummaryCommonPopupComponentComponent,
    AssociatedApplicantsComponent,
    ProjectInformationsComponent,
    GisMapViewComponent,
	  GisMapEditorComponent,
    PermitSelectionComponent,
    VirtualDesktopComponent,
    ProjectNotesComponent,
    ProjectInvoiceComponent,
    ApplicationContactsComponent,
    TruncatePipe,
    PermitSelectionSummaryComponent,
    PermitSelectionPopupComponent,

    DashboardApplicantResponseComponent,
    DashboardResumeEntryComponent,
    DashboardValidateDataComponent,
    DocumentUploadComponent,
    AlreadyUploadedComponent,
    SideblockComponent,
    VirtualDesktopGridComponent,
    VirtualDesktopTableDocumentsComponent,
    VirtualDesktopTableProgramReviewComponent,
    VirtualDesktopTableFeesNInvoiceComponent,
    PhonePipe,
    RegionalProjectComponent,
    EditSystemGenNotesComponent,
    AddReviewRequestsComponent,
	ProjectMapViewComponent,
 VirtualDesktopFoilComponent,
 VirtualDesktopFoilTableComponent,
 LitigationHoldComponent,
 VirtualDesktopLitigationHoldConfirmModalComponent,
 VirtualDesktopLitigationHoldBannerComponent,
 DocumentsLitigationBannerComponent,
 PermitSelectionSummaryModifyPopupComponentComponent,
 PermitSelectionSummaryCommonPopupComponentComponent,
 ApplnPermitDescModalComponent,
 MilestonesComponent,
 AddNewApplicationPermitsComponent,
 NewApplicationPermitModalComponent,
 LitigationHoldTableComponent,
 VirtualDesktopTableApplicantCommunicationsComponent,
 AddApplicantCommunicationsComponent,
 SearchComponent,
 GisMapSiComponent,
 ApplyForInquiryComponent,
 GeographicInquiryComponent,
 InquiryDocumentationComponent,
 KeywordsTextGridComponent,
 MaintenanceDashboardComponent,
 SearchTableComponent,
 ReportsDashboardComponent,
 SubmittalReportComponent,
 GiResponseComponent,
 DocumentMaintenanceComponent,
 CandidateKeywordsReportComponent,
 CandidateKeyReplacementComponent,
 PurgeArchiveComponent,
 MaintenancePermitTypeComponent,
  ],
  providers: [WindowRef],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedModule,
    NgxFileDropModule,
    MatProgressSpinnerModule,
    MultiSelectModule,
    MatExpansionModule,
    MatRadioModule,
    MatAutocompleteModule,
    RouterModule.forChild(routes),
    NgxMaskModule.forRoot(maskConfig),
    MatIconModule,

  ],
  entryComponents: [VirtualDesktopLitigationHoldConfirmModalComponent,
    PermitSelectionSummaryModifyPopupComponentComponent, ApplnPermitDescModalComponent],
  exports: [
    CandidateKeyReplacementComponent,
    PurgeArchiveComponent,
    MaintenancePermitTypeComponent
  ]
})
export class DocumentsModule {}

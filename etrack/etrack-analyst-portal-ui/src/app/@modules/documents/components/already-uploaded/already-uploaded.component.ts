import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { DocumentService } from 'src/app/@shared/services/documentService';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { ErrorService } from 'src/app/@shared/services/errorService';

@Component({
  selector: 'app-already-uploaded',
  templateUrl: './already-uploaded.component.html',
  styleUrls: ['./already-uploaded.component.scss'],
})
export class AlreadyUploadedComponent implements OnInit {
  @ViewChild('pendingPopup', { static: true })
  pendingPopup!: PendingChangesPopupComponent;

  uploadedForm!: UntypedFormGroup;
  formSubmitted: boolean = false;
  displayName: any;
  refTypes: any = [];
  errorMessages: any = {};
  docId: any;
  pageFrom: string | null;
  modalReference: any;
  documentTitleId: any;
  showServerError: boolean = false;
  serverErrorMessage!: string;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private commonService: CommonService,
    private projectService: ProjectService,
    private docService: DocumentService,
    private route: ActivatedRoute,
    private router: Router,
    private errorService: ErrorService
  ) {
    this.displayName = this.route.snapshot.queryParamMap.get('displayName');
    this.pageFrom = this.route.snapshot.queryParamMap.get('page');
    if (this.pageFrom === 'step4') {
      this.docId = this.route.snapshot.queryParamMap.get('docId');
      this.documentTitleId =
        this.route.snapshot.queryParamMap.get('documentTitleId');
    } else {
      this.getUploadDetails();
    }
    this.commonService.removeGreenBackground();
    this.initiateForm();
  }

  async openConfirmModal() {
    if (this.uploadedForm.dirty) {
      this.modalReference = await this.pendingPopup.open();
    } else {
      this.goBack();
    }
  }

  initiateForm() {
    this.uploadedForm = this.formBuilder.group({
      refLocation: ['', Validators.required],
      description: ['', Validators.required],
    });
  }
  getFormData() {
    this.projectService.getAlreadyUploadedData().then((response) => {
      response?.sort(
        (a: any, b: any) => {
          if (a.displayName.toLowerCase() < b.displayName.toLowerCase())
            return -1;
          if (a.displayName.toLowerCase() > b.displayName.toLowerCase())
            return 1;
          return 0;
        },
        (error: any) => {
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      );
      this.refTypes = response;
    });
  }
  ngOnInit(): void {
    this.getFormData();
    this.commonService.emitErrorMessages.subscribe((val) => {
      if (val) this.errorMessages = this.commonService.getErrorMsgsObj();
    });
  }

  getUploadDetails() {
    this.docService.getUploadDocDetails().then(
      (response: any[]) => {
        this.docId = response.filter(
          (item: any) => item.displayName === 'Signature Authorization'
        )[0]?.supportDocRefId;
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }
  getApiData() {
    const { displayName, supportDocRefId, documentId } =
      this.uploadedForm.value?.refLocation;

    let apiData = {
      documentId: documentId,
      refDisplayName: displayName,
      documentTitleId: this.documentTitleId,
      displayName: this.displayName,
      supportDocRefId: this.docId,
      referenceText: this.uploadedForm.get('description')?.value,
    };

    return apiData;
  }
  onFormSubmit() {
    this.formSubmitted = true;
    this.showServerError = false;
    if (this.uploadedForm.valid) {
      let apiData = this.getApiData();
      this.docService.alreadyUploadedSubmit(apiData).then(
        (response) => {
          this.goBack();
        },
        (error: any) => {
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      );
    }
  }
  goBack() {
    this.router.navigate([
      this.pageFrom === 'step4' ? 'supporting-documentation' : 'sign-submit',
    ]);
  }

  ngOnDestroy() {
    this.commonService.addGreenBackground();
  }

  //This returns a mock 500 server error
  getError() {
    this.commonService.getData().subscribe(
      (data) => {},
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }
}

import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnInit,
  ViewChild,
} from '@angular/core';
import {
  FormControl,
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { KeywordMaintainService } from 'src/app/@shared/services/keyword-maintain.service';
import { SearchReportService } from 'src/app/@shared/services/search-report.service';

@Component({
  selector: 'app-candidate-key-replacement',
  templateUrl: './candidate-key-replacement.component.html',
  styleUrls: ['./candidate-key-replacement.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CandidateKeyReplacementComponent implements OnInit {
  candidateKeywordsList: any[] = [];
  otherKeywordItems: any[] = [];
  checkBoxKey: string = 'selected';
  modalReference: any;
  addCategoryKeywordConfig!: { title: string; showHeader: boolean };
  @ViewChild('addCategoryKeywordPopup')
  private addCategoryKeywordPopup!: CustomModalPopupComponent;
  categoryKeyWordForm!: UntypedFormGroup;
  addPermitClicked: boolean = false;
  serverErrorMessage: any;
  showServerError: boolean = false;
  keywordDropdownList: any;
  showCategoryServerError: boolean = false;
  errorMsgObj: any;
  categoryList: any[] = [];
  selectedKeywordList: any[] = [];
  selectedKeywordId: any[] = [];
  replaceClicked: boolean = false;
  replaceButton: boolean = false

  noKeywordSelected: boolean = false;
  constructor(
    private searchReport: SearchReportService,
    private formBuilder: UntypedFormBuilder,
    private keywordMaintenanceService: KeywordMaintainService,
    private errorService: ErrorService,
    private modalService: NgbModal,
    private commonService: CommonService,
    private cdr: ChangeDetectorRef
  ) {
    this.assignForm();
  }

  ngOnInit(): void {
    this.candidateKeywordData();
    this.keywordCategoryDropdownList();
    this.keywordData();
    this.getAllErrorMsgs();

    this.addCategoryKeywordConfig = {
      title: '',
      showHeader: false,
    };
    this.categoryKeyWordForm.controls['keywordId'].valueChanges.subscribe(
      (res: any) => {
        console.log(res);
        if (res) {
          let index = this.categoryList.findIndex(
            (e: any) => e.keywordId == res
          );
          if (index != -1)
            this.categoryKeyWordForm.controls['keywordCategoryId'].setValue(
              this.categoryList[index].keywordCategoryId
            );
        } else {
          this.categoryKeyWordForm.controls['keywordCategoryId'].setValue('');
        }
      }
    );
  }

  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val) => {
      if (val) this.errorMsgObj = this.commonService.getErrorMsgsObj();
    });
  }

  candidateKeywordData() {
    this.searchReport.permitKeyWordData().then((res: any) => {
      this.candidateKeywordsList = res || [];
      this.cdr.detectChanges();
    });
  }

  keywordData() {
    this.keywordMaintenanceService
      .loadKeywordData()
      .then((res: any) => {
        if (res) {
          console.log('Keyword table', res);

          let categories: any[] = [];
          Object.keys(res).forEach((e: any) => {
            res[e][0].categoryText = e;
            categories = categories.concat(res[e]);
          });
          this.categoryList = categories;
        }
      })
      .catch((ex) => {
        this.serverErrorMessage = this.errorService.getServerMessage(ex);
        this.showServerError = true;
        throw ex;
      });
  }

  keywordCategoryDropdownList(setValue?: any): void {
    this.keywordMaintenanceService
      .loadKeywordDropDownList()
      .then((response: any) => {
        this.keywordDropdownList = response;

        if (setValue) {
          // keywordCategory
          let index = this.keywordDropdownList.findIndex(
            (e: any) => e.keywordCategory == setValue.keywordCategory
          );
          if (index != -1)
            this.categoryKeyWordForm.controls['searchItemText'].setValue(
              this.keywordDropdownList[index].keywordCategoryId
            );
        }
      })
      .catch((ex) => {
        this.serverErrorMessage = this.errorService.getServerMessage(ex);
        this.showCategoryServerError = true;
        throw ex;
      });
  }

  emitChanges(ev: any) {
    console.log('Test', ev);
  }

  replaceCategoryKeyword() {
    this.replaceClicked = true;

    if (this.categoryKeyWordForm.controls['keywordId'].valid) {
      let apiData: [] = JSON.parse(
        JSON.stringify(this.categoryKeyWordForm.controls['keywordId'].value)
      );
      let replacedKeywordId = this.selectedKeywordId;
      this.searchReport
        .replaceCandidateKeyword(apiData, replacedKeywordId)
        .subscribe(
          (response: any) => {
            console.log('Replace Category', response);
            // handle success and reload grid data
            this.modalService.dismissAll();
            this.replaceClicked = false;
            this.candidateKeywordData();
            this.assignForm();
          },
          (error: any) => {
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          }
        );
    } else {
      return;
    }
  }

  closeModal(ev: any) {
    this.modalService.dismissAll();
    this.candidateKeywordData();
    this.showServerError = false;
    this.replaceClicked = false;
    this.assignForm();
  }

  assignForm() {
    this.categoryKeyWordForm = this.formBuilder.group({
      permitTypeCode: new FormControl('', [Validators.required]),
      keywordId: new FormControl('', [Validators.required]),
      keywordCategoryId: new FormControl({ disabled: true, value: '' }, [
        Validators.required,
      ]),
      permitKeywordId: new FormControl(''),
    });
  }

  makeList() {
    this.selectedKeywordList = this.candidateKeywordsList
      .filter((e: any) => e[this.checkBoxKey] == 1)
      .map((e: any) => e.keywordText);
    this.selectedKeywordId = this.candidateKeywordsList
      .filter((e: any) => e[this.checkBoxKey] == 1)
      .map((e: any) => e.keywordId);
    if (this.selectedKeywordList.length>0) {
      this.noKeywordSelected = true;
    }
    console.log('C List', this.selectedKeywordId, this.selectedKeywordId);
  }

  replace() {
    this.replaceButton=true;
    this.makeList();
    console.log('no sel', this.checkBoxKey);
    console.log('no sel', this.selectedKeywordId .length);
    console.log('no sel', this.noKeywordSelected);
    if (this.noKeywordSelected == false) {
      return;
    } else {
      console.log('no sel', this.noKeywordSelected);
      this.modalReference = this.addCategoryKeywordPopup.open('lag');
    }
  }
}

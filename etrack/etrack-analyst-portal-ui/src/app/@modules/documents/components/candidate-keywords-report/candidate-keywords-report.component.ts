import { Component, OnInit } from '@angular/core';
import FileSaver from 'file-saver';
import { SearchReportService } from 'src/app/@shared/services/search-report.service';

@Component({
  selector: 'app-candidate-keywords-report',
  templateUrl: './candidate-keywords-report.component.html',
  styleUrls: ['./candidate-keywords-report.component.scss']
})
export class CandidateKeywordsReportComponent implements OnInit {

  candidateKeywordsList:any[]=[];
  constructor(private searchReport: SearchReportService) { }

  ngOnInit(): void {
    this.candidateKeywordReportData();
  }

  candidateKeywordReportData() {
    this.searchReport.loadCandidateKeywordReport().then((res: any) => {
      res.forEach((ele: any) => {
        Object.keys(ele.regions).forEach((e: any) => {
          ele['region'+e] = ele.regions[e] ? ele.regions[e] : 0;
        })
      });
      this.candidateKeywordsList = res || [];
      console.log(res);
    })
  }

  candidateReportExportToExcel(){
    let dataList: any[] = [];

    const headers = ['keywordText','TotalUsage','Region 0', 'Region 1','Region 2', 'Region 3','Region 4','Region 5',
    'Region 6','Region 7', 'Region 8','Region 9'];
    const mapObj: any = {
      'keywordText': 'keyword',
      'TotalUsage': 'totalUsage',
      'Region 0': 'region0',
      'Region 1': 'region1',
      'Region 2': 'region2',
      'Region 3': 'region3',
      'Region 4': 'region4',
      'Region 5': 'region5',
      'Region 6': 'region6',
      'Region 7': 'region7',
      'Region 8': 'region8',
      'Region 9': 'region9',
    }
      dataList = this.candidateKeywordsList.map((data) => {
        let temp: any = {};
        headers.forEach((header) => {
          temp[header] = data[mapObj[header]];
        });
        return temp;
      });
    
    import('xlsx').then((xlsx) => {
      const worksheet = xlsx.utils.json_to_sheet(dataList);
      const workbook = { Sheets: { data: worksheet }, SheetNames: ['data'] };
      const excelBuffer: any = xlsx.write(workbook, {
        bookType: 'xlsx',
        type: 'array',
      });
      this.saveAsExcelFile(excelBuffer, 'candidateReport');
    });

  }

  saveAsExcelFile(buffer: any, fileName: string): void {
    let EXCEL_TYPE =
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
    let EXCEL_EXTENSION = '.xlsx';
    const data: Blob = new Blob([buffer], {
      type: EXCEL_TYPE,
    });
    FileSaver.saveAs(
      data,
      fileName + '_export_' + new Date().getTime() + EXCEL_EXTENSION
    );
  }

}

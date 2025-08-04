import { Component, Input, OnInit, ViewChild } from '@angular/core';
import FileSaver from 'file-saver';
import { SortEvent } from 'primeng/api';
import { Table } from 'primeng/table';
import { PTableHeader } from '../../../../../@shared/components/dashboard-table/table.model';

@Component({
  selector: 'app-search-table',
  templateUrl: './search-table.component.html',
  styleUrls: ['./search-table.component.scss']
})
export class SearchTableComponent implements OnInit {
  @ViewChild('dataTable', { static: true }) dataTable!: Table;
  @Input() headers: PTableHeader[] = [];
  @Input() filename: string = '';
  @Input() totalRecords: number = 0;
  @Input() dataList: any[] = [];
  @Input() activeTab: string = '';
  @Input() isPagination: boolean = false;
  @Input() isRecords: boolean = false;
  @Input() isMaxHeight: boolean = false;
  filterValues: string[] = [];

  constructor() { }

  ngOnInit(): void {
    console.log('here are the headers', this.headers);
    console.log('here is data', this.dataList);
    
    
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


  getFilterList(filterList: any[]) {
    console.log('get filter list', filterList);
    
    if (filterList && filterList.length > 0) {
      return filterList.filter((obj: any) => obj.value && obj.label);
    }
    return [];
  }


  resetFilters() {
    let blankArray: any = [];
    for (let i = 0; i < this.filterValues.length; i++) {
      blankArray.push('');
    }
    this.filterValues = [...blankArray];
    this.dataTable.reset();
  }


  exportToExcel() {
    let filteredVal = this.dataTable.filteredValue;
    let dataList: any[] = [];
    console.log('headers to export', this.headers);
    
    const headers = this.headers
      .filter((header) => header.exportToExcel)
      .map((item) => item.columnTitle);

    if (filteredVal) {
      dataList = filteredVal.map((data) => {
        let temp: any = {};
        headers.forEach((header) => {
          temp[header] = data[header];
        });
        return temp;
      });
    } else {
      dataList = this.dataList.map((data) => {
        let temp: any = {};
        headers.forEach((header) => {
          temp[header] = data[header];
        });
        return temp;
      });
    }
    import('xlsx').then((xlsx) => {
      const worksheet = xlsx.utils.json_to_sheet(dataList);
      const workbook = { Sheets: { data: worksheet }, SheetNames: ['data'] };
      const excelBuffer: any = xlsx.write(workbook, {
        bookType: 'xlsx',
        type: 'array',
      });
      this.saveAsExcelFile(excelBuffer, this.filename);
    });
  }


  customSort(event: SortEvent) {
    console.log(event);
    event.data?.sort((data1, data2) => {
      const dateRegex: RegExp =
        /(0[1-9]|1[012])[- \/.](0[1-9]|[12][0-9]|3[01])[- \/.](19|20)\d\d/;

      let value1 = data1[event.field as string];
      let value2 = data2[event.field as string];
      let result = null;
      if (dateRegex.test(value1) && dateRegex.test(value2)) {
        value1 = new Date(value1);
        value2 = new Date(value2);
      }
      if (value1 == null && value2 != null) result = -1;
      else if (value1 != null && value2 == null) result = 1;
      else if (value1 == null && value2 == null) result = 0;
      else if (typeof value1 === 'string' && typeof value2 === 'string')
        result = value1.localeCompare(value2);
      else result = value1 < value2 ? -1 : value1 > value2 ? 1 : 0;

      return (event.order as number) * result;
    });
  }
}

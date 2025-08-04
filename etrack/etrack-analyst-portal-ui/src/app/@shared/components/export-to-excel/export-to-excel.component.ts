import { Component, OnInit, Input } from '@angular/core';
import FileSaver, { saveAs } from 'file-saver';

@Component({
  selector: 'app-export-to-excel',
  templateUrl: './export-to-excel.component.html',
  styleUrls: ['./export-to-excel.component.scss']
})
export class ExportToExcelComponent implements OnInit {

  @Input() filename:any = 'filename';
  @Input() data:any = [];
  constructor() { }

  ngOnInit(): void {
  }

  exportToExcel(){
    
    import("xlsx").then(xlsx => {
      const worksheet = xlsx.utils.json_to_sheet(this.data);
      const workbook = { Sheets: { data: worksheet }, SheetNames: ["data"] };
      const excelBuffer: any = xlsx.write(workbook, {
        bookType: "xlsx",
        type: "array"
      });
      this.saveAsExcelFile(excelBuffer, this.filename);
    });
  }
  
  saveAsExcelFile(buffer: any, fileName: string): void {

      let EXCEL_TYPE =
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
      let EXCEL_EXTENSION = ".xlsx";
      const data: Blob = new Blob([buffer], {
        type: EXCEL_TYPE
      });
      FileSaver.saveAs(
        data,
        this.filename + "_export_" + new Date().getTime() + EXCEL_EXTENSION
      );
  
  }


}

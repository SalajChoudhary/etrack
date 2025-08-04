import { PTableHeader } from "../../../../@shared/components/dashboard-table/table.model";

export const DocumentResultHeaders: PTableHeader[] = [
    {
      columnLabel: 'Project ID',
      columnTitle: 'projectId',
      isFilter: true,
      isSort: true,
      isLink:true,
      //optionals
      headerWidth: '9%',
      bodyWidth: '9%',
      filtersList: [],
      exportToExcel:true
    },
    {
      columnLabel: 'Document Name',
      columnTitle: 'documentNm',
      isFilter: true,
      isSort: true,
      isLink:true,
      //optionals
      headerWidth: '20%',
      bodyWidth: '20%',
      filtersList: [],
      exportToExcel:true
    },
    {
      columnLabel: 'Document Type',
      columnTitle: 'documentTypeDesc',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '20%',
      bodyWidth: '20%',
      filtersList: [],
      exportToExcel:true
    },
    {
      columnLabel: 'Sub-Type',
      columnTitle: 'documentSubTypeDesc',
      isCheckBox: true,
      isFilter: true,
      isSort: true,
      headerWidth: '15%',
      bodyWidth: '15%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'DEC ID',
      columnTitle: 'decId',
      isFilter: true,
      isSort: true,
      isLink:true,
      headerWidth: '10%',
      bodyWidth: '10%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Facility ',
      columnTitle: 'districtName',
      isFilter: true,
      isSort: true,
      headerWidth: '15%',
      bodyWidth: '15%',
      filtersList: [],
      exportToExcel:true
    },
    {
      columnLabel: 'Address',
      columnTitle: 'facAddress',
      isFilter: true,
      isSort: true,
      headerWidth: '20%',
      bodyWidth: '20%',
      filtersList: [],
      exportToExcel:true
    },
  
    {
      columnLabel: 'Municipality',
      columnTitle: 'primaryMuni',
      isFilter: true,
      isSort: true,
      headerWidth: '10%',
      bodyWidth: '10%',
      filtersList: [],
      exportToExcel:true
    },
    {
      columnLabel: "Recv'd Date" ,
      columnTitle: 'rcvdDate',
      isFilter: true,
      isSort: true,
      isDate: true,
      headerWidth: '10%',
      bodyWidth: '10%',
      filtersList: [],
      exportToExcel:true
    },
    {
      columnLabel: "Permit Types" ,
      columnTitle: 'permitTypeTxnType',
      isFilter: true,
      isSort: true,
      isDate: true,
      headerWidth: '15%',
      bodyWidth: '15%',
      filtersList: [],
      exportToExcel: true
    },
    
    {
      columnLabel: '',
      columnTitle: '',
      isAction: true,
      isExportIcon: true,
      isClearFilterIcon: true,
      headerWidth: '5%',
      bodyWidth: '4%',
      filtersList: [],
      // exportToExcel:true
    },
  ];

  export const ProjectResultHeaders: PTableHeader[] = [
    {
      columnLabel: 'Project ID',
      columnTitle: 'projectId',
      isFilter: true,
      isSort: true,
      isLink:true,
      //optionals
      headerWidth: '7%',
      bodyWidth: '7%',
      filtersList: [],
      exportToExcel:true
    },
    {
      columnLabel: 'Primary LRP',
      columnTitle: 'primaryLrpName',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '20%',
      bodyWidth: '20%',
      filtersList: [],
      exportToExcel:true
    },
    {
      columnLabel: 'DEC ID',
      columnTitle: 'decId',
      isFilter: true,
      isSort: true,
      isLink:true,
      headerWidth: '7%',
      bodyWidth: '7%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Facility ',
      columnTitle: 'districtName',
      isFilter: true,
      isSort: true,
      headerWidth: '17%',
      bodyWidth: '17%',
      filtersList: [],
      exportToExcel:true
    },
    {
      columnLabel: 'Address',
      columnTitle: 'facAddress',
      isFilter: true,
      isSort: true,
      headerWidth: '20%',
      bodyWidth: '20%',
      filtersList: [],
      exportToExcel:true
    },
  
    {
      columnLabel: 'Municipality',
      columnTitle: 'primaryMuni',
      isFilter: true,
      isSort: true,
      headerWidth: '10%',
      bodyWidth: '10%',
      filtersList: [],
      exportToExcel:true
    },
    {
      columnLabel: "Recv'd Date" ,
      columnTitle: 'rcvdDate',
      isFilter: true,
      isSort: true,
      isDate: true,
      headerWidth: '7%',
      bodyWidth: '7%',
      filtersList: [],
      exportToExcel: true
    },
    {
      columnLabel: "Permit Types" ,
      columnTitle: 'permitTypeTxnType',
      isFilter: true,
      isSort: true,
      isDate: true,
      headerWidth: '15%',
      bodyWidth: '15%',
      filtersList: [],
      exportToExcel: true
    },
   
    {
      columnLabel: '',
      columnTitle: '',
      isAction: true,
      isExportIcon: true,
      isClearFilterIcon: true,
      headerWidth: '5%',
      bodyWidth: '4%',
      filtersList: [],
      //exportToExcel:true
    },
  ];

  export const ExistingSearchesHeaders: PTableHeader[] = [
    {
      columnLabel: 'Name',
      columnTitle: 'name',
      isSort: true,
      //optionals
      headerWidth: '5%',
      bodyWidth: '6%',
      filtersList: [],
      exportToExcel:true
    },
    {
      columnLabel: 'User',
      columnTitle: 'userId',
      isSort: true,
      //optionals
      headerWidth: '7%',
      bodyWidth: '9%',
      filtersList: [],
      exportToExcel:true
    },
    {
      columnLabel: 'Docs/Projects',
      columnTitle: 'docsProjects',
      isSort: true,
      headerWidth: '7%',
      bodyWidth: '9%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Last Updated',
      columnTitle: 'lastUpdated',
      isFilter: true,
      isSort: true,
      headerWidth: '8%',
      bodyWidth: '9%',
      filtersList: [],
      exportToExcel:true
    }
  ];
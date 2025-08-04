import { PTableHeader } from "../../../../@shared/components/dashboard-table/table.model";

export const ProgramReviewHeaders:PTableHeader[] = [
  {
    columnLabel: 'Project ID',
    columnTitle: 'projectId',
    isFilter: true,
    isSort: true,
    isLink: true,
    linkToNavigate: '/virtual-workspace',
    isSetLocal: false,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: 'EA',
    columnTitle: 'eaInd',
    isCheckBox: true,
    isFilter: true,
    //optionals
    isSort: true,
    headerWidth: '4%',
    bodyWidth: '4%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'GP',
    columnTitle: 'gpInd',
    isCheckBox: true,
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '4%',
    bodyWidth: '4%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Review Due',
    columnTitle: 'dueDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    isDateRed: true,

    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: 'Assigned Date',
    columnTitle: 'dateAssigned',
    isFilter: true,
    isSort: true,
    isDate: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: 'Program Staff',
    columnTitle: 'programStaff',
    //isLink:true,
    isSetLocal: true,
    isAssign: true,
    isFilter: true,
    isSort: true,
    isDate: false,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel:true
  },
 
  {
    columnLabel: 'DEC ID',
    columnTitle: 'decId',
    //isLink:true,
    isSetLocal: true,
    isAssign: true,
    isFilter: true,
    isSort: true,
    isDate: false,
    //optionals
    headerWidth: '8%',
    bodyWidth: '8%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: 'Facility',
    columnTitle: 'facilityName',
    isFilter: true,
    isSort: true,
    isLink: true,
    linkToNavigate: '',
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: 'County',
    columnTitle: 'county',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: 'Permit Type',
    columnTitle: 'permitType',
    desc:'permitTypeDesc',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    //isLink:true,
    isSetLocal: true,
    isAssign: true,
    isFilter: true,
    isSort: true,
    isDate: false,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: 'Status',
    columnTitle: 'dartStatus',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel:true
  },

  {
    columnLabel: '',
    columnTitle: '',
    isExportIcon: true,
    isClearFilterIcon: true,
    // //isFilter:false,
    // isSort:false,
    // isDate:false,
    // isButton:null,

    //optionals
    headerWidth: '8%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel:true
  },
];

export const GIProgramReviewHeaders:PTableHeader[]= [
  {
    columnLabel: 'GI ID',
    columnTitle: 'inquiryId',
    isFilter: true,
    isSort: true,
    className: 'validate-project-id',
    isLink: true,
    isSetLocal: true,
    //optionals
    headerWidth: '7%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel: true
  },
  {
    columnLabel: 'Inquiry Type',
    columnTitle: 'inquiryTypeDesc',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '13%',
    bodyWidth: '13%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Review Due',
    columnTitle: 'dueDate',
    isFilter: true,
    isSort: true,
    isLink: false,
    isDate: true,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Assigned Date',
    columnTitle: 'dateAssigned',
    isFilter: true,
    isSort: true,
    isLink: false,
    isDate: true,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
  },

  {
    columnLabel: 'Program Staff',
    columnTitle: 'programStaff',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },


  {
    columnLabel: 'Request Identifier',
    columnTitle: 'requestIdentifier',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Requestor',
    columnTitle: 'requestor',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Muni/County',
    columnTitle: 'municicounty',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Date Complete',
    columnTitle: 'completedDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: '',
    columnTitle: '',
    isAction: true,
    isExportIcon: true,
    isClearFilterIcon: true,
    //isDelete: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '7%',
    filtersList: [],
  },
]

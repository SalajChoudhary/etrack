import { PTableHeader } from "src/app/@shared/components/dashboard-table/table.model";
import { environment } from "../../../../../environments/environment";

export const ReviewEntryHeaders :PTableHeader[] = [
  {
    columnLabel: 'Project ID',
    columnTitle: 'projectId',
    isFilter: true,
    isSort: true,
    //optionals
    isLink: true,
    isSetLocal: true,
    linkToNavigate: '/virtual-workspace',
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Applicant',
    columnTitle: 'applicant',
    isFilter: true,
    isSort: true,
    //optionals
    isLink: true,
    linkToNavigate: environment.lrpUrl,
    //linkToNavigate: '/virtual-workspace',
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'DEC ID',
    columnTitle: 'decId',
    isFilter: true,
    isSort: true,
    //optionals

    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Facility',
    columnTitle: 'facilityName',
    isFilter: true,
    isSort: true,
    //optionals
    isLink: true,
    linkToNavigate: '',
    //linkToNavigate: '/virtual-workspace',
    headerWidth: '14%',
    bodyWidth: '14%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'County',
    columnTitle: 'county',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '7%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Municipality',
    columnTitle: 'municipality',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
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
    exportToExcel: true,
  },
  {
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Due Date',
    columnTitle: 'dueDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    isDateRed: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Date Assigned',
    columnTitle: 'dateAssigned',
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
    headerWidth: '7%',
    bodyWidth: '6%',
    filtersList: [],
  },
];
export const ResumeEntryHeaders :PTableHeader[] = [
  {
    columnLabel: '',
    columnTitle: '',
    isFilter: false,
    isSort: false,
    isButton: 'isButton',
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: false,
  },
  {
    columnLabel: 'Project ID',
    columnTitle: 'projectId',
    isFilter: false,
    isSort: true,
    //optionals
    headerWidth: '10%',
    bodyWidth: '11%',
    filtersList: [],
    className: 'resume-project-id',
    exportToExcel: true,
    isRejected:true,
  },
  {
    columnLabel: 'EA',
    columnTitle: 'eaInd',
    isCheckBox: true,

    //optionals
    headerWidth: '4%',
    bodyWidth: '4%',
    filtersList: [],
    exportToExcel: true,
    isRejected:true,
  },
  {
    columnLabel: 'GP',
    columnTitle: 'gpInd',
    isCheckBox: true,
    //optionals
    headerWidth: '4%',
    bodyWidth: '4%',
    filtersList: [],
    exportToExcel: true,
    isRejected:true,
  },
  {
    columnLabel: 'Applicant',
    columnTitle: 'applicant',
    isFilter: false,
    isSort: true,
    //optionals
    headerWidth: '14%',
    bodyWidth: '14%',
    filtersList: [],
    exportToExcel: true,
    isRejected:true
  },
  {
    columnLabel: 'Facility',
    columnTitle: 'facilityName',
    isFilter: false,
    isSort: true,
    //optionals
    headerWidth: '18%',
    bodyWidth: '18%',
    filtersList: [],
    exportToExcel: true,
    isRejected:true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'locationDirections',
    isFilter: false,
    isSort: true,
    //optionals
    headerWidth: '18%',
    bodyWidth: '18%',
    filtersList: [],
    exportToExcel: true,
    isRejected:true,
  },
  {
    columnLabel: 'Municipality',
    columnTitle: 'municipality',
    isFilter: false,
    isSort: true,
    //optionals
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel: true,
    isRejected:true,
  },
  
  {
    columnLabel: "Recv'd Date",
    columnTitle: 'createDate',
    isFilter: false,
    isSort: true,
    isDate: true,
    //optionals
    headerWidth: '14%',
    bodyWidth: '14%',
    filtersList: [],
    exportToExcel: true,
    isRejected:true,
  },
  {
    columnLabel: '',
    columnTitle: '',

    isAction: true,
    isExportIcon: true,
    isDelete: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel: false,
  },
];
export const ApplicationResponseHeaders :PTableHeader[] = [
  {
    columnLabel: 'Project ID',
    columnTitle: 'projectId',
    isFilter: true,
    isSort: true,
    isSetLocal: true,
    isLink: true,
    linkToNavigate: '/virtual-workspace',
    //optionals
    headerWidth: '8%',
    bodyWidth: '8%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'EA',
    columnTitle: 'eaInd',
    isCheckBox: true,
    isFilter: true,

    //optionals
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
    //optionals
    headerWidth: '4%',
    bodyWidth: '4%',
    filtersList: [],
    exportToExcel: true,
  },

  {
    columnLabel: 'Facility',
    columnTitle: 'facilityName',
    isLink: true,
    isSort: true,
    isFilter: true,
    linkToNavigate: '',
    //optionals
    headerWidth: '14%',
    bodyWidth: '14%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    // columnLabel: 'Fac Address',
    columnLabel: 'Address',
    columnTitle: 'locationDirections',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },

  {
    columnLabel: 'Permit Type',
    //  columnLabel: 'Permit Type',
    columnTitle: 'permitType',
    desc:'permitTypeDesc',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '8%',
    bodyWidth: '8%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Tracked ID',
    columnTitle: 'trackedIdFormatted',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  // {
  //   columnLabel: 'Batch ID',
  //   columnTitle: 'batchId',
  //   isFilter: false,
  //   isSort: true,
  //,
  //   //optionals
  //   headerWidth: '8%',
  //   bodyWidth: '8%',
  //   filtersList: [],
  //   exportToExcel: true,
  // },
  {
    columnLabel: 'Status',
    // columnLabel: 'DART Status',
    columnTitle: 'dartStatus',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Due Date',
    columnTitle: 'dueDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    isDateRed: true,
    //optionals
    headerWidth: '8%',
    bodyWidth: '8%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Out for Review',
    columnTitle: 'outForReview',
    isFilter: true,
    isSort: true,
    isDate: false,
    isCheckBox: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
  },

  {
    columnLabel: '',
    columnTitle: '',
    isExportIcon: true,
    isClearFilterIcon: true,
    // isFilter:false,
    // isSort:false,
    // isDate:false,
    // isButton:null,

    //optionals
    headerWidth: '8%',
    bodyWidth: '7%',
    filtersList: [],
  },
];
export const ValidateHeaders :PTableHeader[] = [
  {
    columnLabel: '',
    columnTitle: '',
    isFilter: false,
    isSort: false,
    isButton: 'Validate',
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
  },
  {
    columnLabel: 'Project ID',
    columnTitle: 'projectId',
    isFilter: true,
    isSort: true,
    className: 'validate-project-id',
    //isLink: true,
    // linkToNavigate: '/virtual-workspace',
    isSetLocal: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'EA',
    columnTitle: 'eaInd',
    isCheckBox: true,
    isFilter: true,
    //optionals
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
    //optionals
    headerWidth: '4%',
    bodyWidth: '4%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Applicant',
    columnTitle: 'applicant',
    isFilter: true,
    isSort: true,
    isLink: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Facility',
    columnTitle: 'facilityName',
    isFilter: true,
    isSort: true,
    isLink: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'locationDirections',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Municipality',
    columnTitle: 'municipality',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Permit Type',
    columnTitle: 'permitType',
    desc:'permitTypeDesc',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'App Type',
    columnTitle: 'appType',
    isFilter: true,
    isSort: true,
    isDate: false,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: "Recv'd Date",
    columnTitle: 'rcvdDate',
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
    // isFilter:false,
    // isSort:false,
    // isDate:false,
    // isButton:null,
    isAction: true,
    isReject: true,
    isExportIcon: true,
    isClearFilterIcon: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '7%',
    filtersList: [],
    deleteBodyText : 'Are you sure you want to reject project ',
    secondLineDeleteBodyText :'All uploaded files will also be permanently deleted for this project.'
    

  },
];
export const TaskDueHeaders :PTableHeader[] = [
  {
    columnLabel: 'Project ID',
    columnTitle: 'projectId',
    isFilter: true,
    isSort: true,
    isSetLocal: true,
    isLink: true,
    linkToNavigate: '/virtual-workspace',
    //optionals
    headerWidth: '8%',
    bodyWidth: '8%',
    filtersList: [],
    exportToExcel: true,
  },

  {
    columnLabel: 'EA',
    columnTitle: 'eaInd',
    isCheckBox: true,
    isFilter: true,
    //optionals
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
    //optionals
    headerWidth: '4%',
    bodyWidth: '4%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Facility',
    columnTitle: 'facilityName',
    isLink: true,
    isSort: true,
    isFilter: true,
    linkToNavigate: '',
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    // columnLabel: 'Fac Address',
    columnLabel: 'Address',
    columnTitle: 'locationDirections',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Permit Type',
    //  columnLabel: 'Permit Type',
    columnTitle: 'permitType',
    desc:'permitTypeDesc',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '8%',
    bodyWidth: '8%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Tracked ID',
    columnTitle: 'trackedIdFormatted',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },

  {
    columnLabel: 'Status',
    // columnLabel: 'DART Status',
    columnTitle: 'dartStatus',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Due Date',
    columnTitle: 'dueDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    isDateRed: true,
    //optionals
    headerWidth: '8%',
    bodyWidth: '8%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Out for Review',
    columnTitle: 'outForReview',
    isFilter: true,
    isSort: true,
    isCheckBox: true,
    align:"center",
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
  },

  {
    columnLabel: '',
    columnTitle: '',
    isExportIcon: true,
    isClearFilterIcon: true,

    //optionals
    headerWidth: '8%',
    bodyWidth: '6%',
    filtersList: [],
  },
];
export const AllActiveHeaders :PTableHeader[]= [
  {
    columnLabel: 'Project ID',
    columnTitle: 'projectId',
    isSetLocal: false, //set to true if navigatino is based on local storage
    isFilter: true,
    isSort: true,
    isLink: true,
    linkToNavigate: '/virtual-workspace',
    //optionals
    headerWidth: '100px',
    bodyWidth: '100px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'EA',
    columnTitle: 'eaInd',
    isCheckBox: true,
    isFilter: true,
    //optionals
    isSort: true,
    headerWidth: '50px',
    bodyWidth: '50px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'GP',
    columnTitle: 'gpInd',
    isCheckBox: true,
    isFilter: true,

    //optionals
    headerWidth: '50px',
    bodyWidth: '50px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Applicant',
    columnTitle: 'applicant',
    isFilter: true,
    isLink: true,
    isSort: true,
    linkToNavigate: environment.facilityNameUrl,
    //optionals
    headerWidth: '100px',
    bodyWidth: '100px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Facility',
    columnTitle: 'facilityName',
    isLink: true,
    isSort: true,
    isFilter: true,
    linkToNavigate: environment.facilityNameUrl,
    //optionals
    headerWidth: '100px',
    bodyWidth: '100px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'locationDirections',
    isSort: true,
    isFilter: true,
    //optionals
    headerWidth: '150px',
    bodyWidth: '150px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Municipality',
    columnTitle: 'municipality',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '110px',
    bodyWidth: '110px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Permit Type',
    columnTitle: 'permitType',
    desc:'permitTypeDesc',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '100px',
    bodyWidth: '100px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'App Type',
    columnTitle: 'appType',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '100px',
    bodyWidth: '100px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Tracked ID',
    columnTitle: 'trackedIdFormatted',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '200px',
    bodyWidth: '200px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Batch ID',
    columnTitle: 'batchId',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '100px',
    bodyWidth: '100px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Status',
    columnTitle: 'dartStatus',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '100px',
    bodyWidth: '100px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Due Date',
    columnTitle: 'dueDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    isDateRed: true,
    //optionals
    headerWidth: '100px',
    bodyWidth: '100px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: "Recv'd Date",
    columnTitle: 'rcvdDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    isDateRed: false,
    //optionals
    headerWidth: '100px',
    bodyWidth: '80px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: '',
    columnTitle: '',
    isExportIcon: true,
    isClearFilterIcon: true,

    //optionals
    headerWidth: '80px',
    bodyWidth: '75px',
    filtersList: [],
  },
];
export const OutForReviewHeaders:PTableHeader[] = [
  {
    columnLabel: 'Project ID',
    columnTitle: 'projectId',
    isSetLocal: false, //set to true if navigatino is based on local storage
    isFilter: true,
    isSort: true,
    isLink: true,
    linkToNavigate: '/virtual-workspace',
    //optionals
    headerWidth: '8%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'EA',
    columnTitle: 'eaInd',
    isCheckBox: true,
    isFilter: true,
    //optionals
    isSort: true,
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'GP',
    columnTitle: 'gpInd',
    isCheckBox: true,
    isFilter: true,

    //optionals
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },

  {
    columnLabel: 'Facility',
    columnTitle: 'facilityName',
    isLink: true,
    isFilter: true,
    isSort: true,
    linkToNavigate: environment.facilityNameUrl,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'formattedAddress',
    isSort: true,
    isFilter: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Permit Type',
    columnTitle: 'permitType',
    desc:'permitTypeDesc',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },

  {
    columnLabel: 'Tracked ID',
    columnTitle: 'trackedIdFormatted',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Status',
    columnTitle: 'dartStatus',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Due Date',
    columnTitle: 'dueDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    isDateRed: true,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
  },

  {
    columnLabel: 'Reviewer',
    columnTitle: 'programStaff',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
  },

  {
    columnLabel: '',
    columnTitle: '',
    isExportIcon: true,
    isClearFilterIcon: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '4%',
    filtersList: [],
  },
];
export const EmergencyAuthorizationHeaders:PTableHeader[] = [
  {
    columnLabel: 'Project ID',
    columnTitle: 'projectId',
    isSetLocal: false, //set to true if navigatino is based on local storage
    isFilter: true,
    isSort: true,
    isLink: true,
    linkToNavigate: '/virtual-workspace',
    //optionals
    headerWidth: '8%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  

  {
    columnLabel: 'Facility',
    columnTitle: 'facilityName',
    isLink: true,
    isFilter: true,
    isSort: true,
    linkToNavigate: environment.facilityNameUrl,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'formattedAddress',
    isSort: true,
    isFilter: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Permit Type',
    columnTitle: 'permitType',
    desc:'permitTypeDesc',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },

  {
    columnLabel: 'Tracked ID',
    columnTitle: 'trackedIdFormatted',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Status',
    columnTitle: 'dartStatus',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Due Date',
    columnTitle: 'dueDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    isDateRed: true,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
  },

  {
    columnLabel: 'Reviewer',
    columnTitle: 'programStaff',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
  },

  {
    columnLabel: '',
    columnTitle: '',
    isExportIcon: true,
    isClearFilterIcon: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '4%',
    filtersList: [],
  },
];
export const SuspendedHeaders:PTableHeader[] = [
  {
    columnLabel: 'Project ID',
    columnTitle: 'projectId',
    isSetLocal: true,
    isFilter: true,
    isSort: true,
    isLink: true,
    linkToNavigate: '/virtual-workspace',
    //optionals
    headerWidth: '8%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'EA',
    columnTitle: 'eaInd',
    isCheckBox: true,
    isFilter: true,
    //optionals
    isSort: true,
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'GP',
    columnTitle: 'gpInd',
    isCheckBox: true,
    isFilter: true,

    //optionals
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },

  {
    columnLabel: 'Facility',
    columnTitle: 'facilityName',
    isLink: true,
    isFilter: true,
    isSort: true,
    linkToNavigate: environment.facilityNameUrl,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'formattedAddress',
    isSort: true,
    isFilter: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Permit Type',
    columnTitle: 'permitType',
    desc:'permitTypeDesc',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },

  {
    columnLabel: 'Tracked ID',
    columnTitle: 'trackedIdFormatted',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Suspend Reason',
    columnTitle: 'dartStatus',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Date Suspended',
    columnTitle: 'dueDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    isDateRed: true,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
  },

  {
    columnLabel: 'Out for Review',
    columnTitle: 'outForReview',
    isFilter: true,
    isSort: true,
    isCheckBox: true,
    align:"center",
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
  },

  {
    columnLabel: '',
    columnTitle: '',
    isExportIcon: true,
    isClearFilterIcon: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '4%',
    filtersList: [],
  },

];
export const PermitScreeningHeaders :PTableHeader[] = [  
  {
    columnLabel: 'GI ID',
    columnTitle: 'inquiryId',
    isFilter: true,
    isSort: true,
    className: 'validate-project-id',
    isLink: true,
   linkToNavigate: '/virtual-workspace',
    isSetLocal: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,    
  },
  {
    columnLabel: 'Project Name',
    columnTitle: 'projectName',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Requestor',
    columnTitle: 'requestorName',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'address',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Municipality',
    columnTitle: 'municipality',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: "Recv'd Date",
    columnTitle: 'rcvdDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
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
    headerWidth: '8%',
    bodyWidth: '6%',
    filtersList: [],
  },
];
export const LeadAgencyRequestHeaders :PTableHeader[] = [  
  {
    columnLabel: 'GI ID',
    columnTitle: 'inquiryId',
    isFilter: true,
    isSort: true,
    className: 'validate-project-id',
    isLink: true,
   linkToNavigate: '/virtual-workspace',
    isSetLocal: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Project Name',
    columnTitle: 'projectName',
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
    columnLabel: 'Project Sponsor',
    columnTitle: 'projectSponsor',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '13%',
    bodyWidth: '13%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'address',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '13%',
    bodyWidth: '13%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Municipality',
    columnTitle: 'municipality',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '13%',
    bodyWidth: '13%',
    filtersList: [],
    exportToExcel: true,
  },


  {
    columnLabel: 'Lead Agency Name',
    columnTitle: 'leadAgencyName',
    desc:'permitTypeDesc',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '13%',
    bodyWidth: '13%',
    filtersList: [],
    exportToExcel: true,
  },
  
  
  {
    columnLabel: 'Lead Agency Contact',
    columnTitle: 'leadAgencyContact',
     isFilter:true,
     isSort:true,
     isDate:false,
    headerWidth: '13%',
    bodyWidth: '13%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: "Recv'd Date",
    columnTitle: 'rcvdDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    //optionals
    headerWidth: '13%',
    bodyWidth: '13%',
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
    headerWidth: '8%',
    bodyWidth: '6%',
    filtersList: [],
  },
];
export const PreAppMeetingRequestHeaders:PTableHeader[]=[
  {
    columnLabel: 'GI ID',
    columnTitle: 'inquiryId',
    isFilter: true,
    isSort: true,
    className: 'validate-project-id',
    isLink: true,
   linkToNavigate: '/virtual-workspace',
    isSetLocal: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Project Name',
    columnTitle: 'projectName',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Project Sponsor',
    columnTitle: 'projectSponsor',
    isFilter: true,
    isSort: true,
    isLink:false,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'address',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Municipality',
    columnTitle: 'municipality',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: "Recv'd Date",
    columnTitle: 'rcvdDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
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
    headerWidth: '8%',
    bodyWidth: '6%',
    filtersList: [],
  },

]
export const BBLDeterminationHeaders:PTableHeader[]=[
  {
    columnLabel: 'GI ID',
    columnTitle: 'inquiryId',
    isFilter: true,
    isSort: true,
    className: 'validate-project-id',
    isLink: true,
   linkToNavigate: '/virtual-workspace',
    isSetLocal: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true
  },
  {
    columnLabel: 'Requestor',
    columnTitle: 'requestorName',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '20%',
    bodyWidth: '20%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'address',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '20%',
    bodyWidth: '20%',
    filtersList: [],
    exportToExcel: true,    
  },
  {
    columnLabel: 'Borough/Block/Lot',
    columnTitle: 'bbl',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '20%',
    bodyWidth: '20%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: "Recv'd Date",
    columnTitle: 'rcvdDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    //optionals
    headerWidth: '20%',
    bodyWidth: '20%',
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
    headerWidth: '8%',
    bodyWidth: '6%',
    filtersList: [],
  }

]
export const MGMTCompHeaders:PTableHeader[]=[
  {
    columnLabel: 'GI ID',
    columnTitle: 'inquiryId',
    isFilter: true,
    isSort: true,
    className: 'validate-project-id',
    isLink: true,
   linkToNavigate: '/virtual-workspace',
    isSetLocal: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true
  },
  {
    columnLabel: 'Project Name',
    columnTitle: 'planName',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Requestor',
    columnTitle: 'requestorName',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'address',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Municipality',
    columnTitle: 'municipality',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: "Recv'd Date",
    columnTitle: 'rcvdDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
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
    headerWidth: '8%',
    bodyWidth: '6%',
    filtersList: [],
  },
]
export const SanitorySewageHeaders:PTableHeader[]=[
  {
    columnLabel: 'GI ID',
    columnTitle: 'inquiryId',
    isFilter: true,
    isSort: true,
    className: 'validate-project-id',
    isLink: true,
   linkToNavigate: '/virtual-workspace',
    isSetLocal: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true
  },
  {
    columnLabel: 'Extender Name',
    columnTitle: 'extenderName',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'DOW Contact',
    columnTitle: 'dowContact',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'address',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Municipality',
    columnTitle: 'municipality',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: "Recv'd Date",
    columnTitle: 'rcvdDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  }, {
    columnLabel: '',
    columnTitle: '',
    isAction: true,
    isExportIcon: true,
    isClearFilterIcon: true,
    //isDelete: true,
    //optionals
    headerWidth: '8%',
    bodyWidth: '6%',
    filtersList: [],
  },

]
export const EnergyProjectHeaders:PTableHeader[]=[
  {
    columnLabel: 'GI ID',
    columnTitle: 'inquiryId',
    isFilter: true,
    isSort: true,
    className: 'validate-project-id',
    isLink: true,
   linkToNavigate: '/virtual-workspace',
    isSetLocal: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Project Name',
    columnTitle: 'projectName',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Developer',
    columnTitle: 'developer',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Owner',
    columnTitle: 'owner',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'PSC Docket #',
    columnTitle: 'pscDocketNum',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'address',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Municipality',
    columnTitle: 'municipality',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: "Recv'd Date",
    columnTitle: 'rcvdDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    //optionals
    headerWidth: '12%',
    bodyWidth: '12%',
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
    headerWidth: '8%',
    bodyWidth: '6%',
    filtersList: [],
  },
]
export const SerpCertificationHeaders:PTableHeader[]=[
  {
    columnLabel: 'GI ID',
    columnTitle: 'inquiryId',
    isFilter: true,
    isSort: true,
    className: 'validate-project-id',
    isLink: true,
   linkToNavigate: '/virtual-workspace',
    isSetLocal: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Project Name',
    columnTitle: 'projectName',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'EFC Contact',
    columnTitle: 'efcContact',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'address',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Municipality',
    columnTitle: 'municipality',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: "Recv'd Date",
    columnTitle: 'rcvdDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
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
    headerWidth: '8%',
    bodyWidth: '6%',
    filtersList: [],
  },
]
export const GeographicalHeaders:PTableHeader[]=[
  {
    columnLabel: 'GI ID',
    columnTitle: 'inquiryId',
    isFilter: true,
    isSort: true,
    className: 'validate-project-id',
    isLink: true,
   linkToNavigate: '/virtual-workspace',
    isSetLocal: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
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
    columnLabel: 'Requestor',
    columnTitle: 'requestorName',
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
    columnLabel: 'Proj Name/Address',
    columnTitle: 'projAddress',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '15%',
    bodyWidth: '15%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Municipality/County',
    columnTitle: 'municicounty',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '15%',
    bodyWidth: '15%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '14%',
    bodyWidth: '14%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: "Review Due",
    columnTitle: 'revDue',
    isFilter: true,
    isSort: true,
    isDate: true,
    //optionals
    headerWidth: '14%',
    bodyWidth: '14%',
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
    headerWidth: '8%',
    bodyWidth: '6%',
    filtersList: [],
  },

]
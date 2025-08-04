import { PTableHeader } from 'src/app/@shared/components/dashboard-table/table.model';
import { environment } from '../../../../../environments/environment';

export const UnValidatedHeaders: PTableHeader[] = [
  {
    columnLabel: '',
    columnTitle: '',
    isFilter: false,
    isSort: false,
    isButton: 'ASSIGN',
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
  },
  {
    columnLabel: 'Project ID',
    columnTitle: 'projectId',
    isFilter: true,
    isSort: true,
    //isLink: true,
    // linkToNavigate: '/virtual-workspace',
    isSetLocal: true,
    className: 'validate-project-id',
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
    isSort: true,
    //optionals
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Applicant',
    columnTitle: 'applicant',
    isFilter: true,
    isSort: true,
    isLink: true,
    linkToNavigate: '',
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
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
    columnLabel: 'Municipality',
    columnTitle: 'municipality',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
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
    columnLabel: 'App Type',
    columnTitle: 'appType',
    isFilter: true,
    isSort: true,
    isDate: false,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
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
    exportToExcel:true
  },
  {
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    isLink: true,
    isSetLocal: true,
    isAssign: true,
    isFilter: true,
    isSort: true,
    isDate: false,
    //optionals
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: '',
    columnTitle: '',
    isAction: true,
    isExportIcon: true,
    isClearFilterIcon: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '7.4%',
    filtersList: [],
    exportToExcel:true
  },
];

export const AllActiveHeaders: PTableHeader[] = [
  {
    columnLabel: 'Project ID',
    columnTitle: 'projectId',
    isSetLocal: false,
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
    isSort: true,
    //optionals
    headerWidth: '40px',
    bodyWidth: '40px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Applicant',
    columnTitle: 'applicant',
    isLink: true,
    isFilter: true,
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
    columnLabel: 'County',
    columnTitle: 'county',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '85px',
    bodyWidth: '85px',
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
    headerWidth: '95px',
    bodyWidth: '95px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'App Type',
    columnTitle: 'appType',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '90px',
    bodyWidth: '90px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: "Recv'd Date",
    columnTitle: 'rcvdDate',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '100px',
    bodyWidth: '100px',
    filtersList: [],
    exportToExcel: true,
  },

  // {
  //   columnLabel: "Recv'd Date",
  //   columnTitle: 'rcvdDate',
  //   isFilter: true,
  //   isSort: true,
  //   isDate: true,
  //   isDateRed: false,
  //
  //   //optionals
  //   headerWidth: '100px',
  //   bodyWidth: '100px',
  //   filtersList: [],
  //   exportToExcel: true,
  // },
  {
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
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
    columnLabel: '',
    columnTitle: '',
    isExportIcon: true,
    isClearFilterIcon: true,
    // isFilter:false,
    // isSort:false,
    // isDate:false,
    // isButton:null,

    //optionals
    headerWidth: '7%',
    bodyWidth: '5%',
    filtersList: [],
  },
];

export const DisposedHeaders: PTableHeader[] = [
  {
    columnLabel: 'Project ID',
    columnTitle: 'projectId',
    isSetLocal: false,
    isFilter: true,
    isSort: true,
    isLink: true,
    linkToNavigate: '/virtual-workspace',
    //optionals
    headerWidth: '85px',
    bodyWidth: '85px',
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
    headerWidth: '40px',
    bodyWidth: '40px',
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
    headerWidth: '50px',
    bodyWidth: '50px',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Applicant',
    columnTitle: 'applicant',
    isLink: true,
    isFilter: true,
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
    columnLabel: 'County',
    columnTitle: 'county',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '90px',
    bodyWidth: '90px',
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
    columnLabel: "Effective Date",
    columnTitle: 'effectiveDate',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '100px',
    bodyWidth: '100px',
    filtersList: [],
    exportToExcel: true,
  },

  {
    columnLabel: "Status",
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
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '100px',
    bodyWidth: '100px',
    filtersList: [],
    exportToExcel: true,
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
    headerWidth: '80px',
    bodyWidth: '30px',
    filtersList: [],
  },
];
export const BBLHeaders: PTableHeader[] = [
  {
    columnLabel: '',
    columnTitle: '',
    isFilter: false,
    isSort: false,
    isButton: 'ASSIGN',
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
  },
  {
    columnLabel: 'GI ID',
    columnTitle: 'inquiryId',
    isFilter: true,
    isSort: true,
    isLink: true,
     linkToNavigate: '/virtual-workspace',
    isSetLocal: true,
    className: 'validate-project-id',
    //optionals
    headerWidth: '7%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: 'Requestor',
    columnTitle: 'requestorName',
    isFilter: true,
    //optionals
    isSort: true,
    headerWidth: '15%',
    bodyWidth: '15%',
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
    linkToNavigate: '',
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: "Recv'd Date",
    columnTitle: 'rcvdDate',
    isFilter: true,
    isSort: true,
    isDate: true,
    //optionals
    headerWidth: '15%',
    bodyWidth: '15%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    isLink: true,
    isSetLocal: true,
    isAssign: true,
    isFilter: true,
    isSort: true,
    isDate: false,
    //optionals
    headerWidth: '15%',
    bodyWidth: '15%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: '',
    columnTitle: '',
    isAction: true,
    isExportIcon: true,
    isClearFilterIcon: true,
    //optionals
    headerWidth: '9%',
    bodyWidth: '7.4%',
    filtersList: [],
    exportToExcel:true
  },
];
export const EnergyProjectHeaders:PTableHeader[]=[
  
    {
    columnLabel: '',
    columnTitle: '',
    isFilter: false,
    isSort: false,
    isButton: 'ASSIGN',
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
  },{
    columnLabel: 'GI ID',
    columnTitle: 'inquiryId',
    isFilter: true,
    isSort: true,
    className: 'validate-project-id',
    isLink: true,
   linkToNavigate: '/virtual-workspace',
    isSetLocal: true,
    //optionals
    headerWidth: '7%',
    bodyWidth: '7%',
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
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Developer',
    columnTitle: 'developer',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Owner',
    columnTitle: 'owner',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '13%',
    bodyWidth: '13%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'PSC Docket #',
    columnTitle: 'pscDocketNum',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Address',
    columnTitle: 'address',
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
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: true,
  },{
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    isLink: true,
    isSetLocal: true,
    isAssign: true,
    isFilter: true,
    isSort: true,
    isDate: false,
    //optionals
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: '',
    columnTitle: '',
    isAction: true,
    isExportIcon: true,
    isClearFilterIcon: true,
    //optionals
    headerWidth: '8%',
    bodyWidth: '6.4%',
    filtersList: [],
    exportToExcel:true
  },

]
export const LeadAgencyRequestHeaders :PTableHeader[] = [  
  {
    columnLabel: '',
    columnTitle: '',
    isFilter: false,
    isSort: false,
    isButton: 'ASSIGN',
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
  },
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
    headerWidth: '7%',
    bodyWidth: '7%',
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
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Project Sponsor',
    columnTitle: 'projectSponsor',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
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
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },


  {
    columnLabel: 'Lead Agency Name',
    columnTitle: 'leadAgencyName',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  
  
  {
    columnLabel: 'Lead Agency Contact',
    columnTitle: 'leadAgencyContact',
     isFilter:true,
     isSort:true,
     isDate:false,
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
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
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    isLink: true,
    isAssign: true,
    isFilter: true,
    isSort: true,
    isDate: false,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: '',
    columnTitle: '',
    isAction: true,
    isExportIcon: true,
    isClearFilterIcon: true,
    //optionals
    headerWidth: '8%',
    bodyWidth: '6.4%',
    filtersList: [],
    exportToExcel:true
  },
];
export const PermitScreeningHeaders :PTableHeader[] = [  
  {
    columnLabel: '',
    columnTitle: '',
    isFilter: false,
    isSort: false,
    isButton: 'ASSIGN',
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
  },
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
    headerWidth: '7%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel: true
  }
  ,{
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
  },{
  columnLabel: 'Address',
  columnTitle: 'address',
  isFilter: true,
  isSort: true,
  //optionals
  headerWidth: '20%',
  bodyWidth: '20%',
  filtersList: [],
  exportToExcel: true,
},{
columnLabel: 'Municipality',
columnTitle: 'municipality',
isFilter: true,
isSort: true,
//optionals
headerWidth: '18%',
bodyWidth: '18%',
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
    headerWidth: '16%',
    bodyWidth: '16%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    isLink: true,
    isFilter: true,
    isSetLocal: true,
    isAssign: true,
    isSort: true,
    isDate: false,
    //optionals
    headerWidth: '16%',
    bodyWidth: '16%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: '',
    columnTitle: '',
    isAction: true,
    isExportIcon: true,
    isClearFilterIcon: true,
    //optionals
    headerWidth: '8%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel:true
  },

  
];
export const PreAppMeetingRequestHeaders:PTableHeader[]=[
  {
    columnLabel: '',
    columnTitle: '',
    isFilter: false,
    isSort: false,
    isButton: 'ASSIGN',
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
  },
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
    headerWidth: '7%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Project Name',
    columnTitle: 'projectName',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Project Sponsor',
    columnTitle: 'projectSponsor',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
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
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    isLink: true,
    isSetLocal: true,
    isAssign: true,
    isFilter: true,
    isSort: true,
    isDate: false,
    //optionals
    headerWidth: '13%',
    bodyWidth: '13%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: '',
    columnTitle: '',
    isAction: true,
    isExportIcon: true,
    isClearFilterIcon: true,
    //optionals
    headerWidth: '8%',
    bodyWidth: '6.4%',
    filtersList: [],
    exportToExcel:true
  },

]
export const SanitorySewageHeaders:PTableHeader[]=[
  {
    columnLabel: '',
    columnTitle: '',
    isFilter: false,
    isSort: false,
    isButton: 'ASSIGN',
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
  },
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
    headerWidth: '7%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Extender Name',
    columnTitle: 'extName',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'DOW Contact',
    columnTitle: 'dowContact',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
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
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    isLink: true,
    isSetLocal: true,
    isAssign: true,
    isFilter: true,
    isSort: true,
    isDate: false,
    //optionals
    headerWidth: '13%',
    bodyWidth: '13%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: '',
    columnTitle: '',
    isAction: true,
    isExportIcon: true,
    isClearFilterIcon: true,
    //optionals
    headerWidth: '8%',
    bodyWidth: '6.4%',
    filtersList: [],
    exportToExcel:true
  },
]
export const SerpCertificationHeaders:PTableHeader[]=[
  {
    columnLabel: '',
    columnTitle: '',
    isFilter: false,
    isSort: false,
    isButton: 'ASSIGN',
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
  },
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
    headerWidth: '7%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Project Name',
    columnTitle: 'projectName',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'EFC Contact',
    columnTitle: 'efcContact',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
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
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    isLink: true,
    isSetLocal: true,
    isAssign: true,
    isFilter: true,
    isSort: true,
    isDate: false,
    //optionals
    headerWidth: '13%',
    bodyWidth: '13%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: '',
    columnTitle: '',
    isAction: true,
    isExportIcon: true,
    isClearFilterIcon: true,
    //optionals
    headerWidth: '8%',
    bodyWidth: '6.4%',
    filtersList: [],
    exportToExcel:true
  },

]
export const MGMTCompHeaders:PTableHeader[]=[
  {
    columnLabel: '',
    columnTitle: '',
    isFilter: false,
    isSort: false,
    isButton: 'ASSIGN',
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
  },
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
    headerWidth: '7%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Plan Name',
    columnTitle: 'planName',
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
    columnLabel: 'Requestor',
    columnTitle: 'requestorName',
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
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    isLink: true,
    isSetLocal: true,
    isAssign: true,
    isFilter: true,
    isSort: true,
    isDate: false,
    //optionals
    headerWidth: '13%',
    bodyWidth: '13%',
    filtersList: [],
    exportToExcel:true
  },
  {
    columnLabel: '',
    columnTitle: '',
    isAction: true,
    isExportIcon: true,
    isClearFilterIcon: true,
    //optionals
    headerWidth: '8%',
    bodyWidth: '6.4%',
    filtersList: [],
    exportToExcel:true
  },
]

export const AllActiveInquiriesHeaders:PTableHeader[]= [
  {
    columnLabel: '',
    columnTitle: '',
    isFilter: false,
    isSort: false,
    isButton: 'ASSIGN',
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
  },
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
    columnLabel: 'Requestor',
    columnTitle: 'requestor',
    isFilter: true,
    isSort: true,
    isLink: false,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Request Identifier',
    columnTitle: 'requestIdentifier',
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
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel: true,
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
    columnLabel: "Recv'd Date",
    columnTitle: 'rcvdDate',
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
    columnLabel: 'Analyst',
    columnTitle: 'analystName',
    isLink: true,
    isFilter: true,
    isSort: true,
    isAssign: true,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: true,
  },
  {
    columnLabel: 'Status',
    columnTitle: 'statusInd',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '8%',
    bodyWidth: '8%',
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
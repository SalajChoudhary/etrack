import { PTableHeader } from 'src/app/@shared/components/dashboard-table/table.model';
export const purgeArchiveHeaders :PTableHeader[]=[
    {
      columnLabel: '',
      columnTitle: 'markForReview',
      isFilter: false,
      isSort: false,
      //optionals
      headerWidth: '4%',
      bodyWidth: '4%',
      isCheckBox: true,
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Document Name',
      columnTitle: 'documentName',
      isFilter: true,
      isSort: true,
      isLink: true,
      //optionals
      headerWidth: '15%',
      bodyWidth: '15%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Type',
      columnTitle: 'docType',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '8%',
      bodyWidth: '8%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Sub-Type',
      columnTitle: 'docSubType',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '10%',
      bodyWidth: '10%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Project ID',
      columnTitle: 'projectId',
      isFilter: true,
      isSort: true,
      isLink: true,
      //optionals
      headerWidth: '10%',
      bodyWidth: '10%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'DEC ID',
      columnTitle: 'decId',
      isFilter: true,
      isSort: true,
      isLink: true,
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
      headerWidth: '10%',
      bodyWidth: '10%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Municipality',
      columnTitle: 'municipalityName',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '13%',
      bodyWidth: '13%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: '',
      columnTitle: 'removePurgeArchive',
      //optionals
      align: 'center',
      isButton: 'REMOVE',
      headerWidth: '4%',
      bodyWidth: '4%',
      filtersList: []
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

  export const archiveAdminHeaders :PTableHeader[]=[
    {
      columnLabel: '',
      columnTitle: 'markedForReview',
      isFilter: false,
      isSort: false,
      //optionals
      headerWidth: '4%',
      bodyWidth: '4%',
      isCheckBox: true,
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Document Name',
      columnTitle: 'documentName',
      isFilter: true,
      isSort: true,
      isLink: true,
      //optionals
      headerWidth: '15%',
      bodyWidth: '15%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Type',
      columnTitle: 'docType',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '7%',
      bodyWidth: '7%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Sub-Type',
      columnTitle: 'docSubType',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '11%',
      bodyWidth: '11%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Project ID',
      columnTitle: 'projectId',
      isFilter: true,
      isSort: true,
      isLink: true,
      //optionals
      headerWidth: '10%',
      bodyWidth: '10%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'DEC ID',
      columnTitle: 'decId',
      isFilter: true,
      isSort: true,
      isLink: true,
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
      headerWidth: '10%',
      bodyWidth: '10%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Municipality',
      columnTitle: 'municipalityName',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '12%',
      bodyWidth: '12%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: '',
      columnTitle: 'trashcanPurge',
      //optionals
      align: 'center',
      isButton: 'REMOVE',
      headerWidth: '4%',
      bodyWidth: '4%',
      filtersList: []
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
  ]

  export const purgeArchiveAdminHeaders :PTableHeader[]=[
    {
      columnLabel: '',
      columnTitle: 'markedForReview',
      isFilter: false,
      isSort: false,
      //optionals
      headerWidth: '4%',
      bodyWidth: '4%',
      isCheckBox: true,
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Document Name',
      columnTitle: 'documentName',
      isFilter: true,
      isSort: true,
      isLink: true,
      //optionals
      headerWidth: '15%',
      bodyWidth: '15%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Type',
      columnTitle: 'docType',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '7%',
      bodyWidth: '7%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Sub-Type',
      columnTitle: 'docSubType',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '10%',
      bodyWidth: '10%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Project ID',
      columnTitle: 'projectId',
      isFilter: true,
      isSort: true,
      isLink: true,
      //optionals
      headerWidth: '10%',
      bodyWidth: '10%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'DEC ID',
      columnTitle: 'decId',
      isFilter: true,
      isSort: true,
      isLink: true,
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
      headerWidth: '9%',
      bodyWidth: '9%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Municipality',
      columnTitle: 'municipalityName',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '11%',
      bodyWidth: '11%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: '',
      columnTitle: 'trashcanPurge',
      //optionals
      align: 'center',
      isButton: 'REMOVE',
      headerWidth: '4%',
      bodyWidth: '4%',
      filtersList: []
    },
    {
      columnLabel: '',
      columnTitle: 'removePurgeArchive',
      //optionals
      align: 'center',
      isButton: 'REMOVE',
      headerWidth: '4%',
      bodyWidth: '4%',
      filtersList: []
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

  export const purgeArchiveAdminReadOnlyHeaders :PTableHeader[]=[
    {
      columnLabel: 'Document Name',
      columnTitle: 'documentName',
      isFilter: true,
      isSort: true,
      isLink: true,
      //optionals
      headerWidth: '18%',
      bodyWidth: '18%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Type',
      columnTitle: 'docType',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '9%',
      bodyWidth: '9%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Sub-Type',
      columnTitle: 'docSubType',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '13%',
      bodyWidth: '13%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Project ID',
      columnTitle: 'projectId',
      isFilter: true,
      isSort: true,
      isLink: true,
      //optionals
      headerWidth: '13%',
      bodyWidth: '13%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'DEC ID',
      columnTitle: 'decId',
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
      //optionals
      headerWidth: '11%',
      bodyWidth: '11%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Municipality',
      columnTitle: 'municipalityName',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '15%',
      bodyWidth: '15%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: '',
      columnTitle: '',
      isAction: true,
      isExportIcon: true,
      isClearFilterIcon: true,
      //optionals
      headerWidth: '10%',
      bodyWidth: '8.4%',
      filtersList: [],
      exportToExcel:true
    },
  ]

  export const purgeArchiveDownloadHeaders :PTableHeader[]=[
    {
      columnLabel: '',
      columnTitle: 'markForDownload',
      isFilter: false,
      isSort: false,
      //optionals
      headerWidth: '10%',
      bodyWidth: '10%',
      isCheckBox: true,
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Doc Name',
      columnTitle: 'documentName',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '10%',
      bodyWidth: '10%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Type',
      columnTitle: 'docType',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '8%',
      bodyWidth: '8%',
      filtersList: [],
      exportToExcel: true,
    },
    {
      columnLabel: 'Sub-Type',
      columnTitle: 'docSubType',
      isFilter: true,
      isSort: true,
      //optionals
      headerWidth: '10%',
      bodyWidth: '10%',
      filtersList: [],
      exportToExcel: true,
    },

    {
        columnLabel: 'App ID',
        columnTitle: 'appId',
        isFilter: true,
        isSort: true,
        //optionals
        headerWidth: '9%',
        bodyWidth: '9%',
        filtersList: [],
        exportToExcel: true,
    },

    {
        columnLabel: 'File Name',
        columnTitle: 'fileName',
        isFilter: true,
        isSort: true,
        //optionals
        headerWidth: '10%',
        bodyWidth: '10%',
        filtersList: [],
        exportToExcel: true,
    },

    {
        columnLabel: 'File Date',
        columnTitle: 'fileDate',
        isFilter: true,
        isSort: true,
        //optionals
        headerWidth: '10%',
        bodyWidth: '10%',
        filtersList: [],
        exportToExcel: true,
    },

    {
        columnLabel: 'Access',
        columnTitle: 'accessLevel',
        isFilter: true,
        isSort: true,
        //optionals
        headerWidth: '10%',
        bodyWidth: '10%',
        filtersList: [],
        exportToExcel: true,
    },

    {
        columnLabel: 'DEP Rel Det',
        columnTitle: 'depRelDet',
        isFilter: true,
        isSort: true,
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
      //optionals
      headerWidth: '10%',
      bodyWidth: '8.4%',
      filtersList: [],
      exportToExcel:true
    }
  ]
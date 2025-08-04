import { PTableHeader } from '../../../../@shared/components/dashboard-table/table.model';

export const SystemParameterHeaders: any[] = [
  {
    columnLabel: 'URL ID',
    columnTitle: 'urlId',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '20%',
    bodyWidth: '20%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: 25,
    columnType: 'text',
    isDisabled: false,
    dropdownKey: 'urlId',
    mapperBinding: {
      urlLink: 'urlLink',
    },
    sendObj: {
      uniquekey: 'urlId',
      value: 'updatedValue',
    },
    addNewHeaders: [
      {
        name: 'URL ID',
        columnType: 'textbox',
        columnTitle: 'urlId',
        maxCharacter: 25,
        titleName: 'System Parameter',
        sendObj: {
          uniquekey: 'urlId',
          value: 'urlLink',
        },
      },
      {
        name: 'URL Link',
        columnType: 'textbox',
        maxCharacter: 200,
        columnTitle: 'urlLink'
      }
    ]
  },
  {
    columnLabel: 'URL Link',
    columnTitle: 'urlLink',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '40%',
    bodyWidth: '40%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: 200,
    columnType: 'text',
    isDisabled: true,
  },
  {
    columnLabel: 'Updated URL Link',
    columnTitle: 'updatedValue',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: true,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: 200,
    columnType: 'textbox',
    isDisabled: false,
  },
];

export const TransTypeCodeHeaders: any[] = [
  {
    columnLabel: 'Trans Type Code',
    columnTitle: 'transTypeCode',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '20%',
    bodyWidth: '20%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'dropdown',
    isDisabled: false,
    dropdownKey: 'transTypeCode',
    mapperBinding: {
      transTypeDesc: 'transTypeDesc',
    },
    sendObj: {
      uniquekey: 'transTypeCode',
      value: 'updatedValue',
    },
    addNewHeaders: [
      {
        name: 'Trans Type Code',
        columnType: 'textbox',
        columnTitle: 'transTypeCode',
        sendObj: {
          uniquekey: 'transTypeCode',
          value: 'transTypeDesc',
        },
      },
      {
        name: 'Trans Type Desc',
        columnType: 'textbox',
        columnTitle: 'transTypeDesc'
      }
    ]
  },
  {
    columnLabel: 'Trans Type Desc',
    columnTitle: 'transTypeDesc',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'textbox',
    isDisabled: true,
  },
  {
    columnLabel: 'Updated Value',
    columnTitle: 'updatedValue',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '40%',
    bodyWidth: '40%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'textbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'addBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Add',
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const MessagesHeaders: any[] = [
  {
    columnLabel: 'Message Code',
    columnTitle: 'messageCode',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '25%',
    bodyWidth: '25%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: 25,
    columnType: 'text',
    isDisabled: false,
    dropdownKey: 'messageCode',
    mapperBinding: {
      existingValue: 'messageDesc',
    },
    sendObj: {
      messageCode: 'messageCode',
      messageDesc: 'updatedValue',
      messageTypeId:'messageTypeId'
    },
    editObj: {
      messageCode: 'messageCode',
      messageDesc: 'messageDesc',
      messageTypeId: 'messageTypeId'
    },
    addNewHeaders: [
      {
        name: 'Message Code',
        columnType: 'textbox',
        columnTitle: 'messageCode',
        maxCharacter: 25,
        sendObj: {
          messageCode: 'messageCode',
          messageDesc: 'messageDesc',
          messageTypeId:'messageTypeId',
          newMessageCode: 1,
        },
      },
      {
        name: 'Message Text',
        columnType: 'textbox',
        maxCharacter: 500,
        columnTitle: 'messageDesc'
      },
      {
        name: 'Message Type',
        columnType: 'dropdown',
        columnTitle: 'messageTypeId',
        tableName: 'e_message_type',
        tableData: [],
        dropdownValue: 'messageTypeId',
        dropdownKey: 'messageTypeDescription'
      }
    ],
    editHeaders: [
      {
        name: 'Message Code',
        columnType: 'textbox',
        columnTitle: 'messageCode',
        maxCharacter: 25,
        sendObj: {
          messageCode: 'messageCode',
          messageDesc: 'messageDesc',
          messageTypeId:'messageTypeId',
        },
        disableOnEdit: true
      },
      {
        name: 'Message Text',
        columnType: 'textbox',
        maxCharacter: 500,
        columnTitle: 'messageDesc'
      },
      {
        name: 'Message Type',
        columnType: 'dropdown',
        columnTitle: 'messageTypeId',
        tableName: 'e_message_type',
        tableData: [],
        dropdownValue: 'messageTypeId',
        dropdownKey: 'messageTypeDescription'
      }
    ]
  },
  {
    columnLabel: 'Message Text',
    columnTitle: 'messageDesc',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '58%',
    bodyWidth: '58%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
  },
  {
    columnLabel: 'Message Type',
    columnTitle: 'messageTypeDescription',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text'
  },

  {
    columnLabel: '',
    columnTitle: 'editBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Edit',
    //optionals
    headerWidth: '5%',
    bodyWidth: '3%',
    filtersList: [],
    exportToExcel: true,
  },

];

export const PermitCategoryHeaders: any[] = [
  {
    columnLabel: 'Permit Category',
    columnTitle: 'existingValue',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '40%',
    bodyWidth: '40%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '70%',
    maxCharacter: 50,
    columnType: 'dropdown',
    isDisabled: false,
    dropdownKey: 'permitCategoryDescription',
    mapperBinding: {
      activeInd: 'activeInd',
      updatedValue:'permitCategoryDescription'
    },
    sendObj: {
      permitCategoryId: 'permitCategoryId',
      permitCategoryDesc: 'updatedValue',
      activeInd: 'activeInd',
    },
    checkboxes: ['activeInd'],
    addNewHeaders: [
      {
        name: 'Permit Category',
        columnType: 'textbox',
        columnTitle: 'permitCategoryDescription',
        maxCharacter: 50,
        sendObj: {
          permitCategoryId: 'permitCategoryId',
          permitCategoryDesc: 'permitCategoryDescription',
          activeInd: 'activeInd',
        },
      },
    ]
  },
  {
    columnLabel: 'Updated Permit Category',
    columnTitle: 'updatedValue',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '40%',
    bodyWidth: '40%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: 50,
    columnType: 'textbox',
    isDisabled: false,
  },
  {
    columnLabel: 'Active',
    columnTitle: 'activeInd',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'addBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Add',
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: false,
  },
];

export const PermitTypeCodeHeaders: any[] = [
  {
    columnLabel: 'Permit Type',
    columnTitle: 'existingValue',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '70%',
    maxCharacter: 75,
    //tableName:'E_PERMIT_TYPE_CODE',
    columnType: 'dropdown',
    isDisabled: false,
    dropdownKey: 'permitTypeDescription',
    addNewHeaders: [],
    mapperBinding: {
      permitCategory:'permitCategoryDescription',
      // active: 'activeInd',
    },
    sendObj: {
      permitTypeCode: 'permitTypeCode',
      permitCategoryId: 'updatedValue',
    },

  },
  {
    columnLabel: 'Current Category',
    columnTitle: 'permitCategory',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '28%',
    bodyWidth: '28%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: 75,
    columnType: 'textbox',
    isDisabled: true,
  },
  {
    columnLabel: 'Move To New Category',
    columnTitle: 'updatedValue',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '70%',
    maxCharacter: 75,
    columnType: 'dropdown-sub',
    dropdownValues: [],
    tableName: 'e_permit_category',
    dropdownKey: 'permitCategoryDescription',
    dropdownValue:'permitCategoryId',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'addBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Add',
    //optionals
    headerWidth: '7%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const DocumentTitleHeaders: any[] = [
  {
    columnLabel: 'Document Title',
    columnTitle: 'documentTitle',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '40%',
    bodyWidth: '40%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'dropdown',
    isDisabled: false,
    dropdownKey: 'description',
    mapperBinding: {
      classType: 'documentClassId',
      depOnly: 'availToDepInd',
      active: 'activeInd',
    },
    sendObj: {
      docTitleId:'id',
      docTitle:'updatedValue',
    },
    checkboxes: ['active', 'depOnly'],
    addNewHeaders: [
      {
        name: 'Document Title',
        columnType: 'textbox',
        columnTitle: 'documentTitleHeader',
        maxCharacter: 100,
        sendObj: {
          docTitleId:'',
          docTitle: 'documentTitleHeader',
        },
      },
    ]
  },
  {
    columnLabel: 'Updated Value',
    columnTitle: 'updatedValue',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '40%',
    bodyWidth: '40%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: false,
    searchableDropdownWidth: '24rem',
    maxCharacter: 100,
    columnType: 'textbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'addBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Add',
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: false,
  },
];

export const DocumentTypeHeaders: any[] = [
  {
    columnLabel: 'Document Type',
    columnTitle: 'description',
    isFilter: false,
    isSort: true,
    //optionals
    headerWidth: '40%',
    bodyWidth: '40%',
    filtersList: [],
    exportToExcel: false,
    maxCharacter: 75,
    isDisabled: false,
    columnType: "text",
    dropdownKey: 'description',
    mapperBinding: {
      classType: 'documentClassName',
      depOnly: 'availToDepInd',
      active: 'activeInd',
    },
    sendObj: {
      documentTypeId: 'documentTypeId',
      docDescription: 'description',
      documentClassId: 'documentClassId',
      activeInd: 'active',
      availToDepOnlyInd: 'depOnly',
    },
    editObj: {
      'documentType': 'description',
      'classType': 'documentClassId'
    },
    checkboxes: ['active', 'depOnly'],
    addNewHeaders: [
      {
        name: 'Document Type',
        columnType: 'textbox',
        columnTitle: 'documentType',
        maxCharacter: 75,
        sendObj: {
          docDescription: 'documentType',
          documentClassId:'classType',
          documentTypeId: '',
          activeInd: '',
          availToDepOnlyInd: '',
        },
      },
      {
        name: 'Document Class Type',
        columnType: 'dropdown',
        columnTitle: 'classType',
        tableName: 'e_document_class',
        tableData: [],
        dropdownValue: 'id',
        dropdownKey: 'description'
      }
    ],
    editHeaders: [
      {
        name: 'Document Type',
        columnType: 'textbox',
        columnTitle: 'documentType',
        maxCharacter: 75,
        sendObj: {
          docDescription: 'documentType',
          documentClassId:'classType',
          documentTypeId: 'documentTypeId',
          activeInd: 'activeInd',
          availToDepOnlyInd: 'availToDepInd',
        },
      },
      {
        name: 'Document Class Type',
        columnType: 'dropdown',
        columnTitle: 'classType',
        tableName: 'e_document_class',
        disableOnEdit: true,
        tableData: [],
        dropdownValue: 'id',
        dropdownKey: 'description'
      }
    ]
  },
  {
    columnLabel: 'Document Class Type',
    columnTitle: 'documentClassName',
    isFilter: false,
    isSort: true,
    //optionals
    headerWidth: '39%',
    bodyWidth: '39%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'text',
  },
  {
    columnLabel: 'DEP Only',
    columnTitle: 'depOnly',
    oppTitle: 'active',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    oppTitle: 'depOnly',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'editBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Edit',
    //optionals
    headerWidth: '5%',
    bodyWidth: '3%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const DocumentSubTypeHeaders: any[] = [
  {
    columnLabel: 'Document Type',
    columnTitle: 'documentTypeName',
    isFilter: false,
    isSort: true,
    //optionals
    headerWidth: '39%',
    bodyWidth: '39%',
    filtersList: [],
    exportToExcel: false,
    maxCharacter: '75',
    columnType: 'text',
    mapperBinding: {
      documentTypeName: 'documentTypeName',
      depOnly: 'availToDepInd',
      active: 'activeInd',
    },
    sendObj: {
      documentSubTypeId: 'documentSubTypeId',
      documentTypeId: 'documentTypeId',
      docSubTypeDescription: 'description',
      activeInd: 'active',
      availToDepOnlyInd: 'depOnly',
    },
    editObj: {
      'documentSubType': 'description',
      'documentTypeId': 'documentTypeId'
    },
    checkboxes: ['active', 'depOnly'],
    addNewHeaders: [
      {
        name: 'Document Type',
        columnType: 'dropdown',
        columnTitle: 'documentTypeId',
        tableName: 'e_document_type',
        tableData: [],
        dropdownValue: 'documentTypeId',
        dropdownKey: 'description',
        sendObj: {
          docSubTypeDescription: 'documentSubType',
          documentTypeId: 'documentTypeId',
          activeInd: '',
          availToDepOnlyInd: 'depOnly',
        },
      },
      {
        name: 'Document Sub Type',
        columnType: 'textbox',
        columnTitle: 'documentSubType',
        maxCharacter: 100,

      }

    ],
    editHeaders: [
      {
        name: 'Document Type',
        columnType: 'dropdown',
        columnTitle: 'documentTypeId',
        tableName: 'e_document_type',
        disableOnEdit: true,
        tableData: [],
        dropdownValue: 'documentTypeId',
        dropdownKey: 'description',
        sendObj: {
          documentSubTypeId: 'documentSubTypeId',
          docSubTypeDescription: 'documentSubType',
          documentTypeId: 'documentTypeId',
          activeInd: 'activeInd',
          availToDepOnlyInd: 'availToDepInd',
        },
      },
      {
        name: 'Document Sub Type',
        columnType: 'textbox',
        columnTitle: 'documentSubType',
        maxCharacter: 100,
      }
    ]
  },
  {
    columnLabel: 'Document Sub Type',
    columnTitle: 'description',
    isFilter: false,
    isSort: true,
    //optionals
    headerWidth: '39%',
    bodyWidth: '40%',
    filtersList: [],
    exportToExcel: false,
    maxCharacter: 500,
    columnType: 'text',
    isDisabled: false,
    dropdownKey: 'description',

  },

  {
    columnLabel: 'DEP Only',
    columnTitle: 'depOnly',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'editBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Edit',
    //optionals
    headerWidth: '5%',
    bodyWidth: '3%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const GenericDocumentTableHeaders: PTableHeader[] = [
  {
    columnLabel: 'Document Type Description',
    columnTitle: 'docTypeDesc',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
  },
  {
    columnLabel: 'Updated Value',
    columnTitle: 'updatedValue',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: false,
    maxCharacter: '75',
  },
  {
    columnLabel: 'DEP Only',
    columnTitle: 'depInd',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '10%',
    isCheckBox: true,
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: false,
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    isCheckBox: true,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: false,
  },
];
export const DocumentSubTypeTableHeaders: PTableHeader[] = [
  {
    columnLabel: 'Document Type',
    columnTitle: 'docType',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '15%',
    bodyWidth: '15%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '19rem',
  },
  {
    columnLabel: 'Sub Type Description',
    columnTitle: 'subType',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '15rem',
    //Check this max character
    maxCharacter: '100',
  },
  {
    columnLabel: 'Updated Value',
    columnTitle: 'updatedValue',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: false,
    maxCharacter: '75',
  },
  {
    columnLabel: 'DEP Only',
    columnTitle: 'depInd',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '12.5%',
    isCheckBox: true,
    bodyWidth: '12.5%',
    filtersList: [],
    exportToExcel: false,
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    isCheckBox: true,
    //optionals
    headerWidth: '12.5%',
    bodyWidth: '12.5%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const DocumentTitleTableHeaders: PTableHeader[] = [
  {
    columnLabel: 'Document Title',
    columnTitle: 'docTitle',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '35%',
    bodyWidth: '35%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '100',
  },
  {
    columnLabel: 'Updated Value',
    columnTitle: 'updatedValue',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '35%',
    bodyWidth: '35%',
    filtersList: [],
    exportToExcel: false,
    maxCharacter: '100',
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    isCheckBox: true,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: false,
  },
  {
    columnLabel: '',
    columnTitle: 'addBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Add',
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const DocumentSubTypeTitleTableHeaders: any[] = [
  {
    columnLabel: 'Document Type',
    columnTitle: 'documentTypeDesc',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
    checkboxes: ['active'],
    sendObj: {
      documentSubTypeId: 'documentSubTypeId',
      documentTypeId: 'documentTypeId',
      documentTitleId: 'documentTitleId',
      activeInd: 'active',
      documentSubTypeTitleId: 'documentSubTypeTitleId'
    },
    editObj: {
      documentSubTypeId: 'documentSubTypeId',
      documentTypeId: 'documentTypeId',
      documentTitleId: 'documentTitleId',
    },
    addNewHeaders: [
      {
        name: 'Document Type',
        columnType: 'dropdown',
        columnTitle: 'documentTypeId',
        tableName: 'e_document_type',
        tableData: [],
        dropdownValue: 'documentTypeId',
        dropdownKey: 'description',
        sendObj: {
          documentSubTypeId: 'documentSubTypeId',
          documentTypeId: 'documentTypeId',
          documentTitleId: 'documentTitleId',
          activeInd: '1',
          documentSubTypeTitleId: '',
        },
      },
      {
        name: 'Document Sub Type',
        columnType: 'dropdown',
        columnTitle: 'documentSubTypeId',
        tableName: 'e_document_sub_type',
        tableData: [],
        dropdownValue: 'documentSubTypeId',
        dropdownKey: 'description',
        optional: true
      },
      {
        name: 'Doc Sub Type Title',
        columnType: 'dropdown',
        columnTitle: 'documentTitleId',
        tableName: 'e_document_title',
        tableData: [],
        dropdownValue: 'id',
        dropdownKey: 'description',
      },

    ],
    editHeaders: [
      {
        name: 'Document Type',
        columnType: 'dropdown',
        columnTitle: 'documentTypeId',
        tableName: 'e_document_type',
        tableData: [],
        dropdownValue: 'id',
        dropdownKey: 'description',
        disableOnEdit: true,
        sendObj: {
          documentSubTypeId: 'documentSubTypeId',
          documentTypeId: 'documentTypeId',
          documentTitleId: 'documentTitleId',
          activeInd: 'activeInd',
          documentSubTypeTitleId: 'documentSubTypeTitleId',
        },
      },
      {
        name: 'Document Sub Type',
        columnType: 'dropdown',
        columnTitle: 'documentSubTypeId',
        tableName: 'e_document_sub_type',
        tableData: [],
        dropdownValue: 'id',
        dropdownKey: 'description',
        disableOnEdit: true,
        optional: true
      },
      {
        name: 'Doc Sub Type Title',
        columnType: 'dropdown',
        columnTitle: 'documentTitleId',
        tableName: 'e_document_title',
        tableData: [],
        dropdownValue: 'id',
        dropdownKey: 'description',
      },
    ]
  },
  {
    columnLabel: 'Document Sub Type',
    columnTitle: 'documentSubTypeDesc',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '29%',
    bodyWidth: '29%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '15rem',
    columnType: 'text'
  },
  {
    columnLabel: 'Doc Sub Type Title',
    columnTitle: 'documentTitle',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '15rem',
    columnType: 'text'
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    isCheckBox: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'checkbox'
  },
  {
    columnLabel: '',
    columnTitle: 'editBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Edit',
    //optionals
    headerWidth: '5%',
    bodyWidth: '3%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const SWFacilityTypeHeaders: any[] = [
  {
    columnLabel: 'SW Facility Type',
    columnTitle: 'facilityType',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '50%',
    bodyWidth: '50%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
    checkboxes: ['active'],
    editObj: {
      facilityType: 'facilityType',
      regulationCode: 'regulationCode',
    },
    sendObj: {
      facilityType: 'facilityType',
      regulationCode: 'regulationCode',
      swFacilityTypeId: 'swFacilityTypeId',
      activeInd: 'active'
    },
    addNewHeaders: [
      {
        name: 'SW Facility Type',
        columnType: 'textbox',
        columnTitle: 'facilityType',
        maxCharacter: 80,
        sendObj: {
          facilityType: 'facilityType',
          regulationCode: 'regulationCode',
          swFacilityTypeId: '',
          activeInd: ''
        },
      },
      {
        name: 'SW Regulation Code',
        columnType: 'textbox',
        columnTitle: 'regulationCode',
        maxCharacter: 10
      }
    ],
    editHeaders: [
      {
        name: 'SW Facility Type',
        columnType: 'textbox',
        columnTitle: 'facilityType',
        maxCharacter: 80,
        sendObj: {
          facilityType: 'facilityType',
          regulationCode: 'regulationCode',
          swFacilityTypeId: 'swFacilityTypeId',
          activeInd: 'activeInd'
        },
      },
      {
        name: 'SW Regulation Code',
        columnType: 'textbox',
        columnTitle: 'regulationCode',
        maxCharacter: 10
      }
    ]
  },
  {
    columnLabel: 'SW Regulation Code',
    columnTitle: 'regulationCode',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '39%',
    bodyWidth: '39%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'editBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Edit',
    //optionals
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const SWFacilitySubTypeHeaders: any[] = [
  {
    columnLabel: 'SW Facility Type',
    columnTitle: 'facilityType',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
    checkboxes: ['active'],
    editObj: {
      facilityType: 'facilityTypeId',
      regulationCode: 'regulationCode',
      facilitySubType: 'facilitySubType',
      subTypeRegulationCode: 'facilitySubTypeRegulationCode'
    },
    sendObj: {
      facilityTypeId: 'facilityTypeId',
      facilitySubType: 'facilitySubType',
      facilitySubTypeRegulationCode: 'facilitySubTypeRegulationCode',
      swFacilitySubTypeId: 'swFacilitySubTypeId',
      activeInd: 'active'
    },
    addNewHeaders: [
      {
        name: 'SW Facility Type',
        columnType: 'dropdown',
        columnTitle: 'facilityType',
        tableData: [],
        tableName: 'e_sw_facility_type',
        dropdownValue: 'swFacilityTypeId',
        dropdownKey: 'facilityType',
        sendObj: {
          facilityTypeId: 'facilityType',
          facilitySubType: 'facilitySubType',
          facilitySubTypeRegulationCode: 'subTypeRegulationCode',
          swFacilitySubTypeId: 'swFacilitySubTypeId',
          activeInd: ''
        },
      },
      {
        name: 'SW Regulation Code',
        columnType: 'text',
        columnTitle: 'regulationCode',
      },
      {
        name: 'SW Facility Sub Type',
        columnType: 'textbox',
        columnTitle: 'facilitySubType',
        maxCharacter: 80,
      },
      {
        name: 'SW Sub Type Regulation Code',
        columnType: 'textbox',
        columnTitle: 'subTypeRegulationCode',
        maxCharacter: 10
      }
    ],
    editHeaders: [
      {
        name: 'SW Facility Type',
        columnType: 'dropdown',
        columnTitle: 'facilityType',
        tableData: [],
        tableName: 'e_sw_facility_type',
        dropdownValue: 'swFacilityTypeId',
        dropdownKey: 'facilityType',
        disableOnEdit: true,
        sendObj: {
          facilityTypeId: 'facilityTypeId',
          facilitySubType: 'facilitySubType',
          facilitySubTypeRegulationCode: 'subTypeRegulationCode',
          swFacilitySubTypeId: 'swFacilitySubTypeId',
          activeInd: 'activeInd'
        },
      },
      {
        name: 'SW Regulation Code',
        columnType: 'text',
        columnTitle: 'regulationCode',
      },
      {
        name: 'SW Facility Sub Type',
        columnType: 'textbox',
        columnTitle: 'facilitySubType',
        maxCharacter: 80,
      },
      {
        name: 'SW Sub Type Regulation Code',
        columnType: 'textbox',
        columnTitle: 'subTypeRegulationCode',
        maxCharacter: 10
      }
    ]
  },
  {
    columnLabel: 'SW Regulation',
    columnTitle: 'facilityTypeRegulationCode',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'SW Facility Sub Type',
    columnTitle: 'facilitySubType',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'SW Sub Type Regulation',
    columnTitle: 'facilitySubTypeRegulationCode',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '17%',
    bodyWidth: '17%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'editBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Edit',
    //optionals
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const GISLayerHeaders: any[] = [
  {
    columnLabel: 'Layer Name',
    columnTitle: 'layerName',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '24%',
    bodyWidth: '24%',
    filtersList: [],
    exportToExcel: false,
    maxCharacter: 75,
    isDisabled: false,
    columnType: "text",
    dropdownKey: 'description',
    mapperBinding: {},
    sendObj: {
      layerName: 'layerName',
      layerUrl: 'updatedLayerUrl',
      layerType: 'layerTypeVal',
      activeInd: 'active',
      orderInd: 'order'
    },
    editObj: {
      'layerName': 'layerName',
      'layerUrl': 'layerUrl',
      'layerType': 'layerType',
      'activeInd': 'activeInd',
      'orderInd': 'orderInd'
    },
    checkboxes: ['active'],
    addNewHeaders: [
      {
        name: 'Layer Name',
        columnType: 'textbox',
        columnTitle: 'layerName',
        maxCharacter: 50,
        sendObj: {
          layerName: 'layerName',
          layerUrl: 'layerUrl',
          layerType: 'layerType',
          activeInd: '',
          orderInd: '',
        },
      },
      {
        name: 'Layer URL',
        columnType: 'textbox',
        columnTitle: 'layerUrl',
        maxCharacter: 200,
      },
      {
        name: 'Layer Type',
        columnType: 'dropdown',
        columnTitle: 'layerType',
        tableName: 'e_gis_layer_config',
        tableData: [],
        dropdownValue: 'layerType',
        dropdownKey: 'layerType'
      }
    ],
    editHeaders: [
      {
        name: 'Layer Name',
        columnType: 'textbox',
        columnTitle: 'layerName',
        maxCharacter: 50,
        disableOnEdit: true,
        sendObj: {
          layerName: 'layerName',
          layerUrl: 'layerUrl',
          layerType: 'layerType',
          activeInd: 'activeInd',
          orderInd: 'orderInd',
        },
      },
      {
        name: 'Layer URL',
        columnType: 'textbox',
        columnTitle: 'layerUrl',
        maxCharacter: 200,
      },
      {
        name: 'Layer Type',
        columnType: 'dropdown',
        columnTitle: 'layerType',
        tableName: 'e_gis_layer_config',
        tableData: [],
        dropdownValue: 'layerType',
        dropdownKey: 'layerType'
      }
    ]
  },
  {
    columnLabel: 'Layer URL',
    columnTitle: 'layerUrl',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '22%',
    bodyWidth: '22%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
  },
  {
    columnLabel: 'Updated Layer URL',
    columnTitle: 'updatedLayerUrl',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '31%',
    bodyWidth: '31%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'textbox',
    maxCharacter: 200
  },
  {
    columnLabel: 'Layer Type',
    columnTitle: 'layerType',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '11%',
    bodyWidth: '11%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'dropdown',
    tableData: [],
    tableName: 'e_gis_layer_config',
    dropdownKey: 'layerType',
    dropdownValue: 'layerType'
  },
  {
    columnLabel: 'Order',
    columnTitle: 'order',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'textbox',
    maxCharacter: 2,
    pattern: '[0-9]'
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    oppTitle: 'depOnly',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: true,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
];

export const InvoiceFeeHeaders: any[] = [
  {
    columnLabel: 'Permit Type',
    columnTitle: 'permitTypeCode',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '13%',
    bodyWidth: '13%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
    checkboxes: ['active'],
    sendObj: {
      invoiceFeeDesc: 'invoiceFeeDesc',
      permitTypeCode: 'permitTypeCode',
      invoiceFeeType: 'invoiceFeeType',
      invoiceFee: 'invoiceFee',
      activeInd: 'active'
    },
    editObj: {
      invoiceFee: 'invoiceFee',
      permitTypeCode: 'permitTypeCode',
      invoiceFeeType: 'invoiceFeeType',
      invoiceFeeDesc: 'invoiceFeeDesc',
    },
    addNewHeaders: [
      {
        name: 'Permit Type',
        columnType: 'dropdown',
        columnTitle: 'permitTypeCode',
        tableData: [],
        tableName: 'e_permit_type_code',
        dropdownValue: 'permitTypeCode',
        dropdownKey: 'permitTypeCode',
        width: '20%',
        sendObj: {
          invoiceFeeDesc: 'invoiceFeeDesc',
          permitTypeCode: 'permitTypeCode',
          invoiceFeeType: 'invoiceFeeType',
          invoiceFee: 'invoiceFee',
          newInvoiceFee: true,
          activeInd: ''
        },
      },
      {
        name: 'Invoice Fee Type',
        columnType: 'textbox',
        columnTitle: 'invoiceFeeType',
        width: '20%',
        maxCharacter: 4
      },
      {
        name: 'Invoice Fee Description',
        columnType: 'textbox',
        columnTitle: 'invoiceFeeDesc',
        maxCharacter: 100
      },
      {
        name: 'Fee Amount',
        columnType: 'textbox',
        columnTitle: 'invoiceFee',
        width: '20%',
        maxCharacter: 5
      }
    ],
    editHeaders: [
      {
        name: 'Permit Type',
        columnType: 'dropdown',
        columnTitle: 'permitTypeCode',
        tableData: [],
        tableName: 'e_permit_type_code',
        dropdownValue: 'permitTypeCode',
        dropdownKey: 'permitTypeCode',
        width: '20%',
        disableOnEdit: true,
        sendObj: {
          invoiceFeeDesc: 'invoiceFeeDesc',
          permitTypeCode: 'permitTypeCode',
          invoiceFeeType: 'invoiceFeeType',
          invoiceFee: 'invoiceFee',
          activeInd: 'active'
        },
      },
      {
        name: 'Invoice Fee Type',
        columnType: 'textbox',
        columnTitle: 'invoiceFeeType',
        maxCharacter: 4,
        width: '20%',
        disableOnEdit: true
      },
      {
        name: 'Invoice Fee Description',
        columnType: 'textbox',
        columnTitle: 'invoiceFeeDesc',
        maxCharacter: 100
      },
      {
        name: 'Fee Amount',
        columnType: 'textbox',
        columnTitle: 'invoiceFee',
        width: '20%',
        maxCharacter: 5
      }
    ]
  },
  {
    columnLabel: 'Invoice Fee Type',
    columnTitle: 'invoiceFeeType',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '14%',
    bodyWidth: '14%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'Invoice Fee Description',
    columnTitle: 'invoiceFeeDesc',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '50%',
    bodyWidth: '50%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'Fee Amount',
    columnTitle: 'invoiceFee',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'editBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Edit',
    //optionals
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const ReqdDocFacilityTypeHeaders: any[] = [
  {
    columnLabel: 'Document Title',
    columnTitle: 'documentTitle',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '49%',
    bodyWidth: '49%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text',
    hasRadioButtons: true,
    checkboxes: ['active'],
    sendObj: {
      activeInd: 'active',
      documentTitleId: 'documentTitleId',
      swFacilityTypeId: 'facilityTypeId',
      swFacilitySubTypeId: 'facilitySubTypeId',
      reqdNew: 'reqdNew',
      reqdMod: 'reqdMod',
      reqdExt: 'reqdExt',
      reqdMnm: 'reqdMnm',
      reqdMtn: 'reqdMtn',
      reqdRen: 'reqdRen',
      reqdRtn: 'reqdRtn',
      reqdXfer: 'reqdXfer',
      uniqueId: 'uniqueId',
    },
    editObj: {
      facilityType: 'facilityTypeId',
      facilitySubType: 'facilitySubTypeId',
      documentTitle: 'documentTitleId'
    },
    addNewHeaders: [
      {
        name: 'SW Facility Type',
        columnType: 'dropdown',
        columnTitle: 'facilityType',
        tableData: [],
        tableName: 'e_sw_facility_type',
        dropdownValue: 'swFacilityTypeId',
        dropdownKey: 'facilityType',
        sendObj: {
          swFacilityTypeId: 'facilityType',
          swFacilitySubTypeId: 'facilitySubType',
          documentTitleId: 'documentTitle',
          reqdNew: 'reqdNew',
          reqdMod: 'reqdMod',
          reqdExt: 'reqdExt',
          reqdMnm: 'reqdMnm',
          reqdMtn: 'reqdMtn',
          reqdRen: 'reqdRen',
          reqdRtn: 'reqdRtn',
          reqdXfer: 'reqdXfer',
          uniqueId: '',
          activeInd: ''
        },
      },
      {
        name: 'SW Facility Sub Type',
        columnType: 'dropdown',
        columnTitle: 'facilitySubType',
        tableData: [],
        dropdownValue: 'swFacilitySubTypeId',
        dropdownKey: 'facilitySubType',
        optional: true
      },
      {
        name: 'Document Title',
        columnType: 'dropdown',
        columnTitle: 'documentTitle',
        tableData: [],
        tableName: 'e_document_title',
        dropdownValue: 'id',
        dropdownKey: 'description',
      }
    ],
    editHeaders: [
      {
        name: 'SW Facility Type',
        columnType: 'dropdown',
        columnTitle: 'facilityType',
        tableData: [],
        tableName: 'e_sw_facility_type',
        dropdownValue: 'swFacilityTypeId',
        dropdownKey: 'facilityType',
        disableOnEdit: true,
        sendObj: {
          swFacilityTypeId: 'facilityType',
          swFacilitySubTypeId: 'facilitySubTypeId',
          documentTitleId: 'documentTitle',
          reqdNew: 'reqdNew',
          reqdMod: 'reqdMod',
          reqdExt: 'reqdExt',
          reqdMnm: 'reqdMnm',
          reqdMtn: 'reqdMtn',
          reqdRen: 'reqdRen',
          reqdRtn: 'reqdRtn',
          reqdXfer: 'reqdXfer',
          activeInd: 'activeInd',
          uniqueId: 'uniqueId'
        },
      },
      {
        name: 'SW Facility Sub Type',
        columnType: 'dropdown',
        columnTitle: 'facilitySubType',
        disableOnEdit: true,
        dropdownValue: 'swFacilitySubTypeId',
        dropdownKey: 'facilitySubType',
        optional: true,
        tableData: []
      },
      {
        name: 'Document Title',
        columnType: 'dropdown',
        columnTitle: 'documentTitle',
        tableData: [],
        tableName: 'e_document_title',
        dropdownValue: 'id',
        dropdownKey: 'description',
        disableOnEdit: true
      }
    ]
  },
  {
    columnLabel: 'NEW',
    columnTitle: 'reqdNew',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MOD',
    columnTitle: 'reqdMod',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'EXT',
    columnTitle: 'reqdExt',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MNM',
    columnTitle: 'reqdMnm',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MTN',
    columnTitle: 'reqdMtn',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'REN',
    columnTitle: 'reqdRen',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'RTN',
    columnTitle: 'reqdRtn',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'XFER',
    columnTitle: 'reqdXfer',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'editBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Edit',
    //optionals
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const ReqdDocNaturalResourcesHeaders: any[] = [
  {
    columnLabel: 'Document Title',
    columnTitle: 'documentTitle',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '41%',
    bodyWidth: '41%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
    hasRadioButtons: true,
    checkboxes: ['active'],
    sendObj: {
      activeInd: 'active',
      documentTitleId: 'documentTitleId',
      reqdNew: 'reqdNew',
      reqdMod: 'reqdMod',
      reqdExt: 'reqdExt',
      reqdMnm: 'reqdMnm',
      reqdMtn: 'reqdMtn',
      reqdRen: 'reqdRen',
      reqdRtn: 'reqdRtn',
      reqdXfer: 'reqdXfer',
      uniqueId: 'uniqueId',
    },
    editObj: {
      documentTitle: 'documentTitleId'
    },
    addNewHeaders: [
      {
        name: 'Document Title',
        columnType: 'dropdown',
        columnTitle: 'documentTitle',
        tableData: [],
        tableName: 'e_document_title',
        dropdownValue: 'id',
        dropdownKey: 'description',
        sendObj: {
          documentTitleId: 'documentTitle',
          reqdNew: 'reqdNew',
          reqdMod: 'reqdMod',
          reqdExt: 'reqdExt',
          reqdMnm: 'reqdMnm',
          reqdMtn: 'reqdMtn',
          reqdRen: 'reqdRen',
          reqdRtn: 'reqdRtn',
          reqdXfer: 'reqdXfer',
          uniqueId: '',
          activeInd: 'activeInd'
        },
      }
    ],
    editHeaders: [
      {
        name: 'Document Title',
        columnType: 'dropdown',
        columnTitle: 'documentTitle',
        tableData: [],
        tableName: 'e_document_title',
        dropdownValue: 'id',
        dropdownKey: 'description',
        disableOnEdit: true,
        sendObj: {
          documentTitleId: 'documentTitle',
          reqdNew: 'reqdNew',
          reqdMod: 'reqdMod',
          reqdExt: 'reqdExt',
          reqdMnm: 'reqdMnm',
          reqdMtn: 'reqdMtn',
          reqdRen: 'reqdRen',
          reqdRtn: 'reqdRtn',
          reqdXfer: 'reqdXfer',
          uniqueId: 'uniqueId',
          activeInd: 'activeInd'
        },
      }
    ]
  },
  {
    columnLabel: 'NEW',
    columnTitle: 'reqdNew',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MOD',
    columnTitle: 'reqdMod',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'EXT',
    columnTitle: 'reqdExt',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MNM',
    columnTitle: 'reqdMnm',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MTN',
    columnTitle: 'reqdMtn',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'REN',
    columnTitle: 'reqdRen',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'RTN',
    columnTitle: 'reqdRtn',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'XFER',
    columnTitle: 'reqdXfer',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'editBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Edit',
    //optionals
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const ReqdDocPermitTypeHeaders: any[] = [
  {
    columnLabel: 'Document Title',
    columnTitle: 'documentTitle',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '41%',
    bodyWidth: '41%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
    hasRadioButtons: true,
    checkboxes: ['active'],
    editObj: {
      documentTitle: 'documentTitleId',
      permitTypeCode: 'permitTypeCode'
    },
    sendObj: {
      activeInd: 'active',
      documentTitleId: 'documentTitleId',
      permitTypeCode: 'permitTypeCode',
      reqdNew: 'reqdNew',
      reqdMod: 'reqdMod',
      reqdExt: 'reqdExt',
      reqdMnm: 'reqdMnm',
      reqdMtn: 'reqdMtn',
      reqdRen: 'reqdRen',
      reqdRtn: 'reqdRtn',
      reqdXfer: 'reqdXfer',
      uniqueId: 'uniqueId',
    },
    addNewHeaders: [
      {
        name: 'Permit Type',
        columnType: 'dropdown',
        columnTitle: 'permitTypeCode',
        tableData: [],
        tableName: 'e_permit_type_code',
        dropdownValue: 'permitTypeCode',
        dropdownKey: 'permitTypeDescription',
        sendObj: {
          documentTitleId: 'documentTitle',
          permitTypeCode: 'permitTypeCode',
          reqdNew: 'reqdNew',
          reqdMod: 'reqdMod',
          reqdExt: 'reqdExt',
          reqdMnm: 'reqdMnm',
          reqdMtn: 'reqdMtn',
          reqdRen: 'reqdRen',
          reqdRtn: 'reqdRtn',
          reqdXfer: 'reqdXfer',
          uniqueId: '',
          activeInd: ''
        },
      },
      {
        name: 'Document Title',
        columnType: 'dropdown',
        columnTitle: 'documentTitle',
        tableData: [],
        tableName: 'e_document_title',
        dropdownValue: 'id',
        dropdownKey: 'description',
      }
    ],
    editHeaders: [
      {
        name: 'Permit Type',
        columnType: 'dropdown',
        columnTitle: 'permitTypeCode',
        tableData: [],
        tableName: 'e_permit_type_code',
        dropdownValue: 'permitTypeCode',
        dropdownKey: 'permitTypeDescription',
        disableOnEdit: true,
        sendObj: {
          documentTitleId: 'documentTitle',
          permitTypeCode: 'permitTypeCode',
          reqdNew: 'reqdNew',
          reqdMod: 'reqdMod',
          reqdExt: 'reqdExt',
          reqdMnm: 'reqdMnm',
          reqdMtn: 'reqdMtn',
          reqdRen: 'reqdRen',
          reqdRtn: 'reqdRtn',
          reqdXfer: 'reqdXfer',
          uniqueId: 'uniqueId',
          activeInd: 'activeInd'
        },
      },
      {
        name: 'Document Title',
        columnType: 'dropdown',
        columnTitle: 'documentTitle',
        tableData: [],
        tableName: 'e_document_title',
        dropdownValue: 'id',
        dropdownKey: 'description',
        disableOnEdit: true
      }
    ]
  },
  {
    columnLabel: 'NEW',
    columnTitle: 'reqdNew',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MOD',
    columnTitle: 'reqdMod',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'EXT',
    columnTitle: 'reqdExt',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MNM',
    columnTitle: 'reqdMnm',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MTN',
    columnTitle: 'reqdMtn',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'REN',
    columnTitle: 'reqdRen',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'RTN',
    columnTitle: 'reqdRtn',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'XFER',
    columnTitle: 'reqdXfer',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'editBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Edit',
    //optionals
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const ReqdDocSeqrHeaders: any[] = [
  {
    columnLabel: 'Document Title',
    columnTitle: 'documentTitle',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '41%',
    bodyWidth: '41%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
    hasRadioButtons: true,
    checkboxes: ['active'],
    sendObj: {
      activeInd: 'active',
      documentTitleId: 'documentTitleId',
      reqdNew: 'reqdNew',
      reqdMod: 'reqdMod',
      reqdExt: 'reqdExt',
      reqdMnm: 'reqdMnm',
      reqdMtn: 'reqdMtn',
      reqdRen: 'reqdRen',
      reqdRtn: 'reqdRtn',
      reqdXfer: 'reqdXfer',
      uniqueId: 'uniqueId',
    },
    editObj: {
      documentTitle: 'documentTitleId'
    },
    addNewHeaders: [
      {
        name: 'Document Title',
        columnType: 'dropdown',
        columnTitle: 'documentTitle',
        tableData: [],
        tableName: 'e_document_title',
        dropdownValue: 'id',
        dropdownKey: 'description',
        sendObj: {
          documentTitleId: 'documentTitle',
          reqdNew: 'reqdNew',
          reqdMod: 'reqdMod',
          reqdExt: 'reqdExt',
          reqdMnm: 'reqdMnm',
          reqdMtn: 'reqdMtn',
          reqdRen: 'reqdRen',
          reqdRtn: 'reqdRtn',
          reqdXfer: 'reqdXfer',
          uniqueId: '',
          activeInd: ''
        },
      }
    ],
    editHeaders: [
      {
        name: 'Document Title',
        columnType: 'dropdown',
        columnTitle: 'documentTitle',
        tableData: [],
        tableName: 'e_document_title',
        dropdownValue: 'id',
        dropdownKey: 'description',
        disableOnEdit: true,
        sendObj: {
          documentTitleId: 'documentTitle',
          reqdNew: 'reqdNew',
          reqdMod: 'reqdMod',
          reqdExt: 'reqdExt',
          reqdMnm: 'reqdMnm',
          reqdMtn: 'reqdMtn',
          reqdRen: 'reqdRen',
          reqdRtn: 'reqdRtn',
          reqdXfer: 'reqdXfer',
          uniqueId: 'uniqueId',
          activeInd: 'activeInd'
        },
      }
    ]
  },
  {
    columnLabel: 'NEW',
    columnTitle: 'reqdNew',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MOD',
    columnTitle: 'reqdMod',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'EXT',
    columnTitle: 'reqdExt',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MNM',
    columnTitle: 'reqdMnm',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MTN',
    columnTitle: 'reqdMtn',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'REN',
    columnTitle: 'reqdRen',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'RTN',
    columnTitle: 'reqdRtn',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'XFER',
    columnTitle: 'reqdXfer',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'editBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Edit',
    //optionals
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const ReqdDocShpaHeaders: any[] = [
  {
    columnLabel: 'Document Title',
    columnTitle: 'documentTitle',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '41%',
    bodyWidth: '41%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
    hasRadioButtons: true,
    checkboxes: ['active'],
    sendObj: {
      activeInd: 'active',
      documentTitleId: 'documentTitleId',
      reqdNew: 'reqdNew',
      reqdMod: 'reqdMod',
      reqdExt: 'reqdExt',
      reqdMnm: 'reqdMnm',
      reqdMtn: 'reqdMtn',
      reqdRen: 'reqdRen',
      reqdRtn: 'reqdRtn',
      reqdXfer: 'reqdXfer',
      uniqueId: 'uniqueId',
    },
    editObj: {
      documentTitle: 'documentTitleId'
    },
    addNewHeaders: [
      {
        name: 'Document Title',
        columnType: 'dropdown',
        columnTitle: 'documentTitle',
        tableData: [],
        tableName: 'e_document_title',
        dropdownValue: 'id',
        dropdownKey: 'description',
        sendObj: {
          documentTitleId: 'documentTitle',
          reqdNew: 'reqdNew',
          reqdMod: 'reqdMod',
          reqdExt: 'reqdExt',
          reqdMnm: 'reqdMnm',
          reqdMtn: 'reqdMtn',
          reqdRen: 'reqdRen',
          reqdRtn: 'reqdRtn',
          reqdXfer: 'reqdXfer',
          uniqueId: 'uniqueId',
          activeInd: ''
        },
      }
    ],
    editHeaders: [
      {
        name: 'Document Title',
        columnType: 'dropdown',
        columnTitle: 'documentTitle',
        tableData: [],
        tableName: 'e_document_title',
        dropdownValue: 'id',
        dropdownKey: 'description',
        disableOnEdit: true,
        sendObj: {
          documentTitleId: 'documentTitle',
          reqdNew: 'reqdNew',
          reqdMod: 'reqdMod',
          reqdExt: 'reqdExt',
          reqdMnm: 'reqdMnm',
          reqdMtn: 'reqdMtn',
          reqdRen: 'reqdRen',
          reqdRtn: 'reqdRtn',
          reqdXfer: 'reqdXfer',
          activeInd: 'activeInd',
          uniqueId: 'uniqueId',
        },
      }
    ]
  },
  {
    columnLabel: 'NEW',
    columnTitle: 'reqdNew',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MOD',
    columnTitle: 'reqdMod',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'EXT',
    columnTitle: 'reqdExt',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MNM',
    columnTitle: 'reqdMnm',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'MTN',
    columnTitle: 'reqdMtn',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'REN',
    columnTitle: 'reqdRen',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'RTN',
    columnTitle: 'reqdRtn',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'XFER',
    columnTitle: 'reqdXfer',
    isFilter: false,
    isSort: false,
    isInput: true,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    columnType: 'text'
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'editBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Edit',
    //optionals
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const ReqdDocGiHeaders: any[] = [
  {
    columnLabel: 'GI Category Code',
    columnTitle: 'spatialInqCategoryCode',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '39%',
    bodyWidth: '39%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
    checkboxes: ['reqdDoc', 'active'],
    editObj: {
      docSubTypeTitle: 'docSubTypeTitleId',
      spatialInqCategoryCode: 'spatialInqCategoryCode'
    },
    sendObj: {
      reqdDocGiId: 'reqdDocGiId',
      spatialInqCategoryCode: 'spatialInqCategoryCode',
      docSubTypeTitleId: 'docSubTypeTitleId',
      reqdDocInd: 'reqdDoc',
      activeInd: 'active'
    },
    addNewHeaders: [
      {
        name: 'GI Category',
        columnType: 'dropdown',
        columnTitle: 'spatialInqCategoryCode',
        tableData: [],
        tableName: 'e_spatial_inq_category',
        dropdownValue: 'spatialInqCategoryCode',
        dropdownKey: 'spatialInqCategoryCode',
        sendObj: {
          docSubTypeTitleId: 'docSubTypeTitle',
          spatialInqCategoryCode: 'spatialInqCategoryCode',
          reqdDocInd: '',
          reqdDocGiId: '',
          activeInd: ''
        }

      },
      {
        name: 'Doc Sub Type Title',
        columnType: 'dropdown',
        columnTitle: 'docSubTypeTitle',
        tableData: [],
        tableName: 'e_document_sub_type_title',
        dropdownValue: 'documentSubTypeTitleId',
        dropdownKey: 'documentTitle',
      }
    ],
    editHeaders: [
      {
        name: 'GI Category',
        columnType: 'dropdown',
        columnTitle: 'spatialInqCategoryCode',
        tableData: [],
        tableName: 'e_spatial_inq_category',
        dropdownValue: 'spatialInqCategoryCode',
        dropdownKey: 'spatialInqCategoryCode',
        sendObj: {
          docSubTypeTitleId: 'docSubTypeTitle',
          spatialInqCategoryCode: 'spatialInqCategoryCode',
          reqdDocInd: 'reqdDocInd',
          reqdDocGiId: 'reqdDocGiId',
          activeInd: 'activeInd'
        }

      },
      {
        name: 'Doc Sub Type Title',
        columnType: 'dropdown',
        columnTitle: 'docSubTypeTitle',
        tableData: [],
        tableName: 'e_document_sub_type_title',
        dropdownValue: 'documentSubTypeTitleId',
        dropdownKey: 'documentTitle',
      }
    ]
  },
  {
    columnLabel: 'Doc Sub Type Title',
    columnTitle: 'docSubTypeTitle',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '41%',
    bodyWidth: '41%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
  },
  {
    columnLabel: 'Mandatory',
    columnTitle: 'reqdDoc',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '9%',
    bodyWidth: '9%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'editBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Edit',
    //optionals
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },
];

export const TransTypeRuleHeaders: any[] = [
  {
    columnLabel: 'Permit Type',
    columnTitle: 'permitTypeCode',
    isFilter: true,
    isSort: true,
    //optionals
    headerWidth: '16%',
    bodyWidth: '16%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
    checkboxes: ['userSelNew', 'userSelMod', 'userSelExt', 'userSelTransfer', 'userSelRen',
      'chgOriginalProject', 'modExtForm', 'active'],
    editObj: {
      permitTypeCode: 'permitTypeCode',
      transTypeCode: 'transTypeCode',
      userSelNew: 'userSelNew',
      userSelMod: 'userSelMod',
      userSelExt: 'userSelExt',
      userSelTransfer: 'userSelTransfer',
      userSelRen: 'userSelRen',
      chgOriginalProjectInd: 'chgOriginalProjectInd',
      supportDocTransType: 'supportDocTransType',
      modExtForm: 'modExtForm',
    },
    sendObj: {
      transactionTypeRuleId: 'transactionTypeRuleId',
      permitTypeCode: 'permitTypeCode',
      transTypeCode: 'transTypeCode',
      userSelNewInd: 'userSelNew',
      userSelModInd: 'userSelMod',
      userSelExtInd: 'userSelExt',
      userSelTransferInd: 'userSelTransfer',
      userSelRenInd: 'userSelRen',
      chgOriginalProjectInd: 'chgOriginalProject',
      supportDocTransType: 'supportDocTransType',
      modExtFormInd: 'modExtForm',
      activeInd: 'active'
    },
    addNewHeaders: [
      {
        name: 'Permit Type',
        columnType: 'dropdown',
        columnTitle: 'permitTypeCode',
        tableData: [],
        tableName: 'e_permit_type_code',
        dropdownValue: 'permitTypeCode',
        dropdownKey: 'permitTypeCode',
        sendObj: {
          transactionTypeRuleId: '',
          permitTypeCode: 'permitTypeCode',
          transTypeCode: 'transTypeCode',
          userSelNewInd: 'userSelNew',
          userSelModInd: 'userSelMod',
          userSelExtInd: 'userSelExt',
          userSelTransferInd: 'userSelTransfer',
          userSelRenInd: 'userSelRen',
          chgOriginalProjectInd: 'chgOriginalProjectInd',
          supportDocTransType: 'supportDocTransType',
          modExtFormInd: 'modExtForm',
          activeInd: ''
        }

      },
      {
        name: 'Selected NEW',
        columnType: 'checkbox',
        columnTitle: 'userSelNew',
        optional: true
      },
      {
        name: 'Selected MOD',
        columnType: 'checkbox',
        columnTitle: 'userSelMod',
        optional: true
      },
      {
        name: 'Selected EXT',
        columnType: 'checkbox',
        columnTitle: 'userSelExt',
        optional: true
      },
      {
        name: 'Selected XFER',
        columnType: 'checkbox',
        columnTitle: 'userSelTransfer',
        optional: true
      },
      {
        name: 'Selected MOD',
        columnType: 'checkbox',
        columnTitle: 'userSelRen',
        optional: true
      },
      {
        name: 'Material Change',
        columnType: 'radioButton',
        columnTitle: 'chgOriginalProjectInd',
        optional: true
      },
      {
        name: 'DART APP TYPE (translation)',
        columnType: 'dropdown',
        columnTitle: 'transTypeCode',
        tableData: [
          {edbTransTypeCode: 'MOD'},
          {edbTransTypeCode: 'MTN'},
          {edbTransTypeCode: 'NEW'},
          {edbTransTypeCode: 'REI'},
          {edbTransTypeCode: 'REN'},
          {edbTransTypeCode: 'RTN'}
        ]
      },
      {
        name: 'Supporting Doc Type(s)',
        columnType: 'dropdown',
        columnTitle: 'supportDocTransType',
        tableData: [
          {supportDocTransType: 'EXT'},
          {supportDocTransType: 'MOD'},
          {supportDocTransType: 'MOD and XFER'},
          {supportDocTransType: 'MTN'},
          {supportDocTransType: 'MTN and XFER'},
          {supportDocTransType: 'NEW'},
          {supportDocTransType: 'REN'},
          {supportDocTransType: 'REN and XFER'},
          {supportDocTransType: 'RTN'},
          {supportDocTransType: 'RTN and XFER'},
          {supportDocTransType: 'XFER'},
          {supportDocTransType: 'XFER and EXT'},
        ]
      },
      {
        name: 'MOD/EXT Form',
        columnType: 'checkbox',
        columnTitle: 'modExtForm',
        optional: true
      },
    ],
    editHeaders: [
      {
        name: 'Permit Type',
        columnType: 'dropdown',
        columnTitle: 'permitTypeCode',
        tableData: [],
        tableName: 'e_permit_type_code',
        dropdownValue: 'permitTypeCode',
        dropdownKey: 'permitTypeCode',
        sendObj: {
          transactionTypeRuleId: 'transactionTypeRuleId',
          permitTypeCode: 'permitTypeCode',
          transTypeCode: 'transTypeCode',
          userSelNewInd: 'userSelNew',
          userSelModInd: 'userSelMod',
          userSelExtInd: 'userSelExt',
          userSelTransferInd: 'userSelTransfer',
          userSelRenInd: 'userSelRen',
          chgOriginalProjectInd: 'chgOriginalProjectInd',
          supportDocTransType: 'supportDocTransType',
          modExtFormInd: 'modExtForm',
          activeInd: 'activeInd'
        }

      },
      {
        name: 'Selected NEW',
        columnType: 'checkbox',
        columnTitle: 'userSelNew',
        optional: true
      },
      {
        name: 'Selected MOD',
        columnType: 'checkbox',
        columnTitle: 'userSelMod',
        optional: true
      },
      {
        name: 'Selected EXT',
        columnType: 'checkbox',
        columnTitle: 'userSelExt',
        optional: true
      },
      {
        name: 'Selected XFER',
        columnType: 'checkbox',
        columnTitle: 'userSelTransfer',
        optional: true
      },
      {
        name: 'Selected MOD',
        columnType: 'checkbox',
        columnTitle: 'userSelRen',
        optional: true
      },
      {
        name: 'Material Change',
        columnType: 'radioButton',
        columnTitle: 'chgOriginalProjectInd',
        optional: true
      },
      {
        name: 'DART APP TYPE (translation)',
        columnType: 'dropdown',
        columnTitle: 'transTypeCode',
        tableData: [
          {edbTransTypeCode: 'MOD'},
          {edbTransTypeCode: 'MTN'},
          {edbTransTypeCode: 'NEW'},
          {edbTransTypeCode: 'REI'},
          {edbTransTypeCode: 'REN'},
          {edbTransTypeCode: 'RTN'}
        ]
      },
      {
        name: 'Supporting Doc Type(s)',
        columnType: 'dropdown',
        columnTitle: 'supportDocTransType',
        tableData: [
          {supportDocTransType: 'EXT'},
          {supportDocTransType: 'MOD'},
          {supportDocTransType: 'MOD and XFER'},
          {supportDocTransType: 'MTN'},
          {supportDocTransType: 'MTN and XFER'},
          {supportDocTransType: 'NEW'},
          {supportDocTransType: 'REN'},
          {supportDocTransType: 'REN and XFER'},
          {supportDocTransType: 'RTN'},
          {supportDocTransType: 'RTN and XFER'},
          {supportDocTransType: 'XFER'},
          {supportDocTransType: 'XFER and EXT'},
        ]
      },
      {
        name: 'MOD/EXT Form',
        columnType: 'checkbox',
        columnTitle: 'modExtForm',
        optional: true
      },
    ]
  },
  {
    columnLabel: 'Selected NEW',
    columnTitle: 'userSelNew',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '7%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: true,
  },
  {
    columnLabel: 'Selected MOD',
    columnTitle: 'userSelMod',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '7%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: true,
  },
  {
    columnLabel: 'Selected EXT',
    columnTitle: 'userSelExt',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '7%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: true,
  },
  {
    columnLabel: 'Selected XFER',
    columnTitle: 'userSelTransfer',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '7%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: true,
  },
  {
    columnLabel: 'Selected REN',
    columnTitle: 'userSelRen',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '7%',
    bodyWidth: '7%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: true,
  },
  {
    columnLabel: 'Material Change',
    columnTitle: 'chgOriginalProject',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '8%',
    bodyWidth: '8%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: true,
  },
  {
    columnLabel: 'Translated Trans Type',
    columnTitle: 'transTypeCode',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '10%',
    bodyWidth: '10%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
  },
  {
    columnLabel: 'Support Doc Type',
    columnTitle: 'supportDocTransType',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '12%',
    bodyWidth: '12%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    columnType: 'text',
  },
  {
    columnLabel: 'MOD/EXT Form',
    columnTitle: 'modExtForm',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '8%',
    bodyWidth: '8%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: true,
  },
  {
    columnLabel: 'Active',
    columnTitle: 'active',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '6%',
    bodyWidth: '6%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: '75',
    columnType: 'checkbox',
    isDisabled: false,
  },
  {
    columnLabel: '',
    columnTitle: 'editBtn',
    isFilter: false,
    isSort: false,
    isButton: 'Edit',
    //optionals
    headerWidth: '5%',
    bodyWidth: '5%',
    filtersList: [],
    exportToExcel: true,
  },
];
// PermitType URL Ref Link Headers

export const PermitTypeURLHeaders: any[] = [
  {
    columnLabel: 'Permit Type',
    columnTitle: 'permitTypeDescription',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '20%',
    bodyWidth: '20%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: 25,
    columnType: 'text',
    isDisabled: false,
    dropdownKey: 'permitTypeDesc',
    mapperBinding: {
      urlLink: 'refLink',
      updatedURLLinkValue:'refLink',
    },
    sendObj: {
      "activeInd": "",
      "generalPermitInd": "",
      "natResInd": "",
      "permitCategoryDesc": "",
      "permitCategoryId": "",
      "permitTypeCode": 'permitTypeCode',
      "permitTypeDesc": "",
      "refUrl": 'updatedURLLinkValue',
      "relatedRegularPermitTypeCodeForGp": "",
      "relatedRegularPermitTypeDescForGp": ""
    },
    addNewHeaders: [
      {
        name: 'permitType',
        columnType: 'textbox',
        columnTitle: 'urlId',
        maxCharacter: 25,
        titleName: 'System Parameter',
        sendObj: {
          uniquekey: 'urlId',
          value: 'urlLink',
        },
      },
      {
        name: 'URL Link',
        columnType: 'textbox',
        maxCharacter: 150,
        columnTitle: 'urlLink'
      }
    ]
   },
  {
    columnLabel: 'URL Link',
    columnTitle: 'refLink',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: false,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: 150,
    columnType: 'text',
    isDisabled: true,
  },
  {
    columnLabel: 'Updated URL Link(defaults to original)',
    columnTitle: 'updatedURLLinkValue',
    isFilter: false,
    isSort: false,
    //optionals
    headerWidth: '30%',
    bodyWidth: '30%',
    filtersList: [],
    exportToExcel: true,
    isSearchableDropdown: true,
    searchableDropdownWidth: '24rem',
    maxCharacter: 150,
    columnType: 'textbox',
    isDisabled: false,
  },
];

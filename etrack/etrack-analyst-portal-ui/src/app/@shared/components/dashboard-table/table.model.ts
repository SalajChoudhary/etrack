export interface PTableHeader{
    columnLabel: string,
    columnTitle: string,
    // [key:string]:any,
     //optionals
    isSetLocal?: boolean,
    isFilter?: boolean,
    isSort?: boolean,
    isButton?: string,
    isLink?: boolean,
    linkToNavigate?: string,
    headerWidth?: string,
    bodyWidth?: string,
    filtersList?: PTableFilterList[],
    exportToExcel?: boolean,
    isCheckBox?:boolean,
    isDate?:boolean,
    isDateRed?:boolean,
    isExportIcon?: boolean,
    isClearFilterIcon?: boolean,
    isAction?:boolean,
    className?:string,
    isDelete?:boolean,
    isEdit?:boolean,
    isAssign?:boolean,
    align?:'left' | 'center' |'right',
    alignLabel?:'left' | 'center' | 'right'
    desc?:string;
    deleteBodyText?:string;
    secondLineDeleteBodyText?:string;
    isRejected?:boolean;
    isReject?:boolean;
    isSearchableDropdown?: boolean;
    searchableDropdownWidth? : string;
    isInput? : boolean;
    maxCharacter? : string;
}

export interface PTableFilterList{
    label:string;
    value:string;
}
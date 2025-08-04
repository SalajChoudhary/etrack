export const CodeTables = {
    "documents": [{
        'tableName': 'Document Type',
        'columnNames': ['Document Type Description'],
        'checkboxValues' : null,        
    },
    {
        'tableName': 'Document Sub Type',
        'columnNames': ['Document Sub Type Description'],
        'checkboxValues' : null,        
    },
    {
        'tableName': 'Document Title',
        'columnNames': ['Document Title'],
        'checkboxValues' : null,        
    },
    {
        'tableName': 'Document Sub Type Title',
        'columnNames': ['Document Sub Type Description'],
        'checkboxValues' : null,        
    },
],
"systemParamters":[{
    'tableName': 'System Parameters',
    'columnNames': ['Unique ID','Exisiting Value', 'Updated Value'],
    'checkboxValues' : null,        
}],
"requiredDocuments": [
    {
        'tableName': 'Required Document For Facility Type',
        'columnNames': ['Required Doc For Facility Type', 'Required Doc For SW Facility Type','Required Doc For Sub Type', 'Required Doc For SW Facility Type'],
        'checkboxValues' : ['Required NEW', 'Required MOD', 'Required EXT', 'Required MNM', 'Required MTN', 'Required REN', 'Required RTN', 'Required XFER'],        
    },
    {
        'tableName': 'Required Document For Natural GP',
        'columnNames': ['Required Doc For Natural GP', 'Document Sub Type Title'],
        'checkboxValues' : ['Required NEW', 'Required MOD', 'Required EXT', 'Required MNM', 'Required MTN', 'Required REN', 'Required RTN', 'Required XFER'],        
    },
    {
        'tableName': 'Required Document For Permit Type',
        'columnNames': ['Required Doc For Permit Type', 'Permit Type Code', 'Required Sub Type For Permit Type', 'Required Sub Type Title'],
        'checkboxValues' : ['Required NEW', 'Required MOD', 'Required EXT', 'Required MNM', 'Required MTN', 'Required REN', 'Required RTN', 'Required XFER'],        
    },
    {
        'tableName': 'Required Document For SEQR',
        'columnNames': ['Required Doc For SEQR', 'Document Sub Type Title'],
        'checkboxValues' : ['Required NEW', 'Required MOD', 'Required EXT', 'Required MNM', 'Required MTN', 'Required REN', 'Required RTN', 'Required XFER'],        
    },
    {
        'tableName': 'Required Document For SHPA',
        'columnNames': ['Required Doc For SHPA', 'Document Sub Type Title'],
        'checkboxValues' : ['Required NEW', 'Required MOD', 'Required EXT', 'Required MNM', 'Required MTN', 'Required REN', 'Required RTN', 'Required XFER'],        
    },
    {
        'tableName': 'Required Document For Spatial Inq',
        'columnNames': ['Required Doc For GI', 'Document Sub Type Title', 'GI Category Code'],
        'checkboxValues' : ['Required Indicator'],        
    },
],
'errorMessage': [
    {
        'tableName': 'Message',
        'columnNames': ['Message Description'],
        'checkboxValues' : null
    },
],
'swFacilityTypes': [
    {
        'tableName': 'SW Facility Type',
        'columnNames': ['Facility Type Description', 'Regulation Code'],
        'checkboxValues' : null,        
    },
    {
        'tableName': 'SW Facility Sub Type',
        'columnNames': ['Sub Type Description', 'Regulation Code'],
        'checkboxValues' : null,        
    },
],
'gisLayers': [
    {
        'tableName': 'GIS Layer Config',
        'columnNames': ['Layer Name','Layer URL', 'Layer Type'],
        'checkboxValues' : null,        
    },
],
'permitTypeFees': [
    {
        'tableName': 'Invoice Fee Type',
        'columnNames': ['Facility Invoice Fee Type Description', 'Invoice Fee Description', 'Invoice Fee', 'Permit Type Code'],
        'checkboxValues' : null,        
    },
]
}
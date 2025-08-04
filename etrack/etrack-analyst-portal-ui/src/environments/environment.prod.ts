// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: true,

  //production
  clientId:'aefb4072-9c2b-4156-9e39-d711275da9e9',
  authUrl: 'https://fs.svc.ny.gov/adfs',
  redirectUri: 'https://etrackanalyst.dec.ny.gov/callback',
  logoutRedirectUrl: 'https://etrackanalyst.dec.ny.gov',
  apiBaseurl:'https://etrackservice.dec.ny.gov',
  paymentUrl: 'https://devel.vpsenv.com/apipaymentQA/Payment/currentsession/',

  //GIS Urls
  printServiceUrl:'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/eTrack_ExportWebMap/GPServer/Export%20Web%20Map',
  decRegionsUrl: 'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/DEC_RegionBoundary_Admin/MapServer/0',
  decRegionsQuery: 'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/DEC_RegionBoundary_Admin/MapServer/0/query',
  facilityLayer:'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/eTrack_facility_poly/FeatureServer/0',
  facilityQuery: 'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/eTrack_facility_poly/FeatureServer/0/query',
  eFindFacilityLayer: 'https://gisinternal.dec.ny.gov/arcgis/rest/services/eFind/eFind_facility_poly/FeatureServer/0',
  efindUrl:'https://internal1.dec.state.ny.us/efind/public/',

  applicantScratchPolyUrl:'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/eTrack_applicant_scratch_poly/FeatureServer/0',
  submittedPolygonUrl:'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/eTrack_applicant_submittal_poly/FeatureServer/0',
  analystScratchPolyUrl:'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/eTrack_analyst_poly/FeatureServer/0',
  countiesQuery:'https://gisservices.its.ny.gov/arcgis/rest/services/NYS_Civil_Boundaries/FeatureServer/2/query',
  municipalitiesQuery:'https://gisservices.its.ny.gov/arcgis/rest/services/NYS_Civil_Boundaries/FeatureServer/6/query',
  municipalitiesUrl:'https://gisservices.its.ny.gov/arcgis/rest/services/NYS_Civil_Boundaries/FeatureServer/6',
  municipalitiesDECQuery: 'https://gisinternal.dec.ny.gov/arcgis/rest/services/eFind/reference_layers/MapServer/6/query',

  //Juristrictional Layers
  Other_DEC_Program_Consultation_Layers:'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/Other_DEC_Program_Consultation_Layers/MapServer',
  Other_Agency_Coordination_Layers:'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/Other_Agency_Coordination_Layers/MapServer',
  Layers_Affecting_DEC_Jurisdiction:'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/Layers_Affecting_DEC_Jurisdiction/MapServer',
  Jurisdictional_Layers:'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/Jurisdictional_Layers/MapServer',
  NYSMaskLayer:'https://gisservices.dec.ny.gov/arcgis/rest/services/EAF/EAF_Mapper/MapServer/0',
  inquiryPolyUrl:'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/eTrack_spatial_inquiry_poly/FeatureServer/0',
  //etrack-Prod*/
  taxParcelUrl:'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/nys_tax_parcels_agencies_etrack/MapServer/0',
  taxParcelQuery: 'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/nys_tax_parcels_agencies_etrack/MapServer/0/query',
// etrack LRP
lrpUrl: 'https://internal1.dec.state.ny.us/efind/public/',
facilityNameUrl:'https://internal1.dec.state.ny.us/efind/site/',
ajaxCallTime:300000,

// inactive session timeout in ms
sessionTimeout: 3600000,

  version:'3.3.12',
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.

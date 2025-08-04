// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
    production: false,

    //qa
    clientId:'bbac41fb-914c-4609-981f-aeca74aee244',
    authUrl: 'https://fs.svc.ny.gov/adfs',
    redirectUri: 'https://etrackanalyst-qa.dec.ny.gov/callback',
    logoutRedirectUrl: 'https://etrackanalyst-qa.dec.ny.gov',
    apiBaseurl:'https://etrackservice-qa.dec.ny.gov',
    paymentUrl: 'https://devel.vpsenv.com/apipaymentQA/Payment/currentsession/',

     //prod
    // clientId: '777e714a-20d7-45a5-b92a-4cf1be44f5eb',
    // authUrl: 'https://fs.svc.ny.gov/adfs',
    //  redirectUri: "https://etrackdms-qa.dec.ny.gov/callback",
    //  logoutRedirectUrl: 'https://etrackdms-qa.dec.ny.gov',
 //GIS Urls
 printServiceUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/eTrack_ExportWebMap/GPServer/Export%20Web%20Map',
 decRegionsUrl: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/DEC_RegionBoundary_Admin/MapServer/0',
 decRegionsQuery: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/DEC_RegionBoundary_Admin/MapServer/0/query',
 facilityLayer:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/eTrack_facility_poly/FeatureServer/0',
 facilityQuery: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/eTrack_facility_poly/FeatureServer/0/query',
 eFindFacilityLayer: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eFind_test/eFind_facility_poly/FeatureServer/0',
 efindUrl:'https://internal1.dec.state.ny.us/efind/public/',

 inquiryPolyUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/eTrack_spatial_inquiry_poly/FeatureServer/0',
 applicantScratchPolyUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/eTrack_applicant_scratch_poly/FeatureServer/0',
 submittedPolygonUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/eTrack_applicant_submittal_poly/FeatureServer/0',
 analystScratchPolyUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/eTrack_analyst_poly/FeatureServer/0',
 countiesQuery:'https://gisservices.its.ny.gov/arcgis/rest/services/NYS_Civil_Boundaries/FeatureServer/2/query',
 municipalitiesQuery:'https://gisservices.its.ny.gov/arcgis/rest/services/NYS_Civil_Boundaries/FeatureServer/6/query',
 municipalitiesUrl:'https://gisservices.its.ny.gov/arcgis/rest/services/NYS_Civil_Boundaries/FeatureServer/6',
 municipalitiesDECQuery: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eFind_test/reference_layers/MapServer/6/query',

 //Juristrictional Layers
 Other_DEC_Program_Consultation_Layers:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/Other_DEC_Program_Consultation_Layers/MapServer',
 Other_Agency_Coordination_Layers:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/Other_Agency_Coordination_Layers/MapServer',
 Layers_Affecting_DEC_Jurisdiction:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/Layers_Affecting_DEC_Jurisdiction/MapServer',
 Jurisdictional_Layers:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/Jurisdictional_Layers/MapServer',
 NYSMaskLayer:'https://gisservices-test.dec.ny.gov/arcgis/rest/services/EAF/EAF_Mapper/MapServer/0',

 //etrack-ua*/
 taxParcelUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/nys_tax_parcels_agencies_etrack/MapServer/0',
 taxParcelQuery: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/nys_tax_parcels_agencies_etrack/MapServer/0/query',

//  etrack LRP
lrpUrl: 'https://dectest.dec.state.ny.us/efind/public/',
facilityNameUrl:'https://dectest.dec.state.ny.us/efind/site/',
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

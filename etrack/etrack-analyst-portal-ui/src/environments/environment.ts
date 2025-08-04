// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
   apiBaseurl:'https://etrackservice-dev.dec.ny.gov',
   sessionTimeout: 6000000,
   //apiBaseurl: 'http://localhost:4200',
  //dev



  //local
  clientId:'d2b1a867-b86f-4fd2-a092-9d39d7dba3a8',
  logoutRedirectUrl: 'http://localhost:4200',
  redirectUri: 'http://localhost:4200/callback',
  authUrl: 'https://adfsdev.svc.ny.gov/adfs',
  paymentUrl: 'https://devel.vpsenv.com/apipaymentQA/Payment/currentsession/',



  // production: true,

  //   //staging
  //   clientId:'b2108a0b-0115-444d-8b7e-066652e41253',
  //   authUrl: 'https://fs.svc.ny.gov/adfs',
  //   redirectUri: 'https://etrackanalyst-st.dec.ny.gov/callback',
  //   logoutRedirectUrl: 'https://etrackanalyst-st.dec.ny.gov',
  //   apiBaseurl:'https://etrackservice-st.dec.ny.gov'

//GIS Urls
 printServiceUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/eTrack_ExportWebMap/GPServer/Export%20Web%20Map',
 decRegionsUrl: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/DEC_RegionBoundary_Admin/MapServer/0',
 decRegionsQuery: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/DEC_RegionBoundary_Admin/MapServer/0/query',
 facilityLayer:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/eTrack_facility_poly/FeatureServer/0',
 facilityQuery: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/eTrack_facility_poly/FeatureServer/0/query',
 eFindFacilityLayer: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eFind_dev/eFind_facility_poly/FeatureServer/0',
 efindUrl:'https://internal1.dec.state.ny.us/efind/public/',

 applicantScratchPolyUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/eTrack_applicant_scratch_poly/FeatureServer/0',
 submittedPolygonUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/eTrack_applicant_submittal_poly/FeatureServer/0',
 analystScratchPolyUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/eTrack_analyst_poly/FeatureServer/0',
 countiesQuery:'https://gisservices.its.ny.gov/arcgis/rest/services/NYS_Civil_Boundaries/FeatureServer/2/query',
 municipalitiesQuery:'https://gisservices.its.ny.gov/arcgis/rest/services/NYS_Civil_Boundaries/FeatureServer/6/query',
 municipalitiesUrl:'https://gisservices.its.ny.gov/arcgis/rest/services/NYS_Civil_Boundaries/FeatureServer/6',
 municipalitiesDECQuery: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eFind_dev/reference_layers/MapServer/6/query',

 //Juristrictional Layers
 Other_DEC_Program_Consultation_Layers:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/Other_DEC_Program_Consultation_Layers/MapServer',
 Other_Agency_Coordination_Layers:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/Other_Agency_Coordination_Layers/MapServer',
 Layers_Affecting_DEC_Jurisdiction:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/Layers_Affecting_DEC_Jurisdiction/MapServer',
 Jurisdictional_Layers:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/Jurisdictional_Layers/MapServer',
 NYSMaskLayer:'https://gisservices-test.dec.ny.gov/arcgis/rest/services/EAF/EAF_Mapper/MapServer/0',
 inquiryPolyUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/eTrack_spatial_inquiry_poly/FeatureServer/0',
 //etrack-dev*/
 taxParcelUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/nys_tax_parcels_agencies_etrack/MapServer/0',
 taxParcelQuery: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/nys_tax_parcels_agencies_etrack/MapServer/0/query',

//  etrack LRP
lrpUrl: 'https://dectest.dec.state.ny.us/efind/public/',
facilityNameUrl:'https://dectest.dec.state.ny.us/efind/site/',
ajaxCallTime:30000
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.

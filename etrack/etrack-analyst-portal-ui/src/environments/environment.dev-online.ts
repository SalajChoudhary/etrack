// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  login_portal:'online',
   apiBaseurl:'https://etrackservice-dev.dec.ny.gov',
   authMechanism:{
    jwks_uri:"/v1/keys?client_id=",
    //authorization_endpoint:"/oauth2/authorize",
    authorization_endpoint:"/v1/authorize",      
    token_endpoint:"/v1/token",
    userinfo_endpoint:"/v1/userinfo",
    end_session_endpoint:"/v1/logout",
    check_session_iframe:"/v1/checksession",
    revocation_endpoint:"/v1/revoke",
    introspection_endpoint:"/v1/introspect",
  },
   //apiBaseurl: 'http://localhost:4200',
  //dev
  //local
  clientId:'0oac8cfvxrvGGbZUA297',
  authUrl: 'https://login-qa.ny.gov/oauth2/default',
  redirectUri: "https://etrackanalyst-dev.dec.ny.gov/callback",
  logoutRedirectUrl: 'https://etrackanalyst-dev.dec.ny.gov',
  paymentUrl: 'https://devel.vpsenv.com/apipaymentQA/Payment/currentsession/',
  


  // production: true,

  //   //staging
  //   clientId:'b2108a0b-0115-444d-8b7e-066652e41253',
  //   authUrl: 'https://fs.svc.ny.gov/adfs',
  //   redirectUri: 'https://etrackanalyst-st.dec.ny.gov/callback',
  //   logoutRedirectUrl: 'https://etrackanalyst-st.dec.ny.gov',
  //   apiBaseurl:'https://etrackservice-st.dec.ny.gov'

 //GIS Urls
 //taxParcelUrl:'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/EAF/EAF_Mapper/MapServer/1',
 printServiceUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/eTrack_ExportWebMap/GPServer/Export%20Web%20Map',
 decRegionsUrl: 'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/reference/MapServer/12',
 decRegionsQuery: 'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/reference/MapServer/12/query',
 //taxparcelQuery: 'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/EAF/EAF_Mapper/MapServer/1/query',
 facilityLayer:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/eTrack_facility_poly/FeatureServer/0',
 facilityQuery: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/eTrack_facility_poly/FeatureServer/0/query',
  efindUrl:'https://internal1.dec.state.ny.us/efind/public/',
 //submitedPolygonUrl:'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/eTrack/eTrack_appl_submit/FeatureServer/0/',
 //analystScrachPolyUrl:'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/eTrack/eTrack_analyst_scratch/FeatureServer/0/',
 //applicantScrachPolyUrl:'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/eTrack/eTrack_appl_scratch/FeatureServer/0/',

 applicantScratchPolyUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/eTrack_applicant_scratch_poly/FeatureServer/0',
 submittedPolygonUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/eTrack_applicant_submittal_poly/FeatureServer/0',
 analystScratchPolyUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/eTrack_analyst_poly/FeatureServer/0',

 //taxParcelUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eFind_dev/nys_tax_parcels_agencies/MapServer/0',
 //taxparcelQuery: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eFind_dev/nys_tax_parcels_agencies/MapServer/0/query',
 //taxparcelExternalQuery: 'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/EAF/EAF_Mapper/MapServer/1/query',
 //taxparcelExternal: 'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/EAF/EAF_Mapper/MapServer/1',
 countiesQuery:'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/dil/dil_reference/MapServer/2/query',
 municipalitiesQuery:'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/search_layers/MapServer/1/query',
 municipalitiesUrl:'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/search_layers/MapServer/1',
 //Juristrictional Layers
 Other_DEC_Program_Consultation_Layers:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/Other_DEC_Program_Consultation_Layers/MapServer',
 Other_Agency_Coordination_Layers:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/Other_Agency_Coordination_Layers/MapServer',
 Layers_Affecting_DEC_Jurisdiction:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/Layers_Affecting_DEC_Jurisdiction/MapServer',
 Jurisdictional_Layers:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/Jurisdictional_Layers/MapServer',
 NYSMaskLayer:'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/EAF/EAF_Mapper/MapServer/0',
 //external tax parcels
 /*taxparcelQuery: 'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/EAF/EAF_Mapper/MapServer/1/query',
 taxParcelUrl: 'https://gisservices-dev.dec.ny.gov/arcgis/rest/services/EAF/EAF_Mapper/MapServer/1',*/

 //etrack-dev*/
 taxParcelUrl:'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/nys_tax_parcels_agencies_etrack/MapServer/0',
 taxParcelQuery: 'https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_dev/nys_tax_parcels_agencies_etrack/MapServer/0/query',
 /*//etrack-prod
 taxParcelUrl:'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/nys_tax_parcels_agencies_etrack/MapServer/0',
 taxparcelQuery: 'https://gisinternal.dec.ny.gov/arcgis/rest/services/eTrack/nys_tax_parcels_agencies_etrack/MapServer/0/query',*/

//  etrack LRP
lrpUrl: 'https://dectest.dec.state.ny.us/efind/public/',
facilityNameUrl:'https://dectest.dec.state.ny.us/efind/site/',

// inactive session timeout in ms
sessionTimeout: 7200000
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.

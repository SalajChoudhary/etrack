import { ElementRef, Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { AuthService } from 'src/app/core/auth/auth.service';
import { environment } from '../../../environments/environment';
import { ProjectLocation } from 'src/app/@store/models/projectLocation';
import Query from '@arcgis/core/rest/support/Query';
import Point from '@arcgis/core/geometry/Point';
import FeatureLayer from '@arcgis/core/layers/FeatureLayer';
import Fullscreen from '@arcgis/core/widgets/Fullscreen';
import Expand from '@arcgis/core/widgets/Expand';
import PortalSource from '@arcgis/core/widgets/BasemapGallery/support/PortalBasemapsSource';
import BasemapGallery from '@arcgis/core/widgets/BasemapGallery';
import SimpleRenderer from '@arcgis/core/renderers/SimpleRenderer';
import SimpleFillSymbol from '@arcgis/core/symbols/SimpleFillSymbol';
import MapImageLayer from '@arcgis/core/layers/MapImageLayer';
import LayerList from '@arcgis/core/widgets/LayerList';
import PrintTemplate from '@arcgis/core/rest/support/PrintTemplate';
import PrintParameters from '@arcgis/core/rest/support/PrintParameters';
import * as print from "@arcgis/core/rest/print";
import LegendLayer from "@arcgis/core/rest/support/LegendLayer.js";
import MapNotesLayer from '@arcgis/core/layers/MapNotesLayer';
import SketchViewModel from '@arcgis/core/widgets/Sketch/SketchViewModel';
import TextSymbol from '@arcgis/core/symbols/TextSymbol';
import Graphic from '@arcgis/core/Graphic';
import { PolygonStatus } from 'src/app/@store/models/PolygonStatus';
import { CivilDivision } from 'src/app/@store/models/civilDivision';
import { SIProject } from 'src/app/@store/models/siProject';

import * as reactiveUtils from "@arcgis/core/core/reactiveUtils";
@Injectable({
  providedIn: 'root',
})
export class GisService {

  private ITS_ADDRESS_API_SERVER = `${environment.apiBaseurl}/etrack-gis/ITSAddress`;
  private TAX_REST_API_URL = `${environment.apiBaseurl}/etrack-gis/taxParcel`;
  private COUNTIES_REST_API_URL = `${environment.apiBaseurl}/etrack-gis/counties`;
  private MUNICIPALITIES_REST_API_URL = `${environment.apiBaseurl}/etrack-gis/municipalities`;
  private ESRI_ADDRESS_API_SERVER = `${environment.apiBaseurl}/etrack-gis/EsriAddresses`;
  private DEC_PLACE_NAME_API_SERVICE = `${environment.apiBaseurl}/etrack-gis/placeNames`;
  private DEC_POLYGON_BY_ADDRESS = `${environment.apiBaseurl}/etrack-gis/DECPolygonByAddress`;

  private DEC_POLYGON_BY_DECID = `${environment.apiBaseurl}/etrack-gis/DECPolygonByDecId`;
  private DEC_ADDRESS_BY_ID = `${environment.apiBaseurl}/etrack-gis/decId`;
  private DEC_FACILITY_BY_TAXMAP = `${environment.apiBaseurl}/etrack-gis/decId/txmap`;
  private FACILITY_DETAILS_BY_ADDRESS = `${environment.apiBaseurl}/etrack-dart-db/facilities`;
  private PROJECT_LOCATION = `${environment.apiBaseurl}/etrack-dart-db/facility`;
  private SAVE_PROJECT_LOCATION = `${environment.apiBaseurl}/etrack-gis/facility`;
  private SHAPE_FILE_UPLOAD = `${environment.apiBaseurl}/etrack-gis/upload`;

  private VIEW_PROJECT_HISTORY = `${environment.apiBaseurl}/etrack-gis/facility/view`;

  private SAVE_APPL_POLYGON = `${environment.apiBaseurl}/etrack-gis/applicantPolygon`;
  private DELETE_APPL_POLYGON = `${environment.apiBaseurl}/etrack-gis/deletePolygon`;
  private GET_APPL_POLYGON = `${environment.apiBaseurl}/etrack-gis/applicantPolygon`;

  private SAVE_SI_PROJECT = `${environment.apiBaseurl}/etrack-gis/save-spatial-inquiry`;
  private GET_SI_PROJECT = `${environment.apiBaseurl}/etrack-gis/spatial-inquiry`;

  private SAVE_SI_POLYGON = `${environment.apiBaseurl}/etrack-gis/spatial-polygon`;
  private DELETE_SI_POLYGON = `${environment.apiBaseurl}/etrack-gis/delete-spatial-polygon`;
  private GET_SI_POLYGON = `${environment.apiBaseurl}/etrack-gis/spatial-polygon`;

  private GET_SI_CATEGORIES = `${environment.apiBaseurl}/etrack-config/spatial-inq-category`;

  private SAVE_WORK_AREA_POLYGON = `${environment.apiBaseurl}/etrack-gis/workarea-polygon`;
  private DELETE_WORK_AREA_POLYGON = `${environment.apiBaseurl}/etrack-gis/delete-workarea-polygon`;
  private GET_WORK_AREA_POLYGON = `${environment.apiBaseurl}/etrack-gis/workarea-polygon`;

  private SAVE_ANALYST_POLYGON = `${environment.apiBaseurl}/etrack-gis/analystPolygon`;
  private DELETE_ANALYST_POLYGON = `${environment.apiBaseurl}/etrack-gis/delete-analyst-polygon`;
  private GET_ANALYST_POLYGON = `${environment.apiBaseurl}/etrack-gis/analystPolygon`;

  private SAVE_SUBMITTED_APP_POLYGON = `${environment.apiBaseurl}/etrack-gis/submitedPolygon`;
  private GET_SUBMITTED_APP_POLYGON = `${environment.apiBaseurl}/etrack-gis/submitedPolygon`;
  private DELETE_SUBMITTED_APP_POLYGON = `${environment.apiBaseurl}/etrack-gis/delete-submittal-polygon`;
  private GET_JURISDICTIONAL_LAYERS = `${environment.apiBaseurl}/etrack-config/gis-layers`;


  private taxParcelInternalLayerQuery = new FeatureLayer({
    url: `${environment.taxParcelQuery}`,
    outFields: ['*'],
  });
  private municipalitiesQuery = new FeatureLayer({
    url: `${environment.municipalitiesQuery}`,
    outFields: ['NAME'],
  });
  private municipalitiesDECQuery = new FeatureLayer({
    url: `${environment.municipalitiesDECQuery}`,
    outFields: ['MUNICIPALITY'],
  });
  private regionsLayerQuery = new FeatureLayer({
    url: `${environment.decRegionsQuery}`,
    outFields: ['*'],
  });

  private countiesQuery = new FeatureLayer({
    url: `${environment.countiesQuery}`,
    outFields: ['*'],
  });

  private approvedFacilityLayerQuery = new FeatureLayer({
    url: `${environment.facilityQuery}`,
    outFields: ['*'],
  });

  constructor(
    private httpClient: HttpClient,
    public authService: AuthService
  ) { }

  public getITSAddresses(address: string) {
    let headers = this.buildAuthHeaders();
    let params = new HttpParams().set('SingleLine', address);
    return this.httpClient.get<any>(this.ITS_ADDRESS_API_SERVER, {
      params: params, headers: headers
    });
  }

  public getEsriAddresses(address: string,city:string,postal:string='') {
    let headers = this.buildAuthHeaders();
    let params = new HttpParams().set('address', address).set('city',city).set('postal',postal);
    return this.httpClient.get<any>(this.ESRI_ADDRESS_API_SERVER, {
      params: params, headers: headers
    });
  }

  public getPlaceNames(placeName: string) {
    let headers = this.buildAuthHeaders();
    let params = new HttpParams().set('placeName', placeName);
    return this.httpClient.get<any>(this.DEC_PLACE_NAME_API_SERVICE, {
      params: params, headers: headers
    });
  }

  public getTaxParcel(
    taxParcelID: string,
    countyName: string,
    municipalName: string
  ) {
    let headers = this.buildAuthHeaders();
    let params = new HttpParams()
      .set('taxParcelID', taxParcelID)
      .set('countyName', countyName)
      .set('municipalName', municipalName);
    return this.httpClient.get(this.TAX_REST_API_URL, { params: params, headers: headers });
  }

  public getCounties() {
    return this.httpClient.get<any>(this.COUNTIES_REST_API_URL);
  }

  public getMunicipalities(countyName: string) {
    let headers = this.buildAuthHeaders();
    let params = new HttpParams().set('countyName', countyName);
    return this.httpClient.get<any>(this.MUNICIPALITIES_REST_API_URL, {
      params: params, headers: headers
    });
  }

  public getSiCategories(){
    let headers = this.buildAuthHeaders();
    return this.httpClient.get<any>(this.GET_SI_CATEGORIES, { headers: headers });
  }

  public testLocalApi() {
    return this.httpClient.get<any>('/api/data');
  }
  public getDECPolygonByDecId(decId: string): any {
    let headers = this.buildAuthHeaders();
    let params = new HttpParams().set('decId', decId);
    return this.httpClient.get<any>(this.DEC_POLYGON_BY_DECID, {
      params: params, headers: headers
    });
  }
  public getAddressById(programType: string, programId: string): any {
    let user = localStorage.getItem('loggedUserName');
    let options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        programType: programType,
        programId: programId,
      }),
    };
    return this.httpClient.get<any>(this.DEC_ADDRESS_BY_ID, options);
  }
  public getFacilityByTaxMap(county: string, municipality: string, taxmap: string): any {
    let headers = this.buildAuthHeaders();
    let params = new HttpParams()
      .set('county', county)
      .set('municipality', municipality)
      .set('txmap', taxmap);

    return this.httpClient.get<any>(this.DEC_FACILITY_BY_TAXMAP, { params: params, headers: headers });
  }

  public getFacilityByAddress(addrLine1: string, city: string): any {
    let headers = this.buildAuthHeaders();
    let params = new HttpParams()
      .set('addrLine1', addrLine1)
      .set('city', city);
    return this.httpClient.get<any>(this.FACILITY_DETAILS_BY_ADDRESS, { params: params, headers: headers });
  }

  public saveInquiryProject(inquiryProject:SIProject) {
    let options = {
      headers: this.buildAuthHeaders(),
    };
    return this.httpClient.post<any>(
      this.SAVE_SI_PROJECT,
      inquiryProject,
      options
    );
  }

  public getInquiryProject(inquiryId: string) {
    let user = localStorage.getItem('loggedUserName');
    let headers=new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        inquiryId: inquiryId,
      });
    return this.httpClient.get<any>(this.GET_SI_PROJECT+'/'+inquiryId, { headers: headers});
  }

  public getAllInquiryProjects() {
    let user = localStorage.getItem('loggedUserName');
    let userFullName=localStorage.getItem('fullName');
    let headers=new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        requestorName:userFullName,
      });
    return this.httpClient.get<any>(this.GET_SI_PROJECT, { headers: headers});
  }

  public saveProjectLocation(projectLocation: ProjectLocation) {
    let options = {
      headers: this.buildAuthHeaders(),
    };
    return this.httpClient.post<any>(
      this.SAVE_PROJECT_LOCATION,
      projectLocation,
      options
    );
  }

  public updateProjectLocation(projectLocation: ProjectLocation) {
    let options = {
      headers: this.buildAuthHeaders(),
    };
    return this.httpClient.put<any>(
      this.SAVE_PROJECT_LOCATION,
      projectLocation,
      options
    );
  }

  public getProjectLocation(projectId: string) {
    let user = localStorage.getItem('loggedUserName');
    let options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        projectId: projectId,
      }),
    };
    return this.httpClient.get<any>(this.PROJECT_LOCATION, options);
  }

  public getDECPolygonByAddress(street: string, city: string) {
    let headers = this.buildAuthHeaders();
    let params = new HttpParams().set('street', street).set('city', city);
    return this.httpClient.get<any>(this.DEC_POLYGON_BY_ADDRESS, {
      params: params, headers: headers
    });
  }

  public saveApplicantPolygon(featuresAsString: string, action: string) {
    let formData = new FormData();
    formData.append('features', featuresAsString);
    formData.append('f', 'pjson');
    return this.httpClient.post<any>(this.SAVE_APPL_POLYGON + '/' + action, formData);
  }

  public deleteApplicantPolygon(objectId: number) {
    return this.httpClient
      .post<any>(this.DELETE_APPL_POLYGON + '/' + objectId, { headers: this.buildAuthHeaders()});
  }


  public searchApplicantPolygon(applicationId: string) {
    let headers = this.buildAuthHeaders();
    let params = new HttpParams().set('applicationId', applicationId);
    return this.httpClient
      .get<any>(this.GET_APPL_POLYGON, { params: params, headers: headers });
  }

  public saveSpatialInquiryPolygon(featuresAsString: string, action: string) {
    let formData = new FormData();
    formData.append('features', featuresAsString);
    formData.append('f', 'pjson');
    return this.httpClient.post<any>(this.SAVE_SI_POLYGON + '/' + action, formData);
  }

  public deleteSpatialInquiryPolygon(objectId: string) {
    let options = {
      headers: this.buildAuthHeaders(),
    };
    return this.httpClient
      .post<any>(this.DELETE_SI_POLYGON + '/' + objectId, options);
  }

  public searchSpatialInquiryPolygon(inquiryId: string) {
    let options = {
      headers: this.buildAuthHeaders(),
    };
    return this.httpClient
      .get<any>(this.GET_SI_POLYGON+ '/' + inquiryId, options);
  }

  public saveWorkAreaPolygon(featuresAsString: string, action: string) {
    let formData = new FormData();
    formData.append('features', featuresAsString);
    formData.append('f', 'pjson');
    return this.httpClient.post<any>(this.SAVE_WORK_AREA_POLYGON + '/' + action, formData);
  }

  public deleteWorkAreaPolygon(objectId: string) {
    let options = {
      headers: this.buildAuthHeaders(),
    };
    return this.httpClient
      .post<any>(this.DELETE_WORK_AREA_POLYGON + '/' + objectId,null, options);
  }

  public searchWorkAreaPolygon(workareaId: string) {
    let headers =this.buildAuthHeaders();
    let params = new HttpParams().set('workareaId', workareaId);
    return this.httpClient
      .get<any>(this.GET_WORK_AREA_POLYGON, { params: params, headers: headers });
  }

  public saveAnalystPolygon(featuresAsString: string, action: string) {
    let formData = new FormData();
    formData.append('features', featuresAsString);
    formData.append('f', 'pjson');
    return this.httpClient.post<any>(this.SAVE_ANALYST_POLYGON + '/' + action, formData);
  }

  public deleteAnalystPolygon(objectId: string) {
    let options = {
      headers: this.buildAuthHeaders(),
    };
    return this.httpClient
      .post<any>(this.DELETE_ANALYST_POLYGON + '/' + objectId, options);
  }

  public searchAnalystPolygon(analystId: string) {
    let headers = this.buildAuthHeaders();
    const params = new HttpParams().set('analystId', analystId);
    return this.httpClient
      .get<any>(this.GET_ANALYST_POLYGON, { params: params, headers: headers });

  }

  public saveSubmittedPolygon(featuresAsString: string, action: string) {
    let formData = new FormData();
    formData.append('features', featuresAsString);
    formData.append('f', 'pjson');
    return this.httpClient.post<any>(this.SAVE_SUBMITTED_APP_POLYGON + '/' + action, formData);
  }

  public deleteSubmittedPolygon(objectId: number) {
    let options = {
      headers: this.buildAuthHeaders(),
    };
    return this.httpClient
      .post<any>(this.DELETE_SUBMITTED_APP_POLYGON + '/' + objectId, options);
  }

  public searchSubmittedPolygon(applSubId: string) {
    let headers = this.buildAuthHeaders();
    let params = new HttpParams().set('applSubId', applSubId);
    return this.httpClient
      .get<any>(this.GET_SUBMITTED_APP_POLYGON, { params: params, headers: headers });
  }

  public getJurisdictionalLayers() {
    let headers = this.buildAuthHeaders();
    return this.httpClient
      .get<any>(this.GET_JURISDICTIONAL_LAYERS, { headers: headers });
  }

  public getProjectDetails(projectId: string) {
    let user = localStorage.getItem('loggedUserName');
    let options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        projectId: projectId
      }),
    };
    return this.httpClient
      .get<any>(this.SAVE_PROJECT_LOCATION, options);
  }


  public getProjectHistoryDetails(projectId: number) {
    let user = localStorage.getItem('loggedUserName');
    let options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        projectId: projectId.toString()
      }),
    };
    return this.httpClient
      .get<any>(this.VIEW_PROJECT_HISTORY, options);
  }

  public shapeFileUpload(name: string, file: any, spatialReference: number) {
    let options = {
      headers: this.buildAuthHeaders(),
    };
    let params = {
      name: name,
      targetSR: spatialReference,
      maxRecordCount: 10000,
      enforceInputFileSizeLimit: true,
      enforceOutputJsonSizeLimit: true,
      generalize: true,
      maxAllowableOffset: 1,
      reducePrecision: true,
      numberOfDigitsAfterDecimal: 10,
    };
    let formData = new FormData();
    formData.append('filetype', "shapefile")
    formData.append('publishParameters', JSON.stringify(params));
    formData.append('f', 'pjson');
    formData.append('file', file)
    // use the REST generate operation to generate a feature collection from the zipped shapefile
    return this.httpClient.post<any>(this.SHAPE_FILE_UPLOAD, formData, options);
  }

  private buildAuthHeaders(): HttpHeaders {
    let user = localStorage.getItem('loggedUserName');
    return new HttpHeaders({
      // @ts-ignore
      userId: user.replace('SVC', '').substring(1)
    });
  }

  public async queryTaxParcelsAt(geom: any): Promise<string> {
    let taxParcels = new Set<string>();
    let addresses = new Set<string>();
    const taxParcelQuery = new Query({
      spatialRelationship: 'intersects', // Relationship operation to apply
      geometry: geom.geometry, // The sketch feature geometry
      outFields: ['PRINT_KEY','SBL','PARCEL_ADDR'], // Attributes to return
      returnGeometry: false,
    });
    await this.taxParcelInternalLayerQuery
      .queryFeatures(taxParcelQuery)
      .then((results: any) => {
        results.features.forEach((feature: any) => {
          if (
            feature.attributes.PRINT_KEY !== undefined &&
            feature.attributes.PRINT_KEY !== '' &&
            feature.attributes.PRINT_KEY !== null
          ) {
            taxParcels.add(feature.attributes.PRINT_KEY);
          }else{
            taxParcels.add(feature.attributes.SBL);
          }
          if(feature.attributes.PARCEL_ADDR!== undefined && feature.attributes.PARCEL_ADDR !== '' && feature.attributes.PARCEL_ADDR !== null){
            addresses.add(feature.attributes.PARCEL_ADDR);
          }
        });
      })
      .catch((error: any) => {
        /*ignore error*/
      });
     if(addresses?.size >3){
      addresses.clear();
     }
    return [...taxParcels].join(', ')+":::"+[...addresses].join(',');
  }

  public async queryTaxParcelsOnlyAt(geom: any): Promise<string> {
    let taxParcels = new Set<string>();
    const taxParcelQuery = new Query({
      spatialRelationship: 'intersects', // Relationship operation to apply
      geometry: geom.geometry, // The sketch feature geometry
      outFields: ['PRINT_KEY','SBL'], // Attributes to return
      returnGeometry: false,
    });
    await this.taxParcelInternalLayerQuery
      .queryFeatures(taxParcelQuery)
      .then((results: any) => {
        results.features.forEach((feature: any) => {
          if (
            feature.attributes.PRINT_KEY !== undefined &&
            feature.attributes.PRINT_KEY !== '' &&
            feature.attributes.PRINT_KEY !== null
          ) {
            taxParcels.add(feature.attributes.PRINT_KEY);
          }else{
            taxParcels.add(feature.attributes.SBL);
          }
        });
      })
      .catch((error: any) => {
        console.log(error);
        /*ignore error*/
      });
    return [...taxParcels].join(', ');
  }

  public async findTaxParcelAt(x: number, y: number): Promise<string> {
    let taxMapNumber = '';
    const point = new Point({
      x: x,
      y: y,
    });
    const parcelQuery = new Query({
      spatialRelationship: 'intersects', // Relationship operation to apply
      geometry: point, // The sketch feature geometry
      outFields: ['PRINT_KEY', 'MUNI_NAME', 'COUNTY_NAME','SBL'], // Attributes to return
      returnGeometry: false,
    });
    await this.taxParcelInternalLayerQuery
      .queryFeatures(parcelQuery)
      .then((results: any) => {
        if (results.features.length > 0) {
          if( results.features[0]?.attributes?.PRINT_KEY !==undefined &&  results.features[0]?.attributes?.PRINT_KEY !== null && results.features[0]?.attributes?.PRINT_KEY !== ''){
          taxMapNumber = results.features[0].attributes.PRINT_KEY;
          }else{
            taxMapNumber = results.features[0]?.attributes?.SBL;
          }
        }
      })
      .catch((error: any) => {
        //just ignore
      });
    return taxMapNumber;
  }

  public async getMunicipalityAt(x: number, y: number): Promise<Set<CivilDivision>> {
    const point = new Point({
      x: x,
      y: y,
    });
    let municipalities = new Set<CivilDivision>();
    let municipalityQuery = new Query({
      spatialRelationship: 'intersects', // Relationship operation to apply
      geometry: point, // The sketch feature geometry
      outFields: ['NAME','SWIS','MUNITYCODE'], // Attributes to return
      returnGeometry: false,
    });
    let municipalityDECQuery = new Query({
      spatialRelationship: 'intersects', // Relationship operation to apply
      geometry: point, // The sketch feature geometry
      outFields: ['MUNICIPALITY','DECSWIS','CITY'], // Attributes to return
      returnGeometry: false,
    });
    await this.municipalitiesQuery
      .queryFeatures(municipalityQuery)
      .then(async (results: any) => {
        results.features.forEach((feature: any) => {
          if(!feature.attributes.SWIS?.startsWith('6')){
          municipalities.add(new CivilDivision(feature.attributes.NAME,feature.attributes.SWIS,feature.attributes.MUNITYCODE));
          }
        });
        await this.municipalitiesDECQuery
        .queryFeatures(municipalityDECQuery)
        .then((decResults: any) => {
          //if(municipalities.size ==0){
          decResults.features.forEach((feature1: any) => {
            if(feature1.attributes.DECSWIS?.startsWith('6')){
              municipalities.add(new CivilDivision(feature1.attributes.MUNICIPALITY,feature1.attributes.DECSWIS+"00",(feature1.attributes.CITY==='') ? 2:1 ));
            }
          });
        //}
      });
      })
      .catch((error: any) => {
        //ignore error
      });
    return municipalities;
  }

   public async getRegionAt(x: number, y: number): Promise<string> {
    let point = new Point({
      x: x,
      y: y,
    });
    let regions = new Set<string>();
    const regionsQuery = new Query({
      spatialRelationship: 'intersects', // Relationship operation to apply
      geometry: point, // The sketch feature geometry
      outFields: ['REGION'], // Attributes to return
      returnGeometry: false,
    });
    await this.regionsLayerQuery
      .queryFeatures(regionsQuery)
      .then((results) => {
          if(results?.features?.length >0){
          results.features.forEach((feature) => {
            regions.add(feature?.attributes?.REGION);
          });
        }else{
          regions.add('0');
        }
      })
      .catch((error: any) => {
        //ignore error
      });
    return [...regions][0];
  }


  public async queryMunicipalitiesAt(geom: any): Promise<Set<CivilDivision>> {
    let municipalities = new Set<CivilDivision>();
    let municipalityQuery = new Query({
      spatialRelationship: 'intersects', // Relationship operation to apply
      geometry: geom.geometry, // The sketch feature geometry
      outFields: ['NAME','SWIS','MUNITYCODE'], // Attributes to return
      returnGeometry: false,
    });
    let municipalityDECQuery = new Query({
      spatialRelationship: 'intersects', // Relationship operation to apply
      geometry: geom.geometry, // The sketch feature geometry
      outFields: ['MUNICIPALITY','DECSWIS','CITY'], // Attributes to return
      returnGeometry: false,
    });
    await this.municipalitiesQuery
      .queryFeatures(municipalityQuery)
      .then(async (results: any) => {
          results.features.forEach((feature: any) => {
            if(!feature.attributes.SWIS?.startsWith('6')){
              municipalities.add(new CivilDivision(feature.attributes.NAME,feature.attributes.SWIS,feature.attributes.MUNITYCODE));
            }
          });
          await this.municipalitiesDECQuery
          .queryFeatures(municipalityDECQuery)
          .then((decResults: any) => {
           // if(municipalities.size >0){
            decResults.features.forEach((feature: any) => {
              if(feature.attributes.DECSWIS?.startsWith('6')){
                municipalities.add(new CivilDivision(feature.attributes.MUNICIPALITY,feature.attributes.DECSWIS+"00",(feature.attributes.CITY==='') ? 2:1 ));
              }
            });
          /*}else{
            decResults.features.forEach((feature: any) => {
              municipalities.add(new CivilDivision(feature.attributes.MUNICIPALITY,feature.attributes.DECSWIS+"00",(feature.attributes.CITY==='') ? 2:1 ));
            });
          }*/
          }).catch((error: any) => {
            //ignore error
          });
      })
      .catch((error: any) => {
        //ignore error
      });
    return municipalities;
  }

  public async queryRegionsAt(geom: any): Promise<string> {
    let regions = new Set<string>();
    const regionsQuery = new Query({
      spatialRelationship: 'intersects', // Relationship operation to apply
      geometry: geom.geometry, // The sketch feature geometry
      outFields: ['REGION'], // Attributes to return
      returnGeometry: false,
    });
    await this.regionsLayerQuery
      .queryFeatures(regionsQuery)
      .then((results) => {
        if(results?.features?.length >0){
        results.features.forEach((feature) => {
          regions.add(feature.attributes.REGION);
        });
      }else{
        //regions.add('0')
      }
      })
      .catch((error: any) => {
        //ignore error
      });
    return [...regions].join(', ');
  }

  public async queryCountiesAt(geom: any): Promise<Set<CivilDivision>> {
    let counties = new Set<CivilDivision>();
    const countyQuery = new Query({
      spatialRelationship: 'intersects', // Relationship operation to apply
      geometry: geom.geometry, // The sketch feature geometry
      outFields: ['NAME','SWIS'], // Attributes to return
      returnGeometry: false,
    });
    await this.countiesQuery
      .queryFeatures(countyQuery)
      .then((results) => {
        results.features.forEach((feature) => {
          counties.add(new CivilDivision(feature.attributes.NAME,feature.attributes.SWIS));
        });
      })
      .catch((error: any) => {
        //ignore error
      });
    return counties;
  }

  public async addBaseLayers(mapView: any): Promise<void> {
    const allowedBasemapTitles = ['Streets', 'Imagery with Labels'];
    const portalSource = new PortalSource({
      query: {
        title: "United States Basemaps",
        owner: "Esri_cy_US"
      },
      // filtering portal basemaps
      filterFunction: (basemap: any) =>
        allowedBasemapTitles.indexOf(basemap.portalItem.title) > -1,
    });
    const basemapGallery = new BasemapGallery({
      view: mapView,
      container: document.createElement('div'),
      source: portalSource,
    });

    const bgExpand = new Expand({
      view: mapView,
      content: basemapGallery,
      expandIconClass: 'esri-icon-layers',
      expandTooltip: 'Change base map',
    });
    mapView.ui.add(bgExpand, 'bottom-left');
  }

  public async addFullScreen(mapView: any): Promise<void> {
    const fullscreen = new Fullscreen({
      view: mapView,
    });
   /*reactiveUtils.when(
      // getValue function
      () => fullscreen.viewModel.state,
      // callback
      (state,oldState) => {
        if(state !== oldState ){
          if(state==='active'){
          console.log(state);
          }
          if(state==='ready'){
            console.log(state);
          }
        }
      });*/
    mapView.ui.add(fullscreen, 'top-right');
  }

  public async addMaskLayers(mapView: any): Promise<void> {
    let nysMaskRenderer = new SimpleRenderer({
      symbol: new SimpleFillSymbol({
        color: [255, 255, 255, 0.9],
      }),
    });
    let nysMaskLayer = new FeatureLayer({
      url: `${environment.NYSMaskLayer}`,
      renderer: nysMaskRenderer,
      listMode: 'hide',
    });
    mapView.map.layers.add(nysMaskLayer);
  }
  inquiryRenderer = {
    type: "unique-value",
    field: "SI_TYPE",
    uniqueValueInfos: [
    {
        value: "Borough/Block/Lot Jurisdictional Determination",
        symbol: new TextSymbol({
          color: '#641E16',
          text: '\ue609',
          haloColor: 'white',
          haloSize: '2px',
          font: {
            size: 18,
            family: 'CalciteWebCoreIcons',
          },
        }),
      },
      {
        value: "Permit Screenings",
        symbol: new TextSymbol({
          color: '#512E5F',
          text: '\ue609',
          haloColor: 'white',
          haloSize: '2px',
          xoffset:'10px',
          font: {
            size: 18,
            family: 'CalciteWebCoreIcons',
          },
        }),
      },
      {
        value: "Pre-Application Meeting Request",
        symbol: new TextSymbol({
          color: '#154360',
          text: '\ue609',
          haloColor: 'white',
          haloSize: '2px',
          xoffset:'15px',
          font: {
            size: 18,
            family: 'CalciteWebCoreIcons',
          },
        }),
      },
      {
        value: "SEQR Lead Agency Coordination Request",
        symbol: new TextSymbol({
          color: '#0B5345',
          text: '\ue609',
          haloColor: 'white',
          haloSize: '2px',
          xoffset:'20px',
          font: {
            size: 18,
            family: 'CalciteWebCoreIcons',
          },
        }),
      },
      {
        value: "Energy Projects",
        symbol: new TextSymbol({
          color: '#7D6608',
          text: '\ue609',
          haloColor: 'white',
          haloSize: '2px',
          xoffset:'25px',
          font: {
            size: 18,
            family: 'CalciteWebCoreIcons',
          },
        }),
      },
      {
        value: "Management Plan/Comprehensive Plan",
        symbol: new TextSymbol({
          color: '#424949',
          text: '\ue609',
          haloColor: 'white',
          haloSize: '2px',
          yoffset:'10px',
          font: {
            size: 18,
            family: 'CalciteWebCoreIcons',
          },
        }),
      },
      {
        value: "Sanitary Sewer Extension",
        symbol: new TextSymbol({
          color: '#17202A',
          text: '\ue609',
          haloColor: 'white',
          haloSize: '2px',
          yoffset:'15px',
          font: {
            size: 18,
            family: 'CalciteWebCoreIcons',
          },
        }),
      },
      {
        value: "SERP Certification",
        symbol: new TextSymbol({
          color: '#626567',
          text: '\ue609',
          haloColor: 'white',
          haloSize: '2px',
          yoffset:'20px',
          font: {
            size: 18,
            family: 'CalciteWebCoreIcons',
          },
        }),
      }
    ],
  };
  public async buildJurisdictionalLayers(mapView: any) {
    this.getJurisdictionalLayers().subscribe((data: any) => {
      data.sort((layer1:any,layer2:any)=>{return layer2.orderInd-layer1.orderInd}).forEach((layer: any) => {
        if (layer.activeInd > 0) {
          if(layer.layerType ==='IMAGE'){
            mapView.map.layers.add(new MapImageLayer({
              url: layer.layerUrl,
              id: layer.layerName,
              opacity: 0.5,
              visible:false
            }));
          }
          if(layer.layerType ==='FEATURE'){
            mapView.map.layers.add(new FeatureLayer({
              url: layer.layerUrl,
              id: layer.layerName,
              opacity: 0.5,
              visible:false
            }));
          }
        }
      });
      mapView.map.layers.add(new FeatureLayer({
        id: 'inquiry_layer',
        url: `${environment.inquiryPolyUrl}`,
        outFields: ['*'],
        definitionExpression :"RECEIVED_DATE is NOT NULL",
        //@ts-ignore
        renderer:this.inquiryRenderer,
        maxScale: 0,
        minScale: 10000,
        visible:false
      }));
      let layerList = new LayerList({
        view: mapView,
        // executes for each ListItem in the LayerList
        listItemCreatedFunction: (event: any) => {
          let item = event.item;
          item.actionsOpen = true;
          item.open = false;
          let layer = item.layer;
          if (layer.parent?.type === 'map-image' && layer.source) {
            item.panel = {
              content: 'legend',
              open: false,
            };
          }

          item.watch('visible', (watchEvent:any)=> {
           if(watchEvent && item.parent !==undefined && item.parent!==null && !item.parent.visible){
            item.parent.visible=true;
           }
          });
        },
      });

      layerList.viewModel.on("trigger-action", (event) => {
         //console.log(event);
      });
      let layerExpand = new Expand({
        expandIconClass: 'esri-icon-feature-layer',
        expandTooltip: 'Jurisdictional layers',
        content: layerList,
        expanded: false,
      });
      mapView.ui.add(layerExpand, 'top-left');
    });
  }

  public async printMap(mapView: any, customTextElements: any, templateLayout:string ="etrack_project_location_layout"):Promise<any>{
    let legendLayers: LegendLayer[] = [];
    // Filter feature layers that are visible on the legend.
    mapView.map.layers.forEach((element: any) => {
      if (element.visible == true && element.type == 'feature') {
        legendLayers.push(
          new LegendLayer({
            layerId: element.id,
            subLayerIds: [],
            title: element.title,
          })
        );
      }
    });
    let printTemplate = new PrintTemplate({
      format: "pdf",
      exportOptions: {
        dpi: 300
      },
      //@ts-ignore
      layout: templateLayout,
      layoutOptions: {
        customTextElements: customTextElements,
        legendLayers: legendLayers
      },
    });
    const params = new PrintParameters({
      view: mapView,
      template: printTemplate
    });
    return print.execute(`${environment.printServiceUrl}`, params);
  }

  public addNotesInteractions(mapView: any,textInputDiv: ElementRef, textTool: ElementRef, textButton: ElementRef, resetButton: ElementRef, textInput:ElementRef,polygonStatus:PolygonStatus): SketchViewModel {
    // add a MapNotesLayer for the sketches and map notes
    let notesLayer = new MapNotesLayer({
      id: 'notes_sketch',
      title: 'Notes Layer',
      listMode:'hide'
    });
    mapView.map.add(notesLayer);
    let textSketchViewModel = new SketchViewModel({
      view: mapView,
      layer: notesLayer.textLayer,
      updateOnGraphicClick: true,
    });

    const textExpand = new Expand({
      view: mapView,
      content: textInputDiv.nativeElement,
    });
    textInputDiv.nativeElement.style.display = 'none';
    mapView.ui.add(textTool.nativeElement, 'top-right');

    mapView.when((event: any) => {
      textSketchViewModel.on('create', (event: any) => {
        if (event.state === 'complete') {
          this.textOnCreate(event,mapView,notesLayer,polygonStatus);
        }
      });
      textSketchViewModel.on('update', (event: any) => {
        if (event.state === 'complete') {
          console.log('updated text');
        }
      });

      textButton.nativeElement.onclick = () => {
        // set the sketch to try to create a text feature with point geometry
        textSketchViewModel.create('point');
        mapView.focus();
        const elements = document.getElementsByClassName('active');
        Array.from(elements).forEach((el) => {
          el.classList.remove('active');
        });
        if (this) {
          textButton.nativeElement.classList.add('active');
        }
      };

      // reset button
      resetButton.nativeElement.onclick = () => {
        notesLayer.textLayer.removeAll();
        mapView.focus();
        const elements = document.getElementsByClassName('active');
        Array.from(elements).forEach((el) => {
          el.classList.remove('active');
        });
        polygonStatus.isProjectLayerSelect = false;
      };

      // Listen to update event to modify a graphic to view
      textSketchViewModel.on('update', (event: any) => {
        // prevent saving if the feature is still being modified
        this.textOnUpdate(event, textExpand,mapView,textSketchViewModel,textInput,textInputDiv,polygonStatus);
      });
    });
    return textSketchViewModel;
  }

  private textOnUpdate(event: any,textExpand:Expand,mapView:any,textSketchViewModel:SketchViewModel, textInput:ElementRef,textInputDiv:ElementRef,polygonStatus:PolygonStatus):void{
    // prevent saving if the feature is still being modified
    textInput.nativeElement.addEventListener('keyup', (event: any) => {
     if (event.defaultPrevented) {
       return;
     }
     if (event.key !== undefined) {
       if (event.key === 'Enter') {
         event.preventDefault();
         textSketchViewModel.complete();
         // once modifications are complete, collapse text input
         textExpand.collapse();
         textInputDiv.nativeElement.style.display = 'none';
         mapView.ui.remove(textExpand);
       }
     }
   });
   mapView.ui.add(textExpand, 'bottom-left');
   textInputDiv.nativeElement.style.display = 'block';
   textExpand.expand();
   let currentGraphic = event.graphics[0];
   if (event.state === 'complete') {
     currentGraphic.symbol = new TextSymbol({
       text: textInput.nativeElement.value,
       color: [0, 0, 0],
       font: {
         family: 'Arial Unicode MS',
         size: 14,
       },
     });
     // once modifications are complete, collapse text input
     textExpand.collapse();
     polygonStatus.isProjectLayerSelect = true;
     textInputDiv.nativeElement.style.display = 'none';
     mapView.ui.remove(textExpand);
   }
   // populate the textInput with the selected
   // graphics's symbol text
   if (event.state === 'start') {
     if (currentGraphic.symbol.type === 'text') {
       let newTextSymbol = currentGraphic.symbol;
       textInput.nativeElement.value = newTextSymbol.text;
     }
   }
  }

  private textOnCreate(event: any,mapView:any,notesLayer:MapNotesLayer,polygonStatus:PolygonStatus):void{
    if(event.tool ==='point'){
      let elemental = document.getElementsByClassName('active');
      if (elemental[0].id == 'textButton') {
       notesLayer.textLayer.remove(event.graphic);
        const newTextGraphic = new Graphic({
          geometry: event.graphic.geometry,
          symbol: new TextSymbol({
            color: [0, 0, 0],
            text: 'Click to edit text below',
            font: {
              family: 'Arial Unicode MS',
              size: 14,
            },
          }),
        });
        notesLayer.textLayer.add(newTextGraphic);
      }
      mapView.focus();
      const elements = document.getElementsByClassName('active');
      Array.from(elements).forEach((el) => {
        el.classList.remove('active');
      });
      polygonStatus.isProjectLayerSelect = true;
    }
  }
}

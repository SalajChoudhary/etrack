import {
  Component,
  ElementRef,
  Input,
  OnInit,
  OnDestroy,
  ViewChild,
  Output,
  EventEmitter,
  SimpleChange,
} from '@angular/core';
import config from '@arcgis/core/config';
import Point from '@arcgis/core/geometry/Point';
import Polygon from '@arcgis/core/geometry/Polygon';
import SpatialReference from '@arcgis/core/geometry/SpatialReference';
import Graphic from '@arcgis/core/Graphic';
import GraphicsLayer from '@arcgis/core/layers/GraphicsLayer';
import Sketch from '@arcgis/core/widgets/Sketch';
import FeatureLayer from '@arcgis/core/layers/FeatureLayer';
import Query from '@arcgis/core/rest/support/Query';
import SimpleFillSymbol from '@arcgis/core/symbols/SimpleFillSymbol';
import PopupTemplate from '@arcgis/core/PopupTemplate';
import Measurement from '@arcgis/core/widgets/Measurement';
import SimpleRenderer from '@arcgis/core/renderers/SimpleRenderer';
import TextSymbol from '@arcgis/core/symbols/TextSymbol';
import { AddressResponse } from '..//..//..//../@store/models/addressResponse';
import { TaxMap } from '..//..//..//../@store/models/TaxMap';
import { ApprovedFacility } from 'src/app/@store/models/facility';
import { environment } from 'src/environments/environment';
import WebMap from '@arcgis/core/WebMap';
import MapView from '@arcgis/core/views/MapView';
import { TaxParcel } from 'src/app/@store/models/TaxParcel';
import { TitleCasePipe } from '@angular/common';
import * as webMercatorUtils from '@arcgis/core/geometry/support/webMercatorUtils';
import Extent from '@arcgis/core/geometry/Extent';
import LabelClass from '@arcgis/core/layers/support/LabelClass';
import SimpleLineSymbol from '@arcgis/core/symbols/SimpleLineSymbol';
import * as reactiveUtils from "@arcgis/core/core/reactiveUtils";
import { GisService } from '../../../../@shared/services/gisService';
import SketchViewModel from '@arcgis/core/widgets/Sketch/SketchViewModel';

@Component({
  selector: 'app-gis-map',
  templateUrl: './gis-map.component.html',
  styleUrls: ['./gis-map.component.scss'],
})
export class GisMapComponent implements OnInit, OnDestroy {

  taxParcelLabel = new LabelClass({
    // autocasts as new LabelClass()
    symbol: {
      type: 'text', // autocasts as new TextSymbol()
      color: '#FF5733',
      haloColor: 'white',
      haloSize: '3px',
      font: {
        // autocast as new Font()
        family: 'Arial',
        size: 9,
        weight: 'bold',
      },
    },
    labelPlacement: 'always-horizontal',
    labelExpressionInfo: {
      expression: "Upper($feature.PARCEL_ADDR)",
    },
    maxScale: 0,
    minScale: 5000,
  });

  taxParcelLabel_taxMap = new LabelClass({
    // auto casts as new LabelClass()
    symbol: {
      type: 'text', // auto casts as new TextSymbol()
      color: '#FF5733',
      haloColor: 'white',
      haloSize: '3px',
      font: {
        // auto cast as new Font()
        family: 'Arial',
        size: 9,
        weight: 'bold',
      },
    },
    labelPlacement: 'always-horizontal',
    labelExpressionInfo: {
      expression: "Upper($feature.PRINT_KEY)",
    },
    maxScale: 0,
    minScale: 5000,
  });

  popupTemplate= new PopupTemplate({
    content: this.getTax_ParcelInfo,
    actions: [],
    overwriteActions: true,
  });
  getTax_ParcelInfo(feature:any){
    console.log(feature.graphic.attributes)
    if( feature?.graphic?.attributes?.PRINT_KEY !== null && feature?.graphic?.attributes?.PRINT_KEY !==''){
      return "<strong>Tax Parcel ID:</strong> {PRINT_KEY} <br/> <strong>Parcel Address:</strong> {PARCEL_ADDR} {MUNI_NAME} NY {LOC_ZIP} <br/> <strong>Primary Owner:</strong> {PRIMARY_OWNER}.";
    }else{
      return "<strong>Tax Parcel ID:</strong> { SBL } <br/> <strong>Parcel Address:</strong> {PARCEL_ADDR} {MUNI_NAME} NY {LOC_ZIP} <br/> <strong>Primary Owner:</strong> {PRIMARY_OWNER}.";
    }
  }

  taxParcelLayer = new FeatureLayer({
    id: 'tax_parcel_layer',
    url: `${environment.taxParcelUrl}`,
    outFields: ['*'],
    popupTemplate:this.popupTemplate,
    labelingInfo: [this.taxParcelLabel],
    maxScale: 0,
    minScale: 40000,
    listMode:'hide',
  });

  ext = new Extent({
    xmin: -8879060.2957,
    ymin: 4935440.9267,
    xmax: -7978133.1733,
    ymax: 5624020.3215,
    spatialReference: new SpatialReference({ wkid: 102100 }),
  });

  countiesQuery = new FeatureLayer({
    url: `${environment.countiesQuery}`,
    outFields: ['*'],
    listMode:'hide',
  });
  municipalitiesQuery = new FeatureLayer({
    url: `${environment.municipalitiesQuery}`,
    outFields: ['NAME'],
    listMode:'hide',
  });

  municipalityRenderer = new SimpleRenderer({
    symbol: new SimpleLineSymbol({
      color: [146, 172, 160, 0.8],
      width: '3px',
      style: 'short-dash-dot-dot',
    }),
  });

  municipalitiesLayer = new FeatureLayer({
    visible: false,
    renderer: this.municipalityRenderer,
    listMode:'hide',
  });

  outLineSymbol=new SimpleFillSymbol({
    color: [32, 144, 235, 0.2],
    style: 'solid',
    outline: {
      color: '#2090EB',
      width: 3,
    },
  });

   polyFillSymbol= new SimpleFillSymbol({
    color: [146, 172, 160, 0.001],
    outline: {
      color:'#BFDBFE',
      width: 3,
    },
  });
  facilityRenderer = new SimpleRenderer({
    symbol: new TextSymbol({
      color: 'red',
      text: '\ue687',
      haloColor: 'white',
      haloSize: '2px',
      font: {
        size: 18,
        family: 'CalciteWebCoreIcons',
      },
    }),
  });

  shapeFileRenderer = new SimpleRenderer({
   symbol: this.outLineSymbol,
  });

  facilityLayer = new FeatureLayer({
    id: 'facility_layer',
    url: `${environment.facilityLayer}`,
    outFields: ['*'],
    renderer: this.facilityRenderer,
    popupTemplate: new PopupTemplate({
      content: [
        {
          type: 'text',
          text:
            '<strong>DEC ID :</strong> {PRIMARY_ID}  <br/> <strong>Address :</strong> {LOCATION_DIRECTIONS_1} {CITY} {STATE} {ZIP} {ZIP_EXTENSION}' +
            '<br/> <strong>Municipalities :</strong> {MUNICIPALITIES} <br/> <strong>Counties :</strong> {COUNTIES} <br/> <strong>Regions :</strong> {REGIONS}',
        },
      ],
      actions: [],
      overwriteActions: true,
    }),
    maxScale: 0,
    minScale: 5000,
    listMode:'hide',
  });

  approvedFacilityLayerQuery = new FeatureLayer({
    url: `${environment.facilityQuery}`,
    outFields: ['*'],
    listMode:'hide',
  });

  decRegionsLayer = new FeatureLayer({
    url: `${environment.decRegionsUrl}`,
    outFields: ['*'],
    visible: false,
    listMode:'hide',
  });

  // Create new instance of the Measurement widget
  measurement  = new Measurement();

  // layers to hold Graphics
  sketchLayer = new GraphicsLayer({
    id: 'gl_sketch',
    title: 'Sketch Layer',
    listMode:'hide',
  });

  // Create new instance of the Sketch widget
  locationGraphicLayer = new GraphicsLayer({
    id: 'location_sketch',
    title: 'Location Layer',
    listMode:'hide',
  });

  taxMapGraphicLayer = new GraphicsLayer({
    id: 'taxMap_sketch',
    title: 'TaxMap Layer',
    listMode:'hide',
  });

  taxParcelGraphicsLayer = new GraphicsLayer({
    id: 'taxParcel_sketch',
    title: 'Tax Parcel Layer',
    listMode:'hide',
  });

  facilityGraphicsLayer = new GraphicsLayer({
    id: 'facility_sketch',
    title: 'Facility Layer',
    listMode:'hide',
  });

  shapeGraphicsLayer = new GraphicsLayer({
    id: 'shapeFile_sketch',
    title: 'ShapeFile Layer',
    listMode:'hide',
  });

  municipalityGraphicsLayer = new GraphicsLayer({
    id: 'municipality_sketch',
    title: 'Municipality Layer',
    listMode:'hide',
    visible:false,
  });

  /*workAreaRenderer = new SimpleRenderer({
    symbol:new SimpleFillSymbol({
      color: [146, 172, 160, 0.001],
      outline: {
        color:'#74b1fd',
        width: 3,
      },
    })
  });*/
  outLineWorkAreaSymbol = new SimpleFillSymbol({
    color: [146, 172, 160, 0.001],
    outline: {
      color: '#FF51F3',
      width: 3,
      style: 'dash-dot',
    },
  });

   workAreaGraphicsLayer = new GraphicsLayer({
    id: 'work_area_sketch',
    title: 'Work Area Layer',
    listMode: 'hide',
  });

  workAreaEnabled:boolean=false;
  sketchEnabled:boolean=false;

  @ViewChild('mapViewNode', { static: true }) private elementRef!: ElementRef;
  @ViewChild('toolbarDiv', { static: true }) private toolbarDiv!: ElementRef;
  @ViewChild('distance', { static: true }) private distanceButton!: ElementRef;
  @ViewChild('loadingDiv', { static: true }) private loading!: ElementRef;
  @ViewChild('workAreaDiv', { static: true }) private workAreaDiv!: ElementRef;
  @ViewChild('workArea', { static: true }) private workArea!: ElementRef;
  @ViewChild('approvedPolygonDiv', { static: true }) private approvedPolygonDiv!: ElementRef;


  @Input() mapProperties!: any;

  @Output() mapInit: EventEmitter<any> = new EventEmitter();
  @Output() sketchedGeometry = new EventEmitter<any>();
  @Output() selectedTaxParcelGeometry = new EventEmitter<any>();
  @Output() selectedFacilityGeometryOnMap = new EventEmitter<any>();
  @Output() updateFacilityGeometry = new EventEmitter<any>();
  @Output() selectedFacilityGeometry = new EventEmitter<any>();
  @Output() selectedFacilityTaxParcel = new EventEmitter<TaxParcel>();
  @Output() selectedTaxMapGeometry = new EventEmitter<any>();
  @Output() uploadedShapeFileGeometry = new EventEmitter<any>();
  @Output() workAreaGeometry = new EventEmitter<any>();

  private _codedAddress = new AddressResponse();
  private _taxMap = new TaxMap();
  private _approvedFacility = new ApprovedFacility();
  private _muniVisible = false;
  private sketchViewModel!: SketchViewModel;
  private sketchWidget!: Sketch;
  private workAreaSketchWidget!: Sketch;

  private mapView: any;
  private map: any;
  //layerForm: FormGroup;

  @Input()
  set codedAddress(addressResponse: AddressResponse) {
    this._codedAddress = addressResponse;
  }

  get codedAddress(): AddressResponse {
    return this._codedAddress;
  }

  @Input()
  set taxMap(taxMap: TaxMap) {
    this._taxMap = taxMap;
  }

  get taxMap(): TaxMap {
    return this._taxMap;
  }

  @Input()
  set approvedFacility(approvedFacility: ApprovedFacility) {
    this._approvedFacility = approvedFacility;
  }

  get approvedFacility(): ApprovedFacility {
    return this._approvedFacility;
  }

  @Input()
  set municipality(muniVisible: boolean) {
    this._muniVisible = muniVisible;
  }

  get municipality(): boolean {
    return this._muniVisible;
  }

  constructor(private titleCasePipe: TitleCasePipe,private gisService:GisService) {
  }

  ngOnInit(): void {
    config.assetsPath = 'assets/';
      this.loadMap();
      this.gisService.addBaseLayers(this.mapView);
      this.gisService.addFullScreen(this.mapView);
      this.addLayers();
      this.addSketch();
      this.addGraphicLayers();
      this.addFacilityLayer();
      this.addMeasurement();
      this.addWorkAreaTool();
      this.addDeleteApprovedPolygonTool();
      this.gisService.addMaskLayers(this.mapView);
      this.addLoading();
  }

  ngOnDestroy(): void {
    this.mapView.destroy();
  }

  loadWebMap(props: {
    basemap: any;
    container: any;
    center: any;
    zoom: any;
  }): void {
    this.map = new WebMap({ basemap: props.basemap });
    this.mapView = new MapView({
      container: props.container,
      map: this.map,
      center: props.center,
      zoom: props.zoom,
      extent: this.ext,
      constraints: {
        rotationEnabled: false,
      },
    });
  }

  public loadMap(): void {
    this.loadWebMap({
      ...this.mapProperties,
      container: this.elementRef.nativeElement,
    });
  }

  public resetExtent(): void {
    this.mapView.extent = this.ext;
  }

  ngOnChanges(changes: { [property: string]: SimpleChange }): void {
    // Extract changes to the input property by its name
    if (changes['codedAddress'] !== undefined) {
      const change: SimpleChange = changes['codedAddress'];
      if (change.currentValue) {
        this.refreshTaxParcelWithAddress();
        this.gotoAddress(change.currentValue);
      }
    }
    if (changes['taxMap'] !== undefined) {
      const change: SimpleChange = changes['taxMap'];
      if (change.currentValue) {
        this.refreshTaxParcelWithTaxMap();
        this.gotoTaxMap(change.currentValue);
      }
    }
    if (changes['approvedFacility'] !== undefined) {
      const change: SimpleChange = changes['approvedFacility'];
      if (change.currentValue) {
        this.refreshTaxParcelWithAddress();
        this.gotoFacility(change.currentValue);
      }
    }

    if (changes['municipality'] !== undefined) {
      const change: SimpleChange = changes['municipality'];
      if (change.currentValue) {
        this.refreshTaxParcelWithTaxMap();
        this.municipalitiesLayer.visible = change.currentValue;
      }
    }
  }

  private async addWorkAreaTool(): Promise<void> {
    this.mapView.ui.add(this.workAreaDiv.nativeElement, 'top-left');
  }
  private async addDeleteApprovedPolygonTool(): Promise<void> {
    this.mapView.ui.add(this.approvedPolygonDiv.nativeElement, 'top-left');
  }

 public async toggleWorkAreaSketch(): Promise<void> {
    this.workAreaEnabled=!this.workAreaEnabled;
    if(this.workAreaEnabled){
      this.workArea.nativeElement.classList.add('active');
      this.sketchWidget?.destroy();
      this.taxParcelLayer.popupEnabled=false;
      this.setUpSketchViewModel(this.workAreaGraphicsLayer);
      this.addWorkAreaSketchTool();
    }else{
      this.workArea.nativeElement.classList.remove('active');
      this.workAreaSketchWidget?.destroy();
      this.taxParcelLayer.popupEnabled=true;
      this.addSketch();
    }
  }

  public async deleteApprovedPolygon(): Promise<void> {
    this.facilityGraphicsLayer.graphics.removeAll();
    this.updateFacilityGeometry.emit(this.facilityGraphicsLayer.graphics);
    if(this.workAreaEnabled){
      this.workArea.nativeElement.classList.remove('active');
      this.workAreaSketchWidget?.destroy();
      this.taxParcelLayer.popupEnabled=true;
      this.workAreaEnabled =false;
      this.addSketch();
    }
  }

  public async gotoGeoCodedAddress(x: number, y: number): Promise<void> {
    const point = new Point({
      x: x,
      y: y,
    });
    this.mapView.goTo(
      { target: point, zoom: 18 },
      { duration: 1000, easing: 'ease-in-out' }
    );
  }

  private async gotoFacility(facility: ApprovedFacility): Promise<void> {
    this.facilityGraphicsLayer.graphics.removeAll();
    if (facility.geometry !== undefined) {
      const polygon = new Polygon({
        rings:
          facility.geometry.geometry !== undefined
            ? facility.geometry.geometry.rings
            : facility.geometry.rings,
        spatialReference: new SpatialReference({
          wkid: facility.wkid,
        }),
      });
      const graphic = new Graphic({
        geometry: polygon.spatialReference.isWebMercator
          ? polygon
          : webMercatorUtils.geographicToWebMercator(polygon),
       symbol:this.outLineSymbol,
      });
      if (facility.isValidLocation == 0) {
        this.mapView.goTo(graphic);
      }
      if (facility.isValidLocation == 1) {
        this.facilityGraphicsLayer.graphics.add(graphic);
        this.updateFacilityGeometry.emit(this.facilityGraphicsLayer.graphics);
        this.getExtent();
      }
    }
  }

  private async gotoTaxMap(taxmap: TaxMap): Promise<void> {
    this.taxMapGraphicLayer.graphics.removeAll();
    if (taxmap.geometry !== undefined && taxmap.geometry.rings !== undefined) {
      const polygon = new Polygon({
        rings: taxmap.geometry.rings,
      });
      const graphic = new Graphic({
        geometry: polygon,
        symbol:this.outLineSymbol,
      });
      this.taxMapGraphicLayer.graphics.add(graphic);
      this.selectedTaxMapGeometry.emit(this.taxMapGraphicLayer.graphics);
      this.getExtent();
    }
  }

  public async gotoTaxParcel(taxmap: TaxMap): Promise<void> {
    if (taxmap.geometry !== undefined && taxmap.geometry.rings !== undefined) {
      let polygon = new Polygon({
        rings: taxmap.geometry.rings,
      });
      this.mapView.goTo(polygon, { duration: 1000, easing: 'ease-in-out' });
    }
  }

  private async gotoAddressPolygon(
    addressResponse: AddressResponse
  ): Promise<void> {
    this.facilityGraphicsLayer.graphics.removeAll();
    if (
      addressResponse.geometry !== undefined &&
      addressResponse.geometry.geometry !== undefined
    ) {
      const polygon = new Polygon({
        rings: addressResponse.geometry.geometry.rings,
        spatialReference: new SpatialReference({
          wkid: addressResponse.wkid,
        }),
      });
      const graphic = new Graphic({
        geometry: webMercatorUtils.geographicToWebMercator(polygon),
        symbol:this.outLineSymbol,
      });
      if (addressResponse.isValidLocation == 1) {
        this.facilityGraphicsLayer.graphics.add(graphic);
        this.updateFacilityGeometry.emit(this.facilityGraphicsLayer.graphics);
      }
      const point = new Point({
        x: parseFloat(addressResponse.long),
        y: parseFloat(addressResponse.lat),
      });
      this.mapView
        .goTo(
          { target: point, zoom: 18 },
          { duration: 200, easing: 'ease-in-out' }
        )
        .then(() => {});
    }
  }

  private async gotoAddress(
    codedAddressLocation: AddressResponse
  ): Promise<void> {
    this.locationGraphicLayer.graphics.removeAll();
    if (codedAddressLocation.geometry !== undefined) {
      this.gotoAddressPolygon(codedAddressLocation);
    } else {
      this.facilityGraphicsLayer.graphics.removeAll();
      this.updateFacilityGeometry.emit(this.facilityGraphicsLayer.graphics);
      if (
        codedAddressLocation.long !== undefined &&
        codedAddressLocation.lat !== undefined
      ) {
        let pt = new Point({
          x: parseFloat(codedAddressLocation.long),
          y: parseFloat(codedAddressLocation.lat),
        });
        this.mapView
          .goTo(
            { target: pt, zoom: 18 },
            { duration: 200, easing: 'ease-in-out' }
          )
          .then(() => {});
      }
    }
  }

  private refreshTaxParcelWithTaxMap(){
    this.taxParcelLayer.labelingInfo.pop();
    this.taxParcelLayer.labelingInfo.push(this.taxParcelLabel_taxMap);
    this.taxParcelLayer.refresh();
  }
  private refreshTaxParcelWithAddress(){
    this.taxParcelLayer.labelingInfo.pop();
    this.taxParcelLayer.labelingInfo.push(this.taxParcelLabel);
    this.taxParcelLayer.refresh();
  }

  public zoomToCountyGeometry(countyName: string): void {
    this.municipalityGraphicsLayer.graphics?.removeAll();
    const countyQuery = new Query({
      where: "NAME = '" + countyName + "'",
      outFields: ['NAME'], // Attributes to return
      outSpatialReference: new SpatialReference({
        wkid: 102100,
      }),
      returnGeometry: true,
    });
    this.countiesQuery
      .queryFeatures(countyQuery)
      .then((results) => {
        this.zoomToMunicipalityOrCounty(results);
      })
      .catch((error: any) => {});
  }

  public zoomToMunicipalityGeometry(municipalityName: string,countyName:string): void {
    this.refreshTaxParcelWithTaxMap();
    this.municipalityGraphicsLayer.graphics?.removeAll();
    const muniQuery = new Query({
      where: "NAME = '" + municipalityName + "'"+" AND COUNTY= '"+countyName+"'",
      outFields: ['NAME'], // Attributes to return
      outSpatialReference: new SpatialReference({
        wkid: 102100,
      }),
      returnGeometry: true,
    });
    this.municipalitiesQuery
      .queryFeatures(muniQuery)
      .then((results) => {
        this.zoomToMunicipalityOrCounty(results);
      })
      .catch((error: any) => {});
  }
private zoomToMunicipalityOrCounty(results:any){
  if (results.features.length > 0) {
  let polygon = new Polygon({
    rings: (results.features[0].geometry as Polygon).rings,
    spatialReference: new SpatialReference({
      wkid: 102100,
    }),
  });
  let graphic = new Graphic({
    geometry: polygon,
  });
  this.municipalityGraphicsLayer.graphics.add(graphic);
  this.mapView.goTo((results.features[0].geometry as Polygon).extent, {
    duration: 200,
    easing: 'ease-in-out',
  });
}
}

public async queryFacilitiesAt(geom: any):Promise<Array<ApprovedFacility>>{
  let facilities!: Array<ApprovedFacility>;
  const approvedFacilityQuery = new Query({
    spatialRelationship: 'intersects', // Relationship operation to apply
    geometry: geom.geometry, // The sketch feature geometry
    outFields: ['*'], // Attributes to return
    where:"PRIMARY_ID not like '_-__00-%' AND PRIMARY_ID not like '0%' AND PRIMARY_ID not like '_-990%'",
    returnGeometry: true,
  });
  await this.approvedFacilityLayerQuery
    .queryFeatures(approvedFacilityQuery)
    .then((results) => {
      if(results.features[0]){
        facilities = new Array<ApprovedFacility>();
        results.features.forEach((feature) => {
          this.approvedFacilityLayerQuery.queryRelatedFeatures({
            outFields: ["OWNER_NAME", "DISTRICT_ID"],
            relationshipId: this.approvedFacilityLayerQuery.relationships[0].id,
            objectIds:feature.attributes.OBJECTID
          }).then((relatedData)=>{
            let ownerName=new Set<string>();
            relatedData[feature.attributes.OBJECTID]?.features?.forEach((relatedFeature:any) => {
              ownerName.add(relatedFeature.attributes?.OWNER_NAME);
            });
            facilities.push(this.buildFacility(feature, [...ownerName].join(',')));
          })
        });
      }
    }).catch((error: any) => {
      console.log(error)
      //ignore error
    });
    return facilities;

}

  private buildFacility(feature: any,ownerName:string) {
    let approvedFacility = new ApprovedFacility();
    approvedFacility.SITE_ID = feature.attributes.SITE_ID;
    approvedFacility.SITE_TYPE = this.titleCasePipe.transform(
      feature.attributes.SITE_TYPE
    );
    approvedFacility.SITE_NAME = this.titleCasePipe.transform(
      feature.attributes.SITE_NAME
    );
    approvedFacility.PRIMARY_ID = feature.attributes.PRIMARY_ID;
    approvedFacility.PRIMARY_SWIS = feature.attributes.PRIMARY_SWIS;
    approvedFacility.LOCATION_DIRECTIONS_1 = this.titleCasePipe.transform(
      feature.attributes.LOCATION_DIRECTIONS_1
    );
    approvedFacility.LOCATION_DIRECTIONS_2 = this.titleCasePipe.transform(
      feature.attributes.LOCATION_DIRECTIONS_2
    );
    approvedFacility.CITY = this.titleCasePipe.transform(
      feature.attributes.CITY
    );
    approvedFacility.STATE = feature.attributes.STATE;
    approvedFacility.ZIP = feature.attributes.ZIP;
    approvedFacility.MUNICIPALITIES = this.titleCasePipe.transform(
      feature.attributes.MUNICIPALITIES
    );
    approvedFacility.COUNTIES = this.titleCasePipe.transform(
      feature.attributes.COUNTIES
    );
    approvedFacility.REGIONS = feature.attributes.REGIONS;
    if(feature.attributes.VALIDATED_LOCATION !==1){
      approvedFacility.isValidLocation = 0;
    }else{
      approvedFacility.isValidLocation = feature.attributes.VALIDATED_LOCATION;
    }

    approvedFacility.OWNER_NAME = ownerName;
    return approvedFacility;
  }

  private async addGraphicLayers(): Promise<void> {
    this.mapView.map.layers.add(this.sketchLayer);
    this.mapView.map.layers.add(this.taxParcelGraphicsLayer);
    this.mapView.map.layers.add(this.facilityGraphicsLayer);
    this.mapView.map.layers.add(this.locationGraphicLayer);
    this.mapView.map.layers.add(this.taxMapGraphicLayer);
    this.mapView.map.layers.add(this.shapeGraphicsLayer);
    this.mapView.map.layers.add(this.municipalityGraphicsLayer);
    this.mapView.map.layers.add(this.workAreaGraphicsLayer);
  }

  public async clearAllGraphics(): Promise<void> {
    this.sketchLayer.graphics.removeAll();
    this.taxParcelGraphicsLayer.graphics.removeAll();
    this.facilityGraphicsLayer.graphics.removeAll();
    this.locationGraphicLayer.graphics.removeAll();
    this.taxMapGraphicLayer.graphics.removeAll();
    this.shapeGraphicsLayer.graphics.removeAll();
    this.municipalityGraphicsLayer.graphics.removeAll();
    this.workAreaGraphicsLayer.graphics.removeAll();
    this.mapView.popup.close();
    this.clearMeasurements();
  }

  private async addMeasurement(): Promise<void> {
    this.mapView.ui.add(this.toolbarDiv.nativeElement, 'top-left');
    this.mapView.ui.add(this.measurement, 'bottom-left');
    this.measurement.view = this.mapView;
  }

  public async distanceMeasurement(): Promise<void> {
    if (this.measurement.activeTool === 'distance') {
      this.clearMeasurements();
    } else {
      this.measurement.activeTool = 'distance';
      this.measurement.linearUnit = 'us-feet';
      this.distanceButton.nativeElement.classList.add('active');
    }
  }

  public async clearMeasurements(): Promise<void> {
    this.distanceButton.nativeElement.classList.remove('active');
    this.measurement.clear();
  }

  private addWorkAreaSketchTool(){
    this.workAreaSketchWidget = new Sketch({
      layer: this.workAreaGraphicsLayer,
      availableCreateTools: ['polygon', 'move', 'transform', 'reshape'],
      creationMode: 'update',
      layout: 'vertical',
      view: this.mapView,
    });
    this.workAreaSketchWidget.visibleElements = {
      settingsMenu: false,
      selectionTools: {
        'lasso-selection': false,
        'rectangle-selection': false,
      },
    };
    this.workAreaSketchWidget.on('create', (event: any) => {
      if (event.state === 'complete') {
        this.workAreaGraphicsLayer?.graphics?.map((graphic) => {
          graphic.symbol = this.outLineWorkAreaSymbol;
        });
        this.workAreaGeometry.emit(this.workAreaGraphicsLayer.graphics);
        this.getExtent();
      }
    });
    this.workAreaSketchWidget.on('update', (event: any) => {
      if (event.state === 'complete') {
        this.workAreaGraphicsLayer?.graphics?.map((graphic) => {
          graphic.symbol = this.outLineWorkAreaSymbol;
        });
        this.workAreaGeometry.emit(this.workAreaGraphicsLayer.graphics);
        this.getExtent();
      }
    });
    this.workAreaSketchWidget.on("delete", (event:any) =>{
      this.workAreaGeometry.emit(this.workAreaGraphicsLayer.graphics);
      this.getExtent();
    });
    this.mapView.ui.add(this.workAreaSketchWidget, 'bottom-right');
  }

  private setUpSketchViewModel(layer:GraphicsLayer){
    this.sketchViewModel = new SketchViewModel({
      view: this.mapView,
      layer: layer,
      updateOnGraphicClick: false,
    });
  }

  private async changeSketchLayer(): Promise<void> {
    this.sketchWidget = new Sketch({
      layer: this.facilityGraphicsLayer,
      availableCreateTools: ['polygon'],
      creationMode: 'update',
      layout: 'horizontal',
      view: this.mapView,
    });
    this.sketchWidget.visibleElements = {
      settingsMenu: false,
      createTools: { polygon: false },
      selectionTools: {"rectangle-selection": false,'lasso-selection': false },
    };
    this.sketchWidget.on('create', (event: any) => {
      this.sketchEnabled=true;
      if (event.state === 'complete') {
        this.updateFacilityGeometry.emit(this.facilityGraphicsLayer.graphics);
        this.getExtent();
      }
    });
    this.sketchWidget.on('update', (event: any) => {
      this.sketchEnabled=true;
      this.updateFacilityGeometry.emit(this.facilityGraphicsLayer.graphics);
      if (event.state === 'complete') {
        this.updateFacilityGeometry.emit(this.facilityGraphicsLayer.graphics);
        this.getExtent();
      }
    });
    this.sketchWidget.on('delete', (event: any) => {
      this.sketchEnabled=true;
      this.addSketch();
      if (event.state === 'complete') {
        this.updateFacilityGeometry.emit(this.facilityGraphicsLayer.graphics);

      }
    });
    this.mapView.ui.add(this.sketchWidget, 'bottom-right');
  }

  private async changeBackToSketchLayer(): Promise<void> {
    this.sketchWidget = new Sketch({
      layer: this.sketchLayer,
      availableCreateTools: ['polygon', 'move', 'transform', 'reshape'],
      creationMode: 'update',
      layout: 'vertical',
      view: this.mapView,
    });
    this.sketchWidget.visibleElements = {
      settingsMenu: false,
      selectionTools: {"rectangle-selection": false,'lasso-selection': false },
    };
    this.sketchWidget.on('create', (event: any) => {
      this.sketchEnabled=true;
      if (event.state === 'complete') {
        this.sketchedGeometry.emit(this.sketchLayer.graphics);
        this.getExtent();
      }
    });
    this.sketchWidget.on('update', (event: any) => {
      this.sketchEnabled=true;
      this.sketchedGeometry.emit(this.sketchLayer.graphics);
      if (event.state === 'complete') {
        this.sketchedGeometry.emit(this.sketchLayer.graphics);
        this.getExtent();
      }
    });
    this.sketchWidget.on('delete', (event: any) => {
      this.sketchEnabled=true;
      if (event.state === 'complete') {
        this.sketchedGeometry.emit(this.sketchLayer.graphics);
        this.getExtent();
      }
    });
    this.mapView.ui.add(this.sketchWidget, 'bottom-right');
  }
  private async addSketch(): Promise<void> {
      this.sketchWidget?.destroy();
      this.setUpSketchViewModel(this.sketchLayer);
      this.changeBackToSketchLayer();
  }
  private async addApprovedSketch(): Promise<void> {
    this.sketchWidget?.destroy();
    this.setUpSketchViewModel(this.facilityGraphicsLayer);
    this.changeSketchLayer();
  }

  public addShapeFileToMap(featureCollection: any) {
    featureCollection.layers.map((layer: any) => {
      const graphics = layer.featureSet.features.map((feature: any) => {
        return Graphic.fromJSON(feature);
      });
      let polyGraphics = new Array<Graphic>();
      graphics.forEach((graphic: any) => {
        let polygon = new Polygon({
          rings: graphic.geometry.rings,
          spatialReference: new SpatialReference({
            wkid: graphic.geometry.spatialReference.wkid,
          }),
        });
        let polygraphic = new Graphic({
          geometry: polygon,
          symbol:this.outLineSymbol,
        });
        polyGraphics.push(polygraphic);
      });
      this.shapeGraphicsLayer.addMany(polyGraphics);
    });
    this.mapView.goTo(this.shapeGraphicsLayer.graphics).catch((error: any) => {
      if (error.name != 'AbortError') {
        console.error(error);
      }
    });
    this.uploadedShapeFileGeometry.emit(this.shapeGraphicsLayer.graphics);
  }

  private addLoading() {
    reactiveUtils.watch(
      // getValue function
      () => this.mapView.updating,
      // callback
      (updating) => {
        if(updating){
          this.loading.nativeElement.classList.remove('loading-hide');
        }else{
          this.loading.nativeElement.classList.add('loading-hide');
        }
      });
  }

  private async addLayers(): Promise<void> {
    this.mapView.map.layers.add(this.taxParcelLayer);
    this.mapView.map.layers.add(this.decRegionsLayer);
  }

  private updateTaxParcelAndEmit(taxGraphic:Graphic):void{
    if (taxGraphic !== undefined) {
      let resultGraphic = taxGraphic;
      const isGraphicExist = this.taxParcelGraphicsLayer.graphics.some(
        (graphic: any) =>
          graphic.attributes?.OBJECTID ===
          resultGraphic.attributes?.OBJECTID
      );
      if (isGraphicExist) {
        this.taxParcelGraphicsLayer.graphics.forEach((graphic: any) => {
          if (
            graphic.attributes?.OBJECTID ===
            resultGraphic.attributes?.OBJECTID
          ) {
            this.taxParcelGraphicsLayer.graphics.remove(graphic);
          }
        });
      } else {
        if (resultGraphic) {
          let selectionGraphic = resultGraphic.clone();
          selectionGraphic.symbol = this.outLineSymbol;
          this.taxParcelGraphicsLayer.graphics.add(selectionGraphic);
          this.getExtent();
        }
      }
      this.selectedTaxParcelGeometry.emit(this.taxParcelGraphicsLayer.graphics);
    }
  }

  private async addFacilityLayer(): Promise<void> {
    this.mapView.map.layers.add(this.facilityLayer);
    this.setUpClickHandler();
  }

  private async setUpClickHandler(): Promise<void> {
      this.setUpSketchViewModel(this.sketchLayer);
      this.mapView.on('click', (event: any) => {
      this.mapView.popup.dockOptions.buttonEnabled = false;
      if (this.measurement.activeTool === null) {
        this.mapView.hitTest(event).then((response: any) => {
          if (this.sketchViewModel?.state === 'active' || this.workAreaEnabled) {
            return;
          }
          if(this.sketchViewModel?.state === 'ready' && this.sketchEnabled){
              this.sketchEnabled=false;
          }else{
            let facilityGraphic=response.results.filter((result: any)=> {
              return result.graphic.layer?.id === 'facility_sketch';
            })[0]?.graphic;
            if(facilityGraphic===undefined){
              this.updateTaxParcelAndEmit(response.results.filter(function (result: any) {
                return result.graphic.layer?.id === 'tax_parcel_layer';
              })[0]?.graphic);
            }
          }
        });
      }
    });
  }

  public get isFacilityExist(){
    return this.facilityGraphicsLayer.graphics?.length > 0;
  }
  public get isPolygonExist(){
    if(this.taxMapGraphicLayer.graphics?.length > 0 || this.facilityGraphicsLayer.graphics?.length > 0 || this.sketchLayer.graphics?.length > 0 || this.taxParcelGraphicsLayer.graphics?.length > 0 || this.shapeGraphicsLayer.graphics?.length > 0){
      return true;
    }else{
      if(this.workAreaGraphicsLayer.graphics?.length>0){
        this.workAreaGraphicsLayer.graphics.removeAll();
        this.workAreaGeometry.emit(this.workAreaGraphicsLayer.graphics);
      }
      return false;
    }
  }
  private async getExtent(): Promise<void> {
    let graphics = new Array<Graphic>();
    if (this.taxMapGraphicLayer.graphics?.length > 0) {
      this.taxMapGraphicLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
    }
    if (this.facilityGraphicsLayer.graphics?.length > 0) {
      this.facilityGraphicsLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
    }
    if (this.sketchLayer.graphics?.length > 0) {
      this.sketchLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
    }
    if (this.taxParcelGraphicsLayer.graphics?.length > 0) {
      this.taxParcelGraphicsLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
    }
    if (this.shapeGraphicsLayer.graphics?.length > 0) {
      this.shapeGraphicsLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
    }
    if (this.workAreaGraphicsLayer.graphics?.length > 0) {
      this.workAreaGraphicsLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
    }
    graphics.forEach((graphic) => {
      graphics.push(graphic);
    });
    this.mapView.goTo(graphics);
  }
}

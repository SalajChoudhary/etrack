import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  SimpleChange,
  ViewChild,
} from '@angular/core';
import config from '@arcgis/core/config';
import Polygon from '@arcgis/core/geometry/Polygon';
import Graphic from '@arcgis/core/Graphic';
import FeatureLayer from '@arcgis/core/layers/FeatureLayer';
import GraphicsLayer from '@arcgis/core/layers/GraphicsLayer';
import SimpleFillSymbol from '@arcgis/core/symbols/SimpleFillSymbol';
import MapView from '@arcgis/core/views/MapView';
import WebMap from '@arcgis/core/WebMap';
import SketchViewModel from '@arcgis/core/widgets/Sketch/SketchViewModel';
import { environment } from 'src/environments/environment';
import SpatialReference from '@arcgis/core/geometry/SpatialReference';
import LabelClass from '@arcgis/core/layers/support/LabelClass';
import { GisService } from 'src/app/@shared/services/gisService';
import { CommonService } from 'src/app/@shared/services/commonService';
import Point from '@arcgis/core/geometry/Point';
import Extent from '@arcgis/core/geometry/Extent';
import { CivilDivision } from 'src/app/@store/models/civilDivision';
import { PolygonStatus } from 'src/app/@store/models/PolygonStatus';
import { Utils } from 'src/app/@shared/services/utils';
import Sketch from '@arcgis/core/widgets/Sketch';
import Query from '@arcgis/core/rest/support/Query';
import { TaxMap } from 'src/app/@store/models/TaxMap';
import { InquiryService } from 'src/app/@shared/services/inquiryService';

@Component({
  selector: 'app-gis-map-si',
  templateUrl: './gis-map-si.component.html',
  styleUrls: ['./gis-map-si.component.scss'],
})
export class GisMapSiComponent implements OnInit, OnDestroy {
  ext = new Extent({
    xmin: -8879060.2957,
    ymin: 4935440.9267,
    xmax: -7978133.1733,
    ymax: 5624020.3215,
    spatialReference: new SpatialReference({ wkid: 102100 }),
  });

  outLineSymbol=new SimpleFillSymbol({
    color: [146, 172, 160, 0.001],
    outline: {
      color:'#BFDBFE',
      width: 3,
    },
  });

  taxParcelInternalLayerQuery = new FeatureLayer({
    url: `${environment.taxParcelQuery}`,
    outFields: ['*'],
    listMode: 'hide',
  });

  regionsLayerQuery = new FeatureLayer({
    url: `${environment.decRegionsQuery}`,
    outFields: ['*'],
    listMode: 'hide',
  });

  countiesQuery = new FeatureLayer({
    url: `${environment.countiesQuery}`,
    outFields: ['*'],
    listMode: 'hide',
  });

  municipalityGraphicsLayer = new GraphicsLayer({
    id: 'municipality_sketch',
    title: 'Municipality Layer',
    listMode:'hide',
  });

  municipalitiesQuery = new FeatureLayer({
    url: `${environment.municipalitiesQuery}`,
    outFields: ['NAME'],
    listMode: 'hide',
  });

  taxParcelLabel = new LabelClass({
    symbol: {
      type: 'text',
      color: '#FF5733',
      haloColor: 'white',
      haloSize: '3px',
      font: {
        family: 'Arial',
        size: 9,
        weight: 'bold',
      },
    },
    labelPlacement: 'always-horizontal',
    labelExpressionInfo: {
      expression: "Upper($feature.PARCEL_ADDR) + TextFormatting.NewLine + DefaultValue($feature.PRINT_KEY, $feature.SBL)",
    },
    maxScale: 0,
    minScale: 3000,
  });

  taxParcelLayer = new FeatureLayer({
    id: 'tax_parcel_layer',
    url: `${environment.taxParcelUrl}`,
    outFields: ['*'],
    labelingInfo: [this.taxParcelLabel],
    maxScale: 0,
    minScale: 35000,
    spatialReference: { wkid: 102100 },
    listMode: 'hide',
  });

  decRegionsLayer = new FeatureLayer({
    url: `${environment.decRegionsUrl}`,
    outFields: ['*'],
    visible: false,
    spatialReference: { wkid: 102100 },
    listMode: 'hide',
  });

   projectGraphicsLayer = new GraphicsLayer({
    id: 'project_sketch',
    title: 'Project Layer',
    listMode: 'hide',
  });



  private inquiryLayerQuery = new FeatureLayer({
    url: `${environment.inquiryPolyUrl}`,
    outFields: ['*'],
    listMode: 'hide',
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
      expression: "Upper($feature.PARCEL_ADDR) + TextFormatting.NewLine + DefaultValue($feature.PRINT_KEY, $feature.SBL)",
    },
    maxScale: 0,
    minScale: 5000,
  });


  private _projectGraphic!: Graphic;
  private _taxMap!:TaxMap;

  @Input()
  set projectGraphic(projectGraphic: Graphic) {
    this._projectGraphic = projectGraphic;
  }

  get projectGraphic(): Graphic {
    return this._projectGraphic;
  }
  @Input()
  set taxMap(taxMap: TaxMap) {
    this._taxMap = taxMap;
  }

  get taxMap(): TaxMap {
    return this._taxMap;
  }

  private _customTextElements!:string;

  @Input()
  set customTextElements(customTextElements: any) {
    this._customTextElements = customTextElements;
  }

  get customTextElements():any {
    return this._customTextElements;
  }

  @ViewChild('mapViewDisplay', { static: true }) private elementRef!: ElementRef;
  @ViewChild('textInputDiv', { static: true }) private textInputDiv!: ElementRef;
  @ViewChild('textButton', { static: true }) private textButton!: ElementRef;
  @ViewChild('resetButton', { static: true }) private resetButton!: ElementRef;
  @ViewChild('textInput', { static: true }) private textInput!: ElementRef;
  @ViewChild('textTool', { static: true }) private textTool!: ElementRef;
  @ViewChild('printButton', { static: true }) private printButton!: ElementRef;

  @Input() mapProperties!: any;
  @Input() polygonType!: string;
  @Input() showFacility: boolean = false;
  @Input() isReadonly: boolean = false;
  @Output() mapInit: EventEmitter<boolean> = new EventEmitter();

  @Output() taxParcelNumbers = new EventEmitter<string>();
  @Output() counties = new EventEmitter<string>();
  @Output() municipalities = new EventEmitter<string>();
  @Output() regions = new EventEmitter<string>();
  @Output() polygonExist = new EventEmitter<number>();
  @Output() streetNames = new EventEmitter<string>();

  private mapView: any;
  private map: any;
  private sketchViewModel!: SketchViewModel;
  private sketchWidget!: Sketch;
  private textSketchViewModel!:SketchViewModel;
  polygonStatus= new PolygonStatus();

  printHide:boolean=false;

  constructor(
    private gisService: GisService,
    public commonService: CommonService,
    private inquiryService:InquiryService,
    public utils: Utils
  ) {}

  ngOnInit(): void {
    config.assetsPath = 'assets/';
    this.loadMap();
    this.gisService.addBaseLayers(this.mapView);
    this.gisService.addMaskLayers(this.mapView);
    this.gisService.addFullScreen(this.mapView);
    this.addPrintOptions();
    this.gisService.buildJurisdictionalLayers(this.mapView);
    this.addLayers();
    if(!this.isReadonly){
      this.addSketchTool();
    }
    this.textSketchViewModel=this.gisService.addNotesInteractions(this.mapView, this.textInputDiv, this.textTool, this.textButton, this.resetButton, this.textInput,this.polygonStatus);
    if(!this.isReadonly){
      this.setUpGraphicClickHandler();
    }
  }

  ngOnDestroy(): void {
    this.mapView.destroy();
  }

  ngOnChanges(changes: { [property: string]: SimpleChange }): void {
    // Extract changes to the input property by its name
    if (changes['projectGraphic'] !== undefined) {
      const change: SimpleChange = changes['projectGraphic'];
      if (change.currentValue) {
        this.refreshProjectGraphicsLayer(change.currentValue);
      }
    }
    if (changes['taxMap'] !== undefined) {
      const change: SimpleChange = changes['taxMap'];
      if (change.currentValue) {
        this.gotoTaxMap(change.currentValue);
      }
    }
  }

  private loadWebMap(props: {
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
      constraints: {
        rotationEnabled: false,
      },
      popup:{
        dockEnabled:false,
        dockOptions:{
          buttonEnabled:false,
          breakpoint:false,
        }
      }
    });
  }

  private loadMap(): void {
    this.loadWebMap({
      ...this.mapProperties,
      container: this.elementRef.nativeElement,
    });
    this.mapInit.emit(true);
  }
  public async gotoTaxParcel(taxmap: TaxMap): Promise<void> {
    if (taxmap.geometry !== undefined && taxmap.geometry.rings !== undefined) {
      let polygon = new Polygon({
        rings: taxmap.geometry.rings,
      });
      this.mapView.goTo(polygon, { duration: 1000, easing: 'ease-in-out' });
    }
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

  private refreshTaxParcelWithTaxMap(){
    this.taxParcelLayer.labelingInfo.pop();
    this.taxParcelLayer.labelingInfo.push(this.taxParcelLabel_taxMap);
    this.taxParcelLayer.refresh();
  }

  private async gotoTaxMap(taxmap: TaxMap): Promise<void> {
    if (taxmap.geometry !== undefined && taxmap.geometry.rings !== undefined) {
      const polygon = new Polygon({
        rings: taxmap.geometry.rings,
      });
      const graphic = new Graphic({
        geometry: polygon,
        symbol:this.outLineSymbol,
      });
      this.mapView.goTo(graphic);
    }
  }

  private addPrintOptions(): void {
    this.mapView.ui.add(this.printButton.nativeElement, 'top-right');
    this.mapView.watch('zoom', (newValue: any, oldValue: any, property: any, object: any) => {
      if (newValue > 19.0) {
        this.printHide = true;
      }
      else {
        this.printHide = false;
      }
    });
  }
  public async getPrintUrl(customElements:any): Promise<string> {
    this.utils.emitLoadingEmitter(true);
    await this.getAsyncExtent();
    let printUrl: string = '';
    console.log(this.mapView.zoom);
    if (this.mapView.zoom > 19.0) {
     this.mapView.zoom = 19
     console.log(this.mapView.zoom);
    await this.gisService.printMap(this.mapView, customElements, "etrack_geo_inquiry").then((printResult: any) => {
        this.utils.emitLoadingEmitter(false);
        printUrl = printResult.url
      }, (printError: any) => {
        this.utils.emitLoadingEmitter(false);
        console.log(printError);
      });
    }else{
      await this.gisService.printMap(this.mapView, customElements, "etrack_geo_inquiry").then((printResult: any) => {
        this.utils.emitLoadingEmitter(false);
        printUrl = printResult.url
      }, (printError: any) => {
        this.utils.emitLoadingEmitter(false);
        console.log(printError);
      });
    }
    return printUrl;
  }

  public async printMap(): Promise<void> {
    this.utils.emitLoadingEmitter(true);
    this.gisService.printMap(this.mapView, this.customTextElements, "etrack_geo_inquiry").then((printResult: any) => {
        this.utils.emitLoadingEmitter(false);
        window.open(printResult.url, '_blank');
      }, (printError: any) => {
        this.utils.emitLoadingEmitter(false);
        console.log(printError);
      });
  }

  private addLayers(): void {
    this.mapView.map.layers.add(this.decRegionsLayer);
    this.mapView.map.layers.add(this.taxParcelLayer);
    this.mapView.map.layers.add(this.projectGraphicsLayer);
  }



  public cleanProjectGraphic() {
    this.projectGraphicsLayer.removeAll();
    this.mapView.extent = this.ext;
  }

  public async addSketchTool(): Promise<void> {
    this.sketchWidget = new Sketch({
      layer: this.projectGraphicsLayer,
      availableCreateTools: ['polygon', 'move', 'transform', 'reshape'],
      creationMode: 'update',
      layout: 'vertical',
      view: this.mapView,
    });
    this.sketchWidget.visibleElements = {
      settingsMenu: false,
      selectionTools: {
        'lasso-selection': false,
        'rectangle-selection': false,
      },
    };
    this.sketchWidget.on('create', async (event: any) => {
      if (event.state === 'complete') {
        this.polygonStatus.isProjectLayerSelect = true;
        this.processAllAttributes();
        await this.getAsyncExtent();
      }
    });
    this.sketchWidget.on('update', async (event: any) => {
      if (event.state === 'complete') {
        this.polygonStatus.isProjectLayerSelect = true;
        if(this.projectGraphicsLayer?.graphics?.length>0){
          await this.getAsyncExtent();
        }else{
          this.polygonStatus.isProjectLayerSelect = false;
        }
        this.processAllAttributes();
      }
    });
    this.sketchWidget.on("delete", (event:any) =>{
      this.polygonStatus.isProjectLayerSelect = false;
      this.processAllAttributes();
    });
    this.mapView.ui.add(this.sketchWidget, 'bottom-right');
  }

  public async setUpGraphicClickHandler() {
    this.sketchViewModel = new SketchViewModel({
      view: this.mapView,
      layer: this.projectGraphicsLayer,
      updateOnGraphicClick: false,
    });
    this.mapView.on('click', async (event: any) => {
      if(this.mapView.map?.findLayerById('inquiry_layer')?.visible){
        try {
          let inquiryQuery = new Query({
            spatialRelationship: 'intersects',
            geometry: event.mapPoint,
            outFields: ['*'],
            where:"RECEIVED_DATE is NOT NULL",
            returnGeometry:false,
            returnCentroid: true,
          });
          let sometext="";
          await this.inquiryLayerQuery.queryFeatures(inquiryQuery).then((results: any) => {
            if(results?.features?.length >1){
                sometext="<table style='border: 1px solid #CCC;border-collapse: collapse;'><tr><th>Inquiry Id</th><th>Inquiry Type</th><th>Request Identifier</th><th>Complete Date</th></tr>"
              results?.features?.forEach((feature:any)=>{
                if(feature.attributes?.RESPONSE_DATE !==null){
                  sometext+="<tr style='border: 1px solid #CCC;'><td style='border: 1px solid #CCC;'>"+ this.inquiryService.formatInquiryId(feature.attributes.SI_ID) + "</td><td style='border: 1px solid #CCC;'>"+ feature.attributes.SI_TYPE + "</td><td style='border: 1px solid #CCC;'>"+ feature.attributes?.REQUEST_IDENTIFIER+ "</td><td style='border: 1px solid #CCC;'>"+ (new Date(feature.attributes?.RESPONSE_DATE)).toLocaleDateString('en-US', { timeZone: "UTC" })+"</td></tr>";
                }else{
                  sometext+="<tr style='border: 1px solid #CCC;'><td style='border: 1px solid #CCC;'>"+ this.inquiryService.formatInquiryId(feature.attributes.SI_ID) + "</td><td style='border: 1px solid #CCC;'>"+ feature.attributes.SI_TYPE + "</td><td style='border: 1px solid #CCC;'>"+ feature.attributes?.REQUEST_IDENTIFIER+ "</td><td style='border: 1px solid #CCC;'> </td></tr>";
                }
              });
              sometext+="</table>"
            }
            if(results?.features?.length ===1 ){
              if(results.features[0].attributes?.RESPONSE_DATE !==null){
                sometext= "<strong>Inquiry Id: </strong>"+this.inquiryService.formatInquiryId(results?.features[0].attributes.SI_ID)+"<br/>" + "<strong>Inquiry Type:</strong> "+ results?.features[0].attributes.SI_TYPE+"<br/>" + "<strong>Request Identifier:</strong> "+ results?.features[0].attributes.REQUEST_IDENTIFIER +"<br/>" + "<strong>Complete Date:</strong> "+ (new Date(results?.features[0].attributes.RESPONSE_DATE)).toLocaleDateString('en-US', { timeZone: "UTC" });
              }else{
                sometext= "<strong>Inquiry Id: </strong>"+this.inquiryService.formatInquiryId(results?.features[0].attributes.SI_ID)+"<br/>" + "<strong>Inquiry Type:</strong> "+ results?.features[0].attributes.SI_TYPE+"<br/>" + "<strong>Request Identifier:</strong> "+ results?.features[0].attributes.REQUEST_IDENTIFIER +"<br/>" + "<strong>Complete Date:</strong> ";
              }
            }

        });
        let popupData={
          location: event.mapPoint,
          content: sometext,
          actions: [],
          overwriteActions: true,
         };
         console.log(popupData)
         if(sometext!==""){
            this.mapView.popup.open(popupData);
         }
        } catch (error) {
          console.error("query failed: ", error);
        }
      }
      this.mapView.hitTest(event).then((response: any) => {
        let results = response.results;
        if (this.sketchViewModel.state ==='active' || this.textSketchViewModel.state === 'active') {
          return;
        }
        let projectPolygon = results.filter(function (result: any) {
          return result.graphic.layer?.id === 'project_sketch';
        })[0]?.graphic;
        if (projectPolygon !== undefined) {
          this.polygonStatus.isProjectLayerSelect = true;
          this.getExtent();
          this.sketchViewModel.update([projectPolygon], { tool: 'reshape' });
          this.processAllAttributes();
        } else {
          if (!this.polygonStatus.isProjectLayerSelect) {
            let taxGraphic = results.filter(function (result: any) {
              return result.graphic.layer?.id === 'tax_parcel_layer';
            })[0]?.graphic;
            if (taxGraphic !== undefined) {
              let t_polygon = new Polygon({
                rings: (taxGraphic.geometry as Polygon).rings,
                spatialReference: new SpatialReference({
                  wkid: taxGraphic.geometry.spatialReference.wkid,
                }),
              });
              let t_graphic = new Graphic({
                geometry: t_polygon,
                attributes: taxGraphic.attributes,
                symbol: new SimpleFillSymbol({
                  color: [44, 82, 52, 0.3],
                  outline: {
                    color: '#2196F3',
                    width: 3,
                  },
                }),
              });
              this.projectGraphicsLayer.add(t_graphic);
              this.getExtent();
              this.processAllAttributes();
            }
          } else {
            this.polygonStatus.isProjectLayerSelect = false;
          }
        }
      });
    });
  }

  public getGraphicAsJsonString(attributes:any): string {
    let graphic=this.combineAllGeometry();
    graphic.attributes = attributes;
    return JSON.stringify(graphic);
  }

  public combineAllGeometry(): Graphic {
    let rings: number[][][] = [];
    let spatialReference = { wkid: 102100 };
    this.projectGraphicsLayer.graphics.forEach((graphic: any) => {
      (graphic.geometry as Polygon).rings.forEach((ring: any) => {
        rings.push(ring);
      });
    });
    let polygon = new Polygon({
      rings: rings,
      spatialReference: spatialReference,
    });
    let graphic = new Graphic({
      geometry: polygon,
    });
    return graphic;
  }

  public async processAllAttributes(): Promise<void> {
    let rings: number[][][] = [];
    let rings_partial: number[][][] = [];
    let spatialReference = { wkid: 102100 };
    let parcels = new Set<string>();

    this.projectGraphicsLayer.graphics.forEach((graphic: any) => {
      if (graphic.attributes?.PRINT_KEY === undefined && graphic.attributes?.SBL === undefined) {
        (graphic.geometry as Polygon).rings.forEach((ring: any) => {
          rings_partial.push(ring);
        });
      }
      if(graphic.attributes?.SBL !== undefined && (graphic.attributes?.PRINT_KEY === undefined || graphic.attributes?.PRINT_KEY === null || graphic.attributes?.PRINT_KEY === '')){
        parcels.add(graphic.attributes?.SBL);
      }
      if (graphic.attributes?.PRINT_KEY !== undefined && graphic.attributes?.PRINT_KEY !== null && graphic.attributes?.PRINT_KEY !== '') {
        parcels.add(graphic.attributes?.PRINT_KEY);
      }
      (graphic.geometry as Polygon).rings.forEach((ring: any) => {
        rings.push(ring);
      });
    });
    let polygon = new Polygon({
      rings: rings,
      spatialReference: spatialReference,
    });
    let fullGraphic = new Graphic({
      geometry: polygon,
    });
    let regions = await this.gisService.queryRegionsAt(fullGraphic);
    let counties = await this.gisService.queryCountiesAt(fullGraphic);
    let municipalities = await this.gisService.queryMunicipalitiesAt(fullGraphic);
    let polygon_partial = new Polygon({
      rings: rings_partial,
      spatialReference: spatialReference,
    });
    let partialGraphic = new Graphic({
      geometry: polygon_partial,
    });
    let partial_parcels = await this.gisService.queryTaxParcelsOnlyAt(partialGraphic);
    if(partial_parcels!==undefined && partial_parcels.trim()!==''){
      let full_Parcels=partial_parcels+','+[...parcels].join(',');
      this.taxParcelNumbers.emit(full_Parcels);
    }else{
      this.taxParcelNumbers.emit([...parcels].join(','));
    }

    if(regions!==undefined){
      this.regions.emit(regions);
    }
    let countyNames=new Set<string>();
    counties.forEach((civilDivision:CivilDivision)=>{
      countyNames.add(civilDivision.name);
    })
    if(countyNames!==undefined && countyNames.size >0){
      this.counties.emit([...countyNames].join(', '));
    }
    let municipalityNames=new Set<string>();
    municipalities.forEach((civilDivision:CivilDivision)=>{
      municipalityNames.add(civilDivision.name);
    })
    if(municipalityNames!==undefined && municipalityNames.size >0){
      this.municipalities.emit([...municipalityNames].join(', '));
    }
  }

  public async refreshProjectGraphicsLayer(
    projectGraphic: Graphic
  ): Promise<void> {
    this.projectGraphicsLayer.graphics?.removeAll();
    if (
      projectGraphic.geometry !== undefined &&
      projectGraphic.geometry !== null
    ) {
      let graphic = new Graphic({
        geometry: projectGraphic.geometry as Polygon,
        attributes: projectGraphic.attributes,
        symbol: new SimpleFillSymbol({
          color: [44, 82, 52, 0.3],
          outline: {
            color: '#2196F3',
            width: 3,
          },
        }),
      });
      this.projectGraphicsLayer.graphics.add(graphic);
      await this.getAsyncExtent();
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

  public async getAsyncExtent(): Promise<void> {
    if(this.sketchWidget?.state ==='active'){
      this.sketchViewModel?.complete();
      this.sketchWidget?.complete();
   }
    let graphics = new Array<Graphic>();
    if (this.projectGraphicsLayer.graphics?.length > 0) {
      this.projectGraphicsLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
      this.polygonExist.emit(1);
    }else{
      this.polygonExist.emit(0);
    }
    if (this.mapView.animation) {
      await this.mapView.animation.when();
    }
    await this.mapView.goTo(graphics);
  }
  public getExtent(): void {
    let graphics = new Array<Graphic>();
    if (this.projectGraphicsLayer.graphics?.length > 0) {
      this.projectGraphicsLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
      this.polygonExist.emit(1);
    }else{
      this.polygonExist.emit(0);
    }
    this.mapView.goTo(graphics);
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
      symbol:this.outLineSymbol,
    });
    this.municipalityGraphicsLayer.graphics.add(graphic);
    this.mapView.goTo((results.features[0].geometry as Polygon).extent, {
      duration: 200,
      easing: 'ease-in-out',
    });
  }
  }
  public resetExtent(): void {
    this.mapView.extent = this.ext;
  }
}

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
import MapView from '@arcgis/core/views/MapView';
import WebMap from '@arcgis/core/WebMap';
import SketchViewModel from '@arcgis/core/widgets/Sketch/SketchViewModel';
import { environment } from 'src/environments/environment';
import SpatialReference from '@arcgis/core/geometry/SpatialReference';
import LabelClass from '@arcgis/core/layers/support/LabelClass';
import SimpleRenderer from '@arcgis/core/renderers/SimpleRenderer';
import { GisService } from 'src/app/@shared/services/gisService';
import Sketch from '@arcgis/core/widgets/Sketch';
import { CommonService } from 'src/app/@shared/services/commonService';
import SimpleFillSymbol from '@arcgis/core/symbols/SimpleFillSymbol';
import { PolygonStatus } from 'src/app/@store/models/PolygonStatus';
import { Utils } from 'src/app/@shared/services/utils';
import { CivilDivision } from 'src/app/@store/models/civilDivision';
import { GISLocation } from 'src/app/@store/models/gisLocation';
import * as projection from "@arcgis/core/geometry/projection.js";
import Point from '@arcgis/core/geometry/Point';
import * as geometryEngine from "@arcgis/core/geometry/geometryEngine.js";
import Query from '@arcgis/core/rest/support/Query';
import { InquiryService } from 'src/app/@shared/services/inquiryService';

@Component({
  selector: 'app-gis-map-editor',
  templateUrl: './gis-map-editor.component.html',
  styleUrls: ['./gis-map-editor.component.scss'],
})
export class GisMapEditorComponent implements OnInit, OnDestroy {

  NAD83 = new SpatialReference({ wkid: 26918 });

  outLineSymbol = new SimpleFillSymbol({
    color: [146, 172, 160, 0.001],
    outline: {
      color: '#FF51F3',
      width: 5,
    },
  });

  outLineSubmittalSymbol = new SimpleFillSymbol({
    color: [32, 144, 235, 0.2],
    style: 'solid',
    outline: {
      color: '#2090EB',
      width: 3,
    },
  });

  outLineFacilitySymbol = new SimpleFillSymbol({
    color: '#56F7F9',
    style: "backward-diagonal",
    outline: {
      color: '#56F7F9',
      width: 3,
    },
  });

  outLineWorkAreaSymbol = new SimpleFillSymbol({
    color: [146, 172, 160, 0.001],
    outline: {
      color: '#FF51F3',
      width: 3,
      style: 'dash-dot',
    },
  });

  polygonRenderer = new SimpleRenderer({
    symbol: this.outLineSymbol,
  });

  submitPolygonRenderer = new SimpleRenderer({
    symbol: this.outLineSubmittalSymbol,
  });

  facilityPolygonRenderer = new SimpleRenderer({
    symbol: this.outLineFacilitySymbol,
  });

  submittedPolygonsLayerValidate = new FeatureLayer({
    url: `${environment.submittedPolygonUrl}`,
    outFields: ['APPL_SUB_ID'],
    id: 'submitted_validate_polygon',
    editingEnabled: false,
    renderer: this.submitPolygonRenderer,
    spatialReference: { wkid: 102100 },
    listMode: 'hide',
  });

  facilityPolygonsLayerValidate = new FeatureLayer({
    url: `${environment.facilityLayer}`,
    outFields: ['PRIMARY_ID'],
    id: 'facility_polygon',
    editingEnabled: false,
    renderer: this.facilityPolygonRenderer,
    spatialReference: { wkid: 102100 },
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
      expression: 'Upper($feature.PARCEL_ADDR)',
    },
    maxScale: 0,
    minScale: 3000,
  });

  taxParcelLabelValidate = new LabelClass({
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
      expression: 'Upper($feature.PARCEL_ADDR) + TextFormatting.NewLine+ $feature.PRIMARY_OWNER',
    },
    maxScale: 0,
    minScale: 3000,
  });

  taxParcelLayer = new FeatureLayer({
    id: 'tax_parcel_layer',
    url: `${environment.taxParcelUrl}`,
    outFields: ['*'],
    //labelingInfo: [this.taxParcelLabel],
    maxScale: 0,
    minScale: 40000,
    spatialReference: { wkid: 102100 },
    listMode: 'hide',
  });

  private inquiryLayerQuery = new FeatureLayer({
    url: `${environment.inquiryPolyUrl}`,
    outFields: ['*'],
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

  submittalGraphicsLayer = new GraphicsLayer({
    id: 'sub_sketch',
    title: 'Submitted  Layer',
    listMode: 'hide',
    visible: false,
  });

  facilityGraphicsLayer = new GraphicsLayer({
    id: 'facility_sketch',
    title: 'Facility Layer',
    listMode: 'hide',
    visible: false,
  });

  workAreaGraphicsLayer = new GraphicsLayer({
    id: 'work_area_sketch',
    title: 'Work Area Layer',
    listMode: 'hide',
  });

  workAreaEnabled: boolean = false;
  // sketchEnabled:boolean = false;

  @ViewChild('mapViewDisplay', { static: true }) private elementRef!: ElementRef;
  @ViewChild('textInputDiv', { static: true }) private textInputDiv!: ElementRef;
  @ViewChild('textButton', { static: true }) private textButton!: ElementRef;
  @ViewChild('resetButton', { static: true }) private resetButton!: ElementRef;
  @ViewChild('textInput', { static: true }) private textInput!: ElementRef;
  @ViewChild('textTool', { static: true }) private textTool!: ElementRef;
  @ViewChild('submitLayerDiv', { static: true }) private submitLayerDiv!: ElementRef;
  @ViewChild('applicantInput', { static: true }) private applicantInput!: ElementRef;
  @ViewChild('facilityInput', { static: true }) private facilityInput!: ElementRef;
  @ViewChild('printButton', { static: true }) private printButton!: ElementRef;
  @ViewChild('workAreaDiv', { static: true }) private workAreaDiv!: ElementRef;
  @ViewChild('workArea', { static: true }) private workArea!: ElementRef;

  @Input() mapProperties!: any;
  @Input() polygonType!: string;
  @Input() showFacility: boolean = false;
  @Input() isValidated: boolean = false;
  @Input() isSubmitted: boolean = false;

  @Output() mapInit: EventEmitter<boolean> = new EventEmitter();
  @Output() taxParcelNumbers = new EventEmitter<string>();
  @Output() counties = new EventEmitter<Set<CivilDivision>>();
  @Output() municipalities = new EventEmitter<Set<CivilDivision>>();
  @Output() primaryMunicipality = new EventEmitter<CivilDivision>();
  @Output() gisLocation = new EventEmitter<GISLocation>();
  @Output() regions = new EventEmitter<string>();
  @Output() primaryRegion = new EventEmitter<string>();
  @Output() workAreaPolygonModified = new EventEmitter<boolean>();

  private _decId!: string;
  private _projectGraphic!: Graphic;
  private _workAreaGraphic!: Graphic;
  private _customTextElements!: string;

  private sketchViewModel!: SketchViewModel;
  private sketchWidget!: Sketch;
  private workAreaSketchWidget!: Sketch;
  private textSketchViewModel!: SketchViewModel;

  private mapView: any;
  private map: any;

  polygonStatus = new PolygonStatus();

  hideToggleLayer: boolean = true;
  printHide: boolean = false;
  mode = localStorage.getItem('mode');

  validPolygon!: Graphic;

  @Input()
  set customTextElements(customTextElements: any) {
    this._customTextElements = customTextElements;
  }

  @Input()
  set projectGraphic(projectGraphic: Graphic) {
    this._projectGraphic = projectGraphic;
  }

  @Input()
  set workAreaGraphic(workAreaGraphic: Graphic) {
    this._workAreaGraphic = workAreaGraphic;
  }

  @Input()
  set decId(decId: string) {
    this._decId = decId;
  }

  get customTextElements(): any {
    return this._customTextElements;
  }

  get projectGraphic(): Graphic {
    return this._projectGraphic;
  }

  get workAreaGraphic(): Graphic {
    return this._workAreaGraphic;
  }

  get decId(): string {
    return this._decId;
  }

  get isDecIdExist(): boolean {
    if (this.decId === undefined || this.decId === '' || this.decId === null) {
      return false;
    }
    return true;
  }

  constructor(private gisService: GisService, public commonService: CommonService, private inquiryService:InquiryService,public utils: Utils) { }

  ngOnInit(): void {
    config.assetsPath = 'assets/';
    this.changeOutLineSymbol();
    this.loadMap();
    this.gisService.addBaseLayers(this.mapView);
    this.gisService.addFullScreen(this.mapView);
    this.addPrintOptions();
    this.addLayers();
    if (this.mode == 'validate') {
      this.gisService.buildJurisdictionalLayers(this.mapView);
      this.taxParcelLayer.labelingInfo = [this.taxParcelLabelValidate];
    } else {
      this.taxParcelLayer.labelingInfo = [this.taxParcelLabel];
    }
    this.addWorkAreaTool();
    this.textSketchViewModel = this.gisService.addNotesInteractions(this.mapView, this.textInputDiv, this.textTool, this.textButton, this.resetButton, this.textInput, this.polygonStatus);
  }
  ngOnDestroy(): void {
    this.mapView.destroy();
  }

  ngOnChanges(changes: { [property: string]: SimpleChange }): void {
    // Extract changes to the input property by its name
    if (changes['projectGraphic'] !== undefined) {
      let change: SimpleChange = changes['projectGraphic'];
      if (change.currentValue) {
        this.refreshProjectGraphicsLayer(change.currentValue);
      }
    }
    if (changes['workAreaGraphic'] !== undefined) {
      let change: SimpleChange = changes['workAreaGraphic'];
      if (change.currentValue) {
        this.refreshWorkAreaGraphicsLayer(change.currentValue);
      }
    }
    if (changes['isValidated'] !== undefined) {
      let change: SimpleChange = changes['isValidated'];
      if (change.currentValue) {
        if (this.isValidated) {
          this.resetWidgets();
          /*f (this.sketchWidget !== undefined) {
            this.sketchWidget.destroy();
            this.sketchViewModel.cancel();
          }
          if (this.workAreaSketchWidget !== undefined) {
            this.workAreaSketchWidget.destroy();
            this.sketchViewModel.cancel();
            this.workAreaEnabled=false;
            this.workArea.nativeElement.classList.remove('active');
          }*/
        }
      }
    }
  }

  public resetWidgets() {
    if (this.sketchWidget !== undefined) {
      this.sketchWidget.destroy();
      this.sketchViewModel.cancel();
    }
    if (this.workAreaSketchWidget !== undefined) {
      this.workAreaSketchWidget.destroy();
      this.sketchViewModel.cancel();
      this.workAreaEnabled = false;
      this.workArea.nativeElement.classList.remove('active');
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
    });
  }

  private loadMap(): void {
    this.loadWebMap({
      ...this.mapProperties,
      container: this.elementRef.nativeElement,
    });
    this.mapInit.emit(true);
  }

  private changeOutLineSymbol() {
    if (this.mode == 'validate') {
      this.outLineSymbol = new SimpleFillSymbol({
        color: [146, 172, 160, 0.001],
        outline: {
          color: '#FF51F3',
          width: 5,
        },
      });
    } else {
      this.outLineSymbol = this.outLineSubmittalSymbol;
    }
  }

  private async addWorkAreaTool(): Promise<void> {
    this.mapView.ui.add(this.workAreaDiv.nativeElement, 'top-left');
  }

  public async toggleWorkAreaSketch(): Promise<void> {
    this.workAreaEnabled = !this.workAreaEnabled;
    this.polygonStatus.isWorkAreaLayerSelect = this.workAreaEnabled;
    this.polygonStatus.isProjectLayerSelect = !this.polygonStatus.isWorkAreaLayerSelect;
    if (this.workAreaEnabled) {
      this.workArea.nativeElement.classList.add('active');
      this.sketchWidget?.destroy();
      this.setUpSketchViewModel(this.workAreaGraphicsLayer);
      this.addWorkAreaSketchTool();
    } else {
      this.workArea.nativeElement.classList.remove('active');
      this.workAreaSketchWidget?.destroy();
      // this.setUpSketchViewModel(this.projectGraphicsLayer);
      this.addSketch();
    }
  }

  public async resetSketchWidget(): Promise<void> {
    if (this.sketchWidget?.destroyed) {
      this.setUpSketchViewModel(this.projectGraphicsLayer);
      this.addSketchTool();
    }
  }

  private addWorkAreaSketchTool() {
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
        this.polygonStatus.isWorkAreaLayerSelect = true;
        this.workAreaPolygonModified.emit(true);
      }
    });
    this.workAreaSketchWidget.on('update', (event: any) => {
      if (event.state === 'complete') {
        this.polygonStatus.isWorkAreaLayerSelect = true;
        this.workAreaPolygonModified.emit(true);
        if (this.workAreaGraphicsLayer?.graphics?.length > 0) {
          this.workAreaGraphicsLayer?.graphics?.map((graphic) => {
            graphic.symbol = this.outLineWorkAreaSymbol;
          });
          this.getExtent();
        } else {
          this.polygonStatus.isWorkAreaLayerSelect = false;
        }
      }
    });
    this.workAreaSketchWidget.on("delete", (event: any) => {
      this.polygonStatus.isWorkAreaLayerSelect = false;
      this.workAreaPolygonModified.emit(true);
    });
    this.mapView.ui.add(this.workAreaSketchWidget, 'bottom-right');
  }

  private async addSketch(): Promise<void> {
    this.sketchWidget?.destroy();
    this.setUpSketchViewModel(this.projectGraphicsLayer);
    this.addSketchTool();
}


  public addSketchTool() {
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
    this.sketchWidget.on('create', (event: any) => {
      this.polygonStatus.isProjectLayerSelect = true;
      if (event.state === 'complete') {
        this.projectGraphicsLayer?.graphics?.map((graphic) => {
          graphic.symbol = this.outLineSymbol;
        });
        this.processAllAttributes();
        this.getExtent();
      }
    });
    this.sketchWidget.on('update', (event: any) => {
      if (!this.workAreaEnabled) {
        this.polygonStatus.isProjectLayerSelect = true;
        if (event.state === 'complete') {
          if (this.projectGraphicsLayer?.graphics?.length > 0) {
            this.projectGraphicsLayer?.graphics?.map((graphic) => {
              graphic.symbol = this.outLineSymbol;
            });
            this.getExtent();
          } else {
            this.polygonStatus.isProjectLayerSelect = false;
          }
          this.processAllAttributes();
        }
      }
      // setTimeout(()=>{
      //   if(this.sketchWidget!== null){
      //     this.sketchWidget?.cancel();
      //   }
      // },180000);
    });
    this.sketchWidget.on("delete", (event: any) => {
      this.polygonStatus.isProjectLayerSelect = true;
    });
    // this.mapView.ui.empty("bottom-right");
    this.mapView.ui.add(this.sketchWidget, 'bottom-right');
  }

  public get isPolygonExist(){
    if(this.projectGraphicsLayer.graphics?.length > 0 ){
      return true;
    }else{
      return false;
    }
  }

  private async addPrintOptions(): Promise<void> {
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
  public async getPrintUrl(): Promise<string> {
    this.utils.emitLoadingEmitter(true);
    await this.getExtent();
    let printUrl: string = '';
    if (this.mapView.zoom > 19.0) { this.mapView.zoom = 19.0 }
    if (this.mode == 'validate') {
      await this.gisService.printMap(this.mapView, this.customTextElements, "etrack_jurisdictional_layout").then((printResult: any) => {
        this.utils.emitLoadingEmitter(false);
        printUrl = printResult.url
      }, (printError: any) => {
        this.utils.emitLoadingEmitter(false);
        console.log(printError);
      });
      return printUrl;
    } else {
      await this.gisService.printMap(this.mapView, this.customTextElements).then((printResult: any) => {
        this.utils.emitLoadingEmitter(false);
        printUrl = printResult.url
      }, (printError: any) => {
        this.utils.emitLoadingEmitter(false);
        console.log(printError);
      });
      return printUrl;
    }
  }

  public async printMap(): Promise<void> {
    this.utils.emitLoadingEmitter(true);
    //await this.getExtent();
    if (this.mode == 'validate') {
      this.gisService.printMap(this.mapView, this.customTextElements, "etrack_jurisdictional_layout").then((printResult: any) => {
        this.utils.emitLoadingEmitter(false);
        window.open(printResult.url, '_blank');
      }, (printError: any) => {
        this.utils.emitLoadingEmitter(false);
        console.log(printError);
      });
    } else {
      this.gisService.printMap(this.mapView, this.customTextElements).then((printResult: any) => {
        this.utils.emitLoadingEmitter(false);
        window.open(printResult.url, '_blank');
      }, (printError: any) => {
        this.utils.emitLoadingEmitter(false);
        console.log(printError);
      });
    }
  }

  private async addLayers(): Promise<void> {
    this.mapView.map.layers.add(this.taxParcelLayer);
    this.mapView.map.layers.add(this.decRegionsLayer);
    this.mapView.map.layers.add(this.projectGraphicsLayer);
    this.mapView.map.layers.add(this.workAreaGraphicsLayer);
  }


  private setUpSketchViewModel(layer: GraphicsLayer) {
    this.sketchViewModel = new SketchViewModel({
      view: this.mapView,
      layer: layer,
      updateOnGraphicClick: false,
    });
  }

  public async setUpGraphicClickHandler() {
    this.setUpSketchViewModel(this.projectGraphicsLayer);
    this.mapView.on('click', (event: any) => {
      if (!this.isValidated) {
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
            this.inquiryLayerQuery.queryFeatures(inquiryQuery).then((results: any) => {
              if(results?.features?.length >1){
                sometext="<table style='border: 1px solid #CCC;border-collapse: collapse;'><tr><th>Inquiry Id</th><th>Inquiry Type</th><th>Request Identifier</th><th>Complete Date</th></tr>"
              results?.features?.forEach((feature:any)=>{
                if(feature.attributes?.RESPONSE_DATE !==null){
                  sometext+="<tr style='border: 1px solid #CCC;'><td style='border: 1px solid #CCC;'>"+ this.inquiryService.formatInquiryId(feature.attributes.SI_ID) + "</td><td style='border: 1px solid #CCC;'>"+ feature.attributes.SI_TYPE + "</td><td style='border: 1px solid #CCC;'>"+ feature.attributes?.REQUEST_IDENTIFIER+ "</td><td style='border: 1px solid #CCC;'>"+ (new Date(feature.attributes?.RESPONSE_DATE)).toLocaleString()+"</td></tr>";
                }else{
                  sometext+="<tr style='border: 1px solid #CCC;'><td style='border: 1px solid #CCC;'>"+ this.inquiryService.formatInquiryId(feature.attributes.SI_ID) + "</td><td style='border: 1px solid #CCC;'>"+ feature.attributes.SI_TYPE + "</td><td style='border: 1px solid #CCC;'>"+ feature.attributes?.REQUEST_IDENTIFIER+ "</td><td style='border: 1px solid #CCC;'> </td></tr>";
                }
              });
              sometext+="</table>"
            }
            if(results?.features?.length ===1){
              if(results.features[0].attributes?.RESPONSE_DATE !==null){
              sometext= "<strong>Inquiry Id: </strong>"+this.inquiryService.formatInquiryId(results?.features[0].attributes.SI_ID)+"<br/>" + "<strong>Inquiry Type:</strong> "+ results?.features[0].attributes.SI_TYPE+"<br/>" + "<strong>Request Identifier:</strong> "+ results?.features[0].attributes.REQUEST_IDENTIFIER +"<br/>" + "<strong>Complete Date:</strong> "+ (new Date(results?.features[0].attributes.RESPONSE_DATE)).toLocaleString();
              }else{
                sometext= "<strong>Inquiry Id: </strong>"+this.inquiryService.formatInquiryId(results?.features[0].attributes.SI_ID)+"<br/>" + "<strong>Inquiry Type:</strong> "+ results?.features[0].attributes.SI_TYPE+"<br/>" + "<strong>Request Identifier:</strong> "+ results?.features[0].attributes.REQUEST_IDENTIFIER +"<br/>" + "<strong>Complete Date:</strong> ";
              }
            }
              let popupData={
                location: event.mapPoint,
                content: sometext,
                actions: [],
                overwriteActions: true,
               };
               if(sometext!==""){
                this.mapView.popup.open(popupData);
               }
          });
          } catch (error) {
            console.error("query failed: ", error);
          }
        }

        this.mapView.hitTest(event).then((response: any) => {
          let results = response.results;
          if (this.sketchViewModel?.state === 'active' || this.textSketchViewModel.state === 'active' || this.workAreaEnabled) {
            return;
          } else {
            let projectPolygon = results.filter(function (result: any) {
              return result.graphic.layer?.id === 'project_sketch';
            })[0]?.graphic;
            this.handleProjectPolygon(projectPolygon, results);
          }
        });
      }
    });
  }

  private handleProjectPolygon(projectPolygon: Graphic, hitResults: any): void {
    if (projectPolygon !== undefined) {
      this.polygonStatus.isProjectLayerSelect = true;
      this.getNewExtent();
      this.sketchViewModel?.update([projectPolygon], { tool: 'reshape' });
      this.processAllAttributes();
    } else {
      if (!this.polygonStatus.isProjectLayerSelect) {
        let submittedGraphic = hitResults.filter(function (result: any) {
          return result.graphic.layer?.id === 'submitted_validate_polygon';
        })[0]?.graphic;
        this.handleSubmittedPolygon(submittedGraphic);
        let facilityGraphic = hitResults.filter(function (result: any) {
          return result.graphic.layer?.id === 'facility_polygon';
        })[0]?.graphic;
        this.handleFacilityPolygon(facilityGraphic);
        if (submittedGraphic === undefined && facilityGraphic === undefined) {
          let taxGraphic = hitResults.filter(function (result: any) {
            return result.graphic.layer?.id === 'tax_parcel_layer';
          })[0]?.graphic;
          this.handleTaxPolygon(taxGraphic);
        }
      } else {
        this.polygonStatus.isProjectLayerSelect = false;
      }
    }
  }

  private handleSubmittedPolygon(submittedGraphic: any): void {
    if (submittedGraphic !== undefined) {
      let s_polygon = new Polygon({
        rings: (submittedGraphic.geometry as Polygon).rings,
        spatialReference: new SpatialReference({
          wkid: submittedGraphic.geometry.spatialReference.wkid,
        }),
      });
      let s_graphic = new Graphic({
        geometry: s_polygon,
        attributes: submittedGraphic.attributes,
        symbol: this.outLineSymbol,
      });
      this.projectGraphicsLayer.add(s_graphic);
      this.getNewExtent();
      this.processAllAttributes();
    }
  }


  private handleFacilityPolygon(facilityGraphic: any): void {
    if (facilityGraphic !== undefined) {
      let f_polygon = new Polygon({
        rings: (facilityGraphic.geometry as Polygon).rings,
        spatialReference: new SpatialReference({
          wkid: facilityGraphic.geometry.spatialReference.wkid,
        }),
      });
      let f_graphic = new Graphic({
        geometry: f_polygon,
        attributes: facilityGraphic.attributes,
        symbol: this.outLineSymbol,
      });
      this.projectGraphicsLayer.add(f_graphic);
      this.getNewExtent();
      this.processAllAttributes();
    }
  }

  private handleTaxPolygon(taxGraphic: any): void {
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
        symbol: this.outLineSymbol,
      });
      this.projectGraphicsLayer.add(t_graphic);
      this.getNewExtent();
      this.processAllAttributes();
    }
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

  public isWorkAreaPolygonExist(): boolean {
    return (this.workAreaGraphicsLayer.graphics?.length > 0);
  }

  public combineWorkAreaGeometry(): Graphic {
    let rings: number[][][] = [];
    let spatialReference = { wkid: 102100 };
    this.workAreaGraphicsLayer.graphics.forEach((graphic: any) => {
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
    this.utils.emitLoadingEmitter(true);
    if (this.projectGraphicsLayer?.graphics?.length > 0) {
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
        if(graphic.attributes?.SBL !== undefined && (graphic.attributes?.PRINT_KEY === undefined || graphic.attributes?.PRINT_KEY === '' || graphic.attributes?.PRINT_KEY === null) ){
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
      let municipalities = await this.gisService.queryMunicipalitiesAt(fullGraphic);
      let counties = await this.gisService.queryCountiesAt(fullGraphic);
      let polygon_partial = new Polygon({
        rings: rings_partial,
        spatialReference: spatialReference,
      });
      let partialGraphic = new Graphic({
        geometry: polygon_partial,
      });

      projection.load().then(() => {
        if (polygon?.centroid !== undefined && polygon?.centroid !== null) {
          let point = (projection.project(polygon.centroid, this.NAD83) as Point);
          let gisLocationData = new GISLocation();
          gisLocationData.latitude = polygon.centroid?.latitude;
          gisLocationData.longitude = polygon.centroid?.longitude;
          gisLocationData.nytmx = point?.x;
          gisLocationData.nytmy = point?.y;
          this.gisLocation.emit(gisLocationData);
        } else {
          console.log("project polygon does not exist");
        }
      }).catch((error) => {
        console.log(error);
      });

      let partial_parcels = await this.gisService.queryTaxParcelsAt(partialGraphic);
      if (partial_parcels !== undefined && partial_parcels.trim() !== '') {
        let full_Parcels = partial_parcels.split(':::')[0]?.trim() + ',' + [...parcels].join(',');
        this.taxParcelNumbers.emit(full_Parcels);
      } else {
        this.taxParcelNumbers.emit([...parcels].join(','));
      }
      if (regions !== undefined) {
        this.regions.emit(regions);
        if (regions.indexOf(',') < 0) {
          this.primaryRegion.emit(regions);
        } else {
          this.gisService.getRegionAt(polygon.centroid?.longitude, polygon.centroid?.latitude).then((region) => {
            this.primaryRegion.emit(region);
          }).catch((error) => {
            console.log(error);
          });
        }
      }
      if (municipalities !== undefined) {
        this.municipalities.emit(municipalities);
      }
      this.gisService.getMunicipalityAt(polygon.centroid?.longitude, polygon.centroid?.latitude).then((municipalities) => {
        municipalities.forEach((civilDivision: CivilDivision) => {
          this.primaryMunicipality.emit(civilDivision);
        })
      }).catch((error) => {
        console.log(error);
      });
      if (counties !== undefined) {
        this.counties.emit(counties);
      }
      this.utils.emitLoadingEmitter(false);
    } else {
      this.workAreaGraphicsLayer.removeAll();
      this.utils.emitLoadingEmitter(false);
    }
  }

  private buildPolygons(requestGraphic: Graphic, symbol: SimpleFillSymbol = this.outLineSymbol): Graphic {
    return new Graphic({
      geometry: requestGraphic.geometry as Polygon,
      attributes: requestGraphic.attributes,
      symbol: symbol,
    });
  }

  public async refreshProjectGraphicsLayer(
    projectGraphic: Graphic
  ): Promise<void> {
    this.projectGraphicsLayer.graphics?.removeAll();
    if (
      projectGraphic.geometry !== undefined &&
      projectGraphic.geometry !== null
    ) {
      this.projectGraphicsLayer.graphics.add(this.buildPolygons(projectGraphic));
      this.getExtent();
    }
  }

  public async refreshWorkAreaGraphicsLayer(
    workAreaGraphic: Graphic
  ): Promise<void> {
    this.workAreaGraphicsLayer.graphics?.removeAll();
    if (
      workAreaGraphic.geometry !== undefined &&
      workAreaGraphic.geometry !== null
    ) {
      this.workAreaGraphicsLayer.graphics.add(this.buildPolygons(workAreaGraphic, this.outLineWorkAreaSymbol));
    }
  }

  public async getExtent(): Promise<void> {
    let graphics = new Array<Graphic>();
    if (this.projectGraphicsLayer.graphics?.length > 0) {
      this.projectGraphicsLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
    }
    await this.mapView.goTo(graphics);
  }

  public async addToggleButton(): Promise<void> {
    this.hideToggleLayer = false;
    this.loadSubmittedLayerValidate();
    this.submittedPolygonsLayerValidate.visible = false;
    this.mapView.ui.add(this.submitLayerDiv.nativeElement, 'top-left');
    this.applicantInput.nativeElement.onchange = async (event: any) => {
      if (event.target.checked) {
        this.submittalGraphicsLayer.graphics?.removeAll();
        await this.submittedPolygonsLayerValidate.queryFeatures().then((response) => {
          if (response.features[0] !== undefined) {
            let polygon = new Polygon({
              rings: (response.features[0].geometry as Polygon).rings,
              spatialReference: response.spatialReference,
            });
            let submittedGraphic = new Graphic({
              geometry: polygon,
              attributes: response.features[0].attributes,
            });
            this.submittalGraphicsLayer.graphics.add(submittedGraphic);
          } else {
            let error = new Error("could not able to find Applicant Submitted")
            throw error;
          }
        });
        console.log("All Submitted polygons are loaded ")
        this.submittedPolygonsLayerValidate.visible = true;
        this.getNewExtent();
      } else {
        this.submittedPolygonsLayerValidate.visible = false;
        this.submittalGraphicsLayer.graphics?.removeAll();
        this.getNewExtent();
      }
    };
  }
  //this code is to show facility layer
  public async addToggleFacilityButton(id: string = this.decId): Promise<void> {
    this.loadFacilityLayerValidate(id);
    this.facilityPolygonsLayerValidate.visible = false;
    this.facilityInput.nativeElement.onchange = async (event: any) => {
      if (event.target.checked) {
        this.facilityGraphicsLayer.graphics.removeAll();
        await this.facilityPolygonsLayerValidate.queryFeatures().then((response) => {
          if (response.features[0] !== undefined) {
            let polygon = new Polygon({
              rings: (response.features[0].geometry as Polygon).rings,
              spatialReference: response.spatialReference,
            });
            let facilityGraphic = new Graphic({
              geometry: polygon,
              attributes: response.features[0].attributes,
            });
            this.facilityGraphicsLayer.graphics.add(facilityGraphic);
          } else {
            let error = new Error("could not able to find eFind Polygon")
            throw error;
          }
        });
        console.log("All Facility polygons are loaded ")
        this.facilityPolygonsLayerValidate.visible = true;
        this.getNewExtent();
      } else {
        this.facilityPolygonsLayerValidate.visible = false;
        this.facilityGraphicsLayer.graphics.removeAll();
        this.getNewExtent();
      }
    };
  }

  public loadSubmittedLayerValidate() {
    this.submittedPolygonsLayerValidate.definitionExpression =
      "APPL_SUB_ID='" + localStorage.getItem('projectId') + "'";
    this.mapView.map.layers?.add(this.submittedPolygonsLayerValidate);
  }

  public loadFacilityLayerValidate(id: string = this.decId) {
    this.decId = id;
    if (this.isDecIdExist) {
      this.facilityPolygonsLayerValidate.definitionExpression =
        "PRIMARY_ID='" + this.decId + "'";
      this.mapView.map.layers?.add(this.facilityPolygonsLayerValidate);
      this.facilityPolygonsLayerValidate.queryFeatures({ where: "PRIMARY_ID='" + this.decId + "'", outSpatialReference: { wkid: 102100 }, returnGeometry: true }).then((response) => {
        if (response.features[0] !== undefined) {
          let polygon = new Polygon({
            rings: (response.features[0].geometry as Polygon).rings,
            spatialReference: response.spatialReference,
          });
          let facilityGraphic = new Graphic({
            geometry: polygon,
            attributes: response.features[0].attributes,
          });
          this.validPolygon = facilityGraphic;
        } else {
          let error = new Error("could not able to find eFind Polygon")
          throw error;
        }
      });
    }
  }

  public getNewExtent() {
    let graphics = new Array<Graphic>();
    if (this.projectGraphicsLayer.graphics?.length > 0) {
      this.projectGraphicsLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
    }
    if (this.submittalGraphicsLayer.graphics?.length > 0) {
      this.submittalGraphicsLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
    }
    if (this.facilityGraphicsLayer.graphics?.length > 0) {
      this.facilityGraphicsLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
    }
    this.mapView.goTo(graphics);
  }
  public isProjectAndFacilityHasSamePolygon(): number {
    if (this.validPolygon !== undefined && this.validPolygon.geometry !== undefined) {
      if (this.projectGraphicsLayer.graphics?.length === 1) {
        let projectGraphic = this.projectGraphicsLayer.graphics?.getItemAt(0);
        if (geometryEngine.equals(this.validPolygon.geometry, projectGraphic?.geometry)) {
          return 1;
        } else {
          return 0;
        }
      }
      return 0;
    } else {
      return 0
    }
  }
}

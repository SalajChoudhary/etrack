import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  SimpleChange,
  ViewChild
} from '@angular/core';
import { Utils } from 'src/app/@shared/services/utils';
import config from '@arcgis/core/config';
import Polygon from '@arcgis/core/geometry/Polygon';
import Graphic from '@arcgis/core/Graphic';
import FeatureLayer from '@arcgis/core/layers/FeatureLayer';
import GraphicsLayer from '@arcgis/core/layers/GraphicsLayer';
import MapView from '@arcgis/core/views/MapView';
import WebMap from '@arcgis/core/WebMap';
import Fullscreen from '@arcgis/core/widgets/Fullscreen';
import { environment } from 'src/environments/environment';
import LabelClass from '@arcgis/core/layers/support/LabelClass';
import { GisService } from 'src/app/@shared/services/gisService';
import Measurement from '@arcgis/core/widgets/Measurement';
import SimpleFillSymbol from '@arcgis/core/symbols/SimpleFillSymbol';
import { PolygonStatus } from 'src/app/@store/models/PolygonStatus';
import SimpleRenderer from '@arcgis/core/renderers/SimpleRenderer';

@Component({
  selector: 'app-gis-map-view',
  templateUrl: './gis-map-view.component.html',
  styleUrls: ['./gis-map-view.component.scss'],
})
export class GisMapViewComponent implements OnInit, OnDestroy {
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
    minScale: 10000,
  });

  outLineSymbol = new SimpleFillSymbol({
    color: [32, 144, 235, 0.2],
    style: 'solid',
    outline: {
      color: '#2090EB',
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

  outLineFacilitySymbol = new SimpleFillSymbol({
    color: '#56F7F9',
    style: "backward-diagonal",
    outline: {
      color: '#56F7F9',
      width: 3,
    },
  });

  facilityPolygonRenderer = new SimpleRenderer({
    symbol: this.outLineFacilitySymbol,
  });

  facilityPolygonsLayer = new FeatureLayer({
    url: `${environment.eFindFacilityLayer}`,
    outFields: ['PRIMARY_ID'],
    id: 'facility_polygon',
    editingEnabled: false,
    renderer: this.facilityPolygonRenderer,
    spatialReference: { wkid: 102100 },
    listMode: 'hide',
    visible:false
  });


  taxParcelLayer = new FeatureLayer({
    id: 'tax_parcel_layer',
    url: `${environment.taxParcelUrl}`,
    outFields: ['*'],
    labelingInfo: [this.taxParcelLabel],
    maxScale: 0,
    minScale: 40000,
    listMode: 'hide',
    visible: true,
  });

  decRegionsLayer = new FeatureLayer({
    url: `${environment.decRegionsUrl}`,
    outFields: ['*'],
    visible: false,
    listMode: 'hide',
  });

  taxGraphicLayer = new GraphicsLayer({
    id: 'tax_sketch',
    title: 'TaxParcel Sketch Layer',
    listMode: 'hide',
  });

  taxMapGraphicLayer = new GraphicsLayer({
    id: 'taxMap_sketch',
    title: 'TaxMap Sketch Layer',
    listMode: 'hide',
  });

  sketchLayer = new GraphicsLayer({
    id: 'gl_sketch',
    title: 'Sketch Layer',
    listMode: 'hide',
  });

  facilityGraphicsLayer = new GraphicsLayer({
    id: 'facility_sketch',
    title: 'Facility Sketch Layer',
    listMode: 'hide',
  });

  shapeFileGraphicsLayer = new GraphicsLayer({
    id: 'shapeFile_sketch',
    title: 'Shape File Layer',
    listMode: 'hide',
  });

  workAreaGraphicsLayer = new GraphicsLayer({
    id: 'shapeFile_sketch',
    title: 'Shape File Layer',
    listMode: 'hide',
  });

  projectGraphicsLayer = new GraphicsLayer({
    id: 'project_layer',
    title: 'project Layer',
    listMode: 'hide',
  });

  // Create new instance of the Measurement widget
  measurement = new Measurement();

  private _taxGraphics!: Graphic;
  private _facilityGraphic!: Graphic;
  private _sketchGraphics!: Graphic;
  private _taxMapGraphic!: Graphic;
  private _shapeFileGraphic!: Graphic;
  private _workAreaGraphic!: Graphic;
  private _projectId!: string;
  private _decid!: string;

  private _customTextElements!: string;
  polygonStatus = new PolygonStatus();
  //isProjectLayerSelect: boolean = false;
  printHide: boolean = false;

  @Input()
  set customTextElements(customTextElements: any) {
    this._customTextElements = customTextElements;
  }

  get customTextElements(): any {
    return this._customTextElements;
  }

  @Input()
  set taxGraphics(taxGraphic: Graphic) {
    this._taxGraphics = taxGraphic;
  }

  get taxGraphics(): Graphic {
    return this._taxGraphics;
  }

  @Input()
  set facilityGraphic(facilityGraphic: Graphic) {
    this._facilityGraphic = facilityGraphic;
  }

  get facilityGraphic(): Graphic {
    return this._facilityGraphic;
  }

  @Input()
  set sketchGraphics(sketchGraphics: Graphic) {
    this._sketchGraphics = sketchGraphics;
  }

  get sketchGraphics(): Graphic {
    return this._sketchGraphics;
  }

  @Input()
  set taxMapGraphic(taxMapGraphic: Graphic) {
    this._taxMapGraphic = taxMapGraphic;
  }

  get taxMapGraphic(): Graphic {
    return this._taxMapGraphic;
  }

  @Input()
  set shapeFileGraphic(shapeFileGraphic: Graphic) {
    this._shapeFileGraphic = shapeFileGraphic;
  }

  get shapeFileGraphic(): Graphic {
    return this._shapeFileGraphic;
  }

  @Input()
  set workAreaGraphic(workAreaGraphic: Graphic) {
    this._workAreaGraphic = workAreaGraphic;
  }

  get workAreaGraphic(): Graphic {
    return this._workAreaGraphic;
  }

  @Input()
  set projectId(projectId: string) {
    this._projectId = projectId;
  }

  get projectId(): string {
    return this._projectId;
  }

  @Input()
  set decId(decid: string) {
    this._decid = decid;
  }

  get decId(): string {
    return this._decid;
  }

  get isDecIdExist(): boolean {
    if (this.decId === undefined || this.decId === '') {
      return false;
    }
    return true;
  }

  @ViewChild('mapViewDisplay', { static: true }) private elementRef!: ElementRef;
  @ViewChild('textInputDiv', { static: true }) private textInputDiv!: ElementRef;
  @ViewChild('textButton', { static: true }) private textButton!: ElementRef;
  @ViewChild('resetButton', { static: true }) private resetButton!: ElementRef;
  @ViewChild('textInput', { static: true }) private textInput!: ElementRef;
  @ViewChild('textTool', { static: true }) private textTool!: ElementRef;
  @ViewChild('printButton', { static: true }) private printButton!: ElementRef;
  @ViewChild('toolbarDiv', { static: true }) private toolbarDiv!: ElementRef;
  @ViewChild('distance', { static: true }) private distanceButton!: ElementRef;

  @Input() mapProperties!: any;
  @Input() mapStyle: string = 'parent';
  @Input() polygonType!: string;
  @Output() mapInit: EventEmitter<boolean> = new EventEmitter();

  private mapView: any;
  private map: any;

  constructor(private gisService: GisService, public utils: Utils) { }

  ngOnInit(): void {
    config.assetsPath = `${environment.logoutRedirectUrl}/assets/`;
    this.loadMap();
    this.addLayers();
    this.gisService.addFullScreen(this.mapView);
    this.addPrintOptions();
    //No need for view
    //this.gisService.buildJurisdictionalLayers(this.mapView);
    this.addMeasurement();
    this.gisService.addNotesInteractions(this.mapView, this.textInputDiv, this.textTool, this.textButton, this.resetButton, this.textInput, this.polygonStatus);
  }
  ngOnDestroy(): void {
    this.mapView.destroy();
  }

  ngOnChanges(changes: { [property: string]: SimpleChange }): void {
    // Extract changes to the input property by its name
    if (changes['taxGraphics'] !== undefined) {
      const change: SimpleChange = changes['taxGraphics'];
      if (change.currentValue) {
        this.refreshTaxLayer(change.currentValue);
      }
    }
    if (changes['facilityGraphic'] !== undefined) {
      const change: SimpleChange = changes['facilityGraphic'];
      if (change.currentValue) {
        this.refreshFacilityLayer(change.currentValue);
      }
    }
    if (changes['sketchGraphics'] !== undefined) {
      const change: SimpleChange = changes['sketchGraphics'];
      if (change.currentValue) {
        this.refreshSketchLayer(change.currentValue);
      }
    }
    if (changes['taxMapGraphic'] !== undefined) {
      const change: SimpleChange = changes['taxMapGraphic'];
      if (change.currentValue) {
        this.refreshTaxMapLayer(change.currentValue);
      }
    }
    if (changes['shapeFileGraphic'] !== undefined) {
      const change: SimpleChange = changes['shapeFileGraphic'];
      if (change.currentValue) {
        this.refreshShapeFileGraphicsLayer(change.currentValue);
      }
    }
    if (changes['workAreaGraphic'] !== undefined) {
      const change: SimpleChange = changes['workAreaGraphic'];
      if (change.currentValue) {
        this.refreshWorkAreaGraphicsLayer(change.currentValue);
      }
    }
    if (changes['projectId'] !== undefined) {
      const change: SimpleChange = changes['projectId'];
      if (change.currentValue) {
        this.loadProjectPolygon(change.currentValue);
      }
    }
    if (changes['decId'] !== undefined) {
      const change: SimpleChange = changes['decId'];
      if (change.currentValue) {
        this.loadDecPolygon(change.currentValue);
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
    });
  }

  private loadMap(): void {
    this.loadWebMap({
      ...this.mapProperties,
      container: this.elementRef.nativeElement,
    });
    this.mapInit.emit(true);
  }

  get mapStyleClass() {
    return this.mapStyle;
  }

  private async addFullScreen(): Promise<void> {
    let fullscreen = new Fullscreen({
      view: this.mapView,
    });
    this.mapView.ui.add(fullscreen, 'top-right');
  }

  private async addPrintOptions(): Promise<void> {
    this.mapView.ui.add(this.printButton.nativeElement, 'top-left');
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
    await this.gisService.printMap(this.mapView, this.customTextElements).then((printResult: any) => {
      this.utils.emitLoadingEmitter(false);
      printUrl = printResult.url
    }, (printError: any) => {
      this.utils.emitLoadingEmitter(false);
      console.log(printError);
    });
    return printUrl;
  }

  public async printMap(): Promise<void> {
    this.utils.emitLoadingEmitter(true);
    //await this.getExtent();
    console.log(this.mapView);
    if (this.mapView.zoom > 19.0) { this.mapView.zoom = 19.0 }
    this.gisService.printMap(this.mapView, this.customTextElements).then((printResult: any) => {
      this.utils.emitLoadingEmitter(false);
      window.open(printResult.url, '_blank');
    }, (printError: any) => {
      this.utils.emitLoadingEmitter(false);
      console.log(printError);
    });
  }

  private async addLayers(): Promise<void> {
    this.mapView.map.layers.add(this.taxParcelLayer);
    this.mapView.map.layers.add(this.decRegionsLayer);
    this.mapView.map.layers.add(this.taxMapGraphicLayer);
    this.mapView.map.layers.add(this.taxGraphicLayer);
    this.mapView.map.layers.add(this.facilityGraphicsLayer);
    this.mapView.map.layers.add(this.sketchLayer);
    this.mapView.map.layers.add(this.shapeFileGraphicsLayer);
    this.mapView.map.layers.add(this.projectGraphicsLayer);
    this.mapView.map.layers.add(this.workAreaGraphicsLayer);
    this.mapView.map.layers.add(this.facilityPolygonsLayer);
  }

  private async refreshTaxLayer(taxParcelGraphic: Graphic): Promise<void> {
    this.taxGraphicLayer.graphics?.removeAll();
    if (
      taxParcelGraphic.geometry !== undefined &&
      taxParcelGraphic.geometry !== null
    ) {
      this.taxGraphicLayer.graphics.add(this.buildPolygons(taxParcelGraphic));
      this.getExtent();
    }
  }

  private async refreshFacilityLayer(facility: Graphic): Promise<void> {
    this.facilityGraphicsLayer.graphics?.removeAll();
    if (facility.geometry !== undefined && facility.geometry !== null) {
      this.facilityGraphicsLayer.graphics.add(this.buildPolygons(facility));
      this.getExtent();
    }
  }
  private async refreshSketchLayer(sketch: Graphic): Promise<void> {
    this.sketchLayer.graphics?.removeAll();
    if (sketch.geometry !== undefined && sketch.geometry !== null) {
      this.sketchLayer.graphics.add(this.buildPolygons(sketch));
      this.getExtent();
    }
  }

  private async refreshTaxMapLayer(taxMapGraphic: Graphic): Promise<void> {
    this.taxMapGraphicLayer.graphics?.removeAll();
    if (
      taxMapGraphic.geometry !== undefined &&
      taxMapGraphic.geometry !== null
    ) {
      this.taxMapGraphicLayer.graphics.add(this.buildPolygons(taxMapGraphic));
      this.getExtent();
    }
  }

  private buildPolygons(requestGraphic: Graphic, symbol: SimpleFillSymbol = this.outLineSymbol): Graphic {
    return new Graphic({
      geometry: new Polygon({
        rings: (<Polygon>requestGraphic.geometry).rings,
        spatialReference: requestGraphic.geometry.spatialReference,
      }),
      symbol: symbol,
    });
  }

  private async refreshShapeFileGraphicsLayer(
    shapeFileGraphic: Graphic
  ): Promise<void> {
    this.shapeFileGraphicsLayer.graphics?.removeAll();
    if (
      shapeFileGraphic.geometry !== undefined &&
      shapeFileGraphic.geometry !== null
    ) {
      this.shapeFileGraphicsLayer.graphics.add(
        this.buildPolygons(shapeFileGraphic)
      );
      this.getExtent();
    }
  }

  private async refreshWorkAreaGraphicsLayer(
    workAreaGraphic: Graphic
  ): Promise<void> {
    this.workAreaGraphicsLayer.graphics?.removeAll();
    if (
      workAreaGraphic.geometry !== undefined &&
      workAreaGraphic.geometry !== null
    ) {
      this.workAreaGraphicsLayer.graphics.add(
        this.buildPolygons(workAreaGraphic, this.outLineWorkAreaSymbol)
      );
      this.getExtent();
    }
  }

  private async loadWorkAreaPolygon(workareaId: string): Promise<void> {
    this.workAreaGraphicsLayer.graphics?.removeAll();
    this.utils.emitLoadingEmitter(true);
    await this.gisService
      .searchWorkAreaPolygon(workareaId)
      .toPromise().then((features: any) => {
        if (features.features[0] !== undefined) {
          let polygon = new Polygon({
            rings: (features.features[0].geometry as Polygon).rings,
            spatialReference: features.spatialReference,
          });
          let workAreaGraphic = new Graphic({
            geometry: polygon,
            attributes: features.features[0].attributes,
          });
          this.workAreaGraphicsLayer.graphics.add(
            this.buildPolygons(workAreaGraphic, this.outLineWorkAreaSymbol)
          );
          this.utils.emitLoadingEmitter(false);
        } else {
          this.utils.emitLoadingEmitter(false);
        }
      })
      .catch((error: any) => {
        this.utils.emitLoadingEmitter(false);
      });
  }
  private async loadProjectPolygon(projectId: string): Promise<void> {
    this.projectGraphicsLayer.graphics?.removeAll();
    if (projectId !== undefined && projectId !== null && projectId !== '' && !this.isDecIdExist) {
      this.utils.emitLoadingEmitter(true);
      this.gisService.getProjectDetails(projectId).subscribe(
        async (data: any) => {
          let polygonId = data.polygonId;
          let workareaId=data.polygonId.workAreaId
          await this.gisService
          .searchAnalystPolygon(polygonId)
          .toPromise().then((features: any) => {
            if (features.features[0] !== undefined) {
              let polygon = new Polygon({
                rings: (features.features[0].geometry as Polygon).rings,
                spatialReference: features.spatialReference,
              });
              let graphic = new Graphic({
                geometry: polygon,
                attributes: features.features[0].attributes,
              });
              this.projectGraphicsLayer.graphics?.add(
                this.buildPolygons(graphic)
              );
              this.mapView.goTo(graphic);
              this.utils.emitLoadingEmitter(false);
            } else {
              this.utils.emitLoadingEmitter(false);
            }
          })
          .catch((error: any) => {
            this.utils.emitLoadingEmitter(false);
          });

          if(workareaId !==undefined && workareaId !== null && workareaId !==''){
            this.utils.emitLoadingEmitter(false);
              this.loadWorkAreaPolygon(workareaId);
          }
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          console.log(error);
        });
    }
  }

  private async loadDecPolygon(decId: string): Promise<void> {
    this.facilityPolygonsLayer.visible = false;
    if (decId !== undefined && decId !== null && decId !== '') {
      this.facilityPolygonsLayer.definitionExpression = "SITE_ID='" + decId + "'";
      this.utils.emitLoadingEmitter(true);
      await this.facilityPolygonsLayer.queryFeatures({ where: "SITE_ID='" + decId + "'", outSpatialReference: { wkid: 102100 }, returnGeometry: true }).then((response) => {
        if (response.features[0] !== undefined) {
          let polygon = new Polygon({
            rings: (response.features[0].geometry as Polygon).rings,
            spatialReference: response.spatialReference,
          });
          let facilityGraphic = new Graphic({
            geometry: polygon,
            attributes: response.features[0].attributes,
          });
          this.facilityPolygonsLayer.visible = true;
          this.mapView.goTo(facilityGraphic);
          this.utils.emitLoadingEmitter(false);
        } else {
          this.utils.emitLoadingEmitter(false);
          let error = new Error("could not able to find eFind Polygon")
          throw error;
        }
      });
    }
    this.utils.emitLoadingEmitter(false);
  }

  public async getExtent(): Promise<void> {
    let graphics = new Array<Graphic>();
    if (this.taxGraphicLayer.graphics?.length > 0) {
      this.taxGraphicLayer.graphics.forEach((graphic) => {
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
    if (this.taxMapGraphicLayer.graphics?.length > 0) {
      this.taxMapGraphicLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
    }
    if (this.shapeFileGraphicsLayer.graphics?.length > 0) {
      this.shapeFileGraphicsLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
    }
    if (this.workAreaGraphicsLayer.graphics?.length > 0) {
      this.workAreaGraphicsLayer.graphics.forEach((graphic) => {
        graphics.push(graphic);
      });
    }
    await this.mapView.goTo(graphics);
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
}

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { GisMapComponent } from './gis-map.component';

describe('EsriMapComponent', () => {
  let component: GisMapComponent;
  let fixture: ComponentFixture<GisMapComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ GisMapComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GisMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GisMapViewComponent } from './gis-map-view.component';

describe('GisMapViewComponent', () => {
  let component: GisMapViewComponent;
  let fixture: ComponentFixture<GisMapViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GisMapViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GisMapViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

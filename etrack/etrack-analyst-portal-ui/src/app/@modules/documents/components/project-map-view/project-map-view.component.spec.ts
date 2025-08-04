import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectMapViewComponent } from './project-map-view.component';

describe('ProjectMapViewComponent', () => {
  let component: ProjectMapViewComponent;
  let fixture: ComponentFixture<ProjectMapViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProjectMapViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectMapViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

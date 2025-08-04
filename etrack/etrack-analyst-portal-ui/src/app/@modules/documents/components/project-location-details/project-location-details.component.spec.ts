import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectLocationDetailsComponent } from './project-location-details.component';

describe('ProjectLocationDetailsComponent', () => {
  let component: ProjectLocationDetailsComponent;
  let fixture: ComponentFixture<ProjectLocationDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProjectLocationDetailsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectLocationDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectLocationComponent } from './project-location.component';

describe('ProjectLocationComponent', () => {
  let component: ProjectLocationComponent;
  let fixture: ComponentFixture<ProjectLocationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProjectLocationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectLocationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

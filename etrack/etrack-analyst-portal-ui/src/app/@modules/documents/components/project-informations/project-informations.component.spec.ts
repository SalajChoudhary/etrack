import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectInformationsComponent } from './project-informations.component';

describe('ProjectInformationsComponent', () => {
  let component: ProjectInformationsComponent;
  let fixture: ComponentFixture<ProjectInformationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProjectInformationsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectInformationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

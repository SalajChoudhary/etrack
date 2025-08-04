import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegionalProjectComponent } from './regional-project.component';

describe('RegionalProjectComponent', () => {
  let component: RegionalProjectComponent;
  let fixture: ComponentFixture<RegionalProjectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegionalProjectComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegionalProjectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

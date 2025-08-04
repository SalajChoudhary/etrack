import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MaintenancePermitTypeComponent } from './maintenance-permit-type.component';

describe('MaintenancePermitTypeComponent', () => {
  let component: MaintenancePermitTypeComponent;
  let fixture: ComponentFixture<MaintenancePermitTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MaintenancePermitTypeComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MaintenancePermitTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentMaintenanceComponent } from './document-maintenance.component';

describe('DocumentMaintenanceComponent', () => {
  let component: DocumentMaintenanceComponent;
  let fixture: ComponentFixture<DocumentMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocumentMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DocumentMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

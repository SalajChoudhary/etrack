import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardValidateDataComponent } from './dashboard-validate-data.component';

describe('DashboardValidateDataComponent', () => {
  let component: DashboardValidateDataComponent;
  let fixture: ComponentFixture<DashboardValidateDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DashboardValidateDataComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardValidateDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

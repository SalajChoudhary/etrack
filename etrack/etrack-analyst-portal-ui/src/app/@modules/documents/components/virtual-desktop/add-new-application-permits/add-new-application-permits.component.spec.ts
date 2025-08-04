import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddNewApplicationPermitsComponent } from './add-new-application-permits.component';

describe('AddNewApplicationPermitsComponent', () => {
  let component: AddNewApplicationPermitsComponent;
  let fixture: ComponentFixture<AddNewApplicationPermitsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddNewApplicationPermitsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddNewApplicationPermitsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

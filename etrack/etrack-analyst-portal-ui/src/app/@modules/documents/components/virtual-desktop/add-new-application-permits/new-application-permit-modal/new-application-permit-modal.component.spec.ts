import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewApplicationPermitModalComponent } from './new-application-permit-modal.component';

describe('NewApplicationPermitModalComponent', () => {
  let component: NewApplicationPermitModalComponent;
  let fixture: ComponentFixture<NewApplicationPermitModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NewApplicationPermitModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewApplicationPermitModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

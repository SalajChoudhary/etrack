import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DimsrConfirmationPopupComponent } from './dimsr-confirmation-popup.component';

describe('DimsrConfirmationPopupComponent', () => {
  let component: DimsrConfirmationPopupComponent;
  let fixture: ComponentFixture<DimsrConfirmationPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DimsrConfirmationPopupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DimsrConfirmationPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

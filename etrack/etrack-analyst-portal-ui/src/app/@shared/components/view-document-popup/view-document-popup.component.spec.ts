import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewDocumentPopupComponent } from './view-document-popup.component';

describe('ViewDocumentPopupComponent', () => {
  let component: ViewDocumentPopupComponent;
  let fixture: ComponentFixture<ViewDocumentPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewDocumentPopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewDocumentPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentsLitigationBannerComponent } from './documents-litigation-banner.component';

describe('DocumentsLitigationBannerComponent', () => {
  let component: DocumentsLitigationBannerComponent;
  let fixture: ComponentFixture<DocumentsLitigationBannerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocumentsLitigationBannerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentsLitigationBannerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

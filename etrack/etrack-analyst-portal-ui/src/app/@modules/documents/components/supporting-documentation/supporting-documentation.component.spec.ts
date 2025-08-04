import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupportingDocumentationComponent } from './supporting-documentation.component';

describe('SupportingDocumentationComponent', () => {
  let component: SupportingDocumentationComponent;
  let fixture: ComponentFixture<SupportingDocumentationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SupportingDocumentationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SupportingDocumentationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

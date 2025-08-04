import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VirtualDesktopTableDocumentsComponent } from './virtual-desktop-table-documents.component';

describe('VirtualDesktopTableDocumentsComponent', () => {
  let component: VirtualDesktopTableDocumentsComponent;
  let fixture: ComponentFixture<VirtualDesktopTableDocumentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VirtualDesktopTableDocumentsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VirtualDesktopTableDocumentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

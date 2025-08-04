import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VirtualDesktopTableProgramReviewComponent } from './virtual-desktop-table-program-review.component';

describe('VirtualDesktopTableProgramReviewComponent', () => {
  let component: VirtualDesktopTableProgramReviewComponent;
  let fixture: ComponentFixture<VirtualDesktopTableProgramReviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VirtualDesktopTableProgramReviewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VirtualDesktopTableProgramReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

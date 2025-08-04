import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PendingChangesPopupComponent } from './pending-changes-popup.component';

describe('PendingChangesPopupComponent', () => {
  let component: PendingChangesPopupComponent;
  let fixture: ComponentFixture<PendingChangesPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PendingChangesPopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PendingChangesPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InactiveSessionPopupComponent } from './inactive-session-popup.component';

describe('InactiveSessionPopupComponent', () => {
  let component: InactiveSessionPopupComponent;
  let fixture: ComponentFixture<InactiveSessionPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InactiveSessionPopupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InactiveSessionPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

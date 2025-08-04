import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LitigationHoldComponent } from './litigation-hold.component';

describe('VirtualDesktopLitigationHoldComponent', () => {
  let component: LitigationHoldComponent;
  let fixture: ComponentFixture<LitigationHoldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LitigationHoldComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LitigationHoldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

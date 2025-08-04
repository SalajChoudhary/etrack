import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BackwardForwardActionsComponent } from './backward-forward-actions.component';

describe('BackwardForwardActionsComponent', () => {
  let component: BackwardForwardActionsComponent;
  let fixture: ComponentFixture<BackwardForwardActionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BackwardForwardActionsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BackwardForwardActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

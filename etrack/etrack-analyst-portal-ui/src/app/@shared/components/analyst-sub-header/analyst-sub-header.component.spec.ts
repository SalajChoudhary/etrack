import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalystSubHeaderComponent } from './analyst-sub-header.component';

describe('AnalystSubHeaderComponent', () => {
  let component: AnalystSubHeaderComponent;
  let fixture: ComponentFixture<AnalystSubHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AnalystSubHeaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AnalystSubHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DimsrComponent } from './dimsr.component';

describe('DimsrComponent', () => {
  let component: DimsrComponent;
  let fixture: ComponentFixture<DimsrComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DimsrComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DimsrComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccordionChildComponent } from './accordion-child.component';

describe('AccordionChildComponent', () => {
  let component: AccordionChildComponent;
  let fixture: ComponentFixture<AccordionChildComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccordionChildComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccordionChildComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

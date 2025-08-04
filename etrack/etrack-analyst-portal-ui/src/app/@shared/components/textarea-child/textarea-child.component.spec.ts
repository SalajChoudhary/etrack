import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextareaChildComponent } from './textarea-child.component';

describe('TextareaChildComponent', () => {
  let component: TextareaChildComponent;
  let fixture: ComponentFixture<TextareaChildComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TextareaChildComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TextareaChildComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditSystemGenNotesComponent } from './edit-system-gen-notes.component';

describe('EditSystemGenNotesComponent', () => {
  let component: EditSystemGenNotesComponent;
  let fixture: ComponentFixture<EditSystemGenNotesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditSystemGenNotesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditSystemGenNotesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

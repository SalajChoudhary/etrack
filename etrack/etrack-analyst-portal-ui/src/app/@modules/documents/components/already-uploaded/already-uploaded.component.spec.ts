import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlreadyUploadedComponent } from './already-uploaded.component';

describe('AlreadyUploadedComponent', () => {
  let component: AlreadyUploadedComponent;
  let fixture: ComponentFixture<AlreadyUploadedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AlreadyUploadedComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AlreadyUploadedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

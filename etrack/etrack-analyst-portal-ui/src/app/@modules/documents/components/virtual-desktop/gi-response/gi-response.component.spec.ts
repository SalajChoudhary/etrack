import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GiResponseComponent } from './gi-response.component';

describe('GiResponseComponent', () => {
  let component: GiResponseComponent;
  let fixture: ComponentFixture<GiResponseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GiResponseComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GiResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

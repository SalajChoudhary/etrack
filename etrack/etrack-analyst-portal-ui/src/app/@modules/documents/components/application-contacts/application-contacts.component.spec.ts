import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplicationContactsComponent } from './application-contacts.component';

describe('ApplicationContactsComponent', () => {
  let component: ApplicationContactsComponent;
  let fixture: ComponentFixture<ApplicationContactsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ApplicationContactsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicationContactsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

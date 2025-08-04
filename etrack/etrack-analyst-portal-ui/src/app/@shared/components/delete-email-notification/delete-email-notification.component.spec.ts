import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteEmailNotificationComponent } from './delete-email-notification.component';

describe('DeleteEmailNotificationComponent', () => {
  let component: DeleteEmailNotificationComponent;
  let fixture: ComponentFixture<DeleteEmailNotificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeleteEmailNotificationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteEmailNotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

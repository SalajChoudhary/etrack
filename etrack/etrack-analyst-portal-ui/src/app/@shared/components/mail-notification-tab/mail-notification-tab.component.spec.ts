import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MailNotificationTabComponent } from './mail-notification-tab.component';

describe('MailNotificationTabComponent', () => {
  let component: MailNotificationTabComponent;
  let fixture: ComponentFixture<MailNotificationTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MailNotificationTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MailNotificationTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

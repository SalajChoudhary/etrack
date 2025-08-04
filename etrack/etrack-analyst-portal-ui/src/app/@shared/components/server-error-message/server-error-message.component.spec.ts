import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServerErrorMessageComponent } from './server-error-message.component';

describe('ServerErrorMessageComponent', () => {
  let component: ServerErrorMessageComponent;
  let fixture: ComponentFixture<ServerErrorMessageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ServerErrorMessageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServerErrorMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

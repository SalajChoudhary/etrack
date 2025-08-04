import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VirtualDesktopTableApplicantCommunicationsComponent } from './virtual-desktop-table-applicant-communications.component';

describe('VirtualDesktopTableApplicantCommunicationsComponent', () => {
  let component: VirtualDesktopTableApplicantCommunicationsComponent;
  let fixture: ComponentFixture<VirtualDesktopTableApplicantCommunicationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VirtualDesktopTableApplicantCommunicationsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VirtualDesktopTableApplicantCommunicationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

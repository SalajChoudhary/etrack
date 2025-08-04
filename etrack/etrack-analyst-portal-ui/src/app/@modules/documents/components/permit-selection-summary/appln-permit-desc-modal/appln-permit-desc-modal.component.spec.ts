import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplnPermitDescModalComponent } from './appln-permit-desc-modal.component';

describe('ApplnPermitDescModalComponent', () => {
  let component: ApplnPermitDescModalComponent;
  let fixture: ComponentFixture<ApplnPermitDescModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ApplnPermitDescModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ApplnPermitDescModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CandidateKeyReplacementComponent } from './candidate-key-replacement.component';

describe('CandidateKeyReplacementComponent', () => {
  let component: CandidateKeyReplacementComponent;
  let fixture: ComponentFixture<CandidateKeyReplacementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CandidateKeyReplacementComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CandidateKeyReplacementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

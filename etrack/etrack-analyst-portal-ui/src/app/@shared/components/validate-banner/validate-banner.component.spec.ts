import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValidateBannerComponent } from './validate-banner.component';

describe('ValidateBannerComponent', () => {
  let component: ValidateBannerComponent;
  let fixture: ComponentFixture<ValidateBannerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ValidateBannerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ValidateBannerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

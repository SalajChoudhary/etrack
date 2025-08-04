import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KeywordOtherGridComponent } from './keyword-other-grid.component';

describe('KeywordOtherGridComponent', () => {
  let component: KeywordOtherGridComponent;
  let fixture: ComponentFixture<KeywordOtherGridComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ KeywordOtherGridComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(KeywordOtherGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

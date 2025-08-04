import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KeywordsTextGridComponent } from './keywords-text-grid.component';

describe('KeywordsTextGridComponent', () => {
  let component: KeywordsTextGridComponent;
  let fixture: ComponentFixture<KeywordsTextGridComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ KeywordsTextGridComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(KeywordsTextGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

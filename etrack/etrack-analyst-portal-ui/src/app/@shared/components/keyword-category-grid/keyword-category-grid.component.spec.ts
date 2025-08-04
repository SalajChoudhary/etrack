import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KeywordCategoryGridComponent } from './keyword-category-grid.component';

describe('KeywordCategoryGridComponent', () => {
  let component: KeywordCategoryGridComponent;
  let fixture: ComponentFixture<KeywordCategoryGridComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ KeywordCategoryGridComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(KeywordCategoryGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

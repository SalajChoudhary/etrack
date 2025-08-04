import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchRowQueryComponent } from './search-row-query.component';

describe('SearchRowQueryComponent', () => {
  let component: SearchRowQueryComponent;
  let fixture: ComponentFixture<SearchRowQueryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SearchRowQueryComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchRowQueryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

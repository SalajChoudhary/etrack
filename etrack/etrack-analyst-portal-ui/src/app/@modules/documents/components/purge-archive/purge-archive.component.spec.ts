import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PurgeArchiveComponent } from './purge-archive.component';

describe('PurgeArchiveComponent', () => {
  let component: PurgeArchiveComponent;
  let fixture: ComponentFixture<PurgeArchiveComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PurgeArchiveComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PurgeArchiveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

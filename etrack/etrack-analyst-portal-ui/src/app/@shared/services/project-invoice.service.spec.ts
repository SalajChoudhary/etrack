import { TestBed } from '@angular/core/testing';

import { ProjectInvoiceService } from './project-invoice.service';

describe('ProjectInvoiceService', () => {
  let service: ProjectInvoiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProjectInvoiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

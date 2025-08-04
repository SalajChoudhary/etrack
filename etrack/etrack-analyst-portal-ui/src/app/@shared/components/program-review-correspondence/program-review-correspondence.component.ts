import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-program-review-correspondence',
  templateUrl: './program-review-correspondence.component.html',
  styleUrls: ['./program-review-correspondence.component.scss']
})
export class ProgramReviewCorrespondenceComponent implements OnInit {

  @Input() data: any;

  constructor(public activeModel : NgbActiveModal) { }

  ngOnInit(): void {

  }

}

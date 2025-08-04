import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'backward-forward-actions',
  templateUrl: './backward-forward-actions.component.html',
  styleUrls: ['./backward-forward-actions.component.scss']
})
export class BackwardForwardActionsComponent implements OnInit {

  main: string = '/apply-for-permit-details';
  @Input() backward: string = '';
  @Input() forWard: string = '';
  @Input() lastIndex:number = 1;
  @Input() selectedIndex: number = 1;
  @Input() backwardName: string = '';
  @Input() forwardName: string = '';
  @Input() showBackArrow: boolean = true;
  @Input() showForwardArrow: boolean = true;
  constructor(public router: Router) { }

  ngOnInit(): void {
  }

  navigate(route: string) {
    this.router.navigate([route]);
  }

}

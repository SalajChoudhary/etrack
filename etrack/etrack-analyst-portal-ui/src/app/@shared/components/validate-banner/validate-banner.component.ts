import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-validate-banner',
  templateUrl: './validate-banner.component.html',
  styleUrls: ['./validate-banner.component.scss']
})
export class ValidateBannerComponent implements OnInit {

  @Input() validated:boolean = false;
  @Input() showBottomBorder : boolean = false;
  constructor() { }

  ngOnInit(): void {    
  }

}
